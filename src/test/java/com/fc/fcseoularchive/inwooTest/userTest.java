package com.fc.fcseoularchive.inwooTest;

import com.fc.fcseoularchive.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@SpringBootTest
public class userTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("현재 시간에서 날짜만 볼수있는지 테스트")
    public void test1() throws Exception{

       LocalDateTime now = LocalDateTime.now();

        DayOfWeek dayOfWeek = now.getDayOfWeek();

        System.out.println("dayOfWeek = " + dayOfWeek);

        int dayOfMonth = now.getDayOfMonth();

        System.out.println("dayOfMonth = " + dayOfMonth);

        LocalDateTime lastLogin = null;

        if( lastLogin == null ){
            System.out.println("처음 로그인?");
        }

    }







}
