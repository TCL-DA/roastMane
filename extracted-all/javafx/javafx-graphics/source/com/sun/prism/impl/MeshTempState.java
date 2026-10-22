package com.sun.prism.impl;

import com.sun.javafx.geom.Quat4f;
import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;

final class MeshTempState {
   final Vec3f vec3f1 = new Vec3f();
   final Vec3f vec3f2 = new Vec3f();
   final Vec3f vec3f3 = new Vec3f();
   final Vec3f vec3f4 = new Vec3f();
   final Vec3f vec3f5 = new Vec3f();
   final Vec3f vec3f6 = new Vec3f();
   final Vec2f vec2f1 = new Vec2f();
   final Vec2f vec2f2 = new Vec2f();
   final int[] smFace = new int[7];
   final int[] triVerts = new int[3];
   final Vec3f[] triPoints = new Vec3f[3];
   final Vec2f[] triTexCoords = new Vec2f[3];
   final Vec3f[] triNormals = new Vec3f[3];
   final int[] triPointIndex = new int[3];
   final int[] triNormalIndex = new int[3];
   final int[] triTexCoordIndex = new int[3];
   final float[][] matrix = new float[3][3];
   final float[] vector = new float[3];
   final Quat4f quat = new Quat4f();
   MeshVertex[] pool;
   MeshVertex[] pVertex;
   int[] indexBuffer;
   short[] indexBufferShort;
   float[] vertexBuffer;
   private static final ThreadLocal<MeshTempState> tempStateRef = new ThreadLocal<MeshTempState>() {
      protected MeshTempState initialValue() {
         return new MeshTempState();
      }
   };

   private MeshTempState() {
      for (int var1 = 0; var1 < 3; var1++) {
         this.triNormals[var1] = new Vec3f();
      }
   }

   static MeshTempState getInstance() {
      return tempStateRef.get();
   }
}
