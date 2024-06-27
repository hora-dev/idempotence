package com.onebit;

import redis.clients.jedis.Jedis;

public interface RedisIdemPotenceCounterStrategy {
    boolean incrementIfNotRecently(Jedis jedis, String counterKey, String lastIncrementKey, int timeoutSeconds);
}
