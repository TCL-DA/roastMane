package javafx.scene.control.skin;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.Logging;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.VirtualScrollBar;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import com.sun.javafx.util.Utils;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;

public class VirtualFlow<T extends IndexedCell> extends Region {
   private static final int MIN_SCROLLING_LINES_PER_PAGE = 8;
   private static final String NEW_CELL = "newcell";
   private static final double GOLDEN_RATIO_MULTIPLIER = 0.618033987;
   private static final int DEFAULT_IMPROVEMENT = 2;
   private boolean touchDetected = false;
   private boolean mouseDown = false;
   double lastWidth = -1.0;
   double lastHeight = -1.0;
   int lastCellCount = 0;
   boolean lastVertical;
   double lastPosition;
   double lastCellBreadth = -1.0;
   double lastCellLength = -1.0;
   final VirtualFlow.ArrayLinkedList<T> cells = new VirtualFlow.ArrayLinkedList<>();
   final VirtualFlow.ArrayLinkedList<T> pile = new VirtualFlow.ArrayLinkedList<>();
   T accumCell;
   Group accumCellParent;
   final Group sheet;
   final ObservableList<Node> sheetChildren;
   private VirtualScrollBar hbar = new VirtualScrollBar(this);
   private VirtualScrollBar vbar = new VirtualScrollBar(this);
   VirtualFlow.ClippedContainer clipView;
   StackPane corner;
   double absoluteOffset = 0.0;
   double estimatedSize = -1.0;
   private ArrayList<Double> itemSizeCache = new ArrayList<>();
   private double lastX;
   private double lastY;
   private boolean isPanning = false;
   private boolean fixedCellSizeEnabled = false;
   private boolean needsReconfigureCells = false;
   private boolean needsRecreateCells = false;
   private boolean needsRebuildCells = false;
   private boolean needsCellsLayout = false;
   private boolean sizeChanged = false;
   private final BitSet dirtyCells = new BitSet();
   Timeline sbTouchTimeline;
   KeyFrame sbTouchKF1;
   KeyFrame sbTouchKF2;
   private boolean needBreadthBar;
   private boolean needLengthBar;
   private boolean tempVisibility = false;
   private BooleanProperty vertical;
   private BooleanProperty pannable = new SimpleBooleanProperty(this, "pannable", true);
   private IntegerProperty cellCount = new SimpleIntegerProperty(this, "cellCount", 0) {
      private int oldCount = 0;

      protected void invalidated() {
         int var1 = this.get();
         boolean var2 = this.oldCount != var1;
         this.oldCount = var1;
         if (var2) {
            VirtualScrollBar var3 = VirtualFlow.this.isVertical() ? VirtualFlow.this.vbar : VirtualFlow.this.hbar;
            var3.setMax((double)var1);
         }

         if (var2) {
            VirtualFlow.this.layoutChildren();
            Parent var4 = VirtualFlow.this.getParent();
            if (var4 != null) {
               var4.requestLayout();
            }
         }
      }
   };
   private DoubleProperty position = new SimpleDoubleProperty(this, "position") {
      public void setValue(Number var1) {
         super.setValue(Utils.clamp(0.0, this.get(), 1.0));
      }

      protected void invalidated() {
         super.invalidated();
         VirtualFlow.this.requestLayout();
      }
   };
   private DoubleProperty fixedCellSize = new SimpleDoubleProperty(this, "fixedCellSize") {
      protected void invalidated() {
         VirtualFlow.this.fixedCellSizeEnabled = this.get() > 0.0;
         VirtualFlow.this.needsCellsLayout = true;
         VirtualFlow.this.layoutChildren();
      }
   };
   private ObjectProperty<Callback<VirtualFlow<T>, T>> cellFactory;
   private double maxPrefBreadth;
   private double viewportBreadth;
   private double viewportLength;
   private final List<T> privateCells = new ArrayList<>();

