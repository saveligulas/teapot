package com.sagu.fhv.pool;

public interface Pool<T> {
    T take();
    void store(T t);
}
