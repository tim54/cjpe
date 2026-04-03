package com.home.concurrent.pipeline;

import com.home.concurrent.model.Job;
import com.home.concurrent.model.JobResult;
import com.home.concurrent.model.JobStatus;

import java.util.concurrent.Phaser;

public class JobPipeline {

    private final Phaser phaser;

    public JobPipeline(){
        this.phaser = new Phaser(0);
    }

    public JobPipeline(int parties){
        if (parties <= 0){
            throw new IllegalArgumentException("parties must be positive");
        }
        this.phaser = new Phaser(parties);
    }

    public JobResult process(Job job){
        phaser.register();
        try {
            JobResult result = null;

            validate(job);
            phaser.arriveAndAwaitAdvance();
            enrich(job);
            phaser.arriveAndAwaitAdvance();
            try {
                result = execute(job);
            } catch (Exception e) {
                result = new JobResult(job.id(), JobStatus.FAILED, System.currentTimeMillis(),
                        "error=" + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
            phaser.arriveAndAwaitAdvance();
            persist(job, result);
            phaser.arriveAndAwaitAdvance();
            return result;
        } finally{
            phaser.arriveAndDeregister();
        }
    }

    public void shutdown(){
        phaser.forceTermination();
    }

    private void validate(Job job){
        System.out.println("Validating job");
    }

    private void enrich(Job job){
        System.out.println("Enriching job");
    }

    private JobResult execute(Job job) throws Exception {
        System.out.println("Executing job");
        return job.task().call();
    }

    private void persist(Job job, JobResult result){
        System.out.println("Persisting job");
    }
}
