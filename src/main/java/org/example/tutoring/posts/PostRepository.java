package org.example.tutoring.posts;

import org.example.tutoring.members.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByMember(Member member);
}
