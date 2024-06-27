package com.onebit;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RedisIdemPotenceCounterUsingLuaTest {
    private Jedis jedis;
    private RedisIdemPotenceCounterUsingLua strategy;

    @Before
    public void setUp() {
        jedis = mock(Jedis.class);
        strategy = new RedisIdemPotenceCounterUsingLua();
    }

    @Test
    public void testIncrementIfNotRecently_Success() {
        when(jedis.eval(anyString(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(1L);

        boolean result = strategy.incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60);

        assertTrue(result);
        verify(jedis, times(1)).eval(anyString(), eq(2), eq("counterKey"), eq("lastIncrementKey"), eq("60"), anyString());
    }

    @Test
    public void testIncrementIfNotRecently_Failure() {
        when(jedis.eval(anyString(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(0L);

        boolean result = strategy.incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60);

        assertFalse(result);
        verify(jedis, times(1)).eval(anyString(), eq(2), eq("counterKey"), eq("lastIncrementKey"), eq("60"), anyString());
    }
}
