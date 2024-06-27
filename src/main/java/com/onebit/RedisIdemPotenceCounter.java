package com.onebit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
public class RedisIdemPotenceCounter {

    public static void main(String[] args) throws InterruptedException {

        // Usar un pool de conexiones a Redis
        JedisPool pool = new JedisPool("localhost", 6379);
        String counterKey = "idempotentCounter";
        String lastIncrementKey = "lastIncrementTime";
        int timeoutSeconds = 60;

        incrementCounter(pool, counterKey, lastIncrementKey, timeoutSeconds);

        // Cerrar el pool
        pool.close();
    }

    static void incrementCounter(JedisPool jedisPool, String counterKey, String lastIncrementKey, int timeoutSeconds) throws InterruptedException {
        try (Jedis jedis = jedisPool.getResource()) {
            ContextRedisIdemPotenceCounter contextRedisIdempotentCounter = new ContextRedisIdemPotenceCounter(new RedisIdemPotenceCounterUsingLua());

            // Ejecutar el script Lua
            log.info("Executing increment counter with script Lua");
            boolean isSuccess = contextRedisIdempotentCounter.incrementCounter(jedis, counterKey, lastIncrementKey, timeoutSeconds);

            checkSuccess(isSuccess);

            contextRedisIdempotentCounter = new ContextRedisIdemPotenceCounter(new RedisIdemPotenceCounterUsingJava());

            Thread.sleep(61000);

            log.info("Executing increment counter with Java algorithm");
            isSuccess = contextRedisIdempotentCounter.incrementCounter(jedis, counterKey, lastIncrementKey, timeoutSeconds);

            checkSuccess(isSuccess);
        } catch (InterruptedException e) {
            log.error("Error while waiting for 61 seconds to complete timer");
            Thread.currentThread().interrupt();
        }
    }

    private static void checkSuccess(boolean isSuccess) {
        if (isSuccess) {
            log.info("Counter incremented.");
        } else {
            log.info("Counter not incremented due to restriction time.");
        }
    }
}
