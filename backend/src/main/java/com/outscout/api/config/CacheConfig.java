package com.outscout.api.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Redis imports (present in classpath when spring-boot-starter-data-redis is included)
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class CacheConfig {

    // Declare the logical cache names you plan to use
    private static final List<String> DEFAULT_CACHES = List.of("spots", "forecasts");

    /**
     * Only create RedisTemplate when Redis is enabled.
     */
    @Bean
    @ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
    @ConditionalOnClass(RedisConnectionFactory.class)
    public RedisTemplate<String, Object> redisTemplate(ObjectProvider<RedisConnectionFactory> factoryProvider) {
        RedisConnectionFactory factory = factoryProvider.getIfAvailable();
        if (factory == null) return null; // will fallback to in-memory cache manager

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis-backed CacheManager when app.redis.enabled=true and a RedisConnectionFactory is available.
     * Default TTL = 10 minutes; "forecasts" = 2 hours.
     */
    @Bean
    @ConditionalOnProperty(prefix = "app.redis", name = "enabled", havingValue = "true")
    @ConditionalOnClass(RedisConnectionFactory.class)
    public CacheManager redisCacheManager(ObjectProvider<RedisConnectionFactory> factoryProvider) {
        RedisConnectionFactory factory = factoryProvider.getIfAvailable();
        if (factory == null) {
            // No factory -> fall back to in-memory cache manager
            ConcurrentMapCacheManager fallback = new ConcurrentMapCacheManager();
            fallback.setCacheNames(DEFAULT_CACHES);
            return fallback;
        }

        RedisCacheConfiguration defaults = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues();

        // Per-cache TTL overrides
        RedisCacheConfiguration forecastsCfg = defaults.entryTtl(Duration.ofHours(2));

        // Initial cache names
        Set<String> initial = new HashSet<>(DEFAULT_CACHES);

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaults)
                .initialCacheNames(initial)
                .withCacheConfiguration("forecasts", forecastsCfg)
                .build();
    }

    /**
     * In-memory CacheManager when Redis is disabled or missing.
     */
    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager inMemoryCacheManager() {
        ConcurrentMapCacheManager cm = new ConcurrentMapCacheManager();
        cm.setCacheNames(DEFAULT_CACHES);
        return cm;
    }
}
