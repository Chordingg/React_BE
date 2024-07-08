package com.example.demo.security;

import com.example.demo.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Slf4j
public class TokenProvider {

    private static final String SECRET_KEY
            = "12345678901234567890123456789012345678901234567890";

    public String create(UserEntity userEntity){

        // 기한 지금으로부터 1 일로 설정
        Date expiryDate =Date.from(
                Instant.now().plus(1, ChronoUnit.DAYS));

        // JWT Token 생성
        return Jwts.builder()

                // header 에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)

                // payload 에 들어갈 내용
                .setSubject(userEntity.getId()) // sub
                .setIssuer("demo app") // iss
                .setIssuedAt(new Date()) // iat
                .setExpiration(expiryDate) // exp
                .compact();
    }

    public String validateAndGetUserId(String token){

        // parseClaimsJws 메서드가 Base 64 로 디코딩 및 파싱
        // 즉, 헤더와 페이로드를 setSigningKey 로 넘어온 시크릿을 이용해 서명 후, token 의 서명과 비교.
        // 위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외를 날림
        // 그 중 우리는 userId가 필요하므로 getBody 를 부른다.
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();

    }
}