   public VirtualFlow() {
      this.getStyleClass().add("virtual-flow");
      this.setId("virtual-flow");
      this.sheet = new Group();
      this.sheet.getStyleClass().add("sheet");
      this.sheet.setAutoSizeChildren(false);
      this.sheetChildren = this.sheet.getChildren();
      this.clipView = new VirtualFlow.ClippedContainer(this);
      this.clipView.setNode(this.sheet);
      this.getChildren().add(this.clipView);
      this.accumCellParent = new Group();
      this.accumCellParent.setVisible(false);
      this.getChildren().add(this.accumCellParent);
      EventDispatcher var1 = (var0, var1x) -> var0;
      EventDispatcher var2 = this.hbar.getEventDispatcher();
      this.hbar.setEventDispatcher((var2x, var3x) -> {
         if (var2x.getEventType() == ScrollEvent.SCROLL && !((ScrollEvent)var2x).isDirect()) {
            var3x = var3x.prepend(var1);
            var3x = var3x.prepend(var2);
            return var3x.dispatchEvent(var2x);
         } else {
            return var2.dispatchEvent(var2x, var3x);
         }
      });
      EventDispatcher var3 = this.vbar.getEventDispatcher();
      this.vbar.setEventDispatcher((var2x, var3x) -> {
         if (var2x.getEventType() == ScrollEvent.SCROLL && !((ScrollEvent)var2x).isDirect()) {
            var3x = var3x.prepend(var1);
            var3x = var3x.prepend(var3);
            return var3x.dispatchEvent(var2x);
         } else {
            return var3.dispatchEvent(var2x, var3x);
         }
      });
      this.setOnScroll(
         new EventHandler<ScrollEvent>() {
            public void handle(ScrollEvent var1) {
               if (Properties.IS_TOUCH_SUPPORTED && !VirtualFlow.this.touchDetected && !VirtualFlow.this.mouseDown) {
                  VirtualFlow.this.startSBReleasedAnimation();
               }

               double var2 = 0.0;
               if (VirtualFlow.this.isVertical()) {
                  switch (var1.getTextDeltaYUnits()) {
                     case PAGES:
                        var2 = var1.getTextDeltaY() * VirtualFlow.this.lastHeight;
                        break;
                     case LINES:
                        double var4;
                        if (VirtualFlow.this.fixedCellSizeEnabled) {
                           var4 = VirtualFlow.this.getFixedCellSize();
                        } else {
                           IndexedCell var6 = VirtualFlow.this.cells.getLast();
                           var4 = (
                                 VirtualFlow.this.getCellPosition(var6)
                                    + VirtualFlow.this.getCellLength(var6)
                                    - VirtualFlow.this.getCellPosition(VirtualFlow.this.cells.getFirst())
                              )
                              / (double)VirtualFlow.this.cells.size();
                        }

                        if (VirtualFlow.this.lastHeight / var4 < 8.0) {
                           var4 = VirtualFlow.this.lastHeight / 8.0;
                        }

                        var2 = var1.getTextDeltaY() * var4;
                        break;
                     case NONE:
                        var2 = var1.getDeltaY();
                  }
               } else {
                  switch (var1.getTextDeltaXUnits()) {
                     case CHARACTERS:
                     case NONE:
                        double var9 = var1.getDeltaX();
                        double var12 = var1.getDeltaY();
                        var2 = Math.abs(var9) > Math.abs(var12) ? var9 : var12;
                  }
               }

               if (var2 != 0.0) {
                  double var10 = VirtualFlow.this.scrollPixels(-var2);
                  if (var10 != 0.0) {
                     var1.consume();
                  }
               }

               VirtualScrollBar var11 = VirtualFlow.this.isVertical() ? VirtualFlow.this.hbar : VirtualFlow.this.vbar;
               if (VirtualFlow.this.needBreadthBar) {
                  double var5 = VirtualFlow.this.isVertical() ? var1.getDeltaX() : var1.getDeltaY();
                  if (var5 != 0.0) {
                     double var7 = var11.getValue() - var5;
                     if (var7 < var11.getMin()) {
                        var11.setValue(var11.getMin());
                     } else if (var7 > var11.getMax()) {
                        var11.setValue(var11.getMax());
                     } else {
                        var11.setValue(var7);
                     }

                     var1.consume();
                  }
               }
            }
         }
      );
      this.addEventFilter(
         MouseEvent.MOUSE_PRESSED,
         new EventHandler<MouseEvent>() {
            public void handle(MouseEvent var1) {
               VirtualFlow.this.mouseDown = true;
               if (Properties.IS_TOUCH_SUPPORTED) {
                  VirtualFlow.this.scrollBarOn();
               }

               if (VirtualFlow.this.isFocusTraversable()) {
                  boolean var2 = true;
                  Node var3 = VirtualFlow.this.getScene().getFocusOwner();
                  if (var3 != null) {
                     for (Parent var4 = var3.getParent(); var4 != null; var4 = var4.getParent()) {
                        if (var4.equals(VirtualFlow.this)) {
                           var2 = false;
                           break;
                        }
                     }
                  }

                  if (var2) {
                     VirtualFlow.this.requestFocus();
                  }
               }

               VirtualFlow.this.lastX = var1.getX();
               VirtualFlow.this.lastY = var1.getY();
               VirtualFlow.this.isPanning = !VirtualFlow.this.vbar.getBoundsInParent().contains(var1.getX(), var1.getY())
                  && !VirtualFlow.this.hbar.getBoundsInParent().contains(var1.getX(), var1.getY());
            }
         }
      );
      this.addEventFilter(MouseEvent.MOUSE_RELEASED, var1x -> {
         this.mouseDown = false;
         if (Properties.IS_TOUCH_SUPPORTED) {
            this.startSBReleasedAnimation();
         }
      });
      this.addEventFilter(MouseEvent.MOUSE_DRAGGED, var1x -> {
         if (Properties.IS_TOUCH_SUPPORTED) {
            this.scrollBarOn();
         }

         if (this.isPanning && this.isPannable()) {
            double var2x = this.lastX - var1x.getX();
            double var4x = this.lastY - var1x.getY();
            double var6 = this.isVertical() ? var4x : var2x;
            double var8 = this.scrollPixels(var6);
            if (var8 != 0.0) {
               if (this.isVertical()) {
                  this.lastY = var1x.getY();
               } else {
                  this.lastX = var1x.getX();
               }
            }

            double var10 = this.isVertical() ? var2x : var4x;
            VirtualScrollBar var12 = this.isVertical() ? this.hbar : this.vbar;
            if (var12.isVisible()) {
               double var13 = var12.getValue() + var10;
               if (var13 < var12.getMin()) {
                  var12.setValue(var12.getMin());
               } else if (var13 > var12.getMax()) {
                  var12.setValue(var12.getMax());
               } else {
                  var12.setValue(var13);
                  if (this.isVertical()) {
                     this.lastX = var1x.getX();
                  } else {
                     this.lastY = var1x.getY();
                  }
               }
            }
         }
      });
      this.vbar.setOrientation(Orientation.VERTICAL);
      this.vbar.addEventHandler(MouseEvent.ANY, var0 -> var0.consume());
      this.getChildren().add(this.vbar);
      this.hbar.setOrientation(Orientation.HORIZONTAL);
      this.hbar.addEventHandler(MouseEvent.ANY, var0 -> var0.consume());
      this.getChildren().add(this.hbar);
      this.corner = new StackPane();
      this.corner.getStyleClass().setAll(new String[]{"corner"});
      this.getChildren().add(this.corner);
      InvalidationListener var4 = var1x -> this.updateHbar();
      this.verticalProperty().addListener(var4);
      this.hbar.valueProperty().addListener(var4);
      this.hbar.visibleProperty().addListener(var4);
      ChangeListener var5 = (var1x, var2x, var3x) -> this.clipView.setClipY(this.isVertical() ? 0.0 : this.vbar.getValue());
      this.vbar.valueProperty().addListener(var5);
      super.heightProperty().addListener((var1x, var2x, var3x) -> {
         if (var2x.doubleValue() == 0.0 && var3x.doubleValue() > 0.0) {
            this.recreateCells();
         }
      });
      this.setOnTouchPressed(var1x -> {
         this.touchDetected = true;
         this.scrollBarOn();
      });
      this.setOnTouchReleased(var1x -> {
         this.touchDetected = false;
         this.startSBReleasedAnimation();
      });
      ParentHelper.setTraversalEngine(this, new ParentTraversalEngine(this, new Algorithm() {
         Node selectNextAfterIndex(int var1, TraversalContext var2) {
            IndexedCell var3;
            while ((var3 = VirtualFlow.this.getVisibleCell(++var1)) != null) {
               if (var3.isFocusTraversable()) {
                  return var3;
               }

               Node var4 = var2.selectFirstInParent(var3);
               if (var4 != null) {
                  return var4;
               }
            }

            return null;
         }

         Node selectPreviousBeforeIndex(int var1, TraversalContext var2) {
            IndexedCell var3;
            while ((var3 = VirtualFlow.this.getVisibleCell(--var1)) != null) {
               Node var4 = var2.selectLastInParent(var3);
               if (var4 != null) {
                  return var4;
               }

               if (var3.isFocusTraversable()) {
                  return var3;
               }
            }

            return null;
         }

         public Node select(Node var1, Direction var2, TraversalContext var3) {
            if (VirtualFlow.this.cells.isEmpty()) {
               return null;
            } else {
               IndexedCell var4;
               if (VirtualFlow.this.cells.contains(var1)) {
                  var4 = (IndexedCell)var1;
               } else {
                  var4 = this.findOwnerCell(var1);
                  Node var5 = var3.selectInSubtree(var4, var1, var2);
                  if (var5 != null) {
                     return var5;
                  }

                  if (var2 == Direction.NEXT) {
                     var2 = Direction.NEXT_IN_LINE;
                  }
               }

               int var7 = var4.getIndex();
               switch (var2) {
                  case PREVIOUS:
                     return this.selectPreviousBeforeIndex(var7, var3);
                  case NEXT:
                     Node var6 = var3.selectFirstInParent(var4);
                     if (var6 != null) {
                        return var6;
                     }
                  case NEXT_IN_LINE:
                     return this.selectNextAfterIndex(var7, var3);
                  default:
                     return null;
               }
            }
         }

         private T findOwnerCell(Node var1) {
            Parent var2 = var1.getParent();

            while (!VirtualFlow.this.cells.contains(var2)) {
               var2 = var2.getParent();
            }

            return (T)var2;
         }

         public Node selectFirst(TraversalContext var1) {
            IndexedCell var2 = VirtualFlow.this.cells.getFirst();
            if (var2 == null) {
               return null;
            } else if (var2.isFocusTraversable()) {
               return var2;
            } else {
               Node var3 = var1.selectFirstInParent(var2);
               return var3 != null ? var3 : this.selectNextAfterIndex(var2.getIndex(), var1);
            }
         }

         public Node selectLast(TraversalContext var1) {
            IndexedCell var2 = VirtualFlow.this.cells.getLast();
            if (var2 == null) {
               return null;
            } else {
               Node var3 = var1.selectLastInParent(var2);
               if (var3 != null) {
                  return var3;
               } else {
                  return (Node)(var2.isFocusTraversable() ? var2 : this.selectPreviousBeforeIndex(var2.getIndex(), var1));
               }
            }
         }
      }));
   }

   public final void setVertical(boolean var1) {
      this.verticalProperty().set(var1);
   }

   public final boolean isVertical() {
      return this.vertical == null ? true : this.vertical.get();
   }

   public final BooleanProperty verticalProperty() {
      if (this.vertical == null) {
         this.vertical = new BooleanPropertyBase(true) {
            protected void invalidated() {
               VirtualFlow.this.pile.clear();
               VirtualFlow.this.sheetChildren.clear();
               VirtualFlow.this.cells.clear();
               VirtualFlow.this.lastWidth = VirtualFlow.this.lastHeight = -1.0;
               VirtualFlow.this.setMaxPrefBreadth(-1.0);
               VirtualFlow.this.setViewportBreadth(0.0);
               VirtualFlow.this.setViewportLength(0.0);
               VirtualFlow.this.lastPosition = 0.0;
               VirtualFlow.this.hbar.setValue(0.0);
               VirtualFlow.this.vbar.setValue(0.0);
               VirtualFlow.this.setPosition(0.0);
               VirtualFlow.this.setNeedsLayout(true);
               VirtualFlow.this.requestLayout();
            }

            public Object getBean() {
               return VirtualFlow.this;
            }

            public String getName() {
               return "vertical";
            }
         };
      }

      return this.vertical;
   }

   public final boolean isPannable() {
      return this.pannable.get();
   }

   public final void setPannable(boolean var1) {
      this.pannable.set(var1);
   }

   public final BooleanProperty pannableProperty() {
      return this.pannable;
   }

   public final int getCellCount() {
      return this.cellCount.get();
   }

   public final void setCellCount(int var1) {
      this.resetSizeEstimates();
      this.cellCount.set(var1);
      this.adjustAbsoluteOffset();
   }

