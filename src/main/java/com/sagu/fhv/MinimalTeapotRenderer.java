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

import java.io.File;
import java.util.List;

public class MinimalTeapotRenderer extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private Model model;
    
    @Override
    public void start(Stage stage) {
        // Lade Model
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
        
        stage.setTitle("Minimal Teapot Renderer");
        stage.setScene(scene);
        stage.show();
        
        // Simple Animation Timer
        new AnimationTimer() {
            private long lastUpdate = 0;
            private float rotation = 0;
            
            @Override
            public void handle(long now) {
                if (lastUpdate > 0 && model != null) {
                    float deltaTime = (now - lastUpdate) / 1_000_000_000.0f;
                    rotation += deltaTime * 0.5f; // Langsame Rotation
                    
                    render(gc, rotation);
                }
                lastUpdate = now;
            }
        }.start();
    }
    
    private void render(GraphicsContext gc, float rotation) {
        // Clear
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Info
        gc.setFill(Color.WHITE);
        gc.fillText("Faces: " + model.getFaceCount(), 10, 20);
        gc.fillText("Rotation: " + String.format("%.2f", rotation), 10, 40);
        
        // Model Transformation
        Vector3f modelCenter = model.getCenter();
        
        // Erstelle Transformations-Matrizen
        // Der Trick: Wir zentrieren nur X und Z, Y bleibt bei 0 (Boden)
        Matrix4f modelMatrix = new Matrix4f()
            .rotateY(rotation)                     // Rotiere um Y-Achse
            .scale(100)                            // Scale in Pixel-Einheiten
            .translate(-modelCenter.x, 0, -modelCenter.z);  // Zentriere nur X und Z!
            
        // Einfache orthographische Projektion
        Matrix4f viewMatrix = new Matrix4f()
            .translate(WIDTH/2, HEIGHT/2, 0)       // Verschiebe in Bildschirmmitte
            .scale(1, -1, 1);                      // Invertiere Y für JavaFX
            
        // Kombiniere Matrizen
        Matrix4f mv = new Matrix4f();
        mv.set(viewMatrix).mul(modelMatrix);
        
        // Rendere alle Faces
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(0.5);
        
        int renderedFaces = 0;
        List<OptimizedFace> faces = model.getFaces();
        
        for (OptimizedFace face : faces) {
            // Kopiere Vertices für Transformation
            Vector4f v1 = new Vector4f(face.getV1());
            Vector4f v2 = new Vector4f(face.getV2());
            Vector4f v3 = new Vector4f(face.getV3());
            
            // Transformiere
            mv.transform(v1);
            mv.transform(v2);
            mv.transform(v3);
            
            // Simple Backface Culling (2D cross product)
            float cross = (v2.x - v1.x) * (v3.y - v1.y) - (v2.y - v1.y) * (v3.x - v1.x);
            if (cross < 0) continue;
            
            // Zeichne Wireframe
            gc.strokeLine(v1.x, v1.y, v2.x, v2.y);
            gc.strokeLine(v2.x, v2.y, v3.x, v3.y);
            gc.strokeLine(v3.x, v3.y, v1.x, v1.y);
            
            renderedFaces++;
        }
        
        gc.setFill(Color.WHITE);
        gc.fillText("Rendered: " + renderedFaces + " faces", 10, 60);
        
        // Debug: Zeige Mittelpunkt
        gc.setStroke(Color.RED);
        gc.strokeLine(WIDTH/2 - 10, HEIGHT/2, WIDTH/2 + 10, HEIGHT/2);
        gc.strokeLine(WIDTH/2, HEIGHT/2 - 10, WIDTH/2, HEIGHT/2 + 10);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
