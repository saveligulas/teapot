package com.sagu.fhv.pipeline;

import com.sagu.fhv.parallel.WorkStealingPushPipe;
                import com.sagu.fhv.pipeline.Filter;
                import com.sagu.fhv.pipeline.Pipe;
                import com.sagu.fhv.pipeline.PipelineData;
                import com.sagu.fhv.render.AnimationRenderer;
import com.sagu.fhv.render.JavaFXRenderer;
import com.sagu.fhv.model.Model;
import com.sagu.fhv.model.ModelSource;
import com.sagu.fhv.face.OptimizedFace;
import com.sagu.fhv.pipeline.filters.*;
import javafx.animation.AnimationTimer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SimplePipelineFactory {
    
    public static AnimationTimer createPipeline(PipelineData pd) {
        // Erstelle Filter
        ModelSource source = new ModelSource();
        TransformFilter transformFilter = new TransformFilter();
        JavaFXRenderer renderer = new JavaFXRenderer(
            pd.getGraphicsContext(), 
            pd.getModelColor(), 
            pd.getRenderingMode(),
            pd.getViewWidth(),
            pd.getViewHeight()
        );
        
        // Erstelle eine einfache Pipe
        WorkStealingPushPipe<OptimizedFace> pipe = new WorkStealingPushPipe<>(
            transformFilter, 4, 1000, 100
        );
        
        // Verbinde Pipeline direkt
        source.setOutputPipe(pipe);
        pipe.setDownstreamFilter(transformFilter);
        transformFilter.setOutputPipe(new Pipe<OptimizedFace>() {
            @Override
            public void input(OptimizedFace face) {
                renderer.transform(face);
            }
            
            @Override
            public void flush() {
                // Nichts zu tun
            }
            
            @Override
            public void setDownstreamFilter(Filter<OptimizedFace> filter) {
                // Nicht verwendet
            }
        });
        
        return new AnimationRenderer(pd) {
            private float totalRotation = 0;
            
            @Override
            protected void render(float deltaTime, Model model) {
                totalRotation += deltaTime * 0.5f; // Langsame Rotation
                
                // Hole Model Center für korrektes Zentrieren
                Vector3f modelCenter = model.getCenter();
                
                // Einfachere 2D Transformation (wie im MinimalRenderer)
                Matrix4f transformMatrix = new Matrix4f()
                    .identity()
                    .translate(pd.getViewWidth()/2, pd.getViewHeight()/2, 0)  // Screen-Center
                    .scale(100, -100, 1)                                       // Scale und Y-Flip
                    .rotateZ(totalRotation)                                    // Z-Rotation für 2D
                    .translate(-modelCenter.x, -modelCenter.y + 1.5f, 0);     // Zentriere und hebe an
                
                //System.out.println("\nStarting new frame...");
                
                transformFilter.setTransformMatrix(transformMatrix);
                
                // Starte Pipeline
                source.pushModel(model);
                
                // Flush
                pipe.flush();
                
                // Renderer im JavaFX Thread
                renderer.flushRenderCommands();
            }
        };
    }
}
