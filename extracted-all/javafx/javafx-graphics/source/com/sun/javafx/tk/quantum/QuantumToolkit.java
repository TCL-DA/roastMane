package com.sun.javafx.tk.quantum;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.ClipboardAssistance;
import com.sun.glass.ui.CommonDialogs;
import com.sun.glass.ui.EventLoop;
import com.sun.glass.ui.GlassRobot;
import com.sun.glass.ui.Screen;
import com.sun.glass.ui.Timer;
import com.sun.glass.ui.View;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.font.PrismFontLoader;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.logging.PulseLogger;
import com.sun.javafx.perf.PerformanceTracker;
import com.sun.javafx.runtime.async.AbstractRemoteResource;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.scene.input.DragboardHelper;
import com.sun.javafx.scene.text.TextLayoutFactory;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.text.PrismTextLayoutFactory;
import com.sun.javafx.tk.AppletWindow;
import com.sun.javafx.tk.CompletionListener;
import com.sun.javafx.tk.FileChooserType;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.PlatformImage;
import com.sun.javafx.tk.RenderJob;
import com.sun.javafx.tk.ScreenConfigurationAccessor;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.TKDragGestureListener;
import com.sun.javafx.tk.TKDragSourceListener;
import com.sun.javafx.tk.TKDropTargetListener;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKScreenConfigurationListener;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.TKSystemMenu;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.BasicStroke;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import com.sun.prism.RTTexture;
import com.sun.prism.ResourceFactory;
import com.sun.prism.ResourceFactoryListener;
import com.sun.prism.Texture;
import com.sun.prism.impl.Disposer;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.paint.Paint;
import com.sun.prism.paint.Stop;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;
import com.sun.scenario.effect.impl.prism.PrFilterContext;
import com.sun.scenario.effect.impl.prism.PrImage;
import java.io.File;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javafx.application.ConditionalFeature;
import javafx.geometry.Dimension2D;
import javafx.scene.image.PixelBuffer;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class QuantumToolkit extends Toolkit {
   public static final boolean verbose = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("quantum.verbose")));
   public static final boolean pulseDebug = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("quantum.pulse")));
   private static final boolean multithreaded = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
      String var0 = System.getProperty("quantum.multithreaded");
      if (var0 == null) {
         return true;
      } else {
         boolean var1 = Boolean.parseBoolean(var0);
         if (verbose) {
            System.out.println(var1 ? "Multi-Threading Enabled" : "Multi-Threading Disabled");
         }

         return var1;
      }
   }));
   private static boolean debug = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("quantum.debug")));
   private static Integer pulseHZ = AccessController.doPrivileged((PrivilegedAction<Integer>)(() -> Integer.getInteger("javafx.animation.pulse")));
   static final boolean liveResize = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
      boolean var0 = "swt".equals(System.getProperty("glass.platform"));
      String var1 = (PlatformUtil.isMac() || PlatformUtil.isWindows()) && !var0 ? "true" : "false";
      return "true".equals(System.getProperty("javafx.live.resize", var1));
   }));
   static final boolean drawInPaint = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
      boolean var0 = "swt".equals(System.getProperty("glass.platform"));
      String var1 = PlatformUtil.isMac() && var0 ? "true" : "false";
      return "true".equals(System.getProperty("javafx.draw.in.paint", var1));
   }));
   private static boolean singleThreaded = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
      Boolean var0 = Boolean.getBoolean("quantum.singlethreaded");
      if (var0) {
         System.out.println("Warning: Single GUI Threadiong is enabled, FPS should be slower");
      }

      return var0;
   }));
   private static boolean noRenderJobs = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> {
      Boolean var0 = Boolean.getBoolean("quantum.norenderjobs");
      if (var0) {
         System.out.println("Warning: Quantum will not submit render jobs, nothing should draw");
      }

      return var0;
   }));
   private AtomicBoolean toolkitRunning = new AtomicBoolean(false);
   private QuantumToolkit.PulseTask animationRunning = new QuantumToolkit.PulseTask(false);
   private QuantumToolkit.PulseTask nextPulseRequested = new QuantumToolkit.PulseTask(false);
   private AtomicBoolean pulseRunning = new AtomicBoolean(false);
   private int inPulse = 0;
   private CountDownLatch launchLatch = new CountDownLatch(1);
   final int PULSE_INTERVAL = (int)(TimeUnit.SECONDS.toMillis(1L) / (long)this.getRefreshRate());
   final int FULLSPEED_INTERVAL = 1;
   boolean nativeSystemVsync = false;
   private long firstPauseRequestTime = 0L;
   private boolean pauseRequested = false;
   private static final long PAUSE_THRESHOLD_DURATION = 250L;
   private float _maxPixelScale;
   private Runnable pulseRunnable;
   private Runnable userRunnable;
   private Runnable timerRunnable;
   private Timer pulseTimer = null;
   private Thread shutdownHook = null;
   private PaintCollector collector;
   private QuantumRenderer renderer;
   private GraphicsPipeline pipeline;
   private ClassLoader ccl;
   private HashMap<Object, EventLoop> eventLoopMap = null;
   private final PerformanceTracker perfTracker = new PerformanceTrackerImpl();
   private static ScreenConfigurationAccessor screenAccessor = new ScreenConfigurationAccessor() {
      @Override
      public int getMinX(Object var1) {
         return ((Screen)var1).getX();
      }

      @Override
      public int getMinY(Object var1) {
         return ((Screen)var1).getY();
      }

      @Override
      public int getWidth(Object var1) {
         return ((Screen)var1).getWidth();
      }

      @Override
      public int getHeight(Object var1) {
         return ((Screen)var1).getHeight();
      }

      @Override
      public int getVisualMinX(Object var1) {
         return ((Screen)var1).getVisibleX();
      }

      @Override
      public int getVisualMinY(Object var1) {
         return ((Screen)var1).getVisibleY();
      }

      @Override
      public int getVisualWidth(Object var1) {
         return ((Screen)var1).getVisibleWidth();
      }

      @Override
      public int getVisualHeight(Object var1) {
         return ((Screen)var1).getVisibleHeight();
      }

      @Override
      public float getDPI(Object var1) {
         return (float)((Screen)var1).getResolutionX();
      }

      @Override
      public float getRecommendedOutputScaleX(Object var1) {
         return ((Screen)var1).getRecommendedOutputScaleX();
      }

      @Override
      public float getRecommendedOutputScaleY(Object var1) {
         return ((Screen)var1).getRecommendedOutputScaleY();
      }
   };
   private Map<Object, Object> contextMap = Collections.synchronizedMap(new HashMap<>());
   private DelayedRunnable animationRunnable;
   static BasicStroke tmpStroke = new BasicStroke();
   private QuantumClipboard clipboard;
   private GlassSystemMenu systemMenu = new GlassSystemMenu();

   @Override
   public boolean init() {
      this.renderer = QuantumRenderer.getInstance();
      this.collector = PaintCollector.createInstance(this);
      this.pipeline = GraphicsPipeline.getPipeline();
      this.shutdownHook = new Thread("Glass/Prism Shutdown Hook") {
         @Override
         public void run() {
            QuantumToolkit.this.dispose();
         }
      };
      Void var1 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         Runtime.getRuntime().addShutdownHook(this.shutdownHook);
         return null;
      }));
      return true;
   }

   @Override
   public void startup(Runnable var1) {
      this.ccl = Thread.currentThread().getContextClassLoader();

      try {
         this.userRunnable = var1;
         Application.run(() -> this.runToolkit());
      } catch (RuntimeException var4) {
         if (verbose) {
            var4.printStackTrace();
         }

         throw var4;
      } catch (Throwable var5) {
         if (verbose) {
            var5.printStackTrace();
         }

         throw new RuntimeException(var5);
      }

      try {
         this.launchLatch.await();
      } catch (InterruptedException var3) {
         var3.printStackTrace();
      }
   }

   private void assertToolkitRunning() {
   }

   boolean shouldWaitForRenderingToComplete() {
      return !multithreaded;
   }

   private static void initSceneGraph() {
      javafx.stage.Screen.getPrimary();
   }

   void runToolkit() {
      Thread var1 = Thread.currentThread();
      if (!this.toolkitRunning.getAndSet(true)) {
         var1.setName("JavaFX Application Thread");
         var1.setContextClassLoader(this.ccl);
         setFxUserThread(var1);
         assignScreensAdapters();
         this.renderer.createResourceFactory();
         this.pulseRunnable = () -> this.pulseFromQueue();
         this.timerRunnable = () -> {
            try {
               this.postPulse();
            } catch (Throwable var2) {
               var2.printStackTrace(System.err);
            }
         };
         this.pulseTimer = Application.GetApplication().createTimer(this.timerRunnable);
         Application.GetApplication().setEventHandler(new Application.EventHandler() {
            @Override
            public void handleQuitAction(Application var1, long var2) {
               GlassStage.requestClosingAllWindows();
            }

            @Override
            public boolean handleThemeChanged(String var1) {
               String var2 = Application.GetApplication().getHighContrastScheme(var1);
               return PlatformImpl.setAccessibilityTheme(var2);
            }
         });
      }

      initSceneGraph();
      this.launchLatch.countDown();

      try {
         Application.invokeAndWait(this.userRunnable);
         if (this.getPrimaryTimer().isFullspeed()) {
            this.pulseTimer.start(1);
         } else {
            this.nativeSystemVsync = Screen.getVideoRefreshPeriod() != 0.0;
            if (this.nativeSystemVsync) {
               this.pulseTimer.start();
            } else {
               this.pulseTimer.start(this.PULSE_INTERVAL);
            }
         }
      } catch (Throwable var6) {
         var6.printStackTrace(System.err);
      } finally {
         if (PrismSettings.verbose) {
            System.err.println(" vsync: " + PrismSettings.isVsyncEnabled + " vpipe: " + this.pipeline.isVsyncSupported());
         }

         PerformanceTracker.logEvent("Toolkit.startup - finished");
      }
   }

   public static <T> T runWithoutRenderLock(Supplier<T> var0) {
      boolean var1 = ViewPainter.renderLock.isHeldByCurrentThread();

      Object var2;
      try {
         if (var1) {
            ViewPainter.renderLock.unlock();
         }

         var2 = var0.get();
      } finally {
         if (var1) {
            ViewPainter.renderLock.lock();
         }
      }

      return (T)var2;
   }

   public static <T> T runWithRenderLock(Supplier<T> var0) {
      ViewPainter.renderLock.lock();

      Object var1;
      try {
         var1 = var0.get();
      } finally {
         ViewPainter.renderLock.unlock();
      }

      return (T)var1;
   }

   boolean hasNativeSystemVsync() {
      return this.nativeSystemVsync;
   }

   boolean isVsyncEnabled() {
      return PrismSettings.isVsyncEnabled && this.pipeline.isVsyncSupported();
   }

   @Override
   public void checkFxUserThread() {
      super.checkFxUserThread();
      this.renderer.checkRendererIdle();
   }

   protected static Thread getFxUserThread() {
      return Toolkit.getFxUserThread();
   }

   @Override
   public Future addRenderJob(RenderJob var1) {
      if (noRenderJobs) {
         CompletionListener var2 = var1.getCompletionListener();
         if (var1 instanceof PaintRenderJob) {
            ((PaintRenderJob)var1).getScene().setPainting(false);
         }

         if (var2 != null) {
            try {
               var2.done(var1);
            } catch (Throwable var4) {
               var4.printStackTrace();
            }
         }

         return null;
      } else if (singleThreaded) {
         var1.run();
         return null;
      } else {
         return this.renderer.submitRenderJob(var1);
      }
   }

   void postPulse() {
      if (this.toolkitRunning.get() && (this.animationRunning.get() || this.nextPulseRequested.get()) && !this.setPulseRunning()) {
         Application.invokeLater(this.pulseRunnable);
         if (debug) {
            System.err.println("QT.postPulse@(" + System.nanoTime() + "): " + this.pulseString());
         }
      } else if (!this.animationRunning.get() && !this.nextPulseRequested.get() && !this.pulseRunning.get()) {
         this.pauseTimer();
      } else if (debug) {
         System.err.println("QT.postPulse#(" + System.nanoTime() + "): DROP : " + this.pulseString());
      }
   }

   private synchronized void pauseTimer() {
      if (!this.pauseRequested) {
         this.pauseRequested = true;
         this.firstPauseRequestTime = System.currentTimeMillis();
      }

      if (System.currentTimeMillis() - this.firstPauseRequestTime >= 250L) {
         this.pulseTimer.pause();
         if (debug) {
            System.err.println("QT.pauseTimer#(" + System.nanoTime() + "): Pausing Timer : " + this.pulseString());
         }
      } else if (debug) {
         System.err.println("QT.pauseTimer#(" + System.nanoTime() + "): Pause Timer : DROP : " + this.pulseString());
      }
   }

   private synchronized void resumeTimer() {
      this.pauseRequested = false;
      this.pulseTimer.resume();
   }

   private String pulseString() {
      return (this.toolkitRunning.get() ? "T" : "t")
         + (this.animationRunning.get() ? "A" : "a")
         + (this.pulseRunning.get() ? "P" : "p")
         + (this.nextPulseRequested.get() ? "N" : "n");
   }

   private boolean setPulseRunning() {
      return this.pulseRunning.getAndSet(true);
   }

   private void endPulseRunning() {
      this.pulseRunning.set(false);
      if (debug) {
         System.err.println("QT.endPulse: " + System.nanoTime());
      }
   }

   void pulseFromQueue() {
      try {
         this.pulse();
      } finally {
         this.endPulseRunning();
      }
   }

   protected void pulse() {
      this.pulse(true);
   }

   void pulse(boolean var1) {
      try {
         this.inPulse++;
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.pulseStart();
         }

         if (this.toolkitRunning.get()) {
            this.nextPulseRequested.set(false);
            if (this.animationRunnable != null) {
               this.animationRunning.set(true);
               this.animationRunnable.run();
            } else {
               this.animationRunning.set(false);
            }

            this.firePulse();
            if (var1) {
               this.collector.renderAll();
            }

            return;
         }
      } finally {
         this.inPulse--;
         if (PulseLogger.PULSE_LOGGING_ENABLED) {
            PulseLogger.pulseEnd();
         }
      }
   }

   void vsyncHint() {
      if (this.isVsyncEnabled()) {
         if (debug) {
            System.err.println("QT.vsyncHint: postPulse: " + System.nanoTime());
         }

         this.postPulse();
      }
   }

   @Override
   public AppletWindow createAppletWindow(long var1, String var3) {
      GlassAppletWindow var4 = new GlassAppletWindow(var1, var3);
      WindowStage.setAppletWindow(var4);
      return var4;
   }

   @Override
   public void closeAppletWindow() {
      GlassAppletWindow var1 = WindowStage.getAppletWindow();
      if (null != var1) {
         var1.dispose();
         WindowStage.setAppletWindow(null);
      }
   }

   @Override
   public TKStage createTKStage(Window var1, boolean var2, StageStyle var3, boolean var4, Modality var5, TKStage var6, boolean var7, AccessControlContext var8) {
      this.assertToolkitRunning();
      WindowStage var9 = new WindowStage(var1, var2, var3, var5, var6);
      var9.setSecurityContext(var8);
      if (var4) {
         var9.setIsPrimary();
      }

      var9.setRTL(var7);
      var9.init(this.systemMenu);
      return var9;
   }

   @Override
   public boolean canStartNestedEventLoop() {
      return this.inPulse == 0;
   }

   @Override
   public Object enterNestedEventLoop(Object var1) {
      this.checkFxUserThread();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.canStartNestedEventLoop()) {
         throw new IllegalStateException("Cannot enter nested loop during animation or layout processing");
      } else {
         if (this.eventLoopMap == null) {
            this.eventLoopMap = new HashMap<>();
         }

         if (this.eventLoopMap.containsKey(var1)) {
            throw new IllegalArgumentException("Key already associated with a running event loop: " + var1);
         } else {
            EventLoop var2 = Application.GetApplication().createEventLoop();
            this.eventLoopMap.put(var1, var2);
            Object var3 = var2.enter();
            if (!this.isNestedLoopRunning()) {
               this.notifyLastNestedLoopExited();
            }

            return var3;
         }
      }
   }

   @Override
   public void exitNestedEventLoop(Object var1, Object var2) {
      this.checkFxUserThread();
      if (var1 == null) {
         throw new NullPointerException();
      } else if (this.eventLoopMap != null && this.eventLoopMap.containsKey(var1)) {
         EventLoop var3 = this.eventLoopMap.get(var1);
         this.eventLoopMap.remove(var1);
         var3.leave(var2);
      } else {
         throw new IllegalArgumentException("Key not associated with a running event loop: " + var1);
      }
   }

   @Override
   public void exitAllNestedEventLoops() {
      this.checkFxUserThread();

      for (EventLoop var2 : this.eventLoopMap.values()) {
         var2.leave(null);
      }

      this.eventLoopMap.clear();
      this.eventLoopMap = null;
   }

   @Override
   public TKStage createTKPopupStage(Window var1, StageStyle var2, TKStage var3, AccessControlContext var4) {
      this.assertToolkitRunning();
      boolean var5 = var3 instanceof WindowStage ? ((WindowStage)var3).isSecurityDialog() : false;
      WindowStage var6 = new WindowStage(var1, var5, var2, null, var3);
      var6.setSecurityContext(var4);
      var6.setIsPopup();
      var6.init(this.systemMenu);
      return var6;
   }

   @Override
   public TKStage createTKEmbeddedStage(HostInterface var1, AccessControlContext var2) {
      this.assertToolkitRunning();
      EmbeddedStage var3 = new EmbeddedStage(var1);
      var3.setSecurityContext(var2);
      return var3;
   }

   @Override
   public ScreenConfigurationAccessor setScreenConfigurationListener(final TKScreenConfigurationListener var1) {
      Screen.setEventHandler(new Screen.EventHandler() {
         @Override
         public void handleSettingsChanged() {
            QuantumToolkit.notifyScreenListener(var1);
         }
      });
      return screenAccessor;
   }

   private static void assignScreensAdapters() {
      GraphicsPipeline var0 = GraphicsPipeline.getPipeline();

      for (Screen var2 : Screen.getScreens()) {
         var2.setAdapterOrdinal(var0.getAdapterOrdinal(var2));
      }
   }

   private static void notifyScreenListener(TKScreenConfigurationListener var0) {
      assignScreensAdapters();
      var0.screenConfigurationChanged();
   }

   @Override
   public Object getPrimaryScreen() {
      return Screen.getMainScreen();
   }

   @Override
   public List<?> getScreens() {
      return Screen.getScreens();
   }

   @Override
   public ScreenConfigurationAccessor getScreenConfigurationAccessor() {
      return screenAccessor;
   }

   @Override
   public PerformanceTracker getPerformanceTracker() {
      return this.perfTracker;
   }

   @Override
   public PerformanceTracker createPerformanceTracker() {
      return new PerformanceTrackerImpl();
   }

   private float getMaxRenderScale() {
      if (this._maxPixelScale == 0.0F) {
         for (Object var2 : this.getScreens()) {
            this._maxPixelScale = Math.max(this._maxPixelScale, ((Screen)var2).getRecommendedOutputScaleX());
            this._maxPixelScale = Math.max(this._maxPixelScale, ((Screen)var2).getRecommendedOutputScaleY());
         }
      }

      return this._maxPixelScale;
   }

   @Override
   public ImageLoader loadImage(String var1, double var2, double var4, boolean var6, boolean var7) {
      return new PrismImageLoader2(var1, var2, var4, var6, this.getMaxRenderScale(), var7);
   }

   @Override
   public ImageLoader loadImage(InputStream var1, double var2, double var4, boolean var6, boolean var7) {
      return new PrismImageLoader2(var1, var2, var4, var6, var7);
   }

   public AbstractRemoteResource<? extends ImageLoader> loadImageAsync(
      AsyncOperationListener var1, String var2, double var3, double var5, boolean var7, boolean var8
   ) {
      return new PrismImageLoader2.AsyncImageLoader(var1, var2, var3, var5, var7, var8);
   }

   @Override
   public void defer(Runnable var1) {
      if (this.toolkitRunning.get()) {
         Application.invokeLater(var1);
      }
   }

   @Override
   public void exit() {
      this.checkFxUserThread();
      this.pulseTimer.stop();
      PaintCollector.getInstance().waitForRenderingToComplete();
      this.notifyShutdownHooks();
      runWithRenderLock(() -> {
         Application var0 = Application.GetApplication();
         var0.terminate();
         return null;
      });
      this.dispose();
      super.exit();
   }

   public void dispose() {
      if (this.toolkitRunning.compareAndSet(true, false)) {
         this.pulseTimer.stop();
         this.renderer.stopRenderer();

         try {
            AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
               Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
               return null;
            }));
         } catch (IllegalStateException var2) {
         }
      }
   }

   @Override
   public boolean isForwardTraversalKey(KeyEvent var1) {
      return var1.getCode() == KeyCode.TAB && var1.getEventType() == KeyEvent.KEY_PRESSED && !var1.isShiftDown();
   }

   @Override
   public boolean isBackwardTraversalKey(KeyEvent var1) {
      return var1.getCode() == KeyCode.TAB && var1.getEventType() == KeyEvent.KEY_PRESSED && var1.isShiftDown();
   }

   @Override
   public Map<Object, Object> getContextMap() {
      return this.contextMap;
   }

   @Override
   public int getRefreshRate() {
      return pulseHZ == null ? 60 : pulseHZ;
   }

   @Override
   public void setAnimationRunnable(DelayedRunnable var1) {
      if (var1 != null) {
         this.animationRunning.set(true);
      }

      this.animationRunnable = var1;
   }

   @Override
   public void requestNextPulse() {
      this.nextPulseRequested.set(true);
   }

   @Override
   public void waitFor(Toolkit.Task var1) {
      if (!var1.isFinished()) {
         ;
      }
   }

   @Override
   protected Object createColorPaint(Color var1) {
      return new com.sun.prism.paint.Color((float)var1.getRed(), (float)var1.getGreen(), (float)var1.getBlue(), (float)var1.getOpacity());
   }

   private com.sun.prism.paint.Color toPrismColor(Color var1) {
      return (com.sun.prism.paint.Color)Toolkit.getPaintAccessor().getPlatformPaint(var1);
   }

   private List<Stop> convertStops(List<javafx.scene.paint.Stop> var1) {
      ArrayList var2 = new ArrayList(var1.size());

      for (javafx.scene.paint.Stop var4 : var1) {
         var2.add(new Stop(this.toPrismColor(var4.getColor()), (float)var4.getOffset()));
      }

      return var2;
   }

   @Override
   protected Object createLinearGradientPaint(LinearGradient var1) {
      byte var2 = 2;
      CycleMethod var3 = var1.getCycleMethod();
      if (var3 == CycleMethod.NO_CYCLE) {
         var2 = 0;
      } else if (var3 == CycleMethod.REFLECT) {
         var2 = 1;
      }

      List var4 = this.convertStops(var1.getStops());
      return new com.sun.prism.paint.LinearGradient(
         (float)var1.getStartX(), (float)var1.getStartY(), (float)var1.getEndX(), (float)var1.getEndY(), null, var1.isProportional(), var2, var4
      );
   }

   @Override
   protected Object createRadialGradientPaint(RadialGradient var1) {
      float var2 = (float)var1.getCenterX();
      float var3 = (float)var1.getCenterY();
      float var4 = (float)var1.getFocusAngle();
      float var5 = (float)var1.getFocusDistance();
      byte var6 = 0;
      if (var1.getCycleMethod() == CycleMethod.NO_CYCLE) {
         var6 = 0;
      } else if (var1.getCycleMethod() == CycleMethod.REFLECT) {
         var6 = 1;
      } else {
         var6 = 2;
      }

      List var7 = this.convertStops(var1.getStops());
      return new com.sun.prism.paint.RadialGradient(var2, var3, var4, var5, (float)var1.getRadius(), null, var1.isProportional(), var6, var7);
   }

   @Override
   protected Object createImagePatternPaint(ImagePattern var1) {
      return var1.getImage() == null
         ? com.sun.prism.paint.Color.TRANSPARENT
         : new com.sun.prism.paint.ImagePattern(
            (Image)Toolkit.getImageAccessor().getPlatformImage(var1.getImage()),
            (float)var1.getX(),
            (float)var1.getY(),
            (float)var1.getWidth(),
            (float)var1.getHeight(),
            var1.isProportional(),
            Toolkit.getPaintAccessor().isMutable(var1)
         );
   }

   private void initStroke(StrokeType var1, double var2, StrokeLineCap var4, StrokeLineJoin var5, float var6, float[] var7, float var8) {
      byte var9;
      if (var1 == StrokeType.CENTERED) {
         var9 = 0;
      } else if (var1 == StrokeType.INSIDE) {
         var9 = 1;
      } else {
         var9 = 2;
      }

      byte var10;
      if (var4 == StrokeLineCap.BUTT) {
         var10 = 0;
      } else if (var4 == StrokeLineCap.SQUARE) {
         var10 = 2;
      } else {
         var10 = 1;
      }

      byte var11;
      if (var5 == StrokeLineJoin.BEVEL) {
         var11 = 2;
      } else if (var5 == StrokeLineJoin.MITER) {
         var11 = 0;
      } else {
         var11 = 1;
      }

      tmpStroke.set(var9, (float)var2, var10, var11, var6);
      if (var7 != null && var7.length > 0) {
         tmpStroke.set(var7, var8);
      } else {
         tmpStroke.set((float[])null, 0.0F);
      }
   }

   @Override
   public void accumulateStrokeBounds(
      Shape var1, float[] var2, StrokeType var3, double var4, StrokeLineCap var6, StrokeLineJoin var7, float var8, BaseTransform var9
   ) {
      this.initStroke(var3, var4, var6, var7, var8, null, 0.0F);
      if (var9.isTranslateOrIdentity()) {
         tmpStroke.accumulateShapeBounds(var2, var1, var9);
      } else {
         Shape.accumulate(var2, tmpStroke.createStrokedShape(var1), var9);
      }
   }

   @Override
   public boolean strokeContains(Shape var1, double var2, double var4, StrokeType var6, double var7, StrokeLineCap var9, StrokeLineJoin var10, float var11) {
      this.initStroke(var6, var7, var9, var10, var11, null, 0.0F);
      return tmpStroke.createStrokedShape(var1).contains((float)var2, (float)var4);
   }

   @Override
   public Shape createStrokedShape(Shape var1, StrokeType var2, double var3, StrokeLineCap var5, StrokeLineJoin var6, float var7, float[] var8, float var9) {
      this.initStroke(var2, var3, var5, var6, var7, var8, var9);
      return tmpStroke.createStrokedShape(var1);
   }

   @Override
   public Dimension2D getBestCursorSize(int var1, int var2) {
      return CursorUtils.getBestCursorSize(var1, var2);
   }

   @Override
   public int getMaximumCursorColors() {
      return 2;
   }

   @Override
   public int getKeyCodeForChar(String var1) {
      return var1.length() == 1 ? com.sun.glass.events.KeyEvent.getKeyCodeForChar(var1.charAt(0)) : 0;
   }

   @Override
   public PathElement[] convertShapeToFXPath(Object var1) {
      if (var1 == null) {
         return new PathElement[0];
      } else {
         ArrayList var2 = new ArrayList();
         Shape var3 = (Shape)var1;
         PathIterator var4 = var3.getPathIterator(null);
         PathIteratorHelper var5 = new PathIteratorHelper(var4);
         PathIteratorHelper.Struct var6 = new PathIteratorHelper.Struct();

         while (!var5.isDone()) {
            boolean var7 = var5.getWindingRule() == 0;
            int var8 = var5.currentSegment(var6);
            Object var9;
            if (var8 == 0) {
               var9 = new MoveTo((double)var6.f0, (double)var6.f1);
            } else if (var8 == 1) {
               var9 = new LineTo((double)var6.f0, (double)var6.f1);
            } else if (var8 == 2) {
               var9 = new QuadCurveTo((double)var6.f0, (double)var6.f1, (double)var6.f2, (double)var6.f3);
            } else if (var8 == 3) {
               var9 = new CubicCurveTo((double)var6.f0, (double)var6.f1, (double)var6.f2, (double)var6.f3, (double)var6.f4, (double)var6.f5);
            } else {
               if (var8 != 4) {
                  throw new IllegalStateException("Invalid element type: " + var8);
               }

               var9 = new ClosePath();
            }

            var5.next();
            var2.add(var9);
         }

         return var2.toArray(new PathElement[var2.size()]);
      }
   }

   @Override
   public Filterable toFilterable(javafx.scene.image.Image var1) {
      return PrImage.create((Image)Toolkit.getImageAccessor().getPlatformImage(var1));
   }

   @Override
   public FilterContext getFilterContext(Object var1) {
      if (var1 != null && var1 instanceof Screen) {
         Screen var2 = (Screen)var1;
         return PrFilterContext.getInstance(var2);
      } else {
         return PrFilterContext.getDefaultInstance();
      }
   }

   @Override
   public AbstractPrimaryTimer getPrimaryTimer() {
      return PrimaryTimer.getInstance();
   }

   @Override
   public FontLoader getFontLoader() {
      return PrismFontLoader.getInstance();
   }

   @Override
   public TextLayoutFactory getTextLayoutFactory() {
      return PrismTextLayoutFactory.getFactory();
   }

   @Override
   public Object createSVGPathObject(SVGPath var1) {
      int var2 = var1.getFillRule() == FillRule.NON_ZERO ? 1 : 0;
      Path2D var3 = new Path2D(var2);
      var3.appendSVGPath(var1.getContent());
      return var3;
   }

   @Override
   public Path2D createSVGPath2D(SVGPath var1) {
      int var2 = var1.getFillRule() == FillRule.NON_ZERO ? 1 : 0;
      Path2D var3 = new Path2D(var2);
      var3.appendSVGPath(var1.getContent());
      return var3;
   }

   @Override
   public boolean imageContains(Object var1, float var2, float var3) {
      if (var1 == null) {
         return false;
      } else {
         Image var4 = (Image)var1;
         int var5 = (int)var2 + var4.getMinX();
         int var6 = (int)var3 + var4.getMinY();
         if (var4.isOpaque()) {
            return true;
         } else if (var4.getPixelFormat() == PixelFormat.INT_ARGB_PRE) {
            IntBuffer var10 = (IntBuffer)var4.getPixelBuffer();
            int var12 = var5 + var6 * var4.getRowLength();
            return var12 >= var10.limit() ? false : (var10.get(var12) & 0xFF000000) != 0;
         } else if (var4.getPixelFormat() == PixelFormat.BYTE_BGRA_PRE) {
            ByteBuffer var9 = (ByteBuffer)var4.getPixelBuffer();
            int var11 = var5 * var4.getBytesPerPixelUnit() + var6 * var4.getScanlineStride() + 3;
            return var11 >= var9.limit() ? false : (var9.get(var11) & 255) != 0;
         } else if (var4.getPixelFormat() == PixelFormat.BYTE_ALPHA) {
            ByteBuffer var7 = (ByteBuffer)var4.getPixelBuffer();
            int var8 = var5 * var4.getBytesPerPixelUnit() + var6 * var4.getScanlineStride();
            return var8 >= var7.limit() ? false : (var7.get(var8) & 255) != 0;
         } else {
            return true;
         }
      }
   }

   @Override
   public boolean isNestedLoopRunning() {
      return Application.isNestedLoopRunning();
   }

   @Override
   public boolean isSupported(ConditionalFeature var1) {
      switch (var1) {
         case SCENE3D:
            return GraphicsPipeline.getPipeline().is3DSupported();
         case EFFECT:
            return GraphicsPipeline.getPipeline().isEffectSupported();
         case SHAPE_CLIP:
            return true;
         case INPUT_METHOD:
            return Application.GetApplication().supportsInputMethods();
         case TRANSPARENT_WINDOW:
            return Application.GetApplication().supportsTransparentWindows();
         case UNIFIED_WINDOW:
            return Application.GetApplication().supportsUnifiedWindows();
         case TWO_LEVEL_FOCUS:
            return Application.GetApplication().hasTwoLevelFocus();
         case VIRTUAL_KEYBOARD:
            return Application.GetApplication().hasVirtualKeyboard();
         case INPUT_TOUCH:
            return Application.GetApplication().hasTouch();
         case INPUT_MULTITOUCH:
            return Application.GetApplication().hasMultiTouch();
         case INPUT_POINTER:
            return Application.GetApplication().hasPointer();
         default:
            return false;
      }
   }

   @Override
   public boolean isMSAASupported() {
      return GraphicsPipeline.getPipeline().isMSAASupported();
   }

   private int toGlassKeyCode(KeyCode var1) {
      switch (var1) {
         case CAPS:
            return 20;
         case NUM_LOCK:
            return 144;
         default:
            return 0;
      }
   }

   @Override
   public Optional<Boolean> isKeyLocked(KeyCode var1) {
      return Application.GetApplication().isKeyLocked(this.toGlassKeyCode(var1));
   }

   static TransferMode clipboardActionToTransferMode(int var0) {
      switch (var0) {
         case 0:
            return null;
         case 1:
         case 1073741825:
            return TransferMode.COPY;
         case 2:
         case 1073741826:
            return TransferMode.MOVE;
         case 1073741824:
            return TransferMode.LINK;
         case 1342177279:
            return TransferMode.COPY;
         default:
            return null;
      }
   }

   @Override
   public TKClipboard getSystemClipboard() {
      if (this.clipboard == null) {
         this.clipboard = QuantumClipboard.getClipboardInstance(new ClipboardAssistance("SYSTEM"));
      }

      return this.clipboard;
   }

   @Override
   public TKSystemMenu getSystemMenu() {
      return this.systemMenu;
   }

   @Override
   public TKClipboard getNamedClipboard(String var1) {
      return null;
   }

   @Override
   public void startDrag(TKScene var1, Set<TransferMode> var2, TKDragSourceListener var3, Dragboard var4) {
      if (var4 == null) {
         throw new IllegalArgumentException("dragboard should not be null");
      } else {
         GlassScene var5 = (GlassScene)var1;
         var5.setTKDragSourceListener(var3);
         QuantumClipboard var6 = (QuantumClipboard)DragboardHelper.getPeer(var4);
         var6.setSupportedTransferMode(var2);
         var6.flush();
         var6.close();
      }
   }

   @Override
   public void enableDrop(TKScene var1, TKDropTargetListener var2) {
      assert var1 instanceof GlassScene;

      GlassScene var3 = (GlassScene)var1;
      var3.setTKDropTargetListener(var2);
   }

   @Override
   public void registerDragGestureListener(TKScene var1, Set<TransferMode> var2, TKDragGestureListener var3) {
      assert var1 instanceof GlassScene;

      GlassScene var4 = (GlassScene)var1;
      var4.setTKDragGestureListener(var3);
   }

   @Override
   public void installInputMethodRequests(TKScene var1, InputMethodRequests var2) {
      assert var1 instanceof GlassScene;

      GlassScene var3 = (GlassScene)var1;
      var3.setInputMethodRequests(var2);
   }

   @Override
   public ImageLoader loadPlatformImage(Object var1) {
      if (var1 instanceof QuantumToolkit.QuantumImage) {
         return (QuantumToolkit.QuantumImage)var1;
      } else if (var1 instanceof Image) {
         return new QuantumToolkit.QuantumImage((Image)var1);
      } else if (var1 instanceof PixelBuffer) {
         return new QuantumToolkit.QuantumImage((PixelBuffer<Buffer>)var1);
      } else {
         throw new UnsupportedOperationException("unsupported class for loadPlatformImage");
      }
   }

   @Override
   public PlatformImage createPlatformImage(int var1, int var2) {
      ByteBuffer var3 = ByteBuffer.allocate(var1 * var2 * 4);
      return Image.fromByteBgraPreData(var3, var1, var2);
   }

   @Override
   public Object renderToImage(final Toolkit.ImageRenderingContext var1) {
      Object var2 = var1.platformImage;
      final Paint var4 = var1.platformPaint instanceof Paint ? (Paint)var1.platformPaint : null;
      RenderJob var5 = new RenderJob(
         new Runnable() {
            private com.sun.prism.paint.Color getClearColor() {
               if (var4 == null) {
                  return com.sun.prism.paint.Color.WHITE;
               } else if (var4.getType() == Paint.Type.COLOR) {
                  return (com.sun.prism.paint.Color)var4;
               } else {
                  return var4.isOpaque() ? com.sun.prism.paint.Color.TRANSPARENT : com.sun.prism.paint.Color.WHITE;
               }
            }

            private void draw(Graphics var1x, int var2, int var3, int var4x, int var5) {
               var1x.setLights(var1.lights);
               var1x.setDepthBuffer(var1.depthBuffer);
               var1x.clear(this.getClearColor());
               if (var4 != null && var4.getType() != Paint.Type.COLOR) {
                  var1x.getRenderTarget().setOpaque(var4.isOpaque());
                  var1x.setPaint(var4);
                  var1x.fillQuad(0.0F, 0.0F, (float)var4x, (float)var5);
               }

               if (var2 != 0 || var3 != 0) {
                  var1x.translate((float)(-var2), (float)(-var3));
               }

               if (var1.transform != null) {
                  var1x.transform(var1.transform);
               }

               if (var1.root != null) {
                  if (var1.camera != null) {
                     var1x.setCamera(var1.camera);
                  }

                  NGNode var6 = var1.root;
                  var6.render(var1x);
               }
            }

            private void renderTile(
               int var1x,
               int var2,
               int var3,
               int var4x,
               int var5,
               int var6,
               IntBuffer var7,
               ResourceFactory var8,
               QuantumToolkit.QuantumImage var9,
               QuantumToolkit.QuantumImage var10
            ) {
               RTTexture var11 = var9.getRT(var5, var6, var8);
               if (var11 != null) {
                  Graphics var12 = var11.createGraphics();
                  this.draw(var12, var1x + var2, var3 + var4x, var5, var6);
                  int[] var13 = var11.getPixels();
                  if (var13 != null) {
                     var7.put(var13);
                  } else {
                     var11.readPixels(var7, var11.getContentX(), var11.getContentY(), var5, var6);
                  }

                  var10.image.setPixels(var2, var4x, var5, var6, javafx.scene.image.PixelFormat.getIntArgbPreInstance(), var7, var5);
                  var11.unlock();
               }
            }

            private void renderWholeImage(int var1x, int var2, int var3, int var4x, ResourceFactory var5, QuantumToolkit.QuantumImage var6) {
               RTTexture var7 = var6.getRT(var3, var4x, var5);
               if (var7 != null) {
                  Graphics var8 = var7.createGraphics();
                  this.draw(var8, var1x, var2, var3, var4x);
                  int[] var9 = var7.getPixels();
                  if (var9 != null) {
                     var6.setImage(Image.fromIntArgbPreData(var9, var3, var4x));
                  } else {
                     IntBuffer var10 = IntBuffer.allocate(var3 * var4x);
                     if (var7.readPixels(var10, var7.getContentX(), var7.getContentY(), var3, var4x)) {
                        var6.setImage(Image.fromIntArgbPreData(var10, var3, var4x));
                     } else {
                        var6.dispose();
                        Object var11 = null;
                     }
                  }

                  var7.unlock();
               }
            }

            private int computeTileSize(int var1x, int var2) {
               for (int var3 = 1; var3 <= 3; var3++) {
                  int var4x = var1x / var3;
                  if (var4x <= var2 && var4x * var3 == var1x) {
                     return var4x;
                  }
               }

               return var2;
            }

            @Override
            public void run() {
               ResourceFactory var1x = GraphicsPipeline.getDefaultResourceFactory();
               if (var1x.isDeviceReady()) {
                  int var2 = var1.x;
                  int var3 = var1.y;
                  int var4x = var1.width;
                  int var5 = var1.height;
                  if (var4x > 0 && var5 > 0) {
                     boolean var6 = false;
                     QuantumToolkit.QuantumImage var7 = null;

                     try {
                        QuantumToolkit.QuantumImage var8 = var1.platformImage instanceof QuantumToolkit.QuantumImage
                           ? (QuantumToolkit.QuantumImage)var1.platformImage
                           : new QuantumToolkit.QuantumImage((Image)null);
                        int var9 = var1x.getMaximumTextureSize();
                        if (var5 <= var9 && var4x <= var9) {
                           this.renderWholeImage(var2, var3, var4x, var5, var1x, var8);
                        } else {
                           var7 = new QuantumToolkit.QuantumImage((Image)null);
                           if (var8.image == null) {
                              var8.setImage(Image.fromIntArgbPreData(IntBuffer.allocate(var4x * var5), var4x, var5));
                           }

                           int var10 = this.computeTileSize(var4x, var9);
                           int var11 = this.computeTileSize(var5, var9);
                           IntBuffer var12 = IntBuffer.allocate(var10 * var11);
                           int var13 = 0;
                           int var14 = 0;

                           for (var13 = 0; var13 + var10 <= var4x; var13 += var10) {
                              for (var14 = 0; var14 + var11 <= var5; var14 += var11) {
                                 this.renderTile(var2, var13, var3, var14, var10, var11, var12, var1x, var7, var8);
                              }
                           }

                           int var15 = var13;
                           int var16 = var4x - var13;
                           if (var16 > 0) {
                              for (int var17 = 0; var17 + var11 <= var5; var17 += var11) {
                                 this.renderTile(var2, var15, var3, var17, var16, var11, var12, var1x, var7, var8);
                              }
                           }

                           int var26 = var14;
                           int var18 = var5 - var14;
                           if (var18 > 0) {
                              for (int var19 = 0; var19 + var10 <= var4x; var19 += var10) {
                                 this.renderTile(var2, var19, var3, var26, var10, var18, var12, var1x, var7, var8);
                              }
                           }

                           if (var16 > 0 && var18 > 0) {
                              this.renderTile(var2, var15, var3, var26, var16, var18, var12, var1x, var7, var8);
                           }
                        }

                        var1.platformImage = var8;
                     } catch (Throwable var23) {
                        var6 = true;
                        var23.printStackTrace(System.err);
                     } finally {
                        if (var7 != null) {
                           var7.dispose();
                        }

                        Disposer.cleanUp();
                        var1x.getTextureResourcePool().freeDisposalRequestedAndCheckResources(var6);
                     }
                  }
               }
            }
         }
      );
      CountDownLatch var6 = new CountDownLatch(1);
      var5.setCompletionListener(var1x -> var6.countDown());
      this.addRenderJob(var5);

      while (true) {
         try {
            var6.await();
            break;
         } catch (InterruptedException var8) {
            var8.printStackTrace();
         }
      }

      Object var7 = var1.platformImage;
      var1.platformImage = var2;
      return var7;
   }

   @Override
   public CommonDialogs.FileChooserResult showFileChooser(
      TKStage var1, String var2, File var3, String var4, FileChooserType var5, List<FileChooser.ExtensionFilter> var6, FileChooser.ExtensionFilter var7
   ) {
      WindowStage var8 = null;

      CommonDialogs.FileChooserResult var9;
      try {
         var8 = this.blockOwnerStage(var1);
         var9 = CommonDialogs.showFileChooser(
            var1 instanceof WindowStage ? ((WindowStage)var1).getPlatformWindow() : null,
            var3,
            var4,
            var2,
            var5 == FileChooserType.SAVE ? 1 : 0,
            var5 == FileChooserType.OPEN_MULTIPLE,
            convertExtensionFilters(var6),
            var6.indexOf(var7)
         );
      } finally {
         if (var8 != null) {
            var8.setEnabled(true);
         }
      }

      return var9;
   }

   @Override
   public File showDirectoryChooser(TKStage var1, String var2, File var3) {
      WindowStage var4 = null;

      File var5;
      try {
         var4 = this.blockOwnerStage(var1);
         var5 = CommonDialogs.showFolderChooser(var1 instanceof WindowStage ? ((WindowStage)var1).getPlatformWindow() : null, var3, var2);
      } finally {
         if (var4 != null) {
            var4.setEnabled(true);
         }
      }

      return var5;
   }

   private WindowStage blockOwnerStage(TKStage var1) {
      if (var1 instanceof WindowStage) {
         GlassStage var2 = ((WindowStage)var1).getOwner();
         if (var2 instanceof WindowStage) {
            WindowStage var3 = (WindowStage)var2;
            var3.setEnabled(false);
            return var3;
         }
      }

      return null;
   }

   private static List<CommonDialogs.ExtensionFilter> convertExtensionFilters(List<FileChooser.ExtensionFilter> var0) {
      CommonDialogs.ExtensionFilter[] var1 = new CommonDialogs.ExtensionFilter[var0.size()];
      int var2 = 0;

      for (FileChooser.ExtensionFilter var4 : var0) {
         var1[var2++] = new CommonDialogs.ExtensionFilter(var4.getDescription(), var4.getExtensions());
      }

      return Arrays.asList(var1);
   }

   @Override
   public long getMultiClickTime() {
      return View.getMultiClickTime();
   }

   @Override
   public int getMultiClickMaxX() {
      return View.getMultiClickMaxX();
   }

   @Override
   public int getMultiClickMaxY() {
      return View.getMultiClickMaxY();
   }

   @Override
   public String getThemeName() {
      return Application.GetApplication().getHighContrastTheme();
   }

   @Override
   public GlassRobot createRobot() {
      return Application.GetApplication().createRobot();
   }

   private class PulseTask {
      private volatile boolean isRunning;

      PulseTask(boolean var2) {
         this.isRunning = var2;
      }

      synchronized void set(boolean var1) {
         this.isRunning = var1;
         if (this.isRunning) {
            QuantumToolkit.this.resumeTimer();
         }
      }

      boolean get() {
         return this.isRunning;
      }
   }

   static class QuantumImage implements ImageLoader, ResourceFactoryListener {
      private RTTexture rt;
      private Image image;
      private ResourceFactory rf;

      QuantumImage(Image var1) {
         this.image = var1;
      }

      QuantumImage(PixelBuffer<Buffer> var1) {
         switch (var1.getPixelFormat().getType()) {
            case INT_ARGB_PRE:
               this.image = Image.fromPixelBufferPreData(PixelFormat.INT_ARGB_PRE, var1.getBuffer(), var1.getWidth(), var1.getHeight());
               break;
            case BYTE_BGRA_PRE:
               this.image = Image.fromPixelBufferPreData(PixelFormat.BYTE_BGRA_PRE, var1.getBuffer(), var1.getWidth(), var1.getHeight());
               break;
            default:
               throw new InternalError("Unsupported PixelFormat: " + var1.getPixelFormat().getType());
         }
      }

      RTTexture getRT(int var1, int var2, ResourceFactory var3) {
         boolean var4 = this.rt != null && this.rf == var3 && this.rt.getContentWidth() == var1 && this.rt.getContentHeight() == var2;
         if (var4) {
            this.rt.lock();
            if (this.rt.isSurfaceLost()) {
               var4 = false;
            }
         }

         if (!var4) {
            if (this.rt != null) {
               this.rt.dispose();
            }

            if (this.rf != null) {
               this.rf.removeFactoryListener(this);
               this.rf = null;
            }

            this.rt = var3.createRTTexture(var1, var2, Texture.WrapMode.CLAMP_TO_ZERO);
            if (this.rt != null) {
               this.rf = var3;
               this.rf.addFactoryListener(this);
            }
         }

         return this.rt;
      }

      void dispose() {
         if (this.rt != null) {
            this.rt.dispose();
            this.rt = null;
         }
      }

      void setImage(Image var1) {
         this.image = var1;
      }

      @Override
      public Exception getException() {
         return this.image == null ? new IllegalStateException("Unitialized image") : null;
      }

      @Override
      public int getFrameCount() {
         return 1;
      }

      @Override
      public PlatformImage getFrame(int var1) {
         return this.image;
      }

      @Override
      public int getFrameDelay(int var1) {
         return 0;
      }

      @Override
      public int getLoopCount() {
         return 0;
      }

      @Override
      public double getWidth() {
         return (double)this.image.getWidth();
      }

      @Override
      public double getHeight() {
         return (double)this.image.getHeight();
      }

      @Override
      public void factoryReset() {
         this.dispose();
      }

      @Override
      public void factoryReleased() {
         this.dispose();
         if (this.rf != null) {
            this.rf.removeFactoryListener(this);
            this.rf = null;
         }
      }
   }
}
