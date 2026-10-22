package com.sun.webkit.dom;

import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLLegendElement;

public class HTMLLegendElementImpl extends HTMLElementImpl implements HTMLLegendElement {
   HTMLLegendElementImpl(long var1) {
      super(var1);
   }

   static HTMLLegendElement getImpl(long var0) {
      return (HTMLLegendElement)create(var0);
   }

   @Override
   public HTMLFormElement getForm() {
      return HTMLFormElementImpl.getImpl(getFormImpl(this.getPeer()));
   }

   static native long getFormImpl(long var0);

   @Override
   public String getAlign() {
      return getAlignImpl(this.getPeer());
   }

   static native String getAlignImpl(long var0);

   @Override
   public void setAlign(String var1) {
      setAlignImpl(this.getPeer(), var1);
   }

   static native void setAlignImpl(long var0, String var2);

   @Override
   public String getAccessKey() {
      return getAccessKeyImpl(this.getPeer());
   }

   static native String getAccessKeyImpl(long var0);

   @Override
   public void setAccessKey(String var1) {
      setAccessKeyImpl(this.getPeer(), var1);
   }

   static native void setAccessKeyImpl(long var0, String var2);
}
