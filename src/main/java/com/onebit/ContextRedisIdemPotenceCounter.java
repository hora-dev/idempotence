package com.onebit;

import redis.clients.jedis.Jedis;

public class ContextRedisIdemPotenceCounter {
    private RedisIdemPotenceCounterStrategy redisIdempotentCounterStrategy;

    public ContextRedisIdemPotenceCounter(RedisIdemPotenceCounterStrategy redisIdempotentCounterStrategy) {
        this.redisIdempotentCounterStrategy = redisIdempotentCounterStrategy;
    }

    public void setRedisImpotentCounter(RedisIdemPotenceCounterStrategy redisImpotentStrategy){
        this.redisIdempotentCounterStrategy = redisImpotentStrategy;
    }
    public boolean incrementCounter(Jedis jedis, String counterKey, String lastIncrementKey, int timeoutSeconds) {
        return redisIdempotentCounterStrategy.incrementIfNotRecently(jedis, counterKey, lastIncrementKey, timeoutSeconds);
    }
}
