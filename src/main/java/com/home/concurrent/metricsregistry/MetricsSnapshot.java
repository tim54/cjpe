package com.home.concurrent.metricsregistry;

public record MetricsSnapshot (
        long jobsProduced,
        long jobsStarted,
        long jobsCompleted,
        long jobsFailed,
        int activeWorkers,
        int queueSize
) { }
