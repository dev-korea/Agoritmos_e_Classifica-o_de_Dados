import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;

public class Benchmark1M {

    static final int N = 1_000_000;
    static final int REPETICOES = 10;
    static final String ORDEM = "aleatorio";

    static final class AlgComp { final String nome; final Consumer<Integer[]> f; AlgComp(String n, Consumer<Integer[]> f){ nome=n; this.f=f; } }
    static final class AlgLin  { final String nome; final Consumer<int[]> f;     AlgLin (String n, Consumer<int[]> f){ nome=n; this.f=f; } }

    static int[] carregarCSVInt(Path arq) throws IOException {
        String s = Files.readString(arq, StandardCharsets.UTF_8).trim();
        if (s.isEmpty()) return new int[0];
        String[] t = s.split("[,\\s]+");
        int[] v = new int[t.length];
        for (int i=0;i<t.length;i++) v[i] = Integer.parseInt(t[i]);
        return v;
    }
    static Integer[] toInteger(int[] v){ Integer[] o=new Integer[v.length]; for(int i=0;i<v.length;i++) o[i]=v[i]; return o; }
    static Path ds(){ return Paths.get("datasets","n"+N+"_"+ORDEM+".csv"); }
    static int max(int[] a){ int m=0; for(int x:a) if(x>m) m=x; return m; }

    static long medirComp(Consumer<Integer[]> f, Integer[] base){
        Integer[] a = base.clone();
        long t0=System.nanoTime(); f.accept(a); long t1=System.nanoTime();
        return t1-t0;
    }
    static long medirLin(Consumer<int[]> f, int[] base){
        int[] a = base.clone();
        long t0=System.nanoTime(); f.accept(a); long t1=System.nanoTime();
        return t1-t0;
    }
    static void appendLinha(Path arq, String linha) throws IOException {
        Files.createDirectories(arq.getParent());
        boolean novo = !Files.exists(arq);
        try (BufferedWriter w = Files.newBufferedWriter(arq, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (novo) w.write("algo,ordem,n,run,time_ns\n");
            w.write(linha); w.write('\n');
        }
    }

    public static void main(String[] args) throws Exception {
        Path dataset = ds();
        if (!Files.exists(dataset)) {
            System.err.println("Dataset ausente: " + dataset.toAbsolutePath());
            return;
        }
        int[] baseInt = carregarCSVInt(dataset);
        Integer[] baseInteger = toInteger(baseInt);
        int valorMaximo = max(baseInt);
        int baseRadix = 10, baldes = 32;

        // Recomendo focar nos rápidos para 1M
        AlgComp[] comp = new AlgComp[]{
                new AlgComp("heap",  ordenadores::heapSort),
                new AlgComp("merge", ordenadores::mergeSort),
                new AlgComp("quick", ordenadores::quickSort)
        };

        AlgLin[] lin = new AlgLin[]{
                new AlgLin("counting", v -> Lineares.countingSort(v, valorMaximo)),
                new AlgLin("radixLSD", v -> Lineares.radixSortLSD(v, baseRadix)),
                new AlgLin("bucket+insertion", v -> Lineares.bucketSortComInsertion(v, baldes, valorMaximo)),
                new AlgLin("bucket+merge",     v -> Lineares.bucketSortComMerge(v, baldes, valorMaximo))
        };

        Path dirRaw = Paths.get("results","raw");
        Path dirSummary = Paths.get("results","summary");
        Files.createDirectories(dirRaw);
        Files.createDirectories(dirSummary);

        Map<String, long[]> medias = new HashMap<>();

        for (int run = 1; run <= REPETICOES; run++) {
            for (AlgComp a : comp) {
                System.out.printf("Executando algoritmo %s (ordem=%s, n=%d, run=%d/%d)...%n",
                        a.nome, ORDEM, N, run, REPETICOES);
                long ns = medirComp(a.f, baseInteger);
                System.out.printf("→ concluído: %d ns%n", ns);
                Path arqAlgo = dirRaw.resolve(a.nome + ".csv");
                appendLinha(arqAlgo, a.nome + "," + ORDEM + "," + N + "," + run + "," + ns);
                String k = a.nome + "," + ORDEM + "," + N;
                medias.computeIfAbsent(k, kk -> new long[2]);
                medias.get(k)[0] += ns; medias.get(k)[1] += 1;
            }
            for (AlgLin a : lin) {
                System.out.printf("Executando algoritmo %s (ordem=%s, n=%d, run=%d/%d)...%n",
                        a.nome, ORDEM, N, run, REPETICOES);
                long ns = medirLin(a.f, baseInt);
                System.out.printf("→ concluído: %d ns%n", ns);
                Path arqAlgo = dirRaw.resolve(a.nome + ".csv");
                appendLinha(arqAlgo, a.nome + "," + ORDEM + "," + N + "," + run + "," + ns);
                String k = a.nome + "," + ORDEM + "," + N;
                medias.computeIfAbsent(k, kk -> new long[2]);
                medias.get(k)[0] += ns; medias.get(k)[1] += 1;
            }
        }

        Path mediasCsv = dirSummary.resolve("medias_por_algo_1M.csv");
        try (BufferedWriter w = Files.newBufferedWriter(mediasCsv, StandardCharsets.UTF_8)) {
            w.write("algo,ordem,n,media_ns\n");
            List<String> chaves = new ArrayList<>(medias.keySet());
            Collections.sort(chaves);
            for (String k : chaves) {
                long[] sc = medias.get(k);
                long media = (sc[1]==0) ? 0 : sc[0] / sc[1];
                w.write(k + "," + media + "\n");
            }
        }
        System.out.println("OK! RAW em results/raw/*.csv  |  Médias (1M): " + mediasCsv.toAbsolutePath());
    }
}
