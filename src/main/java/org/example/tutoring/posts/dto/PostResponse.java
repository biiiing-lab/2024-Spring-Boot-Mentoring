package org.example.tutoring.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PostResponse {
    private String title;
    private String content;
    private String postMember;
    private LocalDateTime createDate;
}
