package javafx.scene.control;

import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;

public class TreeTablePosition<S, T> extends TablePositionBase<TreeTableColumn<S, T>> {
   private final WeakReference<TreeTableView<S>> controlRef;
   private final WeakReference<TreeItem<S>> treeItemRef;
   int fixedColumnIndex = -1;
   private final int nonFixedColumnIndex;

   public TreeTablePosition(@NamedArg("treeTableView") TreeTableView<S> var1, @NamedArg("row") int var2, @NamedArg("tableColumn") TreeTableColumn<S, T> var3) {
      this(var1, var2, var3, true);
   }

   TreeTablePosition(
      @NamedArg("treeTableView") TreeTableView<S> var1, @NamedArg("row") int var2, @NamedArg("tableColumn") TreeTableColumn<S, T> var3, boolean var4
   ) {
      super(var2, var3);
      this.controlRef = new WeakReference<>(var1);
      this.treeItemRef = new WeakReference<>(var4 ? (var1 != null ? var1.getTreeItem(var2) : null) : null);
      this.nonFixedColumnIndex = var1 != null && var3 != null ? var1.getVisibleLeafIndex(var3) : -1;
   }

   TreeTablePosition(@NamedArg("treeTableView") TreeTablePosition<S, T> var1, @NamedArg("row") int var2) {
      super(var2, var1.getTableColumn());
      this.controlRef = new WeakReference<>(var1.getTreeTableView());
      this.treeItemRef = new WeakReference<>(var1.getTreeItem());
      this.nonFixedColumnIndex = var1.getColumn();
   }

   @Override
   public int getColumn() {
      return this.fixedColumnIndex > -1 ? this.fixedColumnIndex : this.nonFixedColumnIndex;
   }

   public final TreeTableView<S> getTreeTableView() {
      return this.controlRef.get();
   }

   public final TreeTableColumn<S, T> getTableColumn() {
      return (TreeTableColumn<S, T>)super.getTableColumn();
   }

   public final TreeItem<S> getTreeItem() {
      return this.treeItemRef.get();
   }
}
