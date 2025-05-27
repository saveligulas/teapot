package com.sagu.fhv.pool;

import java.util.concurrent.RecursiveAction;

public abstract class AbstractRecursiveFilterAction extends RecursiveAction {
    private static final int THRESHOLD = 100;
    private final int usedThreshold;

    public AbstractRecursiveFilterAction() {
        this(THRESHOLD);
    }

    public AbstractRecursiveFilterAction(int usedThreshold) {
        this.usedThreshold = usedThreshold;
    }
}
