import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private final Cliente cliente;
    private final List<ItemPedido> itens = new ArrayList<>();
    private TipoPagamento tipoPagamento;
    private boolean pago = false;

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
    }

    public void adicionarItem(Produto p, int qtd) {
        if (p == null) throw new IllegalArgumentException("Produto nulo");
        if (!p.temEstoque(qtd)) throw new IllegalArgumentException("Estoque insuficiente para " + p.getNome());
        itens.add(new ItemPedido(p, qtd));
    }

    public double getTotal() {
        return itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
    }

    public boolean isPago() { return pago; }
    public List<ItemPedido> getItens() { return List.copyOf(itens); }
    public Cliente getCliente() { return cliente; }
    public TipoPagamento getTipoPagamento() { return tipoPagamento; }

    /** Realiza pagamento e dá baixa no estoque dos itens */
    public void pagar(TipoPagamento tipo) {
        if (itens.isEmpty()) throw new IllegalStateException("Pedido sem itens.");
        if (pago) throw new IllegalStateException("Pedido já está pago.");
        this.tipoPagamento = tipo;

        // Efetiva a baixa só aqui (após confirmar forma de pagamento)
        for (ItemPedido item : itens) {
            item.getProduto().baixarEstoque(item.getQuantidade());
        }
        pago = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(cliente).append("\n");
        for (ItemPedido it : itens) sb.append(" - ").append(it).append("\n");
        sb.append(String.format("TOTAL: R$ %.2f", getTotal()));
        if (pago) sb.append(" | Pago em: ").append(tipoPagamento);
        return sb.toString();
    }
}
