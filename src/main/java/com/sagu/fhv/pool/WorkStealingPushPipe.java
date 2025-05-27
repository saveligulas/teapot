package com.sagu.fhv.pool;

import com.sagu.fhv.pipeline.Pipe;

import java.util.concurrent.ForkJoinPool;

public class WorkStealingPushPipe<T> implements Pipe<T> {
    private ForkJoinPool pool;


    @Override
    public void input(T t) {

    }
}
