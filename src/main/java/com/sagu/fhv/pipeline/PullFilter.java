package com.sagu.fhv.pipeline;

public interface PullFilter<T> extends Filter<T> {
    void pull(T t);
}
