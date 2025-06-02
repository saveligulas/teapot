package com.sagu.fhv.parallel;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WorkStealingPushPipe<T> implements Pipe<T> {
    private final ForkJoinPool pool;
    private Filter<T> connectedFilter;
    private final BlockingQueue<T> buffer;
    private final int batchSize;
    private final int parallelismThreshold;

    public WorkStealingPushPipe(Filter<T> filter, int parallelism,
                                int batchSize, int parallelismThreshold) {
        this.pool = new ForkJoinPool(parallelism);
        this.connectedFilter = filter;
        this.buffer = new LinkedBlockingQueue<>();
        this.batchSize = batchSize;
        this.parallelismThreshold = parallelismThreshold;
    }

    @Override
    public void input(T item) {
        buffer.offer(item);

        if (buffer.size() >= batchSize) {
            processBatch();
        }
    }

    private void processBatch() {
        List<T> batch = new ArrayList<>(batchSize);
        buffer.drainTo(batch, batchSize);

        if (!batch.isEmpty()) {
            FilterAction<T> action = new FilterAction<>(
                    batch, 0, batch.size(), connectedFilter, parallelismThreshold
            );
            pool.submit(action);
        }
    }

    public void flush() {
        while (!buffer.isEmpty()) {
            processBatch();
        }
        if (!pool.awaitQuiescence(1, TimeUnit.MINUTES)) {
            throw new RuntimeException("Pipe did not process all remaining tasks in flush within a minute");
        }

    }

    @Override
    public void setDownstreamFilter(Filter<T> filter) {
        this.connectedFilter = filter;
    }

    public void shutdown() {
        flush();
        pool.shutdown();
    }

}
