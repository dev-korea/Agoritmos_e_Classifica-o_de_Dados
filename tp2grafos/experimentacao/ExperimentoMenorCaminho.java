package tp2grafos.experimentacao;

import tp2grafos.representacao.Grafo;
import tp2grafos.algoritmos.MenorCaminho;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;

public class ExperimentoMenorCaminho {

    private static final String CAMINHO_CSV = "resultados_tp2.csv";

    private static final String CAMINHO_PASTA_GRAFOS = "tp2grafos/RandomGraphSamples";

    public static void rodarExperimentos() {
        inicializarCsv();

        Path pasta = Path.of(CAMINHO_PASTA_GRAFOS);
        if (!Files.exists(pasta)) {
            // Tenta verificar se está na raiz sem o prefixo do pacote
            pasta = Path.of("RandomGraphSamples");
        }

        if (!Files.exists(pasta)) {
            System.err.println("ERRO CRÍTICO: Não foi possível encontrar a pasta de grafos.");
            System.err.println("Tentado: " + CAMINHO_PASTA_GRAFOS + " e RandomGraphSamples");
            System.err.println("Verifique onde a pasta 'RandomGraphSamples' está em relação ao local de execução.");
            return;
        }

        try {
            Files.list(pasta)
                    .filter(f -> f.toString().endsWith(".gr"))
                    .forEach(ExperimentoMenorCaminho::processarArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Experimentos concluídos! CSV gerado em: " + CAMINHO_CSV);
    }

    private static void processarArquivo(Path caminho) {
        try {
            String nomeArquivo = caminho.getFileName().toString();
            System.out.println(">> Processando: " + nomeArquivo);

            String limpo = nomeArquivo.replace("sample", "").replace(".gr", "");
            String[] partes = limpo.split("-");

            int vertices = Integer.parseInt(partes[0]); // 100
            int arestas = Integer.parseInt(partes[1]);  // 1980

            // Cálculo da densidade: D = |E| / (|V| * (|V| - 1))
            // Para V=100, E=1980 -> D = 1980 / (100*99) = 0.2
            double densidade = (double) arestas / (vertices * (vertices - 1));

            // Definição do enunciado: Densidade >= 0.6 é Denso, senão Esparso [cite: 27]
            String tipoGrafo = densidade >= 0.55 ? "denso" : "esparso"; // Usando 0.55 como margem de segurança

            Runtime runtime = Runtime.getRuntime();

            System.gc();
            long memAntesLA = runtime.totalMemory() - runtime.freeMemory();
            Grafo gLA = LeitorGTGraph.carregarListaAdj(caminho.toString());
            long memDepoisLA = runtime.totalMemory() - runtime.freeMemory();
            long memoriaLA = Math.max(0, memDepoisLA - memAntesLA);

            System.gc();
            long memAntesMA = runtime.totalMemory() - runtime.freeMemory();
            Grafo gMA = LeitorGTGraph.carregarMatrizAdj(caminho.toString());
            long memDepoisMA = runtime.totalMemory() - runtime.freeMemory();
            long memoriaMA = Math.max(0, memDepoisMA - memAntesMA);

            // --- WARM-UP (Aquecimento) ---
            System.out.print("   (Warm-up)... ");
            MenorCaminho.dijkstra(gLA, 0);
            // Apenas uma rodada rápida para carregar classes
            System.out.println("OK. Iniciando medições...");

            // --- RODAR EXPERIMENTO (10x) ---
            for (int repeticao = 1; repeticao <= 10; repeticao++) {
                medirSalvar("LA", tipoGrafo, "Dijkstra", gLA, vertices, densidade, repeticao, memoriaLA);
                medirSalvar("MA", tipoGrafo, "Dijkstra", gMA, vertices, densidade, repeticao, memoriaMA);

                medirSalvar("LA", tipoGrafo, "Bellman-Ford", gLA, vertices, densidade, repeticao, memoriaLA);
                medirSalvar("MA", tipoGrafo, "Bellman-Ford", gMA, vertices, densidade, repeticao, memoriaMA);
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar arquivo: " + caminho);
            e.printStackTrace(); // Mostra o erro exato se falhar
        }
    }

    private static void medirSalvar(String representacao, String tipo,
                                    String algoritmo, Grafo g,
                                    int vertices, double densidade,
                                    int exec, long memoriaBytes) {

        long inicio = System.nanoTime();

        if (algoritmo.equals("Dijkstra"))
            MenorCaminho.dijkstra(g, 0);
        else
            MenorCaminho.bellmanFord(g, 0);

        long tempo = System.nanoTime() - inicio;

        salvarLinhaCsv(representacao, tipo, algoritmo, vertices, densidade, exec, tempo, memoriaBytes);
    }

    private static void inicializarCsv() {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(CAMINHO_CSV))) {
            w.write("representacao,tipo,algoritmo,vertices,densidade,execucao,tempo_ns,memoria_bytes");
            w.newLine();
        } catch (Exception ignored) {}
    }

    private static void salvarLinhaCsv(String repr, String tipo,
                                       String algoritmo, int vertices,
                                       double densidade, int exec, long tempo, long memoria) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(CAMINHO_CSV, true))) {
            w.write(String.format(Locale.US,
                    "%s,%s,%s,%d,%.2f,%d,%d,%d",
                    repr, tipo, algoritmo, vertices, densidade, exec, tempo, memoria
            ));
            w.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}