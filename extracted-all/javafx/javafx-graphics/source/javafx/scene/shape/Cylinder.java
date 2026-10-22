package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.shape.CylinderHelper;
import com.sun.javafx.scene.shape.MeshHelper;
import com.sun.javafx.sg.prism.NGCylinder;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

public class Cylinder extends Shape3D {
   static final int DEFAULT_DIVISIONS = 64;
   static final double DEFAULT_RADIUS = 1.0;
   static final double DEFAULT_HEIGHT = 2.0;
   private int divisions = 64;
   private TriangleMesh mesh;
   private DoubleProperty height;
   private DoubleProperty radius;

   public Cylinder() {
      this(1.0, 2.0, 64);
   }

   public Cylinder(double var1, double var3) {
      this(var1, var3, 64);
   }

   public Cylinder(double var1, double var3, int var5) {
      CylinderHelper.initHelper(this);
      this.divisions = var5 < 3 ? 3 : var5;
      this.setRadius(var1);
      this.setHeight(var3);
   }

   public final void setHeight(double var1) {
      this.heightProperty().set(var1);
   }

   public final double getHeight() {
      return this.height == null ? 2.0 : this.height.get();
   }

   public final DoubleProperty heightProperty() {
      if (this.height == null) {
         this.height = new SimpleDoubleProperty(this, "height", 2.0) {
            public void invalidated() {
               NodeHelper.markDirty(Cylinder.this, DirtyBits.MESH_GEOM);
               Cylinder.this.manager.invalidateCylinderMesh(Cylinder.this.key);
               Cylinder.this.key = null;
               NodeHelper.geomChanged(Cylinder.this);
            }
         };
      }

      return this.height;
   }

   public final void setRadius(double var1) {
      this.radiusProperty().set(var1);
   }

   public final double getRadius() {
      return this.radius == null ? 1.0 : this.radius.get();
   }

   public final DoubleProperty radiusProperty() {
      if (this.radius == null) {
         this.radius = new SimpleDoubleProperty(this, "radius", 1.0) {
            public void invalidated() {
               NodeHelper.markDirty(Cylinder.this, DirtyBits.MESH_GEOM);
               Cylinder.this.manager.invalidateCylinderMesh(Cylinder.this.key);
               Cylinder.this.key = null;
               NodeHelper.geomChanged(Cylinder.this);
            }
         };
      }

      return this.radius;
   }

