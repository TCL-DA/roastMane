package com.sun.javafx.scene.web;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;

public class PrintableHelper extends NodeHelper {
   private static final PrintableHelper theInstance = new PrintableHelper();
   private static PrintableHelper.PrintableAccessor printableAccessor;

   private static PrintableHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Printable var0) {
      setHelper(var0, getInstance());
   }

   public static void setPrintableAccessor(PrintableHelper.PrintableAccessor var0) {
      if (printableAccessor != null) {
         throw new IllegalStateException();
      } else {
         printableAccessor = var0;
      }
   }

   protected NGNode createPeerImpl(Node var1) {
      return printableAccessor.doCreatePeer(var1);
   }

   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return printableAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return printableAccessor.doComputeContains(var1, var2, var4);
   }

   static {
      Utils.forceInit(Printable.class);
   }

   public interface PrintableAccessor {
      NGNode doCreatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);
   }
}
