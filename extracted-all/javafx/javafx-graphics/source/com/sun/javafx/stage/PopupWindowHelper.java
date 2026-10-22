package com.sun.javafx.stage;

import com.sun.javafx.util.Utils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

public class PopupWindowHelper extends WindowHelper {
   private static final PopupWindowHelper theInstance = new PopupWindowHelper();
   private static PopupWindowHelper.PopupWindowAccessor popupWindowAccessor;

   private static WindowHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(PopupWindow var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void visibleChangingImpl(Window var1, boolean var2) {
      super.visibleChangingImpl(var1, var2);
      popupWindowAccessor.doVisibleChanging(var1, var2);
   }

   @Override
   protected void visibleChangedImpl(Window var1, boolean var2) {
      super.visibleChangedImpl(var1, var2);
      popupWindowAccessor.doVisibleChanged(var1, var2);
   }

   public static ObservableList<Node> getContent(PopupWindow var0) {
      return popupWindowAccessor.getContent(var0);
   }

   public static void setPopupWindowAccessor(PopupWindowHelper.PopupWindowAccessor var0) {
      if (popupWindowAccessor != null) {
         throw new IllegalStateException();
      } else {
         popupWindowAccessor = var0;
      }
   }

   static {
      Utils.forceInit(PopupWindow.class);
   }

   public interface PopupWindowAccessor {
      ObservableList<Node> getContent(PopupWindow var1);

      void doVisibleChanging(Window var1, boolean var2);

      void doVisibleChanged(Window var1, boolean var2);
   }
}
