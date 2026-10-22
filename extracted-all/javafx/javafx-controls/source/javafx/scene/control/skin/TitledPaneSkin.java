package javafx.scene.control.skin;

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

public class TitledPaneSkin extends LabeledSkinBase<TitledPane> {
   private static final Duration TRANSITION_DURATION = new Duration(350.0);
   private static final boolean CACHE_ANIMATION = PlatformUtil.isEmbedded();
   private final TitledPaneBehavior behavior;
   private final TitledPaneSkin.TitleRegion titleRegion;
   private final StackPane contentContainer;
   private Node content;
   private Timeline timeline;
   private double transitionStartValue;
   private Rectangle clipRect;
   private Pos pos;
   private HPos hpos;
   private VPos vpos;
   private DoubleProperty transition;
   private double prefHeightFromAccordion = 0.0;

   public TitledPaneSkin(TitledPane var1) {
      super(var1);
      this.behavior = new TitledPaneBehavior(var1);
      this.clipRect = new Rectangle();
      this.transitionStartValue = 0.0;
      this.titleRegion = new TitledPaneSkin.TitleRegion();
      this.content = this.getSkinnable().getContent();
      this.contentContainer = new StackPane() {
         {
            this.getStyleClass().setAll(new String[]{"content"});
            if (TitledPaneSkin.this.content != null) {
               this.getChildren().setAll(new Node[]{TitledPaneSkin.this.content});
            }
         }
      };
      this.contentContainer.setClip(this.clipRect);
      this.updateClip();
      if (var1.isExpanded()) {
         this.setTransition(1.0);
         this.setExpanded(var1.isExpanded());
      } else {
         this.setTransition(0.0);
         if (this.content != null) {
            this.content.setVisible(false);
         }
      }

      this.getChildren().setAll(new Node[]{this.contentContainer, this.titleRegion});
      this.registerChangeListener(var1.contentProperty(), var1x -> {
         this.content = this.getSkinnable().getContent();
         if (this.content == null) {
            this.contentContainer.getChildren().clear();
         } else {
            this.contentContainer.getChildren().setAll(new Node[]{this.content});
         }
      });
      this.registerChangeListener(var1.expandedProperty(), var1x -> this.setExpanded(this.getSkinnable().isExpanded()));
      this.registerChangeListener(var1.collapsibleProperty(), var1x -> this.titleRegion.update());
      this.registerChangeListener(var1.alignmentProperty(), var1x -> {
         this.pos = this.getSkinnable().getAlignment();
         this.hpos = this.pos.getHpos();
         this.vpos = this.pos.getVpos();
      });
      this.registerChangeListener(var1.widthProperty(), var1x -> this.updateClip());
      this.registerChangeListener(var1.heightProperty(), var1x -> this.updateClip());
      this.registerChangeListener(this.titleRegion.alignmentProperty(), var1x -> {
         this.pos = this.titleRegion.getAlignment();
         this.hpos = this.pos.getHpos();
         this.vpos = this.pos.getVpos();
      });
      this.pos = var1.getAlignment();
      this.hpos = this.pos == null ? HPos.LEFT : this.pos.getHpos();
      this.vpos = this.pos == null ? VPos.CENTER : this.pos.getVpos();
   }

   private final void setTransition(double var1) {
      this.transitionProperty().set(var1);
   }

   private final double getTransition() {
      return this.transition == null ? 0.0 : this.transition.get();
   }

