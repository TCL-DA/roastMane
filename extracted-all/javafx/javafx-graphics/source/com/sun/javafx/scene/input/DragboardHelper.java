package com.sun.javafx.scene.input;

import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.util.Utils;
import javafx.scene.input.Dragboard;

public class DragboardHelper {
   private static DragboardHelper.DragboardAccessor dragboardAccessor;

   private DragboardHelper() {
   }

   public static void setDataAccessRestriction(Dragboard var0, boolean var1) {
      dragboardAccessor.setDataAccessRestriction(var0, var1);
   }

   public static TKClipboard getPeer(Dragboard var0) {
      return dragboardAccessor.getPeer(var0);
   }

   public static Dragboard createDragboard(TKClipboard var0) {
      return dragboardAccessor.createDragboard(var0);
   }

   public static void setDragboardAccessor(DragboardHelper.DragboardAccessor var0) {
      if (dragboardAccessor != null) {
         throw new IllegalStateException();
      } else {
         dragboardAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Dragboard.class);
   }

   public interface DragboardAccessor {
      void setDataAccessRestriction(Dragboard var1, boolean var2);

      TKClipboard getPeer(Dragboard var1);

      Dragboard createDragboard(TKClipboard var1);
   }
}
