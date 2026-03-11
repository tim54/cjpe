package com.home.concurrent.worker;

import com.home.concurrent.model.Job;
import com.home.concurrent.model.JobResult;
import com.home.concurrent.model.JobStatus;
import com.home.concurrent.queue.JobQueue;

public final class Worker implements Runnable {
    private final String name;
    private final JobQueue jobQueue;
    private volatile boolean running;

    public Worker(String name, JobQueue jobQueue){
        this.name = name;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run(){
        try {
            while (running) {
                Job job = jobQueue.take();
                process(job);
            }
        } catch (InterruptedException e){
            running = false;
        }
    }

    private void process(Job job){
        long start = System.currentTimeMillis();
        String threadName = Thread.currentThread().getName();
        JobResult status = null;

        try{
            status = job.task().call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process job: " + job.id(), e);
        } finally {
            long executionTime = System.currentTimeMillis() - start;
            if (status != null && status.status() == JobStatus.FAILED) {
                System.out.println(
                        "worker=" + threadName
                                + ", job=" + job.id()
                                + ", priority=" + job.priority()
                                + ", status=" + status
                                + ", elapsedMs="
                                + executionTime + "ms"
                                + ", error=" + status.message()
                );
            } else {
                System.out.println(
                        "worker=" + threadName
                                + ", job=" + job.id()
                                + ", priority=" + job.priority()
                                + ", status=" + status
                                + ", elapsedMs="
                                + executionTime + "ms"
                );
            }
        }
    }

    public void start(){
        running = true;
        new Thread(this).start();
        Thread.currentThread().setName(name);
    }

    public void stop(){
        running = false;
    }

    public boolean isRunning(){
        return running;
    }
}
