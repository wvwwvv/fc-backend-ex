/*
package com.fc.fcseoularchive.config.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

// Redis 데이터 접근을 위한 클래스

@Component
public class RedisDao {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> values;

    public RedisDao(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.values = redisTemplate.opsForValue();
    }

    // 기본 데이터 저장
    public void setValues(String key, Object value){
        values.set(key,value);
    }

    // 만료 시간이 있는 데이터 저장
    // 우리는 RefreshToken과, 랭킹 데이터 저장 예정임
    public void setValues(String key, String data, Duration duration){
        values.set(key,data,duration);
    }

    // 데이터 조회
    // RefreshToken , 랭킹 데이터 조회
    public Object getValues(String key){
        return values.get(key);
    }

    // 데이터 삭제
    // 로그아웃 시 RefreshToken을 삭제 예정 시간 지나면 알아서 지워지게도 할까?
    public void deleteValues(String key){
        redisTemplate.delete(key);
    }

}
*/
