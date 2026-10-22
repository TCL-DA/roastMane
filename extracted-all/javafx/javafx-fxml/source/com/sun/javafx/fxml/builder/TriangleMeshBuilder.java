package com.sun.javafx.fxml.builder;

import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import javafx.util.Builder;

public class TriangleMeshBuilder extends TreeMap<String, Object> implements Builder<TriangleMesh> {
   private static final String VALUE_SEPARATOR_REGEX = "[,\\s]+";
   private float[] points;
   private float[] texCoords;
   private float[] normals;
   private int[] faces;
   private int[] faceSmoothingGroups;
   private VertexFormat vertexFormat;

   public TriangleMesh build() {
      TriangleMesh var1 = new TriangleMesh();
      if (this.points != null) {
         var1.getPoints().setAll(this.points);
      }

      if (this.texCoords != null) {
         var1.getTexCoords().setAll(this.texCoords);
      }

      if (this.faces != null) {
         var1.getFaces().setAll(this.faces);
      }

      if (this.faceSmoothingGroups != null) {
         var1.getFaceSmoothingGroups().setAll(this.faceSmoothingGroups);
      }

      if (this.normals != null) {
         var1.getNormals().setAll(this.normals);
      }

      if (this.vertexFormat != null) {
         var1.setVertexFormat(this.vertexFormat);
      }

      return var1;
   }

   public Object put(String var1, Object var2) {
      if ("points".equalsIgnoreCase(var1)) {
         String[] var3 = ((String)var2).split("[,\\s]+");
         this.points = new float[var3.length];

         for (int var4 = 0; var4 < var3.length; var4++) {
            this.points[var4] = Float.parseFloat(var3[var4]);
         }
      } else if ("texcoords".equalsIgnoreCase(var1)) {
         String[] var5 = ((String)var2).split("[,\\s]+");
         this.texCoords = new float[var5.length];

         for (int var9 = 0; var9 < var5.length; var9++) {
            this.texCoords[var9] = Float.parseFloat(var5[var9]);
         }
      } else if ("faces".equalsIgnoreCase(var1)) {
         String[] var6 = ((String)var2).split("[,\\s]+");
         this.faces = new int[var6.length];

         for (int var10 = 0; var10 < var6.length; var10++) {
            this.faces[var10] = Integer.parseInt(var6[var10]);
         }
      } else if ("facesmoothinggroups".equalsIgnoreCase(var1)) {
         String[] var7 = ((String)var2).split("[,\\s]+");
         this.faceSmoothingGroups = new int[var7.length];

         for (int var11 = 0; var11 < var7.length; var11++) {
            this.faceSmoothingGroups[var11] = Integer.parseInt(var7[var11]);
         }
      } else if ("normals".equalsIgnoreCase(var1)) {
         String[] var8 = ((String)var2).split("[,\\s]+");
         this.normals = new float[var8.length];

         for (int var12 = 0; var12 < var8.length; var12++) {
            this.normals[var12] = Float.parseFloat(var8[var12]);
         }
      } else if ("vertexformat".equalsIgnoreCase(var1)) {
         if (var2 instanceof VertexFormat) {
            this.vertexFormat = (VertexFormat)var2;
         } else if ("point_texcoord".equalsIgnoreCase((String)var2)) {
            this.vertexFormat = VertexFormat.POINT_TEXCOORD;
         } else if ("point_normal_texcoord".equalsIgnoreCase((String)var2)) {
            this.vertexFormat = VertexFormat.POINT_NORMAL_TEXCOORD;
         }
      }

      return super.put(var1.toLowerCase(Locale.ROOT), var2);
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      return super.entrySet();
   }
}
