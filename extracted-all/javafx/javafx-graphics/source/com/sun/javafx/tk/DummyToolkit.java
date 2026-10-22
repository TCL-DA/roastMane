package com.sun.javafx.tk;

import com.sun.glass.ui.CommonDialogs;
import com.sun.glass.ui.GlassRobot;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.perf.PerformanceTracker;
import com.sun.javafx.runtime.async.AsyncOperation;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.scene.text.TextLayoutFactory;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;
import java.io.File;
import java.io.InputStream;
import java.security.AccessControlContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Future;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public final class DummyToolkit extends Toolkit {
   @Override
   public boolean init() {
      return true;
   }

   @Override
   public boolean canStartNestedEventLoop() {
      return false;
   }

   @Override
   public Object enterNestedEventLoop(Object var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void exitNestedEventLoop(Object var1, Object var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void exitAllNestedEventLoops() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public TKStage createTKStage(Window var1, boolean var2, StageStyle var3, boolean var4, Modality var5, TKStage var6, boolean var7, AccessControlContext var8) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public TKStage createTKPopupStage(Window var1, StageStyle var2, TKStage var3, AccessControlContext var4) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public TKStage createTKEmbeddedStage(HostInterface var1, AccessControlContext var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public AppletWindow createAppletWindow(long var1, String var3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void closeAppletWindow() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public TKSystemMenu getSystemMenu() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public ImageLoader loadImage(String var1, double var2, double var4, boolean var6, boolean var7) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public ImageLoader loadImage(InputStream var1, double var2, double var4, boolean var6, boolean var7) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public AsyncOperation loadImageAsync(AsyncOperationListener<? extends ImageLoader> var1, String var2, double var3, double var5, boolean var7, boolean var8) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public ImageLoader loadPlatformImage(Object var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public PlatformImage createPlatformImage(int var1, int var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void startup(Runnable var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void defer(Runnable var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Future addRenderJob(RenderJob var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Map<Object, Object> getContextMap() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public int getRefreshRate() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setAnimationRunnable(DelayedRunnable var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public PerformanceTracker getPerformanceTracker() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public PerformanceTracker createPerformanceTracker() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void waitFor(Toolkit.Task var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   protected Object createColorPaint(Color var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   protected Object createLinearGradientPaint(LinearGradient var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   protected Object createRadialGradientPaint(RadialGradient var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   protected Object createImagePatternPaint(ImagePattern var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void accumulateStrokeBounds(
      Shape var1, float[] var2, StrokeType var3, double var4, StrokeLineCap var6, StrokeLineJoin var7, float var8, BaseTransform var9
   ) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean strokeContains(Shape var1, double var2, double var4, StrokeType var6, double var7, StrokeLineCap var9, StrokeLineJoin var10, float var11) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Shape createStrokedShape(Shape var1, StrokeType var2, double var3, StrokeLineCap var5, StrokeLineJoin var6, float var7, float[] var8, float var9) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public int getKeyCodeForChar(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Dimension2D getBestCursorSize(int var1, int var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public int getMaximumCursorColors() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public PathElement[] convertShapeToFXPath(Object var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Filterable toFilterable(Image var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public FilterContext getFilterContext(Object var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean isForwardTraversalKey(KeyEvent var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean isBackwardTraversalKey(KeyEvent var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean isNestedLoopRunning() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public AbstractPrimaryTimer getPrimaryTimer() {
      return null;
   }

   @Override
   public FontLoader getFontLoader() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public TextLayoutFactory getTextLayoutFactory() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Object createSVGPathObject(SVGPath var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Path2D createSVGPath2D(SVGPath var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public boolean imageContains(Object var1, float var2, float var3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public TKClipboard getSystemClipboard() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public TKClipboard getNamedClipboard(String var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public ScreenConfigurationAccessor setScreenConfigurationListener(TKScreenConfigurationListener var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Object getPrimaryScreen() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public List<?> getScreens() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public ScreenConfigurationAccessor getScreenConfigurationAccessor() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void registerDragGestureListener(TKScene var1, Set<TransferMode> var2, TKDragGestureListener var3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void startDrag(TKScene var1, Set<TransferMode> var2, TKDragSourceListener var3, Dragboard var4) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void enableDrop(TKScene var1, TKDropTargetListener var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void installInputMethodRequests(TKScene var1, InputMethodRequests var2) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Object renderToImage(Toolkit.ImageRenderingContext var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public KeyCode getPlatformShortcutKey() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public Optional<Boolean> isKeyLocked(KeyCode var1) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public CommonDialogs.FileChooserResult showFileChooser(
      TKStage var1, String var2, File var3, String var4, FileChooserType var5, List<FileChooser.ExtensionFilter> var6, FileChooser.ExtensionFilter var7
   ) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public File showDirectoryChooser(TKStage var1, String var2, File var3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public long getMultiClickTime() {
      return 0L;
   }

   @Override
   public int getMultiClickMaxX() {
      return 0;
   }

   @Override
   public int getMultiClickMaxY() {
      return 0;
   }

   @Override
   public void requestNextPulse() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public GlassRobot createRobot() {
      throw new UnsupportedOperationException("not implemented");
   }
}
