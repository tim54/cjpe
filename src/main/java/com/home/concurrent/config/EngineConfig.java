package com.home.concurrent.config;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class EngineConfig {

    private int maxConcurrentJobs = 0;
    private int producerDelayMin = 0;
    private int producerDelayMax = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public EngineConfig(int maxConcurrentJobs, int producerDelayMin, int producerDelayMax){
        checkInputs(maxConcurrentJobs, producerDelayMin, producerDelayMax);
        this.maxConcurrentJobs = maxConcurrentJobs;
        this.producerDelayMin = producerDelayMin;
        this.producerDelayMax = producerDelayMax;
    }

    public ConfigSnapshot getConfig(){
        lock.readLock().lock();
        try{
            return new ConfigSnapshot(maxConcurrentJobs, producerDelayMin, producerDelayMax);
        } finally{
            lock.readLock().unlock();
        }
    }

    public void updateConfig(int maxConcurrentJobs, int producerDelayMin, int producerDelayMax){
        checkInputs(maxConcurrentJobs, producerDelayMin, producerDelayMax);
        lock.writeLock().lock();
        try{
            int oldMaxConcurrentJobs = this.maxConcurrentJobs;
            this.maxConcurrentJobs = maxConcurrentJobs;
            this.producerDelayMin = producerDelayMin;
            this.producerDelayMax = producerDelayMax;
            System.out.println("maxConcurrentJobs=" + maxConcurrentJobs + ", oldMaxConcurrentJobs=" + oldMaxConcurrentJobs);
        } finally{
            lock.writeLock().unlock();
        }
    }

    void checkInputs(int maxConcurrentJobs, int producerDelayMin, int producerDelayMax ){
        if (maxConcurrentJobs <= 0) {
            throw new IllegalArgumentException("maxConcurrentJobs must be greater than 0");
        }
        if (producerDelayMin < 0) {
            throw new IllegalArgumentException("producerDelayMin must be greater than or equal to 0");
        }
        if (producerDelayMax <= producerDelayMin) {
            throw new IllegalArgumentException("producerDelayMax must be greater than producerDelayMin");
        }
    }
}
