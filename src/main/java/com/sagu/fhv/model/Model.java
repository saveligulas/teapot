package com.sagu.fhv.model;

import com.sagu.fhv.face.OptimizedFace;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Collections;
import java.util.List;

public class Model {
    private final List<OptimizedFace> faces;
    private final Vector3f boundingBoxMin;
    private final Vector3f boundingBoxMax;
    private final Vector3f center;

    public Model(List<OptimizedFace> faces) {
        this.faces = faces;
        this.boundingBoxMin = new Vector3f(Float.MAX_VALUE);
        this.boundingBoxMax = new Vector3f(-Float.MAX_VALUE);
        this.center = new Vector3f();
        calculateBounds();
    }

    private void calculateBounds() {
        for (OptimizedFace face : faces) {
            updateBounds(face.getV1());
            updateBounds(face.getV2());
            updateBounds(face.getV3());
        }
        center.set(boundingBoxMin).add(boundingBoxMax).mul(0.5f);
    }

    private void updateBounds(Vector4f v) {
        boundingBoxMin.min(new Vector3f(v.x, v.y, v.z));
        boundingBoxMax.max(new Vector3f(v.x, v.y, v.z));
    }

    public List<OptimizedFace> getFaces() {
        return Collections.unmodifiableList(faces);
    }

    public Vector3f getCenter() { return new Vector3f(center); }
    public Vector3f getBoundingBoxMin() { return new Vector3f(boundingBoxMin); }
    public Vector3f getBoundingBoxMax() { return new Vector3f(boundingBoxMax); }
    public int getFaceCount() { return faces.size(); }
}
