package com.home.concurrent.queue;

import com.home.concurrent.model.Job;

import java.util.concurrent.PriorityBlockingQueue;

public class JobQueue {
    private final PriorityBlockingQueue<Job> queue = new PriorityBlockingQueue<>();

    public void submit(Job job){
        queue.put(job);
    }

    public Job take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}
