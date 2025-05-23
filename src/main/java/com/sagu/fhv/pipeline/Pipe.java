package com.sagu.fhv.pipeline;

public interface Pipe<T> {
    void setDownstreamFilter(Pipe<T> downstreamFilter);
}
