package com.cropster.roastingintelligence.ui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import org.apache.commons.lang3.Validate;

public final class AnimatedBorders {
   public static final String ANIMATED_BORDER_TIMELINE_PROPERTY = "animated-border-timeline";
   public static final String CACHE_PROPERTY = "cache";
   public static final String CACHE_HINT_PROPERTY = "cache-hint";
   public static final String ORIGINAL_BORDER_PROPERTY = "original-border";
   public static final String STYLE_CLASS_PROPERTY = "style-class";
   public static final CornerRadii DEFAULT_CORNER_RADII = new CornerRadii(8.0);
   public static final BorderWidths DEFAULT_BORDER_WIDTHS = new BorderWidths(1.75);
   public static final Color WAIT_SEGMENT_COLOR = Color.valueOf("#00a44d");
   private static final double WAIT_RING_WIDTH = 2.25;
   private static final long ITERATION_LIMIT = 150L;
   private static final double ITERATION_STEP = 30.0;

   public static Label createWaitRing(double size) {
      return createWaitRing(size, 0.25);
   }

   public static Label createWaitRing(Label label, double size) {
      return createWaitRing(label, size, 0.25);
   }

   public static Label createWaitRing(double size, double percent) {
      return createWaitRing(new Label(), size, percent);
   }

   public static Label createWaitRing(Label label, double size, double percent) {
      Objects.requireNonNull(label, "Null 'label' parameter");
      Validate.isTrue(size > 0.0, "Non-positive size", new Object[0]);
      label.setMinSize(size, size);
      label.setPrefSize(size, size);
      label.setMaxSize(size, size);
      label.setAlignment(Pos.CENTER);
      label.setStyle("-fx-background-color: rgba(0, 164, 77, 0.2), -fx-color-neutral-10; -fx-background-radius: 50%, 50%; -fx-background-insets: -2.25, 0;");
      return setWaitRingBorder(label, percent);
   }

   public static <T extends Region> T setWaitRingBorder(T region, double percent) {
      Objects.requireNonNull(region, "Null 'region' parameter");
      Validate.isTrue(region.getMinWidth() == region.getMinHeight(), "Region is not square", new Object[0]);
      Validate.isTrue(region.getMinWidth() == region.getMaxWidth(), "Region is resizable", new Object[0]);
      Validate.isTrue(region.getMinHeight() == region.getMaxHeight(), "Region is resizable", new Object[0]);
      double p = Math.min(Math.max(percent, 0.0), 1.0);
      if (region.getProperties().get("cache") == null) {
         region.getProperties().put("cache", region.isCache());
         region.getProperties().put("cache-hint", region.getCacheHint());
         region.getProperties().put("style-class", new ArrayList(region.getStyleClass()));
      }

      region.setCache(true);
      region.setCacheHint(CacheHint.SPEED);
      double size = 4.5 + region.getMinWidth();
      double length = (size - 2.25) * Math.PI;
      double step = 1000.0 / length;
      double[] mills = new double[]{-step};
      CornerRadii cr = new CornerRadii(0.5 + size / 2.0);
      BorderWidths bw = new BorderWidths(2.25);
      KeyFrame[] keyFrames = Stream.<Double>iterate(0.0, i -> i + 1.0)
         .limit((long)(10.0 * length))
         .map(i -> new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.ROUND, StrokeLineCap.SQUARE, 10.0, i, List.of(length * p, length * (1.0 - p))))
         .map(bss -> new BorderStroke(WAIT_SEGMENT_COLOR, bss, cr, bw))
         .map(xva$0 -> new Border(new BorderStroke[]{xva$0}))
         .map(b -> {
            mills[0] += step;
            return new KeyFrame(Duration.millis(mills[0]), new KeyValue[]{new KeyValue(region.borderProperty(), b, Interpolator.EASE_IN)});
         })
         .toArray(KeyFrame[]::new);
      if (region.getProperties().get("animated-border-timeline") instanceof Timeline timeline) {
         Duration currentTime = timeline.getCurrentTime();
         timeline.stop();
         timeline.getKeyFrames().setAll(keyFrames);
         timeline.playFrom(currentTime);
      } else {
         region.getProperties().put("original-border", region.getBorder());
         Timeline timeline = new Timeline(keyFrames);
         timeline.setCycleCount(-1);
         timeline.play();
         region.getProperties().put("animated-border-timeline", timeline);
      }

