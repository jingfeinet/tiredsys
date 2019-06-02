package com.sicau.tiredsys.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by zhong  on 2019/4/2 23:36
 */
@Configuration
@EnableCaching //开启缓存
    public class RedisConfig extends CachingConfigurerSupport {

        @Bean
        public CacheManager cacheManager(@SuppressWarnings("rawtypes") RedisConnectionFactory connectionFactory) {
            RedisCacheManager cacheManager =RedisCacheManager.create(connectionFactory);
            // cacheManager.setDefaultExpiration(60);//设置缓存保留时间（seconds）
            return cacheManager;
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
            //设置序列化格式
            return redisTemplate;
        }
    }


