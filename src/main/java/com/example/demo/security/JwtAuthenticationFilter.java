package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            //요청에서 토큰 가져오기
            String token = parseBearerToken(request);
            log.info("Filter is running.........token: {}", token);

            //토큰검사하기. JWT이므로 인가 서버에 요청하지 않고도 검증 가능
            if (token != null && !token.equalsIgnoreCase("null")) {
                //userId 가져오기 , 위조된 경우 예외 처리하기
                String userId = tokenProvider.validateAndGetUserId(token);
                log.info("userId: {}", userId);
                //인증완료; SecurityContextHolder에 등록해야 인증된 사용자라고 볼수 있다.

                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, AuthorityUtils.NO_AUTHORITIES);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);

                SecurityContextHolder.setContext(securityContext);
            }
        }catch (Exception ex) {
            log.error("Could not set user authentication in security context",ex);
        }

        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        //Http  요청의 헤더를 파싱해 Bearer 토큰을 리턴한다.
        //"Authorization":"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyYzkxNzA4MTkwODFhMWRmMDE5MDgxYTRlMmIxMDAwMCIsImlzcyI6ImRlbW8gYXBwIiwiaWF0IjoxNzIwMTYyMjkxLCJleHAiOjE3MjAyNDg2OTF9.6foOBCz5Y7Pidw7TDWjCvmSXGuCQ-ysdIT3E4qAJtTXepc7yAAB7HiSglXX336DgRxBpY80ceIFALwI3nm6Ocw"
        String bearerToken = request.getHeader("Authorization");

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
