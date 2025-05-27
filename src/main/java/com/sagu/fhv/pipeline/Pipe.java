package com.sagu.fhv.pipeline;

public interface Pipe<T> {
    void input(T t);
    T output();
    void setDownstreamFilter(Filter<T> filter);
    boolean previousStageCompleted();
}