   private final DoubleProperty transitionProperty() {
      if (this.transition == null) {
         this.transition = new SimpleDoubleProperty(this, "transition", 0.0) {
            protected void invalidated() {
               TitledPaneSkin.this.contentContainer.requestLayout();
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
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      double var9 = this.snapSizeY(this.titleRegion.prefHeight(-1.0));
      this.titleRegion.resize(var5, var9);
      this.positionInArea(this.titleRegion, var1, var3, var5, var9, 0.0, HPos.LEFT, VPos.CENTER);
      this.titleRegion.requestLayout();
      double var11 = (var7 - var9) * this.getTransition();
      if (this.isInsideAccordion() && this.prefHeightFromAccordion != 0.0) {
         var11 = (this.prefHeightFromAccordion - var9) * this.getTransition();
      }

      var11 = this.snapSizeY(var11);
      var3 += var9;
      this.contentContainer.resize(var5, var11);
      this.clipRect.setHeight(var11);
      this.positionInArea(this.contentContainer, var1, var3, var5, var11, 0.0, HPos.CENTER, VPos.CENTER);
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.snapSizeX(this.titleRegion.prefWidth(var1));
      double var13 = this.snapSizeX(this.contentContainer.minWidth(var1));
      return Math.max(var11, var13) + var9 + var5;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.snapSizeY(this.titleRegion.prefHeight(var1));
      double var13 = this.contentContainer.minHeight(var1) * this.getTransition();
      return var11 + this.snapSizeY(var13) + var3 + var7;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.snapSizeX(this.titleRegion.prefWidth(var1));
      double var13 = this.snapSizeX(this.contentContainer.prefWidth(var1));
      return Math.max(var11, var13) + var9 + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = this.snapSizeY(this.titleRegion.prefHeight(var1));
      double var13 = this.contentContainer.prefHeight(var1) * this.getTransition();
      return var11 + this.snapSizeY(var13) + var3 + var7;
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      return Double.MAX_VALUE;
   }

   private void updateClip() {
      this.clipRect.setWidth(this.getSkinnable().getWidth());
      this.clipRect.setHeight(this.contentContainer.getHeight());
   }

   private void setExpanded(boolean var1) {
      if (!this.getSkinnable().isCollapsible()) {
         this.setTransition(1.0);
      } else {
         if (this.getSkinnable().isAnimated()) {
            this.transitionStartValue = this.getTransition();
            this.doAnimationTransition();
         } else {
            if (var1) {
               this.setTransition(1.0);
            } else {
               this.setTransition(0.0);
            }

            if (this.content != null) {
               this.content.setVisible(var1);
            }

            this.getSkinnable().requestLayout();
         }
      }
   }

   private boolean isInsideAccordion() {
      return this.getSkinnable().getParent() != null && this.getSkinnable().getParent() instanceof Accordion;
   }

   double getTitleRegionSize(double var1) {
      return this.snapSizeY(this.titleRegion.prefHeight(var1)) + this.snappedTopInset() + this.snappedBottomInset();
   }

   void setMaxTitledPaneHeightForAccordion(double var1) {
      this.prefHeightFromAccordion = var1;
   }

   double getTitledPaneHeightForAccordion() {
      double var1 = this.snapSizeY(this.titleRegion.prefHeight(-1.0));
      double var3 = (this.prefHeightFromAccordion - var1) * this.getTransition();
      return var1 + this.snapSizeY(var3) + this.snappedTopInset() + this.snappedBottomInset();
   }

   private void doAnimationTransition() {
      if (this.content != null) {
         Duration var1;
         if (this.timeline != null && this.timeline.getStatus() != Status.STOPPED) {
            var1 = this.timeline.getCurrentTime();
            this.timeline.stop();
         } else {
            var1 = TRANSITION_DURATION;
         }

         this.timeline = new Timeline();
         this.timeline.setCycleCount(1);
         KeyFrame var2;
         KeyFrame var3;
         if (this.getSkinnable().isExpanded()) {
            var2 = new KeyFrame(Duration.ZERO, var1x -> {
               if (CACHE_ANIMATION) {
                  this.content.setCache(true);
               }

               this.content.setVisible(true);
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), this.transitionStartValue)});
            var3 = new KeyFrame(var1, var1x -> {
               if (CACHE_ANIMATION) {
                  this.content.setCache(false);
               }
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), 1, Interpolator.LINEAR)});
         } else {
            var2 = new KeyFrame(Duration.ZERO, var1x -> {
               if (CACHE_ANIMATION) {
                  this.content.setCache(true);
               }
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), this.transitionStartValue)});
            var3 = new KeyFrame(var1, var1x -> {
               this.content.setVisible(false);
               if (CACHE_ANIMATION) {
                  this.content.setCache(false);
               }
            }, new KeyValue[]{new KeyValue(this.transitionProperty(), 0, Interpolator.LINEAR)});
         }

         this.timeline.getKeyFrames().setAll(new KeyFrame[]{var2, var3});
         this.timeline.play();
      }
   }

   class TitleRegion extends StackPane {
      private final StackPane arrowRegion;

      public TitleRegion() {
         this.getStyleClass().setAll(new String[]{"title"});
         this.arrowRegion = new StackPane();
         this.arrowRegion.setId("arrowRegion");
         this.arrowRegion.getStyleClass().setAll(new String[]{"arrow-button"});
         StackPane var2 = new StackPane();
         var2.setId("arrow");
         var2.getStyleClass().setAll(new String[]{"arrow"});
         this.arrowRegion.getChildren().setAll(new Node[]{var2});
         var2.rotateProperty().bind(new DoubleBinding() {
            {
               this.bind(new Observable[]{TitledPaneSkin.this.transitionProperty()});
            }

            protected double computeValue() {
               return -90.0 * (1.0 - TitledPaneSkin.this.getTransition());
            }
         });
         this.setAlignment(Pos.CENTER_LEFT);
         this.setOnMouseReleased(var1x -> {
            if (var1x.getButton() == MouseButton.PRIMARY) {
               ContextMenu var2x = TitledPaneSkin.this.getSkinnable().getContextMenu();
               if (var2x != null) {
                  var2x.hide();
               }

               if (TitledPaneSkin.this.getSkinnable().isCollapsible() && TitledPaneSkin.this.getSkinnable().isFocused()) {
                  TitledPaneSkin.this.behavior.toggle();
               }
            }
         });
         this.update();
      }

      private void update() {
         this.getChildren().clear();
         TitledPane var1 = TitledPaneSkin.this.getSkinnable();
         if (var1.isCollapsible()) {
            this.getChildren().add(this.arrowRegion);
         }

         if (TitledPaneSkin.this.graphic != null) {
            TitledPaneSkin.this.graphic.layoutBoundsProperty().removeListener(TitledPaneSkin.this.graphicPropertyChangedListener);
         }

         TitledPaneSkin.this.graphic = var1.getGraphic();
         if (TitledPaneSkin.this.isIgnoreGraphic()) {
            if (var1.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) {
               this.getChildren().clear();
               this.getChildren().add(this.arrowRegion);
            } else {
               this.getChildren().add(TitledPaneSkin.this.text);
            }
         } else {
            TitledPaneSkin.this.graphic.layoutBoundsProperty().addListener(TitledPaneSkin.this.graphicPropertyChangedListener);
            if (TitledPaneSkin.this.isIgnoreText()) {
               this.getChildren().add(TitledPaneSkin.this.graphic);
            } else {
               this.getChildren().addAll(new Node[]{TitledPaneSkin.this.graphic, TitledPaneSkin.this.text});
            }
         }

         this.setCursor(TitledPaneSkin.this.getSkinnable().isCollapsible() ? Cursor.HAND : Cursor.DEFAULT);
      }

      protected double computePrefWidth(double var1) {
         double var3 = this.snappedLeftInset();
         double var5 = this.snappedRightInset();
         double var7 = 0.0;
         double var9 = this.labelPrefWidth(var1);
         if (this.arrowRegion != null) {
            var7 = this.snapSizeX(this.arrowRegion.prefWidth(var1));
         }

         return var3 + var7 + var9 + var5;
      }

      protected double computePrefHeight(double var1) {
         double var3 = this.snappedTopInset();
         double var5 = this.snappedBottomInset();
         double var7 = 0.0;
         double var9 = this.labelPrefHeight(var1);
         if (this.arrowRegion != null) {
            var7 = this.snapSizeY(this.arrowRegion.prefHeight(var1));
         }

         return var3 + Math.max(var7, var9) + var5;
      }

      protected void layoutChildren() {
         double var1 = this.snappedTopInset();
         double var3 = this.snappedBottomInset();
         double var5 = this.snappedLeftInset();
         double var7 = this.snappedRightInset();
         double var9 = this.getWidth() - (var5 + var7);
         double var11 = this.getHeight() - (var1 + var3);
         double var13 = this.snapSizeX(this.arrowRegion.prefWidth(-1.0));
         double var15 = this.snapSizeY(this.arrowRegion.prefHeight(-1.0));
         double var17 = this.snapSizeX(Math.min(var9 - var13 / 2.0, this.labelPrefWidth(-1.0)));
         double var19 = this.snapSizeY(this.labelPrefHeight(-1.0));
         double var21 = var5 + var13 + Utils.computeXOffset(var9 - var13, var17, TitledPaneSkin.this.hpos);
         if (HPos.CENTER == TitledPaneSkin.this.hpos) {
            var21 = var5 + Utils.computeXOffset(var9, var17, TitledPaneSkin.this.hpos);
         }

         double var23 = var1 + Utils.computeYOffset(var11, Math.max(var15, var19), TitledPaneSkin.this.vpos);
         this.arrowRegion.resize(var13, var15);
         this.positionInArea(this.arrowRegion, var5, var1, var13, var11, 0.0, HPos.CENTER, VPos.CENTER);
         TitledPaneSkin.this.layoutLabelInArea(var21, var23, var17, var11, TitledPaneSkin.this.pos);
      }

      private double labelPrefWidth(double var1) {
         Labeled var3 = TitledPaneSkin.this.getSkinnable();
         Font var4 = TitledPaneSkin.this.text.getFont();
         String var5 = var3.getText();
         boolean var6 = var5 == null || var5.isEmpty();
         Insets var7 = var3.getLabelPadding();
         double var8 = var7.getLeft() + var7.getRight();
         double var10 = var6 ? 0.0 : Utils.computeTextWidth(var4, var5, 0.0);
         Node var12 = var3.getGraphic();
         if (TitledPaneSkin.this.isIgnoreGraphic()) {
            return var10 + var8;
         } else if (TitledPaneSkin.this.isIgnoreText()) {
            return var12.prefWidth(-1.0) + var8;
         } else {
            return var3.getContentDisplay() != ContentDisplay.LEFT && var3.getContentDisplay() != ContentDisplay.RIGHT
               ? Math.max(var10, var12.prefWidth(-1.0)) + var8
               : var10 + var3.getGraphicTextGap() + var12.prefWidth(-1.0) + var8;
         }
      }

      private double labelPrefHeight(double var1) {
         Labeled var3 = TitledPaneSkin.this.getSkinnable();
         Font var4 = TitledPaneSkin.this.text.getFont();
         ContentDisplay var5 = var3.getContentDisplay();
         double var6 = var3.getGraphicTextGap();
         Insets var8 = var3.getLabelPadding();
         double var9 = this.snappedLeftInset() + this.snappedRightInset() + var8.getLeft() + var8.getRight();
         String var11 = var3.getText();
         if (var11 != null && var11.endsWith("\n")) {
            var11 = var11.substring(0, var11.length() - 1);
         }

         if (!TitledPaneSkin.this.isIgnoreGraphic() && (var5 == ContentDisplay.LEFT || var5 == ContentDisplay.RIGHT)) {
            var1 -= TitledPaneSkin.this.graphic.prefWidth(-1.0) + var6;
         }

         var1 -= var9;
         double var12 = Utils.computeTextHeight(var4, var11, var3.isWrapText() ? var1 : 0.0, TitledPaneSkin.this.text.getBoundsType());
         double var14 = var12;
         if (!TitledPaneSkin.this.isIgnoreGraphic()) {
            Node var16 = var3.getGraphic();
            if (var5 != ContentDisplay.TOP && var5 != ContentDisplay.BOTTOM) {
               var14 = Math.max(var12, var16.prefHeight(-1.0));
            } else {
               var14 = var16.prefHeight(-1.0) + var6 + var12;
            }
         }

         return var14 + var8.getTop() + var8.getBottom();
      }
   }
}
