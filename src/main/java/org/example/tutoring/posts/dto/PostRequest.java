package org.example.tutoring.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// 게시글 요청
@Setter
@Getter
@AllArgsConstructor
public class PostRequest {
    private String title;
    private String content;
}
