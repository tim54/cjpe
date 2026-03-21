package com.home.concurrent.config;

public record ConfigSnapshot(
        int maxConcurrentJobs,
        int producerDelayMin,
        int producerDelayMax
) {
    public String toString(){
        return String.format("ConfigSnapshot{maxConcurrentJobs=%d, producerDelayMin=%d, producerDelayMax=%d}", maxConcurrentJobs, producerDelayMin, producerDelayMax);
    }
}
