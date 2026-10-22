package com.sun.javafx.scene.layout;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class RegionHelper extends ParentHelper {
   private static final RegionHelper theInstance = new RegionHelper();
   private static RegionHelper.RegionAccessor regionAccessor;

   private static RegionHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Region var0) {
      setHelper(var0, getInstance());
   }

   public static BaseBounds superComputeGeomBounds(Node var0, BaseBounds var1, BaseTransform var2) {
      return ((RegionHelper)getHelper(var0)).superComputeGeomBoundsImpl(var0, var1, var2);
   }

   @Override
   protected NGNode createPeerImpl(Node var1) {
      return regionAccessor.doCreatePeer(var1);
   }

   @Override
   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      regionAccessor.doUpdatePeer(var1);
   }

   BaseBounds superComputeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return super.computeGeomBoundsImpl(var1, var2, var3);
   }

   @Override
   protected Bounds computeLayoutBoundsImpl(Node var1) {
      return regionAccessor.doComputeLayoutBounds(var1);
   }

   @Override
   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return regionAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   @Override
   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return regionAccessor.doComputeContains(var1, var2, var4);
   }

   @Override
   protected void notifyLayoutBoundsChangedImpl(Node var1) {
      regionAccessor.doNotifyLayoutBoundsChanged(var1);
   }

   @Override
   protected void pickNodeLocalImpl(Node var1, PickRay var2, PickResultChooser var3) {
      regionAccessor.doPickNodeLocal(var1, var2, var3);
   }

   public static void setRegionAccessor(RegionHelper.RegionAccessor var0) {
      if (regionAccessor != null) {
         throw new IllegalStateException();
      } else {
         regionAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Region.class);
   }

   public interface RegionAccessor {
      void doUpdatePeer(Node var1);

      NGNode doCreatePeer(Node var1);

      Bounds doComputeLayoutBounds(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      void doNotifyLayoutBoundsChanged(Node var1);

      void doPickNodeLocal(Node var1, PickRay var2, PickResultChooser var3);
   }
}
