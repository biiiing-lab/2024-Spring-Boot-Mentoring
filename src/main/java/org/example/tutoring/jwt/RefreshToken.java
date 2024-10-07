package org.example.tutoring.jwt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.tutoring.members.Member;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenNo;
    private String tokenDetail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userNo")
    Member member;
}