package javafx.scene.transform;

import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geometry.BoundsUtils;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.transform.TransformHelper;
import com.sun.javafx.scene.transform.TransformUtils;
import com.sun.javafx.util.WeakReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;

public abstract class Transform implements Cloneable, EventTarget {
   private SoftReference<Transform> inverseCache = null;
   private WeakReferenceQueue nodes = new WeakReferenceQueue();
   private Transform.LazyBooleanProperty type2D;
   private Transform.LazyBooleanProperty identity;
   private EventHandlerManager internalEventDispatcher;
   private ObjectProperty<EventHandler<? super TransformChangedEvent>> onTransformChanged;

   public static Affine affine(double var0, double var2, double var4, double var6, double var8, double var10) {
      Affine var12 = new Affine();
      var12.setMxx(var0);
      var12.setMxy(var4);
      var12.setTx(var8);
      var12.setMyx(var2);
      var12.setMyy(var6);
      var12.setTy(var10);
      return var12;
   }

   public static Affine affine(
      double var0,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      double var20,
      double var22
   ) {
      Affine var24 = new Affine();
      var24.setMxx(var0);
      var24.setMxy(var2);
      var24.setMxz(var4);
      var24.setTx(var6);
      var24.setMyx(var8);
      var24.setMyy(var10);
      var24.setMyz(var12);
      var24.setTy(var14);
      var24.setMzx(var16);
      var24.setMzy(var18);
      var24.setMzz(var20);
      var24.setTz(var22);
      return var24;
   }

   public static Translate translate(double var0, double var2) {
      Translate var4 = new Translate();
      var4.setX(var0);
      var4.setY(var2);
      return var4;
   }

   public static Rotate rotate(double var0, double var2, double var4) {
      Rotate var6 = new Rotate();
      var6.setAngle(var0);
      var6.setPivotX(var2);
      var6.setPivotY(var4);
      return var6;
   }

   public static Scale scale(double var0, double var2) {
      Scale var4 = new Scale();
      var4.setX(var0);
      var4.setY(var2);
      return var4;
   }

   public static Scale scale(double var0, double var2, double var4, double var6) {
      Scale var8 = new Scale();
      var8.setX(var0);
      var8.setY(var2);
      var8.setPivotX(var4);
      var8.setPivotY(var6);
      return var8;
   }

   public static Shear shear(double var0, double var2) {
      Shear var4 = new Shear();
      var4.setX(var0);
      var4.setY(var2);
      return var4;
   }

   public static Shear shear(double var0, double var2, double var4, double var6) {
      Shear var8 = new Shear();
      var8.setX(var0);
      var8.setY(var2);
      var8.setPivotX(var4);
      var8.setPivotY(var6);
      return var8;
   }

   public double getMxx() {
      return 1.0;
   }

   public double getMxy() {
      return 0.0;
   }

   public double getMxz() {
      return 0.0;
   }

   public double getTx() {
      return 0.0;
   }

   public double getMyx() {
      return 0.0;
   }

   public double getMyy() {
      return 1.0;
   }

   public double getMyz() {
      return 0.0;
   }

   public double getTy() {
      return 0.0;
   }

   public double getMzx() {
      return 0.0;
   }

   public double getMzy() {
      return 0.0;
   }

   public double getMzz() {
      return 1.0;
   }

   public double getTz() {
      return 0.0;
   }

   public double getElement(MatrixType var1, int var2, int var3) {
      if (var2 >= 0 && var2 < var1.rows() && var3 >= 0 && var3 < var1.columns()) {
         switch (var1) {
            case MT_2D_2x3:
            case MT_2D_3x3:
               if (!this.isType2D()) {
                  throw new IllegalArgumentException("Cannot access 2D matrix of a 3D transform");
               }

               switch (var2) {
                  case 0:
                     switch (var3) {
                        case 0:
                           return this.getMxx();
                        case 1:
                           return this.getMxy();
                        case 2:
                           return this.getTx();
                     }
                  case 1:
                     switch (var3) {
                        case 0:
                           return this.getMyx();
                        case 1:
                           return this.getMyy();
                        case 2:
                           return this.getTy();
                     }
                  case 2:
                     switch (var3) {
                        case 0:
                           return 0.0;
                        case 1:
                           return 0.0;
                        case 2:
                           return 1.0;
                        default:
                           throw new InternalError("Unsupported matrix type " + var1);
                     }
                  default:
                     throw new InternalError("Unsupported matrix type " + var1);
               }
            case MT_3D_3x4:
            case MT_3D_4x4:
               switch (var2) {
                  case 0:
                     switch (var3) {
                        case 0:
                           return this.getMxx();
                        case 1:
                           return this.getMxy();
                        case 2:
                           return this.getMxz();
                        case 3:
                           return this.getTx();
                     }
                  case 1:
                     switch (var3) {
                        case 0:
                           return this.getMyx();
                        case 1:
                           return this.getMyy();
                        case 2:
                           return this.getMyz();
                        case 3:
                           return this.getTy();
                     }
                  case 2:
                     switch (var3) {
                        case 0:
                           return this.getMzx();
                        case 1:
                           return this.getMzy();
                        case 2:
                           return this.getMzz();
                        case 3:
                           return this.getTz();
                     }
                  case 3:
                     switch (var3) {
                        case 0:
                           return 0.0;
                        case 1:
                           return 0.0;
                        case 2:
                           return 0.0;
                        case 3:
                           return 1.0;
                     }
               }
         }

         throw new InternalError("Unsupported matrix type " + var1);
      } else {
         throw new IndexOutOfBoundsException("Index outside of affine matrix " + var1 + ": [" + var2 + ", " + var3 + "]");
      }
   }

   boolean computeIs2D() {
      return this.getMxz() == 0.0 && this.getMzx() == 0.0 && this.getMzy() == 0.0 && this.getMzz() == 1.0 && this.getTz() == 0.0;
   }

   boolean computeIsIdentity() {
      return this.getMxx() == 1.0
         && this.getMxy() == 0.0
         && this.getMxz() == 0.0
         && this.getTx() == 0.0
         && this.getMyx() == 0.0
         && this.getMyy() == 1.0
         && this.getMyz() == 0.0
         && this.getTy() == 0.0
         && this.getMzx() == 0.0
         && this.getMzy() == 0.0
         && this.getMzz() == 1.0
         && this.getTz() == 0.0;
   }

   public double determinant() {
      double var1 = this.getMyx();
      double var3 = this.getMyy();
      double var5 = this.getMyz();
      double var7 = this.getMzx();
      double var9 = this.getMzy();
      double var11 = this.getMzz();
      return this.getMxx() * (var3 * var11 - var9 * var5) + this.getMxy() * (var5 * var7 - var11 * var1) + this.getMxz() * (var1 * var9 - var7 * var3);
   }

   public final boolean isType2D() {
      return this.type2D == null ? this.computeIs2D() : this.type2D.get();
   }

