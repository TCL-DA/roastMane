package com.sun.prism.impl;

import com.sun.javafx.geom.Quat4f;
import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.prism.Mesh;
import java.util.Arrays;
import java.util.HashMap;
import javafx.scene.shape.VertexFormat;

public abstract class BaseMesh extends BaseGraphicsResource implements Mesh {
   private int nVerts;
   private int nTVerts;
   private int nFaces;
   private float[] pos;
   private float[] uv;
   private int[] faces;
   private int[] smoothing;
   private boolean allSameSmoothing;
   private boolean allHardEdges;
   protected static final int POINT_SIZE = 3;
   protected static final int NORMAL_SIZE = 3;
   protected static final int TEXCOORD_SIZE = 2;
   protected static final int POINT_SIZE_VB = 3;
   protected static final int TEXCOORD_SIZE_VB = 2;
   protected static final int NORMAL_SIZE_VB = 4;
   protected static final int VERTEX_SIZE_VB = 9;
   public static final int FACE_MEMBERS_SIZE = 7;
   private boolean[] dirtyVertices;
   private float[] cachedNormals;
   private float[] cachedTangents;
   private float[] cachedBitangents;
   private float[] vertexBuffer;
   private int[] indexBuffer;
   private short[] indexBufferShort;
   private int indexBufferSize;
   private int numberOfVertices;
   private HashMap<Integer, BaseMesh.MeshGeomComp2VB> point2vbMap;
   private HashMap<Integer, BaseMesh.MeshGeomComp2VB> normal2vbMap;
   private HashMap<Integer, BaseMesh.MeshGeomComp2VB> texCoord2vbMap;

   protected BaseMesh(Disposer.Record var1) {
      super(var1);
   }

   public abstract boolean buildNativeGeometry(float[] var1, int var2, int[] var3, int var4);

   public abstract boolean buildNativeGeometry(float[] var1, int var2, short[] var3, int var4);

   private void convertNormalsToQuats(MeshTempState var1, int var2, float[] var3, float[] var4, float[] var5, float[] var6, boolean[] var7) {
      Vec3f var8 = var1.vec3f1;
      Vec3f var9 = var1.vec3f2;
      Vec3f var10 = var1.vec3f3;
      int var11 = 0;

      for (byte var12 = 0; var11 < var2; var12 += 9) {
         if (var7 == null || var7[var11]) {
            int var13 = var11 * 3;
            var8.x = var3[var13];
            var8.y = var3[var13 + 1];
            var8.z = var3[var13 + 2];
            var8.normalize();
            var9.x = var4[var13];
            var9.y = var4[var13 + 1];
            var9.z = var4[var13 + 2];
            var10.x = var5[var13];
            var10.y = var5[var13 + 1];
            var10.z = var5[var13 + 2];
            var1.triNormals[0].set(var8);
            var1.triNormals[1].set(var9);
            var1.triNormals[2].set(var10);
            MeshUtil.fixTSpace(var1.triNormals);
            this.buildVSQuat(var1.triNormals, var1.quat);
            var6[var12 + 5] = var1.quat.x;
            var6[var12 + 6] = var1.quat.y;
            var6[var12 + 7] = var1.quat.z;
            var6[var12 + 8] = var1.quat.w;
         }

         var11++;
      }
   }

