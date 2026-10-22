package javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.EnumConverter;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.skin.SplitPaneSkin;

@DefaultProperty("items")
public class SplitPane extends Control {
   private static final String RESIZABLE_WITH_PARENT = "resizable-with-parent";
   private ObjectProperty<Orientation> orientation;
   private final ObservableList<Node> items = FXCollections.observableArrayList();
   private final ObservableList<SplitPane.Divider> dividers = FXCollections.observableArrayList();
   private final ObservableList<SplitPane.Divider> unmodifiableDividers = FXCollections.unmodifiableObservableList(this.dividers);
   private final WeakHashMap<Integer, Double> dividerCache = new WeakHashMap<>();
   private static final String DEFAULT_STYLE_CLASS = "split-pane";
   private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
   private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");

   public static void setResizableWithParent(Node var0, Boolean var1) {
      if (var1 == null) {
         var0.getProperties().remove("resizable-with-parent");
      } else {
         var0.getProperties().put("resizable-with-parent", var1);
      }
   }

   public static Boolean isResizableWithParent(Node var0) {
      if (var0.hasProperties()) {
         Object var1 = var0.getProperties().get("resizable-with-parent");
         if (var1 != null) {
            return (Boolean)var1;
         }
      }

      return true;
   }

   public SplitPane() {
      this((Node[])null);
   }

   public SplitPane(Node... var1) {
      this.getStyleClass().setAll(new String[]{"split-pane"});
      ((StyleableProperty)this.focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
      this.getItems().addListener(new ListChangeListener<Node>() {
         public void onChanged(Change<? extends Node> var1) {
            while (var1.next()) {
               int var5 = var1.getFrom();
               int var6 = var5;

               for (int var4 = 0; var4 < var1.getRemovedSize(); var4++) {
                  if (var6 < SplitPane.this.dividers.size()) {
                     SplitPane.this.dividerCache.put(var6, Double.MAX_VALUE);
                  } else if (var6 == SplitPane.this.dividers.size() && !SplitPane.this.dividers.isEmpty()) {
                     if (var1.wasReplaced()) {
                        SplitPane.this.dividerCache.put(var6 - 1, ((SplitPane.Divider)SplitPane.this.dividers.get(var6 - 1)).getPosition());
                     } else {
                        SplitPane.this.dividerCache.put(var6 - 1, Double.MAX_VALUE);
                     }
                  }

                  var6++;
               }

               for (int var7 = 0; var7 < SplitPane.this.dividers.size(); var7++) {
                  if (SplitPane.this.dividerCache.get(var7) == null) {
                     SplitPane.this.dividerCache.put(var7, ((SplitPane.Divider)SplitPane.this.dividers.get(var7)).getPosition());
                  }
               }
            }

            SplitPane.this.dividers.clear();

            for (int var2 = 0; var2 < SplitPane.this.getItems().size() - 1; var2++) {
               if (SplitPane.this.dividerCache.containsKey(var2) && SplitPane.this.dividerCache.get(var2) != Double.MAX_VALUE) {
                  SplitPane.Divider var3 = new SplitPane.Divider();
                  var3.setPosition(SplitPane.this.dividerCache.get(var2));
                  SplitPane.this.dividers.add(var3);
               } else {
                  SplitPane.this.dividers.add(new SplitPane.Divider());
               }

               SplitPane.this.dividerCache.remove(var2);
            }
         }
      });
      if (var1 != null) {
         this.getItems().addAll(var1);
      }

      this.pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, true);
   }

   public final void setOrientation(Orientation var1) {
      this.orientationProperty().set(var1);
   }

   public final Orientation getOrientation() {
      return this.orientation == null ? Orientation.HORIZONTAL : (Orientation)this.orientation.get();
   }

   public final ObjectProperty<Orientation> orientationProperty() {
      if (this.orientation == null) {
         this.orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
            public void invalidated() {
               boolean var1 = this.get() == Orientation.VERTICAL;
               SplitPane.this.pseudoClassStateChanged(SplitPane.VERTICAL_PSEUDOCLASS_STATE, var1);
               SplitPane.this.pseudoClassStateChanged(SplitPane.HORIZONTAL_PSEUDOCLASS_STATE, !var1);
            }

            public CssMetaData<SplitPane, Orientation> getCssMetaData() {
               return SplitPane.StyleableProperties.ORIENTATION;
            }

            public Object getBean() {
               return SplitPane.this;
            }

            public String getName() {
               return "orientation";
            }
         };
      }

      return this.orientation;
   }

   public ObservableList<Node> getItems() {
      return this.items;
   }

   public ObservableList<SplitPane.Divider> getDividers() {
      return this.unmodifiableDividers;
   }

   public void setDividerPosition(int var1, double var2) {
      if (this.getDividers().size() <= var1) {
         this.dividerCache.put(var1, var2);
      } else {
         if (var1 >= 0) {
            ((SplitPane.Divider)this.getDividers().get(var1)).setPosition(var2);
         }
      }
   }

   public void setDividerPositions(double... var1) {
      if (this.dividers.isEmpty()) {
         for (int var3 = 0; var3 < var1.length; var3++) {
            this.dividerCache.put(var3, var1[var3]);
         }
      } else {
         for (int var2 = 0; var2 < var1.length && var2 < this.dividers.size(); var2++) {
            ((SplitPane.Divider)this.dividers.get(var2)).setPosition(var1[var2]);
         }
      }
   }

   public double[] getDividerPositions() {
      double[] var1 = new double[this.dividers.size()];

      for (int var2 = 0; var2 < this.dividers.size(); var2++) {
         var1[var2] = ((SplitPane.Divider)this.dividers.get(var2)).getPosition();
      }

      return var1;
   }

   @Override
   protected Skin<?> createDefaultSkin() {
      return new SplitPaneSkin(this);
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return SplitPane.StyleableProperties.STYLEABLES;
   }

   @Override
   public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
      return getClassCssMetaData();
   }

   @Override
   protected Boolean getInitialFocusTraversable() {
      return Boolean.FALSE;
   }

   public static class Divider {
      private DoubleProperty position;

      public final void setPosition(double var1) {
         this.positionProperty().set(var1);
      }

      public final double getPosition() {
         return this.position == null ? 0.5 : this.position.get();
      }

      public final DoubleProperty positionProperty() {
         if (this.position == null) {
            this.position = new SimpleDoubleProperty(this, "position", 0.5);
         }

         return this.position;
      }
   }

   private static class StyleableProperties {
      private static final CssMetaData<SplitPane, Orientation> ORIENTATION = new CssMetaData<SplitPane, Orientation>(
         "-fx-orientation", new EnumConverter(Orientation.class), Orientation.HORIZONTAL
      ) {
         public Orientation getInitialValue(SplitPane var1) {
            return var1.getOrientation();
         }

         public boolean isSettable(SplitPane var1) {
            return var1.orientation == null || !var1.orientation.isBound();
         }

         public StyleableProperty<Orientation> getStyleableProperty(SplitPane var1) {
            return (StyleableProperty<Orientation>)var1.orientationProperty();
         }
      };
      private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

      static {
         ArrayList var0 = new ArrayList<>(Control.getClassCssMetaData());
         var0.add(ORIENTATION);
         STYLEABLES = Collections.unmodifiableList(var0);
      }
   }
}
