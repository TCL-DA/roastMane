package javafx.scene.control;

import javafx.beans.NamedArg;

public class ResizeFeaturesBase<S> {
   private final TableColumnBase<S, ?> column;
   private final Double delta;

   public ResizeFeaturesBase(@NamedArg("column") TableColumnBase<S, ?> var1, @NamedArg("delta") Double var2) {
      this.column = var1;
      this.delta = var2;
   }

   public TableColumnBase<S, ?> getColumn() {
      return this.column;
   }

   public Double getDelta() {
      return this.delta;
   }
}
