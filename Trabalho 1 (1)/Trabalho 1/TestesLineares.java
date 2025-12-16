import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class TestesLineares {

    // ----- GERADORES (int) -----
    static int[] vetorIntCrescente(int n){ int[] v=new int[n]; for(int i=0;i<n;i++) v[i]=i; return v; }
    static int[] vetorIntDecrescente(int n){ int[] v=new int[n]; for(int i=0;i<n;i++) v[i]=n-1-i; return v; }
    static int[] vetorIntAleatorio(int n, long semente, int max){
        Random rnd = new Random(semente); int[] v=new int[n];
        for(int i=0;i<n;i++) v[i]=rnd.nextInt(max+1);
        return v;
    }
    static int[] vetorIntComRepetidos(){ return new int[]{5,1,5,2,5,3,2,1,3,5}; }

    // ----- CHECAGENS/IO -----
    static boolean estaOrdenadoInt(int[] v){ for(int i=1;i<v.length;i++) if(v[i-1]>v[i]) return false; return true; }
    static void imprime(String r,int[] v){ System.out.println(r+Arrays.toString(v)); }

    // ----- RUNNERS -----
    static void rodarCasoInt(String nome, Consumer<int[]> ordenar, String caso, int[] base){
        int[] copia = base.clone();
        System.out.println("\n== "+nome+" | caso: "+caso+" ==");
        imprime("antes: ", copia);
        ordenar.accept(copia);
        imprime("depois:", copia);
        System.out.println( estaOrdenadoInt(copia) ? "OK ✅" : "FALHOU ❌" );
    }
    static void rodarTodosInt(String nome, Consumer<int[]> ordenar, int[] a, int[] b, int[] c, int[] d){
        rodarCasoInt(nome, ordenar, "crescente", a);
        rodarCasoInt(nome, ordenar, "decrescente", b);
        rodarCasoInt(nome, ordenar, "aleatorio", c);
        rodarCasoInt(nome, ordenar, "com repetidos", d);
    }

    public static void main(String[] args){
        final int n    = 10;
        final int max  = 20;
        final int base = 10;
        final int baldes = 5;

        // Casos
        int[] iAsc  = vetorIntCrescente(n);
        int[] iDesc = vetorIntDecrescente(n);
        int[] iRand = vetorIntAleatorio(n, 42L, max);
        int[] iRep  = vetorIntComRepetidos();

        // Wrappers
        Consumer<int[]> csort   = v -> Lineares.countingSort(v, max);
        Consumer<int[]> radix   = v -> Lineares.radixSortLSD(v, base);
        Consumer<int[]> bktIns  = v -> Lineares.bucketSortComInsertion(v, baldes, max);
        Consumer<int[]> bktMrg  = v -> Lineares.bucketSortComMerge(v, baldes, max);

        // Executa mini-testes
        rodarTodosInt("countingSort",        csort,  iAsc, iDesc, iRand, iRep);
        rodarTodosInt("radixSortLSD",        radix,  iAsc, iDesc, iRand, iRep);
        rodarTodosInt("bucketSort+Insertion",bktIns, iAsc, iDesc, iRand, iRep);
        rodarTodosInt("bucketSort+Merge",    bktMrg, iAsc, iDesc, iRand, iRep);

        System.out.println("\n[PASSO 4] Mini-testes (lineares) concluídos.");
    }
}
