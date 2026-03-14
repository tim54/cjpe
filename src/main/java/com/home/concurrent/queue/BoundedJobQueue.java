package com.home.concurrent.queue;

import com.home.concurrent.model.Job;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedJobQueue {

    private final int capacity;
    private int size;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition isQueueFull = lock.newCondition();
    private final Condition isQueueEmpty = lock.newCondition();
    private final Queue<Job> queue = new LinkedList<>();

    public BoundedJobQueue(int capacity){
        this.capacity = capacity;
    }

    public void submit(Job job) throws InterruptedException{
        lock.lock();
        try{
            if(size == capacity){
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
        lock.lock();
        try{
            if (isEmpty()){
                isQueueEmpty.await();
            }
            Job job = queue.poll();
            size--;
            isQueueFull.signal();
            return job;

        } finally{
            lock.unlock();
        }
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }
}
