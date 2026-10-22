package com.sun.webkit.perf;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.webkit.graphics.WCFont;
import com.sun.webkit.graphics.WCTextRun;

public final class WCFontPerfLogger extends WCFont {
   private static final PlatformLogger log = PlatformLogger.getLogger(WCFontPerfLogger.class.getName());
   private static final PerfLogger logger = PerfLogger.getLogger(log);
   private final WCFont fnt;

   public WCFontPerfLogger(WCFont var1) {
      this.fnt = var1;
   }

   public static synchronized boolean isEnabled() {
      return logger.isEnabled();
   }

   public static void log() {
      logger.log();
   }

   public static void reset() {
      logger.reset();
   }

   @Override
   public Object getPlatformFont() {
      return this.fnt.getPlatformFont();
   }

   @Override
   public WCFont deriveFont(float var1) {
      logger.resumeCount("DERIVEFONT");
      WCFont var2 = this.fnt.deriveFont(var1);
      logger.suspendCount("DERIVEFONT");
      return var2;
   }

   @Override
   public WCTextRun[] getTextRuns(String var1) {
      logger.resumeCount("GETTEXTRUNS");
      WCTextRun[] var2 = this.fnt.getTextRuns(var1);
      logger.suspendCount("GETTEXTRUNS");
      return var2;
   }

   @Override
   public int[] getGlyphCodes(char[] var1) {
      logger.resumeCount("GETGLYPHCODES");
      int[] var2 = this.fnt.getGlyphCodes(var1);
      logger.suspendCount("GETGLYPHCODES");
      return var2;
   }

   @Override
   public float getXHeight() {
      logger.resumeCount("GETXHEIGHT");
      float var1 = this.fnt.getXHeight();
      logger.suspendCount("GETXHEIGHT");
      return var1;
   }

   @Override
   public double getGlyphWidth(int var1) {
      logger.resumeCount("GETGLYPHWIDTH");
      double var2 = this.fnt.getGlyphWidth(var1);
      logger.suspendCount("GETGLYPHWIDTH");
      return var2;
   }

   @Override
   public float[] getGlyphBoundingBox(int var1) {
      logger.resumeCount("GETGLYPHBOUNDINGBOX");
      float[] var2 = this.fnt.getGlyphBoundingBox(var1);
      logger.suspendCount("GETGLYPHBOUNDINGBOX");
      return var2;
   }

   @Override
   public int hashCode() {
      logger.resumeCount("HASH");
      int var1 = this.fnt.hashCode();
      logger.suspendCount("HASH");
      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      logger.resumeCount("COMPARE");
      boolean var2 = this.fnt.equals(var1);
      logger.suspendCount("COMPARE");
      return var2;
   }

   @Override
   public float getAscent() {
      logger.resumeCount("GETASCENT");
      float var1 = this.fnt.getAscent();
      logger.suspendCount("GETASCENT");
      return var1;
   }

   @Override
   public float getDescent() {
      logger.resumeCount("GETDESCENT");
      float var1 = this.fnt.getDescent();
      logger.suspendCount("GETDESCENT");
      return var1;
   }

   @Override
   public float getLineSpacing() {
      logger.resumeCount("GETLINESPACING");
      float var1 = this.fnt.getLineSpacing();
      logger.suspendCount("GETLINESPACING");
      return var1;
   }

   @Override
   public float getLineGap() {
      logger.resumeCount("GETLINEGAP");
      float var1 = this.fnt.getLineGap();
      logger.suspendCount("GETLINEGAP");
      return var1;
   }

   @Override
   public boolean hasUniformLineMetrics() {
      logger.resumeCount("HASUNIFORMLINEMETRICS");
      boolean var1 = this.fnt.hasUniformLineMetrics();
      logger.suspendCount("HASUNIFORMLINEMETRICS");
      return var1;
   }

   @Override
   public float getCapHeight() {
      logger.resumeCount("GETCAPHEIGHT");
      float var1 = this.fnt.getCapHeight();
      logger.suspendCount("GETCAPHEIGHT");
      return var1;
   }
}
