package br.com.arxiv.server.search;

import br.com.arxiv.common.model.Article;
import java.util.List;

public interface SearchService {
    List<Article> search(String query);
}