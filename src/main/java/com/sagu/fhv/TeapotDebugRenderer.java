package com.sagu.fhv;

import com.sagu.fhv.model.ObjLoader;
import com.sagu.fhv.model.Model;
import com.sagu.fhv.face.OptimizedFace;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import org.joml.*;
import org.joml.Math;

import java.io.File;
import java.util.List;

public class TeapotDebugRenderer extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Model model;
    
    @Override
    public void start(Stage stage) {
        File teapotFile = new File("resources/teapot.obj");
        System.out.println("Loading from: " + teapotFile.getAbsolutePath());
        
        ObjLoader.loadModel(teapotFile).ifPresentOrElse(
            loadedModel -> {
                this.model = loadedModel;
                System.out.println("Model loaded! Faces: " + model.getFaceCount());
                System.out.println("Center: " + model.getCenter());
                System.out.println("Bounds: " + model.getBoundingBoxMin() + " to " + model.getBoundingBoxMax());
            },
            () -> System.err.println("Failed to load model!")
        );
        
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        
        stage.setTitle("Teapot Debug Renderer");
        stage.setScene(scene);
        stage.show();
        
        // Render once without animation to debug
        if (model != null) {
            renderDebug(gc);
        }
    }
    
    private void renderDebug(GraphicsContext gc) {
        // Clear
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Draw center cross
        gc.setStroke(Color.RED);
        gc.strokeLine(WIDTH/2 - 20, HEIGHT/2, WIDTH/2 + 20, HEIGHT/2);
        gc.strokeLine(WIDTH/2, HEIGHT/2 - 20, WIDTH/2, HEIGHT/2 + 20);
        
        // Info
        gc.setFill(Color.WHITE);
        Vector3f center = model.getCenter();
        Vector3f min = model.getBoundingBoxMin();
        Vector3f max = model.getBoundingBoxMax();
        
        gc.fillText("Model Info:", 10, 20);
        gc.fillText("Center: " + center, 10, 40);
        gc.fillText("Min: " + min, 10, 60);
        gc.fillText("Max: " + max, 10, 80);
        gc.fillText("Size: " + new Vector3f(max).sub(min), 10, 100);
        
        // Test: Render model without any transformation
        gc.setStroke(Color.YELLOW);
        gc.fillText("RAW VERTICES (first 10 faces):", 10, 140);
        
        List<OptimizedFace> faces = model.getFaces();
        for (int i = 0; i < Math.min(10, faces.size()); i++) {
            OptimizedFace face = faces.get(i);
            Vector4f v1 = face.getV1();
            Vector4f v2 = face.getV2();
            Vector4f v3 = face.getV3();
            
            gc.fillText(String.format("Face %d: (%.2f,%.2f,%.2f)", i, v1.x, v1.y, v1.z), 10, 160 + i * 20);
        }
        
        // Test simple 2D projection (just scale and center)
        gc.setStroke(Color.GREEN);
        gc.fillText("Simple 2D projection:", 10, 380);
        
        float scale = 100; // pixels per unit
        float offsetX = WIDTH / 2;
        float offsetY = HEIGHT / 2;
        
        int rendered = 0;
        for (OptimizedFace face : faces) {
            Vector4f v1 = face.getV1();
            Vector4f v2 = face.getV2();
            Vector4f v3 = face.getV3();
            
            // Simple 2D projection - just use X and Y, ignore Z
            float x1 = v1.x * scale + offsetX;
            float y1 = -v1.y * scale + offsetY; // Flip Y
            float x2 = v2.x * scale + offsetX;
            float y2 = -v2.y * scale + offsetY;
            float x3 = v3.x * scale + offsetX;
            float y3 = -v3.y * scale + offsetY;
            
            // Check if on screen
            if (isOnScreen(x1, y1) || isOnScreen(x2, y2) || isOnScreen(x3, y3)) {
                gc.strokeLine(x1, y1, x2, y2);
                gc.strokeLine(x2, y2, x3, y3);
                gc.strokeLine(x3, y3, x1, y1);
                rendered++;
            }
        }
        
        gc.setFill(Color.WHITE);
        gc.fillText("Rendered: " + rendered + " faces", 10, 400);
    }
    
    private boolean isOnScreen(float x, float y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