   public final IntegerProperty cellCountProperty() {
      return this.cellCount;
   }

   public final double getPosition() {
      return this.position.get();
   }

   public final void setPosition(double var1) {
      this.position.set(var1);
      this.adjustAbsoluteOffset();
   }

   public final DoubleProperty positionProperty() {
      return this.position;
   }

   public final void setFixedCellSize(double var1) {
      this.fixedCellSize.set(var1);
   }

   public final double getFixedCellSize() {
      return this.fixedCellSize.get();
   }

   public final DoubleProperty fixedCellSizeProperty() {
      return this.fixedCellSize;
   }

   public final void setCellFactory(Callback<VirtualFlow<T>, T> var1) {
      this.cellFactoryProperty().set(var1);
   }

   public final Callback<VirtualFlow<T>, T> getCellFactory() {
      return this.cellFactory == null ? null : (Callback)this.cellFactory.get();
   }

   public final ObjectProperty<Callback<VirtualFlow<T>, T>> cellFactoryProperty() {
      if (this.cellFactory == null) {
         this.cellFactory = new SimpleObjectProperty<Callback<VirtualFlow<T>, T>>(this, "cellFactory") {
            protected void invalidated() {
               if (this.get() != null) {
                  VirtualFlow.this.setNeedsLayout(true);
                  VirtualFlow.this.recreateCells();
                  if (VirtualFlow.this.getParent() != null) {
                     VirtualFlow.this.getParent().requestLayout();
                  }
               }

               if (VirtualFlow.this.accumCellParent != null) {
                  VirtualFlow.this.accumCellParent.getChildren().clear();
               }

               VirtualFlow.this.accumCell = null;
            }
         };
      }

      return this.cellFactory;
   }

   public void requestLayout() {
      super.requestLayout();
   }

   void adjustAbsoluteOffset() {
      this.absoluteOffset = (this.estimatedSize - this.viewportLength) * this.getPosition();
   }

   void adjustPosition() {
      if (this.viewportLength >= this.estimatedSize) {
         this.setPosition(0.0);
      } else {
         this.setPosition(this.absoluteOffset / (this.estimatedSize - this.viewportLength));
      }
   }

   protected void layoutChildren() {
      double var1 = this.absoluteOffset;
      this.recalculateEstimatedSize();
      if (this.lastCellCount != this.getCellCount()) {
         this.absoluteOffset = var1;
         this.adjustPosition();
      } else {
         this.adjustAbsoluteOffset();
      }

      if (this.needsRecreateCells) {
         this.lastWidth = -1.0;
         this.lastHeight = -1.0;
         this.releaseCell(this.accumCell);
         this.sheet.getChildren().clear();
         int var3 = 0;

         for (int var4 = this.cells.size(); var3 < var4; var3++) {
            this.cells.get(var3).updateIndex(-1);
         }

         this.cells.clear();
         this.pile.clear();
         this.releaseAllPrivateCells();
      } else if (this.needsRebuildCells) {
         this.lastWidth = -1.0;
         this.lastHeight = -1.0;
         this.releaseCell(this.accumCell);
         int var24 = 0;

         for (int var27 = this.cells.size(); var24 < var27; var24++) {
            this.cells.get(var24).updateIndex(-1);
         }

         this.addAllToPile();
         this.releaseAllPrivateCells();
      } else if (this.needsReconfigureCells) {
         this.setMaxPrefBreadth(-1.0);
         this.lastWidth = -1.0;
         this.lastHeight = -1.0;
      }

      if (!this.dirtyCells.isEmpty()) {
         int var28 = this.cells.size();

         int var25;
         while ((var25 = this.dirtyCells.nextSetBit(0)) != -1 && var25 < var28) {
            IndexedCell var5 = this.cells.get(var25);
            if (var5 != null) {
               var5.requestLayout();
            }

            this.dirtyCells.clear(var25);
         }

         this.setMaxPrefBreadth(-1.0);
         this.lastWidth = -1.0;
         this.lastHeight = -1.0;
      }

      boolean var26 = this.sizeChanged;
      boolean var29 = this.needsRebuildCells || this.needsRecreateCells || this.sizeChanged;
      this.needsRecreateCells = false;
      this.needsReconfigureCells = false;
      this.needsRebuildCells = false;
      this.sizeChanged = false;
      if (this.needsCellsLayout) {
         int var32 = 0;

         for (int var6 = this.cells.size(); var32 < var6; var32++) {
            Cell var33 = this.cells.get(var32);
            if (var33 != null) {
               var33.requestLayout();
            }
         }

         this.needsCellsLayout = false;
      } else {
         double var31 = this.getWidth();
         double var7 = this.getHeight();
         boolean var9 = this.isVertical();
         double var10 = this.getPosition();
         if (!(var31 <= 0.0) && !(var7 <= 0.0)) {
            boolean var12 = false;
            boolean var13 = false;
            if (Properties.IS_TOUCH_SUPPORTED
               && (
                  this.tempVisibility && (!this.hbar.isVisible() || !this.vbar.isVisible())
                     || !this.tempVisibility && (this.hbar.isVisible() || this.vbar.isVisible())
               )) {
               var13 = true;
            }

            if (!var12) {
               for (int var14 = 0; var14 < this.cells.size(); var14++) {
                  Cell var15 = this.cells.get(var14);
                  var12 = var15.isNeedsLayout();
                  if (var12) {
                     break;
                  }
               }
            }

            int var34 = this.getCellCount();
            IndexedCell var35 = this.getFirstVisibleCell();
            if (!var12 && !var13) {
               boolean var16 = false;
               if (var35 != null) {
                  double var17 = this.getCellBreadth(var35);
                  double var19 = this.getCellLength((T)var35);
                  var16 = var17 != this.lastCellBreadth || var19 != this.lastCellLength;
                  this.lastCellBreadth = var17;
                  this.lastCellLength = var19;
               }

               if (var31 == this.lastWidth
                  && var7 == this.lastHeight
                  && var34 == this.lastCellCount
                  && var9 == this.lastVertical
                  && var10 == this.lastPosition
                  && !var16) {
                  return;
               }
            }

            boolean var36 = false;
            boolean var37 = var12
               || var9 != this.lastVertical
               || this.cells.isEmpty()
               || this.getMaxPrefBreadth() == -1.0
               || var10 != this.lastPosition
               || var34 != this.lastCellCount
               || var26
               || var9 && var7 < this.lastHeight
               || !var9 && var31 < this.lastWidth;
            if (!var37) {
               double var18 = this.getMaxPrefBreadth();
               boolean var20 = false;

               for (int var21 = 0; var21 < this.cells.size(); var21++) {
                  double var22 = this.getCellBreadth(this.cells.get(var21));
                  if (var18 == var22) {
                     var20 = true;
                  } else if (var22 > var18) {
                     var37 = true;
                     break;
                  }
               }

               if (!var20) {
                  var37 = true;
               }
            }

            if (!var37 && (var9 && var7 > this.lastHeight || !var9 && var31 > this.lastWidth)) {
               var36 = true;
            }

            this.initViewport();
            int var38 = this.computeCurrentIndex();
            if (this.lastCellCount != var34) {
               if (var10 != 0.0 && var10 != 1.0 && var38 >= var34) {
                  this.setPosition(1.0);
               }

               var38 = this.computeCurrentIndex();
            }

            if (var37) {
               this.setMaxPrefBreadth(-1.0);
               this.addAllToPile();
               double var39 = -this.computeViewportOffset(this.getPosition());
               this.addLeadingCells(var38, var39);
               this.addTrailingCells(true);
            } else if (var36) {
               this.addTrailingCells(true);
            }

            this.computeBarVisiblity();
            var29 = var29 || var37;
            this.updateScrollBarsAndCells(var29);
            this.lastWidth = this.getWidth();
            this.lastHeight = this.getHeight();
            this.lastCellCount = this.getCellCount();
            this.lastVertical = this.isVertical();
            this.lastPosition = this.getPosition();
            this.recalculateEstimatedSize();
            this.cleanPile();
         } else {
            this.addAllToPile();
            this.lastWidth = var31;
            this.lastHeight = var7;
            this.hbar.setVisible(false);
            this.vbar.setVisible(false);
            this.corner.setVisible(false);
         }
      }
   }

   protected void setWidth(double var1) {
      if (var1 != this.lastWidth) {
         super.setWidth(var1);
         this.sizeChanged = true;
         this.setNeedsLayout(true);
         this.requestLayout();
      }
   }

