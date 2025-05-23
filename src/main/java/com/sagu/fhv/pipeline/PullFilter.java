package com.sagu.fhv.pipeline;

import com.sagu.fhv.face.FaceWrapper;

public interface PullFilter<T> extends Filter<T> {
    void pull(T t);
}
