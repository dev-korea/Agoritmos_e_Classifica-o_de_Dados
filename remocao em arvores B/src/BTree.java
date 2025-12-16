import java.util.Scanner;

/* ============================================================
 *                    ÁRVORE B — IMPLEMENTAÇÃO
 * ------------------------------------------------------------
 * Esta versão foi reorganizada com foco pedagógico:
 *  - blocos lógicos claramente divididos
 *  - comentários conceituais (invariantes da Árvore B)
 *  - nomes consistentes
 *  - explicações para os casos de remoção
 *
 * Grau mínimo (t):
 *    - cada nó possui entre t-1 e 2t-1 chaves
 *    - a raiz é exceção e pode ter menos
 *
 * Opera operações clássicas: busca, inserção, split, remoção.
 * ============================================================
 */
class BTreeNode {

    /* ============================================================
     *                       ATRIBUTOS DO NÓ
     * ============================================================
     */
    int[] keys;               // Vetor de chaves (ordenado)
    BTreeNode[] children;     // Ponteiros para filhos
    int numKeys;              // Número atual de chaves
    boolean leaf;             // Indica se é nó folha
    final int t;              // Grau mínimo

    /* ============================================================
     *                         CONSTRUTOR
     * ============================================================
     * Cada nó é alocado com:
     *   - até 2t-1 chaves
     *   - até 2t filhos
     *   - flag indicando se é folha
     */
    BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.keys = new int[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.numKeys = 0;
    }

    /* ============================================================
     *                          BUSCA
     * ------------------------------------------------------------
     * Busca recursiva clássica de Árvore B.
     * A busca percorre o nó até encontrar posição adequada.
     *
     * Custo: O(t * log n) mas como t é constante => O(log n)
     * ============================================================
     */
    BTreeNode search(int k) {
        int i = 0;

        // Avança enquanto k é maior que keys[i]
        while (i < numKeys && k > keys[i])
            i++;

        // Encontrou a chave no próprio nó
        if (i < numKeys && keys[i] == k)
            return this;

        // Se for folha, não existe
        if (leaf)
            return null;

        // Busca recursivamente no filho apropriado
        return children[i].search(k);
    }

    /* ============================================================
     * Conta o número de chaves na B-Tree
     * ============================================================
     */
    int countKeys() {
        int total = numKeys;
        if (!leaf) {
            for (int i = 0; i <= numKeys; i++)
                total += children[i].countKeys();
        }
        return total;
    }

    /* ============================================================
     *                   IMPRESSÃO HIERÁRQUICA DIDÁTICA
     * ============================================================
     */
    void print(int level) {
        for (int i = 0; i < level; i++)
            System.out.print("\t");

        System.out.print("[ ");
        for (int i = 0; i < numKeys; i++)
            System.out.print(keys[i] + " ");
        System.out.println("]");

        if (!leaf) {
            for (int i = 0; i <= numKeys; i++)
                children[i].print(level + 1);
        }
    }

    /* ============================================================
     *                         INSERÇÃO
     * ------------------------------------------------------------
     * Precondição: O nó não está cheio ao chamar insertNonFull().
     * ============================================================
     */
    void insertNonFull(int k) {

        int i = numKeys - 1;

        // Caso 1: nó folha => inserir diretamente
        if (leaf) {

            // Move chaves para abrir espaço
            while (i >= 0 && keys[i] > k) {
                keys[i + 1] = keys[i];
                i--;
            }

            // Insere no local
            keys[i + 1] = k;
            numKeys++;
        }

        // Caso 2: nó interno
        else {

            // Encontra o filho onde devemos descer
            while (i >= 0 && keys[i] > k)
                i--;

            i++;

            // Se o filho está cheio, é necessário dividir antes de descer
            if (children[i].numKeys == 2 * t - 1) {
                splitChild(i, children[i]);

                // Após o split, decidir em qual dos dois filhos descer
                if (keys[i] < k)
                    i++;
            }

            children[i].insertNonFull(k);
        }
    }

