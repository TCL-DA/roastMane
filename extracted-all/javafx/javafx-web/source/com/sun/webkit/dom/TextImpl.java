package com.sun.webkit.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class TextImpl extends CharacterDataImpl implements Text {
   TextImpl(long var1) {
      super(var1);
   }

   static Text getImpl(long var0) {
      return (Text)create(var0);
   }

   @Override
   public String getWholeText() {
      return getWholeTextImpl(this.getPeer());
   }

   static native String getWholeTextImpl(long var0);

   @Override
   public Text splitText(int var1) throws DOMException {
      return getImpl(splitTextImpl(this.getPeer(), var1));
   }

   static native long splitTextImpl(long var0, int var2);

   @Override
   public Text replaceWholeText(String var1) throws DOMException {
      return getImpl(replaceWholeTextImpl(this.getPeer(), var1));
   }

   static native long replaceWholeTextImpl(long var0, String var2);

   @Override
   public boolean isElementContentWhitespace() {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
