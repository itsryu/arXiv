package br.com.arxiv.common.model;

import java.util.List;

public record SearchResponse(List<Article> results) { }