package com.sun.webkit.dom;

import com.sun.webkit.Disposer;
import com.sun.webkit.DisposerRecord;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;

public class CSSRuleImpl implements CSSRule {
   private final long peer;
   public static final int UNKNOWN_RULE = 0;
   public static final int STYLE_RULE = 1;
   public static final int CHARSET_RULE = 2;
   public static final int IMPORT_RULE = 3;
   public static final int MEDIA_RULE = 4;
   public static final int FONT_FACE_RULE = 5;
   public static final int PAGE_RULE = 6;
   public static final int KEYFRAMES_RULE = 7;
   public static final int KEYFRAME_RULE = 8;
   public static final int SUPPORTS_RULE = 12;
   public static final int WEBKIT_REGION_RULE = 16;
   public static final int WEBKIT_KEYFRAMES_RULE = 7;
   public static final int WEBKIT_KEYFRAME_RULE = 8;

   CSSRuleImpl(long var1) {
      this.peer = var1;
      Disposer.addRecord(this, new CSSRuleImpl.SelfDisposer(var1));
   }

   static CSSRule create(long var0) {
      if (var0 == 0L) {
         return null;
      } else {
         switch (getTypeImpl(var0)) {
            case 1:
               return new CSSStyleRuleImpl(var0);
            case 2:
               return new CSSCharsetRuleImpl(var0);
            case 3:
               return new CSSImportRuleImpl(var0);
            case 4:
               return new CSSMediaRuleImpl(var0);
            case 5:
               return new CSSFontFaceRuleImpl(var0);
            case 6:
               return new CSSPageRuleImpl(var0);
            default:
               return new CSSRuleImpl(var0);
         }
      }
   }

   long getPeer() {
      return this.peer;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof CSSRuleImpl && this.peer == ((CSSRuleImpl)var1).peer;
   }

   @Override
   public int hashCode() {
      long var1 = this.peer;
      return (int)(var1 ^ var1 >> 17);
   }

   static long getPeer(CSSRule var0) {
      return var0 == null ? 0L : ((CSSRuleImpl)var0).getPeer();
   }

   private static native void dispose(long var0);

   static CSSRule getImpl(long var0) {
      return create(var0);
   }

   @Override
   public short getType() {
      return getTypeImpl(this.getPeer());
   }

   static native short getTypeImpl(long var0);

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
   public CSSStyleSheet getParentStyleSheet() {
      return CSSStyleSheetImpl.getImpl(getParentStyleSheetImpl(this.getPeer()));
   }

   static native long getParentStyleSheetImpl(long var0);

   @Override
   public CSSRule getParentRule() {
      return getImpl(getParentRuleImpl(this.getPeer()));
   }

   static native long getParentRuleImpl(long var0);

   private static class SelfDisposer implements DisposerRecord {
      private final long peer;

      SelfDisposer(long var1) {
         this.peer = var1;
      }

      @Override
      public void dispose() {
         CSSRuleImpl.dispose(this.peer);
      }
   }
}
