package com.onebit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class RedisIdemPotenceCounterUsingJava implements RedisIdemPotenceCounterStrategy {
    @Override
    public boolean incrementIfNotRecently(Jedis jedis, String counterKey, String lastIncrementKey, int timeoutSeconds) {
        long currentTime = System.currentTimeMillis() / 1000;

        while (true) {
            jedis.watch(lastIncrementKey);
            String lastTimeStr = jedis.get(lastIncrementKey);
            long lastTime = (lastTimeStr != null) ? Long.parseLong(lastTimeStr) : 0;

            if (lastTime == 0 || (lastTime + timeoutSeconds) < currentTime) {
                Transaction transaction = jedis.multi();
                transaction.incr(counterKey);
                transaction.set(lastIncrementKey, String.valueOf(currentTime));
                if (transaction.exec() != null) {
                    return true;
                }
               } else {
                jedis.unwatch();
                return false;
            }
        }    }
}
