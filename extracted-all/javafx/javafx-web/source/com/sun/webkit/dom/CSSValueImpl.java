package com.sun.webkit.dom;

import com.sun.webkit.Disposer;
import com.sun.webkit.DisposerRecord;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSValue;

public class CSSValueImpl implements CSSValue {
   private final long peer;
   public static final int CSS_INHERIT = 0;
   public static final int CSS_PRIMITIVE_VALUE = 1;
   public static final int CSS_VALUE_LIST = 2;
   public static final int CSS_CUSTOM = 3;

   CSSValueImpl(long var1) {
      this.peer = var1;
      Disposer.addRecord(this, new CSSValueImpl.SelfDisposer(var1));
   }

   static CSSValue create(long var0) {
      if (var0 == 0L) {
         return null;
      } else {
         switch (getCssValueTypeImpl(var0)) {
            case 1:
               return new CSSPrimitiveValueImpl(var0);
            case 2:
               return new CSSValueListImpl(var0);
            default:
               return new CSSValueImpl(var0);
         }
      }
   }

   long getPeer() {
      return this.peer;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof CSSValueImpl && this.peer == ((CSSValueImpl)var1).peer;
   }

   @Override
   public int hashCode() {
      long var1 = this.peer;
      return (int)(var1 ^ var1 >> 17);
   }

   static long getPeer(CSSValue var0) {
      return var0 == null ? 0L : ((CSSValueImpl)var0).getPeer();
   }

   private static native void dispose(long var0);

   static CSSValue getImpl(long var0) {
      return create(var0);
   }

   @Override
   public String getCssText() {
      return getCssTextImpl(this.getPeer());
   }

   static native String getCssTextImpl(long var0);

   @Override
   public void setCssText(String var1) throws DOMException {
      setCssTextImpl(this.getPeer(), var1);
   }

   static native void setCssTextImpl(long var0, String var2);

   @Override
   public short getCssValueType() {
      return getCssValueTypeImpl(this.getPeer());
   }

   static native short getCssValueTypeImpl(long var0);

   private static class SelfDisposer implements DisposerRecord {
      private final long peer;

      SelfDisposer(long var1) {
         this.peer = var1;
      }

      @Override
      public void dispose() {
         CSSValueImpl.dispose(this.peer);
      }
   }
}
