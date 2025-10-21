package main.java.order;

import java.util.concurrent.atomic.AtomicLong;

public final class OrderIds {
    private static final AtomicLong counter = new AtomicLong(1000);

    private OrderIds() {} // prevent instantiation

    public static long next() {
        return counter.incrementAndGet();
    }
}
