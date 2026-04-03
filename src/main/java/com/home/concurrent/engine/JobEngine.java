package com.home.concurrent.engine;

import com.home.concurrent.config.EngineConfig;
import com.home.concurrent.metricsregistry.MetricsRegistry;
import com.home.concurrent.metricsregistry.MetricsSnapshot;
import com.home.concurrent.producer.JobProducer;
import com.home.concurrent.queue.BoundedJobQueue;
import com.home.concurrent.queue.JobQueue;
import com.home.concurrent.queue.PriorityJobQueue;
import com.home.concurrent.util.AdjustableSemaphore;
import com.home.concurrent.util.ConcurrencyLimiter;
import com.home.concurrent.worker.Worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public final class JobEngine {

    private static final int DEFAULT_MAX_CONCURRENT_JOBS = 2;
    private static final int DEFAULT_PRODUCER_DELAY_MIN = 100;
    private static final int DEFAULT_PRODUCER_DELAY_MAX = 500;

    private final JobQueue jobQueue;
    private final ExecutorService executor;
    private final int workerCount;
    private final int producerCount;
    private final AtomicLong jobIdGenerator = new AtomicLong(0);
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final List<Worker> workers = new ArrayList<>();
    private final List<JobProducer> producers = new ArrayList<>();

    private final MetricsRegistry metrics = new MetricsRegistry();
    private final ConcurrencyLimiter limiter = new ConcurrencyLimiter(DEFAULT_MAX_CONCURRENT_JOBS);
    private final EngineConfig cfg = new EngineConfig(
            DEFAULT_MAX_CONCURRENT_JOBS,
            DEFAULT_PRODUCER_DELAY_MIN,
            DEFAULT_PRODUCER_DELAY_MAX
    );

    public JobEngine(int workerCount, int producerCount){
        this.workerCount = workerCount;
        this.producerCount = producerCount;
        this.executor = Executors.newFixedThreadPool(workerCount + producerCount);
        this.jobQueue = new BoundedJobQueue(50);
    }

    public void start(){
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("JobEngine has already started");
        }

        for (int i = 0; i < workerCount; i++){
            Worker worker = new Worker("worker-" + i, jobQueue, metrics, cfg, limiter);
            workers.add(worker);
            executor.submit(worker);
        }

        for (int i = 0; i < producerCount; i++){
            JobProducer producer = new JobProducer("producer-" + i, jobQueue, jobIdGenerator, metrics, cfg);
            producers.add(producer);
            executor.submit(producer);
        }
    }

    public void stop() {

        for (JobProducer producer : producers){
            producer.stop();
        }

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (Worker worker : workers){
            worker.stop();
        }

        executor.shutdown();

        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                List<Runnable> droppedTasks = executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)){
                    System.out.println("executor did not terminate cleanly; droppedTasks=" + droppedTasks.size());
                }
            }
        } catch (InterruptedException ie){
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int queueSize(){
        return jobQueue.size();
    }

    public void printMetrics(){
        MetricsSnapshot snapshot = metrics.snapshot();
        System.out.println("metrics jobsProduced=" + snapshot.jobsProduced()
                + " jobsStarted=" + snapshot.jobsStarted()
                + " jobsCompleted=" + snapshot.jobsCompleted()
                + " jobsFailed=" + snapshot.jobsFailed()
                + " activeWorkers=" + snapshot.activeWorkers()
                + " queueSize=" + snapshot.queueSize()
        );
    }

    public void updateEngineConfig(int maxConcurrentJobs, int producerDelayMin, int producerDelayMax){
        cfg.updateConfig(maxConcurrentJobs, producerDelayMin, producerDelayMax);
    }
}
