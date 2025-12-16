package tp2grafos.app;

import tp2grafos.algoritmos.*;
import tp2grafos.experimentacao.ExperimentoMenorCaminho;
import tp2grafos.modelo.Aresta;
import tp2grafos.representacao.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static Grafo grafoAtual;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== TP2 - Grafos ===");
            System.out.println("1 - Criar grafo (Lista de Adjacência)");
            System.out.println("2 - Criar grafo (Matriz de Adjacência)");
            System.out.println("3 - Imprimir grafo");
            System.out.println("4 - BFS (distância em número de arestas)");
            System.out.println("5 - DFS (tempo de entrada)");
            System.out.println("6 - Ordenação Topológica (apenas DAG)");
            System.out.println("7 - Prim (Árvore Geradora Mínima)");
            System.out.println("8 - Kruskal (Árvore Geradora Mínima)");
            System.out.println("9 - Dijkstra (pesos não negativos)");
            System.out.println("10 - Bellman-Ford (aceita pesos negativos)");
            System.out.println("11 - Floyd-Warshall (todos os pares)");
            System.out.println("12 - Fluxo Máximo (Ford-Fulkerson)");
            System.out.println("13 - Executar experimento simples (Dijkstra x Bellman-Ford)");
            System.out.println("0 - Sair");
            int opcao = lerInteiro(scanner, "Escolha: ", 0, 13);

            switch (opcao) {
                case 1 -> criarGrafoLista(scanner);
                case 2 -> criarGrafoMatriz(scanner);
                case 3 -> imprimirGrafo();
                case 4 -> executarBfs(scanner);
                case 5 -> executarDfs(scanner);
                case 6 -> executarTopologica();
                case 7 -> executarPrim(scanner);
                case 8 -> executarKruskal();
                case 9 -> executarDijkstra(scanner);
                case 10 -> executarBellmanFord(scanner);
                case 11 -> executarFloydWarshall();
                case 12 -> executarFluxoMaximo(scanner);
                case 13 -> ExperimentoMenorCaminho.rodarExperimentos();
                case 0 -> continuar = false;
                default -> System.out.println("Opção inválida.");
            }
        }

        scanner.close();
    }

    private static void criarGrafoLista(Scanner scanner) {
        int n = lerInteiro(scanner, "Número de vértices (ex: 5): ", 1, Integer.MAX_VALUE);
        boolean direcionado = lerBooleanoSimNao(scanner, "Grafo direcionado? (s/n) [s=sim, n=não]: ");
        boolean ponderado = lerBooleanoSimNao(scanner, "Grafo ponderado? (s/n) [s=sim, n=não]: ");

        GrafoListaAdj grafo = new GrafoListaAdj(n, direcionado, ponderado);
        inserirArestas(scanner, grafo, ponderado);
        grafoAtual = grafo;
    }

    private static void criarGrafoMatriz(Scanner scanner) {
        int n = lerInteiro(scanner, "Número de vértices (ex: 5): ", 1, Integer.MAX_VALUE);
        boolean direcionado = lerBooleanoSimNao(scanner, "Grafo direcionado? (s/n) [s=sim, n=não]: ");
        boolean ponderado = lerBooleanoSimNao(scanner, "Grafo ponderado? (s/n) [s=sim, n=não]: ");

        GrafoMatrizAdj grafo = new GrafoMatrizAdj(n, direcionado, ponderado);
        inserirArestas(scanner, grafo, ponderado);
        grafoAtual = grafo;
    }

    private static void inserirArestas(Scanner scanner, Grafo grafo, boolean ponderado) {
        System.out.println("Digite as arestas. Formato e exemplos:");
        if (ponderado) {
            System.out.println("origem destino peso");
            System.out.println("Ex: 0 1 2.5");
        } else {
            System.out.println("origem destino");
            System.out.println("Ex: 0 1");
        }
        System.out.println("Vértices válidos: 0 até " + (grafo.getNumeroVertices() - 1));
        System.out.println("Digite 'fim' para encerrar.");

        int n = grafo.getNumeroVertices();
        long maxArestas = grafo.isDirecionado() ? (long) n * (n - 1) : (long) n * (n - 1) / 2;
        long adicionadas = 0;

        while (true) {
            System.out.print("Aresta: ");
            String linha = scanner.nextLine().trim();
            if (linha.equalsIgnoreCase("fim")) {
                break;
            }
            String[] partes = linha.split("\\s+");
            if ((ponderado && partes.length != 3) || (!ponderado && partes.length != 2)) {
                System.out.println("Entrada inválida.");
                continue;
            }
            int origem;
            int destino;
            try {
                origem = Integer.parseInt(partes[0]);
                destino = Integer.parseInt(partes[1]);
            } catch (Exception e) {
                System.out.println("Entrada inválida.");
                continue;
            }
            if (origem < 0 || origem >= n || destino < 0 || destino >= n) {
                System.out.println("Vértice inválido.");
                continue;
            }
            if (adicionadas >= maxArestas) {
                System.out.println("Limite de arestas atingido.");
                break;
            }
            if (ponderado) {
                double peso;
                try {
                    peso = Double.parseDouble(partes[2]);
                } catch (Exception e) {
                    System.out.println("Entrada inválida.");
                    continue;
                }
                grafo.adicionarAresta(origem, destino, peso);
            } else {
                grafo.adicionarAresta(origem, destino);
            }
            adicionadas++;
        }
    }

    private static void imprimirGrafo() {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        grafoAtual.imprimirGrafo();
    }

    private static void executarBfs(Scanner scanner) {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        int origem = lerInteiro(scanner, "Vértice de origem: ", 0, grafoAtual.getNumeroVertices() - 1);
        int[] distancias = Buscas.bfs(grafoAtual, origem);
        System.out.println("Distâncias a partir de " + origem + ": (vértices inalcançáveis recebem -1)");
        for (int i = 0; i < distancias.length; i++) {
            System.out.println(origem + " -> " + i + " = " + distancias[i]);
        }
    }

    private static void executarDfs(Scanner scanner) {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        int origem = lerInteiro(scanner, "Vértice de origem: ", 0, grafoAtual.getNumeroVertices() - 1);
        int[] tempos = Buscas.dfs(grafoAtual, origem);
        System.out.println("Tempo de entrada de cada vértice (ordem de descoberta):");
        for (int i = 0; i < tempos.length; i++) {
            System.out.println("vértice " + i + ": " + tempos[i]);
        }
    }

    private static void executarTopologica() {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        if (!grafoAtual.isDirecionado()) {
            System.out.println("Operação válida apenas para grafos direcionados.");
            return;
        }
        System.out.println("Ordenação Topológica (apenas grafos direcionados acíclicos - DAG):");
        System.out.println(Buscas.ordenacaoTopologica(grafoAtual));
    }

    private static void executarPrim(Scanner scanner) {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        int inicio = lerInteiro(scanner, "Vértice inicial: ", 0, grafoAtual.getNumeroVertices() - 1);
        List<Aresta> agm = ArvoresGeradorasMinimas.prim(grafoAtual, inicio);
        System.out.println("Árvore Geradora Mínima (Prim) — aresta (origem -> destino, w=peso):");
        for (Aresta a : agm) {
            System.out.println(a);
        }
    }

    private static void executarKruskal() {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        List<Aresta> agm = ArvoresGeradorasMinimas.kruskal(grafoAtual);
        System.out.println("Árvore Geradora Mínima (Kruskal) — aresta (origem -> destino, w=peso):");
        for (Aresta a : agm) {
            System.out.println(a);
        }
    }

    private static void executarDijkstra(Scanner scanner) {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        int origem = lerInteiro(scanner, "Vértice de origem: ", 0, grafoAtual.getNumeroVertices() - 1);
        boolean negativo = false;
        for (Aresta a : grafoAtual.getTodasArestas()) {
            if (a.getPeso() < 0) {
                negativo = true;
                break;
            }
        }
        if (negativo) {
            System.out.println("Aviso: Há arestas com peso negativo. Dijkstra exige pesos não negativos.");
        }
        double[] dist = MenorCaminho.dijkstra(grafoAtual, origem);
        System.out.println("Distâncias mínimas a partir de " + origem + " (Dijkstra):");
        for (int i = 0; i < dist.length; i++) {
            System.out.println(origem + " -> " + i + " = " + dist[i]);
        }
    }

    private static void executarBellmanFord(Scanner scanner) {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        int origem = lerInteiro(scanner, "Vértice de origem: ", 0, grafoAtual.getNumeroVertices() - 1);
        double[] dist = MenorCaminho.bellmanFord(grafoAtual, origem);
        System.out.println("Distâncias mínimas a partir de " + origem + " (Bellman-Ford):");
        for (int i = 0; i < dist.length; i++) {
            System.out.println(origem + " -> " + i + " = " + dist[i]);
        }
    }

    private static void executarFloydWarshall() {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        double[][] dist = MenorCaminho.floydWarshall(grafoAtual);
        int n = grafoAtual.getNumeroVertices();
        System.out.println("Matriz de distâncias (Floyd-Warshall) — use 'INF' para ausência de caminho:");

        int width = 3;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double v = dist[i][j];
                if (v != Double.POSITIVE_INFINITY) {
                    String s = String.format(java.util.Locale.US, "%.1f", v);
                    if (s.length() > width) width = s.length();
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double d = dist[i][j];
                String s = d == Double.POSITIVE_INFINITY ? "INF" : String.format(java.util.Locale.US, "%.1f", d);
                System.out.printf("%" + (width + 1) + "s", s);
            }
            System.out.println();
        }
    }

    private static void executarFluxoMaximo(Scanner scanner) {
        if (grafoAtual == null) {
            System.out.println("Nenhum grafo carregado.");
            return;
        }
        int fonte = lerInteiro(scanner, "Vértice fonte: ", 0, grafoAtual.getNumeroVertices() - 1);
        int sorvedouro = lerInteiro(scanner, "Vértice sorvedouro: ", 0, grafoAtual.getNumeroVertices() - 1);
        if (!grafoAtual.isDirecionado()) {
            System.out.println("Aviso: Fluxo máximo é usualmente definido em grafos direcionados. Resultado pode não representar um fluxo válido.");
        }
        double fluxo = FluxoMaximo.fordFulkerson(grafoAtual, fonte, sorvedouro);
        System.out.println("Fluxo máximo de " + fonte + " para " + sorvedouro + ": " + fluxo);
    }

    private static int lerInteiro(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) {
                    System.out.println("Valor fora do intervalo [" + min + ".." + max + "].");
                    continue;
                }
                return v;
            } catch (Exception e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    private static boolean lerBooleanoSimNao(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim().toLowerCase();
            if (s.equals("s")) return true;
            if (s.equals("n")) return false;
            System.out.println("Entrada inválida. Responda 's' para sim ou 'n' para não.");
        }
    }
}
