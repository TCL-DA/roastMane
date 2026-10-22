package javafx.scene.control;

import com.sun.javafx.collections.UnmodifiableListSet;
import com.sun.javafx.scene.control.TabObservableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Side;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.skin.TabPaneSkin;

@DefaultProperty("tabs")
public class TabPane extends Control {
   private static final double DEFAULT_TAB_MIN_WIDTH = 0.0;
   private static final double DEFAULT_TAB_MAX_WIDTH = Double.MAX_VALUE;
   private static final double DEFAULT_TAB_MIN_HEIGHT = 0.0;
   private static final double DEFAULT_TAB_MAX_HEIGHT = Double.MAX_VALUE;
   public static final String STYLE_CLASS_FLOATING = "floating";
   private ObservableList<Tab> tabs = new TabObservableList<Tab>(new ArrayList<>());
   private ObjectProperty<SingleSelectionModel<Tab>> selectionModel = new SimpleObjectProperty(this, "selectionModel");
   private ObjectProperty<Side> side;
   private ObjectProperty<TabPane.TabClosingPolicy> tabClosingPolicy;
   private BooleanProperty rotateGraphic;
   private DoubleProperty tabMinWidth;
   private DoubleProperty tabMaxWidth;
   private DoubleProperty tabMinHeight;
   private DoubleProperty tabMaxHeight;
   private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
   private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
   private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
   private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");
   private ObjectProperty<TabPane.TabDragPolicy> tabDragPolicy;

   public TabPane() {
      this((Tab[])null);
   }

