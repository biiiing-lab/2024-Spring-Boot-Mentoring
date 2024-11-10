package org.example.tutoring.posts;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tutoring.members.Member;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title; // 제목
    private String content; // 내용

    // 작성자 조인
    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    @CreatedDate
    private LocalDateTime postResisterDate;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
