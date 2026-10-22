package com.phidget22;

public class LCDBase extends Phidget {
   LCDBase(long var1) {
      super(var1);
   }

   public LCDBase() throws PhidgetException {
      super(create());
   }

   private static native long create() throws PhidgetException;

   private final void setDeviceEvents(boolean var1) {
   }

   public native double getBacklight() throws PhidgetException;

   public native void setBacklight(double var1) throws PhidgetException;

   public native double getMinBacklight() throws PhidgetException;

   public native double getMaxBacklight() throws PhidgetException;

   public native void setCharacterBitmap(LCDFont var1, String var2, byte[] var3) throws PhidgetException;

   public native void setCharacterBitmap(LCDFont var1, String var2, byte[] var3, AsyncListener var4);

   public native int getMaxCharacters(LCDFont var1) throws PhidgetException;

   public native void clear(AsyncListener var1);

   public native void clear() throws PhidgetException;

   public native double getContrast() throws PhidgetException;

   public native void setContrast(double var1) throws PhidgetException;

   public native double getMinContrast() throws PhidgetException;

   public native double getMaxContrast() throws PhidgetException;

   public native void copy(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) throws PhidgetException;

   public native void copy(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, AsyncListener var10);

   public native boolean getCursorBlink() throws PhidgetException;

   public native void setCursorBlink(boolean var1) throws PhidgetException;

   public native boolean getCursorOn() throws PhidgetException;

   public native void setCursorOn(boolean var1) throws PhidgetException;

   public native void drawLine(int var1, int var2, int var3, int var4, AsyncListener var5);

   public native void drawLine(int var1, int var2, int var3, int var4) throws PhidgetException;

   public native void drawPixel(int var1, int var2, LCDPixelState var3) throws PhidgetException;

   public native void drawPixel(int var1, int var2, LCDPixelState var3, AsyncListener var4);

   public native void drawRect(int var1, int var2, int var3, int var4, boolean var5, boolean var6, AsyncListener var7);

   public native void drawRect(int var1, int var2, int var3, int var4, boolean var5, boolean var6) throws PhidgetException;

   public native void flush() throws PhidgetException;

   public native void flush(AsyncListener var1);

   public native void setFontSize(LCDFont var1, int var2, int var3) throws PhidgetException;

   public native int getFrameBuffer() throws PhidgetException;

   public native void setFrameBuffer(int var1) throws PhidgetException;

   public native void setFrameBuffer(int var1, AsyncListener var2);

   public native int getHeight() throws PhidgetException;

   public native void initialize() throws PhidgetException;

   public native void saveFrameBuffer(int var1, AsyncListener var2);

   public native void saveFrameBuffer(int var1) throws PhidgetException;

   public native LCDScreenSize getScreenSize() throws PhidgetException;

   public native void setScreenSize(LCDScreenSize var1) throws PhidgetException;

   public native boolean getSleeping() throws PhidgetException;

   public native void setSleeping(boolean var1) throws PhidgetException;

   public native int getWidth() throws PhidgetException;

   public native void writeBitmap(int var1, int var2, int var3, int var4, byte[] var5, AsyncListener var6);

   public native void writeBitmap(int var1, int var2, int var3, int var4, byte[] var5) throws PhidgetException;

   public native void writeText(LCDFont var1, int var2, int var3, String var4) throws PhidgetException;

   public native void writeText(LCDFont var1, int var2, int var3, String var4, AsyncListener var5);
}
