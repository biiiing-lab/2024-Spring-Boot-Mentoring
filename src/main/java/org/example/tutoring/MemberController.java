package org.example.tutoring;

import lombok.RequiredArgsConstructor;
import org.example.tutoring.dto.BasicResponse;
import org.example.tutoring.dto.JoinRequest;
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
}
