package impl.org.controlsfx.skin;

import java.util.Objects;
import javafx.scene.Node;
import javafx.scene.control.TableRow;
import javafx.scene.control.skin.TableRowSkin;
import org.controlsfx.control.table.TableRowExpanderColumn;

public class ExpandableTableRowSkin<S> extends TableRowSkin<S> {
   private final TableRow<S> tableRow;
   private TableRowExpanderColumn<S> expander;
   private Double tableRowPrefHeight = -1.0;

   public ExpandableTableRowSkin(TableRow<S> tableRow, TableRowExpanderColumn<S> expander) {
      super(tableRow);
      this.tableRow = tableRow;
      this.expander = expander;
      tableRow.itemProperty().addListener((observable, oldValue, newValue) -> {
         if (oldValue != null) {
            Node expandedNode = this.expander.getExpandedNode((S)oldValue);
            if (expandedNode != null) {
               this.getChildren().remove(expandedNode);
            }
         }
      });
   }

   private Node getContent() {
      Node node = this.expander.getOrCreateExpandedNode(this.tableRow);
      if (!this.getChildren().contains(node)) {
         this.getChildren().add(node);
      }

      return node;
   }

   private Boolean isExpanded() {
      return ((TableRow)this.getSkinnable()).getItem() != null
         && (Boolean)Objects.requireNonNullElse((Boolean)this.expander.getCellData(((TableRow)this.getSkinnable()).getIndex()), false);
   }

   protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
      this.tableRowPrefHeight = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
      return this.isExpanded() ? this.tableRowPrefHeight + this.getContent().prefHeight(width) : this.tableRowPrefHeight;
   }

   protected void layoutChildren(double x, double y, double w, double h) {
      super.layoutChildren(x, y, w, h);
      if (this.isExpanded()) {
         this.getContent().resizeRelocate(0.0, this.tableRowPrefHeight, w, h - this.tableRowPrefHeight);
      }
   }
}
