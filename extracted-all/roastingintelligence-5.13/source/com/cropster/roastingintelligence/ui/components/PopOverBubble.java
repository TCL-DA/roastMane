package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.WindowManager;
import com.cropster.roastingintelligence.ui.components.skin.NoTitlePopOverSkin;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.Styles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Path;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils.ActionTextBehavior;

public final class PopOverBubble extends PopOver {
   public static final String POPOVERBUBBLE_STYLE_SHEET = PopOverBubble.class
      .getResource(SystemUtils.IS_OS_MAC_OSX ? "popoverbubble-mac.css" : "popoverbubble.css")
      .toExternalForm();
   public static final String POPOVER_BUBBLE_WINDOW_ID = "POPOVER_BUBBLE_WINDOW_ID";
   public static final int DEFAULT_MAXIMUM_CONTENT_WIDTH = 240;
   public static final int DEFAULT_MINIMUM_CONTENT_WIDTH = 80;
   private static final String ORIGINAL_PREF_WIDTH_PROPERTY = "original-pref-width";
   private static final String SKIP_RESIZE_PROPERTY = "skip--resize";
   private final BorderPane content;
   private final boolean detachedCentered;
   private final BorderPane footer;
   private final BorderPane header;
   private final String windowId;
   private double effectOffsetX = 0.0;
   private double effectOffsetY = 0.0;
   private double effectRadius = 0.0;
   private double strokeWidth = Double.NaN;
   private String borderStyle = null;

   private static void makeSamePrefWidth(Pane pane) {
      ObservableList<Node> children = pane.getChildren();
      List<Node> contributingChildren = children.stream()
         .filter(c -> !(Boolean)c.getProperties().getOrDefault("skip--resize", false))
         .collect(Collectors.toList());
      OptionalDouble optionalMaxWidth = contributingChildren.stream().filter(n -> n instanceof Region).map(n -> (Region)n).peek(r -> {
         ObservableMap<Object, Object> properties = r.getProperties();
         if (properties.containsKey("original-pref-width")) {
            r.setPrefWidth((Double)properties.get("original-pref-width"));
         }
      }).mapToDouble(r -> r.getWidth()).filter(pw -> pw > -1.0 && pw < Double.MAX_VALUE).max();
      if (optionalMaxWidth.isPresent()) {
         double maxWidth = optionalMaxWidth.getAsDouble();
         if (maxWidth * (double)contributingChildren.size() < pane.getWidth()) {
            contributingChildren.stream()
               .filter(n -> n instanceof Region)
               .map(n -> (Region)n)
               .peek(r -> r.getProperties().put("original-pref-width", r.getPrefWidth()))
               .forEach(r -> {
                  r.setPrefWidth(maxWidth);
                  r.setMinWidth(maxWidth);
                  r.setMaxWidth(maxWidth);
               });
            pane.layout();
         }
      }
   }

   public static PopOverBubble.Builder builder() {
      return new PopOverBubble.Builder();
   }

   private PopOverBubble(PopOverBubble.Builder builder) {
      this.header = builder.header;
      this.content = builder.content;
      this.footer = builder.footer;
      this.detachedCentered = builder.detachedCentered;
      this.borderStyle = builder.borderStyle;
      this.windowId = builder.windowId;
      if (StringUtils.isNotBlank(builder.id)) {
         this.setId(builder.id);
      }

      this.setArrowIndent(12.0);
      this.setArrowLocation(builder.arrowLocation);
      this.setArrowSize(12.0);
      this.setCloseButtonEnabled(false);
      this.setCornerRadius(builder.cornerRadius);
      this.setDetachable(builder.detachable);
      this.setDetached(builder.detached);
      this.setHeaderAlwaysVisible(true);
      if (builder.onCloseCallback != null) {
         this.setOnHidden(e -> builder.onCloseCallback.run());
      }

      this.getRoot().getStylesheets().addAll(new String[]{JavaFxController.STYLE_SHEET, POPOVERBUBBLE_STYLE_SHEET});
      this.init(builder.closable, builder.minimumWidth, builder.maximumWidth, builder.maximumHeight, builder.smallTextPadding);
      if (this.content != null) {
         if (builder.darkBackground) {
            this.content.getStyleClass().add("dark-background");
         }

         if (builder.contentExtraHorizontalPadding != 0.0 || builder.contentExtraVerticalPadding != 0.0) {
            double hpadding = 16.0 + builder.contentExtraHorizontalPadding;
            double vpadding = 16.0 + builder.contentExtraVerticalPadding;
            this.content.setStyle("-fx-padding: " + vpadding + " " + hpadding + " " + vpadding + " " + hpadding);
         }
      }
   }

