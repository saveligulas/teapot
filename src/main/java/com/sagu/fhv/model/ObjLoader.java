package com.sagu.fhv.model;

import com.sagu.fhv.face.OptimizedFace;
import com.sagu.fhv.model.Model;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ObjLoader {
    private static final String COMMENT = "#";
    private static final String VERTEX = "v ";
    private static final String NORMAL = "vn ";
    private static final String FACE = "f ";
    private static final String TEXTURE = "vt ";

    public static Optional<Model> loadModel(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<Vector3f> vertices = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();
            List<OptimizedFace> faces = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith(COMMENT)) {
                    continue;
                }

                if (line.startsWith(VERTEX)) {
                    parseVertex(line.substring(2).trim(), vertices);
                } else if (line.startsWith(NORMAL)) {
                    parseNormal(line.substring(3).trim(), normals);
                } else if (line.startsWith(FACE)) {
                    parseFace(line.substring(2).trim(), vertices, normals, faces);
                }
            }

            return Optional.of(new Model(faces));

        } catch (IOException e) {
            System.err.println("Error loading model: " + e.getMessage());
            return Optional.empty();
        }
    }

    private static void parseVertex(String line, List<Vector3f> vertices) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 3) {
            try {
                float x = Float.parseFloat(parts[0]);
                float y = Float.parseFloat(parts[1]);
                float z = Float.parseFloat(parts[2]);
                vertices.add(new Vector3f(x, y, z));
            } catch (NumberFormatException e) {
                System.err.println("Invalid vertex: " + line);
            }
        }
    }

    private static void parseNormal(String line, List<Vector3f> normals) {
        String[] parts = line.split("\\s+");
        if (parts.length >= 3) {
            try {
                float x = Float.parseFloat(parts[0]);
                float y = Float.parseFloat(parts[1]);
                float z = Float.parseFloat(parts[2]);
                normals.add(new Vector3f(x, y, z).normalize());
            } catch (NumberFormatException e) {
                System.err.println("Invalid normal: " + line);
            }
        }
    }

    private static void parseFace(String line,
                                  List<Vector3f> vertices,
                                  List<Vector3f> normals,
                                  List<OptimizedFace> faces) {
        String[] parts = line.split("\\s+");
        if (parts.length != 3) {
            return; // Nur Triangles
        }

        try {
            int[] vIdx = new int[3];
            int[] nIdx = new int[3];
            boolean hasNormals = false;

            for (int i = 0; i < 3; i++) {
                String[] indices = parts[i].split("/");
                vIdx[i] = Integer.parseInt(indices[0]) - 1;

                if (indices.length == 3 && !indices[2].isEmpty()) {
                    nIdx[i] = Integer.parseInt(indices[2]) - 1;
                    hasNormals = true;
                }
            }

            Vector3f v1 = vertices.get(vIdx[0]);
            Vector3f v2 = vertices.get(vIdx[1]);
            Vector3f v3 = vertices.get(vIdx[2]);

            OptimizedFace face = new OptimizedFace();
            face.set(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, v3.x, v3.y, v3.z);

            if (hasNormals) {
                face.setNormals(
                        normals.get(nIdx[0]),
                        normals.get(nIdx[1]),
                        normals.get(nIdx[2])
                );
            } else {
                // Berechne Face-Normal
                Vector3f edge1 = new Vector3f(v2).sub(v1);
                Vector3f edge2 = new Vector3f(v3).sub(v1);
                Vector3f normal = new Vector3f();
                edge1.cross(edge2, normal).normalize();
                face.setNormals(normal, normal, normal);
            }

            faces.add(face);

        } catch (Exception e) {
            System.err.println("Invalid face: " + line);
        }
    }
}