   public final ReadOnlyBooleanProperty type2DProperty() {
      if (this.type2D == null) {
         this.type2D = new Transform.LazyBooleanProperty() {
            @Override
            protected boolean computeValue() {
               return Transform.this.computeIs2D();
            }

            public Object getBean() {
               return Transform.this;
            }

            public String getName() {
               return "type2D";
            }
         };
      }

      return this.type2D;
   }

   public final boolean isIdentity() {
      return this.identity == null ? this.computeIsIdentity() : this.identity.get();
   }

   public final ReadOnlyBooleanProperty identityProperty() {
      if (this.identity == null) {
         this.identity = new Transform.LazyBooleanProperty() {
            @Override
            protected boolean computeValue() {
               return Transform.this.computeIsIdentity();
            }

            public Object getBean() {
               return Transform.this;
            }

            public String getName() {
               return "identity";
            }
         };
      }

      return this.identity;
   }

   private double transformDiff(Transform var1, double var2, double var4) {
      Point2D var6 = this.transform(var2, var4);
      Point2D var7 = var1.transform(var2, var4);
      return var6.distance(var7);
   }

   private double transformDiff(Transform var1, double var2, double var4, double var6) {
      Point3D var8 = this.transform(var2, var4, var6);
      Point3D var9 = var1.transform(var2, var4, var6);
      return var8.distance(var9);
   }

   public boolean similarTo(Transform var1, Bounds var2, double var3) {
      if (this.isType2D() && var1.isType2D()) {
         double var14 = var2.getMinX();
         double var23 = var2.getMinY();
         if (this.transformDiff(var1, var14, var23) > var3) {
            return false;
         } else {
            var23 = var2.getMaxY();
            if (this.transformDiff(var1, var14, var23) > var3) {
               return false;
            } else {
               var14 = var2.getMaxX();
               var23 = var2.getMinY();
               if (this.transformDiff(var1, var14, var23) > var3) {
                  return false;
               } else {
                  var23 = var2.getMaxY();
                  return !(this.transformDiff(var1, var14, var23) > var3);
               }
            }
         }
      } else {
         double var5 = var2.getMinX();
         double var7 = var2.getMinY();
         double var9 = var2.getMinZ();
         if (this.transformDiff(var1, var5, var7, var9) > var3) {
            return false;
         } else {
            var7 = var2.getMaxY();
            if (this.transformDiff(var1, var5, var7, var9) > var3) {
               return false;
            } else {
               var5 = var2.getMaxX();
               var7 = var2.getMinY();
               if (this.transformDiff(var1, var5, var7, var9) > var3) {
                  return false;
               } else {
                  var7 = var2.getMaxY();
                  if (this.transformDiff(var1, var5, var7, var9) > var3) {
                     return false;
                  } else {
                     if (var2.getDepth() != 0.0) {
                        var5 = var2.getMinX();
                        var7 = var2.getMinY();
                        var9 = var2.getMaxZ();
                        if (this.transformDiff(var1, var5, var7, var9) > var3) {
                           return false;
                        }

                        var7 = var2.getMaxY();
                        if (this.transformDiff(var1, var5, var7, var9) > var3) {
                           return false;
                        }

                        var5 = var2.getMaxX();
                        var7 = var2.getMinY();
                        if (this.transformDiff(var1, var5, var7, var9) > var3) {
                           return false;
                        }

                        var7 = var2.getMaxY();
                        if (this.transformDiff(var1, var5, var7, var9) > var3) {
                           return false;
                        }
                     }

                     return true;
                  }
               }
            }
         }
      }
   }

   void fill2DArray(double[] var1) {
      var1[0] = this.getMxx();
      var1[1] = this.getMxy();
      var1[2] = this.getTx();
      var1[3] = this.getMyx();
      var1[4] = this.getMyy();
      var1[5] = this.getTy();
   }

   void fill3DArray(double[] var1) {
      var1[0] = this.getMxx();
      var1[1] = this.getMxy();
      var1[2] = this.getMxz();
      var1[3] = this.getTx();
      var1[4] = this.getMyx();
      var1[5] = this.getMyy();
      var1[6] = this.getMyz();
      var1[7] = this.getTy();
      var1[8] = this.getMzx();
      var1[9] = this.getMzy();
      var1[10] = this.getMzz();
      var1[11] = this.getTz();
   }

   public double[] toArray(MatrixType var1, double[] var2) {
      this.checkRequestedMAT(var1);
      if (var2 == null || var2.length < var1.elements()) {
         var2 = new double[var1.elements()];
      }

      switch (var1) {
         case MT_2D_3x3:
            var2[6] = 0.0;
            var2[7] = 0.0;
            var2[8] = 1.0;
         case MT_2D_2x3:
            this.fill2DArray(var2);
            break;
         case MT_3D_4x4:
            var2[12] = 0.0;
            var2[13] = 0.0;
            var2[14] = 0.0;
            var2[15] = 1.0;
         case MT_3D_3x4:
            this.fill3DArray(var2);
            break;
         default:
            throw new InternalError("Unsupported matrix type " + var1);
      }

      return var2;
   }

   public double[] toArray(MatrixType var1) {
      return this.toArray(var1, null);
   }

   public double[] row(MatrixType var1, int var2, double[] var3) {
      this.checkRequestedMAT(var1);
      if (var2 >= 0 && var2 < var1.rows()) {
         if (var3 == null || var3.length < var1.columns()) {
            var3 = new double[var1.columns()];
         }

         switch (var1) {
            case MT_2D_2x3:
            case MT_2D_3x3:
               switch (var2) {
                  case 0:
                     var3[0] = this.getMxx();
                     var3[1] = this.getMxy();
                     var3[2] = this.getTx();
                     return var3;
                  case 1:
                     var3[0] = this.getMyx();
                     var3[1] = this.getMyy();
                     var3[2] = this.getTy();
                     return var3;
                  case 2:
                     var3[0] = 0.0;
                     var3[1] = 0.0;
                     var3[2] = 1.0;
                     return var3;
                  default:
                     return var3;
               }
            case MT_3D_3x4:
            case MT_3D_4x4:
               switch (var2) {
                  case 0:
                     var3[0] = this.getMxx();
                     var3[1] = this.getMxy();
                     var3[2] = this.getMxz();
                     var3[3] = this.getTx();
                     return var3;
                  case 1:
                     var3[0] = this.getMyx();
                     var3[1] = this.getMyy();
                     var3[2] = this.getMyz();
                     var3[3] = this.getTy();
                     return var3;
                  case 2:
                     var3[0] = this.getMzx();
                     var3[1] = this.getMzy();
                     var3[2] = this.getMzz();
                     var3[3] = this.getTz();
                     return var3;
                  case 3:
                     var3[0] = 0.0;
                     var3[1] = 0.0;
                     var3[2] = 0.0;
                     var3[3] = 1.0;
                     return var3;
                  default:
                     return var3;
               }
            default:
               throw new InternalError("Unsupported row " + var2 + " of " + var1);
         }
      } else {
         throw new IndexOutOfBoundsException("Cannot get row " + var2 + " from " + var1);
      }
   }

   public double[] row(MatrixType var1, int var2) {
      return this.row(var1, var2, null);
   }

