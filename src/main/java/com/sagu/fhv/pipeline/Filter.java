package com.sagu.fhv.pipeline;

public interface Filter<T> {
    void transformThenPipe(T t);
    void setDownStreamPipe(Pipe<T> downStreamPipe);
}
