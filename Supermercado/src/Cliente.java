public class Cliente {
    private final String nome;
    private final String cpf;

    public Cliente(String nome, String cpf) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome vazio");
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF vazio");
        this.nome = nome.trim();
        this.cpf = cpf.replaceAll("\\D", ""); // só dígitos
    }

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }

    @Override
    public String toString() {
        return nome + " (CPF: " + cpf + ")";
    }
}
