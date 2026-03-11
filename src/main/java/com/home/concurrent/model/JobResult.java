package com.home.concurrent.model;

public record JobResult(
        String jobId,
        JobStatus status,
        long processedAt,
        String message
) {
}
