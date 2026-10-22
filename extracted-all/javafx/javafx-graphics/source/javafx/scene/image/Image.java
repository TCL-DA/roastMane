package javafx.scene.image;

import com.sun.javafx.runtime.async.AsyncOperation;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.PlatformImage;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.DataURI;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.regex.Pattern;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Image {
   private static final Pattern URL_QUICKMATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$");
   private final String url;
   private final InputStream inputSource;
   private ReadOnlyDoubleWrapper progress;
   private final double requestedWidth;
   private final double requestedHeight;
   private Image.DoublePropertyImpl width;
   private Image.DoublePropertyImpl height;
   private final boolean preserveRatio;
   private final boolean smooth;
   private final boolean backgroundLoading;
   private ReadOnlyBooleanWrapper error;
   private ReadOnlyObjectWrapper<Exception> exception;
   private Image.ObjectPropertyImpl<PlatformImage> platformImage;
   private Image.ImageTask backgroundTask;
   private Image.Animation animation;
   private volatile boolean isAnimated;
   private PlatformImage[] animFrames;
   private static final int MAX_RUNNING_TASKS = 4;
   private static int runningTasks = 0;
   private static final Queue<Image.ImageTask> pendingTasks = new LinkedList<>();
   private PixelReader reader;

   public final String getUrl() {
      return this.url;
   }

   final InputStream getInputSource() {
      return this.inputSource;
   }

   final void setProgress(double var1) {
      this.progressPropertyImpl().set(var1);
   }

   public final double getProgress() {
      return this.progress == null ? 0.0 : this.progress.get();
   }

   public final ReadOnlyDoubleProperty progressProperty() {
      return this.progressPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyDoubleWrapper progressPropertyImpl() {
      if (this.progress == null) {
         this.progress = new ReadOnlyDoubleWrapper(this, "progress");
      }

      return this.progress;
   }

   public final double getRequestedWidth() {
      return this.requestedWidth;
   }

   public final double getRequestedHeight() {
      return this.requestedHeight;
   }

   public final double getWidth() {
      return this.width == null ? 0.0 : this.width.get();
   }

   public final ReadOnlyDoubleProperty widthProperty() {
      return this.widthPropertyImpl();
   }

   private Image.DoublePropertyImpl widthPropertyImpl() {
      if (this.width == null) {
         this.width = new Image.DoublePropertyImpl("width");
      }

      return this.width;
   }

   public final double getHeight() {
      return this.height == null ? 0.0 : this.height.get();
   }

   public final ReadOnlyDoubleProperty heightProperty() {
      return this.heightPropertyImpl();
   }

   private Image.DoublePropertyImpl heightPropertyImpl() {
      if (this.height == null) {
         this.height = new Image.DoublePropertyImpl("height");
      }

      return this.height;
   }

   public final boolean isPreserveRatio() {
      return this.preserveRatio;
   }

   public final boolean isSmooth() {
      return this.smooth;
   }

   public final boolean isBackgroundLoading() {
      return this.backgroundLoading;
   }

   private void setError(boolean var1) {
      this.errorPropertyImpl().set(var1);
   }

   public final boolean isError() {
      return this.error == null ? false : this.error.get();
   }

   public final ReadOnlyBooleanProperty errorProperty() {
      return this.errorPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyBooleanWrapper errorPropertyImpl() {
      if (this.error == null) {
         this.error = new ReadOnlyBooleanWrapper(this, "error");
      }

      return this.error;
   }

   private void setException(Exception var1) {
      this.exceptionPropertyImpl().set(var1);
   }

   public final Exception getException() {
      return this.exception == null ? null : (Exception)this.exception.get();
   }

   public final ReadOnlyObjectProperty<Exception> exceptionProperty() {
      return this.exceptionPropertyImpl().getReadOnlyProperty();
   }

   private ReadOnlyObjectWrapper<Exception> exceptionPropertyImpl() {
      if (this.exception == null) {
         this.exception = new ReadOnlyObjectWrapper(this, "exception");
      }

      return this.exception;
   }

   final Object getPlatformImage() {
      return this.platformImage == null ? null : this.platformImage.get();
   }

   final ReadOnlyObjectProperty<PlatformImage> acc_platformImageProperty() {
      return this.platformImagePropertyImpl();
   }

   private Image.ObjectPropertyImpl<PlatformImage> platformImagePropertyImpl() {
      if (this.platformImage == null) {
         this.platformImage = new Image.ObjectPropertyImpl<>("platformImage");
      }

      return this.platformImage;
   }

   void pixelsDirty() {
      this.platformImagePropertyImpl().fireValueChangedEvent();
   }

   public Image(@NamedArg("url") String var1) {
      this(validateUrl(var1), null, 0.0, 0.0, false, false, false);
      this.initialize(null);
   }

   public Image(@NamedArg("url") String var1, @NamedArg("backgroundLoading") boolean var2) {
      this(validateUrl(var1), null, 0.0, 0.0, false, false, var2);
      this.initialize(null);
   }

   public Image(
      @NamedArg("url") String var1,
      @NamedArg("requestedWidth") double var2,
      @NamedArg("requestedHeight") double var4,
      @NamedArg("preserveRatio") boolean var6,
      @NamedArg("smooth") boolean var7
   ) {
      this(validateUrl(var1), null, var2, var4, var6, var7, false);
      this.initialize(null);
   }

   public Image(
      @NamedArg(value = "url",defaultValue = "\"\"") String var1,
      @NamedArg("requestedWidth") double var2,
      @NamedArg("requestedHeight") double var4,
      @NamedArg("preserveRatio") boolean var6,
      @NamedArg(value = "smooth",defaultValue = "true") boolean var7,
      @NamedArg("backgroundLoading") boolean var8
   ) {
      this(validateUrl(var1), null, var2, var4, var6, var7, var8);
      this.initialize(null);
   }

   public Image(@NamedArg("is") InputStream var1) {
      this(null, validateInputStream(var1), 0.0, 0.0, false, false, false);
      this.initialize(null);
   }

   public Image(
      @NamedArg("is") InputStream var1,
      @NamedArg("requestedWidth") double var2,
      @NamedArg("requestedHeight") double var4,
      @NamedArg("preserveRatio") boolean var6,
      @NamedArg("smooth") boolean var7
   ) {
      this(null, validateInputStream(var1), var2, var4, var6, var7, false);
      this.initialize(null);
   }

   Image(int var1, int var2) {
      this(null, null, (double)var1, (double)var2, false, false, false);
      if (var1 > 0 && var2 > 0) {
         this.initialize(Toolkit.getToolkit().createPlatformImage(var1, var2));
      } else {
         throw new IllegalArgumentException("Image dimensions must be positive (w,h > 0)");
      }
   }

   Image(PixelBuffer var1) {
      this(null, null, (double)var1.getWidth(), (double)var1.getHeight(), false, false, false);
      this.initialize(var1);
   }

   private Image(Object var1) {
      this(null, null, 0.0, 0.0, false, false, false);
      this.initialize(var1);
   }

   private Image(String var1, InputStream var2, double var3, double var5, boolean var7, boolean var8, boolean var9) {
      this.url = var1;
      this.inputSource = var2;
      this.requestedWidth = var3;
      this.requestedHeight = var5;
      this.preserveRatio = var7;
      this.smooth = var8;
      this.backgroundLoading = var9;
   }

   public void cancel() {
      if (this.backgroundTask != null) {
         this.backgroundTask.cancel();
      }
   }

   void dispose() {
      this.cancel();
      Platform.runLater(() -> {
         if (this.animation != null) {
            this.animation.stop();
         }
      });
   }

   private void initialize(Object var1) {
      if (var1 != null) {
         ImageLoader var2 = loadPlatformImage(var1);
         this.finishImage(var2);
      } else if (this.isBackgroundLoading() && this.inputSource == null) {
         this.loadInBackground();
      } else {
         ImageLoader var3;
         if (this.inputSource != null) {
            var3 = loadImage(this.inputSource, this.getRequestedWidth(), this.getRequestedHeight(), this.isPreserveRatio(), this.isSmooth());
         } else {
            var3 = loadImage(this.getUrl(), this.getRequestedWidth(), this.getRequestedHeight(), this.isPreserveRatio(), this.isSmooth());
         }

         this.finishImage(var3);
      }
   }

   private void finishImage(ImageLoader var1) {
      Exception var2 = var1.getException();
      if (var2 != null) {
         this.finishImage(var2);
      } else {
         if (var1.getFrameCount() > 1) {
            this.initializeAnimatedImage(var1);
         } else {
            PlatformImage var3 = var1.getFrame(0);
            double var4 = var1.getWidth() / (double)var3.getPixelScale();
            double var6 = var1.getHeight() / (double)var3.getPixelScale();
            this.setPlatformImageWH(var3, var4, var6);
         }

         this.setProgress(1.0);
      }
   }

   private void finishImage(Exception var1) {
      this.setException(var1);
      this.setError(true);
      this.setPlatformImageWH(null, 0.0, 0.0);
      this.setProgress(1.0);
   }

   private void initializeAnimatedImage(ImageLoader var1) {
      int var2 = var1.getFrameCount();
      this.animFrames = new PlatformImage[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this.animFrames[var3] = var1.getFrame(var3);
      }

      PlatformImage var8 = var1.getFrame(0);
      double var4 = var1.getWidth() / (double)var8.getPixelScale();
      double var6 = var1.getHeight() / (double)var8.getPixelScale();
      this.setPlatformImageWH(var8, var4, var6);
      this.isAnimated = true;
      Platform.runLater(() -> {
         this.animation = new Image.Animation(this, var1);
         this.animation.start();
      });
   }

   private void cycleTasks() {
      synchronized (pendingTasks) {
         runningTasks--;
         Image.ImageTask var2 = pendingTasks.poll();
         if (var2 != null) {
            runningTasks++;
            var2.start();
         }
      }
   }

   private void loadInBackground() {
      this.backgroundTask = new Image.ImageTask();
      synchronized (pendingTasks) {
         if (runningTasks >= 4) {
            pendingTasks.offer(this.backgroundTask);
         } else {
            runningTasks++;
            this.backgroundTask.start();
         }
      }
   }

   static Image fromPlatformImage(Object var0) {
      return new Image(var0);
   }

   private void setPlatformImageWH(PlatformImage var1, double var2, double var4) {
      if (Toolkit.getImageAccessor().getPlatformImage(this) != var1 || this.getWidth() != var2 || this.getHeight() != var4) {
         Object var6 = Toolkit.getImageAccessor().getPlatformImage(this);
         double var7 = this.getWidth();
         double var9 = this.getHeight();
         this.storePlatformImageWH(var1, var2, var4);
         if (var6 != var1) {
            this.platformImagePropertyImpl().fireValueChangedEvent();
         }

         if (var7 != var2) {
            this.widthPropertyImpl().fireValueChangedEvent();
         }

         if (var9 != var4) {
            this.heightPropertyImpl().fireValueChangedEvent();
         }
      }
   }

   private void storePlatformImageWH(PlatformImage var1, double var2, double var4) {
      this.platformImagePropertyImpl().store(var1);
      this.widthPropertyImpl().store(var2);
      this.heightPropertyImpl().store(var4);
   }

   void setPlatformImage(PlatformImage var1) {
      this.platformImage.set(var1);
   }

   private static ImageLoader loadImage(String var0, double var1, double var3, boolean var5, boolean var6) {
      return Toolkit.getToolkit().loadImage(var0, var1, var3, var5, var6);
   }

   private static ImageLoader loadImage(InputStream var0, double var1, double var3, boolean var5, boolean var6) {
      return Toolkit.getToolkit().loadImage(var0, var1, var3, var5, var6);
   }

   private static AsyncOperation loadImageAsync(
      AsyncOperationListener<? extends ImageLoader> var0, String var1, double var2, double var4, boolean var6, boolean var7
   ) {
      return Toolkit.getToolkit().loadImageAsync(var0, var1, var2, var4, var6, var7);
   }

   private static ImageLoader loadPlatformImage(Object var0) {
      return Toolkit.getToolkit().loadPlatformImage(var0);
   }

   private static String validateUrl(String var0) {
      if (var0 == null) {
         throw new NullPointerException("URL must not be null");
      } else if (var0.trim().isEmpty()) {
         throw new IllegalArgumentException("URL must not be empty");
      } else {
         try {
            if (!URL_QUICKMATCH.matcher(var0).matches()) {
               ClassLoader var1 = Thread.currentThread().getContextClassLoader();
               URL var2;
               if (var0.charAt(0) == '/') {
                  var2 = var1.getResource(var0.substring(1));
               } else {
                  var2 = var1.getResource(var0);
               }

               if (var2 == null) {
                  throw new IllegalArgumentException("Invalid URL or resource not found");
               } else {
                  return var2.toString();
               }
            } else if (DataURI.matchScheme(var0)) {
               return var0;
            } else {
               return new File(var0).exists() ? var0 : new URL(var0).toString();
            }
         } catch (MalformedURLException | IllegalArgumentException var3) {
            throw new IllegalArgumentException(constructDetailedExceptionMessage("Invalid URL", var3), var3);
         }
      }
   }

   private static InputStream validateInputStream(InputStream var0) {
      if (var0 == null) {
         throw new NullPointerException("Input stream must not be null");
      } else {
         return var0;
      }
   }

   private static String constructDetailedExceptionMessage(String var0, Throwable var1) {
      if (var1 == null) {
         return var0;
      } else {
         String var2 = var1.getMessage();
         return constructDetailedExceptionMessage(var2 != null ? var0 + ": " + var2 : var0, var1.getCause());
      }
   }

   boolean isAnimation() {
      return this.isAnimated;
   }

   boolean pixelsReadable() {
      return this.getProgress() >= 1.0 && !this.isAnimation() && !this.isError();
   }

   public final PixelReader getPixelReader() {
      if (!this.pixelsReadable()) {
         return null;
      } else {
         if (this.reader == null) {
            this.reader = new PixelReader() {
               @Override
               public PixelFormat getPixelFormat() {
                  PlatformImage var1 = Image.this.platformImage.get();
                  return var1.getPlatformPixelFormat();
               }

               @Override
               public int getArgb(int var1, int var2) {
                  PlatformImage var3 = Image.this.platformImage.get();
                  return var3.getArgb(var1, var2);
               }

               @Override
               public Color getColor(int var1, int var2) {
                  int var3 = this.getArgb(var1, var2);
                  int var4 = var3 >>> 24;
                  int var5 = var3 >> 16 & 0xFF;
                  int var6 = var3 >> 8 & 0xFF;
                  int var7 = var3 & 0xFF;
                  return Color.rgb(var5, var6, var7, (double)var4 / 255.0);
               }

               @Override
               public <T extends Buffer> void getPixels(int var1, int var2, int var3, int var4, WritablePixelFormat<T> var5, T var6, int var7) {
                  PlatformImage var8 = Image.this.platformImage.get();
                  var8.getPixels(var1, var2, var3, var4, var5, var6, var7);
               }

               @Override
               public void getPixels(int var1, int var2, int var3, int var4, WritablePixelFormat<ByteBuffer> var5, byte[] var6, int var7, int var8) {
                  PlatformImage var9 = Image.this.platformImage.get();
                  var9.getPixels(var1, var2, var3, var4, var5, var6, var7, var8);
               }

               @Override
               public void getPixels(int var1, int var2, int var3, int var4, WritablePixelFormat<IntBuffer> var5, int[] var6, int var7, int var8) {
                  PlatformImage var9 = Image.this.platformImage.get();
                  var9.getPixels(var1, var2, var3, var4, var5, var6, var7, var8);
               }
            };
         }

         return this.reader;
      }
   }

   PlatformImage getWritablePlatformImage() {
      PlatformImage var1 = this.platformImage.get();
      if (!var1.isWritable()) {
         var1 = var1.promoteToWritableImage();
         this.platformImage.set(var1);
      }

      return var1;
   }

   static {
      Toolkit.setImageAccessor(new Toolkit.ImageAccessor() {
         @Override
         public boolean isAnimation(Image var1) {
            return var1.isAnimation();
         }

         @Override
         public ReadOnlyObjectProperty<PlatformImage> getImageProperty(Image var1) {
            return var1.acc_platformImageProperty();
         }

         @Override
         public int[] getPreColors(PixelFormat<ByteBuffer> var1) {
            return ((PixelFormat.IndexedPixelFormat)var1).getPreColors();
         }

         @Override
         public int[] getNonPreColors(PixelFormat<ByteBuffer> var1) {
            return ((PixelFormat.IndexedPixelFormat)var1).getNonPreColors();
         }

         @Override
         public Object getPlatformImage(Image var1) {
            return var1.getPlatformImage();
         }

         @Override
         public Image fromPlatformImage(Object var1) {
            return Image.fromPlatformImage(var1);
         }
      });
   }

   private static final class Animation {
      final WeakReference<Image> imageRef;
      final Timeline timeline;
      final SimpleIntegerProperty frameIndex = new SimpleIntegerProperty() {
         protected void invalidated() {
            Animation.this.updateImage(this.get());
         }
      };

      public Animation(Image var1, ImageLoader var2) {
         this.imageRef = new WeakReference<>(var1);
         this.timeline = new Timeline();
         int var3 = var2.getLoopCount();
         this.timeline.setCycleCount(var3 == 0 ? -1 : var3);
         int var4 = var2.getFrameCount();
         int var5 = 0;

         for (int var6 = 0; var6 < var4; var6++) {
            this.addKeyFrame(var6, (double)var5);
            var5 += var2.getFrameDelay(var6);
         }

         this.timeline.getKeyFrames().add(new KeyFrame(Duration.millis((double)var5)));
      }

      public void start() {
         this.timeline.play();
      }

      public void stop() {
         this.timeline.stop();
      }

      private void updateImage(int var1) {
         Image var2 = this.imageRef.get();
         if (var2 != null) {
            var2.platformImagePropertyImpl().set(var2.animFrames[var1]);
         } else {
            this.timeline.stop();
         }
      }

      private void addKeyFrame(int var1, double var2) {
         this.timeline.getKeyFrames().add(new KeyFrame(Duration.millis(var2), new KeyValue(this.frameIndex, var1, Interpolator.DISCRETE)));
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
         return Image.this;
      }

      public String getName() {
         return this.name;
      }
   }

   private final class ImageTask implements AsyncOperationListener<ImageLoader> {
      private final AsyncOperation peer = this.constructPeer();

      public ImageTask() {
      }

      @Override
      public void onCancel() {
         Image.this.finishImage(new CancellationException("Loading cancelled"));
         Image.this.cycleTasks();
      }

      @Override
      public void onException(Exception var1) {
         Image.this.finishImage(var1);
         Image.this.cycleTasks();
      }

      public void onCompletion(ImageLoader var1) {
         Image.this.finishImage(var1);
         Image.this.cycleTasks();
      }

      @Override
      public void onProgress(int var1, int var2) {
         if (var2 > 0) {
            double var3 = (double)var1 / (double)var2;
            if (var3 < 1.0 && var3 >= Image.this.getProgress() + 0.1) {
               Image.this.setProgress(var3);
            }
         }
      }

      public void start() {
         this.peer.start();
      }

      public void cancel() {
         this.peer.cancel();
      }

      private AsyncOperation constructPeer() {
         return Image.loadImageAsync(this, Image.this.url, Image.this.requestedWidth, Image.this.requestedHeight, Image.this.preserveRatio, Image.this.smooth);
      }
   }

   private final class ObjectPropertyImpl<T> extends ReadOnlyObjectPropertyBase<T> {
      private final String name;
      private T value;
      private boolean valid = true;

      public ObjectPropertyImpl(String var2) {
         this.name = var2;
      }

      public void store(T var1) {
         this.value = (T)var1;
      }

      public void set(T var1) {
         if (this.value != var1) {
            this.value = (T)var1;
            this.markInvalid();
         }
      }

      public void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }

      private void markInvalid() {
         if (this.valid) {
            this.valid = false;
            this.fireValueChangedEvent();
         }
      }

      public T get() {
         this.valid = true;
         return this.value;
      }

      public Object getBean() {
         return Image.this;
      }

      public String getName() {
         return this.name;
      }
   }
}
