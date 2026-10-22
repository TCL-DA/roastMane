package javafx.scene.shape;

import com.sun.javafx.geom.QuadCurve2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.QuadCurveHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGQuadCurve;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

public class QuadCurve extends Shape {
   private final QuadCurve2D shape = new QuadCurve2D();
   private DoubleProperty startX;
   private DoubleProperty startY;
   private DoubleProperty controlX;
   private DoubleProperty controlY;
   private DoubleProperty endX;
   private DoubleProperty endY;

   public QuadCurve() {
      QuadCurveHelper.initHelper(this);
      this.controlX = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(QuadCurve.this);
         }

         public Object getBean() {
            return QuadCurve.this;
         }

         public String getName() {
            return "controlX";
         }
      };
      this.controlY = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(QuadCurve.this);
         }

         public Object getBean() {
            return QuadCurve.this;
         }

         public String getName() {
            return "controlY";
         }
      };
   }

   public QuadCurve(double var1, double var3, double var5, double var7, double var9, double var11) {
      QuadCurveHelper.initHelper(this);
      this.controlX = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(QuadCurve.this);
         }

         public Object getBean() {
            return QuadCurve.this;
         }

         public String getName() {
            return "controlX";
         }
      };
      this.controlY = new DoublePropertyBase() {
         public void invalidated() {
            NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(QuadCurve.this);
         }

         public Object getBean() {
            return QuadCurve.this;
         }

         public String getName() {
            return "controlY";
         }
      };
      this.setStartX(var1);
      this.setStartY(var3);
      this.setControlX(var5);
      this.setControlY(var7);
      this.setEndX(var9);
      this.setEndY(var11);
   }

   public final void setStartX(double var1) {
      if (this.startX != null || var1 != 0.0) {
         this.startXProperty().set(var1);
      }
   }

   public final double getStartX() {
      return this.startX == null ? 0.0 : this.startX.get();
   }

   public final DoubleProperty startXProperty() {
      if (this.startX == null) {
         this.startX = new DoublePropertyBase() {
            public void invalidated() {
               NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(QuadCurve.this);
            }

            public Object getBean() {
               return QuadCurve.this;
            }

            public String getName() {
               return "startX";
            }
         };
      }

      return this.startX;
   }

   public final void setStartY(double var1) {
      if (this.startY != null || var1 != 0.0) {
         this.startYProperty().set(var1);
      }
   }

   public final double getStartY() {
      return this.startY == null ? 0.0 : this.startY.get();
   }

   public final DoubleProperty startYProperty() {
      if (this.startY == null) {
         this.startY = new DoublePropertyBase() {
            public void invalidated() {
               NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(QuadCurve.this);
            }

            public Object getBean() {
               return QuadCurve.this;
            }

            public String getName() {
               return "startY";
            }
         };
      }

      return this.startY;
   }

   public final void setControlX(double var1) {
      this.controlX.set(var1);
   }

   public final double getControlX() {
      return this.controlX.get();
   }

   public final DoubleProperty controlXProperty() {
      return this.controlX;
   }

   public final void setControlY(double var1) {
      this.controlY.set(var1);
   }

   public final double getControlY() {
      return this.controlY.get();
   }

   public final DoubleProperty controlYProperty() {
      return this.controlY;
   }

   public final void setEndX(double var1) {
      if (this.endX != null || var1 != 0.0) {
         this.endXProperty().set(var1);
      }
   }

   public final double getEndX() {
      return this.endX == null ? 0.0 : this.endX.get();
   }

   public final DoubleProperty endXProperty() {
      if (this.endX == null) {
         this.endX = new DoublePropertyBase() {
            public void invalidated() {
               NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(QuadCurve.this);
            }

            public Object getBean() {
               return QuadCurve.this;
            }

            public String getName() {
               return "endX";
            }
         };
      }

      return this.endX;
   }

   public final void setEndY(double var1) {
      if (this.endY != null || var1 != 0.0) {
         this.endYProperty().set(var1);
      }
   }

   public final double getEndY() {
      return this.endY == null ? 0.0 : this.endY.get();
   }

   public final DoubleProperty endYProperty() {
      if (this.endY == null) {
         this.endY = new DoublePropertyBase() {
            public void invalidated() {
               NodeHelper.markDirty(QuadCurve.this, DirtyBits.NODE_GEOMETRY);
               NodeHelper.geomChanged(QuadCurve.this);
            }

            public Object getBean() {
               return QuadCurve.this;
            }

            public String getName() {
               return "endY";
            }
         };
      }

      return this.endY;
   }

   private NGNode doCreatePeer() {
      return new NGQuadCurve();
   }

   private QuadCurve2D doConfigShape() {
      this.shape.x1 = (float)this.getStartX();
      this.shape.y1 = (float)this.getStartY();
      this.shape.ctrlx = (float)this.getControlX();
      this.shape.ctrly = (float)this.getControlY();
      this.shape.x2 = (float)this.getEndX();
      this.shape.y2 = (float)this.getEndY();
      return this.shape;
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
         NGQuadCurve var1 = NodeHelper.getPeer(this);
         var1.updateQuadCurve(
            (float)this.getStartX(),
            (float)this.getStartY(),
            (float)this.getEndX(),
            (float)this.getEndY(),
            (float)this.getControlX(),
            (float)this.getControlY()
         );
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("QuadCurve[");
      String var2 = this.getId();
      if (var2 != null) {
         var1.append("id=").append(var2).append(", ");
      }

      var1.append("startX=").append(this.getStartX());
      var1.append(", startY=").append(this.getStartY());
      var1.append(", controlX=").append(this.getControlX());
      var1.append(", controlY=").append(this.getControlY());
      var1.append(", endX=").append(this.getEndX());
      var1.append(", endY=").append(this.getEndY());
      var1.append(", fill=").append(this.getFill());
      Paint var3 = this.getStroke();
      if (var3 != null) {
         var1.append(", stroke=").append(var3);
         var1.append(", strokeWidth=").append(this.getStrokeWidth());
      }

      return var1.append("]").toString();
   }

   static {
      QuadCurveHelper.setQuadCurveAccessor(new QuadCurveHelper.QuadCurveAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((QuadCurve)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((QuadCurve)var1).doUpdatePeer();
         }

         @Override
         public com.sun.javafx.geom.Shape doConfigShape(Shape var1) {
            return ((QuadCurve)var1).doConfigShape();
         }
      });
   }
}
