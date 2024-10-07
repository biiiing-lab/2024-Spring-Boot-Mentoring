package org.example.tutoring.members;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tutoring.jwt.JwtTokenProvider;
import org.example.tutoring.jwt.dto.TokenResponse;
import org.example.tutoring.members.dto.BasicResponse;
import org.example.tutoring.members.dto.JoinRequest;
import org.example.tutoring.members.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public ResponseEntity<BasicResponse> joinMember(JoinRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();

        if(memberRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponse("중복 회원 있음"));
        } // 24.09.23 : 증복 회원 방지 유효성 검사

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password)) // 24.09.23 : 가입 시 패스워드 암호화
                .build();

        memberRepository.save(member); // 멤버 저장

        return ResponseEntity.ok(new BasicResponse("회원가입 완료"));
    }


    // 첫 로그인 시
    @Transactional
    public ResponseEntity<TokenResponse> login(LoginRequest loginRequest) {
        return tokenProvider.createToken(loginRequest);
    }

    // 재 로그인 시 (어세스 토큰 만료시)
    public ResponseEntity<?> accessTokenRequest(HttpServletRequest request) {
        return tokenProvider.createAccessToken(request);
    }

}
