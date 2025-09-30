package com.example.api_gateway.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private StringRedisSerializer keySerializer = new StringRedisSerializer();
    private GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

    public void set(int dbIndex, String key, Object value, Duration duration) {

        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();

        LettuceConnectionFactory tempFactory = new LettuceConnectionFactory(connectionFactory.getHostName(), connectionFactory.getPort());
        tempFactory.setDatabase(dbIndex);
        tempFactory.afterPropertiesSet();

        RedisConnection connection = tempFactory.getConnection();
        try {
            if (duration == null || duration.isZero() || duration.isNegative()) {

                // Süresiz
                connection.set(
                        keySerializer.serialize(key),
                        valueSerializer.serialize(value)
                );

            } else {
                // TTL’li
                connection.setEx(
                        keySerializer.serialize(key),
                        duration.getSeconds(),
                        valueSerializer.serialize(value)
                );

            }
        } finally {

            connection.close();
            tempFactory.destroy();

        }

    }

    @SuppressWarnings("unchecked")
    public <T> T get(int dbIndex, String key, Class<T> clazz) {

        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        LettuceConnectionFactory tempFactory = new LettuceConnectionFactory(connectionFactory.getHostName(), connectionFactory.getPort());

        tempFactory.setDatabase(dbIndex);
        tempFactory.afterPropertiesSet();

        RedisConnection connection = tempFactory.getConnection();
        byte[] valueBytes = connection.get(keySerializer.serialize(key));
        connection.close();
        tempFactory.destroy();

        if (valueBytes != null) {
            Object deserialized = valueSerializer.deserialize(valueBytes);
            return (T) deserialized;
        }

        return null;
    }

    public void delete(int dbIndex, String key) {

        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        LettuceConnectionFactory tempFactory = new LettuceConnectionFactory(connectionFactory.getHostName(), connectionFactory.getPort());
        tempFactory.setDatabase(dbIndex);
        tempFactory.afterPropertiesSet();
        RedisConnection connection = tempFactory.getConnection();
        connection.del(keySerializer.serialize(key));
        connection.close();
        tempFactory.destroy();

    }
}
