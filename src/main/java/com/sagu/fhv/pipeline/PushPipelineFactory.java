package com.sagu.fhv.pipeline;

import com.sagu.fhv.parallel.WorkStealingPushPipe;
import com.sagu.fhv.render.AnimationRenderer;
import com.sagu.fhv.render.JavaFXRenderer;
import com.sagu.fhv.model.Model;
import com.sagu.fhv.model.ModelSource;
import com.sagu.fhv.face.OptimizedFace;
import com.sagu.fhv.pipeline.filters.*;
import javafx.animation.AnimationTimer;
import org.joml.Matrix4f;

public class PushPipelineFactory {
    
    public static AnimationTimer createPipeline(PipelineData pd) {
        // Jede Pipeline braucht ihre EIGENEN Filter-Instanzen!
        ModelSource source = new ModelSource();
        ModelViewFilter modelViewFilter = new ModelViewFilter();
        BackfaceCullingFilter cullingFilter = new BackfaceCullingFilter();
        DepthSortingFilter depthSort = new DepthSortingFilter();
        ProjectionFilter projectionFilter = new ProjectionFilter(pd.getProjTransform());
        ViewportFilter viewportFilter = new ViewportFilter(pd.getViewportTransform());
        JavaFXRenderer renderer = new JavaFXRenderer(
            pd.getGraphicsContext(), 
            pd.getModelColor(), 
            pd.getRenderingMode(),
            pd.getViewWidth(),
            pd.getViewHeight()
        );
        
        WorkStealingPushPipe<OptimizedFace> pipe1 = new WorkStealingPushPipe<>(
            modelViewFilter, 4, 1000, 100
        );
        
        // DepthSort braucht KEINE Pipe davor - muss sequenziell sein!
        
        WorkStealingPushPipe<OptimizedFace> pipe3 = new WorkStealingPushPipe<>(
            projectionFilter, 4, 500, 50
        );
        
        WorkStealingPushPipe<OptimizedFace> pipe4 = new WorkStealingPushPipe<>(
            viewportFilter, 4, 500, 50
        );
        
        WorkStealingPushPipe<OptimizedFace> pipe5 = new WorkStealingPushPipe<>(
            renderer, 2, 100, 10
        );
        
        source.setOutputPipe(pipe1);
        pipe1.setDownstreamFilter(cullingFilter);
        // CullingFilter braucht spezielle Behandlung
        cullingFilter.setOutputPipe(new Pipe<OptimizedFace>() {
            @Override
            public void input(OptimizedFace face) {
                depthSort.transform(face);
            }
            
            @Override
            public void flush() {
            }
            
            @Override
            public void setDownstreamFilter(Filter<OptimizedFace> filter) {
            }

        });
        depthSort.setOutputPipe(pipe3);
        pipe3.setDownstreamFilter(projectionFilter);
        projectionFilter.setOutputPipe(pipe4);
        pipe4.setDownstreamFilter(viewportFilter);
        viewportFilter.setOutputPipe(pipe5);
        pipe5.setDownstreamFilter(renderer);
        
        return new AnimationRenderer(pd) {
            private float totalRotation = 0;
            
            @Override
            protected void render(float deltaTime, Model model) {
                totalRotation += deltaTime;
                
                Matrix4f modelMatrix = new Matrix4f()
                    .translate(pd.getModelTranslation().m30(), pd.getModelTranslation().m31(), pd.getModelTranslation().m32())
                    .rotate(totalRotation, pd.getModelRotAxis());
                
                Matrix4f modelView = new Matrix4f(pd.getViewTransform()).mul(modelMatrix);
                modelViewFilter.setModelViewMatrix(modelView);
                
                source.pushModel(model);

                pipe1.flush();

                depthSort.flush();

                pipe3.flush();
                pipe4.flush();
                pipe5.flush();

                renderer.flushRenderCommands();
            }
        };
    }
}
