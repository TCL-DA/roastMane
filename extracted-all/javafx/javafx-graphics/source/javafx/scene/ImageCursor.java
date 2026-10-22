package javafx.scene;

import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.ImageCursorFrame;
import com.sun.javafx.tk.Toolkit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.NamedArg;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;

public class ImageCursor extends Cursor {
   private ImageCursor.ObjectPropertyImpl<Image> image;
   private ImageCursor.DoublePropertyImpl hotspotX;
   private ImageCursor.DoublePropertyImpl hotspotY;
   private CursorFrame currentCursorFrame;
   private ImageCursorFrame firstCursorFrame;
   private Map<Object, ImageCursorFrame> otherCursorFrames;
   private int activeCounter;
   private InvalidationListener imageListener;

   public final Image getImage() {
      return this.image == null ? null : this.image.get();
   }

   public final ReadOnlyObjectProperty<Image> imageProperty() {
      return this.imagePropertyImpl();
   }

   private ImageCursor.ObjectPropertyImpl<Image> imagePropertyImpl() {
      if (this.image == null) {
         this.image = new ImageCursor.ObjectPropertyImpl<>("image");
      }

      return this.image;
   }

   public final double getHotspotX() {
      return this.hotspotX == null ? 0.0 : this.hotspotX.get();
   }

   public final ReadOnlyDoubleProperty hotspotXProperty() {
      return this.hotspotXPropertyImpl();
   }

   private ImageCursor.DoublePropertyImpl hotspotXPropertyImpl() {
      if (this.hotspotX == null) {
         this.hotspotX = new ImageCursor.DoublePropertyImpl("hotspotX");
      }

      return this.hotspotX;
   }

   public final double getHotspotY() {
      return this.hotspotY == null ? 0.0 : this.hotspotY.get();
   }

   public final ReadOnlyDoubleProperty hotspotYProperty() {
      return this.hotspotYPropertyImpl();
   }

   private ImageCursor.DoublePropertyImpl hotspotYPropertyImpl() {
      if (this.hotspotY == null) {
         this.hotspotY = new ImageCursor.DoublePropertyImpl("hotspotY");
      }

      return this.hotspotY;
   }

   public ImageCursor() {
   }

   public ImageCursor(@NamedArg("image") Image var1) {
      this(var1, 0.0, 0.0);
   }

   public ImageCursor(@NamedArg("image") Image var1, @NamedArg("hotspotX") double var2, @NamedArg("hotspotY") double var4) {
      if (var1 != null && var1.getProgress() < 1.0) {
         ImageCursor.DelayedInitialization.applyTo(this, var1, var2, var4);
      } else {
         this.initialize(var1, var2, var4);
      }
   }

   public static Dimension2D getBestSize(double var0, double var2) {
      return Toolkit.getToolkit().getBestCursorSize((int)var0, (int)var2);
   }

   public static int getMaximumColors() {
      return Toolkit.getToolkit().getMaximumCursorColors();
   }

   public static ImageCursor chooseBestCursor(Image[] var0, double var1, double var3) {
      ImageCursor var5 = new ImageCursor();
      if (needsDelayedInitialization(var0)) {
         ImageCursor.DelayedInitialization.applyTo(var5, var0, var1, var3);
      } else {
         var5.initialize(var0, var1, var3);
      }

      return var5;
   }

   @Override
   CursorFrame getCurrentFrame() {
      if (this.currentCursorFrame != null) {
         return this.currentCursorFrame;
      } else {
         Image var1 = this.getImage();
         if (var1 == null) {
            this.currentCursorFrame = Cursor.DEFAULT.getCurrentFrame();
            return this.currentCursorFrame;
         } else {
            Object var2 = Toolkit.getImageAccessor().getPlatformImage(var1);
            if (var2 == null) {
               this.currentCursorFrame = Cursor.DEFAULT.getCurrentFrame();
               return this.currentCursorFrame;
            } else {
               if (this.firstCursorFrame == null) {
                  this.firstCursorFrame = new ImageCursorFrame(var2, var1.getWidth(), var1.getHeight(), this.getHotspotX(), this.getHotspotY());
                  this.currentCursorFrame = this.firstCursorFrame;
               } else if (this.firstCursorFrame.getPlatformImage() == var2) {
                  this.currentCursorFrame = this.firstCursorFrame;
               } else {
                  if (this.otherCursorFrames == null) {
                     this.otherCursorFrames = new HashMap<>();
                  }

                  this.currentCursorFrame = this.otherCursorFrames.get(var2);
                  if (this.currentCursorFrame == null) {
                     ImageCursorFrame var3 = new ImageCursorFrame(var2, var1.getWidth(), var1.getHeight(), this.getHotspotX(), this.getHotspotY());
                     this.otherCursorFrames.put(var2, var3);
                     this.currentCursorFrame = var3;
                  }
               }

               return this.currentCursorFrame;
            }
         }
      }
   }

