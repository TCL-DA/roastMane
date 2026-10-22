package com.sun.javafx.scene;

import com.sun.glass.ui.Accessible;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.Style;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import javafx.scene.text.Font;

public abstract class NodeHelper {
   private static NodeHelper.NodeAccessor nodeAccessor;

   protected NodeHelper() {
   }

   protected static NodeHelper getHelper(Node var0) {
      NodeHelper var1 = nodeAccessor.getHelper(var0);
      if (var1 == null) {
         String var2;
         if (var0 instanceof Shape) {
            var2 = "Shape";
         } else if (var0 instanceof Shape3D) {
            var2 = "Shape3D";
         } else {
            var2 = "Node";
         }

         throw new UnsupportedOperationException("Applications should not extend the " + var2 + " class directly.");
      } else {
         return var1;
      }
   }

   protected static void setHelper(Node var0, NodeHelper var1) {
      nodeAccessor.setHelper(var0, var1);
   }

   public static NGNode createPeer(Node var0) {
      return getHelper(var0).createPeerImpl(var0);
   }

   public static void markDirty(Node var0, DirtyBits var1) {
      getHelper(var0).markDirtyImpl(var0, var1);
   }

   public static void updatePeer(Node var0) {
      getHelper(var0).updatePeerImpl(var0);
   }

   public static Bounds computeLayoutBounds(Node var0) {
      return getHelper(var0).computeLayoutBoundsImpl(var0);
   }

   public static BaseBounds computeGeomBounds(Node var0, BaseBounds var1, BaseTransform var2) {
      return getHelper(var0).computeGeomBoundsImpl(var0, var1, var2);
   }

   public static void transformsChanged(Node var0) {
      getHelper(var0).transformsChangedImpl(var0);
   }

   public static boolean computeContains(Node var0, double var1, double var3) {
      return getHelper(var0).computeContainsImpl(var0, var1, var3);
   }

   public static void pickNodeLocal(Node var0, PickRay var1, PickResultChooser var2) {
      getHelper(var0).pickNodeLocalImpl(var0, var1, var2);
   }

   public static boolean computeIntersects(Node var0, PickRay var1, PickResultChooser var2) {
      return getHelper(var0).computeIntersectsImpl(var0, var1, var2);
   }

   public static void geomChanged(Node var0) {
      getHelper(var0).geomChangedImpl(var0);
   }

   public static void notifyLayoutBoundsChanged(Node var0) {
      getHelper(var0).notifyLayoutBoundsChangedImpl(var0);
   }

   public static void processCSS(Node var0) {
      getHelper(var0).processCSSImpl(var0);
   }

   protected abstract NGNode createPeerImpl(Node var1);

   protected abstract boolean computeContainsImpl(Node var1, double var2, double var4);

