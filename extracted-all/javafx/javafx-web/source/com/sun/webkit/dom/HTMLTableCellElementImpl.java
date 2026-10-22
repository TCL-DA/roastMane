package com.sun.webkit.dom;

import org.w3c.dom.html.HTMLTableCellElement;

public class HTMLTableCellElementImpl extends HTMLElementImpl implements HTMLTableCellElement {
   HTMLTableCellElementImpl(long var1) {
      super(var1);
   }

   static HTMLTableCellElement getImpl(long var0) {
      return (HTMLTableCellElement)create(var0);
   }

   @Override
   public int getCellIndex() {
      return getCellIndexImpl(this.getPeer());
   }

   static native int getCellIndexImpl(long var0);

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
   public String getAxis() {
      return getAxisImpl(this.getPeer());
   }

   static native String getAxisImpl(long var0);

   @Override
   public void setAxis(String var1) {
      setAxisImpl(this.getPeer(), var1);
   }

   static native void setAxisImpl(long var0, String var2);

   @Override
   public String getBgColor() {
      return getBgColorImpl(this.getPeer());
   }

   static native String getBgColorImpl(long var0);

   @Override
   public void setBgColor(String var1) {
      setBgColorImpl(this.getPeer(), var1);
   }

   static native void setBgColorImpl(long var0, String var2);

   @Override
   public String getCh() {
      return getChImpl(this.getPeer());
   }

   static native String getChImpl(long var0);

   @Override
   public void setCh(String var1) {
      setChImpl(this.getPeer(), var1);
   }

   static native void setChImpl(long var0, String var2);

   @Override
   public String getChOff() {
      return getChOffImpl(this.getPeer());
   }

   static native String getChOffImpl(long var0);

   @Override
   public void setChOff(String var1) {
      setChOffImpl(this.getPeer(), var1);
   }

   static native void setChOffImpl(long var0, String var2);

   @Override
   public int getColSpan() {
      return getColSpanImpl(this.getPeer());
   }

   static native int getColSpanImpl(long var0);

   @Override
   public void setColSpan(int var1) {
      setColSpanImpl(this.getPeer(), var1);
   }

   static native void setColSpanImpl(long var0, int var2);

   @Override
   public int getRowSpan() {
      return getRowSpanImpl(this.getPeer());
   }

   static native int getRowSpanImpl(long var0);

   @Override
   public void setRowSpan(int var1) {
      setRowSpanImpl(this.getPeer(), var1);
   }

   static native void setRowSpanImpl(long var0, int var2);

   @Override
   public String getHeaders() {
      return getHeadersImpl(this.getPeer());
   }

   static native String getHeadersImpl(long var0);

   @Override
   public void setHeaders(String var1) {
      setHeadersImpl(this.getPeer(), var1);
   }

   static native void setHeadersImpl(long var0, String var2);

   @Override
   public String getHeight() {
      return getHeightImpl(this.getPeer());
   }

   static native String getHeightImpl(long var0);

   @Override
   public void setHeight(String var1) {
      setHeightImpl(this.getPeer(), var1);
   }

   static native void setHeightImpl(long var0, String var2);

   @Override
   public boolean getNoWrap() {
      return getNoWrapImpl(this.getPeer());
   }

   static native boolean getNoWrapImpl(long var0);

   @Override
   public void setNoWrap(boolean var1) {
      setNoWrapImpl(this.getPeer(), var1);
   }

   static native void setNoWrapImpl(long var0, boolean var2);

   @Override
   public String getVAlign() {
      return getVAlignImpl(this.getPeer());
   }

   static native String getVAlignImpl(long var0);

   @Override
   public void setVAlign(String var1) {
      setVAlignImpl(this.getPeer(), var1);
   }

   static native void setVAlignImpl(long var0, String var2);

   @Override
   public String getWidth() {
      return getWidthImpl(this.getPeer());
   }

   static native String getWidthImpl(long var0);

   @Override
   public void setWidth(String var1) {
      setWidthImpl(this.getPeer(), var1);
   }

   static native void setWidthImpl(long var0, String var2);

   @Override
   public String getAbbr() {
      return getAbbrImpl(this.getPeer());
   }

   static native String getAbbrImpl(long var0);

   @Override
   public void setAbbr(String var1) {
      setAbbrImpl(this.getPeer(), var1);
   }

   static native void setAbbrImpl(long var0, String var2);

   @Override
   public String getScope() {
      return getScopeImpl(this.getPeer());
   }

   static native String getScopeImpl(long var0);

   @Override
   public void setScope(String var1) {
      setScopeImpl(this.getPeer(), var1);
   }

   static native void setScopeImpl(long var0, String var2);
}
