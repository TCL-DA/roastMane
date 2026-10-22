package impl.org.controlsfx.tableview2;

import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import org.controlsfx.control.tableview2.TableView2;

public class SouthTableHeaderRow extends Region {
   public static final String SOUTH_HEADER_STYLE = "south";
   private final TableView2Skin<?> skin;
   private final TableView2<?> control;
   private ObservableList<SouthTableColumnHeader> southColumnHeaders;
   private SouthTableHeaderRow parentSouthHeader;
   private final ListChangeListener tableColumnsListener = c -> {
      while (c.next()) {
         this.updateSouthHeaders();
      }
   };
   private final WeakListChangeListener weakTableColumnsListener = new WeakListChangeListener(this.tableColumnsListener);
   private final InvalidationListener southHeaderBlendedListener = o -> this.updateSouthHeaderRowStyle();
   private final WeakInvalidationListener weakSouthHeaderBlendedListener = new WeakInvalidationListener(this.southHeaderBlendedListener);

   public SouthTableHeaderRow(TableView2Skin<?> skin) {
      this.skin = skin;
      this.control = (TableView2<?>)skin.getSkinnable();
      this.getStyleClass().setAll(new String[]{"column-header-background", "south-header"});
      this.init();
   }

   private void init() {
      this.updateSouthHeaders();
      this.control.getVisibleLeafColumns().addListener(this.weakTableColumnsListener);
      this.control.getColumns().addListener(this.weakTableColumnsListener);
      this.control.southHeaderBlendedProperty().addListener(this.weakSouthHeaderBlendedListener);
      this.updateSouthHeaderRowStyle();
      if (this.control.getParent() != null && this.control.getParent() instanceof RowHeader) {
         this.parentSouthHeader = ((TableView2Skin)((RowHeader)this.control.getParent()).getParentTableView().getSkin()).getSouthHeader();
      }
   }

   public ObservableList<SouthTableColumnHeader> getSouthColumnHeaders() {
      if (this.southColumnHeaders == null) {
         this.southColumnHeaders = FXCollections.observableArrayList();
      }

      return this.southColumnHeaders;
   }

   public SouthTableColumnHeader getSouthColumnHeaderFor(TableColumnBase<?, ?> col) {
      return col == null ? null : this.getSouthColumnHeaders().stream().filter(header -> header.getTableColumn().equals(col)).findFirst().orElse(null);
   }

   protected void layoutChildren() {
      super.layoutChildren();
      double x = this.snappedLeftInset();
      double h = this.getHeight() - this.snappedTopInset() - this.snappedBottomInset();
      double fixedColumnWidth = 0.0;
      double hbarValue = this.skin.getHBar().getValue();
      int i = 0;

      for (int max = this.getSouthColumnHeaders().size(); i < max; i++) {
         SouthTableColumnHeader n = (SouthTableColumnHeader)this.getSouthColumnHeaders().get(i);
         if (n.isVisible()) {
            double prefWidth = this.snapSize(n.prefWidth(-1.0));
            TableColumn<?, ?> column = (TableColumn<?, ?>)n.getTableColumn();

            while (column.getParentColumn() != null) {
               column = (TableColumn<?, ?>)column.getParentColumn();
            }

            double tableCellX = 0.0;
            if (this.control.isColumnFixingEnabled() && this.control.getFixedColumns().contains(column) && hbarValue + fixedColumnWidth > x) {
               tableCellX = Math.abs(hbarValue + fixedColumnWidth - x);
               n.toFront();
               fixedColumnWidth += prefWidth;
            }

            n.relocate(x + tableCellX, this.snappedTopInset());
            n.resize(prefWidth, this.snapSize(h));
            x += prefWidth;
         }
      }
   }

   protected void updateScrollX() {
      this.requestLayout();
   }

   private void updateSouthHeaders() {
      this.getSouthColumnHeaders().forEach(SouthTableColumnHeader::dispose);
      this.getSouthColumnHeaders()
         .setAll(
            ((TableView)this.skin.getSkinnable())
               .getVisibleLeafColumns()
               .stream()
               .map(col -> new SouthTableColumnHeader(this.skin, col))
               .collect(Collectors.toList())
         );
      this.getChildren().setAll(this.getSouthColumnHeaders());
   }

   private void updateSouthHeaderRowStyle() {
      if (this.control.isSouthHeaderBlended() && !this.getStyleClass().contains("south")) {
         this.getStyleClass().add("south");
      } else if (!this.control.isSouthHeaderBlended() && this.getStyleClass().contains("south")) {
         this.getStyleClass().remove("south");
      }
   }

   protected double computePrefHeight(double width) {
      double height = this.getSouthColumnHeaders().stream().map(f -> f.prefHeight(-1.0)).max(Double::compare).orElse(0.0);
      if (this.parentSouthHeader != null) {
         double parentHeight = this.parentSouthHeader.getHeight();
         if (parentHeight == 0.0) {
            height = 0.0;
         } else {
            height = Math.max(height, parentHeight);
         }
      }

      return height + this.snappedTopInset() + this.snappedBottomInset();
   }
}
