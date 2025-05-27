package com.sagu.fhv.pipeline;

public interface PushFilter<T> extends Filter<T> {
    void push(T t);
}
