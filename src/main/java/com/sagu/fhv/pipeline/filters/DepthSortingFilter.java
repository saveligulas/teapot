package com.sagu.fhv.pipeline.filters;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.face.OptimizedFace;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class DepthSortingFilter implements Filter<OptimizedFace> {
    private final List<OptimizedFace> faceBuffer = Collections.synchronizedList(new ArrayList<>(10000));
    private Pipe<OptimizedFace> outputPipe;
    private volatile boolean isFlushing = false;
    
    @Override
    public void transform(OptimizedFace face) {
        if (!isFlushing) {
            faceBuffer.add(face);
        }
    }
    
    @Override
    public void transformThenPush(OptimizedFace face) {
        // DepthSort sammelt nur, pushed nicht direkt weiter
        transform(face);
    }
    
    @Override
    public Pipe<OptimizedFace> getDownstreamPipe() {
        return outputPipe;
    }
    
    @Override
    public void setOutputPipe(Pipe<OptimizedFace> downstreamPipe) {
        this.outputPipe = downstreamPipe;
    }
    
    public void flush() {
        isFlushing = true;
        
        // Kopiere Buffer für sicheres Sortieren
        List<OptimizedFace> sortList;
        synchronized(faceBuffer) {
            sortList = new ArrayList<>(faceBuffer);
            faceBuffer.clear();
        }
        
        // Sortiere sicher
        sortList.sort((f1, f2) -> {
            if (f1 == null || f2 == null) return 0;
            float d1 = f1.getDepth();
            float d2 = f2.getDepth();
            return Float.compare(d2, d1); // Reverse für back-to-front
        });
        
        // Output sorted faces
        for (OptimizedFace face : sortList) {
            if (face != null && outputPipe != null) {
                outputPipe.input(face);
            }
        }
        
        isFlushing = false;
    }
}
