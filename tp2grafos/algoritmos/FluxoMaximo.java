package tp2grafos.algoritmos;

import tp2grafos.representacao.Grafo;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class FluxoMaximo {

    public static double fordFulkerson(Grafo grafo, int fonte, int sorvedouro) {
        double[][] capacidade = grafo.getMatrizPesos();
        int n = grafo.getNumeroVertices();

        double[][] residual = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(capacidade[i], 0, residual[i], 0, n);
        }

        int[] pai = new int[n];
        double fluxoMaximo = 0.0;

        while (bfsResidual(residual, fonte, sorvedouro, pai)) {
            double fluxoCaminho = Double.POSITIVE_INFINITY;

            int v = sorvedouro;
            while (v != fonte) {
                int u = pai[v];
                fluxoCaminho = Math.min(fluxoCaminho, residual[u][v]);
                v = u;
            }

            v = sorvedouro;
            while (v != fonte) {
                int u = pai[v];
                residual[u][v] -= fluxoCaminho;
                residual[v][u] += fluxoCaminho;
                v = u;
            }

            fluxoMaximo += fluxoCaminho;
        }

        return fluxoMaximo;
    }

    private static boolean bfsResidual(double[][] residual, int fonte, int sorvedouro, int[] pai) {
        int n = residual.length;
        boolean[] visitado = new boolean[n];
        Arrays.fill(visitado, false);
        Arrays.fill(pai, -1);

        Queue<Integer> fila = new ArrayDeque<>();
        fila.add(fonte);
        visitado[fonte] = true;

        while (!fila.isEmpty()) {
            int u = fila.poll();
            for (int v = 0; v < n; v++) {
                if (!visitado[v] && residual[u][v] > 0 && residual[u][v] != Double.POSITIVE_INFINITY) {
                    fila.add(v);
                    pai[v] = u;
                    visitado[v] = true;
                    if (v == sorvedouro) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
