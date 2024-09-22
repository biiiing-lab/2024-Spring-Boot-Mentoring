package org.example.tutoring;

import lombok.RequiredArgsConstructor;
import org.example.tutoring.dto.BasicResponse;
import org.example.tutoring.dto.JoinRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<BasicResponse> joinMember(JoinRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password)) // 24.09.23 : 가입 시 패스워드 암호화
                .build();

        memberRepository.save(member); // 멤버 저장

        return ResponseEntity.ok(new BasicResponse("회원가입 완료"));
    }

}
