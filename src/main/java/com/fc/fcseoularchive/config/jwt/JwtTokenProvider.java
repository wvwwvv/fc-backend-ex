package com.fc.fcseoularchive.config.jwt;

import ch.qos.logback.core.status.ErrorStatus;
import com.fc.fcseoularchive.config.redis.RedisDao;
import com.fc.fcseoularchive.error.ApiException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import io.jsonwebtoken.security.Keys; // JJWT 라이브러리의 유틸리티 클래스임 (JWT서명용 key 객체 만들어줌)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final RedisDao redisDao;

    private static final String GRANT_TYPE = "Bearer";

    private static final String TOKEN_PREFIX = "RefreshToken:"; // 토큰 태그 달아주기

    @Value("${jwt.access-token.expire-time}") // 1시간으로 설정
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token.expire-time}") // 7일으로 설정
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, RedisDao redisDao) {

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.redisDao = redisDao;
    }

    // User 정보 가지고 accessToken, RefreshToken 생성하기
    public JwtToken generateToken(Authentication authentication) {
        // 권한 가져오기
        // JWT 토큰의 claims에 포함되어 사용자의 권한 정보를 저장하는데 사용됨
        String authorities = authentication.getAuthorities().stream() // Authentication 객체에서 사용자 권한 목록 가지고오기
                .map(GrantedAuthority::getAuthority) // 각 GrantedAuthority 객체에서 권한 문자열만 추출하기
                .collect(Collectors.joining(",")); // 추출한 권한 문자열들을 쉼표로 구분하여 하나의 문자열로 결합

        long now = (new Date()).getTime();
        String username = authentication.getName();

        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER");

        // AccessToken 생성하기
        Date accessTokenExpire = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = generateAccessToken(username, userRole, accessTokenExpire);


        // RefreshToken 생성하기
        Date refreshTokenExpire = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = generateRefreshToken(username, refreshTokenExpire);

        // Redis에 RefreshToken 넣기
        // "REFRESH_TOKEN_EXPIRE_TIME"만큼 시간이 지나면 삭제됨
        redisDao.setValues(TOKEN_PREFIX + username, refreshToken, Duration.ofMillis(REFRESH_TOKEN_EXPIRE_TIME));

        return JwtToken.builder().grantType(GRANT_TYPE) // "Bearer" 임!
                .accessToken(accessToken).refreshToken(refreshToken).build();
    }


    // 토큰 생성자
    public String generateAccessToken(String username, String userRole, Date expireDate) {
        return Jwts.builder()
                .setSubject(username) // 토큰 제목 (username) < Subject에
                .claim("role", userRole) // userRole 넣기 ("ADMIN", "USER") 넣을 예정
                .setExpiration(expireDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 지정돤 키와, 알고리즘 으로 서명
                .compact(); // 최종 JWT 문자열 생성 (header.payload.signature 구조임!)
    }

    // 리프레시 토큰 생성자
    private String generateRefreshToken(String username, Date expireDate) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    // JWT 토큰 복호화하여 토큰에 들어있는 정보 꺼내기
    public Authentication getAuthentication(String accessToken) {
        // JWT 토큰 복호화
        Claims claims = parseClaims(accessToken);
        if (claims.get("role") == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED,"401","UNAUTHORIZED","권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("role").toString().split(","))
//                .map(SimpleGrantedAuthority::new) // SimpleGrantedAuthority 객체들의 컬렉션으로 변환
                .map( s -> new SimpleGrantedAuthority("ROLE_"+s))
                .toList();

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 클래스
        UserDetails principal = new User(claims.getSubject(), "", authorities); // 파라미터: 사용자 식별자, credentials, 권한 목록
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);


    }

    // JWT 토큰 복호화
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰 정보 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("유효하지 않은 JWT 토큰입니다.", e);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 JWT 토큰입니다.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT 문자열이 비어 있거나 잘못되었습니다.", e);
        }
        return false;
    }


    // RefreshToken 검증
    public boolean validateRefreshToken(String token) {
        // 기본적인 JWT 검증
        if (!validateToken(token)) return false;

        try {
            // token에서 username 추출하기
            String username = getUserNameFromToken(token);
            // Redis에 저장된 RefreshToken과 비교하기
            String redisToken = (String) redisDao.getValues(TOKEN_PREFIX + username);

            return token.equals(redisToken);
        } catch (Exception e) {
            log.info("리프레시 토큰 검증에 실패했습니다.", e);
            return false;
        }
    }

    // 토큰에서 username 추출
    public String getUserNameFromToken(String token) {
        try {
            // 토큰 파싱해서 클레임 얻기
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 사용자 이름(subject) 반환 -> subject에 이름을 넣어뒀었음
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되어도 클레임 내용을 가져올 수 있음
            return e.getClaims().getSubject();
        }
    }

    // RefreshToken 삭제
    public void deleteRefreshToken(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "500", "INTERNAL_SERVER_ERROR", "리프레시 토큰 삭제를 위한 username이 비어 있습니다.");
        }

        // 로그아웃 시 Redis에서 RefreshToken 삭제
        redisDao.deleteValues(TOKEN_PREFIX + username);
    }


}

/**
 * jwt 는 크게 Header.payload.Signature 3 단계다.
 * <p>
 * 1: Header
 * -> 토큰의 메타 정보다 {"alg" : "HS256" , "typ":"JWT"} 처럼!
 * <p>
 * 2: payload
 * -> 토큰 내부 정보다!
 * .setSubject(username)은 누구껀지!
 * * 그렇게 하면 payload 안에서 보통 sub 라는 이름으로 들어간다.
 * * ex ) { "sub" : "username" } 처럼
 * <p>
 * . claim(..) 이거는 쉽게 생각하면 커스텀이다.
 * * 필요한 정보들을 여기서 추가하면 된다.
 * * 그러면 또
 * * ex) { "auth" : "authorities", "userRole" : "userRole" } 처럼 payload에 들어간다.
 * <p>
 * 3: Signature
 * -> 위조 방지 서명이다.
 * HMACSHA256{ base64UrlEncode(header) + "." + bas64UrlEncode(payload
 * , secretKey} 처럼 되었이다.
 * 즉, 서버는 같은 secret key로 다시 계산 해보고 서명이 같으면, 정상, 다르면 위조로 판단한다.
 * <p>
 * 그럼? Header 가 있는데 Signature 가 왜 필요하나..?
 * payload 는 그냥 인코딩한 Signature를 통해서 진짜인지 아닌지 확인하는 것이다.
 * <p>
 * 정리하자면, Header는 이 토큰이 어떤 방식으로 만들어 졌는지 알려주고,
 * 시그니처는 이 토큰이 중간에 안 바뀌고, 진짜 서버가 만든게 맞는지 검증하는 장치다.
 * <p>
 * 1. Header JSON 생성
 * 2. Payload JSON 생성
 * 3. Header를 base64Url 인코딩
 * 4. Payload를 base64Url 인코딩
 * 5. 인코딩된 Header + "." + 인코딩된 Payload 를 만든다
 * 6. 그 값을 secretKey 와 알고리즘(예: HS256)으로 서명해서 Signature를 만든다
 * 7. 최종적으로
 * 인코딩된 Header + "." + 인코딩된 Payload + "." + 인코딩된 Signature
 * 를 붙인 것이 JWT 토큰이다. 근데 이건 내가 하는게 아니라 Jwts 라이브러리에서 해주고 String 으로 반환해주기 때문에
 * 나는 가져다만 쓴다.
 */