   private boolean doBuildPNTGeometry(float[] var1, float[] var2, float[] var3, int[] var4) {
      if (this.point2vbMap == null) {
         this.point2vbMap = new HashMap<>();
      } else {
         this.point2vbMap.clear();
      }

      if (this.normal2vbMap == null) {
         this.normal2vbMap = new HashMap<>();
      } else {
         this.normal2vbMap.clear();
      }

      if (this.texCoord2vbMap == null) {
         this.texCoord2vbMap = new HashMap<>();
      } else {
         this.texCoord2vbMap.clear();
      }

      int var5 = VertexFormat.POINT_NORMAL_TEXCOORD.getVertexIndexSize();
      int var6 = var5 * 3;
      int var7 = VertexFormat.POINT_NORMAL_TEXCOORD.getPointIndexOffset();
      int var8 = VertexFormat.POINT_NORMAL_TEXCOORD.getNormalIndexOffset();
      int var9 = VertexFormat.POINT_NORMAL_TEXCOORD.getTexCoordIndexOffset();
      int var10 = var1.length / 3;
      int var11 = var2.length / 3;
      int var12 = var3.length / 2;
      int var13 = var4.length / var6;

      assert var10 > 0 && var11 > 0 && var12 > 0 && var13 > 0;

      this.cachedNormals = new float[var10 * 3];
      this.cachedTangents = new float[var10 * 3];
      this.cachedBitangents = new float[var10 * 3];
      this.vertexBuffer = new float[var10 * 9];
      this.indexBuffer = new int[var13 * 3];
      int var18 = 0;
      byte var19 = 0;
      MeshTempState var20 = MeshTempState.getInstance();

      for (int var21 = 0; var21 < 3; var21++) {
         if (var20.triPoints[var21] == null) {
            var20.triPoints[var21] = new Vec3f();
         }

         if (var20.triTexCoords[var21] == null) {
            var20.triTexCoords[var21] = new Vec2f();
         }
      }

      for (int var35 = 0; var35 < var13; var35++) {
         int var22 = var35 * var6;

         for (int var23 = 0; var23 < 3; var23++) {
            int var24 = var22 + var23 * var5;
            int var25 = var24 + var7;
            int var26 = var24 + var8;
            int var27 = var24 + var9;
            Integer var14 = var19 / 9;
            if (this.vertexBuffer.length <= var19) {
               int var28 = var19 / 9;
               int var29 = var28 + Math.max(var28 >> 3, 6);
               float[] var30 = new float[var29 * 9];
               System.arraycopy(this.vertexBuffer, 0, var30, 0, this.vertexBuffer.length);
               this.vertexBuffer = var30;
               var30 = new float[var29 * 3];
               System.arraycopy(this.cachedNormals, 0, var30, 0, this.cachedNormals.length);
               this.cachedNormals = var30;
               var30 = new float[var29 * 3];
               System.arraycopy(this.cachedTangents, 0, var30, 0, this.cachedTangents.length);
               this.cachedTangents = var30;
               var30 = new float[var29 * 3];
               System.arraycopy(this.cachedBitangents, 0, var30, 0, this.cachedBitangents.length);
               this.cachedBitangents = var30;
            }

            int var43 = var4[var25] * 3;
            int var44 = var4[var26] * 3;
            int var48 = var4[var27] * 2;
            var20.triPointIndex[var23] = var43;
            var20.triTexCoordIndex[var23] = var48;
            var20.triVerts[var23] = var19 / 9;
            this.vertexBuffer[var19] = var1[var43];
            this.vertexBuffer[var19 + 1] = var1[var43 + 1];
            this.vertexBuffer[var19 + 2] = var1[var43 + 2];
            this.vertexBuffer[var19 + 3] = var3[var48];
            this.vertexBuffer[var19 + 4] = var3[var48 + 1];
            int var31 = var20.triVerts[var23] * 3;
            this.cachedNormals[var31] = var2[var44];
            this.cachedNormals[var31 + 1] = var2[var44 + 1];
            this.cachedNormals[var31 + 2] = var2[var44 + 2];
            var19 += 9;
            BaseMesh.MeshGeomComp2VB var15 = this.point2vbMap.get(var43);
            if (var15 == null) {
               var15 = new BaseMesh.MeshGeomComp2VB(var43, var14);
               this.point2vbMap.put(var43, var15);
            } else {
               var15.addLoc(var14);
            }

            BaseMesh.MeshGeomComp2VB var16 = this.normal2vbMap.get(var44);
            if (var16 == null) {
               var16 = new BaseMesh.MeshGeomComp2VB(var44, var14);
               this.normal2vbMap.put(var44, var16);
            } else {
               var16.addLoc(var14);
            }

            BaseMesh.MeshGeomComp2VB var17 = this.texCoord2vbMap.get(var48);
            if (var17 == null) {
               var17 = new BaseMesh.MeshGeomComp2VB(var48, var14);
               this.texCoord2vbMap.put(var48, var17);
            } else {
               var17.addLoc(var14);
            }

            this.indexBuffer[var18++] = var14;
         }

         for (int var40 = 0; var40 < 3; var40++) {
            var20.triPoints[var40].x = var1[var20.triPointIndex[var40]];
            var20.triPoints[var40].y = var1[var20.triPointIndex[var40] + 1];
            var20.triPoints[var40].z = var1[var20.triPointIndex[var40] + 2];
            var20.triTexCoords[var40].x = var3[var20.triTexCoordIndex[var40]];
            var20.triTexCoords[var40].y = var3[var20.triTexCoordIndex[var40] + 1];
         }

         MeshUtil.computeTBNNormalized(
            var20.triPoints[0], var20.triPoints[1], var20.triPoints[2], var20.triTexCoords[0], var20.triTexCoords[1], var20.triTexCoords[2], var20.triNormals
         );

         for (int var41 = 0; var41 < 3; var41++) {
            int var42 = var20.triVerts[var41] * 3;
            this.cachedTangents[var42] = var20.triNormals[1].x;
            this.cachedTangents[var42 + 1] = var20.triNormals[1].y;
            this.cachedTangents[var42 + 2] = var20.triNormals[1].z;
            this.cachedBitangents[var42] = var20.triNormals[2].x;
            this.cachedBitangents[var42 + 1] = var20.triNormals[2].y;
            this.cachedBitangents[var42 + 2] = var20.triNormals[2].z;
         }
      }

      this.numberOfVertices = var19 / 9;
      this.convertNormalsToQuats(var20, this.numberOfVertices, this.cachedNormals, this.cachedTangents, this.cachedBitangents, this.vertexBuffer, null);
      this.indexBufferSize = var13 * 3;
      if (this.numberOfVertices > 65536) {
         return this.buildNativeGeometry(this.vertexBuffer, this.numberOfVertices * 9, this.indexBuffer, this.indexBufferSize);
      } else {
         if (this.indexBufferShort == null || this.indexBufferShort.length < this.indexBufferSize) {
            this.indexBufferShort = new short[this.indexBufferSize];
         }

         int var36 = 0;

         for (int var39 = 0; var39 < var13; var39++) {
            this.indexBufferShort[var36] = (short)this.indexBuffer[var36++];
            this.indexBufferShort[var36] = (short)this.indexBuffer[var36++];
            this.indexBufferShort[var36] = (short)this.indexBuffer[var36++];
         }

         this.indexBuffer = null;
         return this.buildNativeGeometry(this.vertexBuffer, this.numberOfVertices * 9, this.indexBufferShort, this.indexBufferSize);
      }
   }