   protected void setHeight(double var1) {
      if (var1 != this.lastHeight) {
         super.setHeight(var1);
         this.sizeChanged = true;
         this.setNeedsLayout(true);
         this.requestLayout();
      }
   }

   protected T getAvailableCell(int var1) {
      IndexedCell var2 = null;
      int var3 = 0;

      for (int var4 = this.pile.size(); var3 < var4; var3++) {
         IndexedCell var5 = this.pile.get(var3);

         assert var5 != null;

         if (this.getCellIndex((T)var5) == var1) {
            var2 = var5;
            this.pile.remove(var3);
            break;
         }
      }

      if (var2 == null && !this.pile.isEmpty()) {
         var2 = this.pile.removeLast();
      }

      if (var2 == null) {
         var2 = (IndexedCell)this.getCellFactory().call(this);
         var2.getProperties().put("newcell", null);
      }

      if (var2.getParent() == null) {
         this.sheetChildren.add(var2);
      }

      return (T)var2;
   }

   protected void addAllToPile() {
      int var1 = 0;

      for (int var2 = this.cells.size(); var1 < var2; var1++) {
         this.addToPile(this.cells.removeFirst());
      }
   }

   public T getVisibleCell(int var1) {
      if (this.cells.isEmpty()) {
         return null;
      } else {
         IndexedCell var2 = this.cells.getLast();
         int var3 = this.getCellIndex((T)var2);
         if (var1 == var3) {
            return (T)var2;
         } else {
            IndexedCell var4 = this.cells.getFirst();
            int var5 = this.getCellIndex((T)var4);
            if (var1 == var5) {
               return (T)var4;
            } else {
               if (var1 > var5 && var1 < var3) {
                  IndexedCell var6 = this.cells.get(var1 - var5);
                  if (this.getCellIndex((T)var6) == var1) {
                     return (T)var6;
                  }
               }

               return null;
            }
         }
      }
   }

