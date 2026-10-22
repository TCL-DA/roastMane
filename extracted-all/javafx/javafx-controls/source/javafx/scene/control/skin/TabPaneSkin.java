package javafx.scene.control.skin;

import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.TabObservableList;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import com.sun.javafx.util.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.Animation.Status;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;

public class TabPaneSkin extends SkinBase<TabPane> {
   static int CLOSE_BTN_SIZE = 16;
   private static final double ANIMATION_SPEED = 150.0;
   private static final int SPACER = 10;
   private TabPaneSkin.TabHeaderArea tabHeaderArea;
   private ObservableList<TabPaneSkin.TabContentRegion> tabContentRegions;
   private Rectangle clipRect;
   private Rectangle tabHeaderAreaClipRect;
   private Tab selectedTab;
   private final TabPaneBehavior behavior;
   private ObjectProperty<TabPaneSkin.TabAnimation> openTabAnimation = new StyleableObjectProperty<TabPaneSkin.TabAnimation>(TabPaneSkin.TabAnimation.GROW) {
      public CssMetaData<TabPane, TabPaneSkin.TabAnimation> getCssMetaData() {
         return TabPaneSkin.StyleableProperties.OPEN_TAB_ANIMATION;
      }

      public Object getBean() {
         return TabPaneSkin.this;
      }

      public String getName() {
         return "openTabAnimation";
      }
   };
   private ObjectProperty<TabPaneSkin.TabAnimation> closeTabAnimation = new StyleableObjectProperty<TabPaneSkin.TabAnimation>(TabPaneSkin.TabAnimation.GROW) {
      public CssMetaData<TabPane, TabPaneSkin.TabAnimation> getCssMetaData() {
         return TabPaneSkin.StyleableProperties.CLOSE_TAB_ANIMATION;
      }

      public Object getBean() {
         return TabPaneSkin.this;
      }

      public String getName() {
         return "closeTabAnimation";
      }
   };
   private SelectionModel<Tab> selectionModel;
   private InvalidationListener selectionChangeListener = var1x -> {
      this.tabHeaderArea.invalidateScrollOffset();
      this.selectedTab = this.getSkinnable().getSelectionModel().getSelectedItem();
      this.getSkinnable().requestLayout();
   };
   private WeakInvalidationListener weakSelectionChangeListener = new WeakInvalidationListener(this.selectionChangeListener);
   ListChangeListener<Tab> tabsListener;
   WeakListChangeListener<Tab> weakTabsListener;
   private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");
   private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
   private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
   private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
   private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");
   private static final PseudoClass DISABLED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("disabled");
   private EventHandler<MouseEvent> headerDraggedHandler = this::handleHeaderDragged;
   private EventHandler<MouseEvent> headerMousePressedHandler = this::handleHeaderMousePressed;
   private EventHandler<MouseEvent> headerMouseReleasedHandler = this::handleHeaderMouseReleased;
   private int dragTabHeaderStartIndex;
   private int dragTabHeaderIndex;
   private TabPaneSkin.TabHeaderSkin dragTabHeader;
   private TabPaneSkin.TabHeaderSkin dropTabHeader;
   private StackPane headersRegion;
   private TabPaneSkin.DragState dragState;
   private final int MIN_TO_MAX = 1;
   private final int MAX_TO_MIN = -1;
   private int xLayoutDirection;
   private double dragEventPrevLoc;
   private int prevDragDirection = 1;
   private final double DRAG_DIST_THRESHOLD = 0.75;
   private final double ANIM_DURATION = 120.0;
   private TabPaneSkin.TabHeaderSkin dropAnimHeader;
   private double dropHeaderSourceX;
   private double dropHeaderTransitionX;
   private final Animation dropHeaderAnim = new Transition() {
      {
         this.setInterpolator(Interpolator.EASE_BOTH);
         this.setCycleDuration(Duration.millis(120.0));
         this.setOnFinished(var1x -> TabPaneSkin.this.completeHeaderReordering());
      }

      protected void interpolate(double var1) {
         TabPaneSkin.this.dropAnimHeader.setLayoutX(TabPaneSkin.this.dropHeaderSourceX + TabPaneSkin.this.dropHeaderTransitionX * var1);
      }
   };
   private double dragHeaderDestX;
   private double dragHeaderSourceX;
   private double dragHeaderTransitionX;
   private final Animation dragHeaderAnim = new Transition() {
      {
         this.setInterpolator(Interpolator.EASE_OUT);
         this.setCycleDuration(Duration.millis(120.0));
         this.setOnFinished(var1x -> {
            TabPaneSkin.this.reorderTabs();
            TabPaneSkin.this.resetDrag();
         });
      }

      protected void interpolate(double var1) {
         TabPaneSkin.this.dragTabHeader.setLayoutX(TabPaneSkin.this.dragHeaderSourceX + TabPaneSkin.this.dragHeaderTransitionX * var1);
      }
   };
   private ListChangeListener childListener = new ListChangeListener<Node>() {
      public void onChanged(Change<? extends Node> var1) {
         while (var1.next()) {
            if (var1.wasAdded()) {
               for (Node var3 : var1.getAddedSubList()) {
                  TabPaneSkin.this.addReorderListeners(var3);
               }
            }

            if (var1.wasRemoved()) {
               for (Node var5 : var1.getRemoved()) {
                  TabPaneSkin.this.removeReorderListeners(var5);
               }
            }
         }
      }
   };

   public TabPaneSkin(TabPane var1) {
      super(var1);
      this.behavior = new TabPaneBehavior(var1);
      this.clipRect = new Rectangle(var1.getWidth(), var1.getHeight());
      this.getSkinnable().setClip(this.clipRect);
      this.tabContentRegions = FXCollections.observableArrayList();

      for (Tab var3 : this.getSkinnable().getTabs()) {
         this.addTabContent(var3);
      }

      this.tabHeaderAreaClipRect = new Rectangle();
      this.tabHeaderArea = new TabPaneSkin.TabHeaderArea();
      this.tabHeaderArea.setClip(this.tabHeaderAreaClipRect);
      this.getChildren().add(this.tabHeaderArea);
      if (this.getSkinnable().getTabs().size() == 0) {
         this.tabHeaderArea.setVisible(false);
      }

      this.initializeTabListener();
      this.updateSelectionModel();
      this.registerChangeListener(var1.selectionModelProperty(), var1x -> this.updateSelectionModel());
      this.registerChangeListener(var1.sideProperty(), var1x -> this.updateTabPosition());
      this.registerChangeListener(var1.widthProperty(), var1x -> {
         this.tabHeaderArea.invalidateScrollOffset();
         this.clipRect.setWidth(this.getSkinnable().getWidth());
      });
      this.registerChangeListener(var1.heightProperty(), var1x -> {
         this.tabHeaderArea.invalidateScrollOffset();
         this.clipRect.setHeight(this.getSkinnable().getHeight());
      });
      this.selectedTab = this.getSkinnable().getSelectionModel().getSelectedItem();
      if (this.selectedTab == null && this.getSkinnable().getSelectionModel().getSelectedIndex() != -1) {
         this.getSkinnable().getSelectionModel().select(this.getSkinnable().getSelectionModel().getSelectedIndex());
         this.selectedTab = this.getSkinnable().getSelectionModel().getSelectedItem();
      }

      if (this.selectedTab == null) {
         this.getSkinnable().getSelectionModel().selectFirst();
      }

      this.selectedTab = this.getSkinnable().getSelectionModel().getSelectedItem();
      this.initializeSwipeHandlers();
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         if (this.selectionModel != null) {
            this.selectionModel.selectedItemProperty().removeListener(this.weakSelectionChangeListener);
            this.selectionModel = null;
         }

         this.getSkinnable().getTabs().removeListener(this.weakTabsListener);
         this.tabHeaderArea.dispose();
         this.getChildren().remove(this.tabHeaderArea);

         for (Tab var2 : this.getSkinnable().getTabs()) {
            this.removeTabContent(var2);
         }

         super.dispose();
         if (this.behavior != null) {
            this.behavior.dispose();
         }
      }
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;

      for (TabPaneSkin.TabContentRegion var14 : this.tabContentRegions) {
         var11 = Math.max(var11, this.snapSizeX(var14.prefWidth(-1.0)));
      }

      boolean var18 = this.isHorizontal();
      double var19 = var18 ? this.snapSizeX(this.tabHeaderArea.prefWidth(-1.0)) : this.snapSizeY(this.tabHeaderArea.prefHeight(-1.0));
      double var16 = var18 ? Math.max(var11, var19) : var11 + var19;
      return this.snapSizeX(var16) + var5 + var9;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;

      for (TabPaneSkin.TabContentRegion var14 : this.tabContentRegions) {
         var11 = Math.max(var11, this.snapSizeY(var14.prefHeight(-1.0)));
      }

