package com.sun.javafx.webkit.theme;

import com.sun.javafx.scene.control.ControlHelper;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;

public class ScrollBarWidgetHelper extends ControlHelper {
   private static final ScrollBarWidgetHelper theInstance = new ScrollBarWidgetHelper();
   private static ScrollBarWidgetHelper.ScrollBarWidgetAccessor scrollBarWidgetAccessor;

   private static ScrollBarWidgetHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(ScrollBarWidget var0) {
      setHelper(var0, getInstance());
   }

   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      scrollBarWidgetAccessor.doUpdatePeer(var1);
   }

   public static void setScrollBarWidgetAccessor(ScrollBarWidgetHelper.ScrollBarWidgetAccessor var0) {
      if (scrollBarWidgetAccessor != null) {
         throw new IllegalStateException();
      } else {
         scrollBarWidgetAccessor = var0;
      }
   }

   static {
      Utils.forceInit(ScrollBarWidget.class);
   }

   public interface ScrollBarWidgetAccessor {
      void doUpdatePeer(Node var1);
   }
}
