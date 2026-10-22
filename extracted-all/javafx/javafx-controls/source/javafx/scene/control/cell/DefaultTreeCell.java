package javafx.scene.control.cell;

import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

class DefaultTreeCell<T> extends TreeCell<T> {
   private HBox hbox;
   private WeakReference<TreeItem<T>> treeItemRef;
   private InvalidationListener treeItemGraphicListener = var1 -> this.updateDisplay(this.getItem(), this.isEmpty());
   private InvalidationListener treeItemListener = new InvalidationListener() {
      public void invalidated(Observable var1) {
         TreeItem var2 = DefaultTreeCell.this.treeItemRef == null ? null : DefaultTreeCell.this.treeItemRef.get();
         if (var2 != null) {
            var2.graphicProperty().removeListener(DefaultTreeCell.this.weakTreeItemGraphicListener);
         }

         TreeItem var3 = DefaultTreeCell.this.getTreeItem();
         if (var3 != null) {
            var3.graphicProperty().addListener(DefaultTreeCell.this.weakTreeItemGraphicListener);
            DefaultTreeCell.this.treeItemRef = new WeakReference<>(var3);
         }
      }
   };
   private WeakInvalidationListener weakTreeItemGraphicListener = new WeakInvalidationListener(this.treeItemGraphicListener);
   private WeakInvalidationListener weakTreeItemListener = new WeakInvalidationListener(this.treeItemListener);

   public DefaultTreeCell() {
      this.treeItemProperty().addListener(this.weakTreeItemListener);
      if (this.getTreeItem() != null) {
         this.getTreeItem().graphicProperty().addListener(this.weakTreeItemGraphicListener);
      }
   }

   void updateDisplay(T var1, boolean var2) {
      if (var1 != null && !var2) {
         TreeItem var3 = this.getTreeItem();
         if (var3 == null || var3.getGraphic() == null) {
            this.hbox = null;
            if (var1 instanceof Node) {
               this.setText(null);
               this.setGraphic((Node)var1);
            } else {
               this.setText(var1.toString());
               this.setGraphic(null);
            }
         } else if (var1 instanceof Node) {
            this.setText(null);
            if (this.hbox == null) {
               this.hbox = new HBox(3.0);
            }

            this.hbox.getChildren().setAll(new Node[]{var3.getGraphic(), (Node)var1});
            this.setGraphic(this.hbox);
         } else {
            this.hbox = null;
            this.setText(var1.toString());
            this.setGraphic(var3.getGraphic());
         }
      } else {
         this.hbox = null;
         this.setText(null);
         this.setGraphic(null);
      }
   }

   @Override
   public void updateItem(T var1, boolean var2) {
      super.updateItem((T)var1, var2);
      this.updateDisplay((T)var1, var2);
   }
}
