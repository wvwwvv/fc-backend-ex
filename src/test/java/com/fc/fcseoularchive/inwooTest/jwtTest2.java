package com.fc.fcseoularchive.inwooTest;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;

@SpringBootTest
public class jwtTest2 {

    @Test
    @DisplayName("토큰 구조 바뀌어서 id 위치 보기")
    public void test1() throws Exception{

        LocalDateTime now = LocalDateTime.now();
        System.out.println("now = " + now);

    }



}
