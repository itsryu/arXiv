package br.com.arxiv.server.search;

import br.com.arxiv.common.model.Article;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileSearchService implements SearchService {
    private final List<Article> articles;

    public FileSearchService(String resourcePath) {
        this.articles = loadArticlesFromResources(resourcePath);
        System.out.printf("[%s] %d artigos carregados com sucesso.%n", resourcePath, articles != null ? articles.size() : 0);
    }

    @Override
    public List<Article> search(String query) {
        if (query == null || query.isBlank() || articles == null) {
            return Collections.emptyList();
        }

        final String lowerCaseQuery = query.toLowerCase();

        return articles.parallelStream()
                .filter(Objects::nonNull)
                .filter(article -> {
                    boolean foundInTitle = article.title() != null && kmpSearch(article.title().toLowerCase(), lowerCaseQuery);
                    boolean foundInAbstract = !foundInTitle && article.abstractText() != null && kmpSearch(article.abstractText().toLowerCase(), lowerCaseQuery);

                    return foundInTitle || foundInAbstract;
                })
                .collect(Collectors.toList());
    }

    private boolean kmpSearch(String text, String pattern) {
        int m = pattern.length();
        int n = text.length();
        if (m == 0) return true;
        if (n == 0) return false;

        int[] lps = computeLPSArray(pattern);
        int i = 0;
        int j = 0;

        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                return true;
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return false;
    }

    private int[] computeLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    private List<Article> loadArticlesFromResources(String resourcePath) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Arquivo de dados não encontrado: " + resourcePath);
                return Collections.emptyList();
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                Type listType = new TypeToken<List<Article>>() {}.getType();
                return new Gson().fromJson(reader, listType);
            }
        } catch (IOException e) {
            System.err.println("Falha ao ler o arquivo de dados: " + resourcePath);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}