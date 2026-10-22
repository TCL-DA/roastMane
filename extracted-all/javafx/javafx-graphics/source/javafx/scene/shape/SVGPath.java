package javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.shape.SVGPathHelper;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGSVGPath;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Logging;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

public class SVGPath extends Shape {
   private ObjectProperty<FillRule> fillRule;
   private Path2D path2d;
   private StringProperty content;
   private Object svgPathObject;

   public SVGPath() {
      SVGPathHelper.initHelper(this);
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
               NodeHelper.markDirty(SVGPath.this, DirtyBits.SHAPE_FILLRULE);
               NodeHelper.geomChanged(SVGPath.this);
            }

            public Object getBean() {
               return SVGPath.this;
            }

            public String getName() {
               return "fillRule";
            }
         };
      }

      return this.fillRule;
   }

   public final void setContent(String var1) {
      this.contentProperty().set(var1);
   }

   public final String getContent() {
      return this.content == null ? "" : (String)this.content.get();
   }

   public final StringProperty contentProperty() {
      if (this.content == null) {
         this.content = new StringPropertyBase("") {
            public void invalidated() {
               NodeHelper.markDirty(SVGPath.this, DirtyBits.NODE_CONTENTS);
               NodeHelper.geomChanged(SVGPath.this);
               SVGPath.this.path2d = null;
            }

            public Object getBean() {
               return SVGPath.this;
            }

            public String getName() {
               return "content";
            }
         };
      }

      return this.content;
   }

   private NGNode doCreatePeer() {
      return new NGSVGPath();
   }

   private Path2D doConfigShape() {
      if (this.path2d == null) {
         this.path2d = this.createSVGPath2D();
      } else {
         this.path2d.setWindingRule(this.getFillRule() == FillRule.NON_ZERO ? 1 : 0);
      }

      return this.path2d;
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.SHAPE_FILLRULE) || NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
         NGSVGPath var1 = NodeHelper.getPeer(this);
         if (var1.acceptsPath2dOnUpdate()) {
            if (this.svgPathObject == null) {
               this.svgPathObject = new Path2D();
            }

            Path2D var2 = (Path2D)this.svgPathObject;
            var2.setTo((Path2D)ShapeHelper.configShape(this));
         } else {
            this.svgPathObject = this.createSVGPathObject();
         }

         var1.setContent(this.svgPathObject);
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("SVGPath[");
      String var2 = this.getId();
      if (var2 != null) {
         var1.append("id=").append(var2).append(", ");
      }

      var1.append("content=\"").append(this.getContent()).append("\"");
      var1.append(", fill=").append(this.getFill());
      var1.append(", fillRule=").append(this.getFillRule());
      Paint var3 = this.getStroke();
      if (var3 != null) {
         var1.append(", stroke=").append(var3);
         var1.append(", strokeWidth=").append(this.getStrokeWidth());
      }

      return var1.append("]").toString();
   }

   private Path2D createSVGPath2D() {
      try {
         return Toolkit.getToolkit().createSVGPath2D(this);
      } catch (RuntimeException var2) {
         Logging.getJavaFXLogger().warning("Failed to configure svg path \"{0}\": {1}", new Object[]{this.getContent(), var2.getMessage()});
         return Toolkit.getToolkit().createSVGPath2D(new SVGPath());
      }
   }

   private Object createSVGPathObject() {
      try {
         return Toolkit.getToolkit().createSVGPathObject(this);
      } catch (RuntimeException var2) {
         Logging.getJavaFXLogger().warning("Failed to configure svg path \"{0}\": {1}", new Object[]{this.getContent(), var2.getMessage()});
         return Toolkit.getToolkit().createSVGPathObject(new SVGPath());
      }
   }

   static {
      SVGPathHelper.setSVGPathAccessor(new SVGPathHelper.SVGPathAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((SVGPath)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((SVGPath)var1).doUpdatePeer();
         }

         @Override
         public com.sun.javafx.geom.Shape doConfigShape(Shape var1) {
            return ((SVGPath)var1).doConfigShape();
         }
      });
   }
}
