package com.sun.webkit.perf;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.prism.paint.Color;
import com.sun.webkit.graphics.Ref;
import com.sun.webkit.graphics.RenderTheme;
import com.sun.webkit.graphics.ScrollBarTheme;
import com.sun.webkit.graphics.WCFont;
import com.sun.webkit.graphics.WCGradient;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCIcon;
import com.sun.webkit.graphics.WCImage;
import com.sun.webkit.graphics.WCPath;
import com.sun.webkit.graphics.WCPoint;
import com.sun.webkit.graphics.WCRectangle;
import com.sun.webkit.graphics.WCTransform;
import java.nio.ByteBuffer;

public final class WCGraphicsPerfLogger extends WCGraphicsContext {
   private static final PlatformLogger log = PlatformLogger.getLogger(WCGraphicsPerfLogger.class.getName());
   private static final PerfLogger logger = PerfLogger.getLogger(log);
   private final WCGraphicsContext gc;

   public WCGraphicsPerfLogger(WCGraphicsContext var1) {
      this.gc = var1;
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
   public Object getPlatformGraphics() {
      return this.gc.getPlatformGraphics();
   }

   @Override
   public boolean isValid() {
      return this.gc.isValid();
   }

   @Override
   public void drawString(WCFont var1, int[] var2, float[] var3, float var4, float var5) {
      logger.resumeCount("DRAWSTRING_GV");
      this.gc.drawString(var1, var2, var3, var4, var5);
      logger.suspendCount("DRAWSTRING_GV");
   }

   @Override
   public void strokeRect(float var1, float var2, float var3, float var4, float var5) {
      logger.resumeCount("STROKERECT_FFFFF");
      this.gc.strokeRect(var1, var2, var3, var4, var5);
      logger.suspendCount("STROKERECT_FFFFF");
   }

   @Override
   public void fillRect(float var1, float var2, float var3, float var4, Color var5) {
      logger.resumeCount("FILLRECT_FFFFI");
      this.gc.fillRect(var1, var2, var3, var4, var5);
      logger.suspendCount("FILLRECT_FFFFI");
   }

   @Override
   public void fillRoundedRect(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      Color var13
   ) {
      logger.resumeCount("FILL_ROUNDED_RECT");
      this.gc.fillRoundedRect(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      logger.suspendCount("FILL_ROUNDED_RECT");
   }

   @Override
   public void clearRect(float var1, float var2, float var3, float var4) {
      logger.resumeCount("CLEARRECT");
      this.gc.clearRect(var1, var2, var3, var4);
      logger.suspendCount("CLEARRECT");
   }

   @Override
   public void setFillColor(Color var1) {
      logger.resumeCount("SETFILLCOLOR");
      this.gc.setFillColor(var1);
      logger.suspendCount("SETFILLCOLOR");
   }

   @Override
   public void setFillGradient(WCGradient var1) {
      logger.resumeCount("SET_FILL_GRADIENT");
      this.gc.setFillGradient(var1);
      logger.suspendCount("SET_FILL_GRADIENT");
   }

   @Override
   public void setTextMode(boolean var1, boolean var2, boolean var3) {
      logger.resumeCount("SET_TEXT_MODE");
      this.gc.setTextMode(var1, var2, var3);
      logger.suspendCount("SET_TEXT_MODE");
   }

   @Override
   public void setFontSmoothingType(int var1) {
      logger.resumeCount("SET_FONT_SMOOTHING_TYPE");
      this.gc.setFontSmoothingType(var1);
      logger.suspendCount("SET_FONT_SMOOTHING_TYPE");
   }

   @Override
   public int getFontSmoothingType() {
      logger.resumeCount("GET_FONT_SMOOTHING_TYPE");
      int var1 = this.gc.getFontSmoothingType();
      logger.suspendCount("GET_FONT_SMOOTHING_TYPE");
      return var1;
   }

   @Override
   public void setStrokeStyle(int var1) {
      logger.resumeCount("SETSTROKESTYLE");
      this.gc.setStrokeStyle(var1);
      logger.suspendCount("SETSTROKESTYLE");
   }

   @Override
   public void setStrokeColor(Color var1) {
      logger.resumeCount("SETSTROKECOLOR");
      this.gc.setStrokeColor(var1);
      logger.suspendCount("SETSTROKECOLOR");
   }

   @Override
   public void setStrokeWidth(float var1) {
      logger.resumeCount("SETSTROKEWIDTH");
      this.gc.setStrokeWidth(var1);
      logger.suspendCount("SETSTROKEWIDTH");
   }

   @Override
   public void setStrokeGradient(WCGradient var1) {
      logger.resumeCount("SET_STROKE_GRADIENT");
      this.gc.setStrokeGradient(var1);
      logger.suspendCount("SET_STROKE_GRADIENT");
   }

   @Override
   public void setLineDash(float var1, float... var2) {
      logger.resumeCount("SET_LINE_DASH");
      this.gc.setLineDash(var1, var2);
      logger.suspendCount("SET_LINE_DASH");
   }

   @Override
   public void setLineCap(int var1) {
      logger.resumeCount("SET_LINE_CAP");
      this.gc.setLineCap(var1);
      logger.suspendCount("SET_LINE_CAP");
   }

   @Override
   public void setLineJoin(int var1) {
      logger.resumeCount("SET_LINE_JOIN");
      this.gc.setLineJoin(var1);
      logger.suspendCount("SET_LINE_JOIN");
   }

   @Override
   public void setMiterLimit(float var1) {
      logger.resumeCount("SET_MITER_LIMIT");
      this.gc.setMiterLimit(var1);
      logger.suspendCount("SET_MITER_LIMIT");
   }

   @Override
   public void setShadow(float var1, float var2, float var3, Color var4) {
      logger.resumeCount("SETSHADOW");
      this.gc.setShadow(var1, var2, var3, var4);
      logger.suspendCount("SETSHADOW");
   }

   @Override
   public void drawPolygon(WCPath var1, boolean var2) {
      logger.resumeCount("DRAWPOLYGON");
      this.gc.drawPolygon(var1, var2);
      logger.suspendCount("DRAWPOLYGON");
   }

   @Override
   public void drawLine(int var1, int var2, int var3, int var4) {
      logger.resumeCount("DRAWLINE");
      this.gc.drawLine(var1, var2, var3, var4);
      logger.suspendCount("DRAWLINE");
   }

   @Override
   public void drawImage(WCImage var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      logger.resumeCount("DRAWIMAGE");
      this.gc.drawImage(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      logger.suspendCount("DRAWIMAGE");
   }

   @Override
   public void drawIcon(WCIcon var1, int var2, int var3) {
      logger.resumeCount("DRAWICON");
      this.gc.drawIcon(var1, var2, var3);
      logger.suspendCount("DRAWICON");
   }

   @Override
   public void drawPattern(WCImage var1, WCRectangle var2, WCTransform var3, WCPoint var4, WCRectangle var5) {
      logger.resumeCount("DRAWPATTERN");
      this.gc.drawPattern(var1, var2, var3, var4, var5);
      logger.suspendCount("DRAWPATTERN");
   }

   @Override
   public void translate(float var1, float var2) {
      logger.resumeCount("TRANSLATE");
      this.gc.translate(var1, var2);
      logger.suspendCount("TRANSLATE");
   }

   @Override
   public void scale(float var1, float var2) {
      logger.resumeCount("SCALE");
      this.gc.scale(var1, var2);
      logger.suspendCount("SCALE");
   }

   @Override
   public void rotate(float var1) {
      logger.resumeCount("ROTATE");
      this.gc.rotate(var1);
      logger.suspendCount("ROTATE");
   }

   @Override
   public void saveState() {
      logger.resumeCount("SAVESTATE");
      this.gc.saveState();
      logger.suspendCount("SAVESTATE");
   }

   @Override
   public void restoreState() {
      logger.resumeCount("RESTORESTATE");
      this.gc.restoreState();
      logger.suspendCount("RESTORESTATE");
   }

   @Override
   public void setClip(WCPath var1, boolean var2) {
      logger.resumeCount("CLIP_PATH");
      this.gc.setClip(var1, var2);
      logger.suspendCount("CLIP_PATH");
   }

   @Override
   public void setClip(WCRectangle var1) {
      logger.resumeCount("SETCLIP_R");
      this.gc.setClip(var1);
      logger.suspendCount("SETCLIP_R");
   }

   @Override
   public void setClip(int var1, int var2, int var3, int var4) {
      logger.resumeCount("SETCLIP_IIII");
      this.gc.setClip(var1, var2, var3, var4);
      logger.suspendCount("SETCLIP_IIII");
   }

   @Override
   public WCRectangle getClip() {
      logger.resumeCount("SETCLIP_IIII");
      WCRectangle var1 = this.gc.getClip();
      logger.suspendCount("SETCLIP_IIII");
      return var1;
   }

   @Override
   public void drawRect(int var1, int var2, int var3, int var4) {
      logger.resumeCount("DRAWRECT");
      this.gc.drawRect(var1, var2, var3, var4);
      logger.suspendCount("DRAWRECT");
   }

   @Override
   public void setComposite(int var1) {
      logger.resumeCount("SETCOMPOSITE");
      this.gc.setComposite(var1);
      logger.suspendCount("SETCOMPOSITE");
   }

   @Override
   public void strokeArc(int var1, int var2, int var3, int var4, int var5, int var6) {
      logger.resumeCount("STROKEARC");
      this.gc.strokeArc(var1, var2, var3, var4, var5, var6);
      logger.suspendCount("STROKEARC");
   }

   @Override
   public void drawEllipse(int var1, int var2, int var3, int var4) {
      logger.resumeCount("DRAWELLIPSE");
      this.gc.drawEllipse(var1, var2, var3, var4);
      logger.suspendCount("DRAWELLIPSE");
   }

   @Override
   public void drawFocusRing(int var1, int var2, int var3, int var4, Color var5) {
      logger.resumeCount("DRAWFOCUSRING");
      this.gc.drawFocusRing(var1, var2, var3, var4, var5);
      logger.suspendCount("DRAWFOCUSRING");
   }

   @Override
   public void setAlpha(float var1) {
      logger.resumeCount("SETALPHA");
      this.gc.setAlpha(var1);
      logger.suspendCount("SETALPHA");
   }

   @Override
   public float getAlpha() {
      logger.resumeCount("GETALPHA");
      float var1 = this.gc.getAlpha();
      logger.suspendCount("GETALPHA");
      return var1;
   }

   @Override
   public void beginTransparencyLayer(float var1) {
      logger.resumeCount("BEGINTRANSPARENCYLAYER");
      this.gc.beginTransparencyLayer(var1);
      logger.suspendCount("BEGINTRANSPARENCYLAYER");
   }

   @Override
   public void endTransparencyLayer() {
      logger.resumeCount("ENDTRANSPARENCYLAYER");
      this.gc.endTransparencyLayer();
      logger.suspendCount("ENDTRANSPARENCYLAYER");
   }

   @Override
   public void drawString(WCFont var1, String var2, boolean var3, int var4, int var5, float var6, float var7) {
      logger.resumeCount("DRAWSTRING");
      this.gc.drawString(var1, var2, var3, var4, var5, var6, var7);
      logger.suspendCount("DRAWSTRING");
   }

   @Override
   public void strokePath(WCPath var1) {
      logger.resumeCount("STROKE_PATH");
      this.gc.strokePath(var1);
      logger.suspendCount("STROKE_PATH");
   }

   @Override
   public void fillPath(WCPath var1) {
      logger.resumeCount("FILL_PATH");
      this.gc.fillPath(var1);
      logger.suspendCount("FILL_PATH");
   }

   @Override
   public WCImage getImage() {
      logger.resumeCount("GETIMAGE");
      WCImage var1 = this.gc.getImage();
      logger.suspendCount("GETIMAGE");
      return var1;
   }

   @Override
   public void drawWidget(RenderTheme var1, Ref var2, int var3, int var4) {
      logger.resumeCount("DRAWWIDGET");
      this.gc.drawWidget(var1, var2, var3, var4);
      logger.suspendCount("DRAWWIDGET");
   }

   @Override
   public void drawScrollbar(ScrollBarTheme var1, Ref var2, int var3, int var4, int var5, int var6) {
      logger.resumeCount("DRAWSCROLLBAR");
      this.gc.drawScrollbar(var1, var2, var3, var4, var5, var6);
      logger.suspendCount("DRAWSCROLLBAR");
   }

   @Override
   public void dispose() {
      logger.resumeCount("DISPOSE");
      this.gc.dispose();
      logger.suspendCount("DISPOSE");
   }

   @Override
   public void flush() {
      logger.resumeCount("FLUSH");
      this.gc.flush();
      logger.suspendCount("FLUSH");
   }

   @Override
   public void setPerspectiveTransform(WCTransform var1) {
      logger.resumeCount("SETPERSPECTIVETRANSFORM");
      this.gc.setPerspectiveTransform(var1);
      logger.suspendCount("SETPERSPECTIVETRANSFORM");
   }

   @Override
   public void setTransform(WCTransform var1) {
      logger.resumeCount("SETTRANSFORM");
      this.gc.setTransform(var1);
      logger.suspendCount("SETTRANSFORM");
   }

   @Override
   public WCTransform getTransform() {
      logger.resumeCount("GETTRANSFORM");
      WCTransform var1 = this.gc.getTransform();
      logger.suspendCount("GETTRANSFORM");
      return var1;
   }

   @Override
   public void concatTransform(WCTransform var1) {
      logger.resumeCount("CONCATTRANSFORM");
      this.gc.concatTransform(var1);
      logger.suspendCount("CONCATTRANSFORM");
   }

   @Override
   public void drawBitmapImage(ByteBuffer var1, int var2, int var3, int var4, int var5) {
      logger.resumeCount("DRAWBITMAPIMAGE");
      this.gc.drawBitmapImage(var1, var2, var3, var4, var5);
      logger.suspendCount("DRAWBITMAPIMAGE");
   }

   @Override
   public WCGradient createLinearGradient(WCPoint var1, WCPoint var2) {
      logger.resumeCount("CREATE_LINEAR_GRADIENT");
      WCGradient var3 = this.gc.createLinearGradient(var1, var2);
      logger.suspendCount("CREATE_LINEAR_GRADIENT");
      return var3;
   }

   @Override
   public WCGradient createRadialGradient(WCPoint var1, float var2, WCPoint var3, float var4) {
      logger.resumeCount("CREATE_RADIAL_GRADIENT");
      WCGradient var5 = this.gc.createRadialGradient(var1, var2, var3, var4);
      logger.suspendCount("CREATE_RADIAL_GRADIENT");
      return var5;
   }
}
