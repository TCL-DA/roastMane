package javafx.scene.control;

import java.lang.ref.WeakReference;
import javafx.beans.NamedArg;
import javafx.collections.ObservableList;

public class TablePosition<S, T> extends TablePositionBase<TableColumn<S, T>> {
   private final WeakReference<TableView<S>> controlRef;
   private final WeakReference<S> itemRef;
   int fixedColumnIndex = -1;
   private final int nonFixedColumnIndex;

   public TablePosition(@NamedArg("tableView") TableView<S> var1, @NamedArg("row") int var2, @NamedArg("tableColumn") TableColumn<S, T> var3) {
      super(var2, var3);
      this.controlRef = new WeakReference<>(var1);
      ObservableList var4 = var1 != null ? var1.getItems() : null;
      this.itemRef = (WeakReference<S>)(new WeakReference<>(var4 != null && var2 >= 0 && var2 < var4.size() ? var4.get(var2) : null));
      this.nonFixedColumnIndex = var1 != null && var3 != null ? var1.getVisibleLeafIndex(var3) : -1;
   }

   @Override
   public int getColumn() {
      return this.fixedColumnIndex > -1 ? this.fixedColumnIndex : this.nonFixedColumnIndex;
   }

   public final TableView<S> getTableView() {
      return this.controlRef.get();
   }

   public final TableColumn<S, T> getTableColumn() {
      return (TableColumn<S, T>)super.getTableColumn();
   }

   final S getItem() {
      return this.itemRef == null ? null : this.itemRef.get();
   }

   @Override
   public String toString() {
      return "TablePosition [ row: " + this.getRow() + ", column: " + this.getTableColumn() + ", tableView: " + this.getTableView() + " ]";
   }
}