   public TabPane(Tab... var1) {
      this.getStyleClass().setAll(new String[]{"tab-pane"});
      this.setAccessibleRole(AccessibleRole.TAB_PANE);
      this.setSelectionModel(new TabPane.TabPaneSelectionModel(this));
      this.tabs.addListener(var1x -> {
         while (var1x.next()) {
            for (Tab var3 : var1x.getRemoved()) {
               if (var3 != null && !this.getTabs().contains(var3)) {
                  var3.setTabPane(null);
               }
            }

            for (Tab var5 : var1x.getAddedSubList()) {
               if (var5 != null) {
                  var5.setTabPane(this);
               }
            }
         }
      });
      if (var1 != null) {
         this.getTabs().addAll(var1);
      }

      Side var2 = this.getSide();
      this.pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, var2 == Side.TOP);
      this.pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, var2 == Side.RIGHT);
      this.pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, var2 == Side.BOTTOM);
      this.pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, var2 == Side.LEFT);
   }

   public final ObservableList<Tab> getTabs() {
      return this.tabs;
   }

   public final void setSelectionModel(SingleSelectionModel<Tab> var1) {
      this.selectionModel.set(var1);
   }

   public final SingleSelectionModel<Tab> getSelectionModel() {
      return (SingleSelectionModel<Tab>)this.selectionModel.get();
   }

   public final ObjectProperty<SingleSelectionModel<Tab>> selectionModelProperty() {
      return this.selectionModel;
   }

   public final void setSide(Side var1) {
      this.sideProperty().set(var1);
   }

   public final Side getSide() {
      return this.side == null ? Side.TOP : (Side)this.side.get();
   }

   public final ObjectProperty<Side> sideProperty() {
      if (this.side == null) {
         this.side = new ObjectPropertyBase<Side>(Side.TOP) {
            private Side oldSide;

            protected void invalidated() {
               this.oldSide = (Side)this.get();
               TabPane.this.pseudoClassStateChanged(TabPane.TOP_PSEUDOCLASS_STATE, this.oldSide == Side.TOP || this.oldSide == null);
               TabPane.this.pseudoClassStateChanged(TabPane.RIGHT_PSEUDOCLASS_STATE, this.oldSide == Side.RIGHT);
               TabPane.this.pseudoClassStateChanged(TabPane.BOTTOM_PSEUDOCLASS_STATE, this.oldSide == Side.BOTTOM);
               TabPane.this.pseudoClassStateChanged(TabPane.LEFT_PSEUDOCLASS_STATE, this.oldSide == Side.LEFT);
            }

            public Object getBean() {
               return TabPane.this;
            }

            public String getName() {
               return "side";
            }
         };
      }

      return this.side;
   }

   public final void setTabClosingPolicy(TabPane.TabClosingPolicy var1) {
      this.tabClosingPolicyProperty().set(var1);
   }

   public final TabPane.TabClosingPolicy getTabClosingPolicy() {
      return this.tabClosingPolicy == null ? TabPane.TabClosingPolicy.SELECTED_TAB : (TabPane.TabClosingPolicy)this.tabClosingPolicy.get();
   }

   public final ObjectProperty<TabPane.TabClosingPolicy> tabClosingPolicyProperty() {
      if (this.tabClosingPolicy == null) {
         this.tabClosingPolicy = new SimpleObjectProperty(this, "tabClosingPolicy", TabPane.TabClosingPolicy.SELECTED_TAB);
      }

      return this.tabClosingPolicy;
   }

   public final void setRotateGraphic(boolean var1) {
      this.rotateGraphicProperty().set(var1);
   }

   public final boolean isRotateGraphic() {
      return this.rotateGraphic == null ? false : this.rotateGraphic.get();
   }

   public final BooleanProperty rotateGraphicProperty() {
      if (this.rotateGraphic == null) {
         this.rotateGraphic = new SimpleBooleanProperty(this, "rotateGraphic", false);
      }

      return this.rotateGraphic;
   }

   public final void setTabMinWidth(double var1) {
      this.tabMinWidthProperty().setValue(var1);
   }

   public final double getTabMinWidth() {
      return this.tabMinWidth == null ? 0.0 : this.tabMinWidth.getValue();
   }

   public final DoubleProperty tabMinWidthProperty() {
      if (this.tabMinWidth == null) {
         this.tabMinWidth = new StyleableDoubleProperty(0.0) {
            public CssMetaData<TabPane, Number> getCssMetaData() {
               return TabPane.StyleableProperties.TAB_MIN_WIDTH;
            }

            public Object getBean() {
               return TabPane.this;
            }

            public String getName() {
               return "tabMinWidth";
            }
         };
      }

      return this.tabMinWidth;
   }

   public final void setTabMaxWidth(double var1) {
      this.tabMaxWidthProperty().setValue(var1);
   }

   public final double getTabMaxWidth() {
      return this.tabMaxWidth == null ? Double.MAX_VALUE : this.tabMaxWidth.getValue();
   }

   public final DoubleProperty tabMaxWidthProperty() {
      if (this.tabMaxWidth == null) {
         this.tabMaxWidth = new StyleableDoubleProperty(Double.MAX_VALUE) {
            public CssMetaData<TabPane, Number> getCssMetaData() {
               return TabPane.StyleableProperties.TAB_MAX_WIDTH;
            }

            public Object getBean() {
               return TabPane.this;
            }

            public String getName() {
               return "tabMaxWidth";
            }
         };
      }

      return this.tabMaxWidth;
   }

   public final void setTabMinHeight(double var1) {
      this.tabMinHeightProperty().setValue(var1);
   }

   public final double getTabMinHeight() {
      return this.tabMinHeight == null ? 0.0 : this.tabMinHeight.getValue();
   }

   public final DoubleProperty tabMinHeightProperty() {
      if (this.tabMinHeight == null) {
         this.tabMinHeight = new StyleableDoubleProperty(0.0) {
            public CssMetaData<TabPane, Number> getCssMetaData() {
               return TabPane.StyleableProperties.TAB_MIN_HEIGHT;
            }

            public Object getBean() {
               return TabPane.this;
            }

            public String getName() {
               return "tabMinHeight";
            }
         };
      }

      return this.tabMinHeight;
   }

   public final void setTabMaxHeight(double var1) {
      this.tabMaxHeightProperty().setValue(var1);
   }

   public final double getTabMaxHeight() {
      return this.tabMaxHeight == null ? Double.MAX_VALUE : this.tabMaxHeight.getValue();
   }

   public final DoubleProperty tabMaxHeightProperty() {
      if (this.tabMaxHeight == null) {
         this.tabMaxHeight = new StyleableDoubleProperty(Double.MAX_VALUE) {
            public CssMetaData<TabPane, Number> getCssMetaData() {
               return TabPane.StyleableProperties.TAB_MAX_HEIGHT;
            }

            public Object getBean() {
               return TabPane.this;
            }

            public String getName() {
               return "tabMaxHeight";
            }
         };
      }

      return this.tabMaxHeight;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new TabPaneSkin(this);
   }

   public Node lookup(String var1) {
      Node var2 = super.lookup(var1);
      if (var2 == null) {
         for (Tab var4 : this.tabs) {
            var2 = var4.lookup(var1);
            if (var2 != null) {
               break;
            }
         }
      }

      return var2;
   }

   public Set<Node> lookupAll(String var1) {
      if (var1 == null) {
         return null;
      } else {
         ArrayList var2 = new ArrayList();
         var2.addAll(super.lookupAll(var1));

         for (Tab var4 : this.tabs) {
            var2.addAll(var4.lookupAll(var1));
         }

         return new UnmodifiableListSet(var2);
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return TabPane.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   public final ObjectProperty<TabPane.TabDragPolicy> tabDragPolicyProperty() {
      if (this.tabDragPolicy == null) {
         this.tabDragPolicy = new SimpleObjectProperty(this, "tabDragPolicy", TabPane.TabDragPolicy.FIXED);
      }

      return this.tabDragPolicy;
   }

   public final void setTabDragPolicy(TabPane.TabDragPolicy var1) {
      this.tabDragPolicyProperty().set(var1);
   }

   public final TabPane.TabDragPolicy getTabDragPolicy() {
      return (TabPane.TabDragPolicy)this.tabDragPolicyProperty().get();
   }

   private static class StyleableProperties {
      private static final CssMetaData<TabPane, Number> TAB_MIN_WIDTH = new CssMetaData<TabPane, Number>("-fx-tab-min-width", SizeConverter.getInstance(), 0.0) {
         public boolean isSettable(TabPane var1) {
            return var1.tabMinWidth == null || !var1.tabMinWidth.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TabPane var1) {
            return (StyleableProperty<Number>)var1.tabMinWidthProperty();
         }
      };
      private static final CssMetaData<TabPane, Number> TAB_MAX_WIDTH = new CssMetaData<TabPane, Number>(
         "-fx-tab-max-width", SizeConverter.getInstance(), Double.MAX_VALUE
      ) {
         public boolean isSettable(TabPane var1) {
            return var1.tabMaxWidth == null || !var1.tabMaxWidth.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TabPane var1) {
            return (StyleableProperty<Number>)var1.tabMaxWidthProperty();
         }
      };
      private static final CssMetaData<TabPane, Number> TAB_MIN_HEIGHT = new CssMetaData<TabPane, Number>(
         "-fx-tab-min-height", SizeConverter.getInstance(), 0.0
      ) {
         public boolean isSettable(TabPane var1) {
            return var1.tabMinHeight == null || !var1.tabMinHeight.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TabPane var1) {
            return (StyleableProperty<Number>)var1.tabMinHeightProperty();
         }
      };
      private static final CssMetaData<TabPane, Number> TAB_MAX_HEIGHT = new CssMetaData<TabPane, Number>(
         "-fx-tab-max-height", SizeConverter.getInstance(), Double.MAX_VALUE
      ) {
         public boolean isSettable(TabPane var1) {
            return var1.tabMaxHeight == null || !var1.tabMaxHeight.isBound();
         }

         public StyleableProperty<Number> getStyleableProperty(TabPane var1) {
            return (StyleableProperty<Number>)var1.tabMaxHeightProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(TAB_MIN_WIDTH);
         var0.add(TAB_MAX_WIDTH);
         var0.add(TAB_MIN_HEIGHT);
         var0.add(TAB_MAX_HEIGHT);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }

   public static enum TabClosingPolicy {
      SELECTED_TAB,
      ALL_TABS,
      UNAVAILABLE;
   }

   public static enum TabDragPolicy {
      FIXED,
      REORDER;
   }

   static class TabPaneSelectionModel extends SingleSelectionModel<Tab> {
      private final TabPane tabPane;
      private ListChangeListener<Tab> itemsContentObserver;

      public TabPaneSelectionModel(TabPane var1) {
         if (var1 == null) {
            throw new NullPointerException("TabPane can not be null");
         } else {
            this.tabPane = var1;
            this.itemsContentObserver = var1x -> {
               while (var1x.next()) {
                  for (Tab var3 : var1x.getRemoved()) {
                     if (var3 != null && !this.tabPane.getTabs().contains(var3) && var3.isSelected()) {
                        var3.setSelected(false);
                        int var4 = var1x.getFrom();
                        this.findNearestAvailableTab(var4, true);
                     }
                  }

                  if ((var1x.wasAdded() || var1x.wasRemoved()) && this.getSelectedIndex() != this.tabPane.getTabs().indexOf(this.getSelectedItem())) {
                     this.clearAndSelect(this.tabPane.getTabs().indexOf(this.getSelectedItem()));
                  }
               }

               if (this.getSelectedIndex() == -1 && this.getSelectedItem() == null && this.tabPane.getTabs().size() > 0) {
                  this.findNearestAvailableTab(0, true);
               } else if (this.tabPane.getTabs().isEmpty()) {
                  this.clearSelection();
               }
            };
            if (this.tabPane.getTabs() != null) {
               this.tabPane.getTabs().addListener(new WeakListChangeListener(this.itemsContentObserver));
            }
         }
      }

      @Override
      public void select(int var1) {
         if (var1 >= 0
            && (this.getItemCount() <= 0 || var1 < this.getItemCount())
            && (var1 != this.getSelectedIndex() || !this.getModelItem(var1).isSelected())) {
            if (this.getSelectedIndex() >= 0 && this.getSelectedIndex() < this.tabPane.getTabs().size()) {
               ((Tab)this.tabPane.getTabs().get(this.getSelectedIndex())).setSelected(false);
            }

            this.setSelectedIndex(var1);
            Tab var2 = this.getModelItem(var1);
            if (var2 != null) {
               this.setSelectedItem(var2);
            }

            if (this.getSelectedIndex() >= 0 && this.getSelectedIndex() < this.tabPane.getTabs().size()) {
               ((Tab)this.tabPane.getTabs().get(this.getSelectedIndex())).setSelected(true);
            }

            this.tabPane.notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_ITEM);
         }
      }

      public void select(Tab var1) {
         int var2 = this.getItemCount();

         for (int var3 = 0; var3 < var2; var3++) {
            Tab var4 = this.getModelItem(var3);
            if (var4 != null && var4.equals(var1)) {
               this.select(var3);
               return;
            }
         }
      }

      protected Tab getModelItem(int var1) {
         ObservableList var2 = this.tabPane.getTabs();
         if (var2 == null) {
            return null;
         } else {
            return var1 >= 0 && var1 < var2.size() ? (Tab)var2.get(var1) : null;
         }
      }

      @Override
      protected int getItemCount() {
         ObservableList var1 = this.tabPane.getTabs();
         return var1 == null ? 0 : var1.size();
      }

      private Tab findNearestAvailableTab(int var1, boolean var2) {
         int var3 = this.getItemCount();
         int var4 = 1;
         Tab var5 = null;

         while (true) {
            int var6 = var1 - var4;
            if (var6 >= 0) {
               Tab var7 = this.getModelItem(var6);
               if (var7 != null && !var7.isDisable()) {
                  var5 = var7;
                  break;
               }
            }

            int var9 = var1 + var4 - 1;
            if (var9 < var3) {
               Tab var8 = this.getModelItem(var9);
               if (var8 != null && !var8.isDisable()) {
                  var5 = var8;
                  break;
               }
            }

            if (var6 < 0 && var9 >= var3) {
               break;
            }

            var4++;
         }

         if (var2 && var5 != null) {
            this.select(var5);
         }

         return var5;
      }
   }
}
