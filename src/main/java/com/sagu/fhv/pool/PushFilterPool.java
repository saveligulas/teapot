package com.sagu.fhv.pool;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.pipeline.ThreadedPushFilter;
import com.sagu.fhv.pipeline.ThreadedPushFilterBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

@Deprecated
public class PushFilterPool<T> implements Pipe<T> {
    private final ExecutorService threadPool;
    private final ThreadedPushFilterBuilder<T> filterBuilder;
    private final BlockingQueue<ThreadedPushFilter<T>> filterPool;
    private final BlockingQueue<T> queue;
    private final BlockingQueue<T> finishedQueue;

    public PushFilterPool(int poolSize, ThreadedPushFilterBuilder<T> filterBuilder, ExecutorService threadPool) {
        this.threadPool = threadPool;
        this.filterBuilder = filterBuilder;
        this.filterPool = new LinkedBlockingQueue<>(poolSize);
        this.queue = new LinkedBlockingQueue<>();
        this.finishedQueue = new LinkedBlockingQueue<>();

        initializePool(poolSize);

        startDispatcher();
    }

    private void initializePool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            this.filterPool.add(filterBuilder.build());
        }
    }

    private void startDispatcher() {
        Thread.startVirtualThread(this::dispatchNext);
    }

    private void dispatchNext() {
        try {
            T item = queue.take();
            ThreadedPushFilter<T> filter = filterPool.take();

            filter.setInput(item);
            threadPool.submit(() -> {
                try {
                    filter.run();
                } finally {
                    try {
                        filterPool.put(filter);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                dispatchNext();
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void input(T t) {
        try {
            queue.put(t);
        } catch (InterruptedException e) { // TODO: handle gracefully maybe with backup instance
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush() {
        return;
    }

    @Override
    public void setDownstreamFilter(Filter<T> filter) {

    }
}
