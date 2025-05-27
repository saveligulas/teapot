package com.sagu.fhv.pipeline;

import java.util.List;

public interface BatchPipe<T> extends Pipe<T> {
    default void input(T t) {
        this.inputBatch(List.of(t)); // dirty but works
    }
    void inputBatch(List<T> list);
    List<T> outputBatch();
}
