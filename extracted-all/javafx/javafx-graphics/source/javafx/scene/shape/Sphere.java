package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.shape.MeshHelper;
import com.sun.javafx.scene.shape.SphereHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGSphere;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

public class Sphere extends Shape3D {
   static final int DEFAULT_DIVISIONS = 64;
   static final double DEFAULT_RADIUS = 1.0;
   private int divisions = 64;
   private TriangleMesh mesh;
   private DoubleProperty radius;

   public Sphere() {
      this(1.0, 64);
   }

   public Sphere(double var1) {
      this(var1, 64);
   }

   public Sphere(double var1, int var3) {
      SphereHelper.initHelper(this);
      this.divisions = var3 < 1 ? 1 : var3;
      this.setRadius(var1);
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
               NodeHelper.markDirty(Sphere.this, DirtyBits.MESH_GEOM);
               Sphere.this.manager.invalidateSphereMesh(Sphere.this.key);
               Sphere.this.key = null;
               NodeHelper.geomChanged(Sphere.this);
            }
         };
      }

      return this.radius;
   }

   public int getDivisions() {
      return this.divisions;
   }

   private NGNode doCreatePeer() {
      return new NGSphere();
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.MESH_GEOM)) {
         NGSphere var1 = NodeHelper.getPeer(this);
         float var2 = (float)this.getRadius();
         if (var2 < 0.0F) {
            var1.updateMesh(null);
         } else {
            if (this.key == null) {
               this.key = new Sphere.SphereKey((double)var2, this.divisions);
            }

            this.mesh = this.manager.getSphereMesh(var2, this.divisions, this.key);
            this.mesh.updatePG();
            var1.updateMesh(this.mesh.getPGTriangleMesh());
         }
      }
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      float var3 = (float)this.getRadius();
      if (var3 < 0.0F) {
         return var1.makeEmpty();
      } else {
         var1 = var1.deriveWithNewBounds(-var3, -var3, -var3, var3, var3, var3);
         return var2.transform(var1, var1);
      }
   }

   private boolean doComputeContains(double var1, double var3) {
      double var5 = this.getRadius();
      double var7 = var1 * var1 + var3 * var3;
      return var7 <= var5 * var5;
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
      double var20 = var7 * var7 + var9 * var9 + var11 * var11;
      double var22 = 2.0 * (var7 * var14 + var9 * var16 + var11 * var18);
      double var24 = var14 * var14 + var16 * var16 + var18 * var18 - var4 * var4;
      double var26 = var22 * var22 - 4.0 * var20 * var24;
      if (var26 < 0.0) {
         return false;
      } else {
         double var28 = Math.sqrt(var26);
         double var30 = var22 < 0.0 ? (-var22 - var28) / 2.0 : (-var22 + var28) / 2.0;
         double var32 = var30 / var20;
         double var34 = var24 / var30;
         if (var32 > var34) {
            double var36 = var32;
            var32 = var34;
            var34 = var36;
         }

         double var49 = var1.getNearClip();
         double var38 = var1.getFarClip();
         if (!(var34 < var49) && !(var32 > var38)) {
            double var40 = var32;
            CullFace var42 = this.getCullFace();
            if (var32 < var49 || var42 == CullFace.FRONT) {
               if (var34 <= var38 && this.getCullFace() != CullFace.BACK) {
                  var40 = var34;
               } else if (!var3) {
                  return false;
               }
            }

            if (Double.isInfinite(var40) || Double.isNaN(var40)) {
               return false;
            } else if (var3) {
               return MeshHelper.computeIntersects(this.mesh, var1, var2, this, var42, false);
            } else {
               if (var2 != null && var2.isCloser(var40)) {
                  Point3D var43 = PickResultChooser.computePoint(var1, var40);
                  Point3D var44 = new Point3D(var43.getX(), 0.0, var43.getZ());
                  Point3D var45 = var44.crossProduct(Rotate.Z_AXIS);
                  double var46 = var44.angle(Rotate.Z_AXIS);
                  if (var45.getY() > 0.0) {
                     var46 = 360.0 - var46;
                  }

                  Point2D var48 = new Point2D(1.0 - var46 / 360.0, 0.5 + var43.getY() / (2.0 * var4));
                  var2.offer(this, var40, -1, var43, var48);
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   private static int correctDivisions(int var0) {
      return (var0 + 3) / 4 * 4;
   }

   static TriangleMesh createMesh(int var0, float var1) {
      var0 = correctDivisions(var0);
      int var2 = var0 / 2;
      int var3 = var0 * (var2 - 1) + 2;
      int var4 = (var0 + 1) * (var2 - 1) + var0 * 2;
      int var5 = var0 * (var2 - 2) * 2 + var0 * 2;
      float var6 = 1.0F / (float)var0;
      float[] var7 = new float[var3 * 3];
      float[] var8 = new float[var4 * 2];
      int[] var9 = new int[var5 * 6];
      int var10 = 0;
      byte var11 = 0;

      for (int var12 = 0; var12 < var2 - 1; var12++) {
         float var13 = var6 * (float)(var12 + 1 - var2 / 2) * 2.0F * (float) Math.PI;
         float var14 = (float)Math.sin((double)var13);
         float var15 = (float)Math.cos((double)var13);
         float var16 = 0.5F + var14 * 0.5F;

         for (int var17 = 0; var17 < var0; var17++) {
            double var18 = (double)(var6 * (float)var17 * 2.0F * (float) Math.PI);
            float var20 = (float)Math.sin(var18);
            float var21 = (float)Math.cos(var18);
            var7[var10 + 0] = var20 * var15 * var1;
            var7[var10 + 2] = var21 * var15 * var1;
            var7[var10 + 1] = var14 * var1;
            var8[var11 + 0] = 1.0F - var6 * (float)var17;
            var8[var11 + 1] = var16;
            var10 += 3;
            var11 += 2;
         }

         var8[var11 + 0] = 0.0F;
         var8[var11 + 1] = var16;
         var11 += 2;
      }

      var7[var10 + 0] = 0.0F;
      var7[var10 + 1] = -var1;
      var7[var10 + 2] = 0.0F;
      var7[var10 + 3] = 0.0F;
      var7[var10 + 4] = var1;
      var7[var10 + 5] = 0.0F;
      var10 += 6;
      int var27 = (var2 - 1) * var0;
      float var28 = 0.00390625F;

      for (int var29 = 0; var29 < var0; var29++) {
         var8[var11 + 0] = 1.0F - var6 * (0.5F + (float)var29);
         var8[var11 + 1] = var28;
         var11 += 2;
      }

      for (int var30 = 0; var30 < var0; var30++) {
         var8[var11 + 0] = 1.0F - var6 * (0.5F + (float)var30);
         var8[var11 + 1] = 1.0F - var28;
         var11 += 2;
      }

      int var31 = 0;

      for (int var33 = 0; var33 < var2 - 2; var33++) {
         for (int var36 = 0; var36 < var0; var36++) {
            int var39 = var33 * var0 + var36;
            int var42 = var39 + 1;
            int var19 = var39 + var0;
            int var47 = var42 + var0;
            int var50 = var39 + var33;
            int var22 = var50 + 1;
            int var23 = var50 + var0 + 1;
            int var24 = var22 + var0 + 1;
            var9[var31 + 0] = var39;
            var9[var31 + 1] = var50;
            var9[var31 + 2] = var42 % var0 == 0 ? var42 - var0 : var42;
            var9[var31 + 3] = var22;
            var9[var31 + 4] = var19;
            var9[var31 + 5] = var23;
            var31 += 6;
            var9[var31 + 0] = var47 % var0 == 0 ? var47 - var0 : var47;
            var9[var31 + 1] = var24;
            var9[var31 + 2] = var19;
            var9[var31 + 3] = var23;
            var9[var31 + 4] = var42 % var0 == 0 ? var42 - var0 : var42;
            var9[var31 + 5] = var22;
            var31 += 6;
         }
      }

      int var34 = var27;
      int var37 = (var2 - 1) * (var0 + 1);

      for (int var40 = 0; var40 < var0; var40++) {
         int var45 = var40 + 1;
         int var48 = var37 + var40;
         var9[var31 + 0] = var34;
         var9[var31 + 1] = var48;
         var9[var31 + 2] = var45 == var0 ? 0 : var45;
         var9[var31 + 3] = var45;
         var9[var31 + 4] = var40;
         var9[var31 + 5] = var40;
         var31 += 6;
      }

      var34++;
      var37 += var0;
      int var41 = (var2 - 2) * var0;

      for (int var43 = 0; var43 < var0; var43++) {
         int var46 = var41 + var43;
         int var49 = var41 + var43 + 1;
         int var51 = var37 + var43;
         int var52 = (var2 - 2) * (var0 + 1) + var43;
         int var53 = var52 + 1;
         var9[var31 + 0] = var34;
         var9[var31 + 1] = var51;
         var9[var31 + 2] = var46;
         var9[var31 + 3] = var52;
         var9[var31 + 4] = var49 % var0 == 0 ? var49 - var0 : var49;
         var9[var31 + 5] = var53;
         var31 += 6;
      }

      TriangleMesh var44 = new TriangleMesh(true);
      var44.getPoints().setAll(var7);
      var44.getTexCoords().setAll(var8);
      var44.getFaces().setAll(var9);
      return var44;
   }

   static {
      SphereHelper.setSphereAccessor(new SphereHelper.SphereAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Sphere)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Sphere)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Sphere)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Sphere)var1).doComputeContains(var2, var4);
         }

         @Override
         public boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3) {
            return ((Sphere)var1).doComputeIntersects(var2, var3);
         }
      });
   }

   private static class SphereKey extends Shape3D.Key {
      final double radius;
      final int divisions;

      private SphereKey(double var1, int var3) {
         this.radius = var1;
         this.divisions = var3;
      }

      @Override
      public int hashCode() {
         long var1 = 7L;
         var1 = 31L * var1 + Double.doubleToLongBits(this.radius);
         var1 = 31L * var1 + (long)this.divisions;
         return Long.hashCode(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 == null) {
            return false;
         } else if (!(var1 instanceof Sphere.SphereKey)) {
            return false;
         } else {
            Sphere.SphereKey var2 = (Sphere.SphereKey)var1;
            return this.divisions != var2.divisions ? false : Double.compare(this.radius, var2.radius) == 0;
         }
      }
   }
}
