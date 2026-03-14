package com.home.concurrent.config;

public record ConfigSnapshot(
        int maxConcurrentJobs,
        int producerDelayMin,
        int producerDelayMax
) {
}