   public int getDivisions() {
      return this.divisions;
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.MESH_GEOM)) {
         NGCylinder var1 = NodeHelper.getPeer(this);
         float var2 = (float)this.getHeight();
         float var3 = (float)this.getRadius();
         if (!(var2 < 0.0F) && !(var3 < 0.0F)) {
            if (this.key == null) {
               this.key = new Cylinder.CylinderKey((double)var2, (double)var3, this.divisions);
            }

            this.mesh = this.manager.getCylinderMesh(var2, var3, this.divisions, this.key);
            this.mesh.updatePG();
            var1.updateMesh(this.mesh.getPGTriangleMesh());
         } else {
            var1.updateMesh(null);
         }
      }
   }

   private NGNode doCreatePeer() {
      return new NGCylinder();
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      float var3 = (float)this.getHeight();
      float var4 = (float)this.getRadius();
      if (!(var4 < 0.0F) && !(var3 < 0.0F)) {
         float var5 = var3 * 0.5F;
         var1 = var1.deriveWithNewBounds(-var4, -var5, -var4, var4, var5, var4);
         return var2.transform(var1, var1);
      } else {
         return var1.makeEmpty();
      }
   }

   private boolean doComputeContains(double var1, double var3) {
      double var5 = this.getRadius();
      double var7 = this.getHeight() * 0.5;
      return -var5 <= var1 && var1 <= var5 && -var7 <= var3 && var3 <= var7;
   }

   private boolean doComputeIntersects(PickRay var1, PickResultChooser var2) {
      boolean var3 = this.divisions < 64 && this.mesh != null;
      double var4 = this.getRadius();
      Vec3d var6 = var1.getDirectionNoClone();
      double var7 = var6.x;
      double var9 = var6.y;
      double var11 = var6.z;
      Vec3d var13 = var1.getOriginNoClone();
      double var14 = var13.x;
      double var16 = var13.y;
      double var18 = var13.z;
      double var20 = this.getHeight();
      double var22 = var20 / 2.0;
      CullFace var24 = this.getCullFace();
      double var25 = var7 * var7 + var11 * var11;
      double var27 = 2.0 * (var7 * var14 + var11 * var18);
      double var29 = var14 * var14 + var18 * var18 - var4 * var4;
      double var31 = var27 * var27 - 4.0 * var25 * var29;
      double var37 = Double.POSITIVE_INFINITY;
      double var39 = var1.getNearClip();
      double var41 = var1.getFarClip();
      if (var31 >= 0.0 && (var7 != 0.0 || var11 != 0.0)) {
         double var43 = Math.sqrt(var31);
         double var45 = var27 < 0.0 ? (-var27 - var43) / 2.0 : (-var27 + var43) / 2.0;
         double var33 = var45 / var25;
         double var35 = var29 / var45;
         if (var33 > var35) {
            double var47 = var33;
            var33 = var35;
            var35 = var47;
         }

         double var59 = var16 + var33 * var9;
         if (var33 < var39 || var59 < -var22 || var59 > var22 || var24 == CullFace.FRONT) {
            double var49 = var16 + var35 * var9;
            if (var35 >= var39 && var35 <= var41 && var49 >= -var22 && var49 <= var22 && (var24 != CullFace.BACK || var3)) {
               var37 = var35;
            }
         } else if (var33 <= var41) {
            var37 = var33;
         }
      }

      boolean var56 = false;
      boolean var44 = false;
      if (var37 == Double.POSITIVE_INFINITY || !var3) {
         double var57 = (-var22 - var16) / var9;
         double var60 = (var22 - var16) / var9;
         boolean var62 = false;
         double var54;
         double var55;
         if (var57 < var60) {
            var54 = var57;
            var55 = var60;
            var62 = true;
         } else {
            var54 = var60;
            var55 = var57;
         }

         if (var54 >= var39 && var54 <= var41 && var54 < var37 && var24 != CullFace.FRONT) {
            double var50 = var14 + var7 * var54;
            double var52 = var18 + var11 * var54;
            if (var50 * var50 + var52 * var52 <= var4 * var4) {
               var44 = var62;
               var56 = !var62;
               var37 = var54;
            }
         }

         if (var55 >= var39 && var55 <= var41 && var55 < var37 && (var24 != CullFace.BACK || var3)) {
            double var64 = var14 + var7 * var55;
            double var65 = var18 + var11 * var55;
            if (var64 * var64 + var65 * var65 <= var4 * var4) {
               var56 = var62;
               var44 = !var62;
               var37 = var55;
            }
         }
      }

      if (Double.isInfinite(var37) || Double.isNaN(var37)) {
         return false;
      } else if (var3) {
         return MeshHelper.computeIntersects(this.mesh, var1, var2, this, var24, false);
      } else {
         if (var2 != null && var2.isCloser(var37)) {
            Point3D var58 = PickResultChooser.computePoint(var1, var37);
            Point2D var46;
            if (var56) {
               var46 = new Point2D(0.5 + var58.getX() / (2.0 * var4), 0.5 + var58.getZ() / (2.0 * var4));
            } else if (var44) {
               var46 = new Point2D(0.5 + var58.getX() / (2.0 * var4), 0.5 - var58.getZ() / (2.0 * var4));
            } else {
               Point3D var61 = new Point3D(var58.getX(), 0.0, var58.getZ());
               Point3D var48 = var61.crossProduct(Rotate.Z_AXIS);
               double var63 = var61.angle(Rotate.Z_AXIS);
               if (var48.getY() > 0.0) {
                  var63 = 360.0 - var63;
               }

               var46 = new Point2D(1.0 - var63 / 360.0, 0.5 + var58.getY() / var20);
            }

            var2.offer(this, var37, -1, var58, var46);
         }

         return true;
      }
   }

   static TriangleMesh createMesh(int var0, float var1, float var2) {
      int var3 = var0 * 2 + 2;
      int var4 = (var0 + 1) * 4 + 1;
      int var5 = var0 * 4;
      float var6 = 0.00390625F;
      float var7 = 1.0F / (float)var0;
      var1 *= 0.5F;
      float[] var8 = new float[var3 * 3];
      float[] var9 = new float[var4 * 2];
      int[] var10 = new int[var5 * 6];
      int[] var11 = new int[var5];
      int var12 = 0;
      int var13 = 0;

      for (int var14 = 0; var14 < var0; var14++) {
         double var15 = (double)(var7 * (float)var14 * 2.0F) * Math.PI;
         var8[var12 + 0] = (float)(Math.sin(var15) * (double)var2);
         var8[var12 + 2] = (float)(Math.cos(var15) * (double)var2);
         var8[var12 + 1] = var1;
         var9[var13 + 0] = 1.0F - var7 * (float)var14;
         var9[var13 + 1] = 1.0F - var6;
         var12 += 3;
         var13 += 2;
      }

      var9[var13 + 0] = 0.0F;
      var9[var13 + 1] = 1.0F - var6;
      var13 += 2;

      for (int var27 = 0; var27 < var0; var27++) {
         double var32 = (double)(var7 * (float)var27 * 2.0F) * Math.PI;
         var8[var12 + 0] = (float)(Math.sin(var32) * (double)var2);
         var8[var12 + 2] = (float)(Math.cos(var32) * (double)var2);
         var8[var12 + 1] = -var1;
         var9[var13 + 0] = 1.0F - var7 * (float)var27;
         var9[var13 + 1] = var6;
         var12 += 3;
         var13 += 2;
      }

      var9[var13 + 0] = 0.0F;
      var9[var13 + 1] = var6;
      var13 += 2;
      var8[var12 + 0] = 0.0F;
      var8[var12 + 1] = var1;
      var8[var12 + 2] = 0.0F;
      var8[var12 + 3] = 0.0F;
      var8[var12 + 4] = -var1;
      var8[var12 + 5] = 0.0F;
      var12 += 6;

      for (int var28 = 0; var28 <= var0; var28++) {
         double var33 = var28 < var0 ? (double)(var7 * (float)var28 * 2.0F) * Math.PI : 0.0;
         var9[var13 + 0] = (float)(Math.sin(var33) * 0.5) + 0.5F;
         var9[var13 + 1] = (float)(Math.cos(var33) * 0.5) + 0.5F;
         var13 += 2;
      }

      for (int var29 = 0; var29 <= var0; var29++) {
         double var34 = var29 < var0 ? (double)(var7 * (float)var29 * 2.0F) * Math.PI : 0.0;
         var9[var13 + 0] = 0.5F + (float)(Math.sin(var34) * 0.5);
         var9[var13 + 1] = 0.5F - (float)(Math.cos(var34) * 0.5);
         var13 += 2;
      }

      var9[var13 + 0] = 0.5F;
      var9[var13 + 1] = 0.5F;
      var13 += 2;
      int var30 = 0;

      for (int var35 = 0; var35 < var0; var35++) {
         int var16 = var35 + 1;
         int var17 = var35 + var0;
         int var18 = var16 + var0;
         var10[var30 + 0] = var35;
         var10[var30 + 1] = var35;
         var10[var30 + 2] = var17;
         var10[var30 + 3] = var17 + 1;
         var10[var30 + 4] = var16 == var0 ? 0 : var16;
         var10[var30 + 5] = var16;
         var30 += 6;
         var10[var30 + 0] = var18 % var0 == 0 ? var18 - var0 : var18;
         var10[var30 + 1] = var18 + 1;
         var10[var30 + 2] = var16 == var0 ? 0 : var16;
         var10[var30 + 3] = var16;
         var10[var30 + 4] = var17;
         var10[var30 + 5] = var17 + 1;
         var30 += 6;
      }

      int var36 = (var0 + 1) * 2;
      int var38 = (var0 + 1) * 4;
      int var39 = var0 * 2;

      for (int var41 = 0; var41 < var0; var41++) {
         int var19 = var41 + 1;
         int var20 = var36 + var41;
         int var21 = var20 + 1;
         var10[var30 + 0] = var41;
         var10[var30 + 1] = var20;
         var10[var30 + 2] = var19 == var0 ? 0 : var19;
         var10[var30 + 3] = var21;
         var10[var30 + 4] = var39;
         var10[var30 + 5] = var38;
         var30 += 6;
      }

      var39 = var0 * 2 + 1;
      var36 = (var0 + 1) * 3;

      for (int var42 = 0; var42 < var0; var42++) {
         int var46 = var42 + 1 + var0;
         int var47 = var36 + var42;
         int var48 = var47 + 1;
         var10[var30 + 0] = var42 + var0;
         var10[var30 + 1] = var47;
         var10[var30 + 2] = var39;
         var10[var30 + 3] = var38;
         var10[var30 + 4] = var46 % var0 == 0 ? var46 - var0 : var46;
         var10[var30 + 5] = var48;
         var30 += 6;
      }

      for (int var43 = 0; var43 < var0 * 2; var43++) {
         var11[var43] = 1;
      }

      for (int var44 = var0 * 2; var44 < var0 * 4; var44++) {
         var11[var44] = 2;
      }

      TriangleMesh var45 = new TriangleMesh(true);
      var45.getPoints().setAll(var8);
      var45.getTexCoords().setAll(var9);
      var45.getFaces().setAll(var10);
      var45.getFaceSmoothingGroups().setAll(var11);
      return var45;
   }

   static {
      CylinderHelper.setCylinderAccessor(new CylinderHelper.CylinderAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Cylinder)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Cylinder)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Cylinder)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Cylinder)var1).doComputeContains(var2, var4);
         }

         @Override
         public boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3) {
            return ((Cylinder)var1).doComputeIntersects(var2, var3);
         }
      });
   }

   private static class CylinderKey extends Shape3D.Key {
      final double radius;
      final double height;
      final int divisions;

      private CylinderKey(double var1, double var3, int var5) {
         this.radius = var1;
         this.height = var3;
         this.divisions = var5;
      }

      @Override
      public int hashCode() {
         long var1 = 7L;
         var1 = 31L * var1 + Double.doubleToLongBits(this.radius);
         var1 = 31L * var1 + Double.doubleToLongBits(this.height);
         var1 = 31L * var1 + (long)this.divisions;
         return Long.hashCode(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 == null) {
            return false;
         } else if (!(var1 instanceof Cylinder.CylinderKey)) {
            return false;
         } else {
            Cylinder.CylinderKey var2 = (Cylinder.CylinderKey)var1;
            if (this.divisions != var2.divisions) {
               return false;
            } else {
               return Double.compare(this.radius, var2.radius) != 0 ? false : Double.compare(this.height, var2.height) == 0;
            }
         }
      }
   }
}
