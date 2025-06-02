package com.sagu.fhv.pipeline;

public interface Filter<T> {
    void transform(T t);
    default void transformThenPush(T t) {
        transform(t);
        getDownstreamPipe().input(t);
    }
    Pipe<T> getDownstreamPipe();
    void setOutputPipe(Pipe<T> downstreamPipe);
}
