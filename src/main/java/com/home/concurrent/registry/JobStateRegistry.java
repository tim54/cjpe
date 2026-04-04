package com.home.concurrent.registry;

import com.home.concurrent.model.Job;

import java.sql.Time;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class JobStateRegistry {

    private final ConcurrentHashMap<String, JobExecutionInfo> jobs = new ConcurrentHashMap<>();

    public void onQueued(Job job){
        jobs.putIfAbsent(job.id(), new JobExecutionInfo(job.id(),
                job.priority(),
                JobExecutionState.QUEUED,
                job.createdAt(),
                0,
                null,
                null)
        );
    }

    public void onStarted(Job job, String workerName){
        jobs.compute(job.id(), (id, existing) -> {
            if (existing == null){
                return new JobExecutionInfo(job.id(),
                        job.priority(),
                        JobExecutionState.STARTED,
                        job.createdAt(),
                        0,
                        workerName,
                        null);
            } else {
                return new JobExecutionInfo(
                        existing.jobId(),
                        existing.priority(),
                        JobExecutionState.STARTED,
                        existing.createdAt(),
                        0,
                        workerName,
                        null);
            }
        });
    }

    public void onSucceeded(Job job, String workerName, String message){
        long now = System.currentTimeMillis();
        jobs.compute(job.id(), (id, existing) -> {
            if (existing == null){
                return new JobExecutionInfo(job.id(),
                        job.priority(),
                        JobExecutionState.SUCCESS,
                        job.createdAt(),
                        now,
                        workerName,
                        message);
            } else {
                return new JobExecutionInfo(
                        existing.jobId(),
                        existing.priority(),
                        JobExecutionState.SUCCESS,
                        existing.createdAt(),
                        now,
                        workerName,
                        message);
            }
        });
    }

    public void onFailed(Job job, String workerName, String message){
        long now = System.currentTimeMillis();
        jobs.compute(job.id(), (id, existing) -> {
            if (existing == null){
                return new JobExecutionInfo(job.id(),
                        job.priority(),
                        JobExecutionState.FAILED,
                        job.createdAt(),
                        now,
                        workerName,
                        message);
            } else {
                return new JobExecutionInfo(
                        existing.jobId(),
                        existing.priority(),
                        JobExecutionState.FAILED,
                        existing.createdAt(),
                        now,
                        workerName,
                        message);
            }
        });
    }

    public Optional<JobExecutionInfo> findById(String jobId){
        return Optional.ofNullable(jobs.get(jobId));
    }

    public int size(){
        return jobs.size();
    }

    public Map<String, JobExecutionInfo> snapShot(){
        return Map.copyOf(jobs);
    }

}
