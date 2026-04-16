//package com.fc.fcseoularchive.config.jwt;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//
//import java.io.IOException;
//
///**
// * Spring Security에서 UsernamePasswordAuthenticationFilter 이전에서 동작하게끔 앞단에 필터 넣음
// * <p>
// * 이를 통해 미리 만들어준 JwtAuthenticationFilter 로 시큐리티 컨텍스트에 인증 정보 저장하면
// * UsernamePasswordAuthenticationFilter 가 동작 하지않고, 인증된 사용자 상태를 만들 수 있다.
// */
//
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response
//            , FilterChain chain) throws IOException, ServletException {
//
//        // Request Header에서 JWT 토큰 추출하기
//        String accessToken = resolveToken(request);
//
//        // accessToken 유효성 검사하기
//        if (accessToken != null) {
//
//            if (jwtTokenProvider.validateToken(accessToken)) {
//                // 토큰이 유효한 경우, 토큰에서 Authentication 객체를 가지고와서 SecurityContext 저장
//                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
//                // 현재 실행중인 SecurityContext가지고와서 인증 정보 저장
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//        chain.doFilter(request, response);
//    }
//
//
//    // Request 헤더에서 JWT 토큰만 추출하기
//    private String resolveToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // "Bearer " 이후 부터 넘겨주기 그래서 인덱스가 7임
//        }
//        return null;
//    }
//
//}
