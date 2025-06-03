package com.sagu.fhv;

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

public class SimpleTeapotTest extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    
    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        
        stage.setTitle("Simple Teapot Test");
        stage.setScene(scene);
        stage.show();
        
        // Erstelle ein einfaches Test-Dreieck
        OptimizedFace testFace = createTestTriangle();
        
        // Animation Timer für Rotation
        new AnimationTimer() {
            private long lastUpdate = 0;
            private float rotation = 0;
            
            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                    float deltaTime = (now - lastUpdate) / 1_000_000_000.0f;
                    rotation += deltaTime;
                    
                    // Clear
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0, 0, WIDTH, HEIGHT);
                    gc.setFill(Color.WHITE);
                    gc.fillText("FPS: " + (int)(1/deltaTime), 10, 20);
                    gc.fillText("Rotation: " + String.format("%.2f", rotation), 10, 40);
                    
                    // Render Test Triangle
                    renderTestTriangle(gc, testFace, rotation);
                    
                    // Test ob Model geladen werden kann
                    testModelLoading(gc);
                }
                lastUpdate = now;
            }
        }.start();
    }
    
    private OptimizedFace createTestTriangle() {
        OptimizedFace face = new OptimizedFace();
        // Großes Dreieck in der Mitte
        face.set(
            -100, -100, 0,   // Vertex 1
             100, -100, 0,   // Vertex 2
               0,  100, 0    // Vertex 3
        );
        return face;
    }
    
    private void renderTestTriangle(GraphicsContext gc, OptimizedFace face, float rotation) {
        // Einfache Transformation
        Matrix4f transform = new Matrix4f()
            .translate(WIDTH/2, HEIGHT/2, 0)  // Verschiebe in Mitte
            .rotateZ(rotation);               // Rotiere um Z
        
        // Kopiere Face für Transformation
        OptimizedFace tempFace = new OptimizedFace();
        tempFace.set(
            -100, -100, 0,
             100, -100, 0,
               0,  100, 0
        );
        
        tempFace.transform(transform);
        
        // Zeichne Wireframe
        Vector4f v1 = tempFace.getTransformedV1();
        Vector4f v2 = tempFace.getTransformedV2();
        Vector4f v3 = tempFace.getTransformedV3();
        
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(2);
        gc.strokeLine(v1.x, v1.y, v2.x, v2.y);
        gc.strokeLine(v2.x, v2.y, v3.x, v3.y);
        gc.strokeLine(v3.x, v3.y, v1.x, v1.y);
        
        // Zeichne Vertices
        gc.setFill(Color.RED);
        gc.fillOval(v1.x - 3, v1.y - 3, 6, 6);
        gc.fillOval(v2.x - 3, v2.y - 3, 6, 6);
        gc.fillOval(v3.x - 3, v3.y - 3, 6, 6);
    }
    
    private void testModelLoading(GraphicsContext gc) {
        // Teste ob Model-Dateien existieren
        java.io.File teapotFile = new java.io.File("resources/teapot.obj");
        gc.setFill(Color.YELLOW);
        gc.fillText("Teapot file exists: " + teapotFile.exists(), 10, 60);
        gc.fillText("Teapot path: " + teapotFile.getAbsolutePath(), 10, 80);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
