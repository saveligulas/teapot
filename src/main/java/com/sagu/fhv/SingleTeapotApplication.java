package com.sagu.fhv;

import com.sagu.fhv.model.ObjLoader;
import com.sagu.fhv.pipeline.PipelineData;
import com.sagu.fhv.pipeline.SimplePipelineFactory;
import com.sagu.fhv.render.RenderingMode;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.joml.Vector3f;

import java.io.File;

public class SingleTeapotApplication extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    
    @Override
    public void start(Stage stage) {
        File teapotFile = new File("resources/teapot.obj");
        
        ObjLoader.loadModel(teapotFile).ifPresentOrElse(model -> {
            System.out.println("Model loaded: " + model.getFaceCount() + " faces");
            System.out.println("Center: " + model.getCenter());
            System.out.println("Bounds: " + model.getBoundingBoxMin() + " to " + model.getBoundingBoxMax());
            
            Canvas canvas = new Canvas(WIDTH, HEIGHT);
            StackPane root = new StackPane(canvas);
            Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
            
            stage.setScene(scene);
            stage.setTitle("Single Teapot Pipeline Test");
            stage.show();
            
            // Eine einzelne Pipeline
            PipelineData pd = new PipelineData.Builder(canvas, model, WIDTH, HEIGHT)
                    .setModelColor(Color.GREEN)
                    .setRenderingMode(RenderingMode.WIREFRAME)
                    .build();
            
            AnimationTimer timer = SimplePipelineFactory.createPipeline(pd);
            timer.start();
            
        }, () -> System.err.println("Failed to load model!"));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
