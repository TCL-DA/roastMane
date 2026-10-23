package com.cropster.roastingintelligence.ui.util;

import java.util.ArrayList;
import java.util.function.BiFunction;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public final class GridPaneZebraStyle implements ListChangeListener<Node> {
   public static final String STYLE_GRAY = "-fx-background-color: -fx-color-neutral-20";
   public static final String STYLE_TRANSPARENT = "";
   private GridPane pane;
   private BiFunction<Integer, Integer, String> styleSelector;
   private BiFunction<Integer, Integer, Pos> alignmentSelector;

   public static GridPaneZebraStyle apply(GridPane pane, BiFunction<Integer, Integer, String> styleSelector) {
      GridPaneZebraStyle zebraStyle = new GridPaneZebraStyle(pane, styleSelector);
      pane.getChildren().addListener(zebraStyle);
      return zebraStyle;
   }

   public static GridPaneZebraStyle apply(GridPane pane) {
      return apply(pane, (r, c) -> r % 2 == 0 ? "-fx-background-color: -fx-color-neutral-20" : "");
   }

   public static void setAlignment(Node node, Pos alignment) {
      StackPane.setAlignment(node, alignment);
   }

   private GridPaneZebraStyle(GridPane pane, BiFunction<Integer, Integer, String> styleSelector) {
      this.pane = pane;
      this.styleSelector = styleSelector;
   }

   public GridPaneZebraStyle withAlignmentSelector(BiFunction<Integer, Integer, Pos> alignmentSelector) {
      this.alignmentSelector = alignmentSelector;
      return this;
   }

   public void applyToExisting() {
      for (Node node : new ArrayList<>(this.pane.getChildren())) {
         this.applyStyle(node);
      }
   }

   private void applyStyle(Node node) {
      if (!(node instanceof StackPane)) {
         Integer rowIndex = GridPane.getRowIndex(node);
         if (rowIndex != null && rowIndex >= 0) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            if (columnIndex != null) {
               Integer columnSpan = GridPane.getColumnSpan(node);
               Integer rowSpan = GridPane.getRowSpan(node);
               this.pane.getChildren().remove(node);
               StackPane stackPane = new StackPane();
               stackPane.getChildren().add(node);
               if (this.alignmentSelector != null) {
                  StackPane.setAlignment(node, this.alignmentSelector.apply(rowIndex, columnIndex));
               } else {
                  StackPane.setAlignment(node, Pos.TOP_LEFT);
               }

               if (this.styleSelector != null) {
                  stackPane.setStyle(this.styleSelector.apply(rowIndex, columnIndex));
               }

               this.pane.add(stackPane, columnIndex, rowIndex, columnSpan != null ? columnSpan : 1, rowSpan != null ? rowSpan : 1);
            }
         }
      }
   }

   public void onChanged(Change<? extends Node> c) {
      while (c.next()) {
         if (c.wasAdded()) {
            for (Node node : c.getAddedSubList()) {
               this.applyStyle(node);
            }
         }
      }
   }
}
