package javafx.scene.transform;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

public class Shear extends Transform {
   private DoubleProperty x;
   private DoubleProperty y;
   private DoubleProperty pivotX;
   private DoubleProperty pivotY;

   public Shear() {
   }

   public Shear(double var1, double var3) {
      this.setX(var1);
      this.setY(var3);
   }

   public Shear(double var1, double var3, double var5, double var7) {
      this.setX(var1);
      this.setY(var3);
      this.setPivotX(var5);
      this.setPivotY(var7);
   }

   public final void setX(double var1) {
      this.xProperty().set(var1);
   }

   public final double getX() {
      return this.x == null ? 0.0 : this.x.get();
   }

   public final DoubleProperty xProperty() {
      if (this.x == null) {
         this.x = new DoublePropertyBase() {
            public void invalidated() {
               Shear.this.transformChanged();
            }

            public Object getBean() {
               return Shear.this;
            }

            public String getName() {
               return "x";
            }
         };
      }

      return this.x;
   }

   public final void setY(double var1) {
      this.yProperty().set(var1);
   }

   public final double getY() {
      return this.y == null ? 0.0 : this.y.get();
   }

   public final DoubleProperty yProperty() {
      if (this.y == null) {
         this.y = new DoublePropertyBase() {
            public void invalidated() {
               Shear.this.transformChanged();
            }

            public Object getBean() {
               return Shear.this;
            }

            public String getName() {
               return "y";
            }
         };
      }

      return this.y;
   }

   public final void setPivotX(double var1) {
      this.pivotXProperty().set(var1);
   }

   public final double getPivotX() {
      return this.pivotX == null ? 0.0 : this.pivotX.get();
   }

   public final DoubleProperty pivotXProperty() {
      if (this.pivotX == null) {
         this.pivotX = new DoublePropertyBase() {
            public void invalidated() {
               Shear.this.transformChanged();
            }

            public Object getBean() {
               return Shear.this;
            }

            public String getName() {
               return "pivotX";
            }
         };
      }

      return this.pivotX;
   }

   public final void setPivotY(double var1) {
      this.pivotYProperty().set(var1);
   }

   public final double getPivotY() {
      return this.pivotY == null ? 0.0 : this.pivotY.get();
   }

   public final DoubleProperty pivotYProperty() {
      if (this.pivotY == null) {
         this.pivotY = new DoublePropertyBase() {
            public void invalidated() {
               Shear.this.transformChanged();
            }

            public Object getBean() {
               return Shear.this;
            }

            public String getName() {
               return "pivotY";
            }
         };
      }

      return this.pivotY;
   }

   @Override
   public double getMxy() {
      return this.getX();
   }

   @Override
   public double getMyx() {
      return this.getY();
   }

   @Override
   public double getTx() {
      return -this.getX() * this.getPivotY();
   }

   @Override
   public double getTy() {
      return -this.getY() * this.getPivotX();
   }

   @Override
   boolean computeIs2D() {
      return true;
   }

   @Override
   boolean computeIsIdentity() {
      return this.getX() == 0.0 && this.getY() == 0.0;
   }

   @Override
   void fill2DArray(double[] var1) {
      double var2 = this.getX();
      double var4 = this.getY();
      var1[0] = 1.0;
      var1[1] = var2;
      var1[2] = -var2 * this.getPivotY();
      var1[3] = var4;
      var1[4] = 1.0;
      var1[5] = -var4 * this.getPivotX();
   }

   @Override
   void fill3DArray(double[] var1) {
      double var2 = this.getX();
      double var4 = this.getY();
      var1[0] = 1.0;
      var1[1] = var2;
      var1[2] = 0.0;
      var1[3] = -var2 * this.getPivotY();
      var1[4] = var4;
      var1[5] = 1.0;
      var1[6] = 0.0;
      var1[7] = -var4 * this.getPivotX();
      var1[8] = 0.0;
      var1[9] = 0.0;
      var1[10] = 1.0;
      var1[11] = 0.0;
   }

