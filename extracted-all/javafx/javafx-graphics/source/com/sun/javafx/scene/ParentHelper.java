package com.sun.javafx.scene;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ParentHelper extends NodeHelper {
   private static final ParentHelper theInstance = new ParentHelper();
   private static ParentHelper.ParentAccessor parentAccessor;

   private static ParentHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Parent var0) {
      setHelper(var0, getInstance());
   }

   public static void superProcessCSS(Node var0) {
      ((ParentHelper)getHelper(var0)).superProcessCSSImpl(var0);
   }

   public static List<String> getAllParentStylesheets(Parent var0) {
      return ((ParentHelper)getHelper(var0)).getAllParentStylesheetsImpl(var0);
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return parentAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      parentAccessor.doUpdatePeer(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return parentAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return parentAccessor.doComputeContains(var1, var2, var4);
   }

   void superProcessCSSImpl(Node var1) {
      super.processCSSImpl(var1);
   }

   @Override
   protected void processCSSImpl(Node var1) {
      parentAccessor.doProcessCSS(var1);
   }

   protected List<String> getAllParentStylesheetsImpl(Parent var1) {
      return parentAccessor.doGetAllParentStylesheets(var1);
   }

   @Override
   protected void pickNodeLocalImpl(Node var1, PickRay var2, PickResultChooser var3) {
      parentAccessor.doPickNodeLocal(var1, var2, var3);
   }

   public static boolean pickChildrenNode(Parent var0, PickRay var1, PickResultChooser var2) {
      return parentAccessor.pickChildrenNode(var0, var1, var2);
   }

   public static void setTraversalEngine(Parent var0, ParentTraversalEngine var1) {
      parentAccessor.setTraversalEngine(var0, var1);
   }

   public static ParentTraversalEngine getTraversalEngine(Parent var0) {
      return parentAccessor.getTraversalEngine(var0);
   }

   public static void setParentAccessor(ParentHelper.ParentAccessor var0) {
      if (parentAccessor != null) {
         throw new IllegalStateException();
      } else {
         parentAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Parent.class);
   }

   public interface ParentAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      boolean doComputeContains(Node var1, double var2, double var4);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      void doProcessCSS(Node var1);

      void doPickNodeLocal(Node var1, PickRay var2, PickResultChooser var3);

      boolean pickChildrenNode(Parent var1, PickRay var2, PickResultChooser var3);

      void setTraversalEngine(Parent var1, ParentTraversalEngine var2);

      ParentTraversalEngine getTraversalEngine(Parent var1);

      List<String> doGetAllParentStylesheets(Parent var1);
   }
}
