package com.sun.javafx.charts;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;

public class Legend extends TilePane {
   private static final int GAP = 5;
   private ListChangeListener<Legend.LegendItem> itemsListener = var1 -> {
      List var2 = this.getItems().stream().map(var0 -> var0.label).collect(Collectors.toList());
      this.getChildren().setAll(var2);
      if (this.isVisible()) {
         this.requestLayout();
      }
   };
   private BooleanProperty vertical = new BooleanPropertyBase(false) {
      protected void invalidated() {
         Legend.this.setOrientation(this.get() ? Orientation.VERTICAL : Orientation.HORIZONTAL);
      }

      public Object getBean() {
         return Legend.this;
      }

      public String getName() {
         return "vertical";
      }
   };
   private ObjectProperty<ObservableList<Legend.LegendItem>> items = new ObjectPropertyBase<ObservableList<Legend.LegendItem>>() {
      ObservableList<Legend.LegendItem> oldItems = null;

      protected void invalidated() {
         if (this.oldItems != null) {
            this.oldItems.removeListener(Legend.this.itemsListener);
         }

         Legend.this.getChildren().clear();
         ObservableList var1 = (ObservableList)this.get();
         if (var1 != null) {
            var1.addListener(Legend.this.itemsListener);
            List var2 = var1.stream().map(var0 -> var0.label).collect(Collectors.toList());
            Legend.this.getChildren().addAll(var2);
         }

         this.oldItems = var1;
         Legend.this.requestLayout();
      }

      public Object getBean() {
         return Legend.this;
      }

      public String getName() {
         return "items";
      }
   };

   public final boolean isVertical() {
      return this.vertical.get();
   }

   public final void setVertical(boolean var1) {
      this.vertical.set(var1);
   }

   public final BooleanProperty verticalProperty() {
      return this.vertical;
   }

   public final void setItems(ObservableList<Legend.LegendItem> var1) {
      this.itemsProperty().set(var1);
   }

   public final ObservableList<Legend.LegendItem> getItems() {
      return (ObservableList<Legend.LegendItem>)this.items.get();
   }

   public final ObjectProperty<ObservableList<Legend.LegendItem>> itemsProperty() {
      return this.items;
   }

   public Legend() {
      super(5.0, 5.0);
      this.setTileAlignment(Pos.CENTER_LEFT);
      this.setItems(FXCollections.observableArrayList());
      this.getStyleClass().setAll(new String[]{"chart-legend"});
   }

   protected double computePrefWidth(double var1) {
      return this.getItems().size() > 0 ? super.computePrefWidth(var1) : 0.0;
   }

   protected double computePrefHeight(double var1) {
      return this.getItems().size() > 0 ? super.computePrefHeight(var1) : 0.0;
   }

   public static class LegendItem {
      private Label label = new Label();
      private StringProperty text = new StringPropertyBase() {
         protected void invalidated() {
            LegendItem.this.label.setText(this.get());
         }

         public Object getBean() {
            return LegendItem.this;
         }

         public String getName() {
            return "text";
         }
      };
      private ObjectProperty<Node> symbol = new ObjectPropertyBase<Node>(new Region()) {
         protected void invalidated() {
            Node var1 = (Node)this.get();
            if (var1 != null) {
               var1.getStyleClass().setAll(new String[]{"chart-legend-item-symbol"});
            }

            LegendItem.this.label.setGraphic(var1);
         }

         public Object getBean() {
            return LegendItem.this;
         }

         public String getName() {
            return "symbol";
         }
      };

      public final String getText() {
         return this.text.getValue();
      }

      public final void setText(String var1) {
         this.text.setValue(var1);
      }

      public final StringProperty textProperty() {
         return this.text;
      }

      public final Node getSymbol() {
         return (Node)this.symbol.getValue();
      }

      public final void setSymbol(Node var1) {
         this.symbol.setValue(var1);
      }

      public final ObjectProperty<Node> symbolProperty() {
         return this.symbol;
      }

      public LegendItem(String var1) {
         this.setText(var1);
         this.label.getStyleClass().add("chart-legend-item");
         this.label.setAlignment(Pos.CENTER_LEFT);
         this.label.setContentDisplay(ContentDisplay.LEFT);
         this.label.setGraphic(this.getSymbol());
         this.getSymbol().getStyleClass().setAll(new String[]{"chart-legend-item-symbol"});
      }

      public LegendItem(String var1, Node var2) {
         this(var1);
         this.setSymbol(var2);
      }
   }
}
