import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Main {

    // LÓGICA DE NORMALIZAÇÃO (CORRIGIDA)
    // Converte "Ação" -> "acao" para bater com o banco de dados
    private static String normalizarEntrada(String s){
        if (s == null) {
            return "";
        }

        // 1. Separa os acentos das letras
        String nfdNormalizedString = Normalizer.normalize(s, Normalizer.Form.NFD);

        // 2. Remove os sinais diacríticos (acentos)
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String stringSemAcentos = pattern.matcher(nfdNormalizedString).replaceAll("");

        // 3. Mantém apenas letras a-z minúsculas
        return stringSemAcentos.toLowerCase().replaceAll("[^a-z]", "");
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        Trie trie = new Trie();
        boolean carregado = false;
        int totalPalavras = 0;

        while(true){
            System.out.println("====================================");
            System.out.println("TP3 - TRIE AUTO-COMPLETE (CLI)");
            System.out.println("1) Carregar arquivo de palavras");
            System.out.println("2) Buscar sugestoes por prefixo (Paginado)");
            System.out.println("3) Visualizar subarvore do prefixo (Debug)");
            System.out.println("0) Sair");
            System.out.println("====================================");
            System.out.print("Opcao: ");

            String op = sc.nextLine().trim();

            if(op.equals("0")){
                System.out.println("Saindo...");
                break;
            }

            // ============================================================
            // OPÇÃO 1: CARREGAR ARQUIVO
            // ============================================================
            if(op.equals("1")){
                System.out.print("Digite o caminho do arquivo (ex: dados/words.txt): ");
                String caminho = sc.nextLine().trim();

                try{
                    Path p = Path.of(caminho);

                    if(!Files.exists(p)){
                        System.out.println("Arquivo nao encontrado: " + p.toString());
                        continue;
                    }

                    trie = new Trie(); // Zera a árvore para nova carga
                    totalPalavras = CarregadorPalavras.carregarParaTrie(p, trie);
                    carregado = true;

                    System.out.println("Arquivo carregado com sucesso!");
                    System.out.println("Total de palavras inseridas na Trie: " + totalPalavras);
                }catch(Exception e){
                    System.out.println("Erro ao carregar arquivo: " + e.getMessage());
                }

                // ============================================================
                // OPÇÃO 2: BUSCAR SUGESTÕES (COM PAGINAÇÃO)
                // ============================================================
            }else if(op.equals("2")){
                if(!carregado){
                    System.out.println("Voce precisa carregar o arquivo primeiro (opcao 1).");
                    continue;
                }

                System.out.print("Digite o prefixo: ");
                String prefixo = sc.nextLine();

                prefixo = normalizarEntrada(prefixo); // Usa a normalização nova
                if(prefixo.isEmpty()){
                    System.out.println("Prefixo vazio/invalido.");
                    continue;
                }

                // Busca um lote grande (1000) para permitir paginação
                // O usuário NÃO define mais o limite manualmente aqui.
                List<String> todasSugestoes = trie.autocomplete(prefixo, 1000);

                if(todasSugestoes.isEmpty()){
                    System.out.println("Sem sugestoes para o prefixo: " + prefixo);
                }else{
                    // LÓGICA DE PAGINAÇÃO (15 por vez)
                    int total = todasSugestoes.size();
                    int paginaAtual = 0;
                    int tamanhoPagina = 15;

                    while(true){
                        int inicio = paginaAtual * tamanhoPagina;

                        // Se não tem mais itens para mostrar, para.
                        if(inicio >= total) break;

                        int fim = Math.min(inicio + tamanhoPagina, total);

                        System.out.println("\n--- Sugestoes " + (inicio + 1) + " a " + fim + " de " + total + " ---");

                        for(int i = inicio; i < fim; i++){
                            System.out.println((i + 1) + ") " + todasSugestoes.get(i));
                        }

                        if(fim == total){
                            System.out.println("\n(Fim das sugestoes)");
                            break;
                        }

                        System.out.println("------------------------------------------------");
                        System.out.println("[ENTER] Proxima pagina | [0] Voltar ao Menu");
                        System.out.print("Opcao: ");
                        String nav = sc.nextLine().trim();

                        if(nav.equals("0")){
                            break;
                        }
                        // Enter avança página
                        paginaAtual++;
                    }
                }

                // ============================================================
                // OPÇÃO 3: DEBUG VISUAL (MANTÉM LIMITE MANUAL)
                // ============================================================
            }else if(op.equals("3")){
                if(!carregado){
                    System.out.println("Voce precisa carregar o arquivo primeiro (opcao 1).");
                    continue;
                }

                System.out.print("Digite o prefixo para visualizar a subarvore: ");
                String prefixo = sc.nextLine();

                prefixo = normalizarEntrada(prefixo);
                if(prefixo.isEmpty()){
                    System.out.println("Prefixo vazio/invalido.");
                    continue;
                }

                System.out.print("Quantas palavras completas mostrar na visualizacao (ex: 20)? ");
                String sLimite = sc.nextLine().trim();

                int limite = 20;
                try{
                    limite = Integer.parseInt(sLimite);
                    if(limite <= 0){
                        limite = 20;
                    }
                }catch(Exception e){
                    limite = 20;
                }

                trie.imprimirSubarvore(prefixo, limite);

            }else{
                System.out.println("Opcao invalida.");
            }

            System.out.println();
        }

        sc.close();
    }
}