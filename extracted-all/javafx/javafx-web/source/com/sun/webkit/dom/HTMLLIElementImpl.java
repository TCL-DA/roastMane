package com.sun.webkit.dom;

import org.w3c.dom.html.HTMLLIElement;

public class HTMLLIElementImpl extends HTMLElementImpl implements HTMLLIElement {
   HTMLLIElementImpl(long var1) {
      super(var1);
   }

   static HTMLLIElement getImpl(long var0) {
      return (HTMLLIElement)create(var0);
   }

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

   @Override
   public int getValue() {
      return getValueImpl(this.getPeer());
   }

   static native int getValueImpl(long var0);

   @Override
   public void setValue(int var1) {
      setValueImpl(this.getPeer(), var1);
   }

   static native void setValueImpl(long var0, int var2);
}
