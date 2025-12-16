public class Produto {
    private final DescricaoProduto descricao;
    private final String nome;
    private double preco;
    private int estoque;

    public Produto(DescricaoProduto descricao, String nome, double preco, int estoque) {
        this.descricao = descricao;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public DescricaoProduto getDescricao() { return descricao; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }

    public void setPreco(double preco) {
        if (preco < 0) throw new IllegalArgumentException("Preço inválido");
        this.preco = preco;
    }

    public boolean temEstoque(int qtd) {
        return qtd > 0 && qtd <= estoque;
    }

    public void baixarEstoque(int qtd) {
        if (!temEstoque(qtd)) throw new IllegalArgumentException("Estoque insuficiente");
        estoque -= qtd;
    }

    @Override
    public String toString() {
        return String.format("%s - %s | R$ %.2f | estoque: %d",
                descricao, nome, preco, estoque);
    }
}
