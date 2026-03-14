package com.home.concurrent;

import com.home.concurrent.engine.JobEngine;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        int workerCount  = 4;
        int producerCount = 2;

        System.out.println("Concurrent Lab starting...");
        JobEngine jobEngine = new JobEngine(workerCount, producerCount);
        try {

            jobEngine.start();

            int idx = 5;
            while(idx > 0) {
                TimeUnit.SECONDS.sleep(3);
                jobEngine.printMetrics();
                idx--;
            }

        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            jobEngine.stop();

            System.out.println("engine=stopped remainingQueueSize=" + jobEngine.queueSize());
        }
        jobEngine.printMetrics();
    }
}
