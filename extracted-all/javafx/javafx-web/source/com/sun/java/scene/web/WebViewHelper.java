package com.sun.java.scene.web;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.input.PickResultChooser;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.web.WebView;

public class WebViewHelper extends ParentHelper {
   private static final WebViewHelper theInstance = new WebViewHelper();
   private static WebViewHelper.WebViewAccessor webViewAccessor;

   private static WebViewHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(WebView var0) {
      setHelper(var0, getInstance());
   }

   protected NGNode createPeerImpl(Node var1) {
      return webViewAccessor.doCreatePeer(var1);
   }

   protected void updatePeerImpl(Node var1) {
      super.updatePeerImpl(var1);
      webViewAccessor.doUpdatePeer(var1);
   }

   protected void transformsChangedImpl(Node var1) {
      super.transformsChangedImpl(var1);
      webViewAccessor.doTransformsChanged(var1);
   }

   protected BaseBounds computeGeomBoundsImpl(Node var1, BaseBounds var2, BaseTransform var3) {
      return webViewAccessor.doComputeGeomBounds(var1, var2, var3);
   }

   protected boolean computeContainsImpl(Node var1, double var2, double var4) {
      return webViewAccessor.doComputeContains(var1, var2, var4);
   }

   protected void pickNodeLocalImpl(Node var1, PickRay var2, PickResultChooser var3) {
      webViewAccessor.doPickNodeLocal(var1, var2, var3);
   }

   public static void setWebViewAccessor(WebViewHelper.WebViewAccessor var0) {
      if (webViewAccessor != null) {
         throw new IllegalStateException();
      } else {
         webViewAccessor = var0;
      }
   }

   static {
      Utils.forceInit(WebView.class);
   }

   public interface WebViewAccessor {
      NGNode doCreatePeer(Node var1);

      void doUpdatePeer(Node var1);

      void doTransformsChanged(Node var1);

      BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3);

      boolean doComputeContains(Node var1, double var2, double var4);

      void doPickNodeLocal(Node var1, PickRay var2, PickResultChooser var3);
   }
}
