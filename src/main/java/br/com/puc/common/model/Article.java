package br.com.puc.common.model;

import com.google.gson.annotations.SerializedName;

public record Article(
        String title,
        @SerializedName("abstract") String abstractText,
        String label
) {}