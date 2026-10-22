package javafx.scene.control.skin;

import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ScrollPaneBehavior;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.util.Utils;
import java.util.function.Consumer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ScrollPaneSkin extends SkinBase<ScrollPane> {
   private static final double DEFAULT_PREF_SIZE = 100.0;
   private static final double DEFAULT_MIN_SIZE = 36.0;
   private static final double DEFAULT_SB_BREADTH = 12.0;
   private static final double DEFAULT_EMBEDDED_SB_BREADTH = 8.0;
   private static final double PAN_THRESHOLD = 0.5;
   private Node scrollNode;
   private final BehaviorBase<ScrollPane> behavior;
   private double nodeWidth;
   private double nodeHeight;
   private boolean nodeSizeInvalid = true;
   private double posX;
   private double posY;
   private boolean hsbvis;
   private boolean vsbvis;
   private double hsbHeight;
   private double vsbWidth;
   private StackPane viewRect;
   private StackPane viewContent;
   private double contentWidth;
   private double contentHeight;
   private StackPane corner;
   ScrollBar hsb;
   ScrollBar vsb;
   double pressX;
   double pressY;
   double ohvalue;
   double ovvalue;
   private Cursor saveCursor = null;
   private boolean dragDetected = false;
   private boolean touchDetected = false;
   private boolean mouseDown = false;
   Rectangle clipRect;
   Timeline sbTouchTimeline;
   KeyFrame sbTouchKF1;
   KeyFrame sbTouchKF2;
   Timeline contentsToViewTimeline;
   KeyFrame contentsToViewKF1;
   KeyFrame contentsToViewKF2;
   KeyFrame contentsToViewKF3;
   private boolean tempVisibility;
   private final InvalidationListener nodeListener = new InvalidationListener() {
      public void invalidated(Observable var1) {
         if (!ScrollPaneSkin.this.nodeSizeInvalid) {
            Bounds var2 = ScrollPaneSkin.this.scrollNode.getLayoutBounds();
            double var3 = var2.getWidth();
            double var5 = var2.getHeight();
            if (ScrollPaneSkin.this.vsbvis == ScrollPaneSkin.this.determineVerticalSBVisible()
               && ScrollPaneSkin.this.hsbvis == ScrollPaneSkin.this.determineHorizontalSBVisible()
               && (var3 == 0.0 || ScrollPaneSkin.this.nodeWidth == var3)
               && (var5 == 0.0 || ScrollPaneSkin.this.nodeHeight == var5)) {
               if (!ScrollPaneSkin.this.dragDetected) {
                  ScrollPaneSkin.this.updateVerticalSB();
                  ScrollPaneSkin.this.updateHorizontalSB();
               }
            } else {
               ScrollPaneSkin.this.getSkinnable().requestLayout();
            }
         }
      }
   };
   private final ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {
      public void changed(ObservableValue<? extends Bounds> var1, Bounds var2, Bounds var3) {
         double var4 = var2.getHeight();
         double var6 = var3.getHeight();
         if (var4 > 0.0 && var4 != var6) {
            double var8 = ScrollPaneSkin.this.snapPositionY(
               ScrollPaneSkin.this.snappedTopInset()
                  - ScrollPaneSkin.this.posY
                     / (ScrollPaneSkin.this.vsb.getMax() - ScrollPaneSkin.this.vsb.getMin())
                     * (var4 - ScrollPaneSkin.this.contentHeight)
            );
            double var10 = ScrollPaneSkin.this.snapPositionY(
               ScrollPaneSkin.this.snappedTopInset()
                  - ScrollPaneSkin.this.posY
                     / (ScrollPaneSkin.this.vsb.getMax() - ScrollPaneSkin.this.vsb.getMin())
                     * (var6 - ScrollPaneSkin.this.contentHeight)
            );
            double var12 = var8 / var10 * ScrollPaneSkin.this.vsb.getValue();
            if (var12 < 0.0) {
               ScrollPaneSkin.this.vsb.setValue(0.0);
            } else if (var12 < 1.0) {
               ScrollPaneSkin.this.vsb.setValue(var12);
            } else if (var12 > 1.0) {
               ScrollPaneSkin.this.vsb.setValue(1.0);
            }
         }

         double var18 = var2.getWidth();
         double var19 = var3.getWidth();
         if (var18 > 0.0 && var18 != var19) {
            double var20 = ScrollPaneSkin.this.snapPositionX(
               ScrollPaneSkin.this.snappedLeftInset()
                  - ScrollPaneSkin.this.posX
                     / (ScrollPaneSkin.this.hsb.getMax() - ScrollPaneSkin.this.hsb.getMin())
                     * (var18 - ScrollPaneSkin.this.contentWidth)
            );
            double var14 = ScrollPaneSkin.this.snapPositionX(
               ScrollPaneSkin.this.snappedLeftInset()
                  - ScrollPaneSkin.this.posX
                     / (ScrollPaneSkin.this.hsb.getMax() - ScrollPaneSkin.this.hsb.getMin())
                     * (var19 - ScrollPaneSkin.this.contentWidth)
            );
            double var16 = var20 / var14 * ScrollPaneSkin.this.hsb.getValue();
            if (var16 < 0.0) {
               ScrollPaneSkin.this.hsb.setValue(0.0);
            } else if (var16 < 1.0) {
               ScrollPaneSkin.this.hsb.setValue(var16);
            } else if (var16 > 1.0) {
               ScrollPaneSkin.this.hsb.setValue(1.0);
            }
         }
      }
   };
   private DoubleProperty contentPosX;
   private DoubleProperty contentPosY;

   public ScrollPaneSkin(ScrollPane var1) {
      super(var1);
      this.behavior = new ScrollPaneBehavior(var1);
      this.initialize();
      Consumer var2 = var1x -> this.getSkinnable().requestLayout();
      this.registerChangeListener(var1.contentProperty(), var1x -> {
         if (this.scrollNode != this.getSkinnable().getContent()) {
            if (this.scrollNode != null) {
               this.scrollNode.layoutBoundsProperty().removeListener(this.nodeListener);
               this.scrollNode.layoutBoundsProperty().removeListener(this.boundsChangeListener);
               this.viewContent.getChildren().remove(this.scrollNode);
            }

            this.scrollNode = this.getSkinnable().getContent();
            if (this.scrollNode != null) {
               this.nodeWidth = this.snapSizeX(this.scrollNode.getLayoutBounds().getWidth());
               this.nodeHeight = this.snapSizeY(this.scrollNode.getLayoutBounds().getHeight());
               this.viewContent.getChildren().setAll(new Node[]{this.scrollNode});
               this.scrollNode.layoutBoundsProperty().addListener(this.nodeListener);
               this.scrollNode.layoutBoundsProperty().addListener(this.boundsChangeListener);
            }
         }

         this.getSkinnable().requestLayout();
      });
      this.registerChangeListener(var1.fitToWidthProperty(), var1x -> {
         this.getSkinnable().requestLayout();
         this.viewRect.requestLayout();
      });
      this.registerChangeListener(var1.fitToHeightProperty(), var1x -> {
         this.getSkinnable().requestLayout();
         this.viewRect.requestLayout();
      });
      this.registerChangeListener(var1.hbarPolicyProperty(), var1x -> this.getSkinnable().requestLayout());
      this.registerChangeListener(var1.vbarPolicyProperty(), var1x -> this.getSkinnable().requestLayout());
      this.registerChangeListener(var1.hvalueProperty(), var1x -> this.hsb.setValue(this.getSkinnable().getHvalue()));
      this.registerChangeListener(var1.hmaxProperty(), var1x -> this.hsb.setMax(this.getSkinnable().getHmax()));
      this.registerChangeListener(var1.hminProperty(), var1x -> this.hsb.setMin(this.getSkinnable().getHmin()));
      this.registerChangeListener(var1.vvalueProperty(), var1x -> this.vsb.setValue(this.getSkinnable().getVvalue()));
      this.registerChangeListener(var1.vmaxProperty(), var1x -> this.vsb.setMax(this.getSkinnable().getVmax()));
      this.registerChangeListener(var1.vminProperty(), var1x -> this.vsb.setMin(this.getSkinnable().getVmin()));
      this.registerChangeListener(var1.prefViewportWidthProperty(), var2);
      this.registerChangeListener(var1.prefViewportHeightProperty(), var2);
      this.registerChangeListener(var1.minViewportWidthProperty(), var2);
      this.registerChangeListener(var1.minViewportHeightProperty(), var2);
   }

   private final void setContentPosX(double var1) {
      this.contentPosXProperty().set(var1);
   }

   private final double getContentPosX() {
      return this.contentPosX == null ? 0.0 : this.contentPosX.get();
   }

   private final DoubleProperty contentPosXProperty() {
      if (this.contentPosX == null) {
         this.contentPosX = new DoublePropertyBase() {
            protected void invalidated() {
               ScrollPaneSkin.this.hsb.setValue(ScrollPaneSkin.this.getContentPosX());
               ScrollPaneSkin.this.getSkinnable().requestLayout();
            }

            public Object getBean() {
               return ScrollPaneSkin.this;
            }

            public String getName() {
               return "contentPosX";
            }
         };
      }

      return this.contentPosX;
   }

   private final void setContentPosY(double var1) {
      this.contentPosYProperty().set(var1);
   }

   private final double getContentPosY() {
      return this.contentPosY == null ? 0.0 : this.contentPosY.get();
   }

   private final DoubleProperty contentPosYProperty() {
      if (this.contentPosY == null) {
         this.contentPosY = new DoublePropertyBase() {
            protected void invalidated() {
               ScrollPaneSkin.this.vsb.setValue(ScrollPaneSkin.this.getContentPosY());
               ScrollPaneSkin.this.getSkinnable().requestLayout();
            }

            public Object getBean() {
               return ScrollPaneSkin.this;
            }

            public String getName() {
               return "contentPosY";
            }
         };
      }

      return this.contentPosY;
   }

   @Override
   public void dispose() {
      super.dispose();
      if (this.behavior != null) {
         this.behavior.dispose();
      }
   }

   public final ScrollBar getHorizontalScrollBar() {
      return this.hsb;
   }

   public final ScrollBar getVerticalScrollBar() {
      return this.vsb;
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      ScrollPane var11 = this.getSkinnable();
      double var12 = this.computeVsbSizeHint(var11);
      double var14 = var12 + this.snappedLeftInset() + this.snappedRightInset();
      if (var11.getPrefViewportWidth() > 0.0) {
         return var11.getPrefViewportWidth() + var14;
      } else {
         return var11.getContent() != null ? var11.getContent().prefWidth(var1) + var14 : Math.max(var14, 100.0);
      }
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      ScrollPane var11 = this.getSkinnable();
      double var12 = this.computeHsbSizeHint(var11);
      double var14 = var12 + this.snappedTopInset() + this.snappedBottomInset();
      if (var11.getPrefViewportHeight() > 0.0) {
         return var11.getPrefViewportHeight() + var14;
      } else {
         return var11.getContent() != null ? var11.getContent().prefHeight(var1) + var14 : Math.max(var14, 100.0);
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      ScrollPane var11 = this.getSkinnable();
      double var12 = this.computeVsbSizeHint(var11);
      double var14 = var12 + this.snappedLeftInset() + this.snappedRightInset();
      if (var11.getMinViewportWidth() > 0.0) {
         return var11.getMinViewportWidth() + var14;
      } else {
         double var16 = this.corner.minWidth(-1.0);
         return var16 > 0.0 ? 3.0 * var16 : 36.0;
      }
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      ScrollPane var11 = this.getSkinnable();
      double var12 = this.computeHsbSizeHint(var11);
      double var14 = var12 + this.snappedTopInset() + this.snappedBottomInset();
      if (var11.getMinViewportHeight() > 0.0) {
         return var11.getMinViewportHeight() + var14;
      } else {
         double var16 = this.corner.minHeight(-1.0);
         return var16 > 0.0 ? 3.0 * var16 : 36.0;
      }
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      ScrollPane var9 = this.getSkinnable();
      Insets var10 = var9.getPadding();
      double var11 = this.snapSizeX(var10.getRight());
      double var13 = this.snapSizeX(var10.getLeft());
      double var15 = this.snapSizeY(var10.getTop());
      double var17 = this.snapSizeY(var10.getBottom());
      this.vsb.setMin(var9.getVmin());
      this.vsb.setMax(var9.getVmax());
      this.hsb.setMin(var9.getHmin());
      this.hsb.setMax(var9.getHmax());
      this.contentWidth = var5;
      this.contentHeight = var7;
      double var19 = 0.0;
      double var21 = 0.0;
      this.computeScrollNodeSize(this.contentWidth, this.contentHeight);
      this.computeScrollBarSize();

      for (int var23 = 0; var23 < 2; var23++) {
         this.vsbvis = this.determineVerticalSBVisible();
         this.hsbvis = this.determineHorizontalSBVisible();
         if (this.vsbvis && !Properties.IS_TOUCH_SUPPORTED) {
            this.contentWidth = var5 - this.vsbWidth;
         }

         var19 = var5 + var13 + var11 - (this.vsbvis ? this.vsbWidth : 0.0);
         if (this.hsbvis && !Properties.IS_TOUCH_SUPPORTED) {
            this.contentHeight = var7 - this.hsbHeight;
         }

         var21 = var7 + var15 + var17 - (this.hsbvis ? this.hsbHeight : 0.0);
      }

      if (this.scrollNode != null && this.scrollNode.isResizable()) {
         if (this.vsbvis && this.hsbvis) {
            this.computeScrollNodeSize(this.contentWidth, this.contentHeight);
         } else if (this.hsbvis && !this.vsbvis) {
            this.computeScrollNodeSize(this.contentWidth, this.contentHeight);
            this.vsbvis = this.determineVerticalSBVisible();
            if (this.vsbvis) {
               this.contentWidth = this.contentWidth - this.vsbWidth;
               var19 -= this.vsbWidth;
               this.computeScrollNodeSize(this.contentWidth, this.contentHeight);
            }
         } else if (this.vsbvis && !this.hsbvis) {
            this.computeScrollNodeSize(this.contentWidth, this.contentHeight);
            this.hsbvis = this.determineHorizontalSBVisible();
            if (this.hsbvis) {
               this.contentHeight = this.contentHeight - this.hsbHeight;
               var21 -= this.hsbHeight;
               this.computeScrollNodeSize(this.contentWidth, this.contentHeight);
            }
         }
      }

      double var31 = this.snappedLeftInset() - var13;
      double var25 = this.snappedTopInset() - var15;
      this.vsb.setVisible(this.vsbvis);
      if (this.vsbvis) {
         this.vsb.resizeRelocate(this.snappedLeftInset() + var5 - this.vsbWidth + (var11 < 1.0 ? 0.0 : var11 - 1.0), var25, this.vsbWidth, var21);
      }

      this.updateVerticalSB();
      this.hsb.setVisible(this.hsbvis);
      if (this.hsbvis) {
         this.hsb.resizeRelocate(var31, this.snappedTopInset() + var7 - this.hsbHeight + (var17 < 1.0 ? 0.0 : var17 - 1.0), var19, this.hsbHeight);
      }

      this.updateHorizontalSB();
      this.viewRect.resizeRelocate(this.snappedLeftInset(), this.snappedTopInset(), this.snapSizeX(this.contentWidth), this.snapSizeY(this.contentHeight));
      this.resetClip();
      if (this.vsbvis && this.hsbvis) {
         this.corner.setVisible(true);
         double var27 = this.vsbWidth;
         double var29 = this.hsbHeight;
         this.corner
            .resizeRelocate(this.snapPositionX(this.vsb.getLayoutX()), this.snapPositionY(this.hsb.getLayoutY()), this.snapSizeX(var27), this.snapSizeY(var29));
      } else {
         this.corner.setVisible(false);
      }

      var9.setViewportBounds(
         new BoundingBox(
            this.snapPositionX(this.viewContent.getLayoutX()),
            this.snapPositionY(this.viewContent.getLayoutY()),
            this.snapSizeX(this.contentWidth),
            this.snapSizeY(this.contentHeight)
         )
      );
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case VERTICAL_SCROLLBAR:
            return this.vsb;
         case HORIZONTAL_SCROLLBAR:
            return this.hsb;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   private void initialize() {
      ScrollPane var1 = this.getSkinnable();
      this.scrollNode = var1.getContent();
      ParentTraversalEngine var2 = new ParentTraversalEngine(this.getSkinnable());
      var2.addTraverseListener((var1x, var2x) -> this.scrollBoundsIntoView(var2x));
      ParentHelper.setTraversalEngine(this.getSkinnable(), var2);
      if (this.scrollNode != null) {
         this.scrollNode.layoutBoundsProperty().addListener(this.nodeListener);
         this.scrollNode.layoutBoundsProperty().addListener(this.boundsChangeListener);
      }

      this.viewRect = new StackPane() {
         protected void layoutChildren() {
            ScrollPaneSkin.this.viewContent.resize(this.getWidth(), this.getHeight());
         }
      };
      this.viewRect.setManaged(false);
      this.viewRect.setCache(true);
      this.viewRect.getStyleClass().add("viewport");
      this.clipRect = new Rectangle();
      this.viewRect.setClip(this.clipRect);
      this.hsb = new ScrollBar();
      this.vsb = new ScrollBar();
      this.vsb.setOrientation(Orientation.VERTICAL);
      EventHandler var3 = var1x -> {
         if (this.getSkinnable().isFocusTraversable()) {
            this.getSkinnable().requestFocus();
         }
      };
      this.hsb.addEventFilter(MouseEvent.MOUSE_PRESSED, var3);
      this.vsb.addEventFilter(MouseEvent.MOUSE_PRESSED, var3);
      this.corner = new StackPane();
      this.corner.getStyleClass().setAll(new String[]{"corner"});
      this.viewContent = new StackPane() {
         public void requestLayout() {
            ScrollPaneSkin.this.nodeSizeInvalid = true;
            super.requestLayout();
            ScrollPaneSkin.this.getSkinnable().requestLayout();
         }

         protected void layoutChildren() {
            if (ScrollPaneSkin.this.nodeSizeInvalid) {
               ScrollPaneSkin.this.computeScrollNodeSize(this.getWidth(), this.getHeight());
            }

            if (ScrollPaneSkin.this.scrollNode != null && ScrollPaneSkin.this.scrollNode.isResizable()) {
               ScrollPaneSkin.this.scrollNode.resize(this.snapSizeX(ScrollPaneSkin.this.nodeWidth), this.snapSizeY(ScrollPaneSkin.this.nodeHeight));
               if (ScrollPaneSkin.this.vsbvis != ScrollPaneSkin.this.determineVerticalSBVisible()
                  || ScrollPaneSkin.this.hsbvis != ScrollPaneSkin.this.determineHorizontalSBVisible()) {
                  ScrollPaneSkin.this.getSkinnable().requestLayout();
               }
            }

            if (ScrollPaneSkin.this.scrollNode != null) {
               ScrollPaneSkin.this.scrollNode.relocate(0.0, 0.0);
            }
         }
      };
      this.viewRect.getChildren().add(this.viewContent);
      if (this.scrollNode != null) {
         this.viewContent.getChildren().add(this.scrollNode);
         this.viewRect.nodeOrientationProperty().bind(this.scrollNode.nodeOrientationProperty());
      }

      this.getChildren().clear();
      this.getChildren().addAll(new Node[]{this.viewRect, this.vsb, this.hsb, this.corner});
      InvalidationListener var4 = var1x -> {
         if (!Properties.IS_TOUCH_SUPPORTED) {
            this.posY = Utils.clamp(this.getSkinnable().getVmin(), this.vsb.getValue(), this.getSkinnable().getVmax());
         } else {
            this.posY = this.vsb.getValue();
         }

         this.updatePosY();
      };
      this.vsb.valueProperty().addListener(var4);
      InvalidationListener var5 = var1x -> {
         if (!Properties.IS_TOUCH_SUPPORTED) {
            this.posX = Utils.clamp(this.getSkinnable().getHmin(), this.hsb.getValue(), this.getSkinnable().getHmax());
         } else {
            this.posX = this.hsb.getValue();
         }

         this.updatePosX();
      };
      this.hsb.valueProperty().addListener(var5);
      this.viewRect.setOnMousePressed(var1x -> {
         this.mouseDown = true;
         if (Properties.IS_TOUCH_SUPPORTED) {
            this.startSBReleasedAnimation();
         }

         this.pressX = var1x.getX();
         this.pressY = var1x.getY();
         this.ohvalue = this.hsb.getValue();
         this.ovvalue = this.vsb.getValue();
      });
      this.viewRect.setOnDragDetected(var1x -> {
         if (Properties.IS_TOUCH_SUPPORTED) {
            this.startSBReleasedAnimation();
         }

         if (this.getSkinnable().isPannable()) {
            this.dragDetected = true;
            if (this.saveCursor == null) {
               this.saveCursor = this.getSkinnable().getCursor();
               if (this.saveCursor == null) {
                  this.saveCursor = Cursor.DEFAULT;
               }

               this.getSkinnable().setCursor(Cursor.MOVE);
               this.getSkinnable().requestLayout();
            }
         }
      });
      this.viewRect
         .addEventFilter(
            MouseEvent.MOUSE_RELEASED,
            var1x -> {
               this.mouseDown = false;
               if (this.dragDetected) {
                  if (this.saveCursor != null) {
                     this.getSkinnable().setCursor(this.saveCursor);
                     this.saveCursor = null;
                     this.getSkinnable().requestLayout();
                  }

                  this.dragDetected = false;
               }

               if ((
                     this.posY > this.getSkinnable().getVmax()
                        || this.posY < this.getSkinnable().getVmin()
                        || this.posX > this.getSkinnable().getHmax()
                        || this.posX < this.getSkinnable().getHmin()
                  )
                  && !this.touchDetected) {
                  this.startContentsToViewport();
               }
            }
         );
      this.viewRect.setOnMouseDragged(var1x -> {
         if (Properties.IS_TOUCH_SUPPORTED) {
            this.startSBReleasedAnimation();
         }

         if (this.getSkinnable().isPannable() || Properties.IS_TOUCH_SUPPORTED) {
            double var2x = this.pressX - var1x.getX();
            double var4x = this.pressY - var1x.getY();
            if (this.hsb.getVisibleAmount() > 0.0 && this.hsb.getVisibleAmount() < this.hsb.getMax() && Math.abs(var2x) > 0.5) {
               if (this.isReverseNodeOrientation()) {
                  var2x = -var2x;
               }

               double var6x = this.ohvalue + var2x / (this.nodeWidth - this.viewRect.getWidth()) * (this.hsb.getMax() - this.hsb.getMin());
               if (!Properties.IS_TOUCH_SUPPORTED) {
                  if (var6x > this.hsb.getMax()) {
                     var6x = this.hsb.getMax();
                  } else if (var6x < this.hsb.getMin()) {
                     var6x = this.hsb.getMin();
                  }

                  this.hsb.setValue(var6x);
               } else {
                  this.hsb.setValue(var6x);
               }
            }

            if (this.vsb.getVisibleAmount() > 0.0 && this.vsb.getVisibleAmount() < this.vsb.getMax() && Math.abs(var4x) > 0.5) {
               double var8x = this.ovvalue + var4x / (this.nodeHeight - this.viewRect.getHeight()) * (this.vsb.getMax() - this.vsb.getMin());
               if (!Properties.IS_TOUCH_SUPPORTED) {
                  if (var8x > this.vsb.getMax()) {
                     var8x = this.vsb.getMax();
                  } else if (var8x < this.vsb.getMin()) {
                     var8x = this.vsb.getMin();
                  }

                  this.vsb.setValue(var8x);
               } else {
                  this.vsb.setValue(var8x);
               }
            }
         }

         var1x.consume();
      });
      EventDispatcher var6 = (var0, var1x) -> var0;
      EventDispatcher var7 = this.hsb.getEventDispatcher();
      this.hsb.setEventDispatcher((var2x, var3x) -> {
         if (var2x.getEventType() == ScrollEvent.SCROLL && !((ScrollEvent)var2x).isDirect()) {
            var3x = var3x.prepend(var6);
            var3x = var3x.prepend(var7);
            return var3x.dispatchEvent(var2x);
         } else {
            return var7.dispatchEvent(var2x, var3x);
         }
      });
      EventDispatcher var8 = this.vsb.getEventDispatcher();
      this.vsb.setEventDispatcher((var2x, var3x) -> {
         if (var2x.getEventType() == ScrollEvent.SCROLL && !((ScrollEvent)var2x).isDirect()) {
            var3x = var3x.prepend(var6);
            var3x = var3x.prepend(var8);
            return var3x.dispatchEvent(var2x);
         } else {
            return var8.dispatchEvent(var2x, var3x);
         }
      });
      this.viewRect
         .addEventHandler(
            ScrollEvent.SCROLL,
            var1x -> {
               if (Properties.IS_TOUCH_SUPPORTED) {
                  this.startSBReleasedAnimation();
               }

               if (this.vsb.getVisibleAmount() < this.vsb.getMax()) {
                  double var2x = this.getSkinnable().getVmax() - this.getSkinnable().getVmin();
                  double var4x;
                  if (this.nodeHeight > 0.0) {
                     var4x = var2x / this.nodeHeight;
                  } else {
                     var4x = 0.0;
                  }

                  double var6x = this.vsb.getValue() + -var1x.getDeltaY() * var4x;
                  if (!Properties.IS_TOUCH_SUPPORTED) {
                     if (var1x.getDeltaY() > 0.0 && this.vsb.getValue() > this.vsb.getMin()
                        || var1x.getDeltaY() < 0.0 && this.vsb.getValue() < this.vsb.getMax()) {
                        this.vsb.setValue(var6x);
                        var1x.consume();
                     }
                  } else if (!var1x.isInertia()
                     || var1x.isInertia() && (this.contentsToViewTimeline == null || this.contentsToViewTimeline.getStatus() == Status.STOPPED)) {
                     this.vsb.setValue(var6x);
                     if ((var6x > this.vsb.getMax() || var6x < this.vsb.getMin()) && !this.mouseDown && !this.touchDetected) {
                        this.startContentsToViewport();
                     }

                     var1x.consume();
                  }
               }

               if (this.hsb.getVisibleAmount() < this.hsb.getMax()) {
                  double var8x = this.getSkinnable().getHmax() - this.getSkinnable().getHmin();
                  double var9;
                  if (this.nodeWidth > 0.0) {
                     var9 = var8x / this.nodeWidth;
                  } else {
                     var9 = 0.0;
                  }

                  double var10 = this.hsb.getValue() + -var1x.getDeltaX() * var9;
                  if (!Properties.IS_TOUCH_SUPPORTED) {
                     if (var1x.getDeltaX() > 0.0 && this.hsb.getValue() > this.hsb.getMin()
                        || var1x.getDeltaX() < 0.0 && this.hsb.getValue() < this.hsb.getMax()) {
                        this.hsb.setValue(var10);
                        var1x.consume();
                     }
                  } else if (!var1x.isInertia()
                     || var1x.isInertia() && (this.contentsToViewTimeline == null || this.contentsToViewTimeline.getStatus() == Status.STOPPED)) {
                     this.hsb.setValue(var10);
                     if ((var10 > this.hsb.getMax() || var10 < this.hsb.getMin()) && !this.mouseDown && !this.touchDetected) {
                        this.startContentsToViewport();
                     }

                     var1x.consume();
                  }
               }
            }
         );
      this.getSkinnable().addEventHandler(TouchEvent.TOUCH_PRESSED, var1x -> {
         this.touchDetected = true;
         this.startSBReleasedAnimation();
         var1x.consume();
      });
      this.getSkinnable().addEventHandler(TouchEvent.TOUCH_RELEASED, var1x -> {
         this.touchDetected = false;
         var1x.consume();
      });
      this.consumeMouseEvents(false);
      this.hsb.setValue(var1.getHvalue());
      this.vsb.setValue(var1.getVvalue());
   }

   void scrollBoundsIntoView(Bounds var1) {
      double var2 = 0.0;
      double var4 = 0.0;
      if (var1.getMaxX() > this.contentWidth) {
         var2 = var1.getMinX() - this.snappedLeftInset();
      }

      if (var1.getMinX() < this.snappedLeftInset()) {
         var2 = var1.getMaxX() - this.contentWidth - this.snappedLeftInset();
      }

      if (var1.getMaxY() > this.snappedTopInset() + this.contentHeight) {
         var4 = var1.getMinY() - this.snappedTopInset();
      }

      if (var1.getMinY() < this.snappedTopInset()) {
         var4 = var1.getMaxY() - this.contentHeight - this.snappedTopInset();
      }

      if (var2 != 0.0) {
         double var6 = var2 * (this.hsb.getMax() - this.hsb.getMin()) / (this.nodeWidth - this.contentWidth);
         var6 += -1.0 * Math.signum(var6) * this.hsb.getUnitIncrement() / 5.0;
         this.hsb.setValue(this.hsb.getValue() + var6);
         this.getSkinnable().requestLayout();
      }

      if (var4 != 0.0) {
         double var9 = var4 * (this.vsb.getMax() - this.vsb.getMin()) / (this.nodeHeight - this.contentHeight);
         var9 += -1.0 * Math.signum(var9) * this.vsb.getUnitIncrement() / 5.0;
         this.vsb.setValue(this.vsb.getValue() + var9);
         this.getSkinnable().requestLayout();
      }
   }

   private double computeHsbSizeHint(ScrollPane var1) {
      return var1.getHbarPolicy() != ScrollPane.ScrollBarPolicy.ALWAYS
            && (var1.getHbarPolicy() != ScrollPane.ScrollBarPolicy.AS_NEEDED || !(var1.getPrefViewportHeight() > 0.0) && !(var1.getMinViewportHeight() > 0.0))
         ? 0.0
         : this.hsb.prefHeight(-1.0);
   }

   private double computeVsbSizeHint(ScrollPane var1) {
      return var1.getVbarPolicy() != ScrollPane.ScrollBarPolicy.ALWAYS
            && (var1.getVbarPolicy() != ScrollPane.ScrollBarPolicy.AS_NEEDED || !(var1.getPrefViewportWidth() > 0.0) && !(var1.getMinViewportWidth() > 0.0))
         ? 0.0
         : this.vsb.prefWidth(-1.0);
   }

   private void computeScrollNodeSize(double var1, double var3) {
      if (this.scrollNode != null) {
         if (this.scrollNode.isResizable()) {
            ScrollPane var5 = this.getSkinnable();
            Orientation var6 = this.scrollNode.getContentBias();
            if (var6 == null) {
               this.nodeWidth = this.snapSizeX(
                  com.sun.javafx.scene.control.skin.Utils.boundedSize(
                     var5.isFitToWidth() ? var1 : this.scrollNode.prefWidth(-1.0), this.scrollNode.minWidth(-1.0), this.scrollNode.maxWidth(-1.0)
                  )
               );
               this.nodeHeight = this.snapSizeY(
                  com.sun.javafx.scene.control.skin.Utils.boundedSize(
                     var5.isFitToHeight() ? var3 : this.scrollNode.prefHeight(-1.0), this.scrollNode.minHeight(-1.0), this.scrollNode.maxHeight(-1.0)
                  )
               );
            } else if (var6 == Orientation.HORIZONTAL) {
               this.nodeWidth = this.snapSizeX(
                  com.sun.javafx.scene.control.skin.Utils.boundedSize(
                     var5.isFitToWidth() ? var1 : this.scrollNode.prefWidth(-1.0), this.scrollNode.minWidth(-1.0), this.scrollNode.maxWidth(-1.0)
                  )
               );
               this.nodeHeight = this.snapSizeY(
                  com.sun.javafx.scene.control.skin.Utils.boundedSize(
                     var5.isFitToHeight() ? var3 : this.scrollNode.prefHeight(this.nodeWidth),
                     this.scrollNode.minHeight(this.nodeWidth),
                     this.scrollNode.maxHeight(this.nodeWidth)
                  )
               );
            } else {
               this.nodeHeight = this.snapSizeY(
                  com.sun.javafx.scene.control.skin.Utils.boundedSize(
                     var5.isFitToHeight() ? var3 : this.scrollNode.prefHeight(-1.0), this.scrollNode.minHeight(-1.0), this.scrollNode.maxHeight(-1.0)
                  )
               );
               this.nodeWidth = this.snapSizeX(
                  com.sun.javafx.scene.control.skin.Utils.boundedSize(
                     var5.isFitToWidth() ? var1 : this.scrollNode.prefWidth(this.nodeHeight),
                     this.scrollNode.minWidth(this.nodeHeight),
                     this.scrollNode.maxWidth(this.nodeHeight)
                  )
               );
            }
         } else {
            this.nodeWidth = this.snapSizeX(this.scrollNode.getLayoutBounds().getWidth());
            this.nodeHeight = this.snapSizeY(this.scrollNode.getLayoutBounds().getHeight());
         }

         this.nodeSizeInvalid = false;
      }
   }

   private boolean isReverseNodeOrientation() {
      return this.scrollNode != null && this.getSkinnable().getEffectiveNodeOrientation() != this.scrollNode.getEffectiveNodeOrientation();
   }

   private boolean determineHorizontalSBVisible() {
      ScrollPane var1 = this.getSkinnable();
      if (Properties.IS_TOUCH_SUPPORTED) {
         return this.tempVisibility && this.nodeWidth > this.contentWidth;
      } else {
         ScrollPane.ScrollBarPolicy var2 = var1.getHbarPolicy();
         return ScrollPane.ScrollBarPolicy.NEVER == var2
            ? false
            : (
               ScrollPane.ScrollBarPolicy.ALWAYS == var2
                  ? true
                  : (
                     var1.isFitToWidth() && this.scrollNode != null && this.scrollNode.isResizable()
                        ? this.nodeWidth > this.contentWidth && this.scrollNode.minWidth(-1.0) > this.contentWidth
                        : this.nodeWidth > this.contentWidth
                  )
            );
      }
   }

   private boolean determineVerticalSBVisible() {
      ScrollPane var1 = this.getSkinnable();
      if (Properties.IS_TOUCH_SUPPORTED) {
         return this.tempVisibility && this.nodeHeight > this.contentHeight;
      } else {
         ScrollPane.ScrollBarPolicy var2 = var1.getVbarPolicy();
         return ScrollPane.ScrollBarPolicy.NEVER == var2
            ? false
            : (
               ScrollPane.ScrollBarPolicy.ALWAYS == var2
                  ? true
                  : (
                     var1.isFitToHeight() && this.scrollNode != null && this.scrollNode.isResizable()
                        ? this.nodeHeight > this.contentHeight && this.scrollNode.minHeight(-1.0) > this.contentHeight
                        : this.nodeHeight > this.contentHeight
                  )
            );
      }
   }

   private void computeScrollBarSize() {
      this.vsbWidth = this.snapSizeX(this.vsb.prefWidth(-1.0));
      if (this.vsbWidth == 0.0) {
         if (Properties.IS_TOUCH_SUPPORTED) {
            this.vsbWidth = 8.0;
         } else {
            this.vsbWidth = 12.0;
         }
      }

      this.hsbHeight = this.snapSizeY(this.hsb.prefHeight(-1.0));
      if (this.hsbHeight == 0.0) {
         if (Properties.IS_TOUCH_SUPPORTED) {
            this.hsbHeight = 8.0;
         } else {
            this.hsbHeight = 12.0;
         }
      }
   }

   private void updateHorizontalSB() {
      double var1 = this.nodeWidth * (this.hsb.getMax() - this.hsb.getMin());
      if (var1 > 0.0) {
         this.hsb.setVisibleAmount(this.contentWidth / var1);
         this.hsb.setBlockIncrement(0.9 * this.hsb.getVisibleAmount());
         this.hsb.setUnitIncrement(0.1 * this.hsb.getVisibleAmount());
      } else {
         this.hsb.setVisibleAmount(0.0);
         this.hsb.setBlockIncrement(0.0);
         this.hsb.setUnitIncrement(0.0);
      }

      if (this.hsb.isVisible()) {
         this.updatePosX();
      } else if (this.nodeWidth > this.contentWidth) {
         this.updatePosX();
      } else {
         this.viewContent.setLayoutX(0.0);
      }
   }

   private void updateVerticalSB() {
      double var1 = this.nodeHeight * (this.vsb.getMax() - this.vsb.getMin());
      if (var1 > 0.0) {
         this.vsb.setVisibleAmount(this.contentHeight / var1);
         this.vsb.setBlockIncrement(0.9 * this.vsb.getVisibleAmount());
         this.vsb.setUnitIncrement(0.1 * this.vsb.getVisibleAmount());
      } else {
         this.vsb.setVisibleAmount(0.0);
         this.vsb.setBlockIncrement(0.0);
         this.vsb.setUnitIncrement(0.0);
      }

      if (this.vsb.isVisible()) {
         this.updatePosY();
      } else if (this.nodeHeight > this.contentHeight) {
         this.updatePosY();
      } else {
         this.viewContent.setLayoutY(0.0);
      }
   }

   private double updatePosX() {
      ScrollPane var1 = this.getSkinnable();
      double var2 = this.isReverseNodeOrientation() ? this.hsb.getMax() - (this.posX - this.hsb.getMin()) : this.posX;
      double var4 = Math.min(-var2 / (this.hsb.getMax() - this.hsb.getMin()) * (this.nodeWidth - this.contentWidth), 0.0);
      this.viewContent.setLayoutX(this.snapPositionX(var4));
      if (!var1.hvalueProperty().isBound()) {
         var1.setHvalue(Utils.clamp(var1.getHmin(), this.posX, var1.getHmax()));
      }

      return this.posX;
   }

   private double updatePosY() {
      ScrollPane var1 = this.getSkinnable();
      double var2 = Math.min(-this.posY / (this.vsb.getMax() - this.vsb.getMin()) * (this.nodeHeight - this.contentHeight), 0.0);
      this.viewContent.setLayoutY(this.snapPositionY(var2));
      if (!var1.vvalueProperty().isBound()) {
         var1.setVvalue(Utils.clamp(var1.getVmin(), this.posY, var1.getVmax()));
      }

      return this.posY;
   }

   private void resetClip() {
      this.clipRect.setWidth(this.snapSizeX(this.contentWidth));
      this.clipRect.setHeight(this.snapSizeY(this.contentHeight));
   }

   private void startSBReleasedAnimation() {
      if (this.sbTouchTimeline == null) {
         this.sbTouchTimeline = new Timeline();
         this.sbTouchKF1 = new KeyFrame(Duration.millis(0.0), var1 -> {
            this.tempVisibility = true;
            if (this.touchDetected || this.mouseDown) {
               this.sbTouchTimeline.playFromStart();
            }
         }, new KeyValue[0]);
         this.sbTouchKF2 = new KeyFrame(Duration.millis(1000.0), var1 -> {
            this.tempVisibility = false;
            this.getSkinnable().requestLayout();
         }, new KeyValue[0]);
         this.sbTouchTimeline.getKeyFrames().addAll(new KeyFrame[]{this.sbTouchKF1, this.sbTouchKF2});
      }

      this.sbTouchTimeline.playFromStart();
   }

   private void startContentsToViewport() {
      double var1 = this.posX;
      double var3 = this.posY;
      this.setContentPosX(this.posX);
      this.setContentPosY(this.posY);
      if (this.posY > this.getSkinnable().getVmax()) {
         var3 = this.getSkinnable().getVmax();
      } else if (this.posY < this.getSkinnable().getVmin()) {
         var3 = this.getSkinnable().getVmin();
      }

      if (this.posX > this.getSkinnable().getHmax()) {
         var1 = this.getSkinnable().getHmax();
      } else if (this.posX < this.getSkinnable().getHmin()) {
         var1 = this.getSkinnable().getHmin();
      }

      if (!Properties.IS_TOUCH_SUPPORTED) {
         this.startSBReleasedAnimation();
      }

      if (this.contentsToViewTimeline != null) {
         this.contentsToViewTimeline.stop();
      }

      this.contentsToViewTimeline = new Timeline();
      this.contentsToViewKF1 = new KeyFrame(Duration.millis(50.0), new KeyValue[0]);
      this.contentsToViewKF2 = new KeyFrame(
         Duration.millis(150.0),
         var1x -> this.getSkinnable().requestLayout(),
         new KeyValue[]{new KeyValue(this.contentPosX, var1), new KeyValue(this.contentPosY, var3)}
      );
      this.contentsToViewKF3 = new KeyFrame(Duration.millis(1500.0), new KeyValue[0]);
      this.contentsToViewTimeline.getKeyFrames().addAll(new KeyFrame[]{this.contentsToViewKF1, this.contentsToViewKF2, this.contentsToViewKF3});
      this.contentsToViewTimeline.playFromStart();
   }
}
