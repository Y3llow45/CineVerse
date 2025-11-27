package com.example.CineVerse.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MovieDTO {
    private Long id;
    private String title;
    private List<String> genres;
    private Integer year;
    private String description;
    private String imageUrl;
    private Integer likes;
    private Double rating;
}