   public double[] column(MatrixType var1, int var2, double[] var3) {
      this.checkRequestedMAT(var1);
      if (var2 >= 0 && var2 < var1.columns()) {
         if (var3 == null || var3.length < var1.rows()) {
            var3 = new double[var1.rows()];
         }

         switch (var1) {
            case MT_2D_2x3:
               switch (var2) {
                  case 0:
                     var3[0] = this.getMxx();
                     var3[1] = this.getMyx();
                     return var3;
                  case 1:
                     var3[0] = this.getMxy();
                     var3[1] = this.getMyy();
                     return var3;
                  case 2:
                     var3[0] = this.getTx();
                     var3[1] = this.getTy();
                     return var3;
                  default:
                     return var3;
               }
            case MT_2D_3x3:
               switch (var2) {
                  case 0:
                     var3[0] = this.getMxx();
                     var3[1] = this.getMyx();
                     var3[2] = 0.0;
                     return var3;
                  case 1:
                     var3[0] = this.getMxy();
                     var3[1] = this.getMyy();
                     var3[2] = 0.0;
                     return var3;
                  case 2:
                     var3[0] = this.getTx();
                     var3[1] = this.getTy();
                     var3[2] = 1.0;
                     return var3;
                  default:
                     return var3;
               }
            case MT_3D_3x4:
               switch (var2) {
                  case 0:
                     var3[0] = this.getMxx();
                     var3[1] = this.getMyx();
                     var3[2] = this.getMzx();
                     return var3;
                  case 1:
                     var3[0] = this.getMxy();
                     var3[1] = this.getMyy();
                     var3[2] = this.getMzy();
                     return var3;
                  case 2:
                     var3[0] = this.getMxz();
                     var3[1] = this.getMyz();
                     var3[2] = this.getMzz();
                     return var3;
                  case 3:
                     var3[0] = this.getTx();
                     var3[1] = this.getTy();
                     var3[2] = this.getTz();
                     return var3;
                  default:
                     return var3;
               }
            case MT_3D_4x4:
               switch (var2) {
                  case 0:
                     var3[0] = this.getMxx();
                     var3[1] = this.getMyx();
                     var3[2] = this.getMzx();
                     var3[3] = 0.0;
                     return var3;
                  case 1:
                     var3[0] = this.getMxy();
                     var3[1] = this.getMyy();
                     var3[2] = this.getMzy();
                     var3[3] = 0.0;
                     return var3;
                  case 2:
                     var3[0] = this.getMxz();
                     var3[1] = this.getMyz();
                     var3[2] = this.getMzz();
                     var3[3] = 0.0;
                     return var3;
                  case 3:
                     var3[0] = this.getTx();
                     var3[1] = this.getTy();
                     var3[2] = this.getTz();
                     var3[3] = 1.0;
                     return var3;
                  default:
                     return var3;
               }
            default:
               throw new InternalError("Unsupported column " + var2 + " of " + var1);
         }
      } else {
         throw new IndexOutOfBoundsException("Cannot get row " + var2 + " from " + var1);
      }
   }

   public double[] column(MatrixType var1, int var2) {
      return this.column(var1, var2, null);
   }

   public Transform createConcatenation(Transform var1) {
      double var2 = var1.getMxx();
      double var4 = var1.getMxy();
      double var6 = var1.getMxz();
      double var8 = var1.getTx();
      double var10 = var1.getMyx();
      double var12 = var1.getMyy();
      double var14 = var1.getMyz();
      double var16 = var1.getTy();
      double var18 = var1.getMzx();
      double var20 = var1.getMzy();
      double var22 = var1.getMzz();
      double var24 = var1.getTz();
      return new Affine(
         this.getMxx() * var2 + this.getMxy() * var10 + this.getMxz() * var18,
         this.getMxx() * var4 + this.getMxy() * var12 + this.getMxz() * var20,
         this.getMxx() * var6 + this.getMxy() * var14 + this.getMxz() * var22,
         this.getMxx() * var8 + this.getMxy() * var16 + this.getMxz() * var24 + this.getTx(),
         this.getMyx() * var2 + this.getMyy() * var10 + this.getMyz() * var18,
         this.getMyx() * var4 + this.getMyy() * var12 + this.getMyz() * var20,
         this.getMyx() * var6 + this.getMyy() * var14 + this.getMyz() * var22,
         this.getMyx() * var8 + this.getMyy() * var16 + this.getMyz() * var24 + this.getTy(),
         this.getMzx() * var2 + this.getMzy() * var10 + this.getMzz() * var18,
         this.getMzx() * var4 + this.getMzy() * var12 + this.getMzz() * var20,
         this.getMzx() * var6 + this.getMzy() * var14 + this.getMzz() * var22,
         this.getMzx() * var8 + this.getMzy() * var16 + this.getMzz() * var24 + this.getTz()
      );
   }

   public Transform createInverse() throws NonInvertibleTransformException {
      return this.getInverseCache().clone();
   }

   public Transform clone() {
      return TransformUtils.immutableTransform(this);
   }

   public Point2D transform(double var1, double var3) {
      this.ensureCanTransform2DPoint();
      return new Point2D(this.getMxx() * var1 + this.getMxy() * var3 + this.getTx(), this.getMyx() * var1 + this.getMyy() * var3 + this.getTy());
   }

   public Point2D transform(Point2D var1) {
      return this.transform(var1.getX(), var1.getY());
   }

   public Point3D transform(double var1, double var3, double var5) {
      return new Point3D(
         this.getMxx() * var1 + this.getMxy() * var3 + this.getMxz() * var5 + this.getTx(),
         this.getMyx() * var1 + this.getMyy() * var3 + this.getMyz() * var5 + this.getTy(),
         this.getMzx() * var1 + this.getMzy() * var3 + this.getMzz() * var5 + this.getTz()
      );
   }

   public Point3D transform(Point3D var1) {
      return this.transform(var1.getX(), var1.getY(), var1.getZ());
   }

   public Bounds transform(Bounds var1) {
      if (this.isType2D() && var1.getMinZ() == 0.0 && var1.getMaxZ() == 0.0) {
         Point2D var10 = this.transform(var1.getMinX(), var1.getMinY());
         Point2D var11 = this.transform(var1.getMaxX(), var1.getMinY());
         Point2D var12 = this.transform(var1.getMaxX(), var1.getMaxY());
         Point2D var13 = this.transform(var1.getMinX(), var1.getMaxY());
         return BoundsUtils.createBoundingBox(var10, var11, var12, var13);
      } else {
         Point3D var2 = this.transform(var1.getMinX(), var1.getMinY(), var1.getMinZ());
         Point3D var3 = this.transform(var1.getMinX(), var1.getMinY(), var1.getMaxZ());
         Point3D var4 = this.transform(var1.getMinX(), var1.getMaxY(), var1.getMinZ());
         Point3D var5 = this.transform(var1.getMinX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var6 = this.transform(var1.getMaxX(), var1.getMaxY(), var1.getMinZ());
         Point3D var7 = this.transform(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var8 = this.transform(var1.getMaxX(), var1.getMinY(), var1.getMinZ());
         Point3D var9 = this.transform(var1.getMaxX(), var1.getMinY(), var1.getMaxZ());
         return BoundsUtils.createBoundingBox(var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   void transform2DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) {
      double var6 = this.getMxx();
      double var8 = this.getMxy();
      double var10 = this.getTx();
      double var12 = this.getMyx();
      double var14 = this.getMyy();
      double var16 = this.getTy();

      while (--var5 >= 0) {
         double var18 = var1[var2++];
         double var20 = var1[var2++];
         var3[var4++] = var6 * var18 + var8 * var20 + var10;
         var3[var4++] = var12 * var18 + var14 * var20 + var16;
      }
   }

   void transform3DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) {
      double var6 = this.getMxx();
      double var8 = this.getMxy();
      double var10 = this.getMxz();
      double var12 = this.getTx();
      double var14 = this.getMyx();
      double var16 = this.getMyy();
      double var18 = this.getMyz();
      double var20 = this.getTy();
      double var22 = this.getMzx();
      double var24 = this.getMzy();
      double var26 = this.getMzz();
      double var28 = this.getTz();

      while (--var5 >= 0) {
         double var30 = var1[var2++];
         double var32 = var1[var2++];
         double var34 = var1[var2++];
         var3[var4++] = var6 * var30 + var8 * var32 + var10 * var34 + var12;
         var3[var4++] = var14 * var30 + var16 * var32 + var18 * var34 + var20;
         var3[var4++] = var22 * var30 + var24 * var32 + var26 * var34 + var28;
      }
   }

   public void transform2DPoints(double[] var1, int var2, double[] var3, int var4, int var5) {
      if (var1 == null || var3 == null) {
         throw new NullPointerException();
      } else if (!this.isType2D()) {
         throw new IllegalStateException("Cannot transform 2D points with a 3D transform");
      } else {
         var2 = this.getFixedSrcOffset(var1, var2, var3, var4, var5, 2);
         this.transform2DPointsImpl(var1, var2, var3, var4, var5);
      }
   }

   public void transform3DPoints(double[] var1, int var2, double[] var3, int var4, int var5) {
      if (var1 != null && var3 != null) {
         var2 = this.getFixedSrcOffset(var1, var2, var3, var4, var5, 3);
         this.transform3DPointsImpl(var1, var2, var3, var4, var5);
      } else {
         throw new NullPointerException();
      }
   }

   public Point2D deltaTransform(double var1, double var3) {
      this.ensureCanTransform2DPoint();
      return new Point2D(this.getMxx() * var1 + this.getMxy() * var3, this.getMyx() * var1 + this.getMyy() * var3);
   }

   public Point2D deltaTransform(Point2D var1) {
      return this.deltaTransform(var1.getX(), var1.getY());
   }

   public Point3D deltaTransform(double var1, double var3, double var5) {
      return new Point3D(
         this.getMxx() * var1 + this.getMxy() * var3 + this.getMxz() * var5,
         this.getMyx() * var1 + this.getMyy() * var3 + this.getMyz() * var5,
         this.getMzx() * var1 + this.getMzy() * var3 + this.getMzz() * var5
      );
   }

   public Point3D deltaTransform(Point3D var1) {
      return this.deltaTransform(var1.getX(), var1.getY(), var1.getZ());
   }

   public Point2D inverseTransform(double var1, double var3) throws NonInvertibleTransformException {
      this.ensureCanTransform2DPoint();
      return this.getInverseCache().transform(var1, var3);
   }

   public Point2D inverseTransform(Point2D var1) throws NonInvertibleTransformException {
      return this.inverseTransform(var1.getX(), var1.getY());
   }

   public Point3D inverseTransform(double var1, double var3, double var5) throws NonInvertibleTransformException {
      return this.getInverseCache().transform(var1, var3, var5);
   }

   public Point3D inverseTransform(Point3D var1) throws NonInvertibleTransformException {
      return this.inverseTransform(var1.getX(), var1.getY(), var1.getZ());
   }

   public Bounds inverseTransform(Bounds var1) throws NonInvertibleTransformException {
      if (this.isType2D() && var1.getMinZ() == 0.0 && var1.getMaxZ() == 0.0) {
         Point2D var10 = this.inverseTransform(var1.getMinX(), var1.getMinY());
         Point2D var11 = this.inverseTransform(var1.getMaxX(), var1.getMinY());
         Point2D var12 = this.inverseTransform(var1.getMaxX(), var1.getMaxY());
         Point2D var13 = this.inverseTransform(var1.getMinX(), var1.getMaxY());
         return BoundsUtils.createBoundingBox(var10, var11, var12, var13);
      } else {
         Point3D var2 = this.inverseTransform(var1.getMinX(), var1.getMinY(), var1.getMinZ());
         Point3D var3 = this.inverseTransform(var1.getMinX(), var1.getMinY(), var1.getMaxZ());
         Point3D var4 = this.inverseTransform(var1.getMinX(), var1.getMaxY(), var1.getMinZ());
         Point3D var5 = this.inverseTransform(var1.getMinX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var6 = this.inverseTransform(var1.getMaxX(), var1.getMaxY(), var1.getMinZ());
         Point3D var7 = this.inverseTransform(var1.getMaxX(), var1.getMaxY(), var1.getMaxZ());
         Point3D var8 = this.inverseTransform(var1.getMaxX(), var1.getMinY(), var1.getMinZ());
         Point3D var9 = this.inverseTransform(var1.getMaxX(), var1.getMinY(), var1.getMaxZ());
         return BoundsUtils.createBoundingBox(var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   void inverseTransform2DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) throws NonInvertibleTransformException {
      this.getInverseCache().transform2DPointsImpl(var1, var2, var3, var4, var5);
   }

   void inverseTransform3DPointsImpl(double[] var1, int var2, double[] var3, int var4, int var5) throws NonInvertibleTransformException {
      this.getInverseCache().transform3DPointsImpl(var1, var2, var3, var4, var5);
   }

   public void inverseTransform2DPoints(double[] var1, int var2, double[] var3, int var4, int var5) throws NonInvertibleTransformException {
      if (var1 == null || var3 == null) {
         throw new NullPointerException();
      } else if (!this.isType2D()) {
         throw new IllegalStateException("Cannot transform 2D points with a 3D transform");
      } else {
         var2 = this.getFixedSrcOffset(var1, var2, var3, var4, var5, 2);
         this.inverseTransform2DPointsImpl(var1, var2, var3, var4, var5);
      }
   }

   public void inverseTransform3DPoints(double[] var1, int var2, double[] var3, int var4, int var5) throws NonInvertibleTransformException {
      if (var1 != null && var3 != null) {
         var2 = this.getFixedSrcOffset(var1, var2, var3, var4, var5, 3);
         this.inverseTransform3DPointsImpl(var1, var2, var3, var4, var5);
      } else {
         throw new NullPointerException();
      }
   }

   public Point2D inverseDeltaTransform(double var1, double var3) throws NonInvertibleTransformException {
      this.ensureCanTransform2DPoint();
      return this.getInverseCache().deltaTransform(var1, var3);
   }

   public Point2D inverseDeltaTransform(Point2D var1) throws NonInvertibleTransformException {
      return this.inverseDeltaTransform(var1.getX(), var1.getY());
   }

   public Point3D inverseDeltaTransform(double var1, double var3, double var5) throws NonInvertibleTransformException {
      return this.getInverseCache().deltaTransform(var1, var3, var5);
   }

   public Point3D inverseDeltaTransform(Point3D var1) throws NonInvertibleTransformException {
      return this.inverseDeltaTransform(var1.getX(), var1.getY(), var1.getZ());
   }

   private int getFixedSrcOffset(double[] var1, int var2, double[] var3, int var4, int var5, int var6) {
      if (var3 == var1 && var4 > var2 && var4 < var2 + var5 * var6) {
         System.arraycopy(var1, var2, var3, var4, var5 * var6);
         return var4;
      } else {
         return var2;
      }
   }

   private EventHandlerManager getInternalEventDispatcher() {
      if (this.internalEventDispatcher == null) {
         this.internalEventDispatcher = new EventHandlerManager(this);
      }

      return this.internalEventDispatcher;
   }

   public EventDispatchChain buildEventDispatchChain(EventDispatchChain var1) {
      return this.internalEventDispatcher == null ? var1 : var1.append(this.getInternalEventDispatcher());
   }

   public final <T extends Event> void addEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().addEventHandler(var1, var2);
      this.validate();
   }

   public final <T extends Event> void removeEventHandler(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().removeEventHandler(var1, var2);
   }

   public final <T extends Event> void addEventFilter(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().addEventFilter(var1, var2);
      this.validate();
   }

   public final <T extends Event> void removeEventFilter(EventType<T> var1, EventHandler<? super T> var2) {
      this.getInternalEventDispatcher().removeEventFilter(var1, var2);
   }

   public final void setOnTransformChanged(EventHandler<? super TransformChangedEvent> var1) {
      this.onTransformChangedProperty().set(var1);
      this.validate();
   }

   public final EventHandler<? super TransformChangedEvent> getOnTransformChanged() {
      return this.onTransformChanged == null ? null : (EventHandler)this.onTransformChanged.get();
   }

   public final ObjectProperty<EventHandler<? super TransformChangedEvent>> onTransformChangedProperty() {
      if (this.onTransformChanged == null) {
         this.onTransformChanged = new SimpleObjectProperty<EventHandler<? super TransformChangedEvent>>(this, "onTransformChanged") {
            protected void invalidated() {
               Transform.this.getInternalEventDispatcher().setEventHandler(TransformChangedEvent.TRANSFORM_CHANGED, (EventHandler)this.get());
            }
         };
      }

      return this.onTransformChanged;
   }

   void checkRequestedMAT(MatrixType var1) throws IllegalArgumentException {
      if (var1.is2D() && !this.isType2D()) {
         throw new IllegalArgumentException("Cannot access 2D matrix for a 3D transform");
      }
   }

   void ensureCanTransform2DPoint() throws IllegalStateException {
      if (!this.isType2D()) {
         throw new IllegalStateException("Cannot transform 2D point with a 3D transform");
      }
   }

   void validate() {
      this.getMxx();
      this.getMxy();
      this.getMxz();
      this.getTx();
      this.getMyx();
      this.getMyy();
      this.getMyz();
      this.getTy();
      this.getMzx();
      this.getMzy();
      this.getMzz();
      this.getTz();
   }

   abstract void apply(Affine3D var1);

   abstract BaseTransform derive(BaseTransform var1);

   void add(Node var1) {
      this.nodes.add(var1);
   }

   void remove(Node var1) {
      this.nodes.remove(var1);
   }

   protected void transformChanged() {
      this.inverseCache = null;
      Iterator var1 = this.nodes.iterator();

      while (var1.hasNext()) {
         NodeHelper.transformsChanged((Node)var1.next());
      }

      if (this.type2D != null) {
         this.type2D.invalidate();
      }

      if (this.identity != null) {
         this.identity.invalidate();
      }

      if (this.internalEventDispatcher != null) {
         this.validate();
         Event.fireEvent(this, new TransformChangedEvent(this, this));
      }
   }

   void appendTo(Affine var1) {
      var1.append(
         this.getMxx(),
         this.getMxy(),
         this.getMxz(),
         this.getTx(),
         this.getMyx(),
         this.getMyy(),
         this.getMyz(),
         this.getTy(),
         this.getMzx(),
         this.getMzy(),
         this.getMzz(),
         this.getTz()
      );
   }

   void prependTo(Affine var1) {
      var1.prepend(
         this.getMxx(),
         this.getMxy(),
         this.getMxz(),
         this.getTx(),
         this.getMyx(),
         this.getMyy(),
         this.getMyz(),
         this.getTy(),
         this.getMzx(),
         this.getMzy(),
         this.getMzz(),
         this.getTz()
      );
   }

   private Transform getInverseCache() throws NonInvertibleTransformException {
      if (this.inverseCache != null && this.inverseCache.get() != null) {
         return this.inverseCache.get();
      } else {
         Affine var1 = new Affine(
            this.getMxx(),
            this.getMxy(),
            this.getMxz(),
            this.getTx(),
            this.getMyx(),
            this.getMyy(),
            this.getMyz(),
            this.getTy(),
            this.getMzx(),
            this.getMzy(),
            this.getMzz(),
            this.getTz()
         );
         var1.invert();
         this.inverseCache = new SoftReference<>(var1);
         return var1;
      }
   }

   void clearInverseCache() {
      if (this.inverseCache != null) {
         this.inverseCache.clear();
      }
   }

   static Transform createImmutableTransform() {
      return new Transform.ImmutableTransform();
   }

   static Transform createImmutableTransform(
      double var0,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      double var20,
      double var22
   ) {
      return new Transform.ImmutableTransform(var0, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22);
   }

   static Transform createImmutableTransform(
      Transform var0,
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23
   ) {
      if (var0 == null) {
         return new Transform.ImmutableTransform(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23);
      } else {
         ((Transform.ImmutableTransform)var0).setToTransform(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23);
         return var0;
      }
   }

   static Transform createImmutableTransform(Transform var0, Transform var1, Transform var2) {
      if (var0 == null) {
         var0 = new Transform.ImmutableTransform();
      }

      ((Transform.ImmutableTransform)var0).setToConcatenation((Transform.ImmutableTransform)var1, (Transform.ImmutableTransform)var2);
      return (Transform)var0;
   }

   static {
      TransformHelper.setTransformAccessor(
         new TransformHelper.TransformAccessor() {
            @Override
            public void add(Transform var1, Node var2) {
               var1.add(var2);
            }

            @Override
            public void remove(Transform var1, Node var2) {
               var1.remove(var2);
            }

            @Override
            public void apply(Transform var1, Affine3D var2) {
               var1.apply(var2);
            }

            @Override
            public BaseTransform derive(Transform var1, BaseTransform var2) {
               return var1.derive(var2);
            }

            @Override
            public Transform createImmutableTransform() {
               return Transform.createImmutableTransform();
            }

            @Override
            public Transform createImmutableTransform(
               double var1,
               double var3,
               double var5,
               double var7,
               double var9,
               double var11,
               double var13,
               double var15,
               double var17,
               double var19,
               double var21,
               double var23
            ) {
               return Transform.createImmutableTransform(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23);
            }

            @Override
            public Transform createImmutableTransform(
               Transform var1,
               double var2,
               double var4,
               double var6,
               double var8,
               double var10,
               double var12,
               double var14,
               double var16,
               double var18,
               double var20,
               double var22,
               double var24
            ) {
               return Transform.createImmutableTransform(var1, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22, var24);
            }

            @Override
            public Transform createImmutableTransform(Transform var1, Transform var2, Transform var3) {
               return Transform.createImmutableTransform(var1, var2, var3);
            }
         }
      );
   }

   static class ImmutableTransform extends Transform {
      private static final int APPLY_IDENTITY = 0;
      private static final int APPLY_TRANSLATE = 1;
      private static final int APPLY_SCALE = 2;
      private static final int APPLY_SHEAR = 4;
      private static final int APPLY_NON_3D = 0;
      private static final int APPLY_3D_COMPLEX = 4;
      private transient int state2d;
      private transient int state3d;
      private double xx;
      private double xy;
      private double xz;
      private double yx;
      private double yy;
      private double yz;
      private double zx;
      private double zy;
      private double zz;
      private double xt;
      private double yt;
      private double zt;

      ImmutableTransform() {
         this.xx = this.yy = this.zz = 1.0;
      }

      ImmutableTransform(Transform var1) {
         this(
            var1.getMxx(),
            var1.getMxy(),
            var1.getMxz(),
            var1.getTx(),
            var1.getMyx(),
            var1.getMyy(),
            var1.getMyz(),
            var1.getTy(),
            var1.getMzx(),
            var1.getMzy(),
            var1.getMzz(),
            var1.getTz()
         );
      }

      ImmutableTransform(
         double var1,
         double var3,
         double var5,
         double var7,
         double var9,
         double var11,
         double var13,
         double var15,
         double var17,
         double var19,
         double var21,
         double var23
      ) {
         this.xx = var1;
         this.xy = var3;
         this.xz = var5;
         this.xt = var7;
         this.yx = var9;
         this.yy = var11;
         this.yz = var13;
         this.yt = var15;
         this.zx = var17;
         this.zy = var19;
         this.zz = var21;
         this.zt = var23;
         this.updateState();
      }

      private void setToTransform(
         double var1,
         double var3,
         double var5,
         double var7,
         double var9,
         double var11,
         double var13,
         double var15,
         double var17,
         double var19,
         double var21,
         double var23
      ) {
         this.xx = var1;
         this.xy = var3;
         this.xz = var5;
         this.xt = var7;
         this.yx = var9;
         this.yy = var11;
         this.yz = var13;
         this.yt = var15;
         this.zx = var17;
         this.zy = var19;
         this.zz = var21;
         this.zt = var23;
         this.updateState();
      }

      private void setToConcatenation(Transform.ImmutableTransform var1, Transform.ImmutableTransform var2) {
         if (var1.state3d == 0 && var2.state3d == 0) {
            this.xx = var1.xx * var2.xx + var1.xy * var2.yx;
            this.xy = var1.xx * var2.xy + var1.xy * var2.yy;
            this.xt = var1.xx * var2.xt + var1.xy * var2.yt + var1.xt;
            this.yx = var1.yx * var2.xx + var1.yy * var2.yx;
            this.yy = var1.yx * var2.xy + var1.yy * var2.yy;
            this.yt = var1.yx * var2.xt + var1.yy * var2.yt + var1.yt;
            if (this.state3d != 0) {
               this.xz = this.yz = this.zx = this.zy = this.zt = 0.0;
               this.zz = 1.0;
               this.state3d = 0;
            }

            this.updateState2D();
         } else {
            this.xx = var1.xx * var2.xx + var1.xy * var2.yx + var1.xz * var2.zx;
            this.xy = var1.xx * var2.xy + var1.xy * var2.yy + var1.xz * var2.zy;
            this.xz = var1.xx * var2.xz + var1.xy * var2.yz + var1.xz * var2.zz;
            this.xt = var1.xx * var2.xt + var1.xy * var2.yt + var1.xz * var2.zt + var1.xt;
            this.yx = var1.yx * var2.xx + var1.yy * var2.yx + var1.yz * var2.zx;
            this.yy = var1.yx * var2.xy + var1.yy * var2.yy + var1.yz * var2.zy;
            this.yz = var1.yx * var2.xz + var1.yy * var2.yz + var1.yz * var2.zz;
            this.yt = var1.yx * var2.xt + var1.yy * var2.yt + var1.yz * var2.zt + var1.yt;
            this.zx = var1.zx * var2.xx + var1.zy * var2.yx + var1.zz * var2.zx;
            this.zy = var1.zx * var2.xy + var1.zy * var2.yy + var1.zz * var2.zy;
            this.zz = var1.zx * var2.xz + var1.zy * var2.yz + var1.zz * var2.zz;
            this.zt = var1.zx * var2.xt + var1.zy * var2.yt + var1.zz * var2.zt + var1.zt;
            this.updateState();
         }
      }

      @Override
      public double getMxx() {
         return this.xx;
      }

      @Override
      public double getMxy() {
         return this.xy;
      }

      @Override
      public double getMxz() {
         return this.xz;
      }

      @Override
      public double getTx() {
         return this.xt;
      }

      @Override
      public double getMyx() {
         return this.yx;
      }

      @Override
      public double getMyy() {
         return this.yy;
      }

      @Override
      public double getMyz() {
         return this.yz;
      }

      @Override
      public double getTy() {
         return this.yt;
      }

      @Override
      public double getMzx() {
         return this.zx;
      }

      @Override
      public double getMzy() {
         return this.zy;
      }

      @Override
      public double getMzz() {
         return this.zz;
      }

      @Override
      public double getTz() {
         return this.zt;
      }

      @Override
      public double determinant() {
         switch (this.state3d) {
            case 1:
               return 1.0;
            case 2:
            case 3:
               return this.xx * this.yy * this.zz;
            case 4:
               return this.xx * (this.yy * this.zz - this.zy * this.yz)
                  + this.xy * (this.yz * this.zx - this.zz * this.yx)
                  + this.xz * (this.yx * this.zy - this.zx * this.yy);
            default:
               stateError();
            case 0:
               switch (this.state2d) {
                  case 0:
                  case 1:
                     return 1.0;
                  case 2:
                  case 3:
                     return this.xx * this.yy;
                  case 4:
                  case 5:
                     return -(this.xy * this.yx);
                  default:
                     stateError();
                  case 6:
                  case 7:
                     return this.xx * this.yy - this.xy * this.yx;
               }
         }
      }

      @Override
      public Transform createConcatenation(Transform var1) {
         Affine var2 = new Affine(this);
         var2.append(var1);
         return var2;
      }

      public Affine createInverse() throws NonInvertibleTransformException {
         Affine var1 = new Affine(this);
         var1.invert();
         return var1;
      }

      @Override
      public Transform clone() {
         return new Transform.ImmutableTransform(this);
      }

      @Override
      public Point2D transform(double var1, double var3) {
         this.ensureCanTransform2DPoint();
         switch (this.state2d) {
            case 0:
               return new Point2D(var1, var3);
            case 1:
               return new Point2D(var1 + this.xt, var3 + this.yt);
            case 2:
               return new Point2D(this.xx * var1, this.yy * var3);
            case 3:
               return new Point2D(this.xx * var1 + this.xt, this.yy * var3 + this.yt);
            case 4:
               return new Point2D(this.xy * var3, this.yx * var1);
            case 5:
               return new Point2D(this.xy * var3 + this.xt, this.yx * var1 + this.yt);
            case 6:
               return new Point2D(this.xx * var1 + this.xy * var3, this.yx * var1 + this.yy * var3);
            default:
               stateError();
            case 7:
               return new Point2D(this.xx * var1 + this.xy * var3 + this.xt, this.yx * var1 + this.yy * var3 + this.yt);
         }
      }

      @Override
      public Point3D transform(double var1, double var3, double var5) {
         switch (this.state3d) {
            case 1:
               return new Point3D(var1 + this.xt, var3 + this.yt, var5 + this.zt);
            case 2:
               return new Point3D(this.xx * var1, this.yy * var3, this.zz * var5);
            case 3:
               return new Point3D(this.xx * var1 + this.xt, this.yy * var3 + this.yt, this.zz * var5 + this.zt);
            case 4:
               return new Point3D(
                  this.xx * var1 + this.xy * var3 + this.xz * var5 + this.xt,
                  this.yx * var1 + this.yy * var3 + this.yz * var5 + this.yt,
                  this.zx * var1 + this.zy * var3 + this.zz * var5 + this.zt
               );
            default:
               stateError();
            case 0:
               switch (this.state2d) {
                  case 0:
                     return new Point3D(var1, var3, var5);
                  case 1:
                     return new Point3D(var1 + this.xt, var3 + this.yt, var5);
                  case 2:
                     return new Point3D(this.xx * var1, this.yy * var3, var5);
                  case 3:
                     return new Point3D(this.xx * var1 + this.xt, this.yy * var3 + this.yt, var5);
                  case 4:
                     return new Point3D(this.xy * var3, this.yx * var1, var5);
                  case 5:
                     return new Point3D(this.xy * var3 + this.xt, this.yx * var1 + this.yt, var5);
                  case 6:
                     return new Point3D(this.xx * var1 + this.xy * var3, this.yx * var1 + this.yy * var3, var5);
                  default:
                     stateError();
                  case 7:
                     return new Point3D(this.xx * var1 + this.xy * var3 + this.xt, this.yx * var1 + this.yy * var3 + this.yt, var5);
               }
         }
      }

      @Override
      public Point2D deltaTransform(double var1, double var3) {
         this.ensureCanTransform2DPoint();
         switch (this.state2d) {
            case 0:
            case 1:
               return new Point2D(var1, var3);
            case 2:
            case 3:
               return new Point2D(this.xx * var1, this.yy * var3);
            case 4:
            case 5:
               return new Point2D(this.xy * var3, this.yx * var1);
            default:
               stateError();
            case 6:
            case 7:
               return new Point2D(this.xx * var1 + this.xy * var3, this.yx * var1 + this.yy * var3);
         }
      }

      @Override
      public Point3D deltaTransform(double var1, double var3, double var5) {
         switch (this.state3d) {
            case 1:
               return new Point3D(var1, var3, var5);
            case 2:
            case 3:
               return new Point3D(this.xx * var1, this.yy * var3, this.zz * var5);
            case 4:
               return new Point3D(
                  this.xx * var1 + this.xy * var3 + this.xz * var5,
                  this.yx * var1 + this.yy * var3 + this.yz * var5,
                  this.zx * var1 + this.zy * var3 + this.zz * var5
               );
            default:
               stateError();
            case 0:
               switch (this.state2d) {
                  case 0:
                  case 1:
                     return new Point3D(var1, var3, var5);
                  case 2:
                  case 3:
                     return new Point3D(this.xx * var1, this.yy * var3, var5);
                  case 4:
                  case 5:
                     return new Point3D(this.xy * var3, this.yx * var1, var5);
                  default:
                     stateError();
                  case 6:
                  case 7:
                     return new Point3D(this.xx * var1 + this.xy * var3, this.yx * var1 + this.yy * var3, var5);
               }
         }
      }

      @Override
      public Point2D inverseTransform(double var1, double var3) throws NonInvertibleTransformException {
         this.ensureCanTransform2DPoint();
         switch (this.state2d) {
            case 0:
               return new Point2D(var1, var3);
            case 1:
               return new Point2D(var1 - this.xt, var3 - this.yt);
            case 2:
               if (this.xx != 0.0 && this.yy != 0.0) {
                  return new Point2D(1.0 / this.xx * var1, 1.0 / this.yy * var3);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            case 3:
               if (this.xx != 0.0 && this.yy != 0.0) {
                  return new Point2D(1.0 / this.xx * var1 - this.xt / this.xx, 1.0 / this.yy * var3 - this.yt / this.yy);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            case 4:
               if (this.xy != 0.0 && this.yx != 0.0) {
                  return new Point2D(1.0 / this.yx * var3, 1.0 / this.xy * var1);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            case 5:
               if (this.xy != 0.0 && this.yx != 0.0) {
                  return new Point2D(1.0 / this.yx * var3 - this.yt / this.yx, 1.0 / this.xy * var1 - this.xt / this.xy);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            default:
               return super.inverseTransform(var1, var3);
         }
      }

      @Override
      public Point3D inverseTransform(double var1, double var3, double var5) throws NonInvertibleTransformException {
         switch (this.state3d) {
            case 1:
               return new Point3D(var1 - this.xt, var3 - this.yt, var5 - this.zt);
            case 2:
               if (this.xx != 0.0 && this.yy != 0.0 && this.zz != 0.0) {
                  return new Point3D(1.0 / this.xx * var1, 1.0 / this.yy * var3, 1.0 / this.zz * var5);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            case 3:
               if (this.xx != 0.0 && this.yy != 0.0 && this.zz != 0.0) {
                  return new Point3D(
                     1.0 / this.xx * var1 - this.xt / this.xx, 1.0 / this.yy * var3 - this.yt / this.yy, 1.0 / this.zz * var5 - this.zt / this.zz
                  );
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            case 4:
               return super.inverseTransform(var1, var3, var5);
            default:
               stateError();
            case 0:
               switch (this.state2d) {
                  case 0:
                     return new Point3D(var1, var3, var5);
                  case 1:
                     return new Point3D(var1 - this.xt, var3 - this.yt, var5);
                  case 2:
                     if (this.xx != 0.0 && this.yy != 0.0) {
                        return new Point3D(1.0 / this.xx * var1, 1.0 / this.yy * var3, var5);
                     }

                     throw new NonInvertibleTransformException("Determinant is 0");
                  case 3:
                     if (this.xx != 0.0 && this.yy != 0.0) {
                        return new Point3D(1.0 / this.xx * var1 - this.xt / this.xx, 1.0 / this.yy * var3 - this.yt / this.yy, var5);
                     }

                     throw new NonInvertibleTransformException("Determinant is 0");
                  case 4:
                     if (this.xy != 0.0 && this.yx != 0.0) {
                        return new Point3D(1.0 / this.yx * var3, 1.0 / this.xy * var1, var5);
                     }

                     throw new NonInvertibleTransformException("Determinant is 0");
                  case 5:
                     if (this.xy != 0.0 && this.yx != 0.0) {
                        return new Point3D(1.0 / this.yx * var3 - this.yt / this.yx, 1.0 / this.xy * var1 - this.xt / this.xy, var5);
                     }

                     throw new NonInvertibleTransformException("Determinant is 0");
                  default:
                     return super.inverseTransform(var1, var3, var5);
               }
         }
      }

      @Override
      public Point2D inverseDeltaTransform(double var1, double var3) throws NonInvertibleTransformException {
         this.ensureCanTransform2DPoint();
         switch (this.state2d) {
            case 0:
            case 1:
               return new Point2D(var1, var3);
            case 2:
            case 3:
               if (this.xx != 0.0 && this.yy != 0.0) {
                  return new Point2D(1.0 / this.xx * var1, 1.0 / this.yy * var3);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            case 4:
            case 5:
               if (this.xy != 0.0 && this.yx != 0.0) {
                  return new Point2D(1.0 / this.yx * var3, 1.0 / this.xy * var1);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            default:
               return super.inverseDeltaTransform(var1, var3);
         }
      }

      @Override
      public Point3D inverseDeltaTransform(double var1, double var3, double var5) throws NonInvertibleTransformException {
         switch (this.state3d) {
            case 1:
               return new Point3D(var1, var3, var5);
            case 2:
            case 3:
               if (this.xx != 0.0 && this.yy != 0.0 && this.zz != 0.0) {
                  return new Point3D(1.0 / this.xx * var1, 1.0 / this.yy * var3, 1.0 / this.zz * var5);
               }

               throw new NonInvertibleTransformException("Determinant is 0");
            case 4:
               return super.inverseDeltaTransform(var1, var3, var5);
            default:
               stateError();
            case 0:
               switch (this.state2d) {
                  case 0:
                  case 1:
                     return new Point3D(var1, var3, var5);
                  case 2:
                  case 3:
                     if (this.xx != 0.0 && this.yy != 0.0) {
                        return new Point3D(1.0 / this.xx * var1, 1.0 / this.yy * var3, var5);
                     }

                     throw new NonInvertibleTransformException("Determinant is 0");
                  case 4:
                  case 5:
                     if (this.xy != 0.0 && this.yx != 0.0) {
                        return new Point3D(1.0 / this.yx * var3, 1.0 / this.xy * var1, var5);
                     }

                     throw new NonInvertibleTransformException("Determinant is 0");
                  default:
                     return super.inverseDeltaTransform(var1, var3, var5);
               }
         }
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder("Transform [\n");
         var1.append("\t").append(this.xx);
         var1.append(", ").append(this.xy);
         var1.append(", ").append(this.xz);
         var1.append(", ").append(this.xt);
         var1.append('\n');
         var1.append("\t").append(this.yx);
         var1.append(", ").append(this.yy);
         var1.append(", ").append(this.yz);
         var1.append(", ").append(this.yt);
         var1.append('\n');
         var1.append("\t").append(this.zx);
         var1.append(", ").append(this.zy);
         var1.append(", ").append(this.zz);
         var1.append(", ").append(this.zt);
         return var1.append("\n]").toString();
      }

      private void updateState() {
         this.updateState2D();
         this.state3d = 0;
         if (this.xz != 0.0 || this.yz != 0.0 || this.zx != 0.0 || this.zy != 0.0) {
            this.state3d = 4;
         } else if ((this.state2d & 4) == 0) {
            if (this.zt != 0.0) {
               this.state3d |= 1;
            }

            if (this.zz != 1.0) {
               this.state3d |= 2;
            }

            if (this.state3d != 0) {
               this.state3d = this.state3d | this.state2d & 3;
            }
         } else if (this.zz != 1.0 || this.zt != 0.0) {
            this.state3d = 4;
         }
      }

      private void updateState2D() {
         if (this.xy == 0.0 && this.yx == 0.0) {
            if (this.xx == 1.0 && this.yy == 1.0) {
               if (this.xt == 0.0 && this.yt == 0.0) {
                  this.state2d = 0;
               } else {
                  this.state2d = 1;
               }
            } else if (this.xt == 0.0 && this.yt == 0.0) {
               this.state2d = 2;
            } else {
               this.state2d = 3;
            }
         } else if (this.xx == 0.0 && this.yy == 0.0) {
            if (this.xt == 0.0 && this.yt == 0.0) {
               this.state2d = 4;
            } else {
               this.state2d = 5;
            }
         } else if (this.xt == 0.0 && this.yt == 0.0) {
            this.state2d = 6;
         } else {
            this.state2d = 7;
         }
      }

      @Override
      void ensureCanTransform2DPoint() throws IllegalStateException {
         if (this.state3d != 0) {
            throw new IllegalStateException("Cannot transform 2D point with a 3D transform");
         }
      }

      private static void stateError() {
         throw new InternalError("missing case in a switch");
      }

      @Override
      void apply(Affine3D var1) {
         var1.concatenate(this.xx, this.xy, this.xz, this.xt, this.yx, this.yy, this.yz, this.yt, this.zx, this.zy, this.zz, this.zt);
      }

      @Override
      BaseTransform derive(BaseTransform var1) {
         return var1.deriveWithConcatenation(this.xx, this.xy, this.xz, this.xt, this.yx, this.yy, this.yz, this.yt, this.zx, this.zy, this.zz, this.zt);
      }

      int getState2d() {
         return this.state2d;
      }

      int getState3d() {
         return this.state3d;
      }
   }

   private abstract static class LazyBooleanProperty extends ReadOnlyBooleanProperty {
      private ExpressionHelper<Boolean> helper;
      private boolean valid;
      private boolean value;

      public void addListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(InvalidationListener var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public void addListener(ChangeListener<? super Boolean> var1) {
         this.helper = ExpressionHelper.addListener(this.helper, this, var1);
      }

      public void removeListener(ChangeListener<? super Boolean> var1) {
         this.helper = ExpressionHelper.removeListener(this.helper, var1);
      }

      public boolean get() {
         if (!this.valid) {
            this.value = this.computeValue();
            this.valid = true;
         }

         return this.value;
      }

      public void invalidate() {
         if (this.valid) {
            this.valid = false;
            ExpressionHelper.fireValueChangedEvent(this.helper);
         }
      }

      protected abstract boolean computeValue();
   }
}
