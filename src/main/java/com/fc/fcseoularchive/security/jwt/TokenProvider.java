//package com.fc.fcseoularchive.security.jwt;
//
//import com.fc.fcseoularchive.entity.User;
//import com.fc.fcseoularchive.error.ApiException;
//import com.fc.fcseoularchive.security.UsernamePasswordAuthenticationToken;
//import com.fc.fcseoularchive.security.web.TokenDto;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.SignatureAlgorithm;
//import lombok.Value;
//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
//import org.springframework.http.HttpStatus;
//
//import java.security.Key;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//public class TokenProvider {
//
//    private static final String AUTHORITIES_KEY = "auth";
//
//    private static final String BEARER_TYPE = "bearer";
//
//    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1000ms * 60 * 60 = 60분
//
//    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7일
//
//    private Key key;
//
//    // application.yml에서 주입받은 secret 값 base64 decode해서 key 변수 할당
//    public TokenProvider(@Value("${jwt.secret}") String secret){
//        byte[] keyBytes = Decoders.BASE64.decode(secret);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    /**
//     * 1. DTO 만들어야함 : TokenDto
//     */
//    // Authentication 객체에 포함되어 있는 권한 정보들을 담은 토큰을 생성
//    public TokenDto generateTokenDto(Authentication authentication){
//        // 권한 가져오기
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//        long now = (new Date()).getTime();
//
//        // Access Token 생성하기
//        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME)
//                .setSubject(authentication.getName())
//                .claim(AUTHORITIES_KEY, authorities)
//                .setExpiration(accessTokenExpiresIn)
//                .signWith(key, SignatureAlgorithm.HS512)
//                .compact();
//
//        // Refresh Token 생성는 좀 나중에
//
//        return TokenDto.builder()
//                .grantType(BEARER_TYPE)
//                .accessToken(accessToken)
//                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
////                .refreshToken(refreshToken)
//                .build();
//    }
//
//    // JWT 토큰을 복호화 해서 정보 꺼내기
//    public Authentication getAuthentication(String accessToken){
//
//        // 토큰 복호화 하기 : JWT의 body
//        Claims claims = parseClaims(accessToken);
//
//        if(claims.get(AUTHORITIES_KEY) == null){
//            throw new ApiException(HttpStatus.UNAUTHORIZED,"401", "UNAUTHORIZED", "권한 정보가 없는 토큰입니다.");
//        }
//
//        // 클레임에서 권한 정보 가져오기
//        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
//                claims.get(AUTHORITIES_KEY).toString().split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//
//        // UserDetails 객체를 만들어서 Auth
//        UserDetails principal = new User(claims.getSubject(), "", authorities);
//        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
//
//    }
//
//    // 토큰 검증하기
//    public boolean validateToken(String token){
//        try{
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
//            log.info("잘못된 JWT 서명입니다.");
//        } catch(ExpriedJwtException e){
//            log.info("만료된 JWT 토큰입니다.");
//        } catch (UnsupportedJwtException e){
//            log.info("지원되지 않는 JWT 토큰입니다.");
//        } catch (IllegalArgumentException e) {
//            log.inf("JWT 토큰이 잘못되었습니다.");
//        }
//        return false;
//    }
//
//    private Claims parseClaims(String accessToken){
//        try{
//            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
//        } catch (ExpiredJwtException e){
//            return e.getClaims();
//        }
//    }
//
//
//}