   @Override
   public Transform createConcatenation(Transform var1) {
      if (var1 instanceof Affine) {
         Affine var22 = (Affine)var1.clone();
         var22.prepend(this);
         return var22;
      } else {
         double var2 = this.getX();
         double var4 = this.getY();
         double var6 = var1.getMxx();
         double var8 = var1.getMxy();
         double var10 = var1.getMxz();
         double var12 = var1.getTx();
         double var14 = var1.getMyx();
         double var16 = var1.getMyy();
         double var18 = var1.getMyz();
         double var20 = var1.getTy();
         return new Affine(
            var6 + var2 * var14,
            var8 + var2 * var16,
            var10 + var2 * var18,
            var12 + var2 * var20 - var2 * this.getPivotY(),
            var4 * var6 + var14,
            var4 * var8 + var16,
            var4 * var10 + var18,
            var4 * var12 + var20 - var4 * this.getPivotX(),
            var1.getMzx(),
            var1.getMzy(),
            var1.getMzz(),
            var1.getTz()
         );
      }
   }

   @Override
   public Transform createInverse() {
      double var1 = this.getX();
      double var3 = this.getY();
      if (var3 == 0.0) {
         return new Shear(-var1, 0.0, 0.0, this.getPivotY());
      } else if (var1 == 0.0) {
         return new Shear(0.0, -var3, this.getPivotX(), 0.0);
      } else {
         double var5 = this.getPivotX();
         double var7 = this.getPivotY();
         double var9 = 1.0 / (1.0 - var1 * var3);
         return new Affine(
            var9,
            -var1 * var9,
            0.0,
            var1 * (var7 - var3 * var5) * var9,
            -var3 * var9,
            1.0 + var1 * var3 * var9,
            0.0,
            var3 * var5 + var3 * (var1 * var3 * var5 - var1 * var7) * var9,
            0.0,
            0.0,
            1.0,
            0.0
         );
      }
   }

   public Shear clone() {
      return new Shear(this.getX(), this.getY(), this.getPivotX(), this.getPivotY());
   }

   @Override
   public Point2D transform(double var1, double var3) {
      double var5 = this.getX();
      double var7 = this.getY();
      return new Point2D(var1 + var5 * var3 - var5 * this.getPivotY(), var7 * var1 + var3 - var7 * this.getPivotX());
   }

   @Override
   public Point3D transform(double var1, double var3, double var5) {
      double var7 = this.getX();
      double var9 = this.getY();
      return new Point3D(var1 + var7 * var3 - var7 * this.getPivotY(), var9 * var1 + var3 - var9 * this.getPivotX(), var5);
   }

