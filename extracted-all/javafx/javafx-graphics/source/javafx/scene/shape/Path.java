package javafx.scene.shape;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.PathElementHelper;
import com.sun.javafx.scene.shape.PathHelper;
import com.sun.javafx.scene.shape.PathUtils;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPath;
import java.util.Collection;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Path extends Shape {
   private Path2D path2d = null;
   private ObjectProperty<FillRule> fillRule;
   private boolean isPathValid;
   private final ObservableList<PathElement> elements;

   public Path() {
      PathHelper.initHelper(this);
      ((StyleableProperty)this.fillProperty()).applyStyle(null, null);
      ((StyleableProperty)this.strokeProperty()).applyStyle(null, Color.BLACK);
      this.elements = new TrackableObservableList<PathElement>() {
         protected void onChanged(Change<PathElement> var1) {
            ObservableList var2 = var1.getList();

            boolean var3;
            for (var3 = false; var1.next(); var3 |= var1.getFrom() == 0) {
               List var4 = var1.getRemoved();

               for (int var5 = 0; var5 < var1.getRemovedSize(); var5++) {
                  ((PathElement)var4.get(var5)).removeNode(Path.this);
               }

               for (int var7 = var1.getFrom(); var7 < var1.getTo(); var7++) {
                  ((PathElement)var2.get(var7)).addNode(Path.this);
               }
            }

            if (Path.this.path2d != null) {
               var1.reset();
               var1.next();
               if (var1.getFrom() == var1.getList().size() && !var1.wasRemoved() && var1.wasAdded()) {
                  for (int var6 = var1.getFrom(); var6 < var1.getTo(); var6++) {
                     PathElementHelper.addTo((PathElement)var2.get(var6), Path.this.path2d);
                  }
               } else {
                  Path.this.path2d = null;
               }
            }

            if (var3) {
               Path.this.isPathValid = Path.this.isFirstPathElementValid();
            }

            NodeHelper.markDirty(Path.this, DirtyBits.NODE_CONTENTS);
            NodeHelper.geomChanged(Path.this);
         }
      };
   }

   public Path(PathElement... var1) {
      PathHelper.initHelper(this);
      ((StyleableProperty)this.fillProperty()).applyStyle(null, null);
      ((StyleableProperty)this.strokeProperty()).applyStyle(null, Color.BLACK);
      this.elements = new TrackableObservableList<PathElement>() {
         protected void onChanged(Change<PathElement> var1) {
            ObservableList var2 = var1.getList();

            boolean var3;
            for (var3 = false; var1.next(); var3 |= var1.getFrom() == 0) {
               List var4 = var1.getRemoved();

               for (int var5 = 0; var5 < var1.getRemovedSize(); var5++) {
                  ((PathElement)var4.get(var5)).removeNode(Path.this);
               }

               for (int var7 = var1.getFrom(); var7 < var1.getTo(); var7++) {
                  ((PathElement)var2.get(var7)).addNode(Path.this);
               }
            }

            if (Path.this.path2d != null) {
               var1.reset();
               var1.next();
               if (var1.getFrom() == var1.getList().size() && !var1.wasRemoved() && var1.wasAdded()) {
                  for (int var6 = var1.getFrom(); var6 < var1.getTo(); var6++) {
                     PathElementHelper.addTo((PathElement)var2.get(var6), Path.this.path2d);
                  }
               } else {
                  Path.this.path2d = null;
               }
            }

            if (var3) {
               Path.this.isPathValid = Path.this.isFirstPathElementValid();
            }

            NodeHelper.markDirty(Path.this, DirtyBits.NODE_CONTENTS);
            NodeHelper.geomChanged(Path.this);
         }
      };
      if (var1 != null) {
         this.elements.addAll(var1);
      }
   }

   public Path(Collection<? extends PathElement> var1) {
      PathHelper.initHelper(this);
      ((StyleableProperty)this.fillProperty()).applyStyle(null, null);
      ((StyleableProperty)this.strokeProperty()).applyStyle(null, Color.BLACK);
      this.elements = new TrackableObservableList<PathElement>() {
         protected void onChanged(Change<PathElement> var1) {
            ObservableList var2 = var1.getList();

            boolean var3;
            for (var3 = false; var1.next(); var3 |= var1.getFrom() == 0) {
               List var4 = var1.getRemoved();

               for (int var5 = 0; var5 < var1.getRemovedSize(); var5++) {
                  ((PathElement)var4.get(var5)).removeNode(Path.this);
               }

               for (int var7 = var1.getFrom(); var7 < var1.getTo(); var7++) {
                  ((PathElement)var2.get(var7)).addNode(Path.this);
               }
            }

            if (Path.this.path2d != null) {
               var1.reset();
               var1.next();
               if (var1.getFrom() == var1.getList().size() && !var1.wasRemoved() && var1.wasAdded()) {
                  for (int var6 = var1.getFrom(); var6 < var1.getTo(); var6++) {
                     PathElementHelper.addTo((PathElement)var2.get(var6), Path.this.path2d);
                  }
               } else {
                  Path.this.path2d = null;
               }
            }

            if (var3) {
               Path.this.isPathValid = Path.this.isFirstPathElementValid();
            }

            NodeHelper.markDirty(Path.this, DirtyBits.NODE_CONTENTS);
            NodeHelper.geomChanged(Path.this);
         }
      };
      if (var1 != null) {
         this.elements.addAll(var1);
      }
   }

   void markPathDirty() {
      this.path2d = null;
      NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
      NodeHelper.geomChanged(this);
   }

   public final void setFillRule(FillRule var1) {
      if (this.fillRule != null || var1 != FillRule.NON_ZERO) {
         this.fillRuleProperty().set(var1);
      }
   }

   public final FillRule getFillRule() {
      return this.fillRule == null ? FillRule.NON_ZERO : (FillRule)this.fillRule.get();
   }

   public final ObjectProperty<FillRule> fillRuleProperty() {
      if (this.fillRule == null) {
         this.fillRule = new ObjectPropertyBase<FillRule>(FillRule.NON_ZERO) {
            public void invalidated() {
               NodeHelper.markDirty(Path.this, DirtyBits.NODE_CONTENTS);
               NodeHelper.geomChanged(Path.this);
            }

            public Object getBean() {
               return Path.this;
            }

            public String getName() {
               return "fillRule";
            }
         };
      }

      return this.fillRule;
   }

   public final ObservableList<PathElement> getElements() {
      return this.elements;
   }

   private NGNode doCreatePeer() {
      return new NGPath();
   }

   private Path2D doConfigShape() {
      if (this.isPathValid) {
         if (this.path2d == null) {
            this.path2d = PathUtils.configShape(this.getElements(), this.getFillRule() == FillRule.EVEN_ODD);
         } else {
            this.path2d.setWindingRule(this.getFillRule() == FillRule.NON_ZERO ? 1 : 0);
         }

         return this.path2d;
      } else {
         return new Path2D();
      }
   }

   private Bounds doComputeLayoutBounds() {
      return this.isPathValid ? null : new BoundingBox(0.0, 0.0, -1.0, -1.0);
   }

   private boolean isFirstPathElementValid() {
      ObservableList var1 = this.getElements();
      if (var1 != null && var1.size() > 0) {
         PathElement var2 = (PathElement)var1.get(0);
         if (!var2.isAbsolute()) {
            System.err.printf("First element of the path can not be relative. Path: %s\n", this);
            return false;
         } else if (var2 instanceof MoveTo) {
            return true;
         } else {
            System.err.printf("Missing initial moveto in path definition. Path: %s\n", this);
            return false;
         }
      } else {
         return true;
      }
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
         NGPath var1 = NodeHelper.getPeer(this);
         if (var1.acceptsPath2dOnUpdate()) {
            var1.updateWithPath2d((Path2D)ShapeHelper.configShape(this));
         } else {
            var1.reset();
            if (this.isPathValid) {
               var1.setFillRule(this.getFillRule());

               for (PathElement var3 : this.getElements()) {
                  var3.addTo(var1);
               }

               var1.update();
            }
         }
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
      StringBuilder var1 = new StringBuilder("Path[");
      String var2 = this.getId();
      if (var2 != null) {
         var1.append("id=").append(var2).append(", ");
      }

      var1.append("elements=").append(this.getElements());
      var1.append(", fill=").append(this.getFill());
      var1.append(", fillRule=").append(this.getFillRule());
      Paint var3 = this.getStroke();
      if (var3 != null) {
         var1.append(", stroke=").append(var3);
         var1.append(", strokeWidth=").append(this.getStrokeWidth());
      }

      return var1.append("]").toString();
   }

   static {
      PathHelper.setPathAccessor(new PathHelper.PathAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Path)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((Path)var1).doUpdatePeer();
         }

         @Override
         public Bounds doComputeLayoutBounds(Node var1) {
            return ((Path)var1).doComputeLayoutBounds();
         }

         @Override
         public Paint doCssGetFillInitialValue(Shape var1) {
            return ((Path)var1).doCssGetFillInitialValue();
         }

         @Override
         public Paint doCssGetStrokeInitialValue(Shape var1) {
            return ((Path)var1).doCssGetStrokeInitialValue();
         }

         @Override
         public com.sun.javafx.geom.Shape doConfigShape(Shape var1) {
            return ((Path)var1).doConfigShape();
         }
      });
   }
}
