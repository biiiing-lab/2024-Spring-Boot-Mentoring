package org.example.tutoring.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PostRequest {
    private String title;
    private String content;
}
