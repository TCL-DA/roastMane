package javafx.animation;

import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public final class PathTransition extends Transition {
   private ObjectProperty<Node> node;
   private double totalLength = 0.0;
   private final ArrayList<PathTransition.Segment> segments = new ArrayList<>();
   private static final Node DEFAULT_NODE = null;
   private static final int SMOOTH_ZONE = 10;
   private Node cachedNode;
   private ObjectProperty<Duration> duration;
   private static final Duration DEFAULT_DURATION = Duration.millis(400.0);
   private ObjectProperty<Shape> path;
   private static final Shape DEFAULT_PATH = null;
   private ObjectProperty<PathTransition.OrientationType> orientation;
   private static final PathTransition.OrientationType DEFAULT_ORIENTATION = PathTransition.OrientationType.NONE;
   private boolean cachedIsNormalRequired;

   public final void setNode(Node var1) {
      if (this.node != null || var1 != null) {
         this.nodeProperty().set(var1);
      }
   }

   public final Node getNode() {
      return this.node == null ? DEFAULT_NODE : (Node)this.node.get();
   }

   public final ObjectProperty<Node> nodeProperty() {
      if (this.node == null) {
         this.node = new SimpleObjectProperty(this, "node", DEFAULT_NODE);
      }

      return this.node;
   }

   public final void setDuration(Duration var1) {
      if (this.duration != null || !DEFAULT_DURATION.equals(var1)) {
         this.durationProperty().set(var1);
      }
   }

   public final Duration getDuration() {
      return this.duration == null ? DEFAULT_DURATION : (Duration)this.duration.get();
   }

   public final ObjectProperty<Duration> durationProperty() {
      if (this.duration == null) {
         this.duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {
            public void invalidated() {
               try {
                  PathTransition.this.setCycleDuration(PathTransition.this.getDuration());
               } catch (IllegalArgumentException var2) {
                  if (this.isBound()) {
                     this.unbind();
                  }

                  this.set(PathTransition.this.getCycleDuration());
                  throw var2;
               }
            }

            public Object getBean() {
               return PathTransition.this;
            }

            public String getName() {
               return "duration";
            }
         };
      }

      return this.duration;
   }

   public final void setPath(Shape var1) {
      if (this.path != null || var1 != null) {
         this.pathProperty().set(var1);
      }
   }

   public final Shape getPath() {
      return this.path == null ? DEFAULT_PATH : (Shape)this.path.get();
   }

   public final ObjectProperty<Shape> pathProperty() {
      if (this.path == null) {
         this.path = new SimpleObjectProperty(this, "path", DEFAULT_PATH);
      }

      return this.path;
   }

   public final void setOrientation(PathTransition.OrientationType var1) {
      if (this.orientation != null || !DEFAULT_ORIENTATION.equals(var1)) {
         this.orientationProperty().set(var1);
      }
   }

   public final PathTransition.OrientationType getOrientation() {
      return this.orientation == null ? PathTransition.OrientationType.NONE : (PathTransition.OrientationType)this.orientation.get();
   }

   public final ObjectProperty<PathTransition.OrientationType> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new SimpleObjectProperty(this, "orientation", DEFAULT_ORIENTATION);
      }

      return this.orientation;
   }

   public PathTransition(Duration var1, Shape var2, Node var3) {
      this.setDuration(var1);
      this.setPath(var2);
      this.setNode(var3);
      this.setCycleDuration(var1);
   }

   public PathTransition(Duration var1, Shape var2) {
      this(var1, var2, null);
   }

   public PathTransition() {
      this(DEFAULT_DURATION, null, null);
   }

   @Override
   public void interpolate(double var1) {
      double var3 = this.totalLength * Math.min(1.0, Math.max(0.0, var1));
      int var5 = this.findSegment(0, this.segments.size() - 1, var3);
      PathTransition.Segment var6 = this.segments.get(var5);
      double var7 = var6.accumLength - var6.length;
      double var9 = var3 - var7;
      double var11 = var9 / var6.length;
      PathTransition.Segment var13 = var6.prevSeg;
      double var14 = var13.toX + (var6.toX - var13.toX) * var11;
      double var16 = var13.toY + (var6.toY - var13.toY) * var11;
      double var18 = var6.rotateAngle;
      double var20 = Math.min(10.0, var6.length / 2.0);
      if (var9 < var20 && !var13.isMoveTo) {
         var18 = interpolate(var13.rotateAngle, var6.rotateAngle, var9 / var20 / 2.0 + 0.5);
      } else {
         double var22 = var6.length - var9;
         PathTransition.Segment var24 = var6.nextSeg;
         if (var22 < var20 && var24 != null && !var24.isMoveTo) {
            var18 = interpolate(var6.rotateAngle, var24.rotateAngle, (var20 - var22) / var20 / 2.0);
         }
      }

      this.cachedNode.setTranslateX(var14 - NodeHelper.getPivotX(this.cachedNode));
      this.cachedNode.setTranslateY(var16 - NodeHelper.getPivotY(this.cachedNode));
      if (this.cachedIsNormalRequired) {
         this.cachedNode.setRotate(var18);
      }
   }

   private Node getTargetNode() {
      Node var1 = this.getNode();
      return var1 != null ? var1 : this.getParentTargetNode();
   }

   @Override
   boolean startable(boolean var1) {
      return super.startable(var1)
         && (this.getTargetNode() != null && this.getPath() != null && !this.getPath().getLayoutBounds().isEmpty() || !var1 && this.cachedNode != null);
   }

   @Override
   void sync(boolean var1) {
      super.sync(var1);
      if (var1 || this.cachedNode == null) {
         this.cachedNode = this.getTargetNode();
         this.recomputeSegments();
         this.cachedIsNormalRequired = this.getOrientation() == PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT;
      }
   }

   private void recomputeSegments() {
      this.segments.clear();
      Shape var1 = this.getPath();
      PathTransition.Segment var2 = PathTransition.Segment.getZeroSegment();
      PathTransition.Segment var3 = PathTransition.Segment.getZeroSegment();
      float[] var4 = new float[6];

      for (PathIterator var5 = ShapeHelper.configShape(var1).getPathIterator(NodeHelper.getLeafTransform(var1), 1.0F); !var5.isDone(); var5.next()) {
         PathTransition.Segment var6 = null;
         int var7 = var5.currentSegment(var4);
         double var8 = (double)var4[0];
         double var10 = (double)var4[1];
         switch (var7) {
            case 0:
               var2 = PathTransition.Segment.newMoveTo(var8, var10, var3.accumLength);
               var6 = var2;
               break;
            case 1:
               var6 = PathTransition.Segment.newLineTo(var3, var8, var10);
            case 2:
            case 3:
            default:
               break;
            case 4:
               var6 = PathTransition.Segment.newClosePath(var3, var2);
               if (var6 == null) {
                  var3.convertToClosePath(var2);
               }
         }

         if (var6 != null) {
            this.segments.add(var6);
            var3 = var6;
         }
      }

      this.totalLength = var3.accumLength;
   }

   private int findSegment(int var1, int var2, double var3) {
      if (var1 != var2) {
         int var5 = var1 + (var2 - var1) / 2;
         return this.segments.get(var5).accumLength > var3 ? this.findSegment(var1, var5, var3) : this.findSegment(var5 + 1, var2, var3);
      } else {
         return this.segments.get(var1).isMoveTo && var1 > 0 ? this.findSegment(var1 - 1, var1 - 1, var3) : var1;
      }
   }

   private static double interpolate(double var0, double var2, double var4) {
      double var6 = var2 - var0;
      if (Math.abs(var6) > 180.0) {
         var2 += var6 > 0.0 ? -360.0 : 360.0;
      }

      return normalize(var0 + var4 * (var2 - var0));
   }

   private static double normalize(double var0) {
      while (var0 > 360.0) {
         var0 -= 360.0;
      }

      while (var0 < 0.0) {
         var0 += 360.0;
      }

      return var0;
   }

   public static enum OrientationType {
      NONE,
      ORTHOGONAL_TO_TANGENT;
   }

   private static class Segment {
      private static final PathTransition.Segment zeroSegment = new PathTransition.Segment(true, 0.0, 0.0, 0.0, 0.0, 0.0);
      boolean isMoveTo;
      double length;
      double accumLength;
      double toX;
      double toY;
      double rotateAngle;
      PathTransition.Segment prevSeg;
      PathTransition.Segment nextSeg;

      private Segment(boolean var1, double var2, double var4, double var6, double var8, double var10) {
         this.isMoveTo = var1;
         this.toX = var2;
         this.toY = var4;
         this.length = var6;
         this.accumLength = var8 + var6;
         this.rotateAngle = var10;
      }

      public static PathTransition.Segment getZeroSegment() {
         return zeroSegment;
      }

      public static PathTransition.Segment newMoveTo(double var0, double var2, double var4) {
         return new PathTransition.Segment(true, var0, var2, 0.0, var4, 0.0);
      }

      public static PathTransition.Segment newLineTo(PathTransition.Segment var0, double var1, double var3) {
         double var5 = var1 - var0.toX;
         double var7 = var3 - var0.toY;
         double var9 = Math.sqrt(var5 * var5 + var7 * var7);
         if (!(var9 >= 1.0) && !var0.isMoveTo) {
            return null;
         } else {
            double var11 = Math.signum(var7 == 0.0 ? var5 : var7);
            double var13 = var11 * Math.acos(var5 / var9);
            var13 = PathTransition.normalize(var13 / Math.PI * 180.0);
            PathTransition.Segment var15 = new PathTransition.Segment(false, var1, var3, var9, var0.accumLength, var13);
            var0.nextSeg = var15;
            var15.prevSeg = var0;
            return var15;
         }
      }

      public static PathTransition.Segment newClosePath(PathTransition.Segment var0, PathTransition.Segment var1) {
         PathTransition.Segment var2 = newLineTo(var0, var1.toX, var1.toY);
         if (var2 != null) {
            var2.convertToClosePath(var1);
         }

         return var2;
      }

      public void convertToClosePath(PathTransition.Segment var1) {
         PathTransition.Segment var2 = var1.nextSeg;
         this.nextSeg = var2;
         var2.prevSeg = this;
      }
   }
}
