package com.sagu.fhv.pool.tracking;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {
    private static final AtomicLong counter = new AtomicLong();

    public static long nextId() {
        return counter.incrementAndGet();
    }
}
