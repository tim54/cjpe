package com.home.concurrent.registry;

public record JobExecutionInfo(
        String jobId,
        int priority,
        JobExecutionState state,
        long createdAt,
        long startedAt,
        long finishedAt,
        String workerName,
        String message
) { }
