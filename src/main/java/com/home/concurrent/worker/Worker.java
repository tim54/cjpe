package com.home.concurrent.worker;

import com.home.concurrent.metricsregistry.MetricsRegistry;
import com.home.concurrent.model.Job;
import com.home.concurrent.model.JobResult;
import com.home.concurrent.model.JobStatus;
import com.home.concurrent.queue.JobQueue;

import java.util.Objects;

public final class Worker implements Runnable {
    private final String name;
    private final JobQueue jobQueue;
    private volatile boolean running = true;
    private volatile Thread workerThread;

    private final MetricsRegistry metrics;

    public Worker(String name, JobQueue jobQueue, MetricsRegistry metrics) {
        this.name = Objects.requireNonNull(name);
        this.jobQueue = Objects.requireNonNull(jobQueue);
        this.metrics = Objects.requireNonNull(metrics);
    }

    @Override
    public void run() {
        workerThread = Thread.currentThread();
        workerThread.setName(name);

        while (running) {
            try {
                Job job = jobQueue.take();
                metrics.queueDecremented();
                process(job);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break; // exit loop
            } catch (Throwable t) {
                // last-resort protection: worker must not die
                System.out.println("worker=" + name + " fatalError=" + t);
            }
        }

        workerThread = null;
        running = false;
    }

    private void process(Job job) {
        long startNanos = System.nanoTime();
        JobResult result;

        metrics.workerStarted();
        metrics.incrementStarted();

        try {
            result = Objects.requireNonNull(job.task().call(), "JobResult must not be null");
            metrics.incrementCompleted();
        } catch (Exception e) {
            result = new JobResult(job.id(), JobStatus.FAILED, System.currentTimeMillis(),
                    "error=" + e.getClass().getSimpleName() + ": " + e.getMessage());
            metrics.incrementFailed();
        }



        long elapsedNanos = System.nanoTime() - startNanos;
        double elapsedMs = elapsedNanos / 1_000_000.0;

        if (result.status() == JobStatus.FAILED) {
            System.out.println("worker=" + name
                    + " jobId=" + job.id()
                    + " priority=" + job.priority()
                    + " status=FAILED"
                    + " elapsedMs=" + elapsedMs
                    + " message=" + result.message());
        } else {
            System.out.println("worker=" + name
                    + " jobId=" + job.id()
                    + " priority=" + job.priority()
                    + " status=SUCCESS"
                    + " elapsedMs=" + elapsedMs);
        }

        metrics.workerStopped();
    }

    public void stop() {
        running = false;
        Thread t = workerThread;
        if (t != null) t.interrupt();
    }
}
