package br.com.arxiv;

import br.com.arxiv.client.SearchClient;
import br.com.arxiv.common.model.Article;
import br.com.arxiv.common.model.SearchResponse;
import br.com.arxiv.common.utils.AnsiColors;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    private static String highlightTerm(String text, String term) {
        if (text == null || term == null || term.isBlank()) {
            return text;
        }

        String pattern = "(?i)" + Pattern.quote(term);
        String replacement = AnsiColors.BG_YELLOW + AnsiColors.BLACK + "$0" + AnsiColors.RESET;
        return text.replaceAll(pattern, replacement);
    }

    public static void main(String[] args) {
        SearchClient client = new SearchClient("localhost", 8081);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\n" + AnsiColors.BOLD + AnsiColors.YELLOW + "Digite o termo de busca (ou 'sair' para terminar): " + AnsiColors.RESET);
            String query = scanner.nextLine();

            if ("sair".equalsIgnoreCase(query)) break;
            if (query.isBlank()) continue;

            System.out.print(AnsiColors.FAINT + "Buscando..." + AnsiColors.RESET);
            SearchResponse response = client.search(query);
            System.out.print("\r\u001b[2K");

            if (response != null && !response.results().isEmpty()) {
                System.out.println(AnsiColors.BOLD + AnsiColors.GREEN + "--- " + response.results().size() + " Artigo(s) Encontrado(s) ---" + AnsiColors.RESET);

                for (Article article : response.results()) {
                    System.out.println("\n" + AnsiColors.BOLD + AnsiColors.WHITE + "Título: " + AnsiColors.RESET + highlightTerm(article.title(), query));
                    System.out.println();
                    System.out.println(AnsiColors.FAINT + "Resumo: " + AnsiColors.RESET + highlightTerm(article.abstractText(), query));
                    System.out.println();
                    System.out.println(AnsiColors.BOLD + AnsiColors.CYAN + "Label: " + AnsiColors.RESET + highlightTerm(article.label(), query));
                    System.out.println(AnsiColors.FAINT + "----------------------------------------------------" + AnsiColors.RESET);
                }
            } else if (response != null) {
                System.out.println(AnsiColors.YELLOW + "Nenhum resultado encontrado." + AnsiColors.RESET);
            } else {
                System.out.println(AnsiColors.BOLD + AnsiColors.RED + "Falha ao obter resposta do servidor." + AnsiColors.RESET);
            }
        }

        scanner.close();
        System.out.println(AnsiColors.BOLD + AnsiColors.CYAN + "Cliente encerrado." + AnsiColors.RESET);
    }
}