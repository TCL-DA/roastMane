package com.sun.webkit.dom;

import org.w3c.dom.html.HTMLLinkElement;
import org.w3c.dom.stylesheets.StyleSheet;

public class HTMLLinkElementImpl extends HTMLElementImpl implements HTMLLinkElement {
   HTMLLinkElementImpl(long var1) {
      super(var1);
   }

   static HTMLLinkElement getImpl(long var0) {
      return (HTMLLinkElement)create(var0);
   }

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
   public String getCharset() {
      return getCharsetImpl(this.getPeer());
   }

   static native String getCharsetImpl(long var0);

   @Override
   public void setCharset(String var1) {
      setCharsetImpl(this.getPeer(), var1);
   }

   static native void setCharsetImpl(long var0, String var2);

   @Override
   public String getHref() {
      return getHrefImpl(this.getPeer());
   }

   static native String getHrefImpl(long var0);

   @Override
   public void setHref(String var1) {
      setHrefImpl(this.getPeer(), var1);
   }

   static native void setHrefImpl(long var0, String var2);

   @Override
   public String getHreflang() {
      return getHreflangImpl(this.getPeer());
   }

   static native String getHreflangImpl(long var0);

   @Override
   public void setHreflang(String var1) {
      setHreflangImpl(this.getPeer(), var1);
   }

   static native void setHreflangImpl(long var0, String var2);

   @Override
   public String getMedia() {
      return getMediaImpl(this.getPeer());
   }

   static native String getMediaImpl(long var0);

   @Override
   public void setMedia(String var1) {
      setMediaImpl(this.getPeer(), var1);
   }

   static native void setMediaImpl(long var0, String var2);

   @Override
   public String getRel() {
      return getRelImpl(this.getPeer());
   }

   static native String getRelImpl(long var0);

   @Override
   public void setRel(String var1) {
      setRelImpl(this.getPeer(), var1);
   }

   static native void setRelImpl(long var0, String var2);

   @Override
   public String getRev() {
      return getRevImpl(this.getPeer());
   }

   static native String getRevImpl(long var0);

   @Override
   public void setRev(String var1) {
      setRevImpl(this.getPeer(), var1);
   }

   static native void setRevImpl(long var0, String var2);

   @Override
   public String getTarget() {
      return getTargetImpl(this.getPeer());
   }

   static native String getTargetImpl(long var0);

   @Override
   public void setTarget(String var1) {
      setTargetImpl(this.getPeer(), var1);
   }

   static native void setTargetImpl(long var0, String var2);

   @Override
   public String getType() {
      return getTypeImpl(this.getPeer());
   }

   static native String getTypeImpl(long var0);

   @Override
   public void setType(String var1) {
      setTypeImpl(this.getPeer(), var1);
   }

   static native void setTypeImpl(long var0, String var2);

   public StyleSheet getSheet() {
      return StyleSheetImpl.getImpl(getSheetImpl(this.getPeer()));
   }

   static native long getSheetImpl(long var0);
}
