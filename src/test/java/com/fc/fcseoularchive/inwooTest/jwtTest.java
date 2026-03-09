package com.fc.fcseoularchive.inwooTest;

import com.fc.fcseoularchive.config.jwt.JwtToken;
import com.fc.fcseoularchive.config.jwt.JwtTokenProvider;
import com.fc.fcseoularchive.config.redis.RedisDao;
import com.fc.fcseoularchive.entity.User;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.user.UserRepository;
import com.fc.fcseoularchive.user.dto.LoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * SecurityContext
 * └─ Authentication
 * ├─ principal -> UserDetail 이 들어감
 * [Username=inwoo, Password=[PROTECTED],
 * Enabled=true, AccountNonExpired=true,
 * CredentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ADMIN]]
 * ├─ credentials
 * └─ authorities
 * 처럼 구조되어있음
 * <p>
 * 시큐리티 컨텍스트는 로컬스레드로 관리되며
 * 하나의 동작이 끝나면 clear 후 스레드 반환하는 방식으로 동작함
 * <p>
 * Authenticatin 을 만들기 위해 생성하랴고 보니
 * principal, credential,  + authorities (이건 추가 넣고싶은거 ex: ROLE_USER, ROLE_ADMIN 등)
 * principal : 누구? (id)
 * credentials : 뭐로 인증? (비밀번호)
 * authorities : 뭐 할수있음? (동작
 */


@Testcontainers
@SpringBootTest
public class jwtTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Autowired
    private RedisDao redisDao;

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisPropertie(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }


    @Test
    @DisplayName("uuid 하나 아무꺼나 쓰기 jwt secret에 넣을 것")
    public void test1() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    public void test2() {

        // 토큰 생성
        Date date = new Date(System.currentTimeMillis() + 36000);
        String accessToken = jwtTokenProvider.generateAccessToken("inwoo", "ADMIN", date);
        System.out.println(accessToken);
    }


    @Test
    @DisplayName("토큰 Access, Refresh 생성 테스트")
    void test3() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "inwoo",
                null,
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        System.out.println(jwtToken.getAccessToken());
        System.out.println(jwtToken.getRefreshToken());

        String userNameFromToken = jwtTokenProvider.getUserNameFromToken(jwtToken.getAccessToken());
        System.out.println(userNameFromToken);

        System.out.println("jwtToken.getGrantType() = " + jwtToken.getGrantType());

    }

    @Test
    @DisplayName("토큰 복호화")
    public void test4() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "inwoo",
                null,
                List.of(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("메롱!"))
        );

        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        String accessToken = jwtToken.getAccessToken();

        Authentication authentication1 = jwtTokenProvider.getAuthentication(accessToken); // 복호화

        Assertions.assertEquals(
                "inwoo",
                ((UserDetails) authentication1.getPrincipal()).getUsername()
        );

        Collection<? extends GrantedAuthority> authorities = ((UserDetails) authentication1.getPrincipal()).getAuthorities();
        for (GrantedAuthority authority : authorities) {
            System.out.println("authority = " + authority);
        }


    }

    @Test
    @DisplayName("리프레시 토큰 삭제")
    public void test5() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "inwoo",
                null,
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        jwtTokenProvider.deleteRefreshToken(jwtTokenProvider.getUserNameFromToken(jwtToken.getRefreshToken())); // 토큰 삭제

        jwtTokenProvider.validateRefreshToken(jwtTokenProvider.getUserNameFromToken(jwtToken.getRefreshToken())); // 오류 예상?

    }


    @Test
    @DisplayName("메롱 토큰 확인")
    public void test6() throws Exception {
        Date date = new Date(System.currentTimeMillis() + 36000);

//        String s = jwtTokenProvider.generateAccessTokenTest("inwoo", "메롱", "ADMIN", date);

//        System.out.println("s = " + s);

        // 토큰 복호화
//        Authentication authentication = jwtTokenProvider.getAuthenticationTest(s);
//        System.out.println(authentication.getPrincipal());

    }

    @Test
    @DisplayName("레디스 토큰 Key에 태그 붙었나 테스트")
    public void test7() throws Exception {

        Duration duration = Duration.ofMillis(1000 * 60 * 60);// 1시간

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "inwoo",
                null,
                List.of(new SimpleGrantedAuthority("ADMIN"))
        );

        // -> 여기서 이미 레디스에 값 저장 되어있음!
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 이렇게 하면 못가져와 근데? 어떻게 가져와??
        // 어? 왜 또 못가져와 이상하다..
        Object inwoo = redisDao.getValues("inwoo");
        System.out.println("이건 테스트 코드 value = " + inwoo);

        Assertions.assertNotEquals(jwtToken.getRefreshToken(), inwoo);
    }

    @Test
    @DisplayName("레디스 토큰 생성, 검증, 삭제 테스트")
    public void test8() throws Exception {

        Authentication authenticaton
                = new UsernamePasswordAuthenticationToken("메롱", "ADMIN", List.of(new SimpleGrantedAuthority("ADMIN")));

        JwtToken jwtToken = jwtTokenProvider.generateToken(authenticaton);

        String accessToken = jwtToken.getAccessToken();


        Assertions.assertThrows(AuthenticationException.class, () ->
                jwtTokenProvider.validateRefreshToken(accessToken + "ㅎㅎ"));

    }

    @Test
    @DisplayName("Authentication 에서 정보 꺼내보기")
    public void test9() throws Exception {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "인우",
                null,// 비밀번호 설정 x
                List.of(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("헤헤"))
        );

        String string = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Assertions.assertEquals("ADMIN,헤헤", string);
    }

    @Test
    @DisplayName("byte[] 로 키 만들기")
    public void test10() throws Exception {

        String secretKey = UUID.randomUUID().toString(); // 키 값
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8); // 문자열 키 -> 바이트 배열로 변환
        // Jwt 서명용 키는 문자열 x -> 바이트 기반 키를 다룸
        SecretKey key = Keys.hmacShaKeyFor(keyBytes); // 서명용 키 만드는 용도 Type : SecretKey 임
        // -> SecretKey 로 jwt 토큰 만들 때 사용 가능함.

        // 토큰 생성 테스트
        String jwtToken = Jwts.builder()
                .setSubject("inwoo")
                .claim("ROLE", "ADMIN") // 추가해주기
                .claim("메롱", "메롱") // 추가해주기
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        System.out.println("jwtToken = " + jwtToken);

        // 토큰 파싱 테스트
        Claims payload = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getPayload();

        System.out.println("payload = " + payload);
        // payload = {sub=inwoo, exp=1772949160} 처럼 들어감
        // 그럼 payload 의 role 도 넣고 싶다면? 생성시 함께 넣기

        // 페이로드에 있는 subject 꺼내기
        String who = payload.getSubject();
        System.out.println("who = " + who);

        // 페이로드에 있는 claim - role 꺼내기
        String role = payload.get("ROLE", String.class);
        System.out.println("role = " + role);

        // 페이로드에 있는 claim - 메롱 꺼내기
        String 메롱 = payload.get("메롱", String.class);
        System.out.println("메롱 = " + 메롱);

    }


    @Test
    @DisplayName("vaildateToken 테스트")
    public void test11() throws Exception {

        // 실제 토큰 값
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsiqTtjoDsp4DrsKUiLCJleHAiOjE3NzM1NTk1NzR9.IJGecob31QczxnueNrjEWjGICPbxJmZ1eo_S_NzKtQ8";


        Assertions.assertEquals(true, jwtTokenProvider.validateRefreshToken(token));


    }

    @Test
    @DisplayName("redis 에 저장된 토큰 분해 테트")
    public void test12() throws Exception {

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsiqTtjoDsp4DrsKUiLCJleHAiOjE3NzM1NTk1NzR9.IJGecob31QczxnueNrjEWjGICPbxJmZ1eo_S_NzKtQ8";

        String secretKey = "CD9KEjLo1DkQB6EMWo8RZPl2i8XAxv4RLVNAC3dmdVf";
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

    }

    @Test
    @DisplayName("refreshToken 에서 반환 null 로 되는 오류 테스트")
    public void test13() throws Exception {


        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsiqTtjoDsp4DrsKUiLCJleHAiOjE3NzM1NjIwMjl9.zZdSTYEYHKJ2i8EXJ-F-6dw3u5IYu0EhkB86okOiUrM";

        // 리프레시 토큰 검증 (오류발생 -> validateToken 에서 터짐() )
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "401", "UNAUTHORIZED", "만료된 토큰 입니다.");
        }

        // 사용자 정보 꺼내기 (subject)
        String userId = jwtTokenProvider.getUserNameFromToken(refreshToken);

        // 리프레시 토큰 (Redis에서 삭제)
        jwtTokenProvider.deleteRefreshToken(refreshToken);

        // 유저 정보 db에서 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 회원입니다."));

        // 새로운 인증서 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserId(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().toString()))
        );

        // 토큰 재발급
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        LoginResponse loginResponse = new LoginResponse(jwtToken, user);

        System.out.println("loginResponse 의 담긴 유저 아이디 = " + loginResponse.getUserId());

    }

    @Test
    @DisplayName("토큰 복호화 해서 유저 role 꺼내보기")
    public void test14() throws Exception {

        String secretKey = "CD9KEjLo1DkQB6EMWo8RZPl2i8XAxv4RLVNAC3dmdVf";
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnbjsmrAiLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3NzI5NjIyODJ9.RUaVg0iicE9IrKIn5SI-DK_xyuBdiaGttcMGWcarF90";

        Claims payload = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = payload.get("role", String.class);
        System.out.println("role = " + role);


    }

    @Test
    @DisplayName("관리자 토큰 검증 테스트")
    public void test15() throws Exception {

        String secretKey = "CD9KEjLo1DkQB6EMWo8RZPl2i8XAxv4RLVNAC3dmdVf";
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnbjsmrAiLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3NzI5NjMxNjV9.4fGf8Bg7LfwD6bxzWV1N2BbuM_f_95CDVaUB3YpuGGs";

        Claims payload = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = payload.get("role", String.class);

        Assertions.assertEquals("ADMIN", role);
    }
    
    @Test
    @DisplayName("토큰을 통해 인증 정보 만드는 곳 테스트")
    public void test16() throws Exception{

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnbjsmrAiLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3NzI5NjMxNjV9.4fGf8Bg7LfwD6bxzWV1N2BbuM_f_95CDVaUB3YpuGGs";

        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        List<String> list = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Assertions.assertTrue(list.contains("ADMIN"));

    }
    
    @Test
    @DisplayName("SecurityContext 확인해보기")
    public void test17() throws Exception{

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsnbjsmrAiLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3NzI5NjMxNjV9.4fGf8Bg7LfwD6bxzWV1N2BbuM_f_95CDVaUB3YpuGGs";

        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Authentication securityAuthentication = SecurityContextHolder.getContext().getAuthentication();

        List<String> list = securityAuthentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Assertions.assertTrue(list.contains("ADMIN"));

    }
       


}
