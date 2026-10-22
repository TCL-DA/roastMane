package org.girod.javafx.svgimage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.girod.javafx.svgimage.xml.parsers.SVGLibraryException;

public class SVGImage extends Group {
   private static SnapshotParameters SNAPSHOT_PARAMS = null;
   private final Map<String, Node> nodes = new HashMap<>();
   private List<Animation> animations = new ArrayList<>();
   private final SVGContent content;
   private double currentScale = 1.0;

   public SVGImage() {
      this.content = null;
   }

   public SVGImage(SVGContent content) {
      this.content = content;
   }

   public SVGContent getSVGContent() {
      return this.content;
   }

   public static void setDefaultSnapshotParameters(SnapshotParameters params) {
      SNAPSHOT_PARAMS = params;
   }

   public static SnapshotParameters getDefaultSnapshotParameters() {
      return SNAPSHOT_PARAMS;
   }

   void putNode(String id, Node node) {
      this.nodes.put(id, node);
   }

   public boolean hasNode(String id) {
      return this.nodes.containsKey(id);
   }

   public Node getNode(String id) {
      return this.nodes.get(id);
   }

   void setAnimations(List<Animation> animations) {
      this.animations = animations;
   }

   public void playAnimations() {
      if (Platform.isFxApplicationThread()) {
         this.playAnimationsImpl();
      } else {
         new JFXPanel();
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               SVGImage.this.playAnimationsImpl();
            }
         });
      }
   }

   private void playAnimationsImpl() {
      if (!this.animations.isEmpty()) {
         for (Animation tr : this.animations) {
            tr.play();
         }
      }
   }

   public void stopAnimations() {
      if (Platform.isFxApplicationThread()) {
         this.stopAnimationsImpl();
      } else {
         new JFXPanel();
         Platform.runLater(new Runnable() {
            @Override
            public void run() {
               SVGImage.this.stopAnimationsImpl();
            }
         });
      }
   }

   private void stopAnimationsImpl() {
      if (!this.animations.isEmpty()) {
         for (Animation tr : this.animations) {
            tr.stop();
         }
      }
   }

   public double getWidth() {
      return this.getLayoutBounds().getWidth();
   }

   public double getScaledWidth() {
      return this.getBoundsInParent().getWidth();
   }

   public double getHeight() {
      return this.getLayoutBounds().getHeight();
   }

   public double getScaledHeight() {
      return this.getBoundsInParent().getHeight();
   }

   public Image toImageScaled(double scale) {
      return this.toImageScaled(scale, scale);
   }

   public Image toImageScaled(short quality, double scaleX, double scaleY) {
      if (quality == 1 && scaleX == scaleY) {
         SVGImage image = this.scale(scaleX);
         return image.toImage();
      } else {
         double initialWidth = this.getLayoutBounds().getWidth();
         double initialHeight = this.getLayoutBounds().getHeight();
         this.setScaleX(scaleX);
         this.setScaleY(scaleY);
         double finalWidth = initialWidth * scaleX;
         double finalHeight = initialHeight * scaleY;
         SnapshotParameters params = SNAPSHOT_PARAMS;
         Rectangle2D viewport = new Rectangle2D(0.0, 0.0, finalWidth, finalHeight);
         if (params == null) {
            params = new SnapshotParameters();
            params.setViewport(viewport);
         } else {
            params = new SnapshotParameters();
            params.setCamera(SNAPSHOT_PARAMS.getCamera());
            params.setDepthBuffer(SNAPSHOT_PARAMS.isDepthBuffer());
            params.setTransform(SNAPSHOT_PARAMS.getTransform());
            params.setFill(SNAPSHOT_PARAMS.getFill());
            params.setViewport(viewport);
         }

         return this.snapshotImpl(params);
      }
   }

   public Image toImageScaled(double scaleX, double scaleY) {
      return this.toImageScaled((short)0, scaleX, scaleY);
   }

   public Image toImage(short quality, double width) {
      if (quality == 1) {
         SVGImage image = this.scaleTo(width);
         return image.toImage();
      } else {
         double initialWidth = this.getLayoutBounds().getWidth();
         double initialHeight = this.getLayoutBounds().getHeight();
         double scaleX = width / initialWidth;
         this.setScaleX(scaleX);
         this.setScaleY(scaleX);
         double finalHeight = initialHeight * scaleX;
         SnapshotParameters params = SNAPSHOT_PARAMS;
         Rectangle2D viewport = new Rectangle2D(0.0, 0.0, width, finalHeight);
         if (params == null) {
            params = new SnapshotParameters();
            params.setViewport(viewport);
         } else {
            params = new SnapshotParameters();
            params.setCamera(SNAPSHOT_PARAMS.getCamera());
            params.setDepthBuffer(SNAPSHOT_PARAMS.isDepthBuffer());
            params.setTransform(SNAPSHOT_PARAMS.getTransform());
            params.setFill(SNAPSHOT_PARAMS.getFill());
            params.setViewport(viewport);
         }

         return this.snapshotImpl(params);
      }
   }

   public Image toImage(double width) {
      return this.toImage((short)0, width);
   }

   public Image toImage() {
      SnapshotParameters params = SNAPSHOT_PARAMS;
      if (params == null) {
         params = new SnapshotParameters();
      }

      return this.snapshotImpl(params);
   }

   public Image toImage(SnapshotParameters params) {
      return this.snapshotImpl(params);
   }

   private WritableImage snapshotImplInJFX(SnapshotParameters params) {
      return this.snapshot(params, null);
   }

   public SVGImage scale(double scale) {
      return this.scale(scale, false);
   }

   public SVGImage scale(double scale, boolean createNew) {
      if (this.content == null) {
         this.setScaleX(scale);
         this.setScaleY(scale);
         this.currentScale *= scale;
         return this;
      } else {
         SVGImage image;
         if (this.content.params != null) {
            LoaderParameters params = this.content.params.clone();
            params.width = -1.0;
            this.currentScale *= scale;
            params.scale = this.currentScale;
            if (this.content.isFromURL()) {
               image = SVGLoader.load(this.content.url, params);
            } else {
               image = SVGLoader.load(this.content.content, params);
            }
         } else {
            this.currentScale *= scale;
            if (this.content.isFromURL()) {
               image = SVGLoader.loadScaled(this.content.url, this.currentScale);
            } else {
               image = SVGLoader.loadScaled(this.content.content, this.currentScale);
            }
         }

         if (!createNew) {
            this.nodes.clear();
            this.nodes.putAll(image.nodes);
            this.animations.clear();
            this.animations.addAll(image.animations);
         }

         return image;
      }
   }

   public SVGImage scaleTo(double width) {
      return this.scaleTo(width, true);
   }

   public SVGImage scaleTo(double width, boolean createNew) {
      double initialWidth = this.getLayoutBounds().getWidth();
      double scale = width / initialWidth;
      return this.scale(scale, createNew);
   }

   public boolean snapshot(SnapshotParameters params, String format, File file) throws SVGLibraryException {
      GlobalConfig config = GlobalConfig.getInstance();
      if (config.isSwingAvailable()) {
         try {
            WritableImage image = this.snapshotImpl(params);
            return AwtImageConverter.snapshot(image, params, format, file);
         } catch (SVGLibraryException var6) {
            config.handleLibraryException(var6);
            return false;
         }
      } else {
         config.handleLibraryError("Swing not available");
         return false;
      }
   }

   public boolean snapshot(String format, File file) throws SVGLibraryException {
      SnapshotParameters params = SNAPSHOT_PARAMS;
      if (params == null) {
         params = new SnapshotParameters();
         params.setFill(Color.WHITE);
      }

      return this.snapshot(params, format, file);
   }

   private WritableImage snapshotImpl(final SnapshotParameters params) {
      if (Platform.isFxApplicationThread()) {
         return this.snapshotImplInJFX(params);
      } else {
         new JFXPanel();
         FutureTask<WritableImage> future = new FutureTask<>(new Callable<WritableImage>() {
            public WritableImage call() throws Exception {
               return SVGImage.this.snapshotImplInJFX(params);
            }
         });
         Platform.runLater(future);

         try {
            return future.get();
         } catch (Exception var4) {
            return null;
         }
      }
   }
}
