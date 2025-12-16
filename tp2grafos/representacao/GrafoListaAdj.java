package tp2grafos.representacao;

import tp2grafos.modelo.Aresta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrafoListaAdj implements Grafo {

    private final int numeroVertices;
    private final boolean direcionado;
    private final boolean ponderado;
    private final List<List<Aresta>> listasAdjacencia;

    public GrafoListaAdj(int numeroVertices, boolean direcionado, boolean ponderado) {
        this.numeroVertices = numeroVertices;
        this.direcionado = direcionado;
        this.ponderado = ponderado;
        this.listasAdjacencia = new ArrayList<>();

        for (int i = 0; i < numeroVertices; i++) {
            listasAdjacencia.add(new ArrayList<>());
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
        listasAdjacencia.get(origem).add(new Aresta(origem, destino, peso));
        if (!direcionado) {
            listasAdjacencia.get(destino).add(new Aresta(destino, origem, peso));
        }
    }

    @Override
    public Iterable<Aresta> getAdjacentes(int vertice) {
        return Collections.unmodifiableList(listasAdjacencia.get(vertice));
    }

    @Override
    public List<Aresta> getTodasArestas() {
        List<Aresta> arestas = new ArrayList<>();
        for (List<Aresta> lista : listasAdjacencia) {
            arestas.addAll(lista);
        }
        return arestas;
    }

    @Override
    public double[][] getMatrizPesos() {
        double[][] matriz = new double[numeroVertices][numeroVertices];
        for (int i = 0; i < numeroVertices; i++) {
            for (int j = 0; j < numeroVertices; j++) {
                if (i == j) {
                    matriz[i][j] = 0.0;
                } else {
                    matriz[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (Aresta a : getTodasArestas()) {
            matriz[a.getOrigem()][a.getDestino()] = a.getPeso();
        }

        return matriz;
    }

    @Override
    public void imprimirGrafo() {
        System.out.println("Grafo (Lista de Adjacência) — Vértices: 0.." + (numeroVertices - 1) + 
                ", direcionado=" + (direcionado ? "sim" : "não") + 
                ", ponderado=" + (ponderado ? "sim" : "não"));
        System.out.println("Formato: v: -> destino(w=peso)");
        for (int v = 0; v < numeroVertices; v++) {
            System.out.print(v + ":");
            for (Aresta a : listasAdjacencia.get(v)) {
                System.out.print(" -> " + a.getDestino() + "(w=" + a.getPeso() + ")");
            }
            System.out.println();
        }
    }
}