   private void invalidateCurrentFrame() {
      this.currentCursorFrame = null;
   }

   @Override
   void activate() {
      if (++this.activeCounter == 1) {
         this.bindImage(this.getImage());
         this.invalidateCurrentFrame();
      }
   }

   @Override
   void deactivate() {
      if (--this.activeCounter == 0) {
         this.unbindImage(this.getImage());
      }
   }

   private void initialize(Image[] var1, double var2, double var4) {
      Dimension2D var6 = getBestSize(1.0, 1.0);
      if (var1.length != 0 && var6.getWidth() != 0.0 && var6.getHeight() != 0.0) {
         if (var1.length == 1) {
            this.initialize(var1[0], var2, var4);
         } else {
            Image var7 = findBestImage(var1);
            double var8 = var7.getWidth() / var1[0].getWidth();
            double var10 = var7.getHeight() / var1[0].getHeight();
            this.initialize(var7, var2 * var8, var4 * var10);
         }
      }
   }

   private void initialize(Image var1, double var2, double var4) {
      Image var6 = this.getImage();
      double var7 = this.getHotspotX();
      double var9 = this.getHotspotY();
      if (var1 != null && !(var1.getWidth() < 1.0) && !(var1.getHeight() < 1.0)) {
         if (var2 < 0.0) {
            var2 = 0.0;
         }

         if (var2 > var1.getWidth() - 1.0) {
            var2 = var1.getWidth() - 1.0;
         }

         if (var4 < 0.0) {
            var4 = 0.0;
         }

         if (var4 > var1.getHeight() - 1.0) {
            var4 = var1.getHeight() - 1.0;
         }
      } else {
         var2 = 0.0;
         var4 = 0.0;
      }

      this.imagePropertyImpl().store(var1);
      this.hotspotXPropertyImpl().store(var2);
      this.hotspotYPropertyImpl().store(var4);
      if (var6 != var1) {
         if (this.activeCounter > 0) {
            this.unbindImage(var6);
            this.bindImage(var1);
         }

         this.invalidateCurrentFrame();
         this.image.fireValueChangedEvent();
      }

      if (var7 != var2) {
         this.hotspotX.fireValueChangedEvent();
      }

      if (var9 != var4) {
         this.hotspotY.fireValueChangedEvent();
      }
   }

   private InvalidationListener getImageListener() {
      if (this.imageListener == null) {
         this.imageListener = var1 -> this.invalidateCurrentFrame();
      }

      return this.imageListener;
   }

   private void bindImage(Image var1) {
      if (var1 != null) {
         Toolkit.getImageAccessor().getImageProperty(var1).addListener(this.getImageListener());
      }
   }

   private void unbindImage(Image var1) {
      if (var1 != null) {
         Toolkit.getImageAccessor().getImageProperty(var1).removeListener(this.getImageListener());
      }
   }

   private static boolean needsDelayedInitialization(Image[] var0) {
      for (Image var4 : var0) {
         if (var4.getProgress() < 1.0) {
            return true;
         }
      }

      return false;
   }

