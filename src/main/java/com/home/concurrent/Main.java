package com.home.concurrent;

import com.home.concurrent.engine.JobEngine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        int workerCount  = 4;
        int producerCount = 2;

        System.out.println("Concurrent Lab starting...");
        JobEngine jobEngine = new JobEngine(workerCount, producerCount);
        try {

            jobEngine.start();

            try (ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2)){

                scheduler.scheduleAtFixedRate(
                        () -> jobEngine.updateEngineConfig(ThreadLocalRandom.current().nextInt(2, 10),100,500),
                        3,
                        3,
                        TimeUnit.SECONDS
                );

                scheduler.scheduleAtFixedRate(
                        jobEngine::printMetrics,
                        3,
                        3,
                        TimeUnit.SECONDS
                );

                TimeUnit.SECONDS.sleep(15);
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
