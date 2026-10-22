package javafx.scene.control.skin;

import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ToolBarBehavior;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ToolBarSkin extends SkinBase<ToolBar> {
   private Pane box;
   private ToolBarSkin.ToolBarOverflowMenu overflowMenu;
   private boolean overflow = false;
   private double previousWidth = 0.0;
   private double previousHeight = 0.0;
   private double savedPrefWidth = 0.0;
   private double savedPrefHeight = 0.0;
   private ObservableList<MenuItem> overflowMenuItems;
   private boolean needsUpdate = false;
   private final ParentTraversalEngine engine;
   private final BehaviorBase<ToolBar> behavior;
   private ListChangeListener<Node> itemsListener;
   private DoubleProperty spacing;
   private ObjectProperty<Pos> boxAlignment;

   public ToolBarSkin(final ToolBar var1) {
      super(var1);
      this.behavior = new ToolBarBehavior(var1);
      this.overflowMenuItems = FXCollections.observableArrayList();
      this.initialize();
      this.registerChangeListener(var1.orientationProperty(), var1x -> this.initialize());
      this.engine = new ParentTraversalEngine(this.getSkinnable(), new Algorithm() {
         private Node selectPrev(int var1x, TraversalContext var2) {
            for (int var3 = var1x; var3 >= 0; var3--) {
               Node var4 = (Node)ToolBarSkin.this.box.getChildren().get(var3);
               if (!var4.isDisabled() && NodeHelper.isTreeShowing(var4)) {
                  if (var4 instanceof Parent) {
                     Node var5 = var2.selectLastInParent((Parent)var4);
                     if (var5 != null) {
                        return var5;
                     }
                  }

                  if (var4.isFocusTraversable()) {
                     return var4;
                  }
               }
            }

            return null;
         }

         private Node selectNext(int var1x, TraversalContext var2) {
            int var3 = var1x;

            for (int var4 = ToolBarSkin.this.box.getChildren().size(); var3 < var4; var3++) {
               Node var5 = (Node)ToolBarSkin.this.box.getChildren().get(var3);
               if (!var5.isDisabled() && NodeHelper.isTreeShowing(var5)) {
                  if (var5.isFocusTraversable()) {
                     return var5;
                  }

                  if (var5 instanceof Parent) {
                     Node var6 = var2.selectFirstInParent((Parent)var5);
                     if (var6 != null) {
                        return var6;
                     }
                  }
               }
            }

            return null;
         }

         public Node select(Node var1x, Direction var2, TraversalContext var3) {
            var2 = var2.getDirectionForNodeOrientation(var1.getEffectiveNodeOrientation());
            ObservableList var4 = ToolBarSkin.this.box.getChildren();
            if (var1x == ToolBarSkin.this.overflowMenu) {
               if (var2.isForward()) {
                  return null;
               }

               Node var5 = this.selectPrev(var4.size() - 1, var3);
               if (var5 != null) {
                  return var5;
               }
            }

            int var9 = var4.indexOf(var1x);
            if (var9 < 0) {
               Parent var6 = var1x.getParent();

               while (!var4.contains(var6)) {
                  var6 = var6.getParent();
               }

               Node var7 = var3.selectInSubtree(var6, var1x, var2);
               if (var7 != null) {
                  return var7;
               }

               var9 = var4.indexOf(var6);
               if (var2 == Direction.NEXT) {
                  var2 = Direction.NEXT_IN_LINE;
               }
            }

            if (var9 >= 0) {
               if (var2.isForward()) {
                  Node var10 = this.selectNext(var9 + 1, var3);
                  if (var10 != null) {
                     return var10;
                  }

                  if (ToolBarSkin.this.overflow) {
                     ToolBarSkin.this.overflowMenu.requestFocus();
                     return ToolBarSkin.this.overflowMenu;
                  }
               } else {
                  Node var11 = this.selectPrev(var9 - 1, var3);
                  if (var11 != null) {
                     return var11;
                  }
               }
            }

            return null;
         }

         public Node selectFirst(TraversalContext var1x) {
            Node var2 = this.selectNext(0, var1x);
            if (var2 != null) {
               return var2;
            } else {
               return ToolBarSkin.this.overflow ? ToolBarSkin.this.overflowMenu : null;
            }
         }

         public Node selectLast(TraversalContext var1x) {
            return (Node)(ToolBarSkin.this.overflow ? ToolBarSkin.this.overflowMenu : this.selectPrev(ToolBarSkin.this.box.getChildren().size() - 1, var1x));
         }
      });
      ParentHelper.setTraversalEngine(this.getSkinnable(), this.engine);
      this.registerChangeListener(var1.focusedProperty(), var1x -> {
         if (this.getSkinnable().isFocused()) {
            if (!this.box.getChildren().isEmpty()) {
               ((Node)this.box.getChildren().get(0)).requestFocus();
            } else {
               this.overflowMenu.requestFocus();
            }
         }
      });
      this.itemsListener = var1x -> {
         while (var1x.next()) {
            for (Node var3 : var1x.getRemoved()) {
               this.box.getChildren().remove(var3);
            }

            this.box.getChildren().addAll(var1x.getAddedSubList());
         }

         this.needsUpdate = true;
         this.getSkinnable().requestLayout();
      };
      var1.getItems().addListener(this.itemsListener);
   }

   private double snapSpacing(double var1) {
      return this.getSkinnable().getOrientation() == Orientation.VERTICAL ? this.snapSpaceY(var1) : this.snapSpaceX(var1);
   }

   private final void setSpacing(double var1) {
      this.spacingProperty().set(this.snapSpacing(var1));
   }

   private final double getSpacing() {
      return this.spacing == null ? 0.0 : this.snapSpacing(this.spacing.get());
   }

   private final DoubleProperty spacingProperty() {
      if (this.spacing == null) {
         this.spacing = new StyleableDoubleProperty() {
            protected void invalidated() {
               double var1 = this.get();
               if (ToolBarSkin.this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
                  ((VBox)ToolBarSkin.this.box).setSpacing(var1);
               } else {
                  ((HBox)ToolBarSkin.this.box).setSpacing(var1);
               }
            }

            public Object getBean() {
               return ToolBarSkin.this;
            }

            public String getName() {
               return "spacing";
            }

            public CssMetaData<ToolBar, Number> getCssMetaData() {
               return ToolBarSkin.StyleableProperties.SPACING;
            }
         };
      }

      return this.spacing;
   }

   private final void setBoxAlignment(Pos var1) {
      this.boxAlignmentProperty().set(var1);
   }

   private final Pos getBoxAlignment() {
      return this.boxAlignment == null ? Pos.TOP_LEFT : (Pos)this.boxAlignment.get();
   }

   private final ObjectProperty<Pos> boxAlignmentProperty() {
      if (this.boxAlignment == null) {
         this.boxAlignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
            public void invalidated() {
               Pos var1 = (Pos)this.get();
               if (ToolBarSkin.this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
                  ((VBox)ToolBarSkin.this.box).setAlignment(var1);
               } else {
                  ((HBox)ToolBarSkin.this.box).setAlignment(var1);
               }
            }

            public Object getBean() {
               return ToolBarSkin.this;
            }

            public String getName() {
               return "boxAlignment";
            }

            public CssMetaData<ToolBar, Pos> getCssMetaData() {
               return ToolBarSkin.StyleableProperties.ALIGNMENT;
            }
         };
      }

      return this.boxAlignment;
   }

   @Override
   public void dispose() {
      if (this.getSkinnable() != null) {
         this.getSkinnable().getItems().removeListener(this.itemsListener);
         super.dispose();
         if (this.behavior != null) {
            this.behavior.dispose();
         }
      }
   }

   @Override
   protected double computeMinWidth(double var1, double var3, double var5, double var7, double var9) {
      ToolBar var11 = this.getSkinnable();
      return var11.getOrientation() == Orientation.VERTICAL
         ? this.computePrefWidth(-1.0, var3, var5, var7, var9)
         : this.snapSizeX(this.overflowMenu.prefWidth(-1.0)) + var9 + var5;
   }

   @Override
   protected double computeMinHeight(double var1, double var3, double var5, double var7, double var9) {
      ToolBar var11 = this.getSkinnable();
      return var11.getOrientation() == Orientation.VERTICAL
         ? this.snapSizeY(this.overflowMenu.prefHeight(-1.0)) + var3 + var7
         : this.computePrefHeight(-1.0, var3, var5, var7, var9);
   }

   @Override
   protected double computePrefWidth(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      ToolBar var13 = this.getSkinnable();
      if (var13.getOrientation() == Orientation.HORIZONTAL) {
         for (Node var15 : var13.getItems()) {
            if (var15.isManaged()) {
               var11 += this.snapSizeX(var15.prefWidth(-1.0)) + this.getSpacing();
            }
         }

         var11 -= this.getSpacing();
      } else {
         for (Node var17 : var13.getItems()) {
            if (var17.isManaged()) {
               var11 = Math.max(var11, this.snapSizeX(var17.prefWidth(-1.0)));
            }
         }

         if (var13.getItems().size() > 0) {
            this.savedPrefWidth = var11;
         } else {
            var11 = this.savedPrefWidth;
         }
      }

      return var9 + var11 + var5;
   }

   @Override
   protected double computePrefHeight(double var1, double var3, double var5, double var7, double var9) {
      double var11 = 0.0;
      ToolBar var13 = this.getSkinnable();
      if (var13.getOrientation() == Orientation.VERTICAL) {
         for (Node var15 : var13.getItems()) {
            if (var15.isManaged()) {
               var11 += this.snapSizeY(var15.prefHeight(-1.0)) + this.getSpacing();
            }
         }

         var11 -= this.getSpacing();
      } else {
         for (Node var17 : var13.getItems()) {
            if (var17.isManaged()) {
               var11 = Math.max(var11, this.snapSizeY(var17.prefHeight(-1.0)));
            }
         }

         if (var13.getItems().size() > 0) {
            this.savedPrefHeight = var11;
         } else {
            var11 = this.savedPrefHeight;
         }
      }

      return var3 + var11 + var7;
   }

   @Override
   protected double computeMaxWidth(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().getOrientation() == Orientation.VERTICAL ? this.snapSizeX(this.getSkinnable().prefWidth(-1.0)) : Double.MAX_VALUE;
   }

   @Override
   protected double computeMaxHeight(double var1, double var3, double var5, double var7, double var9) {
      return this.getSkinnable().getOrientation() == Orientation.VERTICAL ? Double.MAX_VALUE : this.snapSizeY(this.getSkinnable().prefHeight(-1.0));
   }

   @Override
   protected void layoutChildren(double var1, double var3, double var5, double var7) {
      ToolBar var9 = this.getSkinnable();
      double var10 = this.getToolbarLength(var9);
      if (var9.getOrientation() == Orientation.VERTICAL) {
         if (this.snapSizeY(var9.getHeight()) == this.previousHeight && !this.needsUpdate) {
            this.correctOverflow(var10);
         } else {
            ((VBox)this.box).setSpacing(this.getSpacing());
            ((VBox)this.box).setAlignment(this.getBoxAlignment());
            this.previousHeight = this.snapSizeY(var9.getHeight());
            this.addNodesToToolBar();
         }
      } else if (this.snapSizeX(var9.getWidth()) == this.previousWidth && !this.needsUpdate) {
         this.correctOverflow(var10);
      } else {
         ((HBox)this.box).setSpacing(this.getSpacing());
         ((HBox)this.box).setAlignment(this.getBoxAlignment());
         this.previousWidth = this.snapSizeX(var9.getWidth());
         this.addNodesToToolBar();
      }

      this.needsUpdate = false;
      double var12 = var5;
      double var14 = var7;
      if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
         var14 = var7 - (this.overflow ? this.snapSizeY(this.overflowMenu.prefHeight(-1.0)) : 0.0);
      } else {
         var12 = var5 - (this.overflow ? this.snapSizeX(this.overflowMenu.prefWidth(-1.0)) : 0.0);
      }

      this.box.resize(var12, var14);
      this.positionInArea(this.box, var1, var3, var12, var14, 0.0, HPos.CENTER, VPos.CENTER);
      if (this.overflow) {
         double var16 = this.snapSizeX(this.overflowMenu.prefWidth(-1.0));
         double var18 = this.snapSizeY(this.overflowMenu.prefHeight(-1.0));
         double var20;
         double var22;
         if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
            if (var12 == 0.0) {
               var12 = this.savedPrefWidth;
            }

            HPos var24 = ((VBox)this.box).getAlignment().getHpos();
            if (HPos.LEFT.equals(var24)) {
               var20 = var1 + Math.abs((var12 - var16) / 2.0);
            } else if (HPos.RIGHT.equals(var24)) {
               var20 = this.snapSizeX(var9.getWidth()) - this.snappedRightInset() - var12 + Math.abs((var12 - var16) / 2.0);
            } else {
               var20 = var1 + Math.abs((this.snapSizeX(var9.getWidth()) - var1 + this.snappedRightInset() - var16) / 2.0);
            }

            var22 = this.snapSizeY(var9.getHeight()) - var18 - var3;
         } else {
            if (var14 == 0.0) {
               var14 = this.savedPrefHeight;
            }

            VPos var25 = ((HBox)this.box).getAlignment().getVpos();
            if (VPos.TOP.equals(var25)) {
               var22 = var3 + Math.abs((var14 - var18) / 2.0);
            } else if (VPos.BOTTOM.equals(var25)) {
               var22 = this.snapSizeY(var9.getHeight()) - this.snappedBottomInset() - var14 + Math.abs((var14 - var18) / 2.0);
            } else {
               var22 = var3 + Math.abs((var14 - var18) / 2.0);
            }

            var20 = this.snapSizeX(var9.getWidth()) - var16 - this.snappedRightInset();
         }

         this.overflowMenu.resize(var16, var18);
         this.positionInArea(this.overflowMenu, var20, var22, var16, var18, 0.0, HPos.CENTER, VPos.CENTER);
      }
   }

   private void initialize() {
      if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
         this.box = new VBox();
      } else {
         this.box = new HBox();
      }

      this.box.getStyleClass().add("container");
      this.box.getChildren().addAll(this.getSkinnable().getItems());
      this.overflowMenu = new ToolBarSkin.ToolBarOverflowMenu(this.overflowMenuItems);
      this.overflowMenu.setVisible(false);
      this.overflowMenu.setManaged(false);
      this.getChildren().clear();
      this.getChildren().add(this.box);
      this.getChildren().add(this.overflowMenu);
      this.previousWidth = 0.0;
      this.previousHeight = 0.0;
      this.savedPrefWidth = 0.0;
      this.savedPrefHeight = 0.0;
      this.needsUpdate = true;
      this.getSkinnable().requestLayout();
   }

   private void correctOverflow(double var1) {
      boolean var3 = this.isOverflowed(var1);
      if (var3 != this.overflow) {
         this.organizeOverflow(var1, this.overflow);
      }
   }

   private void organizeOverflow(double var1, boolean var3) {
      if (var3) {
         if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
            var1 -= this.snapSizeY(this.overflowMenu.prefHeight(-1.0));
         } else {
            var1 -= this.snapSizeX(this.overflowMenu.prefWidth(-1.0));
         }

         var1 -= this.getSpacing();
      }

      double var4 = 0.0;
      this.overflowMenuItems.clear();
      this.box.getChildren().clear();

      for (Node var7 : this.getSkinnable().getItems()) {
         var7.getStyleClass().remove("menu-item");
         var7.getStyleClass().remove("custom-menu-item");
         if (var7.isManaged()) {
            if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
               var4 += this.snapSizeY(var7.prefHeight(-1.0)) + this.getSpacing();
            } else {
               var4 += this.snapSizeX(var7.prefWidth(-1.0)) + this.getSpacing();
            }
         }

         if (var4 <= var1) {
            this.box.getChildren().add(var7);
         } else {
            if (var7.isFocused()) {
               if (!this.box.getChildren().isEmpty()) {
                  Node var8 = this.engine.selectLast();
                  if (var8 != null) {
                     var8.requestFocus();
                  }
               } else {
                  this.overflowMenu.requestFocus();
               }
            }

            if (var7 instanceof Separator) {
               this.overflowMenuItems.add(new SeparatorMenuItem());
            } else {
               CustomMenuItem var14 = new CustomMenuItem(var7);
               String var9 = var7.getTypeSelector();
               switch (var9) {
                  case "Button":
                  case "Hyperlink":
                  case "Label":
                     var14.setHideOnClick(true);
                     break;
                  case "CheckBox":
                  case "ChoiceBox":
                  case "ColorPicker":
                  case "ComboBox":
                  case "DatePicker":
                  case "MenuButton":
                  case "PasswordField":
                  case "RadioButton":
                  case "ScrollBar":
                  case "ScrollPane":
                  case "Slider":
                  case "SplitMenuButton":
                  case "SplitPane":
                  case "TextArea":
                  case "TextField":
                  case "ToggleButton":
                  case "ToolBar":
                  default:
                     var14.setHideOnClick(false);
               }

               this.overflowMenuItems.add(var14);
            }
         }
      }

      this.overflow = this.overflowMenuItems.size() > 0;
      if (!this.overflow && this.overflowMenu.isFocused()) {
         Node var13 = this.engine.selectLast();
         if (var13 != null) {
            var13.requestFocus();
         }
      }

      this.overflowMenu.setVisible(this.overflow);
      this.overflowMenu.setManaged(this.overflow);
   }

   private void addNodesToToolBar() {
      ToolBar var1 = this.getSkinnable();
      double var2 = this.getToolbarLength(var1);
      boolean var4 = this.isOverflowed(var2);
      this.organizeOverflow(var2, var4);
   }

   private double getToolbarLength(ToolBar var1) {
      double var2;
      if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
         var2 = this.snapSizeY(var1.getHeight()) - this.snappedTopInset() - this.snappedBottomInset() + this.getSpacing();
      } else {
         var2 = this.snapSizeX(var1.getWidth()) - this.snappedLeftInset() - this.snappedRightInset() + this.getSpacing();
      }

      return var2;
   }

   private boolean isOverflowed(double var1) {
      double var3 = 0.0;
      boolean var5 = false;

      for (Node var7 : this.getSkinnable().getItems()) {
         if (var7.isManaged()) {
            if (this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
               var3 += this.snapSizeY(var7.prefHeight(-1.0)) + this.getSpacing();
            } else {
               var3 += this.snapSizeX(var7.prefWidth(-1.0)) + this.getSpacing();
            }

            if (var3 > var1) {
               var5 = true;
               break;
            }
         }
      }

      return var5;
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return ToolBarSkin.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected Object queryAccessibleAttribute(AccessibleAttribute var1, Object... var2) {
      switch (var1) {
         case OVERFLOW_BUTTON:
            return this.overflowMenu;
         default:
            return super.queryAccessibleAttribute(var1, var2);
      }
   }

   @Override
   protected void executeAccessibleAction(AccessibleAction var1, Object... var2) {
      switch (var1) {
         case SHOW_MENU:
            this.overflowMenu.fire();
            break;
         default:
            super.executeAccessibleAction(var1, var2);
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<ToolBar, Number> SPACING = new CssMetaData<ToolBar, Number>("-fx-spacing", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(ToolBar var1) {
            ToolBarSkin var2 = (ToolBarSkin)var1.getSkin();
            return var2.spacing == null || !var2.spacing.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(ToolBar var1) {
            ToolBarSkin var2 = (ToolBarSkin)var1.getSkin();
            return (StyleableProperty<Number>)var2.spacingProperty();
         }
      };
      private static final CssMetaData<ToolBar, Pos> ALIGNMENT = new CssMetaData<ToolBar, Pos>("-fx-alignment", new EnumConverter(Pos.class), Pos.TOP_LEFT) {
         public boolean isSettable(ToolBar var1) {
            ToolBarSkin var2 = (ToolBarSkin)var1.getSkin();
            return var2.boxAlignment == null || !var2.boxAlignment.isBound();
         }

         public StyleableProperty<Pos> getStyleableProperty(ToolBar var1) {
            ToolBarSkin var2 = (ToolBarSkin)var1.getSkin();
            return (StyleableProperty<Pos>)var2.boxAlignmentProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(SkinBase.getClassCssMetaData());
         String var1 = ALIGNMENT.getProperty();
         int var2 = 0;

         for (int var3 = var0.size(); var2 < var3; var2++) {
            CssMetaData var4 = (CssMetaData)var0.get(var2);
            if (var1.equals(var4.getProperty())) {
               var0.remove(var4);
            }
         }

         var0.add(SPACING);
         var0.add(ALIGNMENT);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   class ToolBarOverflowMenu extends StackPane {
      private StackPane downArrow;
      private ContextMenu popup;
      private ObservableList<MenuItem> menuItems;

      public ToolBarOverflowMenu(ObservableList<MenuItem> var2) {
         this.getStyleClass().setAll(new String[]{"tool-bar-overflow-button"});
         this.setAccessibleRole(AccessibleRole.BUTTON);
         this.setAccessibleText(ControlResources.getString("Accessibility.title.ToolBar.OverflowButton"));
         this.setFocusTraversable(true);
         this.menuItems = var2;
         this.downArrow = new StackPane();
         this.downArrow.getStyleClass().setAll(new String[]{"arrow"});
         this.downArrow.setOnMousePressed(var1x -> this.fire());
         this.setOnKeyPressed(var1x -> {
            if (KeyCode.SPACE.equals(var1x.getCode())) {
               if (!this.popup.isShowing()) {
                  this.popup.getItems().clear();
                  this.popup.getItems().addAll(this.menuItems);
                  this.popup.show(this.downArrow, Side.BOTTOM, 0.0, 0.0);
               }

               var1x.consume();
            } else if (KeyCode.ESCAPE.equals(var1x.getCode())) {
               if (this.popup.isShowing()) {
                  this.popup.hide();
               }

               var1x.consume();
            } else if (KeyCode.ENTER.equals(var1x.getCode())) {
               this.fire();
               var1x.consume();
            }
         });
         this.visibleProperty().addListener((var1x, var2x, var3) -> {
            if (var3 && ToolBarSkin.this.box.getChildren().isEmpty()) {
               this.setFocusTraversable(true);
            }
         });
         this.popup = new ContextMenu();
         this.setVisible(false);
         this.setManaged(false);
         this.getChildren().add(this.downArrow);
      }

      private void fire() {
         if (this.popup.isShowing()) {
            this.popup.hide();
         } else {
            this.popup.getItems().clear();
            this.popup.getItems().addAll(this.menuItems);
            this.popup.show(this.downArrow, Side.BOTTOM, 0.0, 0.0);
         }
      }

      protected double computePrefWidth(double var1) {
         return this.snappedLeftInset() + this.snappedRightInset();
      }

      protected double computePrefHeight(double var1) {
         return this.snappedTopInset() + this.snappedBottomInset();
      }

      protected void layoutChildren() {
         double var1 = this.snapSizeX(this.downArrow.prefWidth(-1.0));
         double var3 = this.snapSizeY(this.downArrow.prefHeight(-1.0));
         double var5 = (this.snapSizeX(this.getWidth()) - var1) / 2.0;
         double var7 = (this.snapSizeY(this.getHeight()) - var3) / 2.0;
         if (ToolBarSkin.this.getSkinnable().getOrientation() == Orientation.VERTICAL) {
            this.downArrow.setRotate(0.0);
         }

         this.downArrow.resize(var1, var3);
         this.positionInArea(this.downArrow, var5, var7, var1, var3, 0.0, HPos.CENTER, VPos.CENTER);
      }

      public void executeAccessibleAction(AccessibleAction var1, Object... var2) {
         switch (var1) {
            case FIRE:
               this.fire();
               break;
            default:
               super.executeAccessibleAction(var1, new Object[0]);
         }
      }
   }
}
