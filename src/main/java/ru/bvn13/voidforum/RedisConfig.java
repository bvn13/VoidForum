package ru.bvn13.voidforum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.embedded.Redis;
import redis.embedded.RedisServer;
import redis.embedded.exceptions.EmbeddedRedisException;

import java.util.List;

@Configuration
public class RedisConfig {

    public static class RedisDummy implements Redis {

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void start() throws EmbeddedRedisException {

        }

        @Override
        public void stop() throws EmbeddedRedisException {

        }

        @Override
        public List<Integer> ports() {
            return null;
        }
    }

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.embedded}")
    private Boolean useEmbeddedRedis;




    @Bean(name = "RedisServer")
    public Redis redisServer() {
        if (!this.useEmbeddedRedis) {
            return new RedisDummy();
        }
        RedisServer.builder().reset();

        return RedisServer.builder()
                .port(this.port)
                .build();
    }

}
