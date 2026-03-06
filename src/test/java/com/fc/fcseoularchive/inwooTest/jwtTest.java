package com.fc.fcseoularchive.inwooTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class jwtTest {

    @Test
    @DisplayName("uuid 하나 아무꺼나 쓰기 jwt secret에 넣을 것")
    public void test1(){
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }

}
