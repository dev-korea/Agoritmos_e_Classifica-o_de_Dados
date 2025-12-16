package tp2grafos.algoritmos;

import tp2grafos.modelo.Aresta;
import tp2grafos.representacao.Grafo;

import java.util.*;

public class Buscas {

    public static int[] bfs(Grafo grafo, int origem) {
        int n = grafo.getNumeroVertices();
        int[] distancia = new int[n];
        Arrays.fill(distancia, -1);
        Queue<Integer> fila = new ArrayDeque<>();
        distancia[origem] = 0;
        fila.add(origem);

        while (!fila.isEmpty()) {
            int v = fila.poll();
            for (Aresta a : grafo.getAdjacentes(v)) {
                int u = a.getDestino();
                if (distancia[u] == -1) {
                    distancia[u] = distancia[v] + 1;
                    fila.add(u);
                }
            }
        }
        return distancia;
    }

    public static int[] dfs(Grafo grafo, int origem) {
        int n = grafo.getNumeroVertices();
        int[] tempoEntrada = new int[n];
        boolean[] visitado = new boolean[n];
        int[] tempo = {0};
        dfsVisitar(grafo, origem, visitado, tempoEntrada, tempo);
        return tempoEntrada;
    }

    private static void dfsVisitar(Grafo grafo, int v, boolean[] visitado,
                                   int[] tempoEntrada, int[] tempo) {
        visitado[v] = true;
        tempo[0]++;
        tempoEntrada[v] = tempo[0];
        for (Aresta a : grafo.getAdjacentes(v)) {
            int u = a.getDestino();
            if (!visitado[u]) {
                dfsVisitar(grafo, u, visitado, tempoEntrada, tempo);
            }
        }
    }

    public static List<Integer> ordenacaoTopologica(Grafo grafo) {
        int n = grafo.getNumeroVertices();
        boolean[] visitado = new boolean[n];
        Deque<Integer> pilha = new ArrayDeque<>();

        for (int v = 0; v < n; v++) {
            if (!visitado[v]) {
                dfsTopo(grafo, v, visitado, pilha);
            }
        }

        List<Integer> ordem = new ArrayList<>();
        while (!pilha.isEmpty()) {
            ordem.add(pilha.pop());
        }
        return ordem;
    }

    private static void dfsTopo(Grafo grafo, int v, boolean[] visitado, Deque<Integer> pilha) {
        visitado[v] = true;
        for (Aresta a : grafo.getAdjacentes(v)) {
            int u = a.getDestino();
            if (!visitado[u]) {
                dfsTopo(grafo, u, visitado, pilha);
            }
        }
        pilha.push(v);
    }
}
