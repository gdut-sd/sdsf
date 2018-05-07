package com.rdc.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author SD
 * @since 2018/5/7
 */
public class IdGenerator {
    private static final AtomicLong counter = new AtomicLong();

    public static long next() {
        return counter.getAndIncrement();
    }
}
