package com.onebit;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RedisIdemPotenceCounterUsingJavaTest {
    private Jedis jedis;
    private Transaction transaction;
    private RedisIdemPotenceCounterUsingJava strategy;

    @Before
    public void setUp() {
        jedis = mock(Jedis.class);
        transaction = mock(Transaction.class);
        strategy = new RedisIdemPotenceCounterUsingJava();
    }

    @Test
    public void testIncrementIfNotRecently_Success() {
        when(jedis.get("lastIncrementKey")).thenReturn("0");
        when(jedis.multi()).thenReturn(transaction);
        when(transaction.exec()).thenReturn(null);

        boolean result = strategy.incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60);

        assertTrue(result);
        verify(jedis, times(1)).watch("lastIncrementKey");
        verify(transaction, times(1)).incr("counterKey");
        verify(transaction, times(1)).set("lastIncrementKey", String.valueOf(System.currentTimeMillis() / 1000));
        verify(transaction, times(1)).exec();
    }

    @Test
    public void testIncrementIfNotRecently_Failure() {
        when(jedis.get("lastIncrementKey")).thenReturn(String.valueOf(System.currentTimeMillis() / 1000));
        boolean result = strategy.incrementIfNotRecently(jedis, "counterKey", "lastIncrementKey", 60);

        assertFalse(result);
        verify(jedis, times(1)).watch("lastIncrementKey");
    }
}
