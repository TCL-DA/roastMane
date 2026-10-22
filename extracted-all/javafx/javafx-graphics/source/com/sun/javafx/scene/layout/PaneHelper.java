package com.sun.javafx.scene.layout;

import com.sun.javafx.util.Utils;
import javafx.scene.layout.Pane;

public class PaneHelper extends RegionHelper {
   private static final PaneHelper theInstance = new PaneHelper();
   private static PaneHelper.PaneAccessor paneAccessor;

   private static PaneHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Pane var0) {
      setHelper(var0, getInstance());
   }

   public static void setPaneAccessor(PaneHelper.PaneAccessor var0) {
      if (paneAccessor != null) {
         throw new IllegalStateException();
      } else {
         paneAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Pane.class);
   }

   public interface PaneAccessor {
   }
}
