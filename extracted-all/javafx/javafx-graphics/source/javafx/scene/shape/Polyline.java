package javafx.scene.shape;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.PolylineHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPolyline;
import com.sun.javafx.sg.prism.NGShape;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Polyline extends Shape {
   private final Path2D shape = new Path2D();
   private final ObservableList<Double> points;

   public Polyline() {
      PolylineHelper.initHelper(this);
      ((StyleableProperty)this.fillProperty()).applyStyle(null, null);
      ((StyleableProperty)this.strokeProperty()).applyStyle(null, Color.BLACK);
      this.points = new TrackableObservableList<Double>() {
         protected void onChanged(Change<Double> var1) {
            NodeHelper.markDirty(Polyline.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(Polyline.this);
         }
      };
   }

   public Polyline(double... var1) {
      PolylineHelper.initHelper(this);
      ((StyleableProperty)this.fillProperty()).applyStyle(null, null);
      ((StyleableProperty)this.strokeProperty()).applyStyle(null, Color.BLACK);
      this.points = new TrackableObservableList<Double>() {
         protected void onChanged(Change<Double> var1) {
            NodeHelper.markDirty(Polyline.this, DirtyBits.NODE_GEOMETRY);
            NodeHelper.geomChanged(Polyline.this);
         }
      };
      if (var1 != null) {
         for (double var5 : var1) {
            this.getPoints().add(var5);
         }
      }
   }

   public final ObservableList<Double> getPoints() {
      return this.points;
   }

   private NGNode doCreatePeer() {
      return new NGPolyline();
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      if (this.getMode() == NGShape.Mode.EMPTY || this.getPoints().size() <= 1) {
         return var1.makeEmpty();
      } else if (this.getPoints().size() != 2) {
         return this.computeShapeBounds(var1, var2, ShapeHelper.configShape(this));
      } else if (this.getMode() != NGShape.Mode.FILL && this.getStrokeType() != StrokeType.INSIDE) {
         double var3 = this.getStrokeWidth();
         if (this.getStrokeType() == StrokeType.CENTERED) {
            var3 /= 2.0;
         }

         return this.computeBounds(var1, var2, var3, 0.5, (Double)this.getPoints().get(0), (Double)this.getPoints().get(1), 0.0, 0.0);
      } else {
         return var1.makeEmpty();
      }
   }

   private Path2D doConfigShape() {
      double var1 = (Double)this.getPoints().get(0);
      double var3 = (Double)this.getPoints().get(1);
      this.shape.reset();
      this.shape.moveTo((float)var1, (float)var3);
      int var5 = this.getPoints().size() & -2;

      for (byte var6 = 2; var6 < var5; var6 += 2) {
         var1 = (Double)this.getPoints().get(var6);
         var3 = (Double)this.getPoints().get(var6 + 1);
         this.shape.lineTo((float)var1, (float)var3);
      }

      return this.shape;
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
         int var1 = this.getPoints().size() & -2;
         float[] var2 = new float[var1];

         for (int var3 = 0; var3 < var1; var3++) {
            var2[var3] = (float)((Double)this.getPoints().get(var3)).doubleValue();
         }

         NGPolyline var4 = NodeHelper.getPeer(this);
         var4.updatePolyline(var2);
      }
   }

   private Paint doCssGetFillInitialValue() {
      return null;
   }

   private Paint doCssGetStrokeInitialValue() {
      return Color.BLACK;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("Polyline[");
      String var2 = this.getId();
      if (var2 != null) {
         var1.append("id=").append(var2).append(", ");
      }

      var1.append("points=").append(this.getPoints());
      var1.append(", fill=").append(this.getFill());
      Paint var3 = this.getStroke();
      if (var3 != null) {
         var1.append(", stroke=").append(var3);
         var1.append(", strokeWidth=").append(this.getStrokeWidth());
      }

      return var1.append("]").toString();
   }

   static {
      PolylineHelper.setPolylineAccessor(new PolylineHelper.PolylineAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Polyline)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Polyline)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Polyline)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public Paint doCssGetFillInitialValue(Shape var1) {
            return ((Polyline)var1).doCssGetFillInitialValue();
         }

         @Override
         public Paint doCssGetStrokeInitialValue(Shape var1) {
            return ((Polyline)var1).doCssGetStrokeInitialValue();
         }

         @Override
         public com.sun.javafx.geom.Shape doConfigShape(Shape var1) {
            return ((Polyline)var1).doConfigShape();
         }
      });
   }
}
