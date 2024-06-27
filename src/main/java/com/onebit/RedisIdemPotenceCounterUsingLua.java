package com.onebit;

import redis.clients.jedis.Jedis;

public class RedisIdemPotenceCounterUsingLua implements RedisIdemPotenceCounterStrategy {

    @Override
    public boolean incrementIfNotRecently(Jedis jedis, String counterKey, String lastIncrementKey, int timeoutSeconds) {

        // Script Lua para incrementar el contador solo si no ha sido incrementado en los últimos timeoutSeconds
        String luaScript =
                "local lastTime = redis.call('GET', KEYS[2]) " +
                        "if lastTime == false or (tonumber(lastTime) + tonumber(ARGV[1])) < tonumber(ARGV[2]) then " +
                        "  redis.call('INCR', KEYS[1]) " +
                        "  redis.call('SET', KEYS[2], ARGV[2]) " +
                        "  return 1 " +
                        "else " +
                        "  return 0 " +
                        "end";

        long currentTime = System.currentTimeMillis() / 1000;
        Object result = jedis.eval(luaScript, 2, counterKey, lastIncrementKey, String.valueOf(timeoutSeconds), String.valueOf(currentTime));
        return result.equals(1L);
    }
}
