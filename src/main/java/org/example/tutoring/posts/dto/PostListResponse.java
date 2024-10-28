package org.example.tutoring.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {
    private String title;
    private String postMember;
    private LocalDateTime createDate;
}
