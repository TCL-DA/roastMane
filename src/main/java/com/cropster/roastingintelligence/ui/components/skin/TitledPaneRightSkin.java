package com.cropster.roastingintelligence.ui.components.skin;

import com.cropster.roastingintelligence.ui.components.TitledPaneRight;
import com.cropster.roastingintelligence.ui.components.Tooltip;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.behavior.TitledPaneBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Labeled;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class TitledPaneRightSkin extends LabeledSkinBase<TitledPaneRight> {
   private static final boolean CACHE_ANIMATION = PlatformUtil.isEmbedded();
   private static final Duration TRANSITION_DURATION = new Duration(350.0);
   private final TitledPaneBehavior behavior;
   private final StackPane contentContainer;
   private final TitledPaneRightSkin.TitleRegion titleRegion = new TitledPaneRightSkin.TitleRegion();
   private Rectangle clipRect = new Rectangle();
   private Node content;
   private HPos hpos;
   private Pos pos;
   private Timeline timeline;
   private DoubleProperty transition;
   private double transitionStartValue = 0.0;
   private VPos vpos;
   private double prefHeightFromAccordion = 0.0;

   public TitledPaneRightSkin(TitledPaneRight control) {
      super(control);
      this.behavior = new TitledPaneBehavior(control);
      this.content = ((TitledPaneRight)this.getSkinnable()).getContent();
      this.contentContainer = new StackPane() {
         {
            this.getStyleClass().setAll(new String[]{"content"});
            if (TitledPaneRightSkin.this.content != null) {
               this.getChildren().setAll(new Node[]{TitledPaneRightSkin.this.content});
            }
         }
      };
      this.contentContainer.setClip(this.clipRect);
      this.updateClip();
      if (control.isExpanded()) {
         this.setTransition(1.0);
         this.setExpanded(control.isExpanded());
      } else {
         this.setTransition(0.0);
         if (this.content != null) {
            this.content.setVisible(false);
         }
      }

      this.getChildren().setAll(new Node[]{this.contentContainer, this.titleRegion});
      this.registerChangeListener(control.contentProperty(), e -> {
         this.content = ((TitledPaneRight)this.getSkinnable()).getContent();
         if (this.content == null) {
            this.contentContainer.getChildren().clear();
         } else {
            this.contentContainer.getChildren().setAll(new Node[]{this.content});
         }
      });
      this.registerChangeListener(control.expandedProperty(), e -> this.setExpanded(((TitledPaneRight)this.getSkinnable()).isExpanded()));
      this.registerChangeListener(control.collapsibleProperty(), e -> this.titleRegion.update());
      this.registerChangeListener(control.alignmentProperty(), e -> {
         this.pos = ((TitledPaneRight)this.getSkinnable()).getAlignment();
         this.hpos = this.pos.getHpos();
         this.vpos = this.pos.getVpos();
      });
      this.registerChangeListener(control.widthProperty(), e -> this.updateClip());
      this.registerChangeListener(control.heightProperty(), e -> this.updateClip());
      this.registerChangeListener(this.titleRegion.alignmentProperty(), e -> {
         this.pos = this.titleRegion.getAlignment();
         this.hpos = this.pos.getHpos();
         this.vpos = this.pos.getVpos();
      });
      this.pos = control.getAlignment();
      this.hpos = this.pos == null ? HPos.LEFT : this.pos.getHpos();
      this.vpos = this.pos == null ? VPos.CENTER : this.pos.getVpos();
   }

   private void setTransition(double value) {
      this.transitionProperty().set(value);
   }

   private double getTransition() {
      return this.transition == null ? 0.0 : this.transition.get();
   }

   private DoubleProperty transitionProperty() {
      if (this.transition == null) {
         this.transition = new SimpleDoubleProperty(this, "transition", 0.0) {
            protected void invalidated() {
               TitledPaneRightSkin.this.contentContainer.requestLayout();
            }
         };
      }

      return this.transition;
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   @Override
   protected void updateChildren() {
      if (this.titleRegion != null) {
         this.titleRegion.update();
      }
   }

   @Override
   protected void layoutChildren(double x, double y, double w, double h) {
      double headerHeight = this.snapSizeY(this.titleRegion.prefHeight(-1.0));
      this.titleRegion.resize(w, headerHeight);
      this.positionInArea(this.titleRegion, x, y, w, headerHeight, 0.0, HPos.LEFT, VPos.CENTER);
      this.titleRegion.requestLayout();
      double contentHeight = (h - headerHeight) * this.getTransition();
      if (this.isInsideAccordion() && this.prefHeightFromAccordion != 0.0) {
         contentHeight = (this.prefHeightFromAccordion - headerHeight) * this.getTransition();
      }

      contentHeight = this.snapSizeY(contentHeight);
      y += headerHeight;
      this.contentContainer.resize(w, contentHeight);
      this.clipRect.setHeight(contentHeight);
      this.positionInArea(this.contentContainer, x, y, w, contentHeight, 0.0, HPos.CENTER, VPos.CENTER);
   }

   @Override
   protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      double titleWidth = this.snapSizeX(this.titleRegion.prefWidth(height));
      double contentWidth = this.snapSizeX(this.contentContainer.minWidth(height));
      return Math.max(titleWidth, contentWidth) + leftInset + rightInset;
   }

   @Override
   protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      double headerHeight = this.snapSizeY(this.titleRegion.prefHeight(width));
      double contentHeight = this.contentContainer.minHeight(width) * this.getTransition();
      return headerHeight + this.snapSizeY(contentHeight) + topInset + bottomInset;
   }

   @Override
   protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      double titleWidth = this.snapSizeX(this.titleRegion.prefWidth(height));
      double contentWidth = this.snapSizeX(this.contentContainer.prefWidth(height));
      return Math.max(titleWidth, contentWidth) + leftInset + rightInset;
   }

   @Override
   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      double headerHeight = this.snapSizeY(this.titleRegion.prefHeight(width));
      double contentHeight = this.contentContainer.prefHeight(width) * this.getTransition();
      return headerHeight + this.snapSizeY(contentHeight) + topInset + bottomInset;
   }

   @Override
   protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
      return Double.MAX_VALUE;
   }

   private void updateClip() {
      this.clipRect.setWidth(((TitledPaneRight)this.getSkinnable()).getWidth());
      this.clipRect.setHeight(this.contentContainer.getHeight());
   }

   private void setExpanded(boolean expanded) {
      if (!((TitledPaneRight)this.getSkinnable()).isCollapsible()) {
         this.setTransition(1.0);
      } else {
         if (((TitledPaneRight)this.getSkinnable()).isAnimated()) {
            this.transitionStartValue = this.getTransition();
            this.doAnimationTransition();
         } else {
            if (expanded) {
               this.setTransition(1.0);
            } else {
               this.setTransition(0.0);
            }

            if (this.content != null) {
               this.content.setVisible(expanded);
            }

            ((TitledPaneRight)this.getSkinnable()).requestLayout();
         }
      }
   }

   private boolean isInsideAccordion() {
      return ((TitledPaneRight)this.getSkinnable()).getParent() != null && ((TitledPaneRight)this.getSkinnable()).getParent() instanceof Accordion;
   }

   double getTitleRegionSize(double width) {
      return this.snapSizeY(this.titleRegion.prefHeight(width)) + this.snappedTopInset() + this.snappedBottomInset();
   }

   void setMaxTitledPaneHeightForAccordion(double height) {
      this.prefHeightFromAccordion = height;
   }

   double getTitledPaneHeightForAccordion() {
      double headerHeight = this.snapSizeY(this.titleRegion.prefHeight(-1.0));
      double contentHeight = (this.prefHeightFromAccordion - headerHeight) * this.getTransition();
      return headerHeight + this.snapSizeY(contentHeight) + this.snappedTopInset() + this.snappedBottomInset();
   }

   private void doAnimationTransition() {
      if (this.content != null) {
         Duration duration;
         if (this.timeline != null && this.timeline.getStatus() != Status.STOPPED) {
            duration = this.timeline.getCurrentTime();
            this.timeline.stop();
         } else {
            duration = TRANSITION_DURATION;
         }

         this.timeline = new Timeline();
         this.timeline.setCycleCount(1);
         KeyFrame k1;
         KeyFrame k2;
         if (((TitledPaneRight)this.getSkinnable()).isExpanded()) {
            k1 = new KeyFrame(Duration.ZERO, event -> {
               if (CACHE_ANIMATION) {
                  this.content.setCache(true);
               }

               this.content.setVisible(true);
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), this.transitionStartValue)});
            k2 = new KeyFrame(duration, event -> {
               if (CACHE_ANIMATION) {
                  this.content.setCache(false);
               }
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), 1, Interpolator.LINEAR)});
         } else {
            k1 = new KeyFrame(Duration.ZERO, event -> {
               if (CACHE_ANIMATION) {
                  this.content.setCache(true);
               }
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), this.transitionStartValue)});
            k2 = new KeyFrame(duration, event -> {
               this.content.setVisible(false);
               if (CACHE_ANIMATION) {
                  this.content.setCache(false);
               }
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), 0, Interpolator.LINEAR)});
         }

         this.timeline.getKeyFrames().setAll(new KeyFrame[]{k1, k2});
         this.timeline.play();
      }
   }

   class TitleRegion extends StackPane {
      private final StackPane arrowRegion;
      private final Tooltip arrowTooltip = new Tooltip(
         ((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable()).isExpanded()
            ? org.cropster.roastingintelligence.util.Messages.m_("Collapse")
            : org.cropster.roastingintelligence.util.Messages.m_("Expand")
      );

      TitleRegion() {
         this.getStyleClass().setAll(new String[]{"title"});
         this.arrowRegion = new StackPane();
         this.arrowRegion.setId("arrowRegion");
         this.arrowRegion.getStyleClass().setAll(new String[]{"arrow-button"});
         StackPane arrow = new StackPane();
         arrow.setId("arrow");
         arrow.getStyleClass().setAll(new String[]{"arrow"});
         this.arrowRegion.getChildren().setAll(new Node[]{arrow});
         Tooltip.install(arrow, this.arrowTooltip);
         ((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable())
            .expandedProperty()
            .addListener(
               (s, o, n) -> this.arrowTooltip
                     .setText(n ? org.cropster.roastingintelligence.util.Messages.m_("Collapse") : org.cropster.roastingintelligence.util.Messages.m_("Expand"))
            );
         arrow.rotateProperty().bind(new DoubleBinding() {
            {
               this.bind(new Observable[]{TitledPaneRightSkin.this.transitionProperty()});
            }

            protected double computeValue() {
               return 180.0 * (1.0 - TitledPaneRightSkin.this.getTransition());
            }
         });
         this.setAlignment(Pos.CENTER_LEFT);
         this.setOnMouseReleased(
            e -> {
               if (e.getButton() == MouseButton.PRIMARY) {
                  ContextMenu contextMenu = ((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable()).getContextMenu();
                  if (contextMenu != null) {
                     contextMenu.hide();
                  }

                  if (((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable()).isCollapsible()
                     && ((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable()).isFocused()) {
                     TitledPaneRightSkin.this.behavior.toggle();
                  }
               }
            }
         );
         this.update();
      }

      private void update() {
         this.getChildren().clear();
         TitledPane titledPane = (TitledPane)TitledPaneRightSkin.this.getSkinnable();
         if (TitledPaneRightSkin.this.graphic != null) {
            TitledPaneRightSkin.this.graphic.layoutBoundsProperty().removeListener(TitledPaneRightSkin.this.graphicPropertyChangedListener);
         }

         TitledPaneRightSkin.this.graphic = titledPane.getGraphic();
         if (TitledPaneRightSkin.this.isIgnoreGraphic()) {
            if (titledPane.getContentDisplay() != ContentDisplay.GRAPHIC_ONLY) {
               this.getChildren().add(TitledPaneRightSkin.this.text);
            }
         } else {
            TitledPaneRightSkin.this.graphic.layoutBoundsProperty().addListener(TitledPaneRightSkin.this.graphicPropertyChangedListener);
            if (TitledPaneRightSkin.this.isIgnoreText()) {
               this.getChildren().add(TitledPaneRightSkin.this.graphic);
            } else {
               this.getChildren().addAll(new Node[]{TitledPaneRightSkin.this.graphic, TitledPaneRightSkin.this.text});
            }
         }

         if (titledPane.isCollapsible()) {
            this.getChildren().add(this.arrowRegion);
         }

         this.setCursor(((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable()).isCollapsible() ? Cursor.HAND : Cursor.DEFAULT);
      }

      protected double computePrefWidth(double height) {
         double left = this.snappedLeftInset();
         double right = this.snappedRightInset();
         double arrowWidth = 0.0;
         double labelPrefWidth = this.labelPrefWidth(height);
         if (this.arrowRegion != null) {
            arrowWidth = this.snapSizeX(this.arrowRegion.prefWidth(height));
         }

         return left + labelPrefWidth + arrowWidth + right;
      }

      protected double computePrefHeight(double width) {
         double top = this.snappedTopInset();
         double bottom = this.snappedBottomInset();
         double arrowHeight = 0.0;
         double labelPrefHeight = this.labelPrefHeight(width);
         if (this.arrowRegion != null) {
            arrowHeight = this.snapSizeY(this.arrowRegion.prefHeight(width));
         }

         return top + Math.max(arrowHeight, labelPrefHeight) + bottom;
      }

      protected void layoutChildren() {
         double top = this.snappedTopInset();
         double bottom = this.snappedBottomInset();
         double left = this.snappedLeftInset();
         double right = this.snappedRightInset();
         double width = this.getWidth() - (left + right);
         double height = this.getHeight() - (top + bottom);
         double arrowWidth = this.snapSizeX(this.arrowRegion.prefWidth(-1.0));
         double arrowHeight = this.snapSizeY(this.arrowRegion.prefHeight(-1.0));
         double labelWidth = this.snapSizeX(width - arrowWidth - ((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable()).getGraphicTextGap());
         double labelHeight = this.snapSizeY(this.labelPrefHeight(-1.0));
         double x = left
            + (
               HPos.CENTER == TitledPaneRightSkin.this.hpos
                  ? Utils.computeXOffset(width, labelWidth, TitledPaneRightSkin.this.hpos)
                  : Utils.computeXOffset(
                     width - arrowWidth - ((TitledPaneRight)TitledPaneRightSkin.this.getSkinnable()).getGraphicTextGap(),
                     labelWidth,
                     TitledPaneRightSkin.this.hpos
                  )
            );
         double y = top + Utils.computeYOffset(height, Math.max(arrowHeight, labelHeight), TitledPaneRightSkin.this.vpos);
         TitledPaneRightSkin.this.layoutLabelInArea(x, y, labelWidth, height, TitledPaneRightSkin.this.pos);
         this.arrowRegion.resize(arrowWidth, arrowHeight);
         this.positionInArea(this.arrowRegion, left + width - arrowWidth, top, arrowWidth, height, 0.0, HPos.CENTER, VPos.CENTER);
      }

      private double labelPrefWidth(double height) {
         Labeled labeled = (Labeled)TitledPaneRightSkin.this.getSkinnable();
         Font font = TitledPaneRightSkin.this.text.getFont();
         String string = labeled.getText();
         boolean emptyText = string == null || string.isEmpty();
         Insets labelPadding = labeled.getLabelPadding();
         double widthPadding = labelPadding.getLeft() + labelPadding.getRight();
         double textWidth = emptyText ? 0.0 : Utils.computeTextWidth(font, string, 0.0);
         Node graphic = labeled.getGraphic();
         if (TitledPaneRightSkin.this.isIgnoreGraphic()) {
            return textWidth + widthPadding;
         } else if (TitledPaneRightSkin.this.isIgnoreText()) {
            return graphic.prefWidth(-1.0) + widthPadding;
         } else {
            return labeled.getContentDisplay() != ContentDisplay.LEFT && labeled.getContentDisplay() != ContentDisplay.RIGHT
               ? Math.max(textWidth, graphic.prefWidth(-1.0)) + widthPadding
               : textWidth + labeled.getGraphicTextGap() + graphic.prefWidth(-1.0) + widthPadding;
         }
      }

      private double labelPrefHeight(double width) {
         Labeled labeled = (Labeled)TitledPaneRightSkin.this.getSkinnable();
         Font font = TitledPaneRightSkin.this.text.getFont();
         ContentDisplay contentDisplay = labeled.getContentDisplay();
         double gap = labeled.getGraphicTextGap();
         Insets labelPadding = labeled.getLabelPadding();
         double widthPadding = this.snappedLeftInset() + this.snappedRightInset() + labelPadding.getLeft() + labelPadding.getRight();
         String str = labeled.getText();
         if (str != null && str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
         }

         if (!TitledPaneRightSkin.this.isIgnoreGraphic() && (contentDisplay == ContentDisplay.LEFT || contentDisplay == ContentDisplay.RIGHT)) {
            width -= TitledPaneRightSkin.this.graphic.prefWidth(-1.0) + gap;
         }

         width -= widthPadding;
         double textHeight = Utils.computeTextHeight(font, str, labeled.isWrapText() ? width : 0.0, TitledPaneRightSkin.this.text.getBoundsType());
         double h = textHeight;
         if (!TitledPaneRightSkin.this.isIgnoreGraphic()) {
            Node graphic = labeled.getGraphic();
            if (contentDisplay != ContentDisplay.TOP && contentDisplay != ContentDisplay.BOTTOM) {
               h = Math.max(textHeight, graphic.prefHeight(-1.0));
            } else {
               h = graphic.prefHeight(-1.0) + gap + textHeight;
            }
         }

         return h + labelPadding.getTop() + labelPadding.getBottom();
      }
   }
}
