package javafx.scene;

import com.sun.javafx.scene.GroupHelper;
import java.util.Collection;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;

@DefaultProperty("children")
public class Group extends Parent {
   private BooleanProperty autoSizeChildren;

   public Group() {
      GroupHelper.initHelper(this);
   }

   public Group(Node... var1) {
      GroupHelper.initHelper(this);
      this.getChildren().addAll(var1);
   }

   public Group(Collection<Node> var1) {
      GroupHelper.initHelper(this);
      this.getChildren().addAll(var1);
   }

   public final void setAutoSizeChildren(boolean var1) {
      this.autoSizeChildrenProperty().set(var1);
   }

   public final boolean isAutoSizeChildren() {
      return this.autoSizeChildren == null ? true : this.autoSizeChildren.get();
   }

   public final BooleanProperty autoSizeChildrenProperty() {
      if (this.autoSizeChildren == null) {
         this.autoSizeChildren = new BooleanPropertyBase(true) {
            protected void invalidated() {
               Group.this.requestLayout();
            }

            public Object getBean() {
               return Group.this;
            }

            public String getName() {
               return "autoSizeChildren";
            }
         };
      }

      return this.autoSizeChildren;
   }

   @Override
   public ObservableList<Node> getChildren() {
      return super.getChildren();
   }

   private Bounds doComputeLayoutBounds() {
      this.layout();
      return null;
   }

   @Override
   public double prefWidth(double var1) {
      if (this.isAutoSizeChildren()) {
         this.layout();
      }

      double var3 = this.getLayoutBounds().getWidth();
      return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
   }

   @Override
   public double prefHeight(double var1) {
      if (this.isAutoSizeChildren()) {
         this.layout();
      }

      double var3 = this.getLayoutBounds().getHeight();
      return !Double.isNaN(var3) && !(var3 < 0.0) ? var3 : 0.0;
   }

   @Override
   public double minHeight(double var1) {
      return this.prefHeight(var1);
   }

   @Override
   public double minWidth(double var1) {
      return this.prefWidth(var1);
   }

   @Override
   protected void layoutChildren() {
      if (this.isAutoSizeChildren()) {
         super.layoutChildren();
      }
   }

   static {
      GroupHelper.setGroupAccessor(new GroupHelper.GroupAccessor() {
         @Override
         public Bounds doComputeLayoutBounds(Node var1) {
            return ((Group)var1).doComputeLayoutBounds();
         }
      });
   }
}