   public String getBorderStyle() {
      return this.borderStyle;
   }

   public Node getContentElement() {
      return this.content != null && !this.content.getChildren().isEmpty() ? (Node)this.content.getChildren().get(0) : null;
   }

   public Node getFooterElement() {
      return this.footer != null && !this.footer.getChildren().isEmpty() ? (Node)this.footer.getChildren().get(0) : null;
   }

   public Node getHeaderElement() {
      return this.content != null && !this.content.getChildren().isEmpty() ? (Node)this.content.getChildren().get(0) : null;
   }

   public String getWindowId() {
      return this.windowId;
   }

   public void showAt(Node owner) {
      this.showAt(owner, this.getArrowLocation());
   }

   public void showAt(Node owner, ArrowLocation location) {
      if (location == null) {
         location = ArrowLocation.TOP_CENTER;
      }

      this.setBestArrowLocation(owner, location);
   }

   public void showAt(Node owner, double anchorX, double anchorY) {
      this.showAt(owner, ArrowLocation.TOP_CENTER, anchorX, anchorY);
   }

   public void showAt(Node owner, ArrowLocation location, double anchorX, double anchorY) {
      WindowManager wm = WindowManager.get();
      wm.getWindow(this.windowId).ifPresent(wm::unregister);
      if (!wm.isLocked()) {
         if (this.isDetached()) {
            switch (location) {
               case TOP_LEFT:
               case TOP_CENTER:
               case TOP_RIGHT:
                  anchorY -= this.getArrowSize();
                  break;
               case BOTTOM_LEFT:
               case BOTTOM_CENTER:
               case BOTTOM_RIGHT:
                  anchorY += this.getArrowSize();
            }

            switch (location) {
               case LEFT_TOP:
               case LEFT_CENTER:
               case LEFT_BOTTOM:
                  anchorX -= this.getArrowSize();
                  break;
               case RIGHT_TOP:
               case RIGHT_CENTER:
               case RIGHT_BOTTOM:
                  anchorX += this.getArrowSize();
            }

            anchorX += this.getArrowIndent();
            anchorY += this.getArrowIndent();
         }

         this.setArrowLocation(location);
         this.show(owner, anchorX, anchorY);
         wm.register(this, this.windowId);
      }
   }

   protected Skin<?> createDefaultSkin() {
      return new NoTitlePopOverSkin(this);
   }

   private double computeXOffset() {
      switch (this.getArrowLocation()) {
         case TOP_LEFT:
         case BOTTOM_LEFT:
            return this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize();
         case TOP_CENTER:
         case BOTTOM_CENTER:
            return this.getContentNode().prefWidth(-1.0) / 2.0;
         case TOP_RIGHT:
         case BOTTOM_RIGHT:
            return this.getContentNode().prefWidth(-1.0) - this.getArrowIndent() - this.getCornerRadius() - this.getArrowSize();
         default:
            return 0.0;
      }
   }

   private double computeYOffset() {
      switch (this.getArrowLocation()) {
         case LEFT_TOP:
         case RIGHT_TOP:
            return this.getCornerRadius() + this.getArrowIndent() + this.getArrowSize();
         case LEFT_CENTER:
         case RIGHT_CENTER:
            return this.getContentNode().prefHeight(-1.0) / 2.0;
         case LEFT_BOTTOM:
         case RIGHT_BOTTOM:
            return this.getContentNode().prefHeight(-1.0) - this.getArrowIndent() - this.getCornerRadius() - this.getArrowSize();
         default:
            return 0.0;
      }
   }

