package com.sun.webkit.dom;

import com.sun.webkit.Disposer;
import com.sun.webkit.DisposerRecord;
import org.w3c.dom.Node;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.stylesheets.StyleSheet;

public class StyleSheetImpl implements StyleSheet {
   private final long peer;
   private static final int TYPE_CSSStyleSheet = 1;

   StyleSheetImpl(long var1) {
      this.peer = var1;
      Disposer.addRecord(this, new StyleSheetImpl.SelfDisposer(var1));
   }

   static StyleSheet create(long var0) {
      if (var0 == 0L) {
         return null;
      } else {
         switch (getCPPTypeImpl(var0)) {
            case 1:
               return new CSSStyleSheetImpl(var0);
            default:
               return new StyleSheetImpl(var0);
         }
      }
   }

   long getPeer() {
      return this.peer;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof StyleSheetImpl && this.peer == ((StyleSheetImpl)var1).peer;
   }

   @Override
   public int hashCode() {
      long var1 = this.peer;
      return (int)(var1 ^ var1 >> 17);
   }

   static long getPeer(StyleSheet var0) {
      return var0 == null ? 0L : ((StyleSheetImpl)var0).getPeer();
   }

   private static native void dispose(long var0);

   private static native int getCPPTypeImpl(long var0);

   static StyleSheet getImpl(long var0) {
      return create(var0);
   }

   @Override
   public String getType() {
      return getTypeImpl(this.getPeer());
   }

   static native String getTypeImpl(long var0);

   @Override
   public boolean getDisabled() {
      return getDisabledImpl(this.getPeer());
   }

   static native boolean getDisabledImpl(long var0);

   @Override
   public void setDisabled(boolean var1) {
      setDisabledImpl(this.getPeer(), var1);
   }

   static native void setDisabledImpl(long var0, boolean var2);

   @Override
   public Node getOwnerNode() {
      return NodeImpl.getImpl(getOwnerNodeImpl(this.getPeer()));
   }

   static native long getOwnerNodeImpl(long var0);

   @Override
   public StyleSheet getParentStyleSheet() {
      return getImpl(getParentStyleSheetImpl(this.getPeer()));
   }

   static native long getParentStyleSheetImpl(long var0);

   @Override
   public String getHref() {
      return getHrefImpl(this.getPeer());
   }

   static native String getHrefImpl(long var0);

   @Override
   public String getTitle() {
      return getTitleImpl(this.getPeer());
   }

   static native String getTitleImpl(long var0);

   @Override
   public MediaList getMedia() {
      return MediaListImpl.getImpl(getMediaImpl(this.getPeer()));
   }

   static native long getMediaImpl(long var0);

   private static class SelfDisposer implements DisposerRecord {
      private final long peer;

      SelfDisposer(long var1) {
         this.peer = var1;
      }

      @Override
      public void dispose() {
         StyleSheetImpl.dispose(this.peer);
      }
   }
}
