package com.home.concurrent.model;

import java.util.Objects;
import java.util.concurrent.Callable;

public record Job(
        String id,
        int priority,
        long createdAt,
        Callable<JobResult> task
) implements Comparable<Job> {

    public Job {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(task, "task");
    }

    @Override
    public int compareTo(Job other) {
        int byPriority = Integer.compare(other.priority(), this.priority());
        if (byPriority != 0) {
            return byPriority;
        }

        int byCreatedAt = Long.compare(this.createdAt(), other.createdAt());
        if (byCreatedAt != 0) {
            return byCreatedAt;
        }

        return this.id().compareTo(other.id());
    }
}
