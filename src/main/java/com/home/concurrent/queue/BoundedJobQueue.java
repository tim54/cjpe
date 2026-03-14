package com.home.concurrent.queue;

import com.home.concurrent.model.Job;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class BoundedJobQueue implements JobQueue{

    private final int capacity;
    private int size;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition isQueueFull = lock.newCondition();
    private final Condition isQueueEmpty = lock.newCondition();
    private final Queue<Job> queue = new ArrayDeque<>();

    public BoundedJobQueue(int capacity){
        if (capacity <= 0){
            throw new IllegalArgumentException("capacity must be positive");
        }

        this.capacity = capacity;
    }

    public void submit(Job job) throws InterruptedException{
        lock.lockInterruptibly();
        try{
            while(size == capacity){
                isQueueFull.await();
            }
            queue.add(job);
            size++;
            isQueueEmpty.signal();
        } finally{
            lock.unlock();
        }
    }

    public Job take() throws InterruptedException{
        lock.lockInterruptibly();
        try{
            while(isEmpty()){
                isQueueEmpty.await();
            }
            Job job = queue.remove();
            size--;
            isQueueFull.signal();
            return job;

        } finally{
            lock.unlock();
        }
    }

    public int size(){
        lock.lock();
        try {
            return size;
        }
        finally {
            lock.unlock();
        }
    }

    public boolean isEmpty(){
        return size == 0;
    }
}
