package com.sagu.fhv.pipeline;

public interface ThreadedPushFilter<T> extends PushFilter<T>, Runnable {
    void setInput(T t);
    void flush();
}
