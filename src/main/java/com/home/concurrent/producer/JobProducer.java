package com.home.concurrent.producer;

import com.home.concurrent.model.Job;
import com.home.concurrent.model.JobResult;
import com.home.concurrent.model.JobStatus;
import com.home.concurrent.queue.JobQueue;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public final class JobProducer implements Runnable {

    private final String name;
    private final JobQueue jobQueue;
    private final AtomicLong idGenerator;
    private volatile boolean running = true;

    public JobProducer(String name, JobQueue jobQueue, AtomicLong idGenerator){
        this.name = Objects.requireNonNull(name);
        this.jobQueue = Objects.requireNonNull(jobQueue);
        this.idGenerator = Objects.requireNonNull(idGenerator);
    }

    @Override
    public void run() {
        while (running){
            String id = "job-" + idGenerator.getAndIncrement();
            int priority = ThreadLocalRandom.current().nextInt(1, 5);
            long createdAt = System.currentTimeMillis();

            try {

                Job newJob = new Job(id, priority, createdAt, () -> {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 1000));
                    if (ThreadLocalRandom.current().nextInt(10) < 2) {
                        throw new RuntimeException("Job failed");
                    }
                    return new JobResult(id, JobStatus.SUCCESS, System.currentTimeMillis(), "Job Completed");
                });

                jobQueue.submit(newJob);

                System.out.println("producer=" + name + " jobId=" + newJob.id() + " priority=" + priority);

                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 500));
            } catch (InterruptedException ie){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop(){
        running = false;
        Thread.currentThread().interrupt();
    }
}
