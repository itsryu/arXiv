package br.com.arxiv.common.model;

import com.google.gson.annotations.SerializedName;

public record Article(
        String title,
        @SerializedName("abstract") String abstractText,
        String label
) {}