   private boolean updatePNTGeometry(float[] var1, int[] var2, float[] var3, int[] var4, float[] var5, int[] var6) {
      if (this.dirtyVertices == null) {
         this.dirtyVertices = new boolean[this.numberOfVertices];
      }

      Arrays.fill(this.dirtyVertices, false);
      int var7 = var2[0] / 3;
      int var8 = var2[1] / 3;
      if (var2[1] % 3 > 0) {
         var8++;
      }

      if (var8 > 0) {
         for (int var9 = 0; var9 < var8; var9++) {
            int var10 = (var7 + var9) * 3;
            BaseMesh.MeshGeomComp2VB var11 = this.point2vbMap.get(var10);

            assert var11 != null;

            if (var11 != null) {
               int[] var12 = var11.getLocs();
               int var13 = var11.getValidLocs();
               if (var12 != null) {
                  for (int var30 = 0; var30 < var13; var30++) {
                     int var35 = var12[var30] * 9;
                     this.vertexBuffer[var35] = var1[var10];
                     this.vertexBuffer[var35 + 1] = var1[var10 + 1];
                     this.vertexBuffer[var35 + 2] = var1[var10 + 2];
                     this.dirtyVertices[var12[var30]] = true;
                  }
               } else {
                  int var14 = var11.getLoc();
                  int var15 = var14 * 9;
                  this.vertexBuffer[var15] = var1[var10];
                  this.vertexBuffer[var15 + 1] = var1[var10 + 1];
                  this.vertexBuffer[var15 + 2] = var1[var10 + 2];
                  this.dirtyVertices[var14] = true;
               }
            }
         }
      }

      int var21 = var6[0] / 2;
      int var22 = var6[1] / 2;
      if (var6[1] % 2 > 0) {
         var22++;
      }

      if (var22 > 0) {
         for (int var23 = 0; var23 < var22; var23++) {
            int var25 = (var21 + var23) * 2;
            BaseMesh.MeshGeomComp2VB var27 = this.texCoord2vbMap.get(var25);

            assert var27 != null;

            if (var27 != null) {
               int[] var31 = var27.getLocs();
               int var36 = var27.getValidLocs();
               if (var31 != null) {
                  for (int var39 = 0; var39 < var36; var39++) {
                     int var43 = var31[var39] * 9 + 3;
                     this.vertexBuffer[var43] = var5[var25];
                     this.vertexBuffer[var43 + 1] = var5[var25 + 1];
                     this.dirtyVertices[var31[var39]] = true;
                  }
               } else {
                  int var16 = var27.getLoc();
                  int var17 = var16 * 9 + 3;
                  this.vertexBuffer[var17] = var5[var25];
                  this.vertexBuffer[var17 + 1] = var5[var25 + 1];
                  this.dirtyVertices[var16] = true;
               }
            }
         }
      }

      int var24 = var4[0] / 3;
      int var26 = var4[1] / 3;
      if (var4[1] % 3 > 0) {
         var26++;
      }

      if (var26 > 0) {
         MeshTempState var28 = MeshTempState.getInstance();

         for (int var32 = 0; var32 < var26; var32++) {
            int var37 = (var24 + var32) * 3;
            BaseMesh.MeshGeomComp2VB var40 = this.normal2vbMap.get(var37);

            assert var40 != null;

            if (var40 != null) {
               int[] var44 = var40.getLocs();
               int var18 = var40.getValidLocs();
               if (var44 != null) {
                  for (int var46 = 0; var46 < var18; var46++) {
                     int var47 = var44[var46] * 3;
                     this.cachedNormals[var47] = var3[var37];
                     this.cachedNormals[var47 + 1] = var3[var37 + 1];
                     this.cachedNormals[var47 + 2] = var3[var37 + 2];
                     this.dirtyVertices[var44[var46]] = true;
                  }
               } else {
                  int var19 = var40.getLoc();
                  int var20 = var19 * 3;
                  this.cachedNormals[var20] = var3[var37];
                  this.cachedNormals[var20 + 1] = var3[var37 + 1];
                  this.cachedNormals[var20 + 2] = var3[var37 + 2];
                  this.dirtyVertices[var19] = true;
               }
            }
         }
      }

      MeshTempState var29 = MeshTempState.getInstance();

      for (int var33 = 0; var33 < 3; var33++) {
         if (var29.triPoints[var33] == null) {
            var29.triPoints[var33] = new Vec3f();
         }

         if (var29.triTexCoords[var33] == null) {
            var29.triTexCoords[var33] = new Vec2f();
         }
      }

      for (byte var34 = 0; var34 < this.numberOfVertices; var34 += 3) {
         if (this.dirtyVertices[var34] || this.dirtyVertices[var34 + 1] || this.dirtyVertices[var34 + 2]) {
            int var38 = var34 * 9;

            for (int var41 = 0; var41 < 3; var41++) {
               var29.triPoints[var41].x = this.vertexBuffer[var38];
               var29.triPoints[var41].y = this.vertexBuffer[var38 + 1];
               var29.triPoints[var41].z = this.vertexBuffer[var38 + 2];
               var29.triTexCoords[var41].x = this.vertexBuffer[var38 + 3];
               var29.triTexCoords[var41].y = this.vertexBuffer[var38 + 3 + 1];
               var38 += 9;
            }

            MeshUtil.computeTBNNormalized(
               var29.triPoints[0],
               var29.triPoints[1],
               var29.triPoints[2],
               var29.triTexCoords[0],
               var29.triTexCoords[1],
               var29.triTexCoords[2],
               var29.triNormals
            );
            int var42 = var34 * 3;

            for (int var45 = 0; var45 < 3; var45++) {
               this.cachedTangents[var42] = var29.triNormals[1].x;
               this.cachedTangents[var42 + 1] = var29.triNormals[1].y;
               this.cachedTangents[var42 + 2] = var29.triNormals[1].z;
               this.cachedBitangents[var42] = var29.triNormals[2].x;
               this.cachedBitangents[var42 + 1] = var29.triNormals[2].y;
               this.cachedBitangents[var42 + 2] = var29.triNormals[2].z;
               var42 += 3;
            }
         }
      }

      this.convertNormalsToQuats(
         var29, this.numberOfVertices, this.cachedNormals, this.cachedTangents, this.cachedBitangents, this.vertexBuffer, this.dirtyVertices
      );
      return this.indexBuffer != null
         ? this.buildNativeGeometry(this.vertexBuffer, this.numberOfVertices * 9, this.indexBuffer, this.indexBufferSize)
         : this.buildNativeGeometry(this.vertexBuffer, this.numberOfVertices * 9, this.indexBufferShort, this.indexBufferSize);
   }

