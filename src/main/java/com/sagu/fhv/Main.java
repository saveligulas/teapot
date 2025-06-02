package com.sagu.fhv;

import com.sagu.fhv.face.OptimizedFace;
import com.sagu.fhv.model.ModelSource;
import com.sagu.fhv.model.ObjLoader;
import com.sagu.fhv.parallel.WorkStealingPushPipe;
import com.sagu.fhv.pipeline.Pipe;
import com.sagu.fhv.pipeline.ThreadedPushFilter;
import com.sagu.fhv.pipeline.ThreadedPushFilterBuilder;
import com.sagu.fhv.pool.PushFilterPool;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Application.launch(TeapotApplication.class, args);
    }
}