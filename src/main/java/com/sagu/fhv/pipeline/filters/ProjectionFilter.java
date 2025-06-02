package com.sagu.fhv.pipeline.filters;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.face.OptimizedFace;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ProjectionFilter implements Filter<OptimizedFace> {
    private final Matrix4f projMatrix;
    private volatile Pipe<OptimizedFace> outputPipe;
    
    public ProjectionFilter(Matrix4f projMatrix) {
        this.projMatrix = new Matrix4f(projMatrix);
    }
    
    @Override
    public void transform(OptimizedFace face) {
        face.transform(projMatrix);
        
        Vector4f v1 = face.getTransformedV1();
        Vector4f v2 = face.getTransformedV2();
        Vector4f v3 = face.getTransformedV3();
        
        perspectiveDivide(v1);
        perspectiveDivide(v2);
        perspectiveDivide(v3);
    }
    
    @Override
    public void transformThenPush(OptimizedFace face) {
        transform(face);
        if (outputPipe != null) {
            outputPipe.input(face);
        }
    }
    
    private void perspectiveDivide(Vector4f v) {
        if (v.w != 0) {
            v.x /= v.w;
            v.y /= v.w;
            v.z /= v.w;
            v.w = 1.0f;
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
