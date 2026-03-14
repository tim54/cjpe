package com.home.concurrent.queue;

import com.home.concurrent.model.Job;

public interface JobQueue {
    public void submit(Job job) throws InterruptedException;
    public Job take() throws InterruptedException;
    public int size();
}
