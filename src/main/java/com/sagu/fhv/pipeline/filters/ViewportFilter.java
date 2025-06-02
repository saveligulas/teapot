package com.sagu.fhv.pipeline.filters;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.face.OptimizedFace;
import org.joml.Matrix4f;

public class ViewportFilter implements Filter<OptimizedFace> {
    private final Matrix4f viewportMatrix;
    private volatile Pipe<OptimizedFace> outputPipe;
    
    public ViewportFilter(Matrix4f viewportMatrix) {
        this.viewportMatrix = new Matrix4f(viewportMatrix);
    }
    
    @Override
    public void transform(OptimizedFace face) {
        face.transform(viewportMatrix);
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
