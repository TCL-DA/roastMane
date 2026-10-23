package com.cropster.roastingintelligence.ui.util;

import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.SystemUtils;

public class VeilSupport {
   public static final String HOLE_PADDING_PROPERTY = "hole-padding";
   public static final String VEILED_ROOT_ID = "veiledRoot";
   private Window window = null;
   private final ChangeListener<Number> resizeListener = (s, o, n) -> Platform.runLater(this::updateVeil);
   private final StackPane veiledRoot;
   private final boolean spanAcrossWindows;
   private final String veilStyle;
   private boolean makeNodeVisible;
   private Parent originalRoot;
   private Node sceneChildNode;
   private boolean veiled = true;
   private Scene veiledScene;
   private Screen veiledScreen;
   private final ChangeListener<Number> movedListener = (s, o, n) -> {
      if (this.window != null && WindowUtil.screenForTopLeft(this.veiledScene) != this.veiledScreen) {
         this.window.hide();
         this.updateVeil();
      }
   };
   private EventHandler<WindowEvent> unveilHandler = null;

   public static void disableApplicationMenu(Scene applicationScene) {
      Parent root = applicationScene.getRoot();
      if (root instanceof AnchorPane) {
         Node realRoot = (Node)root.getChildrenUnmodifiable().get(0);
         root = realRoot instanceof BorderPane borderPane ? borderPane : null;
      }

      if (root instanceof BorderPane borderPane && borderPane.getTop() instanceof MenuBar menuBar) {
         if (SystemUtils.IS_OS_MAC_OSX) {
            menuBar.getMenus().forEach(m -> m.setDisable(true));
         } else {
            menuBar.setDisable(true);
         }
      }
   }

   public static void enableApplicationMenu(Scene applicationScene) {
      Parent root = applicationScene.getRoot();
      if (root instanceof AnchorPane) {
         Node realRoot = (Node)root.getChildrenUnmodifiable().get(0);
         root = realRoot instanceof BorderPane borderPane ? borderPane : null;
      }

      if (root instanceof BorderPane borderPane && borderPane.getTop() instanceof MenuBar menuBar) {
         if (SystemUtils.IS_OS_MAC_OSX) {
            menuBar.getMenus().forEach(m -> m.setDisable(false));
         } else {
            menuBar.setDisable(false);
         }
      }
   }

   public static int getHolePadding(Node node) {
      Integer padding = (Integer)Objects.requireNonNull(node, "Null 'node' parameter").getProperties().get("hole-padding");
      return padding != null ? padding : 0;
   }

   public static void setHolePadding(Node node, int padding) {
      Objects.requireNonNull(node, "Null 'node' parameter").getProperties().put("hole-padding", padding != 0 ? padding : null);
   }

   private static Rectangle toRectangle(Bounds bounds) {
      return toRectangle(bounds, 0);
   }

   private static Rectangle toRectangle(Bounds bounds, int padding) {
      return new Rectangle(
         bounds.getMinX() - (double)padding,
         bounds.getMinY() - (double)padding,
         bounds.getWidth() + (double)(2 * padding),
         bounds.getHeight() + (double)(2 * padding)
      );
   }

   public VeilSupport(boolean spanAcrossWindows) {
      this(0.35, spanAcrossWindows);
   }

   public VeilSupport(double alpha, boolean spanAcrossWindows) {
      this.veiledRoot = new StackPane();
      this.veiledRoot.addEventFilter(MouseEvent.ANY, Event::consume);
      this.veiledRoot.setId("veiledRoot");
      this.veilStyle = "-fx-fill: rgba(0, 0, 0, " + Double.toString(Math.min(1.0, Math.max(0.0, alpha))) + ")";
      this.spanAcrossWindows = spanAcrossWindows;
   }

   public Parent getOriginalRoot() {
      return this.originalRoot;
   }

