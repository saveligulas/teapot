package com.sagu.fhv;

import com.sagu.fhv.model.ObjLoader;
import com.sagu.fhv.pipeline.PipelineData;
import com.sagu.fhv.pipeline.PushPipelineFactory;
import com.sagu.fhv.render.RenderingMode;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.joml.Vector3f;

import java.io.File;

public class TeapotApplication extends Application {
    private static final int VIEW_WIDTH = 860;
    private static final int VIEW_HEIGHT = 540;
    private static final int SCENE_WIDTH = VIEW_WIDTH * 2;
    private static final int SCENE_HEIGHT = VIEW_HEIGHT * 2;
    
    @Override
    public void start(Stage stage) {
        File teapotFile = new File("resources/teapot.obj");
        
        ObjLoader.loadModel(teapotFile).ifPresent(model -> {
            Group root = new Group();
            Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.BLACK);
            
            Canvas c1 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
            Canvas c2 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
            Canvas c3 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
            Canvas c4 = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
            
            GridPane grid = new GridPane();
            grid.add(c1, 0, 0);
            grid.add(c2, 1, 0);
            grid.add(c3, 0, 1);
            grid.add(c4, 1, 1);
            
            root.getChildren().add(grid);
            stage.setScene(scene);
            stage.setTitle("Teapot Renderer - Work-Stealing Pipeline");
            stage.show();
            
            PipelineData pd1 = new PipelineData.Builder(c1, model, VIEW_WIDTH, VIEW_HEIGHT)
                    .setModelColor(Color.ORANGE)
                    .setRenderingMode(RenderingMode.WIREFRAME)
                    .build();
            
            PipelineData pd2 = new PipelineData.Builder(c2, model, VIEW_WIDTH, VIEW_HEIGHT)
                    .setModelColor(Color.DARKGREEN)
                    .setRenderingMode(RenderingMode.FILLED)
                    .build();
            
            PipelineData pd3 = new PipelineData.Builder(c3, model, VIEW_WIDTH, VIEW_HEIGHT)
                    .setModelColor(Color.RED)
                    .setRenderingMode(RenderingMode.POINT)
                    .build();
            
            PipelineData pd4 = new PipelineData.Builder(c4, model, VIEW_WIDTH, VIEW_HEIGHT)
                    .setModelColor(Color.BLUE)
                    .setRenderingMode(RenderingMode.FILLED)
                    .setPerformLighting(true)
                    .setModelRotAxis(new Vector3f(1, 0, 0))
                    .build();
            
            AnimationTimer anim1 = PushPipelineFactory.createPipeline(pd1);
            AnimationTimer anim2 = PushPipelineFactory.createPipeline(pd2);
            AnimationTimer anim3 = PushPipelineFactory.createPipeline(pd3);
            AnimationTimer anim4 = PushPipelineFactory.createPipeline(pd4);
            
            anim1.start();
            anim2.start();
            anim3.start();
            anim4.start();
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