   public T getLastVisibleCell() {
      if (!this.cells.isEmpty() && !(this.getViewportLength() <= 0.0)) {
         for (int var2 = this.cells.size() - 1; var2 >= 0; var2--) {
            IndexedCell var1 = this.cells.get(var2);
            if (!var1.isEmpty()) {
               return (T)var1;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public T getFirstVisibleCell() {
      if (!this.cells.isEmpty() && !(this.getViewportLength() <= 0.0)) {
         IndexedCell var1 = this.cells.getFirst();
         return (T)(var1.isEmpty() ? null : var1);
      } else {
         return null;
      }
   }

   public void scrollToTop(T var1) {
      if (var1 != null) {
         this.scrollPixels(this.getCellPosition((T)var1));
      }
   }

   public void scrollToBottom(T var1) {
      if (var1 != null) {
         this.scrollPixels(this.getCellPosition((T)var1) + this.getCellLength((T)var1) - this.getViewportLength());
      }
   }

   public void scrollTo(T var1) {
      if (var1 != null) {
         double var2 = this.getCellPosition((T)var1);
         double var4 = this.getCellLength((T)var1);
         double var6 = var2 + var4;
         double var8 = this.getViewportLength();
         if (var2 < 0.0) {
            this.scrollPixels(var2);
         } else if (var6 > var8) {
            this.scrollPixels(var6 - var8);
         }
      }
   }

   public void scrollTo(int var1) {
      IndexedCell var2 = this.getVisibleCell(var1);
      if (var2 != null) {
         this.scrollTo((T)var2);
      } else {
         if (this.tryScrollOneCell(var1, true)) {
            return;
         }

         if (this.tryScrollOneCell(var1, false)) {
            return;
         }

         this.adjustPositionToIndex(var1);
         this.addAllToPile();
         this.requestLayout();
      }
   }

   private boolean tryScrollOneCell(int var1, boolean var2) {
      int var3 = var2 ? -1 : 1;
      IndexedCell var4 = this.getVisibleCell(var1 + var3);
      if (var4 != null) {
         IndexedCell var5 = this.getAvailableCell(var1);
         this.setCellIndex((T)var5, var1);
         this.resizeCell((T)var5);
         this.setMaxPrefBreadth(Math.max(this.getMaxPrefBreadth(), this.getCellBreadth(var5)));
         var5.setVisible(true);
         if (var2) {
            this.cells.addLast((T)var5);
            this.scrollPixels(this.getCellLength((T)var5));
         } else {
            this.cells.addFirst((T)var5);
            this.scrollPixels(-this.getCellLength((T)var5));
         }

         return true;
      } else {
         return false;
      }
   }

   public void scrollToTop(int var1) {
      boolean var2 = false;
      if (var1 >= this.getCellCount() - 1) {
         this.setPosition(1.0);
         var2 = true;
      } else if (var1 < 0) {
         this.setPosition(0.0);
         var2 = true;
      }

      if (!var2) {
         this.adjustPositionToIndex(var1);
      }

      this.requestLayout();
   }

   public double scrollPixels(double var1) {
      if (var1 == 0.0) {
         return 0.0;
      } else {
         boolean var3 = this.isVertical();
         if ((!var3 || (this.tempVisibility ? this.needLengthBar : this.vbar.isVisible()))
            && (var3 || (this.tempVisibility ? this.needLengthBar : this.hbar.isVisible()))) {
            double var4 = this.getPosition();
            if (var4 == 0.0 && var1 < 0.0) {
               return 0.0;
            } else if (var4 == 1.0 && var1 > 0.0) {
               return 0.0;
            } else {
               this.recalculateEstimatedSize();
               double var6 = this.adjustByPixelAmount(var1);
               if (var4 == this.getPosition()) {
                  return 0.0;
               } else {
                  if (this.cells.size() > 0) {
                     for (int var8 = 0; var8 < this.cells.size(); var8++) {
                        IndexedCell var9 = this.cells.get(var8);

                        assert var9 != null;

                        this.positionCell((T)var9, this.getCellPosition((T)var9) - var1);
                     }

                     IndexedCell var23 = this.cells.getFirst();
                     double var26 = var23 == null ? 0.0 : this.getCellPosition((T)var23);

                     for (int var11 = 0; var11 < this.cells.size(); var11++) {
                        IndexedCell var12 = this.cells.get(var11);

                        assert var12 != null;

                        double var13 = this.getCellPosition((T)var12);
                        if (Math.abs(var13 - var26) > 0.001) {
                           this.positionCell((T)var12, var26);
                        }

                        var26 += this.getCellLength((T)var12);
                     }

                     this.cull();
                     var23 = this.cells.getFirst();
                     if (var23 != null) {
                        int var27 = this.getCellIndex((T)var23);
                        double var30 = this.getCellLength(var27 - 1);
                        this.addLeadingCells(var27 - 1, this.getCellPosition((T)var23) - var30);
                     } else {
                        int var28 = this.computeCurrentIndex();
                        double var31 = -this.computeViewportOffset(this.getPosition());
                        this.addLeadingCells(var28, var31);
                     }

                     if (!this.addTrailingCells(false)) {
                        IndexedCell var29 = this.getLastVisibleCell();
                        double var32 = this.getCellLength((T)var29);
                        double var14 = this.getCellPosition((T)var29) + var32;
                        double var16 = this.getViewportLength();
                        if (var14 < var16) {
                           double var18 = var16 - var14;

                           for (int var20 = 0; var20 < this.cells.size(); var20++) {
                              IndexedCell var21 = this.cells.get(var20);
                              this.positionCell((T)var21, this.getCellPosition((T)var21) + var18);
                           }

                           this.setPosition(1.0);
                           var23 = this.cells.getFirst();
                           int var33 = this.getCellIndex((T)var23);
                           double var34 = this.getCellLength(var33 - 1);
                           this.addLeadingCells(var33 - 1, this.getCellPosition((T)var23) - var34);
                        }
                     }
                  }

                  this.cull();
                  this.updateScrollBarsAndCells(false);
                  this.lastPosition = this.getPosition();
                  return var6;
               }
            }
         } else {
            return 0.0;
         }
      }
   }

   protected double computePrefWidth(double var1) {
      double var3 = this.isVertical() ? this.getPrefBreadth(var1) : this.getPrefLength();
      return var3 + this.vbar.prefWidth(-1.0);
   }

   protected double computePrefHeight(double var1) {
      double var3 = this.isVertical() ? this.getPrefLength() : this.getPrefBreadth(var1);
      return var3 + this.hbar.prefHeight(-1.0);
   }

   public T getCell(int var1) {
      if (!this.cells.isEmpty()) {
         IndexedCell var2 = this.getVisibleCell(var1);
         if (var2 != null) {
            return (T)var2;
         }
      }

      for (int var4 = 0; var4 < this.pile.size(); var4++) {
         IndexedCell var3 = this.pile.get(var4);
         if (this.getCellIndex((T)var3) == var1) {
            return (T)var3;
         }
      }

      if (this.accumCell == null) {
         Callback var5 = this.getCellFactory();
         if (var5 != null) {
            this.accumCell = (T)var5.call(this);
            this.accumCell.getProperties().put("newcell", null);
            this.accumCellParent.getChildren().setAll(new Node[]{this.accumCell});
            this.accumCell.setAccessibleRole(AccessibleRole.NODE);
            this.accumCell.getChildrenUnmodifiable().addListener(var1x -> {
               for (Node var3x : this.accumCell.getChildrenUnmodifiable()) {
                  var3x.setAccessibleRole(AccessibleRole.NODE);
               }
            });
         }
      }

      this.setCellIndex(this.accumCell, var1);
      this.resizeCell(this.accumCell);
      return this.accumCell;
   }

   protected void setCellIndex(T var1, int var2) {
      assert var1 != null;

      var1.updateIndex(var2);
      if (var1.isNeedsLayout() && var1.getScene() != null || var1.getProperties().containsKey("newcell")) {
         var1.applyCss();
         var1.getProperties().remove("newcell");
      }
   }

   protected int getCellIndex(T var1) {
      return var1.getIndex();
   }

   protected final ScrollBar getHbar() {
      return this.hbar;
   }

   protected final ScrollBar getVbar() {
      return this.vbar;
   }

   private final void setMaxPrefBreadth(double var1) {
      this.maxPrefBreadth = var1;
   }

   final double getMaxPrefBreadth() {
      return this.maxPrefBreadth;
   }

   private final void setViewportBreadth(double var1) {
      this.viewportBreadth = var1;
   }

   private final double getViewportBreadth() {
      return this.viewportBreadth;
   }

   void setViewportLength(double var1) {
      this.viewportLength = var1;
      this.absoluteOffset = this.getPosition() * (this.estimatedSize - this.viewportLength);
   }

   double getViewportLength() {
      return this.viewportLength;
   }

   double getCellLength(int var1) {
      if (this.fixedCellSizeEnabled) {
         return this.getFixedCellSize();
      } else {
         IndexedCell var2 = this.getCell(var1);
         double var3 = this.getCellLength((T)var2);
         this.releaseCell((T)var2);
         return var3;
      }
   }

   double getCellBreadth(int var1) {
      IndexedCell var2 = this.getCell(var1);
      double var3 = this.getCellBreadth(var2);
      this.releaseCell((T)var2);
      return var3;
   }

   double getCellLength(T var1) {
      if (var1 == null) {
         return 0.0;
      } else if (this.fixedCellSizeEnabled) {
         return this.getFixedCellSize();
      } else {
         return this.isVertical() ? var1.getLayoutBounds().getHeight() : var1.getLayoutBounds().getWidth();
      }
   }

   double getCellBreadth(Cell var1) {
      return this.isVertical() ? var1.prefWidth(-1.0) : var1.prefHeight(-1.0);
   }

   double getCellPosition(T var1) {
      if (var1 == null) {
         return 0.0;
      } else {
         return this.isVertical() ? var1.getLayoutY() : var1.getLayoutX();
      }
   }

   private void positionCell(T var1, double var2) {
      this.updateCellSize((T)var1);
      if (this.isVertical()) {
         var1.setLayoutX(0.0);
         var1.setLayoutY(this.snapSpaceY(var2));
      } else {
         var1.setLayoutX(this.snapSpaceX(var2));
         var1.setLayoutY(0.0);
      }
   }

   protected void resizeCell(T var1) {
      if (var1 != null) {
         if (this.isVertical()) {
            double var2 = Math.max(this.getMaxPrefBreadth(), this.getViewportBreadth());
            var1.resize(
               var2,
               this.fixedCellSizeEnabled
                  ? this.getFixedCellSize()
                  : com.sun.javafx.scene.control.skin.Utils.boundedSize(var1.prefHeight(var2), var1.minHeight(var2), var1.maxHeight(var2))
            );
         } else {
            double var4 = Math.max(this.getMaxPrefBreadth(), this.getViewportBreadth());
            var1.resize(
               this.fixedCellSizeEnabled
                  ? this.getFixedCellSize()
                  : com.sun.javafx.scene.control.skin.Utils.boundedSize(var1.prefWidth(var4), var1.minWidth(var4), var1.maxWidth(var4)),
               var4
            );
         }
      }
   }

   protected List<T> getCells() {
      return this.cells;
   }

   protected T getLastVisibleCellWithinViewport() {
      if (!this.cells.isEmpty() && !(this.getViewportLength() <= 0.0)) {
         double var2 = this.getViewportLength();

         for (int var4 = this.cells.size() - 1; var4 >= 0; var4--) {
            IndexedCell var1 = this.cells.get(var4);
            if (!var1.isEmpty()) {
               double var5 = this.getCellPosition((T)var1);
               double var7 = var5 + this.getCellLength((T)var1);
               if (var7 <= var2 + 2.0) {
                  return (T)var1;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   protected T getFirstVisibleCellWithinViewport() {
      if (!this.cells.isEmpty() && !(this.getViewportLength() <= 0.0)) {
         for (int var2 = 0; var2 < this.cells.size(); var2++) {
            IndexedCell var1 = this.cells.get(var2);
            if (!var1.isEmpty()) {
               double var3 = this.getCellPosition((T)var1);
               if (var3 >= 0.0) {
                  return (T)var1;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   void addLeadingCells(int var1, double var2) {
      double var4 = var2;
      int var6 = var1;
      boolean var7 = true;
      IndexedCell var8 = null;
      if (var1 == this.getCellCount() && var2 == this.getViewportLength()) {
         var6 = var1 - 1;
         var7 = false;
      }

      while (var6 >= 0 && (var4 > 0.0 || var7)) {
         var8 = this.getAvailableCell(var6);
         this.setCellIndex((T)var8, var6);
         this.resizeCell((T)var8);
         this.cells.addFirst((T)var8);
         if (var7) {
            var7 = false;
         } else {
            var4 -= this.getCellLength((T)var8);
         }

         this.positionCell((T)var8, var4);
         this.setMaxPrefBreadth(Math.max(this.getMaxPrefBreadth(), this.getCellBreadth(var8)));
         var8.setVisible(true);
         var6--;
      }

      if (this.cells.size() > 0) {
         var8 = this.cells.getFirst();
         int var9 = this.getCellIndex((T)var8);
         double var10 = this.getCellPosition((T)var8);
         if (var9 == 0 && var10 > 0.0) {
            this.setPosition(0.0);
            var4 = 0.0;

            for (int var12 = 0; var12 < this.cells.size(); var12++) {
               var8 = this.cells.get(var12);
               this.positionCell((T)var8, var4);
               var4 += this.getCellLength((T)var8);
            }
         }
      } else {
         this.vbar.setValue(0.0);
         this.hbar.setValue(0.0);
      }
   }

   boolean addTrailingCells(boolean var1) {
      if (this.cells.isEmpty()) {
         return false;
      } else {
         IndexedCell var2 = this.cells.getLast();
         double var3 = this.getCellPosition((T)var2) + this.getCellLength((T)var2);
         int var5 = this.getCellIndex((T)var2) + 1;
         int var6 = this.getCellCount();
         boolean var7 = var5 <= var6;
         double var8 = this.getViewportLength();
         if (var3 < 0.0 && !var1) {
            return false;
         } else {
            for (double var10 = var8; var3 < var8; var5++) {
               if (var5 >= var6) {
                  if (var3 < var8) {
                     var7 = false;
                  }

                  if (!var1) {
                     return var7;
                  }

                  if ((double)var5 > var10) {
                     PlatformLogger var27 = Logging.getControlsLogger();
                     if (var27.isLoggable(Level.INFO)) {
                        var27.info("index exceeds maxCellCount. Check size calculations for " + var2.getClass());
                     }

                     return var7;
                  }
               }

               IndexedCell var12 = this.getAvailableCell(var5);
               this.setCellIndex((T)var12, var5);
               this.resizeCell((T)var12);
               this.cells.addLast((T)var12);
               this.positionCell((T)var12, var3);
               this.setMaxPrefBreadth(Math.max(this.getMaxPrefBreadth(), this.getCellBreadth(var12)));
               var3 += this.getCellLength((T)var12);
               var12.setVisible(true);
            }

            IndexedCell var28 = this.cells.getFirst();
            var5 = this.getCellIndex((T)var28);
            IndexedCell var13 = this.getLastVisibleCell();
            double var14 = this.getCellPosition((T)var28);
            double var16 = this.getCellPosition((T)var13) + this.getCellLength((T)var13);
            if ((var5 != 0 || var5 == 0 && var14 < 0.0) && var1 && var13 != null && this.getCellIndex((T)var13) == var6 - 1 && var16 < var8) {
               double var18 = var16;
               double var20 = var8 - var16;

               while (var18 < var8 && var5 != 0 && -var14 < var20) {
                  IndexedCell var22 = this.getAvailableCell(--var5);
                  this.setCellIndex((T)var22, var5);
                  this.resizeCell((T)var22);
                  this.cells.addFirst((T)var22);
                  double var23 = this.getCellLength((T)var22);
                  var14 -= var23;
                  var18 += var23;
                  this.positionCell((T)var22, var14);
                  this.setMaxPrefBreadth(Math.max(this.getMaxPrefBreadth(), this.getCellBreadth(var22)));
                  var22.setVisible(true);
               }

               var28 = this.cells.getFirst();
               var14 = this.getCellPosition((T)var28);
               double var32 = var8 - var16;
               if (this.getCellIndex((T)var28) == 0 && var32 > -var14) {
                  var32 = -var14;
               }

               for (int var24 = 0; var24 < this.cells.size(); var24++) {
                  IndexedCell var25 = this.cells.get(var24);
                  this.positionCell((T)var25, this.getCellPosition((T)var25) + var32);
               }

               var14 = this.getCellPosition((T)var28);
               if (this.getCellIndex((T)var28) == 0 && var14 == 0.0) {
                  this.setPosition(0.0);
               } else if (this.getPosition() != 1.0) {
                  this.setPosition(1.0);
               }
            }

            return var7;
         }
      }
   }

   protected void reconfigureCells() {
      this.needsReconfigureCells = true;
      this.requestLayout();
   }

   protected void recreateCells() {
      this.needsRecreateCells = true;
      this.requestLayout();
   }

   protected void rebuildCells() {
      this.needsRebuildCells = true;
      this.requestLayout();
   }

   protected void requestCellLayout() {
      this.needsCellsLayout = true;
      this.requestLayout();
   }

   void setCellDirty(int var1) {
      this.dirtyCells.set(var1);
      this.requestLayout();
   }

   private void startSBReleasedAnimation() {
      if (this.sbTouchTimeline == null) {
         this.sbTouchTimeline = new Timeline();
         this.sbTouchKF1 = new KeyFrame(Duration.millis(0.0), var1 -> {
            this.tempVisibility = true;
            this.requestLayout();
         }, new KeyValue[0]);
         this.sbTouchKF2 = new KeyFrame(Duration.millis(1000.0), var1 -> {
            if (!this.touchDetected && !this.mouseDown) {
               this.tempVisibility = false;
               this.requestLayout();
            }
         }, new KeyValue[0]);
         this.sbTouchTimeline.getKeyFrames().addAll(new KeyFrame[]{this.sbTouchKF1, this.sbTouchKF2});
      }

      this.sbTouchTimeline.playFromStart();
   }

   private void scrollBarOn() {
      this.tempVisibility = true;
      this.requestLayout();
   }

   void updateHbar() {
      if (this.isVisible() && this.getScene() != null) {
         if (this.isVertical()) {
            if (this.hbar.isVisible()) {
               this.clipView.setClipX(this.hbar.getValue());
            } else {
               this.clipView.setClipX(0.0);
               this.hbar.setValue(0.0);
            }
         }
      }
   }

   private boolean computeBarVisiblity() {
      if (this.cells.isEmpty()) {
         this.needLengthBar = false;
         this.needBreadthBar = false;
         return true;
      } else {
         boolean var1 = this.isVertical();
         boolean var2 = false;
         VirtualScrollBar var3 = var1 ? this.hbar : this.vbar;
         VirtualScrollBar var4 = var1 ? this.vbar : this.hbar;
         double var5 = this.getViewportBreadth();
         int var7 = this.cells.size();
         int var8 = this.getCellCount();

         for (int var9 = 0; var9 < 2; var9++) {
            boolean var10 = this.getPosition() > 0.0
               || var8 > var7
               || var8 == var7 && this.getCellPosition(this.cells.getLast()) + this.getCellLength(this.cells.getLast()) > this.getViewportLength()
               || var8 == var7 - 1 && var2 && this.needBreadthBar;
            if (var10 ^ this.needLengthBar) {
               this.needLengthBar = var10;
               var2 = true;
            }

            boolean var11 = this.maxPrefBreadth > var5;
            if (var11 ^ this.needBreadthBar) {
               this.needBreadthBar = var11;
               var2 = true;
            }
         }

         if (!Properties.IS_TOUCH_SUPPORTED) {
            this.updateViewportDimensions();
            var3.setVisible(this.needBreadthBar);
            var4.setVisible(this.needLengthBar);
         } else {
            var3.setVisible(this.needBreadthBar && this.tempVisibility);
            var4.setVisible(this.needLengthBar && this.tempVisibility);
         }

         return var2;
      }
   }

   private void updateViewportDimensions() {
      boolean var1 = this.isVertical();
      double var2 = var1 ? this.snapSizeY(this.hbar.prefHeight(-1.0)) : this.snapSizeX(this.vbar.prefWidth(-1.0));
      double var4 = var1 ? this.snapSizeX(this.vbar.prefWidth(-1.0)) : this.snapSizeY(this.hbar.prefHeight(-1.0));
      if (!Properties.IS_TOUCH_SUPPORTED) {
         this.setViewportBreadth((var1 ? this.getWidth() : this.getHeight()) - (this.needLengthBar ? var4 : 0.0));
         this.setViewportLength((var1 ? this.getHeight() : this.getWidth()) - (this.needBreadthBar ? var2 : 0.0));
      } else {
         this.setViewportBreadth(var1 ? this.getWidth() : this.getHeight());
         this.setViewportLength(var1 ? this.getHeight() : this.getWidth());
      }
   }

   private void initViewport() {
      boolean var1 = this.isVertical();
      this.updateViewportDimensions();
      VirtualScrollBar var2 = var1 ? this.hbar : this.vbar;
      VirtualScrollBar var3 = var1 ? this.vbar : this.hbar;
      var2.setVirtual(false);
      var3.setVirtual(true);
   }

   private void updateScrollBarsAndCells(boolean var1) {
      boolean var2 = this.isVertical();
      VirtualScrollBar var3 = var2 ? this.hbar : this.vbar;
      VirtualScrollBar var4 = var2 ? this.vbar : this.hbar;
      this.fitCells();
      if (!this.cells.isEmpty()) {
         double var5 = -this.computeViewportOffset(this.getPosition());
         int var7 = this.computeCurrentIndex() - this.cells.getFirst().getIndex();
         int var8 = this.cells.size();
         double var9 = var5;

         for (int var11 = var7 - 1; var11 >= 0 && var11 < var8; var11--) {
            IndexedCell var12 = this.cells.get(var11);
            var9 -= this.getCellLength((T)var12);
            this.positionCell((T)var12, var9);
         }

         var9 = var5;

         for (int var22 = var7; var22 >= 0 && var22 < var8; var22++) {
            IndexedCell var24 = this.cells.get(var22);
            this.positionCell((T)var24, var9);
            var9 += this.getCellLength((T)var24);
         }
      }

      this.corner.setVisible(var3.isVisible() && var4.isVisible());
      double var18 = 0.0;
      double var19 = (var2 ? this.getHeight() : this.getWidth()) - (var3.isVisible() ? var3.prefHeight(-1.0) : 0.0);
      double var21 = this.getViewportBreadth();
      double var23 = this.getViewportLength();
      if (var3.isVisible()) {
         if (!Properties.IS_TOUCH_SUPPORTED) {
            if (var2) {
               this.hbar.resizeRelocate(0.0, var23, var21, this.hbar.prefHeight(var21));
            } else {
               this.vbar.resizeRelocate(var23, 0.0, this.vbar.prefWidth(var21), var21);
            }
         } else if (var2) {
            double var13 = this.hbar.prefHeight(var21);
            this.hbar.resizeRelocate(0.0, var23 - var13, var21, var13);
         } else {
            double var25 = this.vbar.prefWidth(var21);
            this.vbar.resizeRelocate(var23 - var25, 0.0, var25, var21);
         }

         if (this.getMaxPrefBreadth() != -1.0) {
            double var26 = Math.max(1.0, this.getMaxPrefBreadth() - var21);
            if (var26 != var3.getMax()) {
               var3.setMax(var26);
               double var15 = var3.getValue();
               boolean var17 = var15 != 0.0 && var26 == var15;
               if (var17 || var15 > var26) {
                  var3.setValue(var26);
               }

               var3.setVisibleAmount(var21 / this.getMaxPrefBreadth() * var26);
            }
         }
      }

      if (var1 && (var4.isVisible() || Properties.IS_TOUCH_SUPPORTED)) {
         int var27 = this.getCellCount();
         int var14 = 0;
         int var30 = 0;

         for (int var16 = this.cells.size(); var30 < var16; var30++) {
            IndexedCell var31 = this.cells.get(var30);
            if (var31 != null && !var31.isEmpty()) {
               var18 += var2 ? var31.getHeight() : var31.getWidth();
               if (var18 > var19) {
                  break;
               }

               var14++;
            }
         }

         var4.setMax(1.0);
         if (var14 == 0 && var27 == 1) {
            var4.setVisibleAmount(var19 / var18);
         } else {
            var4.setVisibleAmount(var23 / this.estimatedSize);
         }
      }

      if (var4.isVisible()) {
         if (!Properties.IS_TOUCH_SUPPORTED) {
            if (var2) {
               this.vbar.resizeRelocate(var21, 0.0, this.vbar.prefWidth(var23), var23);
            } else {
               this.hbar.resizeRelocate(0.0, var21, var23, this.hbar.prefHeight(-1.0));
            }
         } else if (var2) {
            double var28 = this.vbar.prefWidth(var23);
            this.vbar.resizeRelocate(var21 - var28, 0.0, var28, var23);
         } else {
            double var29 = this.hbar.prefHeight(-1.0);
            this.hbar.resizeRelocate(0.0, var21 - var29, var23, var29);
         }
      }

      if (this.corner.isVisible()) {
         if (!Properties.IS_TOUCH_SUPPORTED) {
            this.corner.resize(this.vbar.getWidth(), this.hbar.getHeight());
            this.corner.relocate(this.hbar.getLayoutX() + this.hbar.getWidth(), this.vbar.getLayoutY() + this.vbar.getHeight());
         } else {
            this.corner.resize(this.vbar.getWidth(), this.hbar.getHeight());
            this.corner
               .relocate(
                  this.hbar.getLayoutX() + (this.hbar.getWidth() - this.vbar.getWidth()),
                  this.vbar.getLayoutY() + (this.vbar.getHeight() - this.hbar.getHeight())
               );
            this.hbar.resize(this.hbar.getWidth() - this.vbar.getWidth(), this.hbar.getHeight());
            this.vbar.resize(this.vbar.getWidth(), this.vbar.getHeight() - this.hbar.getHeight());
         }
      }

      this.clipView.resize(this.snapSizeX(var2 ? var21 : var23), this.snapSizeY(var2 ? var23 : var21));
      if (this.getPosition() != var4.getValue()) {
         var4.setValue(this.getPosition());
      }
   }

   private void fitCells() {
      double var1 = Math.max(this.getMaxPrefBreadth(), this.getViewportBreadth());
      boolean var3 = this.isVertical();

      for (int var4 = 0; var4 < this.cells.size(); var4++) {
         Cell var5 = this.cells.get(var4);
         if (var3) {
            var5.resize(var1, var5.prefHeight(var1));
         } else {
            var5.resize(var5.prefWidth(var1), var1);
         }
      }
   }

   private void cull() {
      double var1 = this.getViewportLength();

      for (int var3 = this.cells.size() - 1; var3 >= 0; var3--) {
         IndexedCell var4 = this.cells.get(var3);
         double var5 = this.getCellLength((T)var4);
         double var7 = this.getCellPosition((T)var4);
         double var9 = var7 + var5;
         if (var7 >= var1 || var9 < 0.0) {
            this.addToPile(this.cells.remove(var3));
         }
      }
   }

   private void releaseCell(T var1) {
      if (this.accumCell != null && var1 == this.accumCell) {
         this.accumCell.setVisible(false);
         this.accumCell.updateIndex(-1);
      }
   }

   protected T getPrivateCell(int var1) {
      IndexedCell var2 = null;
      if (!this.cells.isEmpty()) {
         var2 = this.getVisibleCell(var1);
         if (var2 != null) {
            var2.layout();
            return (T)var2;
         }
      }

      if (var2 == null) {
         for (int var3 = 0; var3 < this.sheetChildren.size(); var3++) {
            IndexedCell var4 = (IndexedCell)this.sheetChildren.get(var3);
            if (this.getCellIndex((T)var4) == var1) {
               return (T)var4;
            }
         }
      }

      Callback var5 = this.getCellFactory();
      if (var5 != null) {
         var2 = (IndexedCell)var5.call(this);
      }

      if (var2 != null) {
         this.setCellIndex((T)var2, var1);
         this.resizeCell((T)var2);
         var2.setVisible(false);
         this.sheetChildren.add(var2);
         this.privateCells.add((T)var2);
      }

      return (T)var2;
   }

   private void releaseAllPrivateCells() {
      this.sheetChildren.removeAll(this.privateCells);
      this.privateCells.clear();
   }

   private void addToPile(T var1) {
      assert var1 != null;

      this.pile.addLast((T)var1);
   }

   private void cleanPile() {
      boolean var1 = false;
      int var2 = 0;

      for (int var3 = this.pile.size(); var2 < var3; var2++) {
         IndexedCell var4 = this.pile.get(var2);
         var1 = var1 || this.doesCellContainFocus(var4);
         var4.setVisible(false);
      }

      if (var1) {
         this.requestFocus();
      }
   }

   private boolean doesCellContainFocus(Cell<?> var1) {
      Scene var2 = var1.getScene();
      Node var3 = var2 == null ? null : var2.getFocusOwner();
      if (var3 != null) {
         if (var1.equals(var3)) {
            return true;
         }

         for (Parent var4 = var3.getParent(); var4 != null && !(var4 instanceof VirtualFlow); var4 = var4.getParent()) {
            if (var1.equals(var4)) {
               return true;
            }
         }
      }

      return false;
   }

   private double getPrefBreadth(double var1) {
      double var3 = this.getMaxCellWidth(10);
      if (var1 > -1.0) {
         double var5 = this.getPrefLength();
         var3 = Math.max(var3, var5 * 0.618033987);
      }

      return var3;
   }

   private double getPrefLength() {
      double var1 = 0.0;
      int var3 = Math.min(10, this.getCellCount());

      for (int var4 = 0; var4 < var3; var4++) {
         var1 += this.getCellLength(var4);
      }

      return var1;
   }

   double getMaxCellWidth(int var1) {
      double var2 = 0.0;
      int var4 = Math.max(1, var1 == -1 ? this.getCellCount() : var1);

      for (int var5 = 0; var5 < var4; var5++) {
         var2 = Math.max(var2, this.getCellBreadth(var5));
      }

      return var2;
   }

   private double computeViewportOffset(double var1) {
      double var3 = Utils.clamp(0.0, var1, 1.0);
      double var5 = 0.0;
      double var7 = this.estimatedSize / (double)this.getCellCount();

      for (int var9 = 0; var9 < this.getCellCount(); var9++) {
         double var10 = this.getCellSize(var9);
         if (var10 < 0.0) {
            var10 = var7;
         }

         if (var5 + var10 > this.absoluteOffset) {
            return this.absoluteOffset - var5;
         }

         var5 += var10;
      }

      return 0.0;
   }

   private void adjustPositionToIndex(int var1) {
      int var2 = this.getCellCount();
      if (var2 <= 0) {
         this.setPosition(0.0);
      } else {
         double var3 = 0.0;
         double var5 = this.estimatedSize / (double)var2;

         for (int var7 = 0; var7 < var1; var7++) {
            double var8 = this.getCellSize(var7);
            if (var8 < 0.0) {
               var8 = var5;
            }

            var3 += var8;
         }

         this.absoluteOffset = var3;
         this.adjustPosition();
      }
   }

   private double adjustByPixelAmount(double var1) {
      if (var1 == 0.0) {
         return 0.0;
      } else if (this.absoluteOffset <= 0.0 && var1 < 0.0) {
         return 0.0;
      } else {
         double var3 = this.absoluteOffset;
         this.absoluteOffset = Math.max(0.0, this.absoluteOffset + var1);
         double var5 = Math.min(1.0, this.absoluteOffset / (this.estimatedSize - this.viewportLength));
         if (var1 > 0.0 && var5 < this.getPosition()) {
            var5 = this.getPosition() * 1.01;
         }

         if (var1 < 0.0 && var5 > this.getPosition()) {
            var5 = this.getPosition() * 0.99;
         }

         if (var5 > 0.95) {
            for (int var7 = this.computeCurrentIndex(); var7 < this.getCellCount(); var7++) {
               this.getOrCreateCellSize(var7);
            }

            this.recalculateEstimatedSize();
         }

         if (var5 >= 1.0) {
            this.absoluteOffset = this.estimatedSize - this.viewportLength;
         }

         this.setPosition(var5);
         return this.absoluteOffset - var3;
      }
   }

   private int computeCurrentIndex() {
      double var1 = 0.0;
      int var3 = this.getCellCount();
      double var4 = this.estimatedSize / (double)var3;

      for (int var6 = 0; var6 < var3; var6++) {
         double var7 = this.getCellSize(var6);
         if (var7 < 0.0) {
            var7 = var4;
         }

         var1 += var7;
         if (var1 > this.absoluteOffset) {
            return var6;
         }
      }

      return var3 == 0 ? 0 : var3 - 1;
   }

   private double computeOffsetForCell(int var1) {
      double var2 = (double)this.getCellCount();
      double var4 = Utils.clamp(0.0, (double)var1, var2) / var2;
      return -(this.getViewportLength() * var4);
   }

   double getCellSize(int var1) {
      return this.getOrCreateCellSize(var1, false);
   }

   double getOrCreateCellSize(int var1) {
      return this.getOrCreateCellSize(var1, true);
   }

   private double getOrCreateCellSize(int var1, boolean var2) {
      if (this.itemSizeCache.size() > var1 && this.itemSizeCache.get(var1) != null) {
         return this.itemSizeCache.get(var1);
      } else if (!var2) {
         return -1.0;
      } else {
         boolean var3 = false;
         IndexedCell var4 = this.getVisibleCell(var1);
         if (var4 == null) {
            var4 = this.getCell(var1);
            var3 = true;
         }

         while (var1 >= this.itemSizeCache.size()) {
            this.itemSizeCache.add(this.itemSizeCache.size(), null);
         }

         double var5 = 1.0;
         if (this.isVertical()) {
            var5 = var4.getLayoutBounds().getHeight();
         } else {
            var5 = var4.getLayoutBounds().getWidth();
         }

         this.itemSizeCache.set(var1, var5);
         if (var3) {
            this.releaseCell((T)var4);
         }

         return var5;
      }
   }

   void updateCellSize(T var1) {
      int var2 = var1.getIndex();
      if (this.itemSizeCache.size() > var2) {
         if (this.isVertical()) {
            double var3 = var1.getLayoutBounds().getHeight();
            this.itemSizeCache.set(var2, var3);
         } else {
            double var5 = var1.getLayoutBounds().getWidth();
            this.itemSizeCache.set(var2, var5);
         }
      }
   }

   private void recalculateEstimatedSize() {
      this.recalculateAndImproveEstimatedSize(2);
   }

   private void recalculateAndImproveEstimatedSize(int var1) {
      int var2 = this.getCellCount();
      int var3 = this.itemSizeCache.size();

      for (int var4 = 0; var2 > this.itemSizeCache.size() && var4 < var1; var4++) {
         this.getOrCreateCellSize(this.itemSizeCache.size());
      }

      var3 = this.itemSizeCache.size();
      int var5 = 0;
      double var6 = 0.0;

      for (int var8 = 0; var8 < var2 && var8 < var3; var8++) {
         Double var9 = this.itemSizeCache.get(var8);
         if (var9 != null) {
            var6 += var9;
            var5++;
         }
      }

      this.estimatedSize = var5 == 0 ? 1.0 : var6 * (double)var2 / (double)var5;
   }

   private void resetSizeEstimates() {
      this.itemSizeCache.clear();
      this.estimatedSize = 1.0;
   }

   static class ArrayLinkedList<T> extends AbstractList<T> {
      private final ArrayList<T> array;
      private int firstIndex = -1;
      private int lastIndex = -1;

      public ArrayLinkedList() {
         this.array = new ArrayList<>(50);

         for (int var1 = 0; var1 < 50; var1++) {
            this.array.add(null);
         }
      }

      @Override
      public T getFirst() {
         return this.firstIndex == -1 ? null : this.array.get(this.firstIndex);
      }

      @Override
      public T getLast() {
         return this.lastIndex == -1 ? null : this.array.get(this.lastIndex);
      }

      @Override
      public void addFirst(T var1) {
         if (this.firstIndex == -1) {
            this.firstIndex = this.lastIndex = this.array.size() / 2;
            this.array.set(this.firstIndex, (T)var1);
         } else if (this.firstIndex == 0) {
            this.array.add(0, (T)var1);
            this.lastIndex++;
         } else {
            this.array.set(--this.firstIndex, (T)var1);
         }
      }

      @Override
      public void addLast(T var1) {
         if (this.firstIndex == -1) {
            this.firstIndex = this.lastIndex = this.array.size() / 2;
            this.array.set(this.lastIndex, (T)var1);
         } else if (this.lastIndex == this.array.size() - 1) {
            this.array.add(++this.lastIndex, (T)var1);
         } else {
            this.array.set(++this.lastIndex, (T)var1);
         }
      }

      @Override
      public int size() {
         return this.firstIndex == -1 ? 0 : this.lastIndex - this.firstIndex + 1;
      }

      @Override
      public boolean isEmpty() {
         return this.firstIndex == -1;
      }

      @Override
      public T get(int var1) {
         return var1 <= this.lastIndex - this.firstIndex && var1 >= 0 ? this.array.get(this.firstIndex + var1) : null;
      }

      @Override
      public void clear() {
         for (int var1 = 0; var1 < this.array.size(); var1++) {
            this.array.set(var1, null);
         }

         this.firstIndex = this.lastIndex = -1;
      }

      @Override
      public T removeFirst() {
         return this.isEmpty() ? null : this.remove(0);
      }

      @Override
      public T removeLast() {
         return this.isEmpty() ? null : this.remove(this.lastIndex - this.firstIndex);
      }

      @Override
      public T remove(int var1) {
         if (var1 > this.lastIndex - this.firstIndex || var1 < 0) {
            throw new ArrayIndexOutOfBoundsException();
         } else if (var1 == 0) {
            Object var5 = this.array.get(this.firstIndex);
            this.array.set(this.firstIndex, null);
            if (this.firstIndex == this.lastIndex) {
               this.firstIndex = this.lastIndex = -1;
            } else {
               this.firstIndex++;
            }

            return (T)var5;
         } else if (var1 == this.lastIndex - this.firstIndex) {
            Object var4 = this.array.get(this.lastIndex);
            this.array.set(this.lastIndex--, null);
            return (T)var4;
         } else {
            Object var2 = this.array.get(this.firstIndex + var1);
            this.array.set(this.firstIndex + var1, null);

            for (int var3 = this.firstIndex + var1 + 1; var3 <= this.lastIndex; var3++) {
               this.array.set(var3 - 1, this.array.get(var3));
            }

            this.array.set(this.lastIndex--, null);
            return (T)var2;
         }
      }
   }

   static class ClippedContainer extends Region {
      private Node node;
      private final Rectangle clipRect;

      public Node getNode() {
         return this.node;
      }

      public void setNode(Node var1) {
         this.node = var1;
         this.getChildren().clear();
         this.getChildren().add(this.node);
      }

      public void setClipX(double var1) {
         this.setLayoutX(-var1);
         this.clipRect.setLayoutX(var1);
      }

      public void setClipY(double var1) {
         this.setLayoutY(-var1);
         this.clipRect.setLayoutY(var1);
      }

      public ClippedContainer(VirtualFlow<?> var1) {
         if (var1 == null) {
            throw new IllegalArgumentException("VirtualFlow can not be null");
         } else {
            this.getStyleClass().add("clipped-container");
            this.clipRect = new Rectangle();
            this.clipRect.setSmooth(false);
            this.setClip(this.clipRect);
            super.widthProperty().addListener(var1x -> this.clipRect.setWidth(this.getWidth()));
            super.heightProperty().addListener(var1x -> this.clipRect.setHeight(this.getHeight()));
         }
      }
   }
}
