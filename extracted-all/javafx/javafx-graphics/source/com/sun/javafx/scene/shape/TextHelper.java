package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.text.Text;

public class TextHelper extends ShapeHelper {
   private static final TextHelper theInstance = new TextHelper();
   private static TextHelper.TextAccessor textAccessor;

   private static TextHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Text var0) {
      setHelper(var0, getInstance());
   }

   public static BaseBounds superComputeGeomBounds(Node var0, BaseBounds var1, BaseTransform var2) {
      return ((TextHelper)getHelper(var0)).superComputeGeomBoundsImpl(var0, var1, var2);
   }

   public static Bounds superComputeLayoutBounds(Node var0) {
      return ((TextHelper)getHelper(var0)).superComputeLayoutBoundsImpl(var0);
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return textAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      textAccessor.doUpdatePeer(var1);
   }

   BaseBounds superComputeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return super.computeGeomBoundsImpl(var1, var2, var3);
   }

   Bounds superComputeLayoutBoundsImpl(Node var1) {
      return super.computeLayoutBoundsImpl(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return textAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected Bounds computeLayoutBoundsImpl(Node var1) {
      return textAccessor.doComputeLayoutBounds(var1);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return textAccessor.doComputeContains(var1, var2, var4);
   }

   @Override
   protected void geomChangedImpl(Node var1) {
      super.geomChangedImpl(var1);
      textAccessor.doGeomChanged(var1);
   }

   @Override
   protected Shape configShapeImpl(javafx.scene.shape.Shape var1) {
      return textAccessor.doConfigShape(var1);
   }

   public static void setTextAccessor(TextHelper.TextAccessor var0) {
      if (textAccessor != null) {
         throw new IllegalStateException();
      } else {
         textAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Text.class);
   }

   public interface TextAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      Bounds doComputeLayoutBounds(Node var1);

      boolean doComputeContains(Node var1, double var2, double var4);

      void doGeomChanged(Node var1);

      Shape doConfigShape(javafx.scene.shape.Shape var1);
   }
}
