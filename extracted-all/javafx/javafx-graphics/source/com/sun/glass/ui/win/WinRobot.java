package com.sun.glass.ui.win;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.GlassRobot;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

final class WinRobot extends GlassRobot {
   @Override
   public void create() {
   }

   @Override
   public void destroy() {
   }

   protected native void _keyPress(int var1);

   @Override
   public void keyPress(KeyCode var1) {
      Application.checkEventThread();
      this._keyPress(var1.getCode());
   }

   protected native void _keyRelease(int var1);

   @Override
   public void keyRelease(KeyCode var1) {
      Application.checkEventThread();
      this._keyRelease(var1.getCode());
   }

   protected native void _mouseMove(int var1, int var2);

   @Override
   public void mouseMove(double var1, double var3) {
      Application.checkEventThread();
      this._mouseMove((int)var1, (int)var3);
   }

   protected native void _mousePress(int var1);

   @Override
   public void mousePress(MouseButton... var1) {
      Application.checkEventThread();
      this._mousePress(GlassRobot.convertToRobotMouseButton(var1));
   }

   protected native void _mouseRelease(int var1);

   @Override
   public void mouseRelease(MouseButton... var1) {
      Application.checkEventThread();
      this._mouseRelease(GlassRobot.convertToRobotMouseButton(var1));
   }

   protected native void _mouseWheel(int var1);

   @Override
   public void mouseWheel(int var1) {
      Application.checkEventThread();
      this._mouseWheel(var1);
   }

   protected native float _getMouseX();

   @Override
   public double getMouseX() {
      Application.checkEventThread();
      return (double)this._getMouseX();
   }

   protected native float _getMouseY();

   @Override
   public double getMouseY() {
      Application.checkEventThread();
      return (double)this._getMouseY();
   }

   protected native int _getPixelColor(int var1, int var2);

   @Override
   public Color getPixelColor(double var1, double var3) {
      Application.checkEventThread();
      return GlassRobot.convertFromIntArgb(this._getPixelColor((int)var1, (int)var3));
   }

   protected native void _getScreenCapture(int var1, int var2, int var3, int var4, int[] var5);

   @Override
   public void getScreenCapture(int var1, int var2, int var3, int var4, int[] var5, boolean var6) {
      Application.checkEventThread();
      this._getScreenCapture(var1, var2, var3, var4, var5);
   }
}
