package com.onebit;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ContextRedisIdemPotenceCounterTest {
    private Jedis jedis;
    private RedisIdemPotenceCounterStrategy strategy;
    private ContextRedisIdemPotenceCounter context;

    @Before
    public void setUp() {
        jedis = mock(Jedis.class);
        strategy = mock(RedisIdemPotenceCounterStrategy.class);
        context = new ContextRedisIdemPotenceCounter(strategy);
    }

    @Test
    public void testIncrementCounter_Success() {
        when(strategy.incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60)).thenReturn(true);

        boolean result = context.incrementCounter(jedis, "counterKey", "lastIncrementKey", 60);

        assertTrue(result);
        verify(strategy, times(1)).incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60);
    }

    @Test
    public void testIncrementCounter_Failure() {
        when(strategy.incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60)).thenReturn(false);

        boolean result = context.incrementCounter(jedis, "counterKey", "lastIncrementKey", 60);

        assertFalse(result);
        verify(strategy, times(1)).incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60);
    }
}