   private double getBorderSize() {
      return this.getStrokeWidth() + this.getEffectRadius() + this.getStrokeWidth() / 2.0;
   }

   private double getEffectOffsetX() {
      this.updateBorderData();
      return this.effectOffsetX;
   }

   private double getEffectOffsetY() {
      this.updateBorderData();
      return this.effectOffsetY;
   }

   private double getEffectRadius() {
      this.updateBorderData();
      return this.effectRadius;
   }

   private double getStrokeWidth() {
      this.updateBorderData();
      return this.strokeWidth;
   }

   private double getBottomOffset() {
      Bounds sBounds = this.getSkin().getNode().localToScreen(this.getSkin().getNode().getBoundsInLocal());
      Bounds cBounds = this.getContentNode().localToScreen(this.getContentNode().getBoundsInLocal());
      double offset = sBounds.getMaxY() - cBounds.getMaxY() + (sBounds.getMinY() - cBounds.getMinY());
      if (this.getEffectRadius() > this.getEffectOffsetY()) {
         offset -= this.getEffectRadius() - this.getEffectOffsetY();
      }

      return offset - (this.getArrowSize() + 1.25 * this.getStrokeWidth());
   }

   private double getTopOffset() {
      return 0.75 * this.getStrokeWidth();
   }

   private double getRightOffset() {
      Bounds sBounds = this.getSkin().getNode().localToScreen(this.getSkin().getNode().getBoundsInLocal());
      Bounds cBounds = this.getContentNode().localToScreen(this.getContentNode().getBoundsInLocal());
      double offset = sBounds.getMaxX() - cBounds.getMaxX() + (sBounds.getMinX() - cBounds.getMinX());
      if (this.getEffectRadius() > this.getEffectOffsetX()) {
         offset -= this.getEffectRadius() - this.getEffectOffsetX();
      }

      return offset - (this.getArrowSize() + 1.25 * this.getStrokeWidth());
   }

   private double getLeftOffset() {
      return 0.75 * this.getStrokeWidth();
   }

   private double getBottomBorder() {
      return Math.max(0.0, this.getBorderSize() + this.getEffectOffsetY()) + this.getStrokeWidth();
   }

   private double getTopBorder() {
      return Math.max(0.0, this.getBorderSize() - this.getEffectOffsetY()) + this.getStrokeWidth();
   }

   private double getLeftBorder() {
      return Math.max(0.0, this.getBorderSize() - this.getEffectOffsetX()) + this.getStrokeWidth();
   }

   private double getRightBorder() {
      return Math.max(0.0, this.getBorderSize() + this.getEffectOffsetX()) + this.getStrokeWidth();
   }

   private void updateBorderData() {
      if (Double.isNaN(this.strokeWidth)) {
         NoTitlePopOverSkin skin = (NoTitlePopOverSkin)this.getSkin();
         if (skin != null) {
            Path path = skin.getPath();
            path.applyCss();
            Effect effect = path.getEffect();
            if (effect instanceof DropShadow) {
               this.effectOffsetX = ((DropShadow)effect).getOffsetX();
               this.effectOffsetY = ((DropShadow)effect).getOffsetY();
               this.effectRadius = ((DropShadow)effect).getRadius();
            }

            this.strokeWidth = path.getStrokeWidth();
         }
      }
   }

