package com.sagu.fhv.face;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class OptimizedFace {
    private final Vector4f v1 = new Vector4f();
    private final Vector4f v2 = new Vector4f();
    private final Vector4f v3 = new Vector4f();
    private final Vector4f n1 = new Vector4f();
    private final Vector4f n2 = new Vector4f();
    private final Vector4f n3 = new Vector4f();
    private final Vector4f tv1 = new Vector4f();
    private final Vector4f tv2 = new Vector4f();
    private final Vector4f tv3 = new Vector4f();
    private final Vector4f tn1 = new Vector4f();
    private final Vector4f tn2 = new Vector4f();
    private final Vector4f tn3 = new Vector4f();
    private final Vector3f faceNormal = new Vector3f();
    private float depth;

    public void set(float x1, float y1, float z1,
                    float x2, float y2, float z2,
                    float x3, float y3, float z3) {
        v1.set(x1, y1, z1, 1.0f);
        v2.set(x2, y2, z2, 1.0f);
        v3.set(x3, y3, z3, 1.0f);
        tv1.set(v1);
        tv2.set(v2);
        tv3.set(v3);
        calculateFaceNormal();
    }

    public void setNormals(Vector3f n1, Vector3f n2, Vector3f n3) {
        this.n1.set(n1, 0.0f);
        this.n2.set(n2, 0.0f);
        this.n3.set(n3, 0.0f);
        this.tn1.set(this.n1);
        this.tn2.set(this.n2);
        this.tn3.set(this.n3);
    }

    private void calculateFaceNormal() {
        Vector3f edge1 = new Vector3f(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
        Vector3f edge2 = new Vector3f(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
        edge1.cross(edge2, faceNormal).normalize();
    }

    public void transform(Matrix4f matrix) {
        matrix.transform(v1, tv1);
        matrix.transform(v2, tv2);
        matrix.transform(v3, tv3);
        
        // Für Normals nur die Rotation anwenden (keine Translation)
        // w = 0 für Richtungsvektoren
        tn1.set(n1);
        tn2.set(n2);
        tn3.set(n3);
        matrix.transform(tn1);
        matrix.transform(tn2);
        matrix.transform(tn3);
        tn1.normalize();
        tn2.normalize();
        tn3.normalize();
        
        calculateDepth();
    }

    public void calculateDepth() {
        depth = (tv1.z + tv2.z + tv3.z) * 0.333333f;
    }

    public float getDepth() { return depth; }
    public Vector4f getV1() { return v1; }
    public Vector4f getV2() { return v2; }
    public Vector4f getV3() { return v3; }
    public Vector4f getTransformedV1() { return tv1; }
    public Vector4f getTransformedV2() { return tv2; }
    public Vector4f getTransformedV3() { return tv3; }
    public Vector3f getFaceNormal() { return faceNormal; }
}
