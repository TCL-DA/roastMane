package com.sun.javafx.embed.swing;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Node;
import javax.swing.SwingUtilities;

public class SwingNodeHelper extends NodeHelper {
   private static final SwingNodeHelper theInstance = new SwingNodeHelper();
   private static SwingNodeHelper.SwingNodeAccessor swingNodeAccessor;
   private static final Set<Object> eventLoopKeys = new HashSet<>();

   private static SwingNodeHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(SwingNode var0) {
      setHelper(var0, getInstance());
   }

   protected NGNode createPeerImpl(Node var1) {
      return swingNodeAccessor.doCreatePeer(var1);
   }

   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      swingNodeAccessor.doUpdatePeer(var1);
   }

   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return swingNodeAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return swingNodeAccessor.doComputeContains(var1, var2, var4);
   }

   public static Object getLightweightFrame(SwingNode var0) {
      return swingNodeAccessor.getLightweightFrame(var0);
   }

   public static ReentrantLock getPaintLock(SwingNode var0) {
      return swingNodeAccessor.getPaintLock(var0);
   }

   public static void setImageBuffer(SwingNode var0, int[] var1, int var2, int var3, int var4, int var5, int var6, double var7, double var9) {
      swingNodeAccessor.setImageBuffer(var0, var1, var2, var3, var4, var5, var6, var7, var9);
   }

   public static void setImageBounds(SwingNode var0, int var1, int var2, int var3, int var4) {
      swingNodeAccessor.setImageBounds(var0, var1, var2, var3, var4);
   }

   public static void repaintDirtyRegion(SwingNode var0, int var1, int var2, int var3, int var4) {
      swingNodeAccessor.repaintDirtyRegion(var0, var1, var2, var3, var4);
   }

   public static void ungrabFocus(SwingNode var0, boolean var1) {
      swingNodeAccessor.ungrabFocus(var0, var1);
   }

   public static void setSwingPrefWidth(SwingNode var0, int var1) {
      swingNodeAccessor.setSwingPrefWidth(var0, var1);
   }

   public static void setSwingPrefHeight(SwingNode var0, int var1) {
      swingNodeAccessor.setSwingPrefHeight(var0, var1);
   }

   public static void setSwingMaxWidth(SwingNode var0, int var1) {
      swingNodeAccessor.setSwingMaxWidth(var0, var1);
   }

   public static void setSwingMaxHeight(SwingNode var0, int var1) {
      swingNodeAccessor.setSwingMaxHeight(var0, var1);
   }

   public static void setSwingMinWidth(SwingNode var0, int var1) {
      swingNodeAccessor.setSwingMinWidth(var0, var1);
   }

   public static void setSwingMinHeight(SwingNode var0, int var1) {
      swingNodeAccessor.setSwingMinHeight(var0, var1);
   }

   public static void setGrabbed(SwingNode var0, boolean var1) {
      swingNodeAccessor.setGrabbed(var0, var1);
   }

   public static void runOnFxThread(Runnable var0) {
      if (Platform.isFxApplicationThread()) {
         var0.run();
      } else {
         Platform.runLater(var0);
      }
   }

   public static void runOnEDT(Runnable var0) {
      if (SwingUtilities.isEventDispatchThread()) {
         var0.run();
      } else {
         SwingUtilities.invokeLater(var0);
      }
   }

   public static void runOnEDTAndWait(Object var0, Runnable var1) {
      Toolkit.getToolkit().checkFxUserThread();
      if (SwingUtilities.isEventDispatchThread()) {
         var1.run();
      } else {
         eventLoopKeys.add(var0);
         SwingUtilities.invokeLater(var1);
         Toolkit.getToolkit().enterNestedEventLoop(var0);
      }
   }

   public static void leaveFXNestedLoop(Object var0) {
      if (eventLoopKeys.contains(var0)) {
         if (Platform.isFxApplicationThread()) {
            Toolkit.getToolkit().exitNestedEventLoop(var0, null);
         } else {
            Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(var0, null));
         }

         eventLoopKeys.remove(var0);
      }
   }

   public static void setSwingNodeAccessor(SwingNodeHelper.SwingNodeAccessor var0) {
      if (swingNodeAccessor != null) {
         throw new IllegalStateException();
      } else {
         swingNodeAccessor = var0;
      }
   }

   static {
      Utils.forceInit(SwingNode.class);
   }

   public interface SwingNodeAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      Object getLightweightFrame(SwingNode var1);

      ReentrantLock getPaintLock(SwingNode var1);

      void setImageBuffer(SwingNode var1, int[] var2, int var3, int var4, int var5, int var6, int var7, double var8, double var10);

      void setImageBounds(SwingNode var1, int var2, int var3, int var4, int var5);

      void repaintDirtyRegion(SwingNode var1, int var2, int var3, int var4, int var5);

      void ungrabFocus(SwingNode var1, boolean var2);

      void setSwingPrefWidth(SwingNode var1, int var2);

      void setSwingPrefHeight(SwingNode var1, int var2);

      void setSwingMaxWidth(SwingNode var1, int var2);

      void setSwingMaxHeight(SwingNode var1, int var2);

      void setSwingMinWidth(SwingNode var1, int var2);

      void setSwingMinHeight(SwingNode var1, int var2);

      void setGrabbed(SwingNode var1, boolean var2);
   }
}
