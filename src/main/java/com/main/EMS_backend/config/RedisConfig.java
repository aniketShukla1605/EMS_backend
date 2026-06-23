package com.main.EMS_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {
    @Bean
    public RedisCacheManager cacheManager(
            RedisConnectionFactory factory
    ) {

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(30));

        return RedisCacheManager.builder(factory)

                .withCacheConfiguration(
                        "recommendations",
                        config.entryTtl(Duration.ofMinutes(10))
                )

                .withCacheConfiguration(
                        "dashboardStats",
                        config.entryTtl(Duration.ofMinutes(5))
                )

                .withCacheConfiguration(
                        "filteredEvents",
                        config.entryTtl(Duration.ofMinutes(15))
                )

                .withCacheConfiguration(
                        "eventById",
                        config.entryTtl(Duration.ofHours(1))
                )

                .build();
    }
}
