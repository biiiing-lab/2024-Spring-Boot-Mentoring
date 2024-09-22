package org.example.tutoring;

import lombok.RequiredArgsConstructor;
import org.example.tutoring.dto.BasicResponse;
import org.example.tutoring.dto.JoinRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public ResponseEntity<BasicResponse> joinMember(JoinRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        Member member = Member.builder()
                .username(username)
                .password(password)
                .build();

        memberRepository.save(member); // 멤버 저장

        return ResponseEntity.ok(new BasicResponse("회원가입 완료"));
    }

}
