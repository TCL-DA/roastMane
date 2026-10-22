package com.sun.javafx.scene.control;

import com.sun.javafx.util.Utils;
import javafx.scene.control.TableColumnBase;

public class TableColumnBaseHelper {
   private static TableColumnBaseHelper.TableColumnBaseAccessor tableColumnBaseAccessor;

   private TableColumnBaseHelper() {
   }

   public static void setWidth(TableColumnBase var0, double var1) {
      tableColumnBaseAccessor.setWidth(var0, var1);
   }

   public static void setTableColumnBaseAccessor(TableColumnBaseHelper.TableColumnBaseAccessor var0) {
      if (tableColumnBaseAccessor != null) {
         throw new IllegalStateException();
      } else {
         tableColumnBaseAccessor = var0;
      }
   }

   static {
      Utils.forceInit(TableColumnBase.class);
   }

   public interface TableColumnBaseAccessor {
      void setWidth(TableColumnBase var1, double var2);
   }
}
