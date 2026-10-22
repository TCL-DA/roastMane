package javafx.scene.shape;

import com.sun.javafx.scene.shape.PathElementHelper;
import com.sun.javafx.sg.prism.NGPath;
import com.sun.javafx.util.WeakReferenceQueue;
import java.util.Iterator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.scene.Node;

public abstract class PathElement {
   private PathElementHelper pathElementHelper = null;
   WeakReferenceQueue nodes = new WeakReferenceQueue();
   private BooleanProperty absolute;

   void addNode(Node var1) {
      this.nodes.add(var1);
   }

   void removeNode(Node var1) {
      this.nodes.remove(var1);
   }

   void u() {
      Iterator var1 = this.nodes.iterator();

      while (var1.hasNext()) {
         ((Path)var1.next()).markPathDirty();
      }
   }

   abstract void addTo(NGPath var1);

   public final void setAbsolute(boolean var1) {
      this.absoluteProperty().set(var1);
   }

   public final boolean isAbsolute() {
      return this.absolute == null || this.absolute.get();
   }

   public final BooleanProperty absoluteProperty() {
      if (this.absolute == null) {
         this.absolute = new BooleanPropertyBase(true) {
            protected void invalidated() {
               PathElement.this.u();
            }

            public Object getBean() {
               return PathElement.this;
            }

            public String getName() {
               return "absolute";
            }
         };
      }

      return this.absolute;
   }

   static {
      PathElementHelper.setPathElementAccessor(new PathElementHelper.PathElementAccessor() {
         @Override
         public PathElementHelper getHelper(PathElement var1) {
            return var1.pathElementHelper;
         }

         @Override
         public void setHelper(PathElement var1, PathElementHelper var2) {
            var1.pathElementHelper = var2;
         }
      });
   }
}
