import java.util.Random;

public final class ordenadores {
    private ordenadores() {} // utility: não instanciar

    // ===================== O(n^2) =====================

    public static <T extends Comparable<? super T>> void bubbleSort(T[] vetor){
        boolean trocou;
        int n = vetor.length;
        do{
            trocou = false;
            for(int i=1;i<n;i++){
                if(vetor[i-1].compareTo(vetor[i]) > 0){
                    troca(vetor, i-1, i);
                    trocou = true;
                }
            }
            n--;
        }while(trocou);
    }

    public static <T extends Comparable<? super T>> void insertionSort(T[] vetor){
        for(int i=1;i<vetor.length;i++){
            T chave = vetor[i];
            int j = i - 1;
            while(j >= 0 && vetor[j].compareTo(chave) > 0){
                vetor[j+1] = vetor[j];
                j--;
            }
            vetor[j+1] = chave;
        }
    }

    public static <T extends Comparable<? super T>> void selectionSort(T[] vetor){
        int n = vetor.length;
        for(int i=0;i<n-1;i++){
            int posMenor = i;
            for(int j=i+1;j<n;j++){
                if(vetor[j].compareTo(vetor[posMenor]) < 0){
                    posMenor = j;
                }
            }
            if(posMenor != i){
                troca(vetor, i, posMenor);
            }
        }
    }

    // ===================== O(n log n) =====================

    public static <T extends Comparable<? super T>> void shellSort(T[] vetor){
        int n = vetor.length;
        for(int gap = n/2; gap > 0; gap /= 2){
            for(int i = gap; i < n; i++){
                T atual = vetor[i];
                int j = i;
                while(j >= gap && vetor[j - gap].compareTo(atual) > 0){
                    vetor[j] = vetor[j - gap];
                    j -= gap;
                }
                vetor[j] = atual;
            }
        }
    }

    public static <T extends Comparable<? super T>> void heapSort(T[] vetor){
        int n = vetor.length;
        for(int i = n/2 - 1; i >= 0; i--){
            afunda(vetor, i, n);
        }
        for(int fim = n - 1; fim > 0; fim--){
            troca(vetor, 0, fim);
            afunda(vetor, 0, fim);
        }
    }

    public static <T extends Comparable<? super T>> void mergeSort(T[] vetor){
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Comparable[vetor.length];
        mergeSortRec(vetor, aux, 0, vetor.length - 1);
    }

    // ===================== QUICK SORT (modificado) =====================

    // RNG com seed fixa para reprodutibilidade nos benchmarks
    private static final Random RNG = new Random(123456);

    /** Quick Sort com pivô aleatório (Lomuto) e eliminação de cauda para evitar estouro de pilha. */
    public static <T extends Comparable<? super T>> void quickSort(T[] vetor){
        if (vetor == null || vetor.length < 2) return;
        quickLomutoPivotAleatorioTail(vetor, 0, vetor.length - 1);
    }

    private static <T extends Comparable<? super T>>
    void quickLomutoPivotAleatorioTail(T[] v, int lo, int hi){
        while (lo < hi){
            // escolhe pivô aleatório e coloca no fim
            int pIndex = lo + RNG.nextInt(hi - lo + 1);
            troca(v, pIndex, hi);
            int p = particionaLomuto(v, lo, hi);

            // recursa somente na metade MENOR; itera na maior (tail call elimination)
            if (p - 1 - lo < hi - (p + 1)){
                if (lo < p - 1) quickLomutoPivotAleatorioTail(v, lo, p - 1);
                lo = p + 1;
            } else {
                if (p + 1 < hi) quickLomutoPivotAleatorioTail(v, p + 1, hi);
                hi = p - 1;
            }
        }
    }

    private static <T extends Comparable<? super T>>
    int particionaLomuto(T[] v, int lo, int hi){
        T pivo = v[hi];
        int i = lo;
        for (int j = lo; j < hi; j++){
            if (v[j].compareTo(pivo) <= 0){
                troca(v, i, j);
                i++;
            }
        }
        troca(v, i, hi);
        return i;
    }

    // ===================== Helpers =====================

    private static <T> void troca(T[] v, int i, int j){
        T tmp = v[i]; v[i] = v[j]; v[j] = tmp;
    }

    private static <T extends Comparable<? super T>> void afunda(T[] v, int i, int tamanho){
        while(true){
            int esq = 2*i + 1;
            int dir = 2*i + 2;
            int maior = i;
            if(esq < tamanho && v[esq].compareTo(v[maior]) > 0) maior = esq;
            if(dir < tamanho && v[dir].compareTo(v[maior]) > 0) maior = dir;
            if(maior == i) break;
            troca(v, i, maior);
            i = maior;
        }
    }

    private static <T extends Comparable<? super T>> void mergeSortRec(T[] v, T[] aux, int ini, int fim){
        if(ini >= fim) return;
        int meio = (ini + fim) >>> 1;
        mergeSortRec(v, aux, ini, meio);
        mergeSortRec(v, aux, meio + 1, fim);
        intercala(v, aux, ini, meio, fim);
    }

    private static <T extends Comparable<? super T>> void intercala(T[] v, T[] aux, int ini, int meio, int fim){
        for(int k = ini; k <= fim; k++) aux[k] = v[k];
        int i = ini, j = meio + 1;
        for(int k = ini; k <= fim; k++){
            if(i > meio)                     v[k] = aux[j++];
            else if(j > fim)                 v[k] = aux[i++];
            else if(aux[j].compareTo(aux[i]) < 0) v[k] = aux[j++];
            else                              v[k] = aux[i++];
        }
    }
}
