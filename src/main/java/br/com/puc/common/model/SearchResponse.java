package br.com.puc.common.model;

import java.util.List;

public record SearchResponse(List<Article> results) {
}