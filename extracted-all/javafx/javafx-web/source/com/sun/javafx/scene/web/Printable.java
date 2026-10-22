package com.sun.javafx.scene.web;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.prism.Graphics;
import com.sun.webkit.WebPage;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCGraphicsManager;
import javafx.scene.Node;

public final class Printable extends Node {
   private final WebPage page;
   private final NGNode peer;

   public Printable(WebPage var1, int var2, float var3) {
      this.page = var1;
      this.peer = new Printable.Peer(var2, var3);
      PrintableHelper.initHelper(this);
   }

   private NGNode doCreatePeer() {
      return this.peer;
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      return var1;
   }

   private boolean doComputeContains(double var1, double var3) {
      return false;
   }

   static {
      PrintableHelper.setPrintableAccessor(new PrintableHelper.PrintableAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((Printable)var1).doCreatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((Printable)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((Printable)var1).doComputeContains(var2, var4);
         }
      });
   }

   private final class Peer extends NGNode {
      private final int pageIndex;
      private final float width;

      Peer(int var2, float var3) {
         this.pageIndex = var2;
         this.width = var3;
      }

      protected void renderContent(Graphics var1) {
         WCGraphicsContext var2 = WCGraphicsManager.getGraphicsManager().createGraphicsContext(var1);
         Printable.this.page.print(var2, this.pageIndex, this.width);
      }

      protected boolean hasOverlappingContents() {
         return false;
      }
   }
}
