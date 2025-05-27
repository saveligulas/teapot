package com.sagu.fhv.pool;

import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.pipeline.ThreadedPushFilter;
import com.sagu.fhv.pipeline.ThreadedPushFilterBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class PushFilterPool<T> implements Pipe<T> {
    private final ExecutorService threadPool;
    private final ThreadedPushFilterBuilder<T> filterBuilder;
    private final BlockingQueue<ThreadedPushFilter<T>> filterPool;
    private final BlockingQueue<T> queue;

    public PushFilterPool(int poolSize, ThreadedPushFilterBuilder<T> filterBuilder, ExecutorService threadPool) {
        this.threadPool = threadPool;
        this.filterBuilder = filterBuilder;
        this.filterPool = new LinkedBlockingQueue<>(poolSize);
        this.queue = new LinkedBlockingQueue<>();

        initializePool(poolSize);

        startDispatcher();
    }

    private void initializePool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            this.filterPool.add(filterBuilder.build());
        }
    }

    private void startDispatcher() {
        Thread.startVirtualThread(() -> {
            try {
                while (true) {
                    T item = queue.take(); // wait for input
                    ThreadedPushFilter<T> filter = filterPool.take(); // waits for available filter
                    filter.setInput(item);
                    threadPool.submit(() -> {
                        filter.run(); // run it
                        try {
                            filterPool.put(filter); // return to pool when done
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void input(T t) {
        try {
            queue.put(t);
        } catch (InterruptedException e) { // TODO: handle gracefully maybe with backup instance
            throw new RuntimeException(e);
        }
    }
}
