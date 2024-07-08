package com.example.demo.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String id; // 유저에게 고유하게 부여되는 id.

    private String username; // 아이디로 사용할 유저네임. 이메일일 수도 그냥 문자열일 수도 있다.

    private String password; // 패스워드.

    private String token;


}
