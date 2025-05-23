package com.sagu.fhv.pipeline;

import com.sagu.fhv.face.FaceWrapper;

public interface PushFilter<T> extends Filter<T> {
    void push(T t);
}
