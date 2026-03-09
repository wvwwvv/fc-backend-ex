package com.fc.fcseoularchive.inwooTest;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class fileTest {

    @Test
    @DisplayName("시스템 경로 가져오기")
    public void test0() throws Exception{

        String property = System.getProperty("user.dir");

        System.out.println("property = " + property);

    }

    @Test
    @DisplayName("localdatetime 스트링 변환하기")
    public void test01() throws Exception{

       LocalDateTime localDateTime = LocalDateTime.now();

        int year = localDateTime.getYear();

        System.out.println("year = " + year);

    }



}
