package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.shape.BoxHelper;
import com.sun.javafx.sg.prism.NGBox;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;

public class Box extends Shape3D {
   private TriangleMesh mesh;
   public static final double DEFAULT_SIZE = 2.0;
   private DoubleProperty depth;
   private DoubleProperty height;
   private DoubleProperty width;

   public Box() {
      this(2.0, 2.0, 2.0);
   }

   public Box(double var1, double var3, double var5) {
      BoxHelper.initHelper(this);
      this.setWidth(var1);
      this.setHeight(var3);
      this.setDepth(var5);
   }

   public final void setDepth(double var1) {
      this.depthProperty().set(var1);
   }

   public final double getDepth() {
      return this.depth == null ? 2.0 : this.depth.get();
   }

   public final DoubleProperty depthProperty() {
      if (this.depth == null) {
         this.depth = new SimpleDoubleProperty(this, "depth", 2.0) {
            public void invalidated() {
               NodeHelper.markDirty(Box.this, DirtyBits.MESH_GEOM);
               Box.this.manager.invalidateBoxMesh(Box.this.key);
               Box.this.key = null;
               NodeHelper.geomChanged(Box.this);
            }
         };
      }

      return this.depth;
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
               NodeHelper.markDirty(Box.this, DirtyBits.MESH_GEOM);
               Box.this.manager.invalidateBoxMesh(Box.this.key);
               Box.this.key = null;
               NodeHelper.geomChanged(Box.this);
            }
         };
      }

      return this.height;
   }

   public final void setWidth(double var1) {
      this.widthProperty().set(var1);
   }

   public final double getWidth() {
      return this.width == null ? 2.0 : this.width.get();
   }

   public final DoubleProperty widthProperty() {
      if (this.width == null) {
         this.width = new SimpleDoubleProperty(this, "width", 2.0) {
            public void invalidated() {
               NodeHelper.markDirty(Box.this, DirtyBits.MESH_GEOM);
               Box.this.manager.invalidateBoxMesh(Box.this.key);
               Box.this.key = null;
               NodeHelper.geomChanged(Box.this);
            }
         };
      }

      return this.width;
   }

   private NGNode doCreatePeer() {
      return new NGBox();
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.MESH_GEOM)) {
         NGBox var1 = NodeHelper.getPeer(this);
         float var2 = (float)this.getWidth();
         float var3 = (float)this.getHeight();
         float var4 = (float)this.getDepth();
         if (!(var2 < 0.0F) && !(var3 < 0.0F) && !(var4 < 0.0F)) {
            if (this.key == null) {
               this.key = new Box.BoxKey((double)var2, (double)var3, (double)var4);
            }

            this.mesh = this.manager.getBoxMesh(var2, var3, var4, this.key);
            this.mesh.updatePG();
            var1.updateMesh(this.mesh.getPGTriangleMesh());
         } else {
            var1.updateMesh(null);
         }
      }
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      float var3 = (float)this.getWidth();
      float var4 = (float)this.getHeight();
      float var5 = (float)this.getDepth();
      if (!(var3 < 0.0F) && !(var4 < 0.0F) && !(var5 < 0.0F)) {
         float var6 = var3 * 0.5F;
         float var7 = var4 * 0.5F;
         float var8 = var5 * 0.5F;
         var1 = var1.deriveWithNewBounds(-var6, -var7, -var8, var6, var7, var8);
         return var2.transform(var1, var1);
      } else {
         return var1.makeEmpty();
      }
   }

   private boolean doComputeContains(double var1, double var3) {
      double var5 = this.getWidth();
      double var7 = this.getHeight();
      return -var5 <= var1 && var1 <= var5 && -var7 <= var3 && var3 <= var7;
   }

   private boolean doComputeIntersects(PickRay var1, PickResultChooser var2) {
      double var3 = this.getWidth();
      double var5 = this.getHeight();
      double var7 = this.getDepth();
      double var9 = var3 / 2.0;
      double var11 = var5 / 2.0;
      double var13 = var7 / 2.0;
      Vec3d var15 = var1.getDirectionNoClone();
      double var16 = var15.x == 0.0 ? Double.POSITIVE_INFINITY : 1.0 / var15.x;
      double var18 = var15.y == 0.0 ? Double.POSITIVE_INFINITY : 1.0 / var15.y;
      double var20 = var15.z == 0.0 ? Double.POSITIVE_INFINITY : 1.0 / var15.z;
      Vec3d var22 = var1.getOriginNoClone();
      double var23 = var22.x;
      double var25 = var22.y;
      double var27 = var22.z;
      boolean var29 = var16 < 0.0;
      boolean var30 = var18 < 0.0;
      boolean var31 = var20 < 0.0;
      double var32 = Double.NEGATIVE_INFINITY;
      double var34 = Double.POSITIVE_INFINITY;
      int var36 = 48;
      int var37 = 48;
      if (Double.isInfinite(var16)) {
         if (!(-var9 <= var23) || !(var9 >= var23)) {
            return false;
         }
      } else {
         var32 = ((var29 ? var9 : -var9) - var23) * var16;
         var34 = ((var29 ? -var9 : var9) - var23) * var16;
         var36 = var29 ? 88 : 120;
         var37 = var29 ? 120 : 88;
      }

      if (Double.isInfinite(var18)) {
         if (!(-var11 <= var25) || !(var11 >= var25)) {
            return false;
         }
      } else {
         double var38 = ((var30 ? var11 : -var11) - var25) * var18;
         double var40 = ((var30 ? -var11 : var11) - var25) * var18;
         if (var32 > var40 || var38 > var34) {
            return false;
         }

         if (var38 > var32) {
            var36 = var30 ? 89 : 121;
            var32 = var38;
         }

         if (var40 < var34) {
            var37 = var30 ? 121 : 89;
            var34 = var40;
         }
      }

      if (Double.isInfinite(var20)) {
         if (!(-var13 <= var27) || !(var13 >= var27)) {
            return false;
         }
      } else {
         double var48 = ((var31 ? var13 : -var13) - var27) * var20;
         double var50 = ((var31 ? -var13 : var13) - var27) * var20;
         if (var32 > var50 || var48 > var34) {
            return false;
         }

         if (var48 > var32) {
            var36 = var31 ? 90 : 122;
            var32 = var48;
         }

         if (var50 < var34) {
            var37 = var31 ? 122 : 90;
            var34 = var50;
         }
      }

      int var49 = var36;
      double var39 = var32;
      CullFace var41 = this.getCullFace();
      double var42 = var1.getNearClip();
      double var44 = var1.getFarClip();
      if (var32 > var44) {
         return false;
      } else {
         if (var32 < var42 || var41 == CullFace.FRONT) {
            if (!(var34 >= var42) || !(var34 <= var44) || var41 == CullFace.BACK) {
               return false;
            }

            var49 = var37;
            var39 = var34;
         }

         if (!Double.isInfinite(var39) && !Double.isNaN(var39)) {
            if (var2 != null && var2.isCloser(var39)) {
               Point3D var46 = PickResultChooser.computePoint(var1, var39);
               Object var47 = null;
               switch (var49) {
                  case 88:
                     var47 = new Point2D(0.5 + var46.getZ() / var7, 0.5 + var46.getY() / var5);
                     break;
                  case 89:
                     var47 = new Point2D(0.5 + var46.getX() / var3, 0.5 + var46.getZ() / var7);
                     break;
                  case 90:
                     var47 = new Point2D(0.5 - var46.getX() / var3, 0.5 + var46.getY() / var5);
                     break;
                  case 120:
                     var47 = new Point2D(0.5 - var46.getZ() / var7, 0.5 + var46.getY() / var5);
                     break;
                  case 121:
                     var47 = new Point2D(0.5 + var46.getX() / var3, 0.5 - var46.getZ() / var7);
                     break;
                  case 122:
                     var47 = new Point2D(0.5 + var46.getX() / var3, 0.5 + var46.getY() / var5);
                     break;
                  default:
                     return false;
               }

               var2.offer(this, var39, -1, var46, (Point2D)var47);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   static TriangleMesh createMesh(float var0, float var1, float var2) {
      float var3 = var0 / 2.0F;
      float var4 = var1 / 2.0F;
      float var5 = var2 / 2.0F;
      float[] var6 = new float[]{
         -var3,
         -var4,
         -var5,
         var3,
         -var4,
         -var5,
         var3,
         var4,
         -var5,
         -var3,
         var4,
         -var5,
         -var3,
         -var4,
         var5,
         var3,
         -var4,
         var5,
         var3,
         var4,
         var5,
         -var3,
         var4,
         var5
      };
      float[] var7 = new float[]{0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F};
      int[] var8 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
      int[] var9 = new int[]{
         0,
         0,
         2,
         2,
         1,
         1,
         2,
         2,
         0,
         0,
         3,
         3,
         1,
         0,
         6,
         2,
         5,
         1,
         6,
         2,
         1,
         0,
         2,
         3,
         5,
         0,
         7,
         2,
         4,
         1,
         7,
         2,
         5,
         0,
         6,
         3,
         4,
         0,
         3,
         2,
         0,
         1,
         3,
         2,
         4,
         0,
         7,
         3,
         3,
         0,
         6,
         2,
         2,
         1,
         6,
         2,
         3,
         0,
         7,
         3,
         4,
         0,
         1,
         2,
         5,
         1,
         1,
         2,
         4,
         0,
         0,
         3
      };
      TriangleMesh var10 = new TriangleMesh(true);
      var10.getPoints().setAll(var6);
      var10.getTexCoords().setAll(var7);
      var10.getFaces().setAll(var9);
      var10.getFaceSmoothingGroups().setAll(var8);
      return var10;
   }

   static {
      BoxHelper.setBoxAccessor(new BoxHelper.BoxAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Box)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Box)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Box)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Box)var1).doComputeContains(var2, var4);
         }

         @Override
         public boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3) {
            return ((Box)var1).doComputeIntersects(var2, var3);
         }
      });
   }

   private static class BoxKey extends Shape3D.Key {
      final double width;
      final double height;
      final double depth;

      private BoxKey(double var1, double var3, double var5) {
         this.width = var1;
         this.height = var3;
         this.depth = var5;
      }

      @Override
      public int hashCode() {
         long var1 = 7L;
         var1 = 31L * var1 + Double.doubleToLongBits(this.depth);
         var1 = 31L * var1 + Double.doubleToLongBits(this.height);
         var1 = 31L * var1 + Double.doubleToLongBits(this.width);
         return Long.hashCode(var1);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 == null) {
            return false;
         } else if (!(var1 instanceof Box.BoxKey)) {
            return false;
         } else {
            Box.BoxKey var2 = (Box.BoxKey)var1;
            if (Double.compare(this.depth, var2.depth) != 0) {
               return false;
            } else {
               return Double.compare(this.height, var2.height) != 0 ? false : Double.compare(this.width, var2.width) == 0;
            }
         }
      }
   }
}
