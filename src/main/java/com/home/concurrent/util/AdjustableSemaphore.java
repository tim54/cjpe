package com.home.concurrent.util;

import java.util.concurrent.Semaphore;

public class AdjustableSemaphore extends Semaphore {

    public AdjustableSemaphore(int permits, boolean fair){
        super(permits, fair);
    }

    public void decreasePermitsSafely(int reduction){
        if (reduction < 0) {
            throw new IllegalArgumentException("Permits cannot be negative");
        }
        super.reducePermits(reduction);
    }
}