    /* ============================================================
     *                         SPLIT (DIVISÃO)
     * ------------------------------------------------------------
     * Divide um nó filho cheio (y) em dois nós:
     *   y: primeira metade
     *   z: segunda metade
     * A chave mediana sobe para o nó pai.
     * ============================================================
     */
    void splitChild(int index, BTreeNode y) {

        // Novo nó z contém as t-1 chaves superiores de y
        BTreeNode z = new BTreeNode(t, y.leaf);
        z.numKeys = t - 1;

        // Copia chaves superiores para z
        for (int j = 0; j < t - 1; j++)
            z.keys[j] = y.keys[j + t];

        // Copia filhos (se não for folha)
        if (!y.leaf) {
            for (int j = 0; j < t; j++)
                z.children[j] = y.children[j + t];
        }

        // Ajusta y para conter apenas t-1 chaves
        y.numKeys = t - 1;

        // Desloca filhos do pai para abrir espaço
        for (int j = numKeys; j >= index + 1; j--)
            children[j + 1] = children[j];

        children[index + 1] = z;

        // Desloca chaves do pai e insere a mediana
        for (int j = numKeys - 1; j >= index; j--)
            keys[j + 1] = keys[j];

        keys[index] = y.keys[t - 1];
        numKeys++;
    }

    /* ============================================================
     *                        REMOÇÃO COMPLETA
     * ============================================================
     * A remoção é o ponto mais delicado da Árvore B.
     * Sempre que descemos na árvore, garantimos que o próximo nó
     * terá pelo menos t chaves (nunca t-1), evitando subfluxos.
     * ============================================================
     */
    void remove(int k) {

        int idx = findKey(k);

        /* ----------------------------
         * Caso 1: chave está neste nó
         * ----------------------------
         */
        if (idx < numKeys && keys[idx] == k) {

            if (leaf)
                removeFromLeaf(idx);

            else
                removeFromInternal(idx);
        }

        /* ----------------------------------------
         * Caso 2: chave está em uma subárvore
         * ----------------------------------------
         */
        else {

            if (leaf) {
                System.out.println("Chave " + k + " não encontrada.");
                return;
            }

            boolean removeFromLastChild = (idx == numKeys);

            // Antes de descer, garantir que o nó filho terá >= t chaves
            if (children[idx].numKeys < t)
                fill(idx);

            // Ajuste após possível fusão
            if (removeFromLastChild && idx > numKeys)
                children[idx - 1].remove(k);
            else
                children[idx].remove(k);
        }
    }

    /* ============================================================
     *               FUNÇÃO AUXILIAR: LOCALIZA CHAVE
     * ============================================================
     */
    int findKey(int k) {
        int idx = 0;
        while (idx < numKeys && keys[idx] < k)
            idx++;
        return idx;
    }

    /* ============================================================
     *                  REMOÇÃO EM NÓ FOLHA (simples)
     * ============================================================
     */
    void removeFromLeaf(int idx) {

        System.out.println("-> removeFromLeaf(): removendo chave " + keys[idx] + " de nó folha.");

        for (int i = idx + 1; i < numKeys; i++)
            keys[i - 1] = keys[i];

        numKeys--;
    }

    /* ============================================================
     *            REMOÇÃO EM NÓ INTERNO (casos conceituais)
     * ------------------------------------------------------------
     * Caso A: o filho à esquerda possui >= t chaves
     *    => substitui pela chave predecessora
     * Caso B: o filho à direita possui >= t chaves
     *    => substitui pela chave sucessora
     * Caso C: ambos têm t-1 chaves
     *    => funde y + chave + z em um único nó
     * ============================================================
     */
    void removeFromInternal(int idx) {

        int k = keys[idx];

        System.out.println("-> removeFromInternal(): removendo chave " + k + " de nó interno (posição " + idx + ").");

        // Caso A: filho à ESQUERDA tem >= t chaves -> usar PREDECESSOR
        if (children[idx].numKeys >= t) {

            System.out.println("   removeFromInternal(): filho ESQUERDO tem >= t chaves -> usar PREDECESSOR.");
            int pred = getPredecessor(idx);
            keys[idx] = pred;
            children[idx].remove(pred);
        }

        // Caso B: filho à DIREITA tem >= t chaves -> usar SUCESSOR
        else if (children[idx + 1].numKeys >= t) {

            System.out.println("   removeFromInternal(): filho DIREITO tem >= t chaves -> usar SUCESSOR.");
            int succ = getSuccessor(idx);
            keys[idx] = succ;
            children[idx + 1].remove(succ);
        }

        // Caso C: ambos filhos têm t-1 chaves -> MERGE
        else {

            System.out.println("   removeFromInternal(): ambos filhos têm t-1 chaves -> MERGE e continuar remoção.");
            merge(idx);
            children[idx].remove(k);
        }
    }

