package com.kovalevskii.concurrent.model;

public class JobResult {
    private final String jobId;
    private final JobStatus status;
    private final long processedAt;
    private final String message;

    public JobResult(String jobId, JobStatus status, long processedAt, String message) {
        this.jobId = jobId;
        this.status = status;
        this.processedAt = processedAt;
        this.message = message;
    }

    public String jobId() {
        return jobId;
    }

    public JobStatus status() {
        return status;
    }

    public long processedAt() {
        return processedAt;
    }

    public String message() {
        return message;
    }

    @Override
    public String toString() {
        return "JobResult{" +
                "jobId='" + jobId + '\'' +
                ", status=" + status +
                ", processedAt=" + processedAt +
                ", message='" + message + '\'' +
                '}';
    }
}