   protected abstract BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3);

   protected void markDirtyImpl(Node var1, DirtyBits var2) {
      nodeAccessor.doMarkDirty(var1, var2);
   }

   protected void updatePeerImpl(Node var1) {
      nodeAccessor.doUpdatePeer(var1);
   }

   protected Bounds computeLayoutBoundsImpl(Node var1) {
      return nodeAccessor.doComputeLayoutBounds(var1);
   }

   protected void transformsChangedImpl(Node var1) {
      nodeAccessor.doTransformsChanged(var1);
   }

   protected void pickNodeLocalImpl(Node var1, PickRay var2, PickResultChooser var3) {
      nodeAccessor.doPickNodeLocal(var1, var2, var3);
   }

   protected boolean computeIntersectsImpl(Node var1, PickRay var2, PickResultChooser var3) {
      return nodeAccessor.doComputeIntersects(var1, var2, var3);
   }

   protected void geomChangedImpl(Node var1) {
      nodeAccessor.doGeomChanged(var1);
   }

   protected void notifyLayoutBoundsChangedImpl(Node var1) {
      nodeAccessor.doNotifyLayoutBoundsChanged(var1);
   }

   protected void processCSSImpl(Node var1) {
      nodeAccessor.doProcessCSS(var1);
   }

   public static boolean isDirty(Node var0, DirtyBits var1) {
      return nodeAccessor.isDirty(var0, var1);
   }

   public static boolean isDirtyEmpty(Node var0) {
      return nodeAccessor.isDirtyEmpty(var0);
   }

   public static void syncPeer(Node var0) {
      nodeAccessor.syncPeer(var0);
   }

   public static <P extends NGNode> P getPeer(Node var0) {
      return nodeAccessor.getPeer(var0);
   }

   public static BaseTransform getLeafTransform(Node var0) {
      return nodeAccessor.getLeafTransform(var0);
   }

   public static void layoutBoundsChanged(Node var0) {
      nodeAccessor.layoutBoundsChanged(var0);
   }

   public static void setShowMnemonics(Node var0, boolean var1) {
      nodeAccessor.setShowMnemonics(var0, var1);
   }

   public static boolean isShowMnemonics(Node var0) {
      return nodeAccessor.isShowMnemonics(var0);
   }

   public static BooleanProperty showMnemonicsProperty(Node var0) {
      return nodeAccessor.showMnemonicsProperty(var0);
   }

   public static boolean traverse(Node var0, Direction var1) {
      return nodeAccessor.traverse(var0, var1);
   }

   public static double getPivotX(Node var0) {
      return nodeAccessor.getPivotX(var0);
   }

   public static double getPivotY(Node var0) {
      return nodeAccessor.getPivotY(var0);
   }

   public static double getPivotZ(Node var0) {
      return nodeAccessor.getPivotZ(var0);
   }

   public static void pickNode(Node var0, PickRay var1, PickResultChooser var2) {
      nodeAccessor.pickNode(var0, var1, var2);
   }

   public static boolean intersects(Node var0, PickRay var1, PickResultChooser var2) {
      return nodeAccessor.intersects(var0, var1, var2);
   }

   public static double intersectsBounds(Node var0, PickRay var1) {
      return nodeAccessor.intersectsBounds(var0, var1);
   }

   public static void layoutNodeForPrinting(Node var0) {
      nodeAccessor.layoutNodeForPrinting(var0);
   }

   public static boolean isDerivedDepthTest(Node var0) {
      return nodeAccessor.isDerivedDepthTest(var0);
   }

   public static SubScene getSubScene(Node var0) {
      return nodeAccessor.getSubScene(var0);
   }

   public static Accessible getAccessible(Node var0) {
      return nodeAccessor.getAccessible(var0);
   }

   public static void reapplyCSS(Node var0) {
      nodeAccessor.reapplyCSS(var0);
   }

   public static void recalculateRelativeSizeProperties(Node var0, Font var1) {
      nodeAccessor.recalculateRelativeSizeProperties(var0, var1);
   }

   public static boolean isTreeVisible(Node var0) {
      return nodeAccessor.isTreeVisible(var0);
   }

   public static BooleanExpression treeVisibleProperty(Node var0) {
      return nodeAccessor.treeVisibleProperty(var0);
   }

   public static boolean isTreeShowing(Node var0) {
      return nodeAccessor.isTreeShowing(var0);
   }

   public static List<Style> getMatchingStyles(CssMetaData var0, Styleable var1) {
      return nodeAccessor.getMatchingStyles(var0, var1);
   }

   public static Map<StyleableProperty<?>, List<Style>> findStyles(Node var0, Map<StyleableProperty<?>, List<Style>> var1) {
      return nodeAccessor.findStyles(var0, var1);
   }

   public static void setNodeAccessor(NodeHelper.NodeAccessor var0) {
      if (nodeAccessor != null) {
         throw new IllegalStateException();
      } else {
         nodeAccessor = var0;
      }
   }

   public static NodeHelper.NodeAccessor getNodeAccessor() {
      if (nodeAccessor == null) {
         throw new IllegalStateException();
      } else {
         return nodeAccessor;
      }
   }

   static {
      Utils.forceInit(Node.class);
   }

   public interface NodeAccessor {
      NodeHelper getHelper(Node var1);

      void setHelper(Node var1, NodeHelper var2);

      void doMarkDirty(Node var1, DirtyBits var2);

      void doUpdatePeer(Node var1);

      BaseTransform getLeafTransform(Node var1);

      Bounds doComputeLayoutBounds(Node var1);

      void doTransformsChanged(Node var1);

      void doPickNodeLocal(Node var1, PickRay var2, PickResultChooser var3);

      boolean doComputeIntersects(Node var1, PickRay var2, PickResultChooser var3);

      void doGeomChanged(Node var1);

      void doNotifyLayoutBoundsChanged(Node var1);

      void doProcessCSS(Node var1);

      boolean isDirty(Node var1, DirtyBits var2);

      boolean isDirtyEmpty(Node var1);

      void syncPeer(Node var1);

      <P extends NGNode> P getPeer(Node var1);

      void layoutBoundsChanged(Node var1);

      void setShowMnemonics(Node var1, boolean var2);

      boolean isShowMnemonics(Node var1);

      BooleanProperty showMnemonicsProperty(Node var1);

      boolean traverse(Node var1, Direction var2);

      double getPivotX(Node var1);

      double getPivotY(Node var1);

      double getPivotZ(Node var1);

      void pickNode(Node var1, PickRay var2, PickResultChooser var3);

      boolean intersects(Node var1, PickRay var2, PickResultChooser var3);

      double intersectsBounds(Node var1, PickRay var2);

      void layoutNodeForPrinting(Node var1);

      boolean isDerivedDepthTest(Node var1);

      SubScene getSubScene(Node var1);

      void setLabeledBy(Node var1, Node var2);

      Accessible getAccessible(Node var1);

      void reapplyCSS(Node var1);

      void recalculateRelativeSizeProperties(Node var1, Font var2);

      boolean isTreeVisible(Node var1);

      BooleanExpression treeVisibleProperty(Node var1);

      boolean isTreeShowing(Node var1);

      List<Style> getMatchingStyles(CssMetaData var1, Styleable var2);

      Map<StyleableProperty<?>, List<Style>> findStyles(Node var1, Map<StyleableProperty<?>, List<Style>> var2);
   }
}
