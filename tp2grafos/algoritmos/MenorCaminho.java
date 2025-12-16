package tp2grafos.algoritmos;

import tp2grafos.modelo.Aresta;
import tp2grafos.representacao.Grafo;

import java.util.*;

public class MenorCaminho {

    public static double[] dijkstra(Grafo grafo, int origem) {
        int n = grafo.getNumeroVertices();
        double[] distancia = new double[n];
        Arrays.fill(distancia, Double.POSITIVE_INFINITY);
        distancia[origem] = 0.0;

        PriorityQueue<double[]> fila = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        fila.add(new double[]{origem, 0.0});

        boolean[] visitado = new boolean[n];

        while (!fila.isEmpty()) {
            int v = (int) fila.poll()[0];
            if (visitado[v]) {
                continue;
            }
            visitado[v] = true;

            for (Aresta a : grafo.getAdjacentes(v)) {
                int u = a.getDestino();
                double peso = a.getPeso();
                if (distancia[v] + peso < distancia[u]) {
                    distancia[u] = distancia[v] + peso;
                    fila.add(new double[]{u, distancia[u]});
                }
            }
        }
        return distancia;
    }

    public static double[] bellmanFord(Grafo grafo, int origem) {
        int n = grafo.getNumeroVertices();
        double[] distancia = new double[n];
        Arrays.fill(distancia, Double.POSITIVE_INFINITY);
        distancia[origem] = 0.0;

        List<Aresta> arestas = grafo.getTodasArestas();

        for (int i = 0; i < n - 1; i++) {
            for (Aresta a : arestas) {
                int u = a.getOrigem();
                int v = a.getDestino();
                double peso = a.getPeso();
                if (distancia[u] != Double.POSITIVE_INFINITY && distancia[u] + peso < distancia[v]) {
                    distancia[v] = distancia[u] + peso;
                }
            }
        }

        // Verificação simples de ciclo negativo (pode ser refinada se quiser)
        for (Aresta a : arestas) {
            int u = a.getOrigem();
            int v = a.getDestino();
            double peso = a.getPeso();
            if (distancia[u] != Double.POSITIVE_INFINITY && distancia[u] + peso < distancia[v]) {
                System.out.println("Aviso: Grafo contém ciclo negativo.");
                break;
            }
        }

        return distancia;
    }

    public static double[][] floydWarshall(Grafo grafo) {
        int n = grafo.getNumeroVertices();
        double[][] dist = grafo.getMatrizPesos();

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == Double.POSITIVE_INFINITY) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == Double.POSITIVE_INFINITY) {
                        continue;
                    }
                    double novaDistancia = dist[i][k] + dist[k][j];
                    if (novaDistancia < dist[i][j]) {
                        dist[i][j] = novaDistancia;
                    }
                }
            }
        }
        return dist;
    }
}