   @Override
   public boolean buildGeometry(
      boolean var1, float[] var2, int[] var3, float[] var4, int[] var5, float[] var6, int[] var7, int[] var8, int[] var9, int[] var10, int[] var11
   ) {
      return var1 ? this.buildPNTGeometry(var2, var3, var4, var5, var6, var7, var8, var9) : this.buildPTGeometry(var2, var6, var8, var10);
   }

   private boolean buildPNTGeometry(float[] var1, int[] var2, float[] var3, int[] var4, float[] var5, int[] var6, int[] var7, int[] var8) {
      boolean var9 = var2[1] > 0;
      boolean var10 = var4[1] > 0;
      boolean var11 = var6[1] > 0;
      boolean var12 = var8[1] > 0;
      boolean var13 = !var9 && !var10 && !var11 && !var12;
      if (var12) {
         var13 = true;
      }

      return var13 || this.vertexBuffer == null || this.indexBuffer == null && this.indexBufferShort == null
         ? this.doBuildPNTGeometry(var1, var3, var5, var7)
         : this.updatePNTGeometry(var1, var2, var3, var4, var5, var6);
   }

   private boolean buildPTGeometry(float[] var1, float[] var2, int[] var3, int[] var4) {
      this.nVerts = var1.length / 3;
      this.nTVerts = var2.length / 2;
      this.nFaces = var3.length / (VertexFormat.POINT_TEXCOORD.getVertexIndexSize() * 3);

      assert this.nVerts > 0 && this.nFaces > 0 && this.nTVerts > 0;

      this.pos = var1;
      this.uv = var2;
      this.faces = var3;
      this.smoothing = var4.length == this.nFaces ? var4 : null;
      MeshTempState var5 = MeshTempState.getInstance();
      if (var5.pool == null || var5.pool.length < this.nFaces * 3) {
         var5.pool = new MeshVertex[this.nFaces * 3];
      }

      if (var5.indexBuffer == null || var5.indexBuffer.length < this.nFaces * 3) {
         var5.indexBuffer = new int[this.nFaces * 3];
      }

      if (var5.pVertex != null && var5.pVertex.length >= this.nVerts) {
         Arrays.fill(var5.pVertex, 0, var5.pVertex.length, null);
      } else {
         var5.pVertex = new MeshVertex[this.nVerts];
      }

      this.checkSmoothingGroup();
      this.computeTBNormal(var5.pool, var5.pVertex, var5.indexBuffer);
      int var6 = MeshVertex.processVertices(var5.pVertex, this.nVerts, this.allHardEdges, this.allSameSmoothing);
      if (var5.vertexBuffer == null || var5.vertexBuffer.length < var6 * 9) {
         var5.vertexBuffer = new float[var6 * 9];
      }

      this.buildVertexBuffer(var5.pVertex, var5.vertexBuffer);
      if (var6 > 65536) {
         this.buildIndexBuffer(var5.pool, var5.indexBuffer, null);
         return this.buildNativeGeometry(var5.vertexBuffer, var6 * 9, var5.indexBuffer, this.nFaces * 3);
      } else {
         if (var5.indexBufferShort == null || var5.indexBufferShort.length < this.nFaces * 3) {
            var5.indexBufferShort = new short[this.nFaces * 3];
         }

         this.buildIndexBuffer(var5.pool, var5.indexBuffer, var5.indexBufferShort);
         return this.buildNativeGeometry(var5.vertexBuffer, var6 * 9, var5.indexBufferShort, this.nFaces * 3);
      }
   }

