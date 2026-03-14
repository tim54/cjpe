package com.home.concurrent.metricsregistry;

public class MetricsSnapshot {
    private final long jobsProduced;
    private final long jobsStarted;
    private final long jobsCompleted;
    private final long jobsFailed;

    private final int activeWorkers;
    private final int queueSize;

    public MetricsSnapshot(long jobsProduced, long jobsStarted, long jobsCompleted, long jobsFailed, int activeWorkers, int queueSize) {
        this.jobsProduced = jobsProduced;
        this.jobsStarted = jobsStarted;
        this.jobsCompleted = jobsCompleted;
        this.jobsFailed = jobsFailed;
        this.activeWorkers = activeWorkers;
        this.queueSize = queueSize;
    }
}
