package br.com.puc.server.search;

import br.com.puc.common.model.Article;
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

    @Override
    public List<Article> search(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        final String lowerCaseQuery = query.toLowerCase();

        return articles.parallelStream()
                .filter(Objects::nonNull)
                .filter(article -> (article.title() != null && article.title().toLowerCase().contains(lowerCaseQuery)) ||
                        (article.abstractText() != null && article.abstractText().toLowerCase().contains(lowerCaseQuery)))
                .collect(Collectors.toList());
    }
}