package com.home.concurrent.model;

import java.util.concurrent.Callable;

public class Job implements Comparable<Job>{
    private final String id;
    private final int priority;
    private final long createdAt;
    private final Callable<JobResult> task;

    public Job(String id, int priority, long createdAt, Callable<JobResult> task) {
        this.id = id;
        this.priority = priority;
        this.createdAt = createdAt;
        this.task = task;
    }

    public String getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Callable<JobResult> getTask() {
        return task;
    }

    @Override
    public int compareTo(Job other) {
        int byPriority = Integer.compare(other.getPriority(), this.priority);
        if (byPriority != 0) {
            return byPriority;
        }
        return Long.compare(this.createdAt, other.getCreatedAt());
    }

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                ", task=" + task +
                '}';
    }
}
