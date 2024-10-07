package org.example.tutoring.members.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JoinRequest {
    private String username;
    private String password;
}
