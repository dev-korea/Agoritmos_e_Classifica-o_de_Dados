import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class teste_correcao {

    // ====== Par nome+função para organizar os testes ======
    private static final class ParAlgoritmo {
        final String nome;
        final Consumer<Integer[]> ordenar;
        ParAlgoritmo(String nome, Consumer<Integer[]> ordenar){
            this.nome = nome;
            this.ordenar = ordenar;
        }
    }

    // ========= GERADORES (≤10 elementos) =========
    static Integer[] vetorCrescente(int tamanho){
        Integer[] vetor = new Integer[tamanho];
        for(int i=0;i<tamanho;i++) vetor[i] = i;
        return vetor;
    }

    static Integer[] vetorDecrescente(int tamanho){
        Integer[] vetor = new Integer[tamanho];
        for(int i=0;i<tamanho;i++) vetor[i] = tamanho-1-i;
        return vetor;
    }

    static Integer[] vetorAleatorio(int tamanho, long semente, int maximo){
        Random aleatorio = new Random(semente);
        Integer[] vetor = new Integer[tamanho];
        for(int i=0;i<tamanho;i++) vetor[i] = aleatorio.nextInt(maximo+1); // 0..maximo
        return vetor;
    }

    static Integer[] vetorComRepetidos(){
        // Pequeno e didático: múltiplas repetições
        return new Integer[]{5, 1, 5, 2, 5, 3, 2, 1, 3, 5};
    }

    // ========= UTILITÁRIOS =========
    static <T extends Comparable<? super T>> boolean estaOrdenado(T[] vetor){
        for(int i=1;i<vetor.length;i++){
            if(vetor[i-1].compareTo(vetor[i]) > 0) return false;
        }
        return true;
    }

    static <T> void imprime(String rotulo, T[] vetor){
        System.out.println(rotulo + Arrays.toString(vetor));
    }

    // Roda um caso: clona, imprime antes/depois e checa se ordenou
    static <T extends Comparable<? super T>>
    void rodarCaso(String nomeDoAlgoritmo, Consumer<T[]> ordenar, String nomeDoCaso, T[] base){
        T[] copia = base.clone();
        System.out.println("\n== " + nomeDoAlgoritmo + " | caso: " + nomeDoCaso + " ==");
        imprime("antes: ", copia);
        ordenar.accept(copia);
        imprime("depois:", copia);
        System.out.println( estaOrdenado(copia) ? "OK " : "FALHOU " );
    }

    static void rodarTodosOsCasos(String nomeAlg, Consumer<Integer[]> ordenar,
                                  Integer[] casoCrescente, Integer[] casoDecrescente,
                                  Integer[] casoAleatorio, Integer[] casoRepetidos){
        rodarCaso(nomeAlg, ordenar, "crescente",     casoCrescente);
        rodarCaso(nomeAlg, ordenar, "decrescente",   casoDecrescente);
        rodarCaso(nomeAlg, ordenar, "aleatorio",     casoAleatorio);
        rodarCaso(nomeAlg, ordenar, "com repetidos", casoRepetidos);
    }

    public static void main(String[] args){
        // ---------- Conjunto de 4 casos (≤10) ----------
        final int tamanho = 10;
        Integer[] casoCrescente   = vetorCrescente(tamanho);
        Integer[] casoDecrescente = vetorDecrescente(tamanho);
        Integer[] casoAleatorio   = vetorAleatorio(tamanho, 42L, 20); // semente fixa => reprodutível
        Integer[] casoRepetidos   = vetorComRepetidos();

        // ---------- Lista de algoritmos a testar ----------
        ParAlgoritmo[] algoritmos = new ParAlgoritmo[] {
                new ParAlgoritmo("bubbleSort",    ordenadores::bubbleSort),
                new ParAlgoritmo("insertionSort", ordenadores::insertionSort),
                new ParAlgoritmo("selectionSort", ordenadores::selectionSort),
                new ParAlgoritmo("shellSort",     ordenadores::shellSort),
                new ParAlgoritmo("heapSort",      ordenadores::heapSort),
                new ParAlgoritmo("mergeSort",     ordenadores::mergeSort),
                new ParAlgoritmo("quickSort",     ordenadores::quickSort)
        };

        // ---------- Executa os 4 mini-testes para cada algoritmo ----------
        for(ParAlgoritmo alg : algoritmos){
            rodarTodosOsCasos(alg.nome, alg.ordenar, casoCrescente, casoDecrescente, casoAleatorio, casoRepetidos);
        }

        System.out.println("\n[PASSO 2] Mini-testes executados para TODOS os algoritmos.");
    }
}