   @Override
   void transform2DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) {
      double var6 = this.getX();
      double var8 = this.getY();
      double var10 = this.getPivotX();
      double var12 = this.getPivotY();

      while (--var5 >= 0) {
         double var14 = var1[var2++];
         double var16 = var1[var2++];
         var3[var4++] = var14 + var6 * var16 - var6 * var12;
         var3[var4++] = var8 * var14 + var16 - var8 * var10;
      }
   }

   @Override
   void transform3DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) {
      double var6 = this.getX();
      double var8 = this.getY();
      double var10 = this.getPivotX();
      double var12 = this.getPivotY();

      while (--var5 >= 0) {
         double var14 = var1[var2++];
         double var16 = var1[var2++];
         var3[var4++] = var14 + var6 * var16 - var6 * var12;
         var3[var4++] = var8 * var14 + var16 - var8 * var10;
         var3[var4++] = var1[var2++];
      }
   }

   @Override
   public Point2D deltaTransform(double var1, double var3) {
      return new Point2D(var1 + this.getX() * var3, this.getY() * var1 + var3);
   }

   @Override
   public Point3D deltaTransform(double var1, double var3, double var5) {
      return new Point3D(var1 + this.getX() * var3, this.getY() * var1 + var3, var5);
   }

   @Override
   public Point2D inverseTransform(double var1, double var3) throws NonInvertibleTransformException {
      double var5 = this.getX();
      double var7 = this.getY();
      if (var7 == 0.0) {
         double var11 = -this.getX();
         return new Point2D(var1 + var11 * var3 - var11 * this.getPivotY(), var3);
      } else if (var5 == 0.0) {
         double var9 = -this.getY();
         return new Point2D(var1, var9 * var1 + var3 - var9 * this.getPivotX());
      } else {
         return super.inverseTransform(var1, var3);
      }
   }

   @Override
   public Point3D inverseTransform(double var1, double var3, double var5) throws NonInvertibleTransformException {
      double var7 = this.getX();
      double var9 = this.getY();
      if (var9 == 0.0) {
         double var13 = -this.getX();
         return new Point3D(var1 + var13 * var3 - var13 * this.getPivotY(), var3, var5);
      } else if (var7 == 0.0) {
         double var11 = -this.getY();
         return new Point3D(var1, var11 * var1 + var3 - var11 * this.getPivotX(), var5);
      } else {
         return super.inverseTransform(var1, var3, var5);
      }
   }

   @Override
   void inverseTransform2DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) throws NonInvertibleTransformException {
      double var6 = this.getPivotX();
      double var8 = this.getPivotY();
      double var10 = this.getX();
      double var12 = this.getY();
      if (var12 == 0.0) {
         double var24 = -var10;

         while (--var5 >= 0) {
            double var25 = var1[var2++];
            double var26 = var1[var2++];
            var3[var4++] = var25 + var24 * var26 - var24 * var8;
            var3[var4++] = var26;
         }
      } else if (var10 != 0.0) {
         super.inverseTransform2DPointsImpl(var1, var2, var3, var4, var5);
      } else {
         double var14 = -var12;

         while (--var5 >= 0) {
            double var16 = var1[var2++];
            double var18 = var1[var2++];
            var3[var4++] = var16;
            var3[var4++] = var14 * var16 + var18 - var14 * var6;
         }
      }
   }

   @Override
   void inverseTransform3DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) throws NonInvertibleTransformException {
      double var6 = this.getPivotX();
      double var8 = this.getPivotY();
      double var10 = this.getX();
      double var12 = this.getY();
      if (var12 == 0.0) {
         double var28 = -var10;

         while (--var5 >= 0) {
            double var29 = var1[var2++];
            double var30 = var1[var2++];
            var3[var4++] = var29 + var28 * var30 - var28 * var8;
            var3[var4++] = var30;
            var3[var4++] = var1[var2++];
         }
      } else if (var10 != 0.0) {
         super.inverseTransform3DPointsImpl(var1, var2, var3, var4, var5);
      } else {
         double var14 = -var12;

         while (--var5 >= 0) {
            double var16 = var1[var2++];
            double var18 = var1[var2++];
            var3[var4++] = var16;
            var3[var4++] = var14 * var16 + var18 - var14 * var6;
            var3[var4++] = var1[var2++];
         }
      }
   }

   @Override
   public Point2D inverseDeltaTransform(double var1, double var3) throws NonInvertibleTransformException {
      double var5 = this.getX();
      double var7 = this.getY();
      if (var7 == 0.0) {
         return new Point2D(var1 - this.getX() * var3, var3);
      } else {
         return var5 == 0.0 ? new Point2D(var1, -this.getY() * var1 + var3) : super.inverseDeltaTransform(var1, var3);
      }
   }

   @Override
   public Point3D inverseDeltaTransform(double var1, double var3, double var5) throws NonInvertibleTransformException {
      double var7 = this.getX();
      double var9 = this.getY();
      if (var9 == 0.0) {
         return new Point3D(var1 - this.getX() * var3, var3, var5);
      } else {
         return var7 == 0.0 ? new Point3D(var1, -this.getY() * var1 + var3, var5) : super.inverseDeltaTransform(var1, var3, var5);
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Shear [");
      var1.append("x=").append(this.getX());
      var1.append(", y=").append(this.getY());
      var1.append(", pivotX=").append(this.getPivotX());
      var1.append(", pivotY=").append(this.getPivotY());
      return var1.append("]").toString();
   }

   @Override
   void apply(Affine3D var1) {
      if (this.getPivotX() == 0.0 && this.getPivotY() == 0.0) {
         var1.shear(this.getX(), this.getY());
      } else {
         var1.translate(this.getPivotX(), this.getPivotY());
         var1.shear(this.getX(), this.getY());
         var1.translate(-this.getPivotX(), -this.getPivotY());
      }
   }

   @Override
   BaseTransform derive(BaseTransform var1) {
      return var1.deriveWithConcatenation(1.0, this.getY(), this.getX(), 1.0, this.getTx(), this.getTy());
   }

   @Override
   void validate() {
      this.getX();
      this.getPivotX();
      this.getY();
      this.getPivotY();
   }

   @Override
   void appendTo(Affine var1) {
      var1.appendShear(this.getX(), this.getY(), this.getPivotX(), this.getPivotY());
   }

   @Override
   void prependTo(Affine var1) {
      var1.prependShear(this.getX(), this.getY(), this.getPivotX(), this.getPivotY());
   }
}
