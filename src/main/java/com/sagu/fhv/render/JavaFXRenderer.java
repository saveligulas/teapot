package com.sagu.fhv.render;

import com.sagu.fhv.pipeline.Filter;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.face.OptimizedFace;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.joml.Vector4f;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class JavaFXRenderer implements Filter<OptimizedFace> {
    private final GraphicsContext gc;
    private final AtomicReference<Color> color;
    private final RenderingMode mode;
    private final double width;
    private final double height;
    private volatile Pipe<OptimizedFace> outputPipe;
    private int renderCount = 0;

    private final ConcurrentLinkedQueue<RenderCommand> renderQueue = new ConcurrentLinkedQueue<>();
    
    private static class RenderCommand {
        final double x1, y1, x2, y2, x3, y3;
        final RenderingMode mode;
        final Color color;
        
        RenderCommand(double x1, double y1, double x2, double y2, double x3, double y3, RenderingMode mode, Color color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.x3 = x3;
            this.y3 = y3;
            this.mode = mode;
            this.color = color;
        }
    }
    
    public JavaFXRenderer(GraphicsContext gc, Color color, RenderingMode mode, int width, int height) {
        this.gc = gc;
        this.color = new AtomicReference<>(color);
        this.mode = mode;
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void transform(OptimizedFace face) {
        Vector4f v1 = face.getTransformedV1();
        Vector4f v2 = face.getTransformedV2();
        Vector4f v3 = face.getTransformedV3();
        
        double x1 = v1.x;
        double y1 = v1.y;
        double x2 = v2.x;
        double y2 = v2.y;
        double x3 = v3.x;
        double y3 = v3.y;
        
        if (!isVisible(x1, y1) && !isVisible(x2, y2) && !isVisible(x3, y3)) {
            return;
        }

        renderQueue.offer(new RenderCommand(x1, y1, x2, y2, x3, y3, mode, color.get()));
        renderCount++;
        if (renderCount <= 5) {
            //System.out.println("Renderer queued face " + renderCount + " at (" +
                              //String.format("%.1f, %.1f", x1, y1) + ")");
        }
    }
    
    @Override
    public void transformThenPush(OptimizedFace face) {
        transform(face);
        // Renderer ist End-Filter, kein Push
    }
    
    /**
     * Muss im JavaFX Thread aufgerufen werden!
     */
    public void flushRenderCommands() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::flushRenderCommands);
            return;
        }
        
        int flushed = 0;
        RenderCommand cmd;
        while ((cmd = renderQueue.poll()) != null) {
            switch (cmd.mode) {
                case POINT:
                    gc.setFill(cmd.color);
                    gc.fillOval(cmd.x1 - 1, cmd.y1 - 1, 2, 2);
                    gc.fillOval(cmd.x2 - 1, cmd.y2 - 1, 2, 2);
                    gc.fillOval(cmd.x3 - 1, cmd.y3 - 1, 2, 2);
                    break;
                    
                case WIREFRAME:
                    gc.setStroke(cmd.color);
                    gc.strokeLine(cmd.x1, cmd.y1, cmd.x2, cmd.y2);
                    gc.strokeLine(cmd.x2, cmd.y2, cmd.x3, cmd.y3);
                    gc.strokeLine(cmd.x3, cmd.y3, cmd.x1, cmd.y1);
                    break;
                    
                case FILLED:
                    gc.setFill(cmd.color);
                    gc.fillPolygon(
                        new double[]{cmd.x1, cmd.x2, cmd.x3},
                        new double[]{cmd.y1, cmd.y2, cmd.y3},
                        3
                    );
                    break;
            }
            flushed++;
        }
        
        if (flushed > 0) {
            //System.out.println("Renderer flushed " + flushed + " commands");
        }
    }
    
    public int getQueueSize() {
        return renderQueue.size();
    }
    
    private boolean isVisible(double x, double y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    @Override
    public Pipe<OptimizedFace> getDownstreamPipe() {
        return outputPipe;
    }
    
    @Override
    public void setOutputPipe(Pipe<OptimizedFace> downstreamPipe) {
        this.outputPipe = downstreamPipe;
    }
    
    public void setColor(Color color) {
        this.color.set(color);
    }
}
