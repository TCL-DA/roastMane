package javafx.scene.control;

import java.time.LocalDate;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.DateCellSkin;

public class DateCell extends Cell<LocalDate> {
   private static final String DEFAULT_STYLE_CLASS = "date-cell";

   public DateCell() {
      this.getStyleClass().add("date-cell");
      this.setAccessibleRole(AccessibleRole.TEXT);
   }

   public void updateItem(LocalDate var1, boolean var2) {
      super.updateItem(var1, var2);
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new DateCellSkin(this);
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case TEXT:
            if (this.isFocused()) {
               return this.getText();
            }

            return "";
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }
}