      return region;
   }

   public static <T extends Region> T setAnimatedBorder(T region) {
      return setAnimatedBorder(region, WAIT_SEGMENT_COLOR);
   }

   public static <T extends Region> T setAnimatedBorder(T region, Color color) {
      return setAnimatedBorder(region, color, DEFAULT_CORNER_RADII, DEFAULT_BORDER_WIDTHS);
   }

   public static <T extends Region> T setAnimatedBorder(T region, Color color, CornerRadii cornerRadii, BorderWidths borderWidths) {
      Objects.requireNonNull(region, "Null 'region' parameter");
      Objects.requireNonNull(region, "Null 'color' parameter");
      Objects.requireNonNull(region, "Null 'cornerRadii' parameter");
      Objects.requireNonNull(region, "Null 'borderWidths' parameter");
      if (region.getProperties().get("cache") == null) {
         region.getProperties().put("cache", region.isCache());
         region.getProperties().put("cache-hint", region.getCacheHint());
         region.getProperties().put("style-class", new ArrayList(region.getStyleClass()));
      }

      region.setCache(true);
      region.setCacheHint(CacheHint.SPEED);
      region.getProperties().put("original-border", region.getBorder());
      double[] mills = new double[]{-30.0};
      KeyFrame[] keyFrames = Stream.<Double>iterate(0.0, i -> i + 1.0)
         .limit(150L)
         .map(i -> new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.ROUND, StrokeLineCap.ROUND, 10.0, i, List.of(10.0, 5.0)))
         .map(bss -> new BorderStroke(color, bss, cornerRadii, borderWidths))
         .map(xva$0 -> new Border(new BorderStroke[]{xva$0}))
         .map(b -> {
            mills[0] += 30.0;
            return new KeyFrame(Duration.millis(mills[0]), new KeyValue[]{new KeyValue(region.borderProperty(), b, Interpolator.EASE_IN)});
         })
         .toArray(KeyFrame[]::new);
      Timeline timeline = new Timeline(keyFrames);
      timeline.setCycleCount(-1);
      timeline.play();
      region.getProperties().put("animated-border-timeline", timeline);
      return region;
   }

   public static <T extends Region> T setAnimatedBorder(T region, Color color1, Color color2) {
      return setAnimatedBorder(region, color1, color2, DEFAULT_CORNER_RADII, DEFAULT_BORDER_WIDTHS);
   }

   public static <T extends Region> T setAnimatedBorder(T region, Color color1, Color color2, CornerRadii cornerRadii, BorderWidths borderWidths) {
      Objects.requireNonNull(region, "Null 'region' parameter");
      Objects.requireNonNull(region, "Null 'color1' parameter");
      Objects.requireNonNull(region, "Null 'color2' parameter");
      Objects.requireNonNull(region, "Null 'cornerRadii' parameter");
      Objects.requireNonNull(region, "Null 'borderWidths' parameter");
      if (region.getProperties().get("cache") == null) {
         region.getProperties().put("cache", region.isCache());
         region.getProperties().put("cache-hint", region.getCacheHint());
         region.getProperties().put("style-class", new ArrayList(region.getStyleClass()));
      }

      region.setCache(true);
      region.setCacheHint(CacheHint.SPEED);
      region.getProperties().put("original-border", region.getBorder());
      double[] mills = new double[]{-30.0};
      KeyFrame[] keyFrames = Stream.<Double>iterate(0.0, i -> i + 1.0)
         .limit(150L)
         .map(i -> new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.ROUND, StrokeLineCap.ROUND, 10.0, i, List.of(10.0, 5.0)))
         .map(bss -> new BorderStroke(color1.interpolate(color2, getColorInterpolationFraction(bss.getDashOffset())), bss, cornerRadii, borderWidths))
         .map(xva$0 -> new Border(new BorderStroke[]{xva$0}))
         .map(b -> {
            mills[0] += 30.0;
            return new KeyFrame(Duration.millis(mills[0]), new KeyValue[]{new KeyValue(region.borderProperty(), b, Interpolator.EASE_IN)});
         })
         .toArray(KeyFrame[]::new);
      Timeline timeline = new Timeline(keyFrames);
      timeline.setCycleCount(-1);
      timeline.play();
      region.getProperties().put("animated-border-timeline", timeline);
      return region;
   }

   public static <T extends Region> T setAnimatedBorder(T region, BorderStrokeStyle borderStrokeStyle, Color color1, Color color2) {
      return setAnimatedBorder(region, borderStrokeStyle, color1, color2, DEFAULT_CORNER_RADII, DEFAULT_BORDER_WIDTHS);
   }

   public static <T extends Region> T setAnimatedBorder(
      T region, BorderStrokeStyle borderStrokeStyle, Color color1, Color color2, CornerRadii cornerRadii, BorderWidths borderWidths
   ) {
      Objects.requireNonNull(region, "Null 'region' parameter");
      Objects.requireNonNull(region, "Null 'borderStrokeStyle' parameter");
      Objects.requireNonNull(region, "Null 'color1' parameter");
      Objects.requireNonNull(region, "Null 'color2' parameter");
      Objects.requireNonNull(region, "Null 'cornerRadii' parameter");
      Objects.requireNonNull(region, "Null 'borderWidths' parameter");
      if (region.getProperties().get("cache") == null) {
         region.getProperties().put("cache", region.isCache());
         region.getProperties().put("cache-hint", region.getCacheHint());
         region.getProperties().put("style-class", new ArrayList(region.getStyleClass()));
      }

      region.setCache(true);
      region.setCacheHint(CacheHint.SPEED);
      region.getProperties().put("original-border", region.getBorder());
      double[] mills = new double[]{-30.0};
      KeyFrame[] keyFrames = Stream.<Double>iterate(0.0, i -> i + 1.0)
         .limit(150L)
         .map(i -> new BorderStroke(color1.interpolate(color2, getColorInterpolationFraction(i)), borderStrokeStyle, cornerRadii, borderWidths))
         .map(xva$0 -> new Border(new BorderStroke[]{xva$0}))
         .map(b -> {
            mills[0] += 30.0;
            return new KeyFrame(Duration.millis(mills[0]), new KeyValue[]{new KeyValue(region.borderProperty(), b, Interpolator.EASE_IN)});
         })
         .toArray(KeyFrame[]::new);
      Timeline timeline = new Timeline(keyFrames);
      timeline.setCycleCount(-1);
      timeline.play();
      region.getProperties().put("animated-border-timeline", timeline);
      return region;
   }

   public static <T extends Region> T stopAnRestoreOriginalBorder(T region) {
      Objects.requireNonNull(region, "Null 'region' parameter");
      Timeline timeline = (Timeline)region.getProperties().remove("animated-border-timeline");
      if (timeline != null) {
         timeline.stop();
      }

      Boolean cache = (Boolean)region.getProperties().remove("cache");
      CacheHint cacheHint = (CacheHint)region.getProperties().remove("cache-hint");
      Border originalBorder = (Border)region.getProperties().remove("original-border");
      List<String> styleClass = (List<String>)region.getProperties().remove("style-class");
      PlatformScheduler.schedule(() -> {
         region.setBorder(originalBorder);
         region.getStyleClass().setAll(styleClass != null ? styleClass : List.of());
         region.setCacheHint(cacheHint != null ? cacheHint : CacheHint.DEFAULT);
         region.setCache(cache != null ? cache : false);
      }, 30, TimeUnit.MILLISECONDS);
      return region;
   }

   public static boolean isAnimatedBorderSet(Region region) {
      return region.getProperties().containsKey("animated-border-timeline");
   }

   private static double getColorInterpolationFraction(double dashOffset) {
      return (dashOffset < 75.0 ? dashOffset : 150.0 - dashOffset) / 75.0;
   }

   private AnimatedBorders() {
   }
}
