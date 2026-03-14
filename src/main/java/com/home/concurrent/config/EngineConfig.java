package com.home.concurrent.config;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class EngineConfig {

    private int maxConcurrentJobs = 0;
    private int producerDelayMin = 0;
    private int producerDelayMax = 0;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public EngineConfig(int maxConcurrentJobs, int producerDelayMin, int producerDelayMax){
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
        lock.writeLock().lock();
        try{
            this.maxConcurrentJobs = maxConcurrentJobs;
            this.producerDelayMin = producerDelayMin;
            this.producerDelayMax = producerDelayMax;
        } finally{
            lock.writeLock().unlock();
        }
    }
}
