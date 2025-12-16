package tp2grafos.representacao;

import tp2grafos.modelo.Aresta;

import java.util.ArrayList;
import java.util.List;

public class GrafoMatrizAdj implements Grafo {

    private final int numeroVertices;
    private final boolean direcionado;
    private final boolean ponderado;
    private final double[][] matrizPesos;

    public GrafoMatrizAdj(int numeroVertices, boolean direcionado, boolean ponderado) {
        this.numeroVertices = numeroVertices;
        this.direcionado = direcionado;
        this.ponderado = ponderado;
        this.matrizPesos = new double[numeroVertices][numeroVertices];
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (i == j) {
                    matrizPesos[i][j] = 0.0;
                } else {
                    matrizPesos[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }
    }

    @Override
    public int getNumeroVertices() {
        return numeroVertices;
    }

    @Override
    public boolean isDirecionado() {
        return direcionado;
    }

    @Override
    public boolean isPonderado() {
        return ponderado;
    }

    @Override
    public void adicionarAresta(int origem, int destino) {
        adicionarAresta(origem, destino, 1.0);
    }

    @Override
    public void adicionarAresta(int origem, int destino, double peso) {
        matrizPesos[origem][destino] = peso;
        if (!direcionado) {
            matrizPesos[destino][origem] = peso;
        }
    }

    @Override
    public Iterable<Aresta> getAdjacentes(int vertice) {
        List<Aresta> adjacentes = new ArrayList<>();
        for (int j = 0; j < numeroVertices; j++) {
            if (matrizPesos[vertice][j] != Double.POSITIVE_INFINITY && vertice != j) {
                adjacentes.add(new Aresta(vertice, j, matrizPesos[vertice][j]));
            }
        }
        return adjacentes;
    }

    @Override
    public List<Aresta> getTodasArestas() {
        List<Aresta> arestas = new ArrayList<>();
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (matrizPesos[i][j] != Double.POSITIVE_INFINITY && i != j) {
                    arestas.add(new Aresta(i, j, matrizPesos[i][j]));
                }
            }
        }
        return arestas;
    }

    @Override
    public double[][] getMatrizPesos() {
        double[][] copia = new double[numeroVertices][numeroVertices];
        for (int i = 0; i < numeroVertices; i++) {
            System.arraycopy(matrizPesos[i], 0, copia[i], 0, numeroVertices);
        }
        return copia;
    }

    @Override
    public void imprimirGrafo() {
        System.out.println("Grafo (Matriz de Adjacência) — Vértices: 0.." + (numeroVertices - 1) +
                ", direcionado=" + (direcionado ? "sim" : "não") +
                ", ponderado=" + (ponderado ? "sim" : "não"));
        System.out.println("INF indica ausência de aresta.");

        int width = 3;
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                double v = matrizPesos[i][j];
                if (v != Double.POSITIVE_INFINITY) {
                    String s = String.format(java.util.Locale.US, "%.1f", v);
                    if (s.length() > width) width = s.length();
                }
            }
        }

        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                double valor = matrizPesos[i][j];
                String s = valor == Double.POSITIVE_INFINITY ? "INF" : String.format(java.util.Locale.US, "%.1f", valor);
                System.out.printf("%" + (width + 1) + "s", s);
            }
            System.out.println();
        }
    }
}
