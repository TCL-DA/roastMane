package javafx.scene.robot;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.GlassRobot;
import com.sun.javafx.FXPermissions;
import com.sun.javafx.tk.Toolkit;
import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public final class Robot {
   private final GlassRobot peer;

   public Robot() {
      Application.checkEventThread();
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(FXPermissions.CREATE_ROBOT_PERMISSION);
      }

      this.peer = Toolkit.getToolkit().createRobot();
      this.peer.create();
   }

   public void keyPress(KeyCode var1) {
      Objects.requireNonNull(var1, "keyCode must not be null");
      this.peer.keyPress(var1);
   }

   public void keyRelease(KeyCode var1) {
      Objects.requireNonNull(var1, "keyCode must not be null");
      this.peer.keyRelease(var1);
   }

   public void keyType(KeyCode var1) {
      Objects.requireNonNull(var1, "keyCode must not be null");
      this.keyPress(var1);
      this.keyRelease(var1);
   }

   public double getMouseX() {
      return this.peer.getMouseX();
   }

   public double getMouseY() {
      return this.peer.getMouseY();
   }

   public Point2D getMousePosition() {
      return new Point2D(this.getMouseX(), this.getMouseY());
   }

   public void mouseMove(double var1, double var3) {
      this.peer.mouseMove(var1, var3);
   }

   public final void mouseMove(Point2D var1) {
      Objects.requireNonNull(var1);
      this.mouseMove(var1.getX(), var1.getY());
   }

   public void mousePress(MouseButton... var1) {
      Objects.requireNonNull(var1, "buttons must not be null");
      this.peer.mousePress(var1);
   }

   public void mouseRelease(MouseButton... var1) {
      Objects.requireNonNull(var1, "buttons must not be null");
      this.peer.mouseRelease(var1);
   }

   public void mouseClick(MouseButton... var1) {
      Objects.requireNonNull(var1, "buttons must not be null");
      this.mousePress(var1);
      this.mouseRelease(var1);
   }

   public void mouseWheel(int var1) {
      this.peer.mouseWheel(var1);
   }

   public Color getPixelColor(double var1, double var3) {
      return this.peer.getPixelColor(var1, var3);
   }

   public Color getPixelColor(Point2D var1) {
      return this.getPixelColor(var1.getX(), var1.getY());
   }

   public WritableImage getScreenCapture(WritableImage var1, double var2, double var4, double var6, double var8, boolean var10) {
      return this.peer.getScreenCapture(var1, var2, var4, var6, var8, var10);
   }

   public WritableImage getScreenCapture(WritableImage var1, double var2, double var4, double var6, double var8) {
      return this.getScreenCapture(var1, var2, var4, var6, var8, true);
   }

   public WritableImage getScreenCapture(WritableImage var1, Rectangle2D var2) {
      Objects.requireNonNull(var2);
      return this.getScreenCapture(var1, var2.getMinX(), var2.getMinY(), var2.getWidth(), var2.getHeight(), true);
   }

   public WritableImage getScreenCapture(WritableImage var1, Rectangle2D var2, boolean var3) {
      Objects.requireNonNull(var2);
      return this.getScreenCapture(var1, var2.getMinX(), var2.getMinY(), var2.getWidth(), var2.getHeight(), var3);
   }
}
