package com.sagu.fhv.render;

import com.sagu.fhv.model.Model;
import com.sagu.fhv.pipeline.PipelineData;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

public abstract class AnimationRenderer extends AnimationTimer {
    private long lastUpdate;
    private final PipelineData pd;
    
    public AnimationRenderer(PipelineData pd) {
        this.pd = pd;
    }
    
    protected abstract void render(float deltaTime, Model model);
    
    @Override
    public void handle(long now) {
        if (lastUpdate > 0) {
            float deltaTime = (now - lastUpdate) / 1_000_000_000.0f;
            float fps = 1 / deltaTime;
            
            pd.getGraphicsContext().clearRect(0, 0, pd.getViewWidth(), pd.getViewHeight());
            pd.getGraphicsContext().setFill(Color.WHITE);
            pd.getGraphicsContext().fillText(String.format("%.1f FPS", fps), 10, 20);
            
            render(deltaTime, pd.getModel());
        }
        
        lastUpdate = now;
    }
}
