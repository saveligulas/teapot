package com.sagu.fhv.pipeline;

import com.sagu.fhv.model.Model;
import com.sagu.fhv.render.RenderingMode;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.joml.*;
import org.joml.Math;

public class PipelineData {
    private final GraphicsContext gc;
    private final Model model;
    private final int viewWidth;
    private final int viewHeight;
    
    private final RenderingMode renderingMode;
    private final Color modelColor;
    private final boolean performLighting;
    
    private final Vector3f lightPos;
    private final Vector3f modelPos;
    private final Vector3f modelRotAxis;
    private final Vector3f viewingEye;
    private final Vector3f viewingCenter;
    private final Vector3f viewingUp;
    
    private final Matrix4f modelTranslation;
    private final Matrix4f viewTransform;
    private final Matrix4f projTransform;
    private final Matrix4f viewportTransform;
    
    private PipelineData(Builder builder) {
        this.gc = builder.canvas.getGraphicsContext2D();
        this.model = builder.model;
        this.viewWidth = builder.viewWidth;
        this.viewHeight = builder.viewHeight;
        this.renderingMode = builder.renderingMode;
        this.modelColor = builder.modelColor;
        this.performLighting = builder.performLighting;
        
        this.lightPos = new Vector3f(builder.lightPos);
        this.modelPos = new Vector3f(builder.modelPos);
        this.modelRotAxis = new Vector3f(builder.modelRotAxis);
        this.viewingEye = new Vector3f(builder.viewingEye);
        this.viewingCenter = new Vector3f(builder.viewingCenter);
        this.viewingUp = new Vector3f(builder.viewingUp);
        
        this.modelTranslation = new Matrix4f().translation(modelPos);
        this.viewTransform = new Matrix4f().lookAt(viewingEye, viewingCenter, viewingUp);
        
        float aspect = (float) viewWidth / viewHeight;
        this.projTransform = new Matrix4f().perspective(
            Math.toRadians(10.0f), aspect, 0.1f, 100.0f
        );
        
        this.viewportTransform = createViewportMatrix(viewWidth, viewHeight);
    }
    
    private Matrix4f createViewportMatrix(int width, int height) {
        return new Matrix4f()
            .translate(width / 2.0f, height / 2.0f, 0.5f)
            .scale(width / 2.0f, -height / 2.0f, 0.5f);
    }
    
    public static class Builder {
        private Canvas canvas;
        private Model model;
        private int viewWidth;
        private int viewHeight;
        
        private RenderingMode renderingMode = RenderingMode.POINT;
        private Color modelColor = Color.WHITE;
        private boolean performLighting = false;
        
        private Vector3f lightPos = new Vector3f(10, 10, 10);
        private Vector3f modelPos = new Vector3f(0, -2, 0);
        private Vector3f modelRotAxis = new Vector3f(0, 1, 0);
        private Vector3f viewingEye = new Vector3f(0, 0, 5);
        private Vector3f viewingCenter = new Vector3f(0, 0, -5);
        private Vector3f viewingUp = new Vector3f(0, 1, 0);
        
        public Builder(Canvas canvas, Model model, int width, int height) {
            this.canvas = canvas;
            this.model = model;
            this.viewWidth = width;
            this.viewHeight = height;
        }
        
        public Builder setRenderingMode(RenderingMode mode) {
            this.renderingMode = mode;
            return this;
        }
        
        public Builder setModelColor(Color color) {
            this.modelColor = color;
            return this;
        }
        
        public Builder setPerformLighting(boolean lighting) {
            this.performLighting = lighting;
            return this;
        }
        
        public Builder setModelRotAxis(Vector3f axis) {
            this.modelRotAxis = axis;
            return this;
        }
        
        public PipelineData build() {
            return new PipelineData(this);
        }
    }
    
    public GraphicsContext getGraphicsContext() { return gc; }
    public Model getModel() { return model; }
    public int getViewWidth() { return viewWidth; }
    public int getViewHeight() { return viewHeight; }
    public RenderingMode getRenderingMode() { return renderingMode; }
    public Color getModelColor() { return modelColor; }
    public boolean isPerformLighting() { return performLighting; }
    public Vector3f getLightPos() { return new Vector3f(lightPos); }
    public Vector3f getModelRotAxis() { return new Vector3f(modelRotAxis); }
    public Matrix4f getModelTranslation() { return new Matrix4f(modelTranslation); }
    public Matrix4f getViewTransform() { return new Matrix4f(viewTransform); }
    public Matrix4f getProjTransform() { return new Matrix4f(projTransform); }
    public Matrix4f getViewportTransform() { return new Matrix4f(viewportTransform); }
}
