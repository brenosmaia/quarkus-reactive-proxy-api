package com.brenosmaia.rinha25.config;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RedisConfig {

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    ReactiveRedisDataSource reactiveRedisDataSource;

    public RedisDataSource getRedisDataSource() {
        return redisDataSource;
    }

    public ReactiveRedisDataSource getReactiveRedisDataSource() {
        return reactiveRedisDataSource;
    }
}