   private List<ArrowLocation> getTestLocations(ArrowLocation location) {
      switch (location) {
         case TOP_LEFT:
            return List.of(
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM
            );
         case TOP_CENTER:
            return List.of(
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM
            );
         case TOP_RIGHT:
            return List.of(
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM
            );
         case BOTTOM_LEFT:
            return List.of(
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_RIGHT,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.RIGHT_BOTTOM,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_CENTER
            );
         case BOTTOM_CENTER:
            return List.of(
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM
            );
         case BOTTOM_RIGHT:
            return List.of(
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM
            );
         case LEFT_TOP:
            return List.of(
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT
            );
         case LEFT_CENTER:
            return List.of(
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT
            );
         case LEFT_BOTTOM:
            return List.of(
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT
            );
         case RIGHT_TOP:
            return List.of(
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_BOTTOM,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT
            );
         case RIGHT_CENTER:
            return List.of(
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.RIGHT_BOTTOM,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT
            );
         case RIGHT_BOTTOM:
            return List.of(
               ArrowLocation.RIGHT_CENTER,
               ArrowLocation.RIGHT_TOP,
               ArrowLocation.LEFT_CENTER,
               ArrowLocation.LEFT_TOP,
               ArrowLocation.LEFT_BOTTOM,
               ArrowLocation.TOP_CENTER,
               ArrowLocation.TOP_LEFT,
               ArrowLocation.TOP_RIGHT,
               ArrowLocation.BOTTOM_CENTER,
               ArrowLocation.BOTTOM_LEFT,
               ArrowLocation.BOTTOM_RIGHT
            );
         default:
            return List.of();
      }
   }

   private void init(boolean closable, double minimumWidth, double maximumWidth, double maximumHeight, boolean smallTextPadding) {
      ColumnConstraints hConstraints = new ColumnConstraints();
      hConstraints.setFillWidth(true);
      hConstraints.setHalignment(HPos.LEFT);
      hConstraints.setHgrow(Priority.ALWAYS);
      hConstraints.setMaxWidth(Double.MAX_VALUE);
      GridPane mainContainer = new GridPane();
      mainContainer.setId("popoverContainer");
      mainContainer.getColumnConstraints().add(hConstraints);
      if (this.header != null) {
         this.initHeader(minimumWidth, maximumWidth, closable, mainContainer);
      }

      if (this.content != null) {
         this.initContent(minimumWidth, maximumWidth, mainContainer, smallTextPadding);
      }

      if (this.footer != null) {
         this.initFooter(minimumWidth, maximumWidth, mainContainer);
      }

      if (maximumHeight != Double.POSITIVE_INFINITY) {
         mainContainer.setMaxHeight(maximumHeight);
      }

      JavaFxUtil.setDefaultLineSpacing(mainContainer);
      this.setContentNode(mainContainer);
   }

   private void initContent(double minimumWidth, double maximumWidth, GridPane mainContainer, boolean smallTextPadding) {
      boolean hasHeader = this.header != null;
      boolean hasFooter = this.footer != null;
      this.content.setId("popoverContent");
      this.content.setMinWidth(16.0 + minimumWidth + 16.0);
      this.content.setMaxWidth(16.0 + maximumWidth + 16.0);
      GridPane.setConstraints(this.content, 0, hasHeader ? 2 : 0);
      if (this.content.getChildren().size() == 1 && this.content.getCenter() instanceof TextFlow) {
         TextFlow textFlow = (TextFlow)this.content.getCenter();
         textFlow.setPadding(smallTextPadding ? Styles.INSETS_SMALL : Styles.INSETS_DEFAULT);
      } else {
         this.content.setPadding(smallTextPadding ? Styles.INSETS_SMALL : Styles.INSETS_DEFAULT);
      }

      RowConstraints vConstraints = new RowConstraints();
      vConstraints.setFillHeight(true);
      vConstraints.setValignment(VPos.CENTER);
      vConstraints.setVgrow(Priority.ALWAYS);
      mainContainer.getRowConstraints().add(vConstraints);
      mainContainer.getChildren().add(this.content);
      if (hasFooter) {
         Separator separator = new Separator(Orientation.HORIZONTAL);
         separator.setId("popoverContentSeparator");
         GridPane.setConstraints(separator, 0, hasHeader ? 3 : 1);
         vConstraints = new RowConstraints();
         vConstraints.setVgrow(Priority.NEVER);
         mainContainer.getRowConstraints().add(vConstraints);
         mainContainer.getChildren().add(separator);
      }
   }

   private void initFooter(double minimumWidth, double maximumWidth, GridPane mainContainer) {
      boolean hasHeader = this.header != null;
      boolean hasContent = this.content != null;
      boolean footerOnly = !hasHeader && !hasContent;
      this.footer.setId("popoverFooter");
      this.footer.setMinWidth(16.0 + minimumWidth + 16.0);
      this.footer.setMaxWidth(16.0 + maximumWidth + 16.0);
      GridPane.setConstraints(this.footer, 0, footerOnly ? 0 : (hasHeader && hasContent ? 4 : 2));
      RowConstraints vConstraints = new RowConstraints();
      vConstraints.setFillHeight(true);
      vConstraints.setValignment(VPos.CENTER);
      vConstraints.setVgrow(footerOnly ? Priority.ALWAYS : Priority.NEVER);
      mainContainer.getChildren().add(this.footer);
      mainContainer.getRowConstraints().add(vConstraints);
   }

   private void initHeader(double minimumWidth, double maximumWidth, boolean closable, GridPane mainContainer) {
      this.header.setId("popoverHeader");
      this.header.setMinWidth(16.0 + minimumWidth + 16.0);
      this.header.setMaxWidth(16.0 + maximumWidth + 16.0);
      GridPane.setConstraints(this.header, 0, 0);
      boolean headerOnly = this.content == null && this.footer == null;
      RowConstraints vConstraints = new RowConstraints();
      vConstraints.setFillHeight(true);
      vConstraints.setValignment(VPos.CENTER);
      vConstraints.setVgrow(headerOnly ? Priority.ALWAYS : Priority.NEVER);
      mainContainer.getRowConstraints().add(vConstraints);
      mainContainer.getChildren().add(this.header);
      if (!headerOnly) {
         Separator separator = new Separator(Orientation.HORIZONTAL);
         separator.setId("popoverHeaderSeparator");
         GridPane.setConstraints(separator, 0, 1);
         vConstraints = new RowConstraints();
         vConstraints.setVgrow(Priority.NEVER);
         mainContainer.getRowConstraints().add(vConstraints);
         mainContainer.getChildren().add(separator);
      }

      if (closable) {
         Button closeButton = new Button(null, Icon.CLOSE.load());
         closeButton.setId("popoverCloseButton");
         closeButton.setOnAction(e -> this.hide());
         BorderPane.setMargin(closeButton, Styles.INSETS_LEFT_TINY);
         this.header.setRight(closeButton);
      }
   }

   private boolean isValidLocation(ArrowLocation location, Node owner) {
      if (owner == null) {
         return false;
      } else {
         this.setArrowLocation(location);
         this.offsetShow(owner);
         Bounds oBounds = owner.localToScreen(owner.getBoundsInLocal());
         ObservableList<Screen> oScreens = Screen.getScreensForRectangle(oBounds.getMinX(), oBounds.getMinY(), oBounds.getWidth(), oBounds.getHeight());
         if (!oScreens.isEmpty() && oScreens.size() <= 1) {
            Bounds pBounds = this.getSkin().getNode().localToScreen(this.getSkin().getNode().getBoundsInLocal());
            ObservableList<Screen> pScreens = Screen.getScreensForRectangle(pBounds.getMinX(), pBounds.getMinY(), pBounds.getWidth(), pBounds.getHeight());
            if (!pScreens.isEmpty() && pScreens.size() <= 1 && Objects.equals(oScreens.get(0), pScreens.get(0))) {
               Bounds cBounds = this.getContentNode().localToScreen(this.getContentNode().getBoundsInLocal());
               if (cBounds.intersects(oBounds)) {
                  return false;
               } else {
                  switch (location) {
                     case TOP_LEFT:
                     case TOP_CENTER:
                     case TOP_RIGHT: {
                        double x = pBounds.getMinX() + this.computeXOffset() + this.getLeftBorder();
                        double y = pBounds.getMinY() + this.getTopBorder();
                        return x >= oBounds.getMinX() && x <= oBounds.getMaxX() && y >= oBounds.getMaxY();
                     }
                     case BOTTOM_LEFT:
                     case BOTTOM_CENTER:
                     case BOTTOM_RIGHT: {
                        double x = pBounds.getMinX() + this.computeXOffset() + this.getLeftBorder();
                        double y = pBounds.getMaxY() - this.getBottomBorder();
                        return x >= oBounds.getMinX() && x <= oBounds.getMaxX() && y <= oBounds.getMinY();
                     }
                     case LEFT_TOP:
                     case LEFT_CENTER:
                     case LEFT_BOTTOM: {
                        double x = pBounds.getMaxX() + this.getLeftBorder();
                        double y = pBounds.getMinY() + this.computeYOffset() + this.getTopBorder();
                        return y >= oBounds.getMinY() && y <= oBounds.getMaxY() && x >= oBounds.getMaxX();
                     }
                     case RIGHT_TOP:
                     case RIGHT_CENTER:
                     case RIGHT_BOTTOM: {
                        double x = pBounds.getMaxX() - this.getRightBorder();
                        double y = pBounds.getMinY() + this.computeYOffset() + this.getTopBorder();
                        return y >= oBounds.getMinY() && y <= oBounds.getMaxY() && x <= oBounds.getMinX();
                     }
                     default:
                        return true;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private void offsetShow(Node owner) {
      Objects.requireNonNull(owner);
      boolean detached = this.isDetached();

      try {
         Bounds ownerBounds = owner.localToScreen(owner.getBoundsInLocal());
         switch (this.getArrowLocation()) {
            case TOP_LEFT:
            case TOP_CENTER:
            case TOP_RIGHT:
               this.show(owner, ownerBounds.getMinX() + ownerBounds.getWidth() / 2.0, ownerBounds.getMaxY() + this.getTopOffset());
               break;
            case BOTTOM_LEFT:
            case BOTTOM_CENTER:
            case BOTTOM_RIGHT:
               this.show(owner, ownerBounds.getMinX() + ownerBounds.getWidth() / 2.0, ownerBounds.getMinY() + this.getBottomOffset());
               break;
            case LEFT_TOP:
            case LEFT_CENTER:
            case LEFT_BOTTOM:
               this.show(owner, ownerBounds.getMaxX() + this.getLeftOffset(), ownerBounds.getMinY() + ownerBounds.getHeight() / 2.0);
               break;
            case RIGHT_TOP:
            case RIGHT_CENTER:
            case RIGHT_BOTTOM:
               this.show(owner, ownerBounds.getMinX() + this.getRightOffset(), ownerBounds.getMinY() + ownerBounds.getHeight() / 2.0);
         }
      } finally {
         this.setDetached(detached);
      }
   }

   private void setBestArrowLocation(Node owner, ArrowLocation location) {
      this.show(owner);
      if (!this.isDetached() && !this.isValidLocation(location, owner)) {
         location = this.getTestLocations(location).stream().filter(l -> this.isValidLocation(l, owner)).findFirst().orElse(location);
      }

      WindowManager wm = WindowManager.get();
      wm.getWindow(this.windowId).ifPresent(wm::unregister);
      if (!wm.isLocked()) {
         this.setArrowLocation(location);
         this.offsetShow(owner);
         if (this.isDetached() && this.detachedCentered) {
            Bounds ownerBounds = owner.getBoundsInLocal();
            Point2D ownerCenter = owner.localToScreen(ownerBounds.getCenterX(), ownerBounds.getCenterY());
            double centerX = ownerCenter.getX();
            double centerY = ownerCenter.getY();
            Parent root = owner.getScene().getRoot();
            if (root == owner || "veiledRoot".equals(root.getId()) && !((Pane)root).getChildren().isEmpty() && ((Pane)root).getChildren().get(0) == owner) {
               Window window = owner.getScene().getWindow();
               centerX = window.getX() + window.getWidth() / 2.0;
               centerY = window.getY() + window.getHeight() / 2.0;
            }

            this.setAnchorX(centerX - this.getSkin().getNode().getBoundsInParent().getWidth() / 2.0);
            this.setAnchorY(centerY - this.getSkin().getNode().getBoundsInParent().getHeight() / 2.0);
         }

         wm.register(this, this.windowId);
      }
   }

   public static class Builder {
      private ArrowLocation arrowLocation = ArrowLocation.TOP_CENTER;
      private String borderStyle;
      private boolean closable = true;
      private BorderPane content;
      private double contentExtraHorizontalPadding = 0.0;
      private double contentExtraVerticalPadding = 0.0;
      private double cornerRadius = 7.0;
      private boolean darkBackground;
      private boolean detachable = true;
      private boolean detached;
      private boolean detachedCentered;
      private BorderPane footer;
      private BorderPane header;
      private String id;
      private double maximumHeight = Double.POSITIVE_INFINITY;
      private double maximumWidth = 240.0;
      private double minimumWidth = 80.0;
      private Runnable onCloseCallback;
      private String windowId = "POPOVER_BUBBLE_WINDOW_ID";
      private boolean smallTextPadding;

      public PopOverBubble build() {
         return new PopOverBubble(this);
      }

      public PopOverBubble.Builder arrowLocation(ArrowLocation location) {
         if (location != null) {
            this.arrowLocation = location;
         }

         return this;
      }

      public PopOverBubble.Builder borderStyle(String borderStyle) {
         this.borderStyle = borderStyle;
         return this;
      }

      public PopOverBubble.Builder closable(boolean closable) {
         this.closable = closable;
         return this;
      }

      public PopOverBubble.Builder cornerRadius(double cornerRadius) {
         this.cornerRadius = cornerRadius;
         return this;
      }

      public PopOverBubble.Builder maximumHeight(double maximumHeight) {
         this.maximumHeight = maximumHeight;
         return this;
      }

      public PopOverBubble.Builder maximumWidth(double maximumWidth) {
         this.maximumWidth = maximumWidth;
         return this;
      }

      public PopOverBubble.Builder minimumWidth(double minimumWidth) {
         this.minimumWidth = minimumWidth;
         return this;
      }

      public PopOverBubble.Builder onClose(Runnable callback) {
         this.onCloseCallback = callback;
         return this;
      }

      public PopOverBubble.Builder windowId(String windowId) {
         if (StringUtils.isNotBlank(windowId)) {
            this.windowId = windowId;
         }

         return this;
      }

      public PopOverBubble.Builder withContent(Node contentElement) {
         if (contentElement != null) {
            this.content = new BorderPane(contentElement);
         }

         return this;
      }

      public PopOverBubble.Builder withContent(Node... content) {
         if (content.length > 0) {
            TextFlow popoverContentTextFlow = new TextFlow(content);
            popoverContentTextFlow.setId("popoverContentTextFlow");
            return this.withContent(popoverContentTextFlow);
         } else {
            return this;
         }
      }

      public PopOverBubble.Builder withContent(Collection<Node> content) {
         return content != null && !content.isEmpty() ? this.withContent(content.toArray(Node[]::new)) : this;
      }

      public PopOverBubble.Builder withContent(List<String> content) {
         return content != null && !content.isEmpty() ? this.withContent(content.toArray(String[]::new)) : this;
      }

      public PopOverBubble.Builder withContent(String... content) {
         return content.length > 0 ? this.withContent(Stream.of(content).flatMap(t -> JavaFxUtil.parseHtmlToTextNodes(t).stream()).toArray(Node[]::new)) : this;
      }

      public PopOverBubble.Builder withContentExtraHorizontalPadding(double contentExtraHorizontalPadding) {
         this.contentExtraHorizontalPadding = contentExtraHorizontalPadding;
         return this;
      }

      public PopOverBubble.Builder withContentExtraVerticalPadding(double contentExtraVerticalPadding) {
         this.contentExtraVerticalPadding = contentExtraVerticalPadding;
         return this;
      }

      public PopOverBubble.Builder withContentExtraPadding(double contentExtraHorizontalPadding, double contentExtraVerticalPadding) {
         this.contentExtraHorizontalPadding = contentExtraHorizontalPadding;
         this.contentExtraVerticalPadding = contentExtraVerticalPadding;
         return this;
      }

      public PopOverBubble.Builder withDarkBackground() {
         this.darkBackground = true;
         return this;
      }

      public PopOverBubble.Builder withoutDetachable() {
         this.detachable = false;
         return this;
      }

      public PopOverBubble.Builder withDetached() {
         this.detached = true;
         return this;
      }

      public PopOverBubble.Builder withDetachedCentered() {
         this.detachedCentered = true;
         return this;
      }

      public PopOverBubble.Builder withFooter(Node footerElement) {
         if (footerElement != null) {
            this.footer = new BorderPane(footerElement);
         }

         return this;
      }

      public PopOverBubble.Builder withFooter(
         Collection<? extends Action> leftActions, boolean leftShowText, Collection<? extends Action> rightActions, boolean rightShowText, boolean uniformSize
      ) {
         return this.withFooter(
            leftActions != null
               ? leftActions.stream().map(a -> ActionBuilder.buttonFrom(a, leftShowText ? ActionTextBehavior.SHOW : ActionTextBehavior.HIDE)).toList()
               : null,
            rightActions != null
               ? rightActions.stream().map(a -> ActionBuilder.buttonFrom(a, rightShowText ? ActionTextBehavior.SHOW : ActionTextBehavior.HIDE)).toList()
               : null,
            uniformSize
         );
      }

      public PopOverBubble.Builder withFooter(Collection<? extends Button> leftButtons, Collection<? extends Button> rightButtons, boolean uniformSize) {
         if (leftButtons != null && !leftButtons.isEmpty() || rightButtons != null && !rightButtons.isEmpty()) {
            int column = 0;
            GridPane popoverFooterButtonsPane = new GridPane();
            popoverFooterButtonsPane.setId("popoverFooterButtonsPane");
            if (leftButtons != null && !leftButtons.isEmpty()) {
               for (Button b : leftButtons) {
                  b.setMinWidth(32.0);
                  GridPane.setConstraints(b, column++, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
                  popoverFooterButtonsPane.getChildren().add(b);
               }
            }

            Pane filler = new Pane();
            filler.getProperties().put("skip--resize", true);
            GridPane.setConstraints(filler, column++, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
            popoverFooterButtonsPane.getChildren().add(filler);
            if (rightButtons != null && !rightButtons.isEmpty()) {
               List<Button> rightList = new ArrayList<>(rightButtons);
               Collections.reverse(rightList);

               for (Button b : rightList) {
                  b.setMinWidth(32.0);
                  GridPane.setConstraints(b, column++, 0, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
                  popoverFooterButtonsPane.getChildren().add(b);
               }
            }

            if (uniformSize) {
               popoverFooterButtonsPane.widthProperty()
                  .addListener((s, o, n) -> Platform.runLater(() -> PopOverBubble.makeSamePrefWidth(popoverFooterButtonsPane)));
            }

            return this.withFooter(popoverFooterButtonsPane);
         } else {
            return this;
         }
      }

      public PopOverBubble.Builder withHeader(String header) {
         return this.withHeader(header, null);
      }

      public PopOverBubble.Builder withHeader(Node headerElement) {
         return this.withHeader(headerElement, false);
      }

      public PopOverBubble.Builder withHeader(Node headerElement, boolean centered) {
         if (headerElement != null) {
            this.header = new BorderPane();
            if (centered) {
               this.header.setCenter(headerElement);
            } else {
               this.header.setLeft(headerElement);
            }
         }

         return this;
      }

      public PopOverBubble.Builder withHeader(String header, Node graphics) {
         if (StringUtils.isNotBlank(header)) {
            if (Objects.nonNull(graphics)) {
               graphics = new BorderPane(graphics);
               ((BorderPane)graphics).setPadding(Styles.INSETS_TINY);
            }

            Label headerLabel = new Label(header, graphics);
            headerLabel.setId("popoverHeaderLabel");
            if (Objects.isNull(graphics)) {
               headerLabel.setPadding(Styles.INSETS_TOP_BOTTOM_TINY);
            }

            return this.withHeader(headerLabel);
         } else {
            return this;
         }
      }

      public PopOverBubble.Builder withId(String id) {
         this.id = id;
         return this;
      }

      public PopOverBubble.Builder withSmallTextPadding(boolean smallTextPadding) {
         this.smallTextPadding = smallTextPadding;
         return this;
      }
   }
}