    /* ============================================================
     *                   PREDECESSOR E SUCESSOR
     * ============================================================
     */
    int getPredecessor(int idx) {
        BTreeNode cur = children[idx];
        while (!cur.leaf)
            cur = cur.children[cur.numKeys];
        return cur.keys[cur.numKeys - 1];
    }

    int getSuccessor(int idx) {
        BTreeNode cur = children[idx + 1];
        while (!cur.leaf)
            cur = cur.children[0];
        return cur.keys[0];
    }

    /* ============================================================
     *                         FILL (GARANTIA)
     * ------------------------------------------------------------
     * Garante que o filho children[idx] terá ao menos t chaves.
     * Se um irmão puder emprestar, ok.
     * Senão, precisa fundir.
     * ============================================================
     */
    void fill(int idx) {

        System.out.println("-> fill(): garantindo que o filho de índice " + idx + " terá pelo menos t chaves.");

        if (idx > 0 && children[idx - 1].numKeys >= t) {
            System.out.println("   fill(): caso borrowFromPrev (irmão ESQUERDO pode emprestar).");
            borrowFromPrev(idx);

        } else if (idx < numKeys && children[idx + 1].numKeys >= t) {
            System.out.println("   fill(): caso borrowFromNext (irmão DIREITO pode emprestar).");
            borrowFromNext(idx);

        } else {
            System.out.println("   fill(): nenhum irmão pode emprestar -> caso merge().");
            if (idx < numKeys)
                merge(idx);
            else
                merge(idx - 1);
        }
    }

    /* ============================================================
     *         EMPRESTAR DO IRMÃO ANTERIOR (borrowFromPrev)
     * ============================================================
     */
    void borrowFromPrev(int idx) {

        System.out.println("-> borrowFromPrev(): emprestando chave do irmão ESQUERDO para filho de índice " + idx + ".");

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx - 1];

        // desloca chaves em child para direita
        for (int i = child.numKeys - 1; i >= 0; i--)
            child.keys[i + 1] = child.keys[i];

        // desloca filhos se necessário
        if (!child.leaf)
            for (int i = child.numKeys; i >= 0; i--)
                child.children[i + 1] = child.children[i];

        // traz chave do pai para child
        child.keys[0] = keys[idx - 1];

        if (!child.leaf)
            child.children[0] = sibling.children[sibling.numKeys];

        // move última chave do irmão para o pai
        keys[idx - 1] = sibling.keys[sibling.numKeys - 1];

        child.numKeys++;
        sibling.numKeys--;
    }

    /* ============================================================
     *         EMPRESTAR DO IRMÃO PRÓXIMO (borrowFromNext)
     * ============================================================
     */
    void borrowFromNext(int idx) {

        System.out.println("-> borrowFromNext(): emprestando chave do irmão DIREITO para filho de índice " + idx + ".");

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        // primeira chave do irmão sobe para o pai,
        // chave do pai desce para o final de child
        child.keys[child.numKeys] = keys[idx];

        if (!child.leaf)
            child.children[child.numKeys + 1] = sibling.children[0];

        keys[idx] = sibling.keys[0];

        // desloca chaves do irmão para esquerda
        for (int i = 1; i < sibling.numKeys; i++)
            sibling.keys[i - 1] = sibling.keys[i];

        // desloca filhos do irmão se não folha
        if (!sibling.leaf)
            for (int i = 1; i <= sibling.numKeys; i++)
                sibling.children[i - 1] = sibling.children[i];

        child.numKeys++;
        sibling.numKeys--;
    }

    /* ============================================================
     *                             MERGE
     * ------------------------------------------------------------
     * Funde children[idx] + chave[idx] + children[idx+1]
     * em um único nó.
     * ============================================================
     */
    void merge(int idx) {

        System.out.println("-> merge(): fundindo filhos de índices " + idx + " e " + (idx + 1) + ".");

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        // puxa chave do pai para o meio do filho esquerdo
        child.keys[t - 1] = keys[idx];

        // copia chaves do irmão direito
        for (int i = 0; i < sibling.numKeys; i++)
            child.keys[i + t] = sibling.keys[i];

        // copia filhos do irmão direito, se não folha
        if (!child.leaf)
            for (int i = 0; i <= sibling.numKeys; i++)
                child.children[i + t] = sibling.children[i];

        // desloca chaves no pai
        for (int i = idx + 1; i < numKeys; i++)
            keys[i - 1] = keys[i];

        // desloca ponteiros de filhos no pai
        for (int i = idx + 2; i <= numKeys; i++)
            children[i - 1] = children[i];

        child.numKeys += sibling.numKeys + 1;
        numKeys--;
    }
}

