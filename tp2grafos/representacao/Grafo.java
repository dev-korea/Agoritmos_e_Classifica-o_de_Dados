package tp2grafos.representacao;

import tp2grafos.modelo.Aresta;

import java.util.List;

public interface Grafo {

    int getNumeroVertices();

    boolean isDirecionado();

    boolean isPonderado();

    void adicionarAresta(int origem, int destino);

    void adicionarAresta(int origem, int destino, double peso);

    Iterable<Aresta> getAdjacentes(int vertice);

    List<Aresta> getTodasArestas();

    /**
     * Matriz de pesos usada por algoritmos baseados em matriz.
     * Se não houver aresta, use Double.POSITIVE_INFINITY.
     */
    double[][] getMatrizPesos();

    void imprimirGrafo();
}
