package com.sagu.fhv.parallel;

import com.sagu.fhv.pipeline.Filter;

import java.util.List;
import java.util.concurrent.RecursiveAction;

class FilterAction<T> extends AbstractRecursiveFilterAction<T> {
    FilterAction(List<T> items, int start, int end, Filter<T> filter, int threshold) {
        super(items, start, end, filter, threshold);
    }

    @Override
    protected RecursiveAction createSubtask(int start, int end) {
        return new FilterAction<>(items, start, end, filter, threshold);
    }
}
