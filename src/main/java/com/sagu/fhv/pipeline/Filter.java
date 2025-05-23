package com.sagu.fhv.pipeline;

public interface Filter<T> {
    void consume(T t);
}
