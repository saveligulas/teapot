package com.sagu.fhv.pipeline.filters;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.face.OptimizedFace;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Einfacher Transform-Filter der alle Transformationen in einem Schritt macht
 */
public class TransformFilter implements Filter<OptimizedFace> {
    private volatile Pipe<OptimizedFace> outputPipe;
    private final ThreadLocal<Matrix4f> threadLocalMatrix = ThreadLocal.withInitial(Matrix4f::new);
    private volatile Matrix4f sharedTransformMatrix = new Matrix4f();
    
    private int faceCount = 0;
    
    public void setTransformMatrix(Matrix4f matrix) {
        this.sharedTransformMatrix = new Matrix4f(matrix);
        this.faceCount = 0; // Reset counter
    }
    
    @Override
    public void transform(OptimizedFace face) {
        // Verwende nur X und Y für 2D Projektion
        float x1 = face.getV1().x;
        float y1 = face.getV1().y;
        float x2 = face.getV2().x;
        float y2 = face.getV2().y;
        float x3 = face.getV3().x;
        float y3 = face.getV3().y;
        
        // Hole thread-lokale Matrix
        Matrix4f localMatrix = threadLocalMatrix.get();
        localMatrix.set(sharedTransformMatrix);
        
        // Transformiere als 2D Punkte (Z=0, W=1)
        Vector4f v1 = new Vector4f(x1, y1, 0, 1);
        Vector4f v2 = new Vector4f(x2, y2, 0, 1);
        Vector4f v3 = new Vector4f(x3, y3, 0, 1);
        
        localMatrix.transform(v1);
        localMatrix.transform(v2);
        localMatrix.transform(v3);
        
        // Simple Backface Culling (2D cross product)
        float cross = (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
        if (cross < 0) {
            return; // Cull face
        }
        
        // Setze transformierte Vertices zurück
        face.getTransformedV1().set(v1);
        face.getTransformedV2().set(v2);
        face.getTransformedV3().set(v3);
        
        faceCount++;
        if (faceCount <= 5) {
            //System.out.println("Face " + faceCount + " transformed to: (" +
                              //String.format("%.1f, %.1f", v1.x, v1.y) + ")");
            //System.out.println("  Cross product: " + cross + " (culled: " + (cross < 0) + ")");
        }
    }
    
    @Override
    public void transformThenPush(OptimizedFace face) {
        transform(face);
        // Nur pushen wenn nicht gecullt
        Vector4f v1 = face.getTransformedV1();
        Vector4f v2 = face.getTransformedV2();
        Vector4f v3 = face.getTransformedV3();
        
        float cross = (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
        if (cross >= 0 && outputPipe != null) {
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