/* ============================================================
 *                         CLASSE ÁRVORE B
 * ============================================================
 */
public class BTree {

    BTreeNode root;
    final int t;

    public BTree(int t) {
        this.t = t;
        this.root = null;
    }

    public void print() {
        if (root != null) {
            root.print(0);
            System.out.println(
                    "\nBTree (t=" + this.t + ") com " + root.countKeys() + " registro(s)\n"
            );
        } else {
            System.out.println("[Árvore vazia]");
        }
    }

    public BTreeNode search(int k) {
        return (root == null) ? null : root.search(k);
    }

    /* ============================================================
     *                           INSERÇÃO
     * ============================================================
     */
    public void insert(int k) {

        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = k;
            root.numKeys = 1;
            return;
        }

        // evita duplicados
        if (root.search(k) != null)
            return;

        // Se a raiz está cheia, a árvore cresce em altura
        if (root.numKeys == 2 * t - 1) {

            BTreeNode novaRaiz = new BTreeNode(t, false);

            novaRaiz.children[0] = root;
            novaRaiz.splitChild(0, root);

            int i = (novaRaiz.keys[0] < k) ? 1 : 0;
            novaRaiz.children[i].insertNonFull(k);

            root = novaRaiz;
        } else {
            root.insertNonFull(k);
        }
    }

    /* ============================================================
     *                           REMOÇÃO
     * ============================================================
     */
    public void remove(int k) {

        System.out.println("\n========================================");
        System.out.println("           INÍCIO DA REMOÇÃO (" + k + ")");
        System.out.println("========================================");

        if (root == null) {
            System.out.println("A árvore está vazia.");
            System.out.println("=== FIM DA REMOÇÃO (" + k + ") ===\n");
            return;
        }

        root.remove(k);

        if (root.numKeys == 0) {
            if (root.leaf)
                root = null;
            else
                root = root.children[0];
        }

        System.out.println("=== FIM DA REMOÇÃO (" + k + ") ===\n");
    }

    /* ============================================================
     *                               MAIN
     * ============================================================
     */
    public static void main(String[] args) {

        final int ORDEM = 3;
        final int N = 63; // ajuste para o N que o professor pediu

        BTree tree = new BTree(ORDEM);

        // Inserção sequencial: 1, 2, 3, ..., N
        for (int i = 1; i <= N; i++) {
            tree.insert(i);
        }

        System.out.println("Árvore depois da inserção de 1.." + N + ":");
        tree.print();

        Scanner sc = new Scanner(System.in);
        int chave;

        // (Opcional) inserções extras
        do {
            System.out.print("Digite chave a inserir (0 para pular): ");
            while (!sc.hasNextInt()) {
                System.out.print("Entrada inválida. Digite um inteiro: ");
                sc.next();
            }
            chave = sc.nextInt();
            if (chave != 0) {
                tree.insert(chave);
                tree.print();
            }
        } while (chave != 0);

        // Menu de remoção (vamos usar bastante na atividade)
        do {
            System.out.print("Digite chave a remover (0 para sair): ");
            while (!sc.hasNextInt()) {
                System.out.print("Entrada inválida. Digite um inteiro: ");
                sc.next();
            }
            chave = sc.nextInt();
            if (chave != 0) {
                tree.remove(chave);
                tree.print();
            }
        } while (chave != 0);

        sc.close();
    }
}
