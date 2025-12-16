import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class GeradorVetores {

    enum Ordem { CRESCENTE, DECRESCENTE, ALEATORIO }

    // Gera vetor de inteiros com a ordem desejada; para ALEATORIO usa [0..maximo].
    static int[] gerar(int tamanho, Ordem ordem, long semente, int maximo){
        int[] v = new int[tamanho];
        switch (ordem) {
            case CRESCENTE:
                for (int i = 0; i < tamanho; i++) v[i] = i;
                break;
            case DECRESCENTE:
                for (int i = 0; i < tamanho; i++) v[i] = tamanho - 1 - i;
                break;
            case ALEATORIO:
                Random rnd = new Random(semente);
                for (int i = 0; i < tamanho; i++) v[i] = rnd.nextInt(maximo + 1);
                break;
        }
        return v;
    }

    // Salva o vetor em CSV (uma linha com vírgulas) dentro de datasets/
    static void salvarCSV(Path arquivo, int[] v) throws Exception {
        Files.createDirectories(arquivo.getParent());
        StringBuilder sb = new StringBuilder(Math.max(16, v.length * 3));
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(v[i]);
        }
        Files.writeString(arquivo, sb.toString(), StandardCharsets.UTF_8);
    }

    // Nome do arquivo: datasets/n{tamanho}_{ordem}.csv
    //aqui que direcionamos os arquivos de saida para a pasta datasets
    static Path caminho(int tamanho, Ordem ordem){
        return Paths.get("datasets", "n" + tamanho + "_" + ordem.name().toLowerCase() + ".csv");
    }

    // Seed fixa por tamanho (garante MESMO vetor aleatório para todos os algoritmos daquele n)
    static long seedPorTamanho(int tamanho){
        return 123456789L ^ (31L * tamanho);
    }

    public static void main(String[] args) throws Exception {
        int[] tamanhos = { 100, 1_000, 10_000, 100_000, 1_000_000 };
        Ordem[] ordens  = { Ordem.CRESCENTE, Ordem.DECRESCENTE, Ordem.ALEATORIO };

        for (int n : tamanhos) {
            int maximo = Math.max(1, n - 1);         // mantém valores em [0, n-1] (útil p/ lineares)
            long seed  = seedPorTamanho(n);          // fairness: mesma semente para esse tamanho

            for (Ordem ordem : ordens) {
                int[] dados = gerar(n, ordem, seed, maximo);
                Path arquivo = caminho(n, ordem);
                salvarCSV(arquivo, dados);
                System.out.println("Gerado: " + arquivo.toAbsolutePath());
            }
        }
        System.out.println("Concluído. Arquivos em ./datasets/");
    }
}