   private static Image findBestImage(Image[] var0) {
      for (Image var4 : var0) {
         Dimension2D var5 = getBestSize((double)((int)var4.getWidth()), (double)((int)var4.getHeight()));
         if (var5.getWidth() == var4.getWidth() && var5.getHeight() == var4.getHeight()) {
            return var4;
         }
      }

      Image var15 = null;
      double var16 = Double.MAX_VALUE;

      for (Image var7 : var0) {
         if (var7.getWidth() > 0.0 && var7.getHeight() > 0.0) {
            Dimension2D var8 = getBestSize(var7.getWidth(), var7.getHeight());
            double var9 = var8.getWidth() / var7.getWidth();
            double var11 = var8.getHeight() / var7.getHeight();
            if (var9 >= 1.0 && var11 >= 1.0) {
               double var13 = Math.max(var9, var11);
               if (var13 < var16) {
                  var15 = var7;
                  var16 = var13;
               }
            }
         }
      }

      if (var15 != null) {
         return var15;
      } else {
         for (Image var22 : var0) {
            if (var22.getWidth() > 0.0 && var22.getHeight() > 0.0) {
               Dimension2D var23 = getBestSize(var22.getWidth(), var22.getHeight());
               if (var23.getWidth() > 0.0 && var23.getHeight() > 0.0) {
                  double var24 = var23.getWidth() / var22.getWidth();
                  if (var24 < 1.0) {
                     var24 = 1.0 / var24;
                  }

                  double var25 = var23.getHeight() / var22.getHeight();
                  if (var25 < 1.0) {
                     var25 = 1.0 / var25;
                  }

                  double var26 = Math.max(var24, var25);
                  if (var26 < var16) {
                     var15 = var22;
                     var16 = var26;
                  }
               }
            }
         }

         return var15 != null ? var15 : var0[0];
      }
   }

   private static final class DelayedInitialization implements InvalidationListener {
      private final ImageCursor targetCursor;
      private final Image[] images;
      private final double hotspotX;
      private final double hotspotY;
      private final boolean initAsSingle;
      private int waitForImages;

      private DelayedInitialization(ImageCursor var1, Image[] var2, double var3, double var5, boolean var7) {
         this.targetCursor = var1;
         this.images = var2;
         this.hotspotX = var3;
         this.hotspotY = var5;
         this.initAsSingle = var7;
      }

      public static void applyTo(ImageCursor var0, Image[] var1, double var2, double var4) {
         ImageCursor.DelayedInitialization var6 = new ImageCursor.DelayedInitialization(var0, Arrays.copyOf(var1, var1.length), var2, var4, false);
         var6.start();
      }

      public static void applyTo(ImageCursor var0, Image var1, double var2, double var4) {
         ImageCursor.DelayedInitialization var6 = new ImageCursor.DelayedInitialization(var0, new Image[]{var1}, var2, var4, true);
         var6.start();
      }

      private void start() {
         for (Image var4 : this.images) {
            if (var4.getProgress() < 1.0) {
               this.waitForImages++;
               var4.progressProperty().addListener(this);
            }
         }
      }

      private void cleanupAndFinishInitialization() {
         for (Image var4 : this.images) {
            var4.progressProperty().removeListener(this);
         }

         if (this.initAsSingle) {
            this.targetCursor.initialize(this.images[0], this.hotspotX, this.hotspotY);
         } else {
            this.targetCursor.initialize(this.images, this.hotspotX, this.hotspotY);
         }
      }

      public void invalidated(Observable var1) {
         if (((ReadOnlyDoubleProperty)var1).get() == 1.0 && --this.waitForImages == 0) {
            this.cleanupAndFinishInitialization();
         }
      }
   }

   private final class DoublePropertyImpl extends ReadOnlyDoublePropertyBase {
      private final String name;
      private double value;

      public DoublePropertyImpl(String var2) {
         this.name = var2;
      }

      public void store(double var1) {
         this.value = var1;
      }

      public void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }

      public double get() {
         return this.value;
      }

      public Object getBean() {
         return ImageCursor.this;
      }

      public String getName() {
         return this.name;
      }
   }

   private final class ObjectPropertyImpl<T> extends ReadOnlyObjectPropertyBase<T> {
      private final String name;
      private T value;

      public ObjectPropertyImpl(String var2) {
         this.name = var2;
      }

      public void store(T var1) {
         this.value = (T)var1;
      }

      public void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }

      public T get() {
         return this.value;
      }

      public Object getBean() {
         return ImageCursor.this;
      }

      public String getName() {
         return this.name;
      }
   }
}
