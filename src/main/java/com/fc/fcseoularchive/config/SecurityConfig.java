package com.fc.fcseoularchive.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 패스워드 암호화 관련 메서드 Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    // 시큐리티 대부분 설정을 담당하는 메서드
    // SecurityfilterChain -> 특정 Http 요청에 대해 웹 기반 보안 구성 (인증/인가 및 로그아웃 설정)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        httpSecurity

                /** 스프링 시큐리티 필터 체인에서 CORS 허용 */
                .cors(cors -> {
                })

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
                                                "/api/games/guest",
                                                "/api/players/**",
                                                "/api/rankings/**",
                                                "/upload/**"


//                                        /** 일단.. 불편해서 다 열어주고 개발 운영 시 꼭 지정해주기 ! */
//                                                , "/**"

                                        ).permitAll()

                                        /** POST 전부 열어주는 곳 */
                                        // ex) .requestMatchers(HttpMethod.POST, "/api/users/join").permitAll()


                                        /** 관리자만 가능한 곳! */
                                        .requestMatchers(
                                                "/api/admin/**"
                                        ).hasRole("ADMIN")

                                        /** 위에 없으면 로그인된 회원만 가능! */
                                        .anyRequest().authenticated()
                );

        /** JWT 인증을 위해 직접 구현한 필터 UsernamePasswordAuthticationFilter 전에 실행하기 */
        /*httpSecurity
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);*/

        /** OAuth2 리소스 서버 설정 및 토큰에서 ROLE 꺼내오기 */
        httpSecurity
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));


        return httpSecurity.build();

    }


    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://raichu.inwoohub.com", "https://fc-raichu.vercel.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Keycloak의 Jwt 의 권한을 꺼내서 Authentication 에 올려주기
     */
    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }

    /**
     * 클레임에서 권한을 꺼내서 시큐리티 컨텍스트에 권한 등록
     */
    static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // 클레임에서 "role" 꺼내기
            String role = jwt.getClaim("role");
            if (role == null) {
                // 없다면 바로 반환
                return authorities;
            } else {
                // 있다면 권한 리스트에 추가해주기
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            return authorities;
        }
    }


}