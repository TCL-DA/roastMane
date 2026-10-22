package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Shape;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;

public class SVGPathHelper extends ShapeHelper {
   private static final SVGPathHelper theInstance = new SVGPathHelper();
   private static SVGPathHelper.SVGPathAccessor svgPathAccessor;

   private static SVGPathHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(SVGPath var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return svgPathAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      svgPathAccessor.doUpdatePeer(var1);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return svgPathAccessor.doConfigShape(var1);
   }

   public static void setSVGPathAccessor(SVGPathHelper.SVGPathAccessor var0) {
      if (svgPathAccessor != null) {
         throw new IllegalStateException();
      } else {
         svgPathAccessor = var0;
      }
   }

   static {
      Utils.forceInit(SVGPath.class);
   }

   public interface SVGPathAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