   private void computeTBNormal(MeshVertex[] var1, MeshVertex[] var2, int[] var3) {
      MeshTempState var4 = MeshTempState.getInstance();
      int[] var5 = var4.smFace;
      int[] var6 = var4.triVerts;
      Vec3f[] var7 = var4.triPoints;
      Vec2f[] var8 = var4.triTexCoords;
      Vec3f[] var9 = var4.triNormals;
      String var10 = BaseMesh.class.getName();
      int var11 = 0;
      int var12 = 0;

      for (int var13 = 0; var11 < this.nFaces; var11++) {
         int var14 = var11 * 3;
         var5 = this.getFace(var11, var5);
         var6[0] = var5[BaseMesh.FaceMembers.POINT0.ordinal()];
         var6[1] = var5[BaseMesh.FaceMembers.POINT1.ordinal()];
         var6[2] = var5[BaseMesh.FaceMembers.POINT2.ordinal()];
         if (MeshUtil.isDeadFace(var6) && PlatformLogger.getLogger(var10).isLoggable(Level.FINE)) {
            PlatformLogger.getLogger(var10)
               .fine("Dead face [" + var6[0] + ", " + var6[1] + ", " + var6[2] + "] @ face group " + var11 + "; nEmptyFaces = " + ++var12);
         }

         for (int var15 = 0; var15 < 3; var15++) {
            var7[var15] = this.getVertex(var6[var15], var7[var15]);
         }

         var6[0] = var5[BaseMesh.FaceMembers.TEXCOORD0.ordinal()];
         var6[1] = var5[BaseMesh.FaceMembers.TEXCOORD1.ordinal()];
         var6[2] = var5[BaseMesh.FaceMembers.TEXCOORD2.ordinal()];

         for (int var18 = 0; var18 < 3; var18++) {
            var8[var18] = this.getTVertex(var6[var18], var8[var18]);
         }

         MeshUtil.computeTBNNormalized(var7[0], var7[1], var7[2], var8[0], var8[1], var8[2], var9);

         for (int var19 = 0; var19 < 3; var19++) {
            var1[var13] = var1[var13] == null ? new MeshVertex() : var1[var13];

            for (int var16 = 0; var16 < 3; var16++) {
               var1[var13].norm[var16].set(var9[var16]);
            }

            var1[var13].smGroup = var5[BaseMesh.FaceMembers.SMOOTHING_GROUP.ordinal()];
            var1[var13].fIdx = var11;
            var1[var13].tVert = var6[var19];
            var1[var13].index = -1;
            int var20 = var19 == 0
               ? BaseMesh.FaceMembers.POINT0.ordinal()
               : (var19 == 1 ? BaseMesh.FaceMembers.POINT1.ordinal() : BaseMesh.FaceMembers.POINT2.ordinal());
            int var17 = var5[var20];
            var1[var13].pVert = var17;
            var3[var14 + var19] = var17;
            var1[var13].next = var2[var17];
            var2[var17] = var1[var13];
            var13++;
         }
      }
   }

