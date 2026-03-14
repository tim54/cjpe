package com.home.concurrent.queue;

import com.home.concurrent.model.Job;

import java.util.concurrent.PriorityBlockingQueue;

public class JobQueue {
    private final BoundedJobQueue queue = new BoundedJobQueue(1);

    public void submit(Job job) throws InterruptedException{
        queue.submit(job);
    }

    public Job take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}
