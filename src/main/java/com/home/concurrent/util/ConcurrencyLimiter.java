package com.home.concurrent.util;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyLimiter {
    private final AdjustableSemaphore semaphore;
    private final AtomicInteger configuredMax;

    public ConcurrencyLimiter(int initialMax){
        if (initialMax < 0) throw new IllegalArgumentException();

        this.semaphore = new AdjustableSemaphore(initialMax, true);
        this.configuredMax = new AtomicInteger(initialMax);
    }

    public Semaphore semaphore(){
        return semaphore;
    }

    public int configuredMax(){
        return configuredMax.get();
    }

    public void updateMax(int newMax){
        if (newMax <= 0 ) {
            throw new IllegalArgumentException();
        }

        int oldMax = configuredMax.getAndSet(newMax);
        int delta = newMax - oldMax;

        if (delta > 0){
            semaphore.release(delta);
        } else if (delta < 0){
            semaphore.decreasePermitsSafely(-delta);
        }
    }
}
