package com.example.postgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedContent {
    private String url;
    private String pageTitle;
    private String metaDescription;
    private String mainText;
    private List<String> secondaryPagesVisited;
}
