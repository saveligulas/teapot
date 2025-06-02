package com.sagu.fhv.model;

import com.sagu.fhv.face.OptimizedFace;
import com.sagu.fhv.pipeline.Pipe;

public class ModelSource {
    private Pipe<OptimizedFace> outputPipe;

    public void setOutputPipe(Pipe<OptimizedFace> pipe) {
        this.outputPipe = pipe;
    }

    public void pushModel(Model model) {
        if (outputPipe != null) {
            for (OptimizedFace face : model.getFaces()) {
                outputPipe.input(face);
            }
        }
    }
}
