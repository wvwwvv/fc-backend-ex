package com.fc.fcseoularchive.config;

import com.fc.fcseoularchive.config.jwt.JwtAuthenticationFilter;
import com.fc.fcseoularchive.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    // 패스워드 암호화 관련 메서드 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    // 시큐리티 대부분 설정을 담당하는 메서드
    // SecurityfilterChain -> 특정 Http 요청에 대해 웹 기반 보안 구성 (인증/인가 및 로그아웃 설정)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity

                /** 스프링 시큐리티 필터 체인에서 CORS 허용 */
                .cors(cors -> {})

                /** REST API -> basic auth 및 csrf 보안을 사용안함 */
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                /** JWT 사용해서 세션 사용안함 (Redis는>..?) */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // stateless 선언

                /** JWT 사용해서 폼 로그인, 로그아웃 사용안함 */
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)


                /** http request 인증 설정 중요함 !! (API 만들 때마다 수정해 줘야하나 😬) */
                .authorizeHttpRequests(authorize ->
                        authorize
                                /** 다 열어주는 곳 !! */
                                .requestMatchers(
                                        "/swagger-ui/**", // Swagger
                                        "/v3/api-docs/**", // Swagger
                                        "/error/**", // Error Test
                                        "/api/users/join",
                                        "/api/users/login",
                                        "/api/users/refresh",


                                        /** 일단.. 불편해서 다 열어주고 개발 운영 시 꼭 지정해주기 ! */
                                        "/**"

                                ).permitAll()

                                /** POST 전부 열어주는 곳 */
                                // ex) .requestMatchers(HttpMethod.POST, "/api/users/join").permitAll()


                                /** 관리자만 가능한 곳! */
//                                .requestMatchers(
//                                        "/api/admin/**",
//                                        "/api/**/admin/**"
//                                ).hasRole("ADMIN")

                                /** 위에 없으면 로그인된 회원만 가능! */
//                                .anyRequest().authenticated()
                );

        /** JWT 인증을 위해 직접 구현한 필터 UsernamePasswordAuthticationFilter 전에 실행하기 */
        httpSecurity
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }


}