   public void setVeiledScene(Node sceneChildNode, boolean makeNodeVisible) {
      this.sceneChildNode = sceneChildNode;
      this.makeNodeVisible = makeNodeVisible;
      Scene scene = sceneChildNode.getScene();
      if (scene != this.veiledScene) {
         if (this.veiledScene != null && this.veiledScene.getRoot() == this.veiledRoot) {
            this.veiledScene.widthProperty().removeListener(this.resizeListener);
            this.veiledScene.heightProperty().removeListener(this.resizeListener);
            Window sceneContainer = this.veiledScene.getWindow();
            if (SystemUtils.IS_OS_MAC && sceneContainer != null) {
               sceneContainer.xProperty().removeListener(this.movedListener);
               sceneContainer.yProperty().removeListener(this.movedListener);
               this.veiledScreen = null;
            }

            Parent previousRoot = (Parent)this.veiledRoot.getChildren().get(0);
            this.veiledRoot.getChildren().remove(previousRoot);
            this.veiledScene.setRoot(previousRoot);
            this.veiledScene = null;
            this.originalRoot = null;
         }

         if (scene != null) {
            disableApplicationMenu(scene);
            this.veiledScene = scene;
            this.originalRoot = this.veiledScene.getRoot();
            StackPane.setAlignment(this.originalRoot, Pos.TOP_LEFT);
            this.veiledRoot.getChildren().clear();
            this.veiledRoot.getChildren().add(this.originalRoot);
            this.veiledScene.setRoot(this.veiledRoot);
            this.veiledScene.widthProperty().addListener(this.resizeListener);
            this.veiledScene.heightProperty().addListener(this.resizeListener);
            Window sceneContainer = this.veiledScene.getWindow();
            if (SystemUtils.IS_OS_MAC && sceneContainer != null) {
               this.veiledScreen = WindowUtil.screenForTopLeft(this.veiledScene);
               sceneContainer.xProperty().addListener(this.movedListener);
               sceneContainer.yProperty().addListener(this.movedListener);
            }
         }
      }

      if (this.veiledScene != null) {
         this.updateVeil();
      }
   }

   public void installHandler(Window window) {
      Objects.requireNonNull(window, "Null 'window' parameter.");
      this.window = window;
      if (this.unveilHandler != null) {
         throw new IllegalStateException("'installHandler' method invoked before uninstalling the previous one.");
      } else {
         this.unveilHandler = e -> {
            if (this.veiled) {
               this.unveil();
               this.uninstallHandler(window);
            } else {
               this.veiled = true;
            }
         };
         window.setOnHidden(this.unveilHandler);
      }
   }

   public void uninstallHandler(Window window) {
      this.unveil();
      if (this.spanAcrossWindows) {
         this.veiled = false;
      }

      Objects.requireNonNull(window, "Null 'window' parameter.").removeEventHandler(WindowEvent.WINDOW_HIDDEN, this.unveilHandler);
      this.unveilHandler = null;
   }

   public void unveil() {
      if (this.veiledScene != null && this.veiledScene.getRoot() == this.veiledRoot) {
         this.veiledScene.widthProperty().removeListener(this.resizeListener);
         this.veiledScene.heightProperty().removeListener(this.resizeListener);
         if (SystemUtils.IS_OS_MAC) {
            Window sceneContainer = this.veiledScene.getWindow();
            if (sceneContainer != null) {
               sceneContainer.xProperty().removeListener(this.movedListener);
               sceneContainer.yProperty().removeListener(this.movedListener);
               this.veiledScreen = null;
            }
         }

         Parent previousRoot = (Parent)this.veiledRoot.getChildren().get(0);
         this.veiledRoot.getChildren().remove(previousRoot);
         this.veiledScene.setRoot(previousRoot);
         enableApplicationMenu(this.veiledScene);
         this.veiledScene = null;
         this.originalRoot = null;
      }
   }

   private void updateVeil() {
      Parent oldRoot = (Parent)this.veiledRoot.getChildren().get(0);
      Rectangle oldRootBounds = toRectangle(oldRoot.localToScene(oldRoot.getBoundsInLocal()));
      Shape veil = oldRootBounds;
      if (this.makeNodeVisible) {
         Rectangle holeBounds = toRectangle(this.sceneChildNode.localToScene(this.sceneChildNode.getBoundsInLocal()), getHolePadding(this.sceneChildNode));
         veil = Shape.subtract(oldRootBounds, holeBounds);
      }

      veil.setStyle(this.veilStyle);
      StackPane.setAlignment(veil, Pos.TOP_LEFT);
      veil.setTranslateX(veil.getBoundsInLocal().getMinX());
      veil.setTranslateY(veil.getBoundsInLocal().getMinY());
      if (this.veiledRoot.getChildren().size() == 1) {
         this.veiledRoot.getChildren().add(veil);
      } else {
         this.veiledRoot.getChildren().set(1, veil);
      }

      if (!this.spanAcrossWindows) {
         this.veiled = true;
      }
   }
}
