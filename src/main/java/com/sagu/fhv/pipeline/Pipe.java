package com.sagu.fhv.pipeline;

public interface Pipe<T> {
    void input(T t);
    void flush();
    void setDownstreamFilter(Filter<T> filter);
}
