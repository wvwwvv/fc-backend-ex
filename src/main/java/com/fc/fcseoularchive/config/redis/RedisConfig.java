package com.fc.fcseoularchive.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableRedisRepositories
@EnableCaching
public class RedisConfig {

    // 환경변수 에서 host, port 값 주입하기
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;



    // Redis 연결 팩토리 설정하기
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);

        // Lettuce 라이브러리 사용해서 Redis 연결하기
        // Lettuce는 Jedis 보다 성능이 좋고, 비동기 처리가 가능하다.
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }



    // RedisTemplate 설정하기
    // RedisTemplate은 DB 서버에 Set, Get, Delete 등을 사용할 수 있음
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        // RedisTemplate는 트랜잭션을 지원함.
        // 트랜잭션 안에서 오류가 발생한다면 -> 그 작업 모두 취소 가능

        // Redis와 통신할 때 사용할 템플릿 설정하기
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // key, value에 대한 직렬화 방법 설정하기
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // hash key, hash value에 대한 직렬화 방법 설정하기
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;

    }

    // 캐시 데이터를 어떻게 직렬화해서 Redis 에 넣을지
    // 각 캐시 이름(value) 별로 TTL 제어
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDateTime 해결 - 시간/날짜 타입도 직렬/역직렬 가능
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 "2026-03-15T.." 형태로 저장

        // 캐시에서 객체를 꺼낼 때 원래 타입으로 정확히 복원
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();
        objectMapper.activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);

        // ObjectMapper 를 Redis 직렬화기에 장착
        GenericJackson2JsonRedisSerializer customSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);


        // 기본 설정 : Json 형태로 저장 되도록 세팅
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(customSerializer));

        // 캐시 이름별 TTL 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 직관왕 랭킹 attendanceRank 캐시 : 1분 뒤 만료
        cacheConfigurations.put("attendanceRank", defaultConfig.entryTtl(Duration.ofMinutes(1)));

        // 승률왕 랭킹 winRateRank 캐시 : 1분 뒤 만료
        cacheConfigurations.put("winRateRank", defaultConfig.entryTtl(Duration.ofMinutes(1)));

        // 게스트용 경기 일정 조회 guestGames 캐시 : 1시간 뒤 만료
        cacheConfigurations.put("guestGames", defaultConfig.entryTtl(Duration.ofHours(1)));

        // 인우추가함.

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();

    }

}

