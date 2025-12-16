import java.io.BufferedReader; // Lê texto de forma eficiente (com buffer)
import java.io.IOException;    // Trata erros de entrada/saída (arquivo não existe, etc)
import java.nio.charset.StandardCharsets; // Define que vamos ler em UTF-8
import java.nio.file.Files;    // Utilitários modernos para arquivos
import java.nio.file.Path;     // Representa o caminho do arquivo no disco
import java.text.Normalizer;   // Classe para lidar com acentos Unicode
import java.util.regex.Pattern;// Classe para Expressões Regulares (Regex)

public class CarregadorPalavras {

    public static int carregarParaTrie(Path caminhoArquivo, Trie trie) throws IOException {
        int totalInseridas = 0;

        try(BufferedReader br = Files.newBufferedReader(caminhoArquivo, StandardCharsets.UTF_8)){
            String linha;
            while((linha = br.readLine()) != null){
                String palavra = normalizar(linha);
                if(palavra.isEmpty()){
                    continue;
                }
                trie.insert(palavra);
                totalInseridas++;
            }
        }

        return totalInseridas;
    }


    private static String normalizar(String s) {
        if (s == null) {
            return "";
        }

        // 1. Separa os acentos das letras (Ex: 'á' vira 'a' + '´')
        String nfdNormalizedString = Normalizer.normalize(s, Normalizer.Form.NFD);

        // 2. Remove os sinais de acento usando Regex
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String stringSemAcentos = pattern.matcher(nfdNormalizedString).replaceAll("");

        // 3. Joga para minúsculo e garante que só sobrem letras a-z
        // (Isso remove números, espaços e símbolos que sobraram)
        return stringSemAcentos.toLowerCase().replaceAll("[^a-z]", "");
    }
}
