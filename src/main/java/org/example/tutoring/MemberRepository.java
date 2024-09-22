package org.example.tutoring;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUsername(String username); // 24.09.23 : 증복 회원 방지 유효성 검사
}
