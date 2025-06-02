package com.sagu.fhv.pipeline.filters;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.face.OptimizedFace;
import org.joml.Matrix4f;

public class ModelViewFilter implements Filter<OptimizedFace> {
    private volatile Pipe<OptimizedFace> outputPipe;
    private final ThreadLocal<Matrix4f> threadLocalMatrix = ThreadLocal.withInitial(Matrix4f::new);
    private volatile Matrix4f sharedModelViewMatrix = new Matrix4f();
    
    public void setModelViewMatrix(Matrix4f matrix) {
        this.sharedModelViewMatrix = new Matrix4f(matrix);
    }
    
    @Override
    public void transform(OptimizedFace face) {
        // Jeder Thread hat seine eigene Matrix-Kopie
        Matrix4f localMatrix = threadLocalMatrix.get();
        localMatrix.set(sharedModelViewMatrix);
        face.transform(localMatrix);
    }
    
    @Override
    public void transformThenPush(OptimizedFace face) {
        transform(face);
        if (outputPipe != null) {
            outputPipe.input(face);
        }
    }
    
    @Override
    public Pipe<OptimizedFace> getDownstreamPipe() {
        return outputPipe;
    }
    
    @Override
    public void setOutputPipe(Pipe<OptimizedFace> downstreamPipe) {
        this.outputPipe = downstreamPipe;
    }
}
