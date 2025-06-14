package br.com.puc.server.search;

import br.com.puc.common.model.Article;
import java.util.List;

public interface SearchService {
    List<Article> search(String query);
}