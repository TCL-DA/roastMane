package javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.CircleHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGCircle;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGShape;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

public class Circle extends Shape {
   private final Ellipse2D shape = new Ellipse2D();
   private DoubleProperty centerX;
   private DoubleProperty centerY;
   private final DoubleProperty radius;

   public Circle(double var1) {
      CircleHelper.initHelper(this);
      this.radius = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(Circle.this);
         }

         public Object getBean() {
            return Circle.this;
         }

         public String getName() {
            return "radius";
         }
      };
      this.setRadius(var1);
   }

   public Circle(double var1, Paint var3) {
      CircleHelper.initHelper(this);
      this.radius = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(Circle.this);
         }

         public Object getBean() {
            return Circle.this;
         }

         public String getName() {
            return "radius";
         }
      };
      this.setRadius(var1);
      this.setFill(var3);
   }

   public Circle() {
      CircleHelper.initHelper(this);
      this.radius = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(Circle.this);
         }

         public Object getBean() {
            return Circle.this;
         }

         public String getName() {
            return "radius";
         }
      };
   }

   public Circle(double var1, double var3, double var5) {
      CircleHelper.initHelper(this);
      this.radius = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(Circle.this);
         }

         public Object getBean() {
            return Circle.this;
         }

         public String getName() {
            return "radius";
         }
      };
      this.setCenterX(var1);
      this.setCenterY(var3);
      this.setRadius(var5);
   }

   public Circle(double var1, double var3, double var5, Paint var7) {
      CircleHelper.initHelper(this);
      this.radius = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(Circle.this);
         }

         public Object getBean() {
            return Circle.this;
         }

         public String getName() {
            return "radius";
         }
      };
      this.setCenterX(var1);
      this.setCenterY(var3);
      this.setRadius(var5);
      this.setFill(var7);
   }

   public final void setCenterX(double var1) {
      if (this.centerX != null || var1 != 0.0) {
         this.centerXProperty().set(var1);
      }
   }

   public final double getCenterX() {
      return this.centerX == null ? 0.0 : this.centerX.get();
   }

   public final DoubleProperty centerXProperty() {
      if (this.centerX == null) {
         this.centerX = new DoublePropertyBase(0.0) {
            public void invalidated() {
               NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(Circle.this);
            }

            public Object getBean() {
               return Circle.this;
            }

            public String getName() {
               return "centerX";
            }
         };
      }

      return this.centerX;
   }

   public final void setCenterY(double var1) {
      if (this.centerY != null || var1 != 0.0) {
         this.centerYProperty().set(var1);
      }
   }

   public final double getCenterY() {
      return this.centerY == null ? 0.0 : this.centerY.get();
   }

   public final DoubleProperty centerYProperty() {
      if (this.centerY == null) {
         this.centerY = new DoublePropertyBase(0.0) {
            public void invalidated() {
               NodeHelper.markDirty(Circle.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(Circle.this);
            }

            public Object getBean() {
               return Circle.this;
            }

            public String getName() {
               return "centerY";
            }
         };
      }

      return this.centerY;
   }

   public final void setRadius(double var1) {
      this.radius.set(var1);
   }

   public final double getRadius() {
      return this.radius.get();
   }

   public final DoubleProperty radiusProperty() {
      return this.radius;
   }

   @Override
   StrokeLineJoin convertLineJoin(StrokeLineJoin var1) {
      return StrokeLineJoin.BEVEL;
   }

   private NGNode doCreatePeer() {
      return new NGCircle();
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      if (this.getMode() == NGShape.Mode.EMPTY) {
         return var1.makeEmpty();
      } else {
         double var3 = this.getCenterX();
         double var5 = this.getCenterY();
         if ((var2.getType() & -26) == 0) {
            double var19 = var3 * var2.getMxx() + var5 * var2.getMxy() + var2.getMxt();
            double var20 = var3 * var2.getMyx() + var5 * var2.getMyy() + var2.getMyt();
            double var21 = this.getRadius();
            if (this.getMode() != NGShape.Mode.FILL && this.getStrokeType() != StrokeType.INSIDE) {
               double var22 = this.getStrokeWidth();
               if (this.getStrokeType() == StrokeType.CENTERED) {
                  var22 /= 2.0;
               }

               var21 += var22;
            }

            return var1.deriveWithNewBounds((float)(var19 - var21), (float)(var20 - var21), 0.0F, (float)(var19 + var21), (float)(var20 + var21), 0.0F);
         } else if ((var2.getType() & -72) != 0) {
            return this.computeShapeBounds(var1, var2, ShapeHelper.configShape(this));
         } else {
            double var7 = this.getRadius();
            double var9 = this.getCenterX() - var7;
            double var11 = this.getCenterY() - var7;
            double var13 = 2.0 * var7;
            double var17;
            if (this.getMode() != NGShape.Mode.FILL && this.getStrokeType() != StrokeType.INSIDE) {
               var17 = this.getStrokeWidth();
            } else {
               var17 = 0.0;
            }

            return this.computeBounds(var1, var2, var17, 0.0, var9, var11, var13, var13);
         }
      }
   }

   private Ellipse2D doConfigShape() {
      double var1 = this.getRadius();
      this.shape.setFrame((float)(this.getCenterX() - var1), (float)(this.getCenterY() - var1), (float)(var1 * 2.0), (float)(var1 * 2.0));
      return this.shape;
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
         NGCircle var1 = NodeHelper.getPeer(this);
         var1.updateCircle((float)this.getCenterX(), (float)this.getCenterY(), (float)this.getRadius());
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Circle[");
      String var2 = this.getId();
      if (var2 != null) {
         var1.append("id=").append(var2).append(", ");
      }

      var1.append("centerX=").append(this.getCenterX());
      var1.append(", centerY=").append(this.getCenterY());
      var1.append(", radius=").append(this.getRadius());
      var1.append(", fill=").append(this.getFill());
      Paint var3 = this.getStroke();
      if (var3 != null) {
         var1.append(", stroke=").append(var3);
         var1.append(", strokeWidth=").append(this.getStrokeWidth());
      }

      return var1.append("]").toString();
   }

   static {
      CircleHelper.setCircleAccessor(new CircleHelper.CircleAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Circle)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Circle)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Circle)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public com.sun.javafx.geom.Shape doConfigShape(Shape var1) {
            return ((Circle)var1).doConfigShape();
         }
      });
   }
}
