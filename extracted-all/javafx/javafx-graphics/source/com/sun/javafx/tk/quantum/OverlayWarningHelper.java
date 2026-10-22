package com.sun.javafx.tk.quantum;

import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.GroupHelper;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;

public class OverlayWarningHelper extends GroupHelper {
   private static final OverlayWarningHelper theInstance = new OverlayWarningHelper();
   private static OverlayWarningHelper.OverlayWarningAccessor overlayWarningAccessor;

   private static OverlayWarningHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(OverlayWarning var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      overlayWarningAccessor.doUpdatePeer(var1);
      super.updatePeerImpl(var1);
   }

   @Override
   protected void markDirtyImpl(Node var1, DirtyBits var2) {
      super.markDirtyImpl(var1, var2);
      overlayWarningAccessor.doMarkDirty(var1, var2);
   }

   public static void setOverlayWarningAccessor(OverlayWarningHelper.OverlayWarningAccessor var0) {
      if (overlayWarningAccessor != null) {
         throw new IllegalStateException();
      } else {
         overlayWarningAccessor = var0;
      }
   }

   static {
      Utils.forceInit(OverlayWarning.class);
   }

   public interface OverlayWarningAccessor {
      void doMarkDirty(Node var1, DirtyBits var2);

      void doUpdatePeer(Node var1);
   }
}
