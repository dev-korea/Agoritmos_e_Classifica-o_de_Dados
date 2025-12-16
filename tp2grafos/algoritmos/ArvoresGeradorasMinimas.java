package tp2grafos.algoritmos;

import tp2grafos.modelo.Aresta;
import tp2grafos.representacao.Grafo;

import java.util.*;

public class ArvoresGeradorasMinimas {

    public static List<Aresta> prim(Grafo grafo, int inicio) {
        int n = grafo.getNumeroVertices();
        boolean[] incluido = new boolean[n];
        double[] chave = new double[n];
        int[] pai = new int[n];
        Arrays.fill(chave, Double.POSITIVE_INFINITY);
        Arrays.fill(pai, -1);
        chave[inicio] = 0.0;

        PriorityQueue<double[]> fila = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        fila.add(new double[]{inicio, 0.0});

        while (!fila.isEmpty()) {
            int v = (int) fila.poll()[0];
            if (incluido[v]) {
                continue;
            }
            incluido[v] = true;

            for (Aresta a : grafo.getAdjacentes(v)) {
                int u = a.getDestino();
                double peso = a.getPeso();
                if (!incluido[u] && peso < chave[u]) {
                    chave[u] = peso;
                    pai[u] = v;
                    fila.add(new double[]{u, peso});
                }
            }
        }

        List<Aresta> resultado = new ArrayList<>();
        for (int v = 0; v < n; v++) {
            if (pai[v] != -1) {
                resultado.add(new Aresta(pai[v], v, chave[v]));
            }
        }
        return resultado;
    }

    public static List<Aresta> kruskal(Grafo grafo) {
        int n = grafo.getNumeroVertices();
        List<Aresta> arestas = new ArrayList<>(grafo.getTodasArestas());
        arestas.sort(Comparator.comparingDouble(Aresta::getPeso));

        UnionFind uf = new UnionFind(n);
        List<Aresta> resultado = new ArrayList<>();

        for (Aresta a : arestas) {
            int raizOrigem = uf.encontrar(a.getOrigem());
            int raizDestino = uf.encontrar(a.getDestino());
            if (raizOrigem != raizDestino) {
                resultado.add(a);
                uf.unir(raizOrigem, raizDestino);
            }
        }
        return resultado;
    }

    private static class UnionFind {
        private final int[] pai;
        private final int[] rank;

        UnionFind(int n) {
            pai = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                pai[i] = i;
                rank[i] = 0;
            }
        }

        int encontrar(int x) {
            if (pai[x] != x) {
                pai[x] = encontrar(pai[x]);
            }
            return pai[x];
        }

        void unir(int x, int y) {
            int rx = encontrar(x);
            int ry = encontrar(y);
            if (rx == ry) {
                return;
            }
            if (rank[rx] < rank[ry]) {
                pai[rx] = ry;
            } else if (rank[rx] > rank[ry]) {
                pai[ry] = rx;
            } else {
                pai[ry] = rx;
                rank[rx]++;
            }
        }
    }
}
