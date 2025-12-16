package tp2grafos.experimentacao;

import tp2grafos.representacao.Grafo;
import tp2grafos.representacao.GrafoListaAdj;
import tp2grafos.representacao.GrafoMatrizAdj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LeitorGTGraph {

    public static Grafo carregarListaAdj(String caminhoArquivo) throws IOException {
        return carregar(caminhoArquivo, true);
    }

    public static Grafo carregarMatrizAdj(String caminhoArquivo) throws IOException {
        return carregar(caminhoArquivo, false);
    }

    private static Grafo carregar(String caminhoArquivo, boolean lista) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {

            int vertices = 0;
            int arestas = 0;

            String linha;

            while ((linha = br.readLine()) != null) {
                if (linha.startsWith("p")) {
                    String[] partes = linha.split(" ");
                    vertices = Integer.parseInt(partes[2]);
                    arestas = Integer.parseInt(partes[3]);
                    break;
                }
            }

            Grafo grafo = lista ?
                    new GrafoListaAdj(vertices, true, true) :
                    new GrafoMatrizAdj(vertices, true, true);

            while ((linha = br.readLine()) != null) {
                if (linha.startsWith("a")) {
                    String[] p = linha.split(" ");
                    int u = Integer.parseInt(p[1]) - 1;
                    int v = Integer.parseInt(p[2]) - 1;
                    double w = Double.parseDouble(p[3]);
                    grafo.adicionarAresta(u, v, w);
                }
            }

            return grafo;
        }
    }
}
