package org.controlsfx.control.table;

import impl.org.controlsfx.skin.ExpandableTableRowSkin;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.util.Callback;

public final class TableRowExpanderColumn<S> extends TableColumn<S, Boolean> {
   private static final String STYLE_CLASS = "expander-column";
   private static final String EXPANDER_BUTTON_STYLE_CLASS = "expander-button";
   private final Map<S, Node> expandedNodeCache = new HashMap<>();
   private final Map<S, BooleanProperty> expansionState = new HashMap<>();
   private Callback<TableRowExpanderColumn.TableRowDataFeatures<S>, Node> expandedNodeCallback;

   public BooleanProperty getExpandedProperty(S item) {
      BooleanProperty value = this.expansionState.get(item);
      if (value == null) {
         value = new SimpleBooleanProperty(item, "expanded", false) {
            protected void invalidated() {
               TableRowExpanderColumn.this.getTableView().refresh();
               if (!this.getValue()) {
                  TableRowExpanderColumn.this.expandedNodeCache.remove(this.getBean());
               }
            }
         };
         this.expansionState.put(item, value);
      }

      return value;
   }

   public Node getOrCreateExpandedNode(TableRow<S> tableRow) {
      int index = tableRow.getIndex();
      if (index > -1 && index < this.getTableView().getItems().size()) {
         S item = (S)this.getTableView().getItems().get(index);
         Node node = this.expandedNodeCache.get(item);
         if (node == null) {
            node = (Node)this.expandedNodeCallback.call(new TableRowExpanderColumn.TableRowDataFeatures<>(tableRow, this, item));
            this.expandedNodeCache.put(item, node);
         }

         return node;
      } else {
         return null;
      }
   }

   public Node getExpandedNode(S item) {
      return this.expandedNodeCache.get(item);
   }

   public TableRowExpanderColumn(Callback<TableRowExpanderColumn.TableRowDataFeatures<S>, Node> expandedNodeCallback) {
      this.expandedNodeCallback = expandedNodeCallback;
      this.getStyleClass().add("expander-column");
      this.setCellValueFactory(param -> this.getExpandedProperty((S)param.getValue()));
      this.setCellFactory(param -> new TableRowExpanderColumn.ToggleCell());
      this.installRowFactoryOnTableViewAssignment();
   }

   private void installRowFactoryOnTableViewAssignment() {
      this.tableViewProperty().addListener((observable, oldValue, newValue) -> {
         if (newValue != null) {
            this.getTableView().setRowFactory(param -> new TableRow<S>() {
                  protected Skin<?> createDefaultSkin() {
                     return new ExpandableTableRowSkin(this, TableRowExpanderColumn.this);
                  }
               });
         }
      });
   }

   public void toggleExpanded(int index) {
      BooleanProperty expanded = (BooleanProperty)this.getCellObservableValue(index);
      expanded.setValue(!expanded.getValue());
   }

   public static final class TableRowDataFeatures<S> {
      private TableRow<S> tableRow;
      private TableRowExpanderColumn<S> tableColumn;
      private BooleanProperty expandedProperty;
      private S value;

      public TableRowDataFeatures(TableRow<S> tableRow, TableRowExpanderColumn<S> tableColumn, S value) {
         this.tableRow = tableRow;
         this.tableColumn = tableColumn;
         this.expandedProperty = (BooleanProperty)tableColumn.getCellObservableValue(tableRow.getIndex());
         this.value = value;
      }

      public TableRow<S> getTableRow() {
         return this.tableRow;
      }

      public TableRowExpanderColumn<S> getTableColumn() {
         return this.tableColumn;
      }

      public BooleanProperty expandedProperty() {
         return this.expandedProperty;
      }

      public void toggleExpanded() {
         BooleanProperty expanded = this.expandedProperty();
         expanded.setValue(!expanded.getValue());
      }

      public Boolean isExpanded() {
         return this.expandedProperty().getValue();
      }

      public void setExpanded(Boolean expanded) {
         this.expandedProperty().setValue(expanded);
      }

      public S getValue() {
         return this.value;
      }
   }

   private final class ToggleCell extends TableCell<S, Boolean> {
      private Button button = new Button();

      public ToggleCell() {
         this.button.setFocusTraversable(false);
         this.button.getStyleClass().add("expander-button");
         this.button.setPrefSize(16.0, 16.0);
         this.button.setPadding(new Insets(0.0));
         this.button.setOnAction(event -> TableRowExpanderColumn.this.toggleExpanded(this.getIndex()));
      }

      protected void updateItem(Boolean expanded, boolean empty) {
         super.updateItem(expanded, empty);
         if (expanded != null && !empty) {
            this.button.setText(expanded ? "-" : "+");
            this.setGraphic(this.button);
         } else {
            this.setGraphic(null);
         }
      }
   }
}
