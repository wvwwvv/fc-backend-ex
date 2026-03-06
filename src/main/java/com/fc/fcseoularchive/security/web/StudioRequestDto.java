//package com.fc.fcseoularchive.security.web;
//
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Getter
//@AllArgsConstructor
//@NoArgsConstructor
//public class StudioRequestDto {
//
//    private String email;
//
//    private String password;
//
//    public Studio studio(PasswordEncoder passwordEncoder){
//        return Studio.builder()
//                .email(email)
//                .password(passwordEncoder.encode(password))
//                .authority(Authority.ROLE_STUDIO)
//                .build();
//    }
//
//    public UsernamePasswordAuthenticationToken toAuthentication(){
//        return new UsernamePasswordAuthenticationToken(email, password);
//    }
//
//}