      boolean var18 = this.isHorizontal();
      double var19 = var18 ? this.snapSizeY(this.tabHeaderArea.prefHeight(-1.0)) : this.snapSizeX(this.tabHeaderArea.prefWidth(-1.0));
      double var16 = var18 ? var11 + this.snapSizeY(var19) : Math.max(var11, var19);
      return this.snapSizeY(var16) + var3 + var7;
   }

   @Override
   public double computeBaselineOffset(double var1, double var3, double var5, double var7) {
      Side var9 = this.getSkinnable().getSide();
      return var9 == Side.TOP ? this.tabHeaderArea.getBaselineOffset() + var1 : 0.0;
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      TabPane var9 = this.getSkinnable();
      Side var10 = var9.getSide();
      double var11 = var10.isHorizontal() ? this.snapSizeY(this.tabHeaderArea.prefHeight(-1.0)) : this.snapSizeX(this.tabHeaderArea.prefHeight(-1.0));
      double var13 = var10.equals(Side.RIGHT) ? var1 + var5 - var11 : var1;
      double var15 = var10.equals(Side.BOTTOM) ? var3 + var7 - var11 : var3;
      double var17 = this.snappedLeftInset();
      double var19 = this.snappedTopInset();
      if (var10 == Side.TOP) {
         this.tabHeaderArea.resize(var5, var11);
         this.tabHeaderArea.relocate(var13, var15);
         this.tabHeaderArea.getTransforms().clear();
         this.tabHeaderArea.getTransforms().add(new Rotate((double)getRotation(Side.TOP)));
      } else if (var10 == Side.BOTTOM) {
         this.tabHeaderArea.resize(var5, var11);
         this.tabHeaderArea.relocate(var5 + var17, var15 - var11);
         this.tabHeaderArea.getTransforms().clear();
         this.tabHeaderArea.getTransforms().add(new Rotate((double)getRotation(Side.BOTTOM), 0.0, var11));
      } else if (var10 == Side.LEFT) {
         this.tabHeaderArea.resize(var7, var11);
         this.tabHeaderArea.relocate(var13 + var11, var7 - var11 + var19);
         this.tabHeaderArea.getTransforms().clear();
         this.tabHeaderArea.getTransforms().add(new Rotate((double)getRotation(Side.LEFT), 0.0, var11));
      } else if (var10 == Side.RIGHT) {
         this.tabHeaderArea.resize(var7, var11);
         this.tabHeaderArea.relocate(var13, var3 - var11);
         this.tabHeaderArea.getTransforms().clear();
         this.tabHeaderArea.getTransforms().add(new Rotate((double)getRotation(Side.RIGHT), 0.0, var11));
      }

      this.tabHeaderAreaClipRect.setX(0.0);
      this.tabHeaderAreaClipRect.setY(0.0);
      if (this.isHorizontal()) {
         this.tabHeaderAreaClipRect.setWidth(var5);
      } else {
         this.tabHeaderAreaClipRect.setWidth(var7);
      }

      this.tabHeaderAreaClipRect.setHeight(var11);
      double var21 = 0.0;
      double var23 = 0.0;
      if (var10 == Side.TOP) {
         var21 = var1;
         var23 = var3 + var11;
         if (this.isFloatingStyleClass()) {
            var23--;
         }
      } else if (var10 == Side.BOTTOM) {
         var21 = var1;
         var23 = var3 + var19;
         if (this.isFloatingStyleClass()) {
            var23 = 1.0 + var19;
         }
      } else if (var10 == Side.LEFT) {
         var21 = var1 + var11;
         var23 = var3;
         if (this.isFloatingStyleClass()) {
            var21--;
         }
      } else if (var10 == Side.RIGHT) {
         var21 = var1 + var17;
         var23 = var3;
         if (this.isFloatingStyleClass()) {
            var21 = 1.0 + var17;
         }
      }

      double var25 = var5 - (this.isHorizontal() ? 0.0 : var11);
      double var27 = var7 - (this.isHorizontal() ? var11 : 0.0);
      int var29 = 0;

      for (int var30 = this.tabContentRegions.size(); var29 < var30; var29++) {
         TabPaneSkin.TabContentRegion var31 = (TabPaneSkin.TabContentRegion)this.tabContentRegions.get(var29);
         var31.setAlignment(Pos.TOP_LEFT);
         if (var31.getClip() != null) {
            ((Rectangle)var31.getClip()).setWidth(var25);
            ((Rectangle)var31.getClip()).setHeight(var27);
         }

         var31.resize(var25, var27);
         var31.relocate(var21, var23);
      }
   }

   private void updateSelectionModel() {
      if (this.selectionModel != null) {
         this.selectionModel.selectedItemProperty().removeListener(this.weakSelectionChangeListener);
      }

      this.selectionModel = this.getSkinnable().getSelectionModel();
      if (this.selectionModel != null) {
         this.selectionModel.selectedItemProperty().addListener(this.weakSelectionChangeListener);
      }
   }

   private static int getRotation(Side var0) {
      switch (var0) {
         case TOP:
            return 0;
         case BOTTOM:
            return 180;
         case LEFT:
            return -90;
         case RIGHT:
            return 90;
         default:
            return 0;
      }
   }

   private static Node clone(Node var0) {
      if (var0 == null) {
         return null;
      } else if (var0 instanceof ImageView) {
         ImageView var3 = (ImageView)var0;
         ImageView var4 = new ImageView();
         var4.imageProperty().bind(var3.imageProperty());
         return var4;
      } else if (var0 instanceof Label) {
         Label var1 = (Label)var0;
         Label var2 = new Label(var1.getText(), clone(var1.getGraphic()));
         var2.textProperty().bind(var1.textProperty());
         return var2;
      } else {
         return null;
      }
   }

   private void removeTabs(List<? extends Tab> var1) {
      for (Tab var3 : var1) {
         this.stopCurrentAnimation(var3);
         TabPaneSkin.TabHeaderSkin var4 = this.tabHeaderArea.getTabHeaderSkin(var3);
         if (var4 != null) {
            var4.isClosing = true;
            var4.dispose();
            this.removeTabContent(var3);
            EventHandler var5 = var3x -> {
               var4.animationState = TabPaneSkin.TabAnimationState.NONE;
               this.tabHeaderArea.removeTab(var3);
               this.tabHeaderArea.requestLayout();
               if (this.getSkinnable().getTabs().isEmpty()) {
                  this.tabHeaderArea.setVisible(false);
               }
            };
            if (this.closeTabAnimation.get() == TabPaneSkin.TabAnimation.GROW) {
               var4.animationState = TabPaneSkin.TabAnimationState.HIDING;
               Timeline var6 = var4.currentAnimation = this.createTimeline(var4, Duration.millis(150.0), 0.0, var5);
               var6.play();
            } else {
               var5.handle(null);
            }
         }
      }
   }

   private void stopCurrentAnimation(Tab var1) {
      TabPaneSkin.TabHeaderSkin var2 = this.tabHeaderArea.getTabHeaderSkin(var1);
      if (var2 != null) {
         Timeline var3 = var2.currentAnimation;
         if (var3 != null && var3.getStatus() == Status.RUNNING) {
            var3.getOnFinished().handle(null);
            var3.stop();
            var2.currentAnimation = null;
         }
      }
   }

   private void addTabs(List<? extends Tab> var1, int var2) {
      int var3 = 0;

      for (Node var6 : new ArrayList(this.tabHeaderArea.headersRegion.getChildren())) {
         TabPaneSkin.TabHeaderSkin var7 = (TabPaneSkin.TabHeaderSkin)var6;
         if (var7.animationState == TabPaneSkin.TabAnimationState.HIDING) {
            this.stopCurrentAnimation(var7.tab);
         }
      }

      for (Tab var10 : var1) {
         this.stopCurrentAnimation(var10);
         if (!this.tabHeaderArea.isVisible()) {
            this.tabHeaderArea.setVisible(true);
         }

         int var11 = var2 + var3++;
         this.tabHeaderArea.addTab(var10, var11);
         this.addTabContent(var10);
         TabPaneSkin.TabHeaderSkin var8 = this.tabHeaderArea.getTabHeaderSkin(var10);
         if (var8 != null) {
            if (this.openTabAnimation.get() == TabPaneSkin.TabAnimation.GROW) {
               var8.animationState = TabPaneSkin.TabAnimationState.SHOWING;
               var8.animationTransition.setValue(0.0);
               var8.setVisible(true);
               var8.currentAnimation = this.createTimeline(var8, Duration.millis(150.0), 1.0, var1x -> {
                  var8.animationState = TabPaneSkin.TabAnimationState.NONE;
                  var8.setVisible(true);
                  var8.inner.requestLayout();
               });
               var8.currentAnimation.play();
            } else {
               var8.setVisible(true);
               var8.inner.requestLayout();
            }
         }
      }
   }

   private void initializeTabListener() {
      this.tabsListener = var1 -> {
         ArrayList var2 = new ArrayList();
         ArrayList var3 = new ArrayList();

         while (var1.next()) {
            if (var1.wasPermutated() && this.dragState != TabPaneSkin.DragState.REORDER) {
               TabPane var4 = this.getSkinnable();
               ObservableList var5 = var4.getTabs();
               int var6 = var1.getTo() - var1.getFrom();
               Tab var7 = var4.getSelectionModel().getSelectedItem();
               ArrayList var8 = new ArrayList(var6);
               this.getSkinnable().getSelectionModel().clearSelection();
               TabPaneSkin.TabAnimation var9 = (TabPaneSkin.TabAnimation)this.openTabAnimation.get();
               TabPaneSkin.TabAnimation var10 = (TabPaneSkin.TabAnimation)this.closeTabAnimation.get();
               this.openTabAnimation.set(TabPaneSkin.TabAnimation.NONE);
               this.closeTabAnimation.set(TabPaneSkin.TabAnimation.NONE);

               for (int var11 = var1.getFrom(); var11 < var1.getTo(); var11++) {
                  var8.add((Tab)var5.get(var11));
               }

               this.removeTabs(var8);
               this.addTabs(var8, var1.getFrom());
               this.openTabAnimation.set(var9);
               this.closeTabAnimation.set(var10);
               this.getSkinnable().getSelectionModel().select(var7);
            }

            if (var1.wasRemoved()) {
               var2.addAll(var1.getRemoved());
            }

            if (var1.wasAdded()) {
               var3.addAll(var1.getAddedSubList());
            }
         }

         var2.removeAll(var3);
         this.removeTabs(var2);
         ArrayList var12 = new ArrayList();
         if (!var3.isEmpty()) {
            for (TabPaneSkin.TabContentRegion var15 : this.tabContentRegions) {
               Tab var17 = var15.getTab();
               TabPaneSkin.TabHeaderSkin var18 = this.tabHeaderArea.getTabHeaderSkin(var17);
               if (!var18.isClosing && var3.contains(var15.getTab())) {
                  var3.remove(var15.getTab());
                  int var19 = this.getSkinnable().getTabs().indexOf(var17);
                  int var20 = this.tabHeaderArea.headersRegion.getChildren().indexOf(var18);
                  if (var19 != var20) {
                     var12.add(new Pair(var19, var18));
                  }
               }
            }

            if (!var3.isEmpty()) {
               this.addTabs(var3, this.getSkinnable().getTabs().indexOf(var3.get(0)));
            }

            for (Pair var16 : var12) {
               this.tabHeaderArea.moveTab((Integer)var16.getKey(), (TabPaneSkin.TabHeaderSkin)var16.getValue());
            }
         }

         this.getSkinnable().requestLayout();
      };
      this.weakTabsListener = new WeakListChangeListener(this.tabsListener);
      this.getSkinnable().getTabs().addListener(this.weakTabsListener);
   }

   private void addTabContent(Tab var1) {
      TabPaneSkin.TabContentRegion var2 = new TabPaneSkin.TabContentRegion(var1);
      var2.setClip(new Rectangle());
      this.tabContentRegions.add(var2);
      this.getChildren().add(0, var2);
   }

   private void removeTabContent(Tab var1) {
      for (TabPaneSkin.TabContentRegion var3 : this.tabContentRegions) {
         if (var3.getTab().equals(var1)) {
            this.removeTabContent(var3);
            break;
         }
      }
   }

   private void removeTabContent(TabPaneSkin.TabContentRegion var1) {
      var1.dispose();
      this.tabContentRegions.remove(var1);
      this.getChildren().remove(var1);
   }

   private void updateTabPosition() {
      this.tabHeaderArea.invalidateScrollOffset();
      this.getSkinnable().applyCss();
      this.getSkinnable().requestLayout();
   }

   private Timeline createTimeline(TabPaneSkin.TabHeaderSkin var1, Duration var2, double var3, EventHandler<ActionEvent> var5) {
      Timeline var6 = new Timeline();
      var6.setCycleCount(1);
      KeyValue var7 = new KeyValue(var1.animationTransition, var3, Interpolator.LINEAR);
      var6.getKeyFrames().clear();
      var6.getKeyFrames().add(new KeyFrame(var2, new KeyValue[]{var7}));
      var6.setOnFinished(var5);
      return var6;
   }

   private boolean isHorizontal() {
      Side var1 = this.getSkinnable().getSide();
      return Side.TOP.equals(var1) || Side.BOTTOM.equals(var1);
   }

   private void initializeSwipeHandlers() {
      if (Properties.IS_TOUCH_SUPPORTED) {
         this.getSkinnable().addEventHandler(SwipeEvent.SWIPE_LEFT, var1 -> this.behavior.selectNextTab());
         this.getSkinnable().addEventHandler(SwipeEvent.SWIPE_RIGHT, var1 -> this.behavior.selectPreviousTab());
      }
   }

   private boolean isFloatingStyleClass() {
      return this.getSkinnable().getStyleClass().contains("floating");
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TabPaneSkin.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case FOCUS_ITEM:
            return this.tabHeaderArea.getTabHeaderSkin(this.selectedTab);
         case ITEM_COUNT:
            return this.tabHeaderArea.headersRegion.getChildren().size();
         case ITEM_AT_INDEX:
            Integer var3 = (Integer)var2[0];
            if (var3 == null) {
               return null;
            }

            return this.tabHeaderArea.headersRegion.getChildren().get(var3);
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   private void addReorderListeners(Node var1) {
      var1.addEventHandler(MouseEvent.MOUSE_PRESSED, this.headerMousePressedHandler);
      var1.addEventHandler(MouseEvent.MOUSE_RELEASED, this.headerMouseReleasedHandler);
      var1.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.headerDraggedHandler);
   }

   private void removeReorderListeners(Node var1) {
      var1.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.headerMousePressedHandler);
      var1.removeEventHandler(MouseEvent.MOUSE_RELEASED, this.headerMouseReleasedHandler);
      var1.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this.headerDraggedHandler);
   }

   private void updateListeners() {
      if (this.getSkinnable().getTabDragPolicy() != TabPane.TabDragPolicy.FIXED && this.getSkinnable().getTabDragPolicy() != null) {
         if (this.getSkinnable().getTabDragPolicy() == TabPane.TabDragPolicy.REORDER) {
            for (Node var4 : this.headersRegion.getChildren()) {
               this.addReorderListeners(var4);
            }

            this.headersRegion.getChildren().addListener(this.childListener);
         }
      } else {
         for (Node var2 : this.headersRegion.getChildren()) {
            this.removeReorderListeners(var2);
         }

         this.headersRegion.getChildren().removeListener(this.childListener);
      }
   }

   private void setupReordering(StackPane var1) {
      this.dragState = TabPaneSkin.DragState.NONE;
      this.headersRegion = var1;
      this.updateListeners();
      this.registerChangeListener(this.getSkinnable().tabDragPolicyProperty(), var1x -> this.updateListeners());
   }

   private void handleHeaderMousePressed(MouseEvent var1) {
      if (var1.getButton().equals(MouseButton.PRIMARY)) {
         ((StackPane)var1.getSource()).setMouseTransparent(true);
         this.startDrag(var1);
      }
   }

   private void handleHeaderMouseReleased(MouseEvent var1) {
      if (var1.getButton().equals(MouseButton.PRIMARY)) {
         ((StackPane)var1.getSource()).setMouseTransparent(false);
         this.stopDrag();
         var1.consume();
      }
   }

   private void handleHeaderDragged(MouseEvent var1) {
      if (var1.getButton().equals(MouseButton.PRIMARY)) {
         this.performDrag(var1);
      }
   }

   private double getDragDelta(double var1, double var3) {
      return !this.getSkinnable().getSide().equals(Side.TOP) && !this.getSkinnable().getSide().equals(Side.RIGHT) ? var3 - var1 : var1 - var3;
   }

   private int deriveTabHeaderLayoutXDirection() {
      return !this.getSkinnable().getSide().equals(Side.TOP) && !this.getSkinnable().getSide().equals(Side.RIGHT) ? -1 : 1;
   }

   private void performDrag(MouseEvent var1) {
      if (this.dragState != TabPaneSkin.DragState.NONE) {
         double var9 = this.getHeaderRegionLocalX(var1);
         double var11 = this.getDragDelta(var9, this.dragEventPrevLoc);
         byte var2;
         if (var11 > 0.0) {
            var2 = 1;
         } else {
            var2 = -1;
         }

         if (this.prevDragDirection != var2) {
            this.stopAnim(this.dropHeaderAnim);
            this.prevDragDirection = var2;
         }

         double var3 = this.dragTabHeader.getLayoutX() + (double)this.xLayoutDirection * var11;
         if (var3 >= 0.0 && var3 + this.dragTabHeader.getWidth() <= this.headersRegion.getWidth()) {
            this.dragState = TabPaneSkin.DragState.REORDER;
            this.dragTabHeader.setLayoutX(var3);
            Bounds var5 = this.dragTabHeader.getBoundsInParent();
            if (var2 == 1) {
               for (int var13 = this.dragTabHeaderIndex + 1; var13 < this.headersRegion.getChildren().size(); var13++) {
                  this.dropTabHeader = (TabPaneSkin.TabHeaderSkin)this.headersRegion.getChildren().get(var13);
                  if (this.dropAnimHeader != this.dropTabHeader) {
                     Bounds var6 = this.dropTabHeader.getBoundsInParent();
                     double var7;
                     if (this.xLayoutDirection == 1) {
                        var7 = var5.getMaxX() - var6.getMinX();
                     } else {
                        var7 = var6.getMaxX() - var5.getMinX();
                     }

                     if (!(var7 > var6.getWidth() * 0.75)) {
                        break;
                     }

                     this.stopAnim(this.dropHeaderAnim);
                     this.dropHeaderTransitionX = (double)this.xLayoutDirection * -var5.getWidth();
                     if (this.xLayoutDirection == 1) {
                        this.dragHeaderDestX = var6.getMaxX() - var5.getWidth();
                     } else {
                        this.dragHeaderDestX = var6.getMinX();
                     }

                     this.startHeaderReorderingAnim();
                  }
               }
            } else {
               for (int var16 = this.dragTabHeaderIndex - 1; var16 >= 0; var16--) {
                  this.dropTabHeader = (TabPaneSkin.TabHeaderSkin)this.headersRegion.getChildren().get(var16);
                  if (this.dropAnimHeader != this.dropTabHeader) {
                     Bounds var14 = this.dropTabHeader.getBoundsInParent();
                     double var15;
                     if (this.xLayoutDirection == 1) {
                        var15 = var14.getMaxX() - var5.getMinX();
                     } else {
                        var15 = var5.getMaxX() - var14.getMinX();
                     }

                     if (!(var15 > var14.getWidth() * 0.75)) {
                        break;
                     }

                     this.stopAnim(this.dropHeaderAnim);
                     this.dropHeaderTransitionX = (double)this.xLayoutDirection * var5.getWidth();
                     if (this.xLayoutDirection == 1) {
                        this.dragHeaderDestX = var14.getMinX();
                     } else {
                        this.dragHeaderDestX = var14.getMaxX() - var5.getWidth();
                     }

                     this.startHeaderReorderingAnim();
                  }
               }
            }
         }

         this.dragEventPrevLoc = var9;
         var1.consume();
      }
   }

   private void startDrag(MouseEvent var1) {
      this.stopAnim(this.dropHeaderAnim);
      this.stopAnim(this.dragHeaderAnim);
      this.dragTabHeader = (TabPaneSkin.TabHeaderSkin)var1.getSource();
      if (this.dragTabHeader != null) {
         this.dragState = TabPaneSkin.DragState.START;
         this.xLayoutDirection = this.deriveTabHeaderLayoutXDirection();
         this.dragEventPrevLoc = this.getHeaderRegionLocalX(var1);
         this.dragTabHeaderIndex = this.headersRegion.getChildren().indexOf(this.dragTabHeader);
         this.dragTabHeaderStartIndex = this.dragTabHeaderIndex;
         this.dragTabHeader.setViewOrder(0.0);
         this.dragHeaderDestX = this.dragTabHeader.getLayoutX();
      }
   }

   private double getHeaderRegionLocalX(MouseEvent var1) {
      Point2D var2 = this.headersRegion.sceneToLocal(var1.getSceneX(), var1.getSceneY());
      return var2.getX();
   }

   private void stopDrag() {
      if (this.dragState == TabPaneSkin.DragState.START) {
         this.resetDrag();
      } else if (this.dragState == TabPaneSkin.DragState.REORDER) {
         this.dragHeaderSourceX = this.dragTabHeader.getLayoutX();
         this.dragHeaderTransitionX = this.dragHeaderDestX - this.dragHeaderSourceX;
         this.dragHeaderAnim.playFromStart();
      }

      this.tabHeaderArea.invalidateScrollOffset();
   }

   private void reorderTabs() {
      if (this.dragTabHeaderIndex != this.dragTabHeaderStartIndex) {
         ((TabObservableList)this.getSkinnable().getTabs())
            .reorder((Tab)this.getSkinnable().getTabs().get(this.dragTabHeaderStartIndex), (Tab)this.getSkinnable().getTabs().get(this.dragTabHeaderIndex));
      }
   }

   private void resetDrag() {
      this.dragState = TabPaneSkin.DragState.NONE;
      this.dragTabHeader.setViewOrder(1.0);
      this.dragTabHeader = null;
      this.dropTabHeader = null;
      this.headersRegion.requestLayout();
   }

   private void startHeaderReorderingAnim() {
      this.dropAnimHeader = this.dropTabHeader;
      this.dropHeaderSourceX = this.dropAnimHeader.getLayoutX();
      this.dropHeaderAnim.playFromStart();
   }

   private void completeHeaderReordering() {
      if (this.dropAnimHeader != null) {
         this.headersRegion.getChildren().remove(this.dropAnimHeader);
         this.headersRegion.getChildren().add(this.dragTabHeaderIndex, this.dropAnimHeader);
         this.dropAnimHeader = null;
         this.headersRegion.requestLayout();
         this.dragTabHeaderIndex = this.headersRegion.getChildren().indexOf(this.dragTabHeader);
      }
   }

   private void stopAnim(Animation var1) {
      if (var1.getStatus() == Status.RUNNING) {
         var1.getOnFinished().handle(null);
         var1.stop();
      }
   }

   ContextMenu test_getTabsMenu() {
      return this.tabHeaderArea.controlButtons.popup;
   }

   void test_disableAnimations() {
      this.closeTabAnimation.set(TabPaneSkin.TabAnimation.NONE);
      this.openTabAnimation.set(TabPaneSkin.TabAnimation.NONE);
   }

   double test_getHeaderAreaScrollOffset() {
      return this.tabHeaderArea.getScrollOffset();
   }

   void test_setHeaderAreaScrollOffset(double var1) {
      this.tabHeaderArea.setScrollOffset(var1);
   }

   boolean test_isTabsFit() {
      return this.tabHeaderArea.tabsFit();
   }

   private static enum DragState {
      NONE,
      START,
      REORDER;
   }

   private static class StyleableProperties {
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
      private static final CssMetaData<TabPane, TabPaneSkin.TabAnimation> OPEN_TAB_ANIMATION = new CssMetaData<TabPane, TabPaneSkin.TabAnimation>(
         "-fx-open-tab-animation", new EnumConverter(TabPaneSkin.TabAnimation.class), TabPaneSkin.TabAnimation.GROW
      ) {
         public boolean isSettable(TabPane var1) {
            return true;
         }

         public StyleableProperty<TabPaneSkin.TabAnimation> getStyleableProperty(TabPane var1) {
            TabPaneSkin var2 = (TabPaneSkin)var1.getSkin();
            return (StyleableProperty<TabPaneSkin.TabAnimation>)var2.openTabAnimation;
         }
      };
      private static final CssMetaData<TabPane, TabPaneSkin.TabAnimation> CLOSE_TAB_ANIMATION = new CssMetaData<TabPane, TabPaneSkin.TabAnimation>(
         "-fx-close-tab-animation", new EnumConverter(TabPaneSkin.TabAnimation.class), TabPaneSkin.TabAnimation.GROW
      ) {
         public boolean isSettable(TabPane var1) {
            return true;
         }

         public StyleableProperty<TabPaneSkin.TabAnimation> getStyleableProperty(TabPane var1) {
            TabPaneSkin var2 = (TabPaneSkin)var1.getSkin();
            return (StyleableProperty<TabPaneSkin.TabAnimation>)var2.closeTabAnimation;
         }
      };

      static {
         ArrayList var0 = new ArrayList<>(SkinBase.getClassCssMetaData());
         var0.add(OPEN_TAB_ANIMATION);
         var0.add(CLOSE_TAB_ANIMATION);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   private static enum TabAnimation {
      NONE,
      GROW;
   }

   private static enum TabAnimationState {
      SHOWING,
      HIDING,
      NONE;
   }

   static class TabContentRegion extends StackPane {
      private Tab tab;
      private InvalidationListener tabContentListener = var1x -> this.updateContent();
      private InvalidationListener tabSelectedListener = new InvalidationListener() {
         public void invalidated(Observable var1) {
            TabContentRegion.this.setVisible(TabContentRegion.this.tab.isSelected());
         }
      };
      private WeakInvalidationListener weakTabContentListener = new WeakInvalidationListener(this.tabContentListener);
      private WeakInvalidationListener weakTabSelectedListener = new WeakInvalidationListener(this.tabSelectedListener);

      public Tab getTab() {
         return this.tab;
      }

      public TabContentRegion(Tab var1) {
         this.getStyleClass().setAll(new String[]{"tab-content-area"});
         this.setManaged(false);
         this.tab = var1;
         this.updateContent();
         this.setVisible(var1.isSelected());
         var1.selectedProperty().addListener(this.weakTabSelectedListener);
         var1.contentProperty().addListener(this.weakTabContentListener);
      }

      private void updateContent() {
         Node var1 = this.getTab().getContent();
         if (var1 == null) {
            this.getChildren().clear();
         } else {
            this.getChildren().setAll(new Node[]{var1});
         }
      }

      public void dispose() {
         this.tab.selectedProperty().removeListener(this.weakTabSelectedListener);
         this.tab.contentProperty().removeListener(this.weakTabContentListener);
      }
   }

   class TabControlButtons extends StackPane {
      private StackPane inner;
      private StackPane downArrow;
      private Pane downArrowBtn;
      private boolean showControlButtons;
      private ContextMenu popup;
      InvalidationListener sidePropListener = var1x -> {
         Side var2x = TabPaneSkin.this.getSkinnable().getSide();
         this.downArrow.setRotate(var2x.equals(Side.BOTTOM) ? 180.0 : 0.0);
      };
      ListChangeListener<Tab> tabsListenerForPopup = var1x -> this.setupPopupMenu();
      WeakInvalidationListener weakSidePropListener = new WeakInvalidationListener(this.sidePropListener);
      WeakListChangeListener weakTabsListenerForPopup = new WeakListChangeListener(this.tabsListenerForPopup);
      private boolean showTabsMenu = false;

      public TabControlButtons() {
         this.getStyleClass().setAll(new String[]{"control-buttons-tab"});
         TabPane var2 = TabPaneSkin.this.getSkinnable();
         this.downArrowBtn = new Pane();
         this.downArrowBtn.getStyleClass().setAll(new String[]{"tab-down-button"});
         this.downArrowBtn.setVisible(this.isShowTabsMenu());
         this.downArrow = new StackPane();
         this.downArrow.setManaged(false);
         this.downArrow.getStyleClass().setAll(new String[]{"arrow"});
         this.downArrow.setRotate(var2.getSide().equals(Side.BOTTOM) ? 180.0 : 0.0);
         this.downArrowBtn.getChildren().add(this.downArrow);
         this.downArrowBtn.setOnMouseClicked(var1x -> this.showPopupMenu());
         this.setupPopupMenu();
         this.inner = new StackPane() {
            protected double computePrefWidth(double var1) {
               double var5 = !TabControlButtons.this.isShowTabsMenu()
                  ? 0.0
                  : this.snapSizeX(TabControlButtons.this.downArrow.prefWidth(this.getHeight()))
                     + this.snapSizeX(TabControlButtons.this.downArrowBtn.prefWidth(this.getHeight()));
               double var3 = 0.0;
               if (TabControlButtons.this.isShowTabsMenu()) {
                  var3 += var5;
               }

               if (var3 > 0.0) {
                  var3 += this.snappedLeftInset() + this.snappedRightInset();
               }

               return var3;
            }

            protected double computePrefHeight(double var1) {
               double var3 = 0.0;
               if (TabControlButtons.this.isShowTabsMenu()) {
                  var3 = Math.max(var3, this.snapSizeY(TabControlButtons.this.downArrowBtn.prefHeight(var1)));
               }

               if (var3 > 0.0) {
                  var3 += this.snappedTopInset() + this.snappedBottomInset();
               }

               return var3;
            }

            protected void layoutChildren() {
               if (TabControlButtons.this.isShowTabsMenu()) {
                  double var1 = 0.0;
                  double var3 = this.snappedTopInset();
                  double var5 = this.snapSizeX(this.getWidth()) - var1 + this.snappedLeftInset();
                  double var7 = this.snapSizeY(this.getHeight()) - var3 + this.snappedBottomInset();
                  this.positionArrow(TabControlButtons.this.downArrowBtn, TabControlButtons.this.downArrow, var1, var3, var5, var7);
               }
            }

            private void positionArrow(Pane var1, StackPane var2, double var3, double var5, double var7, double var9) {
               var1.resize(var7, var9);
               this.positionInArea(var1, var3, var5, var7, var9, 0.0, HPos.CENTER, VPos.CENTER);
               double var11 = this.snapSizeX(var2.prefWidth(-1.0));
               double var13 = this.snapSizeY(var2.prefHeight(-1.0));
               var2.resize(var11, var13);
               this.positionInArea(
                  var2,
                  var1.snappedLeftInset(),
                  var1.snappedTopInset(),
                  var7 - var1.snappedLeftInset() - var1.snappedRightInset(),
                  var9 - var1.snappedTopInset() - var1.snappedBottomInset(),
                  0.0,
                  HPos.CENTER,
                  VPos.CENTER
               );
            }
         };
         this.inner.getStyleClass().add("container");
         this.inner.getChildren().add(this.downArrowBtn);
         this.getChildren().add(this.inner);
         var2.sideProperty().addListener(this.weakSidePropListener);
         var2.getTabs().addListener(this.weakTabsListenerForPopup);
         this.showControlButtons = false;
         if (this.isShowTabsMenu()) {
            this.showControlButtons = true;
            this.requestLayout();
         }

         this.getProperties().put(ContextMenu.class, this.popup);
      }

      void dispose() {
         TabPaneSkin.this.getSkinnable().sideProperty().removeListener(this.weakSidePropListener);
         TabPaneSkin.this.getSkinnable().getTabs().removeListener(this.weakTabsListenerForPopup);
      }

      private void showTabsMenu(boolean var1) {
         boolean var2 = this.isShowTabsMenu();
         this.showTabsMenu = var1;
         if (this.showTabsMenu && !var2) {
            this.downArrowBtn.setVisible(true);
            this.showControlButtons = true;
            this.inner.requestLayout();
            TabPaneSkin.this.tabHeaderArea.requestLayout();
         } else if (!this.showTabsMenu && var2) {
            this.hideControlButtons();
         }
      }

      private boolean isShowTabsMenu() {
         return this.showTabsMenu;
      }

      protected double computePrefWidth(double var1) {
         double var3 = this.snapSizeX(this.inner.prefWidth(var1));
         if (var3 > 0.0) {
            var3 += this.snappedLeftInset() + this.snappedRightInset();
         }

         return var3;
      }

      protected double computePrefHeight(double var1) {
         return Math.max(TabPaneSkin.this.getSkinnable().getTabMinHeight(), this.snapSizeY(this.inner.prefHeight(var1)))
            + this.snappedTopInset()
            + this.snappedBottomInset();
      }

      protected void layoutChildren() {
         double var1 = this.snappedLeftInset();
         double var3 = this.snappedTopInset();
         double var5 = this.snapSizeX(this.getWidth()) - var1 + this.snappedRightInset();
         double var7 = this.snapSizeY(this.getHeight()) - var3 + this.snappedBottomInset();
         if (this.showControlButtons) {
            this.showControlButtons();
            this.showControlButtons = false;
         }

         this.inner.resize(var5, var7);
         this.positionInArea(this.inner, var1, var3, var5, var7, 0.0, HPos.CENTER, VPos.BOTTOM);
      }

      private void showControlButtons() {
         this.setVisible(true);
         if (this.popup == null) {
            this.setupPopupMenu();
         }
      }

      private void hideControlButtons() {
         if (this.isShowTabsMenu()) {
            this.showControlButtons = true;
         } else {
            this.setVisible(false);
            this.clearPopupMenu();
            this.popup = null;
         }

         this.requestLayout();
      }

      private void setupPopupMenu() {
         if (this.popup == null) {
            this.popup = new ContextMenu();
         }

         this.clearPopupMenu();
         ToggleGroup var1 = new ToggleGroup();
         ObservableList var2 = FXCollections.observableArrayList();

         for (Tab var4 : TabPaneSkin.this.getSkinnable().getTabs()) {
            TabPaneSkin.TabMenuItem var5 = new TabPaneSkin.TabMenuItem(var4);
            var5.setToggleGroup(var1);
            var5.setOnAction(var2x -> TabPaneSkin.this.getSkinnable().getSelectionModel().select(var4));
            var2.add(var5);
         }

         this.popup.getItems().addAll(var2);
      }

      private void clearPopupMenu() {
         for (MenuItem var2 : this.popup.getItems()) {
            ((TabPaneSkin.TabMenuItem)var2).dispose();
         }

         this.popup.getItems().clear();
      }

      private void showPopupMenu() {
         for (MenuItem var2 : this.popup.getItems()) {
            TabPaneSkin.TabMenuItem var3 = (TabPaneSkin.TabMenuItem)var2;
            if (TabPaneSkin.this.selectedTab.equals(var3.getTab())) {
               var3.setSelected(true);
               break;
            }
         }

         this.popup.show(this.downArrowBtn, Side.BOTTOM, 0.0, 0.0);
      }
   }

   class TabHeaderArea extends StackPane {
      private Rectangle headerClip;
      private StackPane headersRegion;
      private StackPane headerBackground;
      private TabPaneSkin.TabControlButtons controlButtons;
      private boolean measureClosingTabs = false;
      private double scrollOffset;
      private boolean scrollOffsetDirty = true;

      public TabHeaderArea() {
         this.getStyleClass().setAll(new String[]{"tab-header-area"});
         this.setManaged(false);
         TabPane var2 = TabPaneSkin.this.getSkinnable();
         this.headerClip = new Rectangle();
         this.headersRegion = new StackPane() {
            protected double computePrefWidth(double var1) {
               double var3 = 0.0;

               for (Node var6 : this.getChildren()) {
                  TabPaneSkin.TabHeaderSkin var7 = (TabPaneSkin.TabHeaderSkin)var6;
                  if (var7.isVisible() && (TabHeaderArea.this.measureClosingTabs || !var7.isClosing)) {
                     var3 += var7.prefWidth(var1);
                  }
               }

               return this.snapSizeX(var3) + this.snappedLeftInset() + this.snappedRightInset();
            }

            protected double computePrefHeight(double var1) {
               double var3 = 0.0;

               for (Node var6 : this.getChildren()) {
                  TabPaneSkin.TabHeaderSkin var7 = (TabPaneSkin.TabHeaderSkin)var6;
                  var3 = Math.max(var3, var7.prefHeight(var1));
               }

               return this.snapSizeY(var3) + this.snappedTopInset() + this.snappedBottomInset();
            }

            protected void layoutChildren() {
               if (TabHeaderArea.this.tabsFit()) {
                  TabHeaderArea.this.setScrollOffset(0.0);
               } else {
                  if (TabHeaderArea.this.scrollOffsetDirty) {
                     TabHeaderArea.this.ensureSelectedTabIsVisible();
                     TabHeaderArea.this.scrollOffsetDirty = false;
                  }

                  TabHeaderArea.this.validateScrollOffset();
               }

               Side var1 = TabPaneSkin.this.getSkinnable().getSide();
               double var2 = this.snapSizeY(this.prefHeight(-1.0));
               double var4 = !var1.equals(Side.LEFT) && !var1.equals(Side.BOTTOM)
                  ? TabHeaderArea.this.getScrollOffset()
                  : this.snapSizeX(this.getWidth()) - TabHeaderArea.this.getScrollOffset();
               TabHeaderArea.this.updateHeaderClip();

               for (Node var7 : this.getChildren()) {
                  TabPaneSkin.TabHeaderSkin var8 = (TabPaneSkin.TabHeaderSkin)var7;
                  double var9 = this.snapSizeX(var8.prefWidth(-1.0) * var8.animationTransition.get());
                  double var11 = this.snapSizeY(var8.prefHeight(-1.0));
                  var8.resize(var9, var11);
                  double var13 = var1.equals(Side.BOTTOM) ? 0.0 : var2 - var11 - this.snappedBottomInset();
                  if (!var1.equals(Side.LEFT) && !var1.equals(Side.BOTTOM)) {
                     if (TabPaneSkin.this.dragState != TabPaneSkin.DragState.REORDER
                        || var8 != TabPaneSkin.this.dragTabHeader && var8 != TabPaneSkin.this.dropAnimHeader) {
                        var8.relocate(var4, var13);
                     }

                     var4 += var9;
                  } else {
                     var4 -= var9;
                     if (TabPaneSkin.this.dragState != TabPaneSkin.DragState.REORDER
                        || var8 != TabPaneSkin.this.dragTabHeader && var8 != TabPaneSkin.this.dropAnimHeader) {
                        var8.relocate(var4, var13);
                     }
                  }
               }
            }
         };
         this.headersRegion.getStyleClass().setAll(new String[]{"headers-region"});
         this.headersRegion.setClip(this.headerClip);
         TabPaneSkin.this.setupReordering(this.headersRegion);
         this.headerBackground = new StackPane();
         this.headerBackground.getStyleClass().setAll(new String[]{"tab-header-background"});
         int var3 = 0;

         for (Tab var5 : var2.getTabs()) {
            this.addTab(var5, var3++);
         }

         this.controlButtons = TabPaneSkin.this.new TabControlButtons();
         this.controlButtons.setVisible(false);
         if (this.controlButtons.isVisible()) {
            this.controlButtons.setVisible(true);
         }

         this.getChildren().addAll(new Node[]{this.headerBackground, this.headersRegion, this.controlButtons});
         this.addEventHandler(ScrollEvent.SCROLL, var1x -> {
            Side var2x = TabPaneSkin.this.getSkinnable().getSide();
            var2x = var2x == null ? Side.TOP : var2x;
            switch (var2x) {
               case TOP:
               case BOTTOM:
               default:
                  this.setScrollOffset(this.scrollOffset + var1x.getDeltaY());
                  break;
               case LEFT:
               case RIGHT:
                  this.setScrollOffset(this.scrollOffset - var1x.getDeltaY());
            }
         });
      }

      private void updateHeaderClip() {
         Side var1 = TabPaneSkin.this.getSkinnable().getSide();
         double var2 = 0.0;
         double var4 = 0.0;
         double var6 = 0.0;
         double var8 = 0.0;
         double var10 = 0.0;
         double var12 = 0.0;
         double var14 = this.firstTabIndent();
         double var16 = this.snapSizeX(this.controlButtons.prefWidth(-1.0));
         this.measureClosingTabs = true;
         double var18 = this.snapSizeX(this.headersRegion.prefWidth(-1.0));
         this.measureClosingTabs = false;
         double var20 = this.snapSizeY(this.headersRegion.prefHeight(-1.0));
         if (var16 > 0.0) {
            var16 += 10.0;
         }

         if (this.headersRegion.getEffect() instanceof DropShadow) {
            DropShadow var22 = (DropShadow)this.headersRegion.getEffect();
            var12 = var22.getRadius();
         }

         var10 = this.snapSizeX(this.getWidth()) - var16 - var14;
         if (!var1.equals(Side.LEFT) && !var1.equals(Side.BOTTOM)) {
            var2 = -var12;
            var6 = (var18 < var10 ? var18 : var10) + var12;
            var8 = var20;
         } else {
            if (var18 < var10) {
               var6 = var18 + var12;
            } else {
               var2 = var18 - var10;
               var6 = var10 + var12;
            }

            var8 = var20;
         }

         this.headerClip.setX(var2);
         this.headerClip.setY(var4);
         this.headerClip.setWidth(var6);
         this.headerClip.setHeight(var8);
      }

      private void addTab(Tab var1, int var2) {
         TabPaneSkin.TabHeaderSkin var3 = TabPaneSkin.this.new TabHeaderSkin(var1);
         this.headersRegion.getChildren().add(var2, var3);
         this.invalidateScrollOffset();
      }

      private void removeTab(Tab var1) {
         TabPaneSkin.TabHeaderSkin var2 = this.getTabHeaderSkin(var1);
         if (var2 != null) {
            this.headersRegion.getChildren().remove(var2);
         }

         this.invalidateScrollOffset();
      }

      private void moveTab(int var1, TabPaneSkin.TabHeaderSkin var2) {
         if (var1 != this.headersRegion.getChildren().indexOf(var2)) {
            this.headersRegion.getChildren().remove(var2);
            this.headersRegion.getChildren().add(var1, var2);
         }

         this.invalidateScrollOffset();
      }

      private TabPaneSkin.TabHeaderSkin getTabHeaderSkin(Tab var1) {
         for (Node var3 : this.headersRegion.getChildren()) {
            TabPaneSkin.TabHeaderSkin var4 = (TabPaneSkin.TabHeaderSkin)var3;
            if (var4.getTab().equals(var1)) {
               return var4;
            }
         }

         return null;
      }

      private boolean tabsFit() {
         double var1 = this.snapSizeX(this.headersRegion.prefWidth(-1.0));
         double var3 = this.snapSizeX(this.controlButtons.prefWidth(-1.0));
         double var5 = var1 + var3 + this.firstTabIndent() + 10.0;
         return var5 < this.getWidth();
      }

      private void ensureSelectedTabIsVisible() {
         double var1 = this.snapSizeX(
            TabPaneSkin.this.isHorizontal() ? TabPaneSkin.this.getSkinnable().getWidth() : TabPaneSkin.this.getSkinnable().getHeight()
         );
         double var3 = this.snapSizeX(this.controlButtons.getWidth());
         double var5 = var1 - var3 - this.firstTabIndent() - 10.0;
         double var7 = 0.0;
         double var9 = 0.0;
         double var11 = 0.0;

         for (Node var14 : this.headersRegion.getChildren()) {
            TabPaneSkin.TabHeaderSkin var15 = (TabPaneSkin.TabHeaderSkin)var14;
            double var16 = this.snapSizeX(var15.prefWidth(-1.0));
            if (TabPaneSkin.this.selectedTab != null && TabPaneSkin.this.selectedTab.equals(var15.getTab())) {
               var9 = var7;
               var11 = var16;
            }

            var7 += var16;
         }

         double var21 = this.getScrollOffset();
         double var17 = var9 + var11;
         if (var9 < -var21) {
            this.setScrollOffset(-var9);
         } else if (var17 > var5 - var21) {
            this.setScrollOffset(var5 - var17);
         }
      }

      public double getScrollOffset() {
         return this.scrollOffset;
      }

      public void invalidateScrollOffset() {
         this.scrollOffsetDirty = true;
      }

      private void validateScrollOffset() {
         this.setScrollOffset(this.getScrollOffset());
      }

      private void setScrollOffset(double var1) {
         double var3 = this.snapSizeX(
            TabPaneSkin.this.isHorizontal() ? TabPaneSkin.this.getSkinnable().getWidth() : TabPaneSkin.this.getSkinnable().getHeight()
         );
         double var5 = this.snapSizeX(this.controlButtons.getWidth());
         double var7 = var3 - var5 - this.firstTabIndent() - 10.0;
         double var9 = 0.0;

         for (Node var12 : this.headersRegion.getChildren()) {
            TabPaneSkin.TabHeaderSkin var13 = (TabPaneSkin.TabHeaderSkin)var12;
            double var14 = this.snapSizeX(var13.prefWidth(-1.0));
            var9 += var14;
         }

         double var16;
         if (var7 - var1 > var9 && var1 < 0.0) {
            var16 = var7 - var9;
         } else if (var1 > 0.0) {
            var16 = 0.0;
         } else {
            var16 = var1;
         }

         if (Math.abs(var16 - this.scrollOffset) > 0.001) {
            this.scrollOffset = var16;
            this.headersRegion.requestLayout();
         }
      }

      private double firstTabIndent() {
         switch (TabPaneSkin.this.getSkinnable().getSide()) {
            case TOP:
            case BOTTOM:
               return this.snappedLeftInset();
            case LEFT:
            case RIGHT:
               return this.snappedTopInset();
            default:
               return 0.0;
         }
      }

      protected double computePrefWidth(double var1) {
         double var3 = TabPaneSkin.this.isHorizontal()
            ? this.snappedLeftInset() + this.snappedRightInset()
            : this.snappedTopInset() + this.snappedBottomInset();
         return this.snapSizeX(this.headersRegion.prefWidth(var1)) + this.controlButtons.prefWidth(var1) + this.firstTabIndent() + 10.0 + var3;
      }

      protected double computePrefHeight(double var1) {
         double var3 = TabPaneSkin.this.isHorizontal()
            ? this.snappedTopInset() + this.snappedBottomInset()
            : this.snappedLeftInset() + this.snappedRightInset();
         return this.snapSizeY(this.headersRegion.prefHeight(-1.0)) + var3;
      }

      public double getBaselineOffset() {
         return TabPaneSkin.this.getSkinnable().getSide() == Side.TOP ? this.headersRegion.getBaselineOffset() + this.snappedTopInset() : 0.0;
      }

      protected void layoutChildren() {
         double var1 = this.snappedLeftInset();
         double var3 = this.snappedRightInset();
         double var5 = this.snappedTopInset();
         double var7 = this.snappedBottomInset();
         double var9 = this.snapSizeX(this.getWidth()) - (TabPaneSkin.this.isHorizontal() ? var1 + var3 : var5 + var7);
         double var11 = this.snapSizeY(this.getHeight()) - (TabPaneSkin.this.isHorizontal() ? var5 + var7 : var1 + var3);
         double var13 = this.snapSizeY(this.prefHeight(-1.0));
         double var15 = this.snapSizeX(this.headersRegion.prefWidth(-1.0));
         double var17 = this.snapSizeY(this.headersRegion.prefHeight(-1.0));
         this.controlButtons.showTabsMenu(!this.tabsFit());
         this.updateHeaderClip();
         this.headersRegion.requestLayout();
         double var19 = this.snapSizeX(this.controlButtons.prefWidth(-1.0));
         double var21 = this.controlButtons.prefHeight(var19);
         this.controlButtons.resize(var19, var21);
         this.headersRegion.resize(var15, var17);
         if (TabPaneSkin.this.isFloatingStyleClass()) {
            this.headerBackground.setVisible(false);
         } else {
            this.headerBackground.resize(this.snapSizeX(this.getWidth()), this.snapSizeY(this.getHeight()));
            this.headerBackground.setVisible(true);
         }

         double var23 = 0.0;
         double var25 = 0.0;
         double var27 = 0.0;
         double var29 = 0.0;
         Side var31 = TabPaneSkin.this.getSkinnable().getSide();
         if (var31.equals(Side.TOP)) {
            var23 = var1;
            var25 = var13 - var17 - var7;
            var27 = var9 - var19 + var1;
            var29 = this.snapSizeY(this.getHeight()) - var21 - var7;
         } else if (var31.equals(Side.RIGHT)) {
            var23 = var5;
            var25 = var13 - var17 - var1;
            var27 = var9 - var19 + var5;
            var29 = this.snapSizeY(this.getHeight()) - var21 - var1;
         } else if (var31.equals(Side.BOTTOM)) {
            var23 = this.snapSizeX(this.getWidth()) - var15 - var1;
            var25 = var13 - var17 - var5;
            var27 = var3;
            var29 = this.snapSizeY(this.getHeight()) - var21 - var5;
         } else if (var31.equals(Side.LEFT)) {
            var23 = this.snapSizeX(this.getWidth()) - var15 - var5;
            var25 = var13 - var17 - var3;
            var27 = var1;
            var29 = this.snapSizeY(this.getHeight()) - var21 - var3;
         }

         if (this.headerBackground.isVisible()) {
            this.positionInArea(
               this.headerBackground, 0.0, 0.0, this.snapSizeX(this.getWidth()), this.snapSizeY(this.getHeight()), 0.0, HPos.CENTER, VPos.CENTER
            );
         }

         this.positionInArea(this.headersRegion, var23, var25, var9, var11, 0.0, HPos.LEFT, VPos.CENTER);
         this.positionInArea(this.controlButtons, var27, var29, var19, var21, 0.0, HPos.CENTER, VPos.CENTER);
      }

      void dispose() {
         for (Node var2 : this.headersRegion.getChildren()) {
            TabPaneSkin.TabHeaderSkin var3 = (TabPaneSkin.TabHeaderSkin)var2;
            var3.dispose();
         }

         this.controlButtons.dispose();
      }
   }

   class TabHeaderSkin extends StackPane {
      private final Tab tab;
      private Label label;
      private StackPane closeBtn;
      private StackPane inner;
      private Tooltip oldTooltip;
      private Tooltip tooltip;
      private Rectangle clip;
      private boolean isClosing = false;
      private LambdaMultiplePropertyChangeListenerHandler listener = new LambdaMultiplePropertyChangeListenerHandler();
      private final ListChangeListener<String> styleClassListener = new ListChangeListener<String>() {
         public void onChanged(Change<? extends String> var1) {
            TabHeaderSkin.this.getStyleClass().setAll(TabHeaderSkin.this.tab.getStyleClass());
         }
      };
      private final WeakListChangeListener<String> weakStyleClassListener = new WeakListChangeListener(this.styleClassListener);
      private final DoubleProperty animationTransition = new SimpleDoubleProperty(this, "animationTransition", 1.0) {
         protected void invalidated() {
            TabHeaderSkin.this.requestLayout();
         }
      };
      private TabPaneSkin.TabAnimationState animationState = TabPaneSkin.TabAnimationState.NONE;
      private Timeline currentAnimation;

      public Tab getTab() {
         return this.tab;
      }

      public TabHeaderSkin(Tab var2) {
         this.getStyleClass().setAll(var2.getStyleClass());
         this.setId(var2.getId());
         this.setStyle(var2.getStyle());
         this.setAccessibleRole(AccessibleRole.TAB_ITEM);
         this.setViewOrder(1.0);
         this.tab = var2;
         this.clip = new Rectangle();
         this.setClip(this.clip);
         this.label = new Label(var2.getText(), var2.getGraphic());
         this.label.getStyleClass().setAll(new String[]{"tab-label"});
         this.closeBtn = new StackPane() {
            protected double computePrefWidth(double var1) {
               return (double)TabPaneSkin.CLOSE_BTN_SIZE;
            }

            protected double computePrefHeight(double var1) {
               return (double)TabPaneSkin.CLOSE_BTN_SIZE;
            }

            public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
               switch (var1) {
                  case FIRE:
                     Tab var3 = TabHeaderSkin.this.getTab();
                     if (TabPaneSkin.this.behavior.canCloseTab(var3)) {
                        TabPaneSkin.this.behavior.closeTab(var3);
                        this.setOnMousePressed(null);
                     }
                     break;
                  default:
                     super.executeAccessibleAction(var1, var2);
               }
            }
         };
         this.closeBtn.setAccessibleRole(AccessibleRole.BUTTON);
         this.closeBtn.setAccessibleText(ControlResources.getString("Accessibility.title.TabPane.CloseButton"));
         this.closeBtn.getStyleClass().setAll(new String[]{"tab-close-button"});
         this.closeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent var1) {
               Tab var2 = TabHeaderSkin.this.getTab();
               if (var1.getButton().equals(MouseButton.PRIMARY) && TabPaneSkin.this.behavior.canCloseTab(var2)) {
                  TabPaneSkin.this.behavior.closeTab(var2);
                  TabHeaderSkin.this.setOnMousePressed(null);
                  var1.consume();
               }
            }
         });
         this.updateGraphicRotation();
         final Region var3 = new Region();
         var3.setMouseTransparent(true);
         var3.getStyleClass().add("focus-indicator");
         this.inner = new StackPane() {
            protected void layoutChildren() {
               TabPane var1 = TabPaneSkin.this.getSkinnable();
               double var2 = this.snappedTopInset();
               double var4 = this.snappedRightInset();
               double var6 = this.snappedBottomInset();
               double var8 = this.snappedLeftInset();
               double var10 = this.getWidth() - (var8 + var4);
               double var12 = this.getHeight() - (var2 + var6);
               double var14 = this.snapSizeX(TabHeaderSkin.this.label.prefWidth(-1.0));
               double var16 = this.snapSizeY(TabHeaderSkin.this.label.prefHeight(-1.0));
               double var18 = TabHeaderSkin.this.showCloseButton() ? this.snapSizeX(TabHeaderSkin.this.closeBtn.prefWidth(-1.0)) : 0.0;
               double var20 = TabHeaderSkin.this.showCloseButton() ? this.snapSizeY(TabHeaderSkin.this.closeBtn.prefHeight(-1.0)) : 0.0;
               double var22 = this.snapSizeX(var1.getTabMinWidth());
               double var24 = this.snapSizeX(var1.getTabMaxWidth());
               double var26 = this.snapSizeY(var1.getTabMaxHeight());
               double var28 = var14;
               double var30 = var14;
               double var32 = var16;
               double var34 = var14 + var18;
               double var36 = Math.max(var16, var20);
               if (var34 > var24 && var24 != Double.MAX_VALUE) {
                  var28 = var24 - var18;
                  var30 = var24 - var18;
               } else if (var34 < var22) {
                  var28 = var22 - var18;
               }

               if (var36 > var26 && var26 != Double.MAX_VALUE) {
                  var32 = var26;
               }

               if (TabHeaderSkin.this.animationState != TabPaneSkin.TabAnimationState.NONE) {
                  var28 *= TabHeaderSkin.this.animationTransition.get();
                  TabHeaderSkin.this.closeBtn.setVisible(false);
               } else {
                  TabHeaderSkin.this.closeBtn.setVisible(TabHeaderSkin.this.showCloseButton());
               }

               TabHeaderSkin.this.label.resize(var30, var32);
               double var40 = (var24 < Double.MAX_VALUE ? Math.min(var10, var24) : var10) - var4 - var18;
               this.positionInArea(TabHeaderSkin.this.label, var8, var2, var28, var12, 0.0, HPos.CENTER, VPos.CENTER);
               if (TabHeaderSkin.this.closeBtn.isVisible()) {
                  TabHeaderSkin.this.closeBtn.resize(var18, var20);
                  this.positionInArea(TabHeaderSkin.this.closeBtn, var40, var2, var18, var12, 0.0, HPos.CENTER, VPos.CENTER);
               }

               int var42 = Utils.isMac() ? 2 : 3;
               int var43 = Utils.isMac() ? 2 : 1;
               var3.resizeRelocate(var8 - (double)var43, var2 + (double)var42, var10 + (double)(2 * var43), var12 - (double)(2 * var42));
            }
         };
         this.inner.getStyleClass().add("tab-container");
         this.inner.setRotate(TabPaneSkin.this.getSkinnable().getSide().equals(Side.BOTTOM) ? 180.0 : 0.0);
         this.inner.getChildren().addAll(new Node[]{this.label, this.closeBtn, var3});
         this.getChildren().addAll(new Node[]{this.inner});
         this.tooltip = var2.getTooltip();
         if (this.tooltip != null) {
            Tooltip.install(this, this.tooltip);
            this.oldTooltip = this.tooltip;
         }

         this.listener.registerChangeListener(var2.closableProperty(), var1x -> {
            this.inner.requestLayout();
            this.requestLayout();
         });
         this.listener.registerChangeListener(var2.selectedProperty(), var2x -> {
            this.pseudoClassStateChanged(TabPaneSkin.SELECTED_PSEUDOCLASS_STATE, var2.isSelected());
            this.inner.requestLayout();
            this.requestLayout();
         });
         this.listener.registerChangeListener(var2.textProperty(), var1x -> this.label.setText(this.getTab().getText()));
         this.listener.registerChangeListener(var2.graphicProperty(), var1x -> this.label.setGraphic(this.getTab().getGraphic()));
         this.listener.registerChangeListener(var2.tooltipProperty(), var2x -> {
            if (this.oldTooltip != null) {
               Tooltip.uninstall(this, this.oldTooltip);
            }

            this.tooltip = var2.getTooltip();
            if (this.tooltip != null) {
               Tooltip.install(this, this.tooltip);
               this.oldTooltip = this.tooltip;
            }
         });
         this.listener.registerChangeListener(var2.disabledProperty(), var1x -> this.updateTabDisabledState());
         this.listener.registerChangeListener(var2.getTabPane().disabledProperty(), var1x -> this.updateTabDisabledState());
         this.listener.registerChangeListener(var2.styleProperty(), var2x -> this.setStyle(var2.getStyle()));
         var2.getStyleClass().addListener(this.weakStyleClassListener);
         this.listener.registerChangeListener(TabPaneSkin.this.getSkinnable().tabClosingPolicyProperty(), var1x -> {
            this.inner.requestLayout();
            this.requestLayout();
         });
         this.listener.registerChangeListener(TabPaneSkin.this.getSkinnable().sideProperty(), var1x -> {
            Side var2x = TabPaneSkin.this.getSkinnable().getSide();
            this.pseudoClassStateChanged(TabPaneSkin.TOP_PSEUDOCLASS_STATE, var2x == Side.TOP);
            this.pseudoClassStateChanged(TabPaneSkin.RIGHT_PSEUDOCLASS_STATE, var2x == Side.RIGHT);
            this.pseudoClassStateChanged(TabPaneSkin.BOTTOM_PSEUDOCLASS_STATE, var2x == Side.BOTTOM);
            this.pseudoClassStateChanged(TabPaneSkin.LEFT_PSEUDOCLASS_STATE, var2x == Side.LEFT);
            this.inner.setRotate(var2x == Side.BOTTOM ? 180.0 : 0.0);
            if (TabPaneSkin.this.getSkinnable().isRotateGraphic()) {
               this.updateGraphicRotation();
            }
         });
         this.listener.registerChangeListener(TabPaneSkin.this.getSkinnable().rotateGraphicProperty(), var1x -> this.updateGraphicRotation());
         this.listener.registerChangeListener(TabPaneSkin.this.getSkinnable().tabMinWidthProperty(), var1x -> {
            this.requestLayout();
            TabPaneSkin.this.getSkinnable().requestLayout();
         });
         this.listener.registerChangeListener(TabPaneSkin.this.getSkinnable().tabMaxWidthProperty(), var1x -> {
            this.requestLayout();
            TabPaneSkin.this.getSkinnable().requestLayout();
         });
         this.listener.registerChangeListener(TabPaneSkin.this.getSkinnable().tabMinHeightProperty(), var1x -> {
            this.requestLayout();
            TabPaneSkin.this.getSkinnable().requestLayout();
         });
         this.listener.registerChangeListener(TabPaneSkin.this.getSkinnable().tabMaxHeightProperty(), var1x -> {
            this.requestLayout();
            TabPaneSkin.this.getSkinnable().requestLayout();
         });
         this.getProperties().put(Tab.class, var2);
         this.getProperties().put(ContextMenu.class, var2.getContextMenu());
         this.setOnContextMenuRequested(var1x -> {
            if (this.getTab().getContextMenu() != null) {
               this.getTab().getContextMenu().show(this.inner, var1x.getScreenX(), var1x.getScreenY());
               var1x.consume();
            }
         });
         this.setOnMousePressed(
            new EventHandler<MouseEvent>() {
               public void handle(MouseEvent var1) {
                  Tab var2 = TabHeaderSkin.this.getTab();
                  if (!var2.isDisable()) {
                     if ((var1.getButton().equals(MouseButton.MIDDLE) || var1.getButton().equals(MouseButton.PRIMARY))
                        && var2.getContextMenu() != null
                        && var2.getContextMenu().isShowing()) {
                        var2.getContextMenu().hide();
                     }

                     if (var1.getButton().equals(MouseButton.MIDDLE)) {
                        if (TabHeaderSkin.this.showCloseButton() && TabPaneSkin.this.behavior.canCloseTab(var2)) {
                           TabHeaderSkin.this.dispose();
                           TabPaneSkin.this.behavior.closeTab(var2);
                        }
                     } else if (var1.getButton().equals(MouseButton.PRIMARY)) {
                        TabPaneSkin.this.behavior.selectTab(var2);
                     }
                  }
               }
            }
         );
         this.pseudoClassStateChanged(TabPaneSkin.SELECTED_PSEUDOCLASS_STATE, var2.isSelected());
         this.pseudoClassStateChanged(TabPaneSkin.DISABLED_PSEUDOCLASS_STATE, var2.isDisabled());
         Side var4 = TabPaneSkin.this.getSkinnable().getSide();
         this.pseudoClassStateChanged(TabPaneSkin.TOP_PSEUDOCLASS_STATE, var4 == Side.TOP);
         this.pseudoClassStateChanged(TabPaneSkin.RIGHT_PSEUDOCLASS_STATE, var4 == Side.RIGHT);
         this.pseudoClassStateChanged(TabPaneSkin.BOTTOM_PSEUDOCLASS_STATE, var4 == Side.BOTTOM);
         this.pseudoClassStateChanged(TabPaneSkin.LEFT_PSEUDOCLASS_STATE, var4 == Side.LEFT);
      }

      private void updateTabDisabledState() {
         this.pseudoClassStateChanged(TabPaneSkin.DISABLED_PSEUDOCLASS_STATE, this.tab.isDisabled());
         this.inner.requestLayout();
         this.requestLayout();
      }

      private void updateGraphicRotation() {
         if (this.label.getGraphic() != null) {
            this.label
               .getGraphic()
               .setRotate(
                  TabPaneSkin.this.getSkinnable().isRotateGraphic()
                     ? 0.0
                     : (double)(
                        TabPaneSkin.this.getSkinnable().getSide().equals(Side.RIGHT)
                           ? -90.0F
                           : (TabPaneSkin.this.getSkinnable().getSide().equals(Side.LEFT) ? 90.0F : 0.0F)
                     )
               );
         }
      }

      private boolean showCloseButton() {
         return this.tab.isClosable()
            && (
               TabPaneSkin.this.getSkinnable().getTabClosingPolicy().equals(TabPane.TabClosingPolicy.ALL_TABS)
                  || TabPaneSkin.this.getSkinnable().getTabClosingPolicy().equals(TabPane.TabClosingPolicy.SELECTED_TAB) && this.tab.isSelected()
            );
      }

      private void dispose() {
         this.tab.getStyleClass().removeListener(this.weakStyleClassListener);
         this.listener.dispose();
         this.setOnContextMenuRequested(null);
         this.setOnMousePressed(null);
      }

      protected double computePrefWidth(double var1) {
         double var3 = this.snapSizeX(TabPaneSkin.this.getSkinnable().getTabMinWidth());
         double var5 = this.snapSizeX(TabPaneSkin.this.getSkinnable().getTabMaxWidth());
         double var7 = this.snappedRightInset();
         double var9 = this.snappedLeftInset();
         double var11 = this.snapSizeX(this.label.prefWidth(-1.0));
         if (this.showCloseButton()) {
            var11 += this.snapSizeX(this.closeBtn.prefWidth(-1.0));
         }

         if (var11 > var5) {
            var11 = var5;
         } else if (var11 < var3) {
            var11 = var3;
         }

         return var11 + var7 + var9;
      }

      protected double computePrefHeight(double var1) {
         double var3 = this.snapSizeY(TabPaneSkin.this.getSkinnable().getTabMinHeight());
         double var5 = this.snapSizeY(TabPaneSkin.this.getSkinnable().getTabMaxHeight());
         double var7 = this.snappedTopInset();
         double var9 = this.snappedBottomInset();
         double var11 = this.snapSizeY(this.label.prefHeight(var1));
         if (var11 > var5) {
            var11 = var5;
         } else if (var11 < var3) {
            var11 = var3;
         }

         return var11 + var7 + var9;
      }

      protected void layoutChildren() {
         double var1 = (this.snapSizeX(this.getWidth()) - this.snappedRightInset() - this.snappedLeftInset()) * this.animationTransition.getValue();
         this.inner.resize(var1, this.snapSizeY(this.getHeight()) - this.snappedTopInset() - this.snappedBottomInset());
         this.inner.relocate(this.snappedLeftInset(), this.snappedTopInset());
      }

      protected void setWidth(double var1) {
         super.setWidth(var1);
         this.clip.setWidth(var1);
      }

      protected void setHeight(double var1) {
         super.setHeight(var1);
         this.clip.setHeight(var1);
      }

      public Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
         switch (var1) {
            case TEXT:
               return this.getTab().getText();
            case SELECTED:
               return TabPaneSkin.this.selectedTab == this.getTab();
            default:
               return super.queryAccessibleAttribute(var1, var2);
         }
      }

      public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
         switch (var1) {
            case REQUEST_FOCUS:
               TabPaneSkin.this.getSkinnable().getSelectionModel().select(this.getTab());
               break;
            default:
               super.executeAccessibleAction(var1, var2);
         }
      }
   }

   static class TabMenuItem extends RadioMenuItem {
      Tab tab;
      private InvalidationListener disableListener = new InvalidationListener() {
         public void invalidated(Observable var1) {
            TabMenuItem.this.setDisable(TabMenuItem.this.tab.isDisable());
         }
      };
      private WeakInvalidationListener weakDisableListener = new WeakInvalidationListener(this.disableListener);

      public TabMenuItem(Tab var1) {
         super(var1.getText(), TabPaneSkin.clone(var1.getGraphic()));
         this.tab = var1;
         this.setDisable(var1.isDisable());
         var1.disableProperty().addListener(this.weakDisableListener);
         this.textProperty().bind(var1.textProperty());
      }

      public Tab getTab() {
         return this.tab;
      }

      public void dispose() {
         this.textProperty().unbind();
         this.tab.disableProperty().removeListener(this.weakDisableListener);
         this.tab = null;
      }
   }
}
