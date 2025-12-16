import java.util.ArrayList;
import java.util.List;
import java.util.function.IntUnaryOperator;

public final class Lineares {
    private Lineares() {}

    // =========================================================
    // COUNTING SORT (estável) em int[] com chaves esperadas >= 0
    // (Se você nunca terá negativos, estas checagens são "cinto de segurança";
    //  pode removê-las para não afetar medições.)
    // =========================================================
    public static void countingSort(int[] vetor, int valorMaximo){
        if (valorMaximo < 0) throw new IllegalArgumentException("valorMaximo < 0");
        final int n = vetor.length;
        int[] saida    = new int[n];
        int[] contagem = new int[valorMaximo + 1];

        // mesma rotina base, mas com chave = v -> v  e faixa = max+1
        countingEstavelPorChave(vetor, valorMaximo + 1, v -> v, saida, contagem);
        System.arraycopy(saida, 0, vetor, 0, n);
    }

    // =========================================================
    // RADIX SORT (LSD, estável) em int[] com chaves esperadas >= 0
    // Reaproveita o núcleo do COUNTING, mas por "dígito":
    // chave = v -> (v / exp) % base, faixa = base
    // =========================================================
    public static void radixSortLSD(int[] vetor, int base){
        if (base < 2) throw new IllegalArgumentException("base >= 2");

        int maximo = 0;
        for (int v : vetor) {
            if (v < 0) throw new IllegalArgumentException("Radix espera não-negativos: " + v);
            if (v > maximo) maximo = v;
        }
        final int n = vetor.length;
        int[] saida    = new int[n];
        int[] contagem = new int[base];

        for (int exp = 1; maximo / exp > 0; exp *= base) {
            final int expoente = exp; // efetivamente final p/ lambda
            countingEstavelPorChave(vetor, base, v -> (v / expoente) % base, saida, contagem);
            System.arraycopy(saida, 0, vetor, 0, n);
        }
    }

    // =========================================================
    // BUCKET SORT (int[]) — chaves em [0, valorMaximo]
    // Versão 1: cada balde ordenado com Insertion Sort (Ordenadores.insertionSort).
    // =========================================================
    public static void bucketSortComInsertion(int[] vetor, int quantidadeBaldes, int valorMaximo){
        if (quantidadeBaldes <= 0) throw new IllegalArgumentException("baldes > 0");
        if (valorMaximo < 0) throw new IllegalArgumentException("valorMaximo < 0");

        @SuppressWarnings("unchecked")
        List<Integer>[] baldes = new List[quantidadeBaldes];
        for (int i = 0; i < quantidadeBaldes; i++) baldes[i] = new ArrayList<>();

        for (int v : vetor) {
            if (v < 0 || v > valorMaximo)
                throw new IllegalArgumentException("Fora de [0," + valorMaximo + "]: " + v);
            int indice = (int) (((long) v * quantidadeBaldes) / ((long) valorMaximo + 1L));
            baldes[indice].add(v);
        }

        int k = 0;
        for (List<Integer> balde : baldes) {
            if (!balde.isEmpty()) {
                Integer[] arr = balde.toArray(new Integer[0]);
                ordenadores.insertionSort(arr); // auxiliar
                for (int x : arr) vetor[k++] = x;
            }
        }
    }

    // =========================================================
    // BUCKET SORT (int[]) — chaves em [0, valorMaximo]
    // Versão 2: cada balde ordenado com Merge Sort (Ordenadores.mergeSort).
    // =========================================================
    public static void bucketSortComMerge(int[] vetor, int quantidadeBaldes, int valorMaximo){
        if (quantidadeBaldes <= 0) throw new IllegalArgumentException("baldes > 0");
        if (valorMaximo < 0) throw new IllegalArgumentException("valorMaximo < 0");

        @SuppressWarnings("unchecked")
        List<Integer>[] baldes = new List[quantidadeBaldes];
        for (int i = 0; i < quantidadeBaldes; i++) baldes[i] = new ArrayList<>();

        for (int v : vetor) {
            if (v < 0 || v > valorMaximo)
                throw new IllegalArgumentException("Fora de [0," + valorMaximo + "]: " + v);
            int indice = (int) (((long) v * quantidadeBaldes) / ((long) valorMaximo + 1L));
            baldes[indice].add(v);
        }

        int k = 0;
        for (List<Integer> balde : baldes) {
            if (!balde.isEmpty()) {
                Integer[] arr = balde.toArray(new Integer[0]);
                ordenadores.mergeSort(arr); // auxiliar
                for (int x : arr) vetor[k++] = x;
            }
        }
    }

    // =========================================================
    // Núcleo do COUNTING estável, reaproveitado por:
    //  - countingSort (chave = valor, faixa = max+1)
    //  - radixSortLSD  (chave = dígito, faixa = base)
    // Contagem e escrita estáveis (varredura de trás pra frente).
    // =========================================================
    private static void countingEstavelPorChave(int[] vetor, int faixa,
                                                IntUnaryOperator chave,
                                                int[] saida, int[] contagem){
        // zera contagem
        for (int i = 0; i < faixa; i++) contagem[i] = 0;

        // frequências
        for (int v : vetor) {
            int k = chave.applyAsInt(v);
            if (k < 0 || k >= faixa)
                throw new IllegalArgumentException("Chave fora da faixa [0,"+(faixa-1)+"]: " + k);
            contagem[k]++;
        }
        // prefixos acumulados
        for (int i = 1; i < faixa; i++) contagem[i] += contagem[i - 1];

        // estável (de trás pra frente)
        for (int i = vetor.length - 1; i >= 0; i--) {
            int v = vetor[i];
            int k = chave.applyAsInt(v);
            saida[--contagem[k]] = v;
        }
    }
}
