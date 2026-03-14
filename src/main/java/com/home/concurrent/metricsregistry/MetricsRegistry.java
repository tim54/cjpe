package com.home.concurrent.metricsregistry;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public final class MetricsRegistry {
    private final LongAdder jobsProduced = new LongAdder();
    private final LongAdder jobsStarted = new LongAdder();
    private final LongAdder jobsCompleted = new LongAdder();
    private final LongAdder jobsFailed = new LongAdder();

    private final AtomicInteger activeWorkers = new AtomicInteger();
    private final AtomicInteger queueSize = new AtomicInteger();

    public void incrementProduced(){
        jobsProduced.increment();
    }

    public void incrementStarted(){
        jobsStarted.increment();
    }

    public void incrementCompleted(){
        jobsCompleted.increment();
    }

    public void incrementFailed(){
        jobsFailed.increment();
    }

    public void workerStarted(){
        activeWorkers.incrementAndGet();
    }

    public void workerStopped() {
        activeWorkers.decrementAndGet();
    }

    public void queueIncremented(){
        queueSize.incrementAndGet();
    }

    public void queueDecremented(){
        queueSize.decrementAndGet();
    }

    public void queueSize(int size){
        queueSize.set(size);
    }

    public MetricsSnapshot snapshot(){
        return new MetricsSnapshot(jobsProduced.sum(),
                jobsStarted.sum(),
                jobsCompleted.sum(),
                jobsFailed.sum(),
                activeWorkers.get(),
                queueSize.get());
    }
}
