package com.example.CineVerse.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {
    private Long movieId;
    private Integer rating;
    private String comment;
}
