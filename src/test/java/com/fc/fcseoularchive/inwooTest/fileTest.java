//package com.fc.fcseoularchive.inwooTest;
//
//
//import com.fc.fcseoularchive.config.jwt.JwtToken;
//import com.fc.fcseoularchive.config.jwt.JwtTokenProvider;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@SpringBootTest
//public class fileTest {
//
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Test
//    @DisplayName("시스템 경로 가져오기")
//    public void test0() throws Exception{
//
//        String property = System.getProperty("user.dir");
//
//        System.out.println("property = " + property);
//
//    }
//
//    @Test
//    @DisplayName("localdatetime 스트링 변환하기")
//    public void test01() throws Exception{
//
//       LocalDateTime localDateTime = LocalDateTime.now();
//
//        int year = localDateTime.getYear();
//
//        System.out.println("year = " + year);
//
//    }
//
//    @Test
//    @DisplayName("JWT 토큰 userId 가 아닌 id 로 나오는거 테스트")
//    public void test02() throws Exception{
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                "뚱이",
//                null,
//                List.of(new SimpleGrantedAuthority("USER"))
//        );
//
//
//        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
//
//        String refreshToken = jwtToken.getRefreshToken();
//        System.out.println("refreshToken = " + refreshToken);
//
//    }
//
//
//
//}