   private void buildVSQuat(Vec3f[] var1, Quat4f var2) {
      Vec3f var3 = MeshTempState.getInstance().vec3f1;
      var3.cross(var1[1], var1[2]);
      float var4 = var1[0].dot(var3);
      if (var4 < 0.0F) {
         var1[2].mul(-1.0F);
      }

      MeshUtil.buildQuat(var1, var2);
      if (var4 < 0.0F) {
         if (var2.w == 0.0F) {
            var2.w = 1.0E-10F;
         }

         var2.scale(-1.0F);
      }
   }

   private void buildVertexBuffer(MeshVertex[] var1, float[] var2) {
      Quat4f var3 = MeshTempState.getInstance().quat;
      int var4 = 0;
      int var5 = 0;

      for (int var6 = 0; var5 < this.nVerts; var5++) {
         for (MeshVertex var7 = var1[var5]; var7 != null; var7 = var7.next) {
            if (var7.index == var4) {
               int var8 = var7.pVert * 3;
               var2[var6++] = this.pos[var8];
               var2[var6++] = this.pos[var8 + 1];
               var2[var6++] = this.pos[var8 + 2];
               var8 = var7.tVert * 2;
               var2[var6++] = this.uv[var8];
               var2[var6++] = this.uv[var8 + 1];
               this.buildVSQuat(var7.norm, var3);
               var2[var6++] = var3.x;
               var2[var6++] = var3.y;
               var2[var6++] = var3.z;
               var2[var6++] = var3.w;
               var4++;
            }
         }
      }
   }

