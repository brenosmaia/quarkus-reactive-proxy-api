package com.brenosmaia.rinha25.config;

import io.quarkus.redis.datasource.RedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RedisConfig {

    @Inject
    RedisDataSource redisDataSource;

    public RedisDataSource getRedisDataSource() {
        return redisDataSource;
    }
}
