package com.sagu.fhv.pipeline.filters;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.face.OptimizedFace;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class BackfaceCullingFilter implements Filter<OptimizedFace> {
    private static final Vector3f VIEW_DIR = new Vector3f(0, 0, -1);
    private volatile Pipe<OptimizedFace> outputPipe;
    
    // Thread-local state für Berechnungen
    private final ThreadLocal<FilterState> threadLocalState = ThreadLocal.withInitial(FilterState::new);
    
    private static class FilterState {
        final Vector3f edge1 = new Vector3f();
        final Vector3f edge2 = new Vector3f();
        final Vector3f normal = new Vector3f();
    }
    
    @Override
    public void transform(OptimizedFace face) {
        FilterState state = threadLocalState.get();
        
        Vector4f v1 = face.getTransformedV1();
        Vector4f v2 = face.getTransformedV2();
        Vector4f v3 = face.getTransformedV3();
        
        // Nutze thread-lokale Vektoren
        state.edge1.set(
            v2.x - v1.x,
            v2.y - v1.y,
            v2.z - v1.z
        );
        
        state.edge2.set(
            v3.x - v1.x,
            v3.y - v1.y,
            v3.z - v1.z
        );
        
        state.edge1.cross(state.edge2, state.normal);
        float dot = state.normal.dot(VIEW_DIR);
        
        // Face ist sichtbar wenn dot < 0
        if (dot < 0) {
            // Weiterleiten
        } else {
            // Face wird gecullt - nichts tun
        }
    }
    
    @Override
    public void transformThenPush(OptimizedFace face) {
        FilterState state = threadLocalState.get();
        
        Vector4f v1 = face.getTransformedV1();
        Vector4f v2 = face.getTransformedV2();
        Vector4f v3 = face.getTransformedV3();
        
        state.edge1.set(
            v2.x - v1.x,
            v2.y - v1.y,
            v2.z - v1.z
        );
        
        state.edge2.set(
            v3.x - v1.x,
            v3.y - v1.y,
            v3.z - v1.z
        );
        
        state.edge1.cross(state.edge2, state.normal);
        float dot = state.normal.dot(VIEW_DIR);
        
        if (dot < 0 && outputPipe != null) {
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