   private void buildIndexBuffer(MeshVertex[] var1, int[] var2, short[] var3) {
      for (int var4 = 0; var4 < this.nFaces; var4++) {
         int var5 = var4 * 3;
         if (var2[var5] != -1) {
            for (int var7 = 0; var7 < 3; var7++) {
               assert var1[var5].fIdx == var4;

               if (var3 != null) {
                  var3[var5 + var7] = (short)var1[var5 + var7].index;
               } else {
                  var2[var5 + var7] = var1[var5 + var7].index;
               }

               var1[var5 + var7].next = null;
            }
         } else {
            for (int var6 = 0; var6 < 3; var6++) {
               if (var3 != null) {
                  var3[var5 + var6] = 0;
               } else {
                  var2[var5 + var6] = 0;
               }
            }
         }
      }
   }

   public int getNumVerts() {
      return this.nVerts;
   }

   public int getNumTVerts() {
      return this.nTVerts;
   }

   public int getNumFaces() {
      return this.nFaces;
   }

   public Vec3f getVertex(int var1, Vec3f var2) {
      if (var2 == null) {
         var2 = new Vec3f();
      }

      int var3 = var1 * 3;
      var2.set(this.pos[var3], this.pos[var3 + 1], this.pos[var3 + 2]);
      return var2;
   }

   public Vec2f getTVertex(int var1, Vec2f var2) {
      if (var2 == null) {
         var2 = new Vec2f();
      }

      int var3 = var1 * 2;
      var2.set(this.uv[var3], this.uv[var3 + 1]);
      return var2;
   }

   private void checkSmoothingGroup() {
      if (this.smoothing != null && this.smoothing.length != 0) {
         for (int var1 = 0; var1 + 1 < this.smoothing.length; var1++) {
            if (this.smoothing[var1] != this.smoothing[var1 + 1]) {
               this.allSameSmoothing = false;
               this.allHardEdges = false;
               return;
            }
         }

         if (this.smoothing[0] == 0) {
            this.allSameSmoothing = false;
            this.allHardEdges = true;
         } else {
            this.allSameSmoothing = true;
            this.allHardEdges = false;
         }
      } else {
         this.allSameSmoothing = true;
         this.allHardEdges = false;
      }
   }

   public int[] getFace(int var1, int[] var2) {
      int var3 = var1 * 6;
      if (var2 == null || var2.length < 7) {
         var2 = new int[7];
      }

      for (int var4 = 0; var4 < 6; var4++) {
         var2[var4] = this.faces[var3 + var4];
      }

      var2[6] = this.smoothing != null ? this.smoothing[var1] : 1;
      return var2;
   }

   @Override
   public boolean isValid() {
      return true;
   }

   boolean test_isVertexBufferNull() {
      return this.vertexBuffer == null;
   }

   int test_getVertexBufferLength() {
      return this.vertexBuffer.length;
   }

   int test_getNumberOfVertices() {
      return this.numberOfVertices;
   }

   public static enum FaceMembers {
      POINT0,
      TEXCOORD0,
      POINT1,
      TEXCOORD1,
      POINT2,
      TEXCOORD2,
      SMOOTHING_GROUP;
   }

   class MeshGeomComp2VB {
      private final int key;
      private final int loc;
      private int[] locs;
      private int validLocs;

      MeshGeomComp2VB(int var2, int var3) {
         assert var3 >= 0;

         this.key = var2;
         this.loc = var3;
         this.locs = null;
         this.validLocs = 0;
      }

      void addLoc(int var1) {
         if (this.locs == null) {
            this.locs = new int[3];
            this.locs[0] = this.loc;
            this.locs[1] = var1;
            this.validLocs = 2;
         } else if (this.locs.length > this.validLocs) {
            this.locs[this.validLocs] = var1;
            this.validLocs++;
         } else {
            int[] var2 = new int[this.validLocs * 2];
            System.arraycopy(this.locs, 0, var2, 0, this.locs.length);
            this.locs = var2;
            this.locs[this.validLocs] = var1;
            this.validLocs++;
         }
      }

      int getKey() {
         return this.key;
      }

      int getLoc() {
         return this.loc;
      }

      int[] getLocs() {
         return this.locs;
      }

      int getValidLocs() {
         return this.validLocs;
      }
   }
}
