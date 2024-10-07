package org.example.tutoring.members;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.tutoring.jwt.dto.TokenResponse;
import org.example.tutoring.members.dto.BasicResponse;
import org.example.tutoring.members.dto.JoinRequest;
import org.example.tutoring.members.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<BasicResponse> join(@RequestBody JoinRequest request) {
        return memberService.joinMember(request);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> reAccessToken(HttpServletRequest request) {
        return memberService.accessTokenRequest(request);
    }
}
