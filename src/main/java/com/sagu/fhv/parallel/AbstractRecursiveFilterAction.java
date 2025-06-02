package com.sagu.fhv.parallel;

import com.sagu.fhv.pipeline.Filter;

import java.util.List;
import java.util.concurrent.RecursiveAction;

public abstract class AbstractRecursiveFilterAction<T> extends RecursiveAction {
    protected final List<T> items;
    protected final int start;
    protected final int end;
    protected final Filter<T> filter;
    protected final int threshold;

    protected AbstractRecursiveFilterAction(List<T> items, int start, int end,
                                            Filter<T> filter, int threshold) {
        this.items = items;
        this.start = start;
        this.end = end;
        this.filter = filter;
        this.threshold = threshold;
    }

    @Override
    protected void compute() {
        int size = end - start;

        if (size <= threshold) {
            computeDirectly();
        } else {
            int mid = start + (size / 2);

            RecursiveAction left = createSubtask(start, mid);
            RecursiveAction right = createSubtask(mid, end);

            invokeAll(left, right);
        }
    }

    protected void computeDirectly() {
        for (int i = start; i < end; i++) {
            filter.transformThenPush(items.get(i));
        }
    }

    protected abstract RecursiveAction createSubtask(int start, int end);
}
