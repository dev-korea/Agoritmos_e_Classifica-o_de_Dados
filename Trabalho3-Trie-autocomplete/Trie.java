import java.util.ArrayList;
import java.util.List;

public class Trie {

    private static class NoTrie {
        NoTrie[] filhos;
        boolean fim_palavra;

        NoTrie(){
            this.filhos = new NoTrie[26];
            this.fim_palavra = false;
        }
    }

    private final NoTrie raiz;

    public Trie(){
        this.raiz = new NoTrie();
    }

    public void insert(String palavra){
        if(palavra == null){
            return;
        }

        NoTrie atual = raiz;

        for(int i = 0; i < palavra.length(); i++){
            char c = palavra.charAt(i);
            int idx = c - 'a';
            if(idx < 0 || idx >= 26){
                continue; // assume entrada já normalizada
            }

            if(atual.filhos[idx] == null){
                atual.filhos[idx] = new NoTrie();
            }
            atual = atual.filhos[idx];
        }

        atual.fim_palavra = true;
    }


    // AUTO-COMPLETE

    // 1) Localiza o nó correspondente ao prefixo
    private NoTrie buscarNo(String prefixo){
        if(prefixo == null){
            return null;
        }

        NoTrie atual = raiz;

        for(int i = 0; i < prefixo.length(); i++){
            char c = prefixo.charAt(i);
            int idx = c - 'a';
            if(idx < 0 || idx >= 26){
                return null; // prefixo inválido
            }

            if(atual.filhos[idx] == null){
                return null; // prefixo inexistente
            }
            atual = atual.filhos[idx];
        }

        return atual;
    }

    // 2) Retorna sugestões para o prefixo (com limite de resultados)
    public List<String> autocomplete(String prefixo, int limite){
        List<String> sugestoes = new ArrayList<>();
        if(prefixo == null){
            return sugestoes;
        }
        if(limite <= 0){
            return sugestoes;
        }

        // assume prefixo já normalizado (a-z)
        NoTrie noPrefixo = buscarNo(prefixo);
        if(noPrefixo == null){
            return sugestoes; // prefixo inexistente -> sem sugestões
        }

        StringBuilder atual = new StringBuilder(prefixo);
        dfsColetar(noPrefixo, atual, sugestoes, limite);

        return sugestoes;
    }

    public void imprimirSubarvore(String prefixo, int limite){
        if(prefixo == null){
            System.out.println("(prefixo null)");
            return;
        }
        if(limite <= 0){
            System.out.println("(limite invalido)");
            return;
        }

        NoTrie no = buscarNo(prefixo);
        if(no == null){
            System.out.println("Prefixo \"" + prefixo + "\" nao existe na Trie.");
            return;
        }

        System.out.println("Subarvore do prefixo: \"" + prefixo + "\"");
        System.out.println(prefixo + (no.fim_palavra ? " [PALAVRA]" : ""));
        StringBuilder sb = new StringBuilder(prefixo);
        imprimirRec(no, sb, 0, limite, new int[]{0});
    }

    private void imprimirRec(NoTrie no, StringBuilder atual, int nivel, int limite, int[] cont){
        if(cont[0] >= limite){
            return;
        }

        for(int i = 0; i < 26; i++){
            if(no.filhos[i] != null){
                atual.append((char)('a' + i));

                // indentação
                for(int k = 0; k < nivel + 1; k++){
                    System.out.print("  ");
                }

                // imprime o "ramo" atual
                System.out.print("└─ " + (char)('a' + i));
                if(no.filhos[i].fim_palavra){
                    System.out.print("  -> " + atual.toString() + " [PALAVRA]");
                    cont[0]++;
                }
                System.out.println();

                imprimirRec(no.filhos[i], atual, nivel + 1, limite, cont);
                atual.deleteCharAt(atual.length() - 1);

                if(cont[0] >= limite){
                    return;
                }
            }
        }
    }

    // 3) DFS para coletar palavras abaixo do nó do prefixo
    private void dfsColetar(NoTrie no, StringBuilder atual, List<String> out, int limite){
        if(out.size() >= limite){
            return;
        }

        // Se esse nó marca fim de palavra, adiciona a palavra atual
        if(no.fim_palavra){
            out.add(atual.toString());
            if(out.size() >= limite){
                return;
            }
        }

        // Percorre filhos em ordem (a..z) para sugestões ordenadas
        for(int i = 0; i < 26; i++){
            if(no.filhos[i] != null){
                atual.append((char)('a' + i));
                dfsColetar(no.filhos[i], atual, out, limite);
                atual.deleteCharAt(atual.length() - 1); // backtrack
                if(out.size() >= limite){
                    return;
                }
            }
        }
    }
}
