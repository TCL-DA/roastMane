package com.sun.javafx.stage;

import com.sun.javafx.util.Utils;
import javafx.stage.Window;

public class EmbeddedWindowHelper extends WindowHelper {
   private static final EmbeddedWindowHelper theInstance = new EmbeddedWindowHelper();
   private static EmbeddedWindowHelper.EmbeddedWindowAccessor embeddedWindowAccessor;

   private static WindowHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(EmbeddedWindow var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void visibleChangingImpl(Window var1, boolean var2) {
      super.visibleChangingImpl(var1, var2);
      embeddedWindowAccessor.doVisibleChanging(var1, var2);
   }

   public static void setEmbeddedWindowAccessor(EmbeddedWindowHelper.EmbeddedWindowAccessor var0) {
      if (embeddedWindowAccessor != null) {
         throw new IllegalStateException();
      } else {
         embeddedWindowAccessor = var0;
      }
   }

   static {
      Utils.forceInit(EmbeddedWindow.class);
   }

   public interface EmbeddedWindowAccessor {
      void doVisibleChanging(Window var1, boolean var2);
   }
}
