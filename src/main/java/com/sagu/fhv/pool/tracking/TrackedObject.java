package com.sagu.fhv.pool.tracking;

public class TrackedObject<T> {
    private final T data;
    private final boolean[] completedStages;
    private final long id;

    public TrackedObject(T data, int stageCount) {
        this(data, new boolean[stageCount], IdGenerator.nextId());
    }

    public TrackedObject(T data, boolean[] completedStages, long id) {
        this.data = data;
        this.completedStages = completedStages;
        this.id = id;
    }
}
