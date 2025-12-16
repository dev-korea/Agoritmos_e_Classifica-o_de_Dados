import java.util.*;

public class SupermercadoApp {
    private static final Scanner sc = new Scanner(System.in);
    private static final List<Produto> catalogo = new ArrayList<>();

    public static void main(String[] args) {
        seedCatalogo();

        Pedido pedidoAtual = null;

        while (true) {
            System.out.println("\n=== SUPERMERCADO ===");
            System.out.println("1) Novo pedido");
            System.out.println("2) Realizar pagamento");
            System.out.println("0) Sair");
            System.out.print("Opção: ");
            String op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        pedidoAtual = criarPedido();
                        break;
                    case "2":
                        if (pedidoAtual == null) {
                            System.out.println("Não há pedido em aberto. Crie um novo primeiro.");
                        } else {
                            realizarPagamento(pedidoAtual);
                            System.out.println("\n--- RECIBO ---");
                            System.out.println(pedidoAtual);
                            // após pagar, "fecha" o pedido
                            pedidoAtual = null;
                        }
                        break;
                    case "0":
                        System.out.println("Encerrando...");
                        return;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void seedCatalogo() {
        catalogo.add(new Produto(DescricaoProduto.ARROZ,   "Arroz Branco 5kg",  24.90, 20));
        catalogo.add(new Produto(DescricaoProduto.FEIJAO,  "Feijão Carioca 1kg", 8.49,  30));
        catalogo.add(new Produto(DescricaoProduto.FARINHA, "Farinha de Trigo 1kg", 6.99, 25));
        catalogo.add(new Produto(DescricaoProduto.LEITE,   "Leite Integral 1L",  4.79,  40));
    }

    private static Pedido criarPedido() {
        System.out.println("\n=== NOVO PEDIDO ===");
        System.out.print("Nome do cliente: ");
        String nome = sc.nextLine();
        System.out.print("CPF do cliente (apenas números ou com máscara): ");
        String cpf = sc.nextLine();

        Pedido pedido = new Pedido(new Cliente(nome, cpf));

        boolean adicionando = true;
        while (adicionando) {
            listarCatalogo();
            System.out.print("Código do produto (ou ENTER para finalizar itens): ");
            String cod = sc.nextLine().trim();
            if (cod.isEmpty()) break;

            int idx = parseIntOrThrow(cod, "Código inválido");
            if (idx < 1 || idx > catalogo.size()) {
                System.out.println("Produto inexistente.");
                continue;
            }
            Produto selecionado = catalogo.get(idx - 1);

            System.out.print("Quantidade: ");
            int qtd = parseIntOrThrow(sc.nextLine(), "Quantidade inválida");
            pedido.adicionarItem(selecionado, qtd);
            System.out.printf("Item adicionado! Subtotal atual: R$ %.2f%n", pedido.getTotal());

            System.out.print("Adicionar outro item? (s/N): ");
            adicionando = sc.nextLine().trim().equalsIgnoreCase("s");
        }

        System.out.println("\n--- RESUMO DO PEDIDO ---");
        System.out.println(pedido);
        return pedido;
    }

    private static void realizarPagamento(Pedido pedido) {
        if (pedido.getItens().isEmpty()) {
            throw new IllegalStateException("Pedido sem itens.");
        }

        System.out.println("\n=== PAGAMENTO ===");
        System.out.printf("Total a pagar: R$ %.2f%n", pedido.getTotal());
        for (int i = 0; i < TipoPagamento.values().length; i++) {
            System.out.printf("%d) %s%n", i + 1, TipoPagamento.values()[i]);
        }
        System.out.print("Escolha a forma de pagamento: ");
        int op = parseIntOrThrow(sc.nextLine(), "Opção inválida");
        if (op < 1 || op > TipoPagamento.values().length) {
            throw new IllegalArgumentException("Opção inexistente.");
        }
        TipoPagamento tipo = TipoPagamento.values()[op - 1];

        pedido.pagar(tipo);
        System.out.println("Pagamento realizado com sucesso!");
    }

    private static void listarCatalogo() {
        System.out.println("\n--- CATÁLOGO ---");
        for (int i = 0; i < catalogo.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, catalogo.get(i));
        }
    }

    private static int parseIntOrThrow(String s, String msg) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { throw new IllegalArgumentException(msg); }
    }
}
