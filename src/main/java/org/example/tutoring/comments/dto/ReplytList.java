package org.example.tutoring.comments.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReplytList {
    private String content;
    private String replyMember;
    private LocalDateTime createDate;
}
