package impl.org.controlsfx.tableview2;

import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SortEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;

public class RowHeader<S> extends StackPane {
   private final TableView2<S> tableView;
   private TableView2Skin<S> skin;
   private TableView2Skin<S> innerSkin;
   private double tableColumnHeaderHeight;
   private final TableView2<S> innerTableView;
   private final DoubleProperty innerRowHeaderWidth = new SimpleDoubleProperty();
   private Rectangle clip;
   private ListChangeListener<Integer> tableSelectionListener;
   private ListChangeListener<Integer> rowHeaderSelectionListener;
   private boolean sorting;
   private final InvalidationListener layout = o -> this.requestLayout();

   public RowHeader(TableView2<S> tableView) {
      this.tableView = tableView;
      this.getStyleClass().add("row-header");
      if (tableView instanceof FilteredTableView) {
         this.innerTableView = new FilteredTableView<>();
      } else {
         this.innerTableView = new TableView2<>();
      }

      this.innerTableView.setColumnFixingEnabled(false);
      this.innerTableView.setRowHeaderVisible(false);
      this.innerTableView.setEditable(false);
      this.innerTableView.setPlaceholder(new Label());
      this.innerTableView.setSortPolicy(t -> false);
   }

   void init(TableView2Skin<S> skin, TableHeaderRow2 tableColumnHeader) {
      this.skin = skin;
      tableColumnHeader.heightProperty().addListener((obs, oldHeight, newHeight) -> {
         this.tableColumnHeaderHeight = newHeight.doubleValue();
         this.requestLayout();
      });
      this.clip = new Rectangle(
         this.getRowHeaderWidth(), this.snapSize(this.tableView.getHeight() - this.tableView.snappedTopInset() - this.tableView.snappedBottomInset())
      );
      this.clip.relocate(this.snappedTopInset(), this.snappedLeftInset());
      this.clip.setSmooth(false);
      this.clip
         .heightProperty()
         .bind(
            Bindings.createDoubleBinding(
               () -> this.tableView.getHeight() - this.tableView.snappedTopInset() - this.tableView.snappedBottomInset(),
               new Observable[]{this.tableView.heightProperty()}
            )
         );
      this.clip.widthProperty().bind(this.innerRowHeaderWidth);
      this.setClip(this.clip);
      this.tableView.rowHeaderVisibleProperty().addListener(this.layout);
      this.tableView.getFixedRows().addListener(this.layout);
      this.tableView.rowHeaderWidthProperty().addListener(this.layout);
      this.tableView.heightProperty().addListener(this.layout);
      skin.getHBar().visibleProperty().addListener(this.layout);
      this.tableView.fixedCellSizeProperty().addListener(o -> {
         this.innerTableView.refresh();
         this.innerTableView.requestLayout();
      });
      this.tableView.rowFixingEnabledProperty().addListener(o -> {
         this.innerTableView.setRowFixingEnabled(this.tableView.isRowFixingEnabled());
         this.innerTableView.refresh();
         this.innerTableView.requestLayout();
      });
      this.tableView.getVisibleLeafColumns().addListener(o -> {
         if (this.tableView.getVisibleLeafColumns().isEmpty() != this.innerTableView.getColumns().isEmpty()) {
            this.setContent();
         }
      });
      this.tableView.rowHeaderProperty().addListener(o -> this.setContent());
      this.setContent();
      this.innerTableView.itemsProperty().bind(this.tableView.itemsProperty());
      this.innerTableView.getFixedRows().setAll(this.tableView.getFixedRows().stream().collect(Collectors.toList()));
      this.tableView
         .getFixedRows()
         .addListener(o -> this.innerTableView.getFixedRows().setAll(this.tableView.getFixedRows().stream().collect(Collectors.toList())));
      this.innerTableView.skinProperty().addListener(new InvalidationListener() {
         public void invalidated(Observable observable) {
            RowHeader.this.innerSkin = (TableView2Skin<S>)RowHeader.this.innerTableView.getSkin();
            RowHeader.this.setScrollbars();
            RowHeader.this.innerTableView.skinProperty().removeListener(this);
         }
      });
      this.innerTableView.getSelectionModel().selectionModeProperty().bind(this.tableView.getSelectionModel().selectionModeProperty());
      this.rowHeaderSelectionListener = c -> {
         skin.getSelectedRows().removeListener(this.tableSelectionListener);

         while (c.next()) {
            c.getRemoved().forEach(i -> {
               if (this.tableView.getSelectionModel().isCellSelectionEnabled()) {
                  this.tableView.getVisibleLeafColumns().forEach(col -> this.tableView.getSelectionModel().clearSelection(i, col));
               } else {
                  this.tableView.getSelectionModel().clearSelection(i);
               }
            });
            c.getAddedSubList().forEach(i -> this.tableView.getSelectionModel().select(i));
         }

         skin.getSelectedRows().addListener(this.tableSelectionListener);
      };
      this.tableSelectionListener = c -> {
         this.innerTableView.getSelectionModel().getSelectedIndices().removeListener(this.rowHeaderSelectionListener);

         while (c.next()) {
            c.getRemoved().forEach(i -> this.innerTableView.getSelectionModel().clearSelection(i));
            c.getAddedSubList().forEach(i -> this.innerTableView.getSelectionModel().select(i));
         }

         if (!this.sorting) {
            this.innerTableView.getSelectionModel().getSelectedIndices().addListener(this.rowHeaderSelectionListener);
         }
      };
      ChangeListener<Boolean> focusListener = (obs, ov, nv) -> {
         if (!this.tableView.isFocused() && !this.innerTableView.isFocused()) {
            this.tableView.setStyle("-fx-selection-bar-non-focused: lightgrey;");
            this.innerTableView.setStyle("-fx-selection-bar-non-focused: lightgrey;");
         } else {
            this.innerTableView.setStyle("-fx-selection-bar-non-focused: -fx-accent;");
            this.tableView.setStyle("-fx-selection-bar-non-focused: -fx-accent;");
         }
      };
      this.innerTableView.getSelectionModel().getSelectedIndices().addListener(this.rowHeaderSelectionListener);
      skin.getSelectedRows().addListener(this.tableSelectionListener);
      this.tableView.addEventHandler(SortEvent.ANY, e -> {
         if (e != null && SortUtils.SortStartedEvent.SORT_STARTED_EVENT.equals(e.getEventType())) {
            this.sorting = true;
            this.innerTableView.getSelectionModel().getSelectedIndices().removeListener(this.rowHeaderSelectionListener);
         } else if (e != null && SortUtils.SortEndedEvent.SORT_ENDED_EVENT.equals(e.getEventType())) {
            this.sorting = false;
            if (this.innerSkin != null) {
               this.innerSkin.getFlow().rebuildFixedCells();
            }

            this.innerTableView.getSelectionModel().clearSelection();
            if (this.innerTableView.getItems() != null) {
               skin.getSelectedRows().forEach(i -> this.innerTableView.getSelectionModel().select(i));
            }

            this.innerTableView.getSelectionModel().getSelectedIndices().addListener(this.rowHeaderSelectionListener);
         }
      });
      this.innerTableView.southHeaderBlendedProperty().bind(this.tableView.southHeaderBlendedProperty());
      this.tableView.focusedProperty().addListener(focusListener);
      this.innerTableView.focusedProperty().addListener(focusListener);
   }

   public double getRowHeaderWidth() {
      return this.innerRowHeaderWidth.get();
   }

   public ReadOnlyDoubleProperty rowHeaderWidthProperty() {
      return this.innerRowHeaderWidth;
   }

   public double computeHeaderWidth() {
      double width = 0.0;
      if (this.tableView.isRowHeaderVisible()) {
         width += this.tableView.getRowHeaderWidth();
      }

      return width;
   }

   public TableView2<S> getParentTableView() {
      return this.tableView;
   }

   protected void layoutChildren() {
      if (this.tableView.isRowHeaderVisible()) {
         double x = this.snappedLeftInset();
         this.innerRowHeaderWidth.setValue(this.tableView.getRowHeaderWidth());
         if (this.getChildren().isEmpty()) {
            this.getChildren().setAll(new Node[]{this.innerTableView});
         }

         if (this.innerSkin != null) {
            TableHeaderRow2 tableHeaderRow2 = this.innerSkin.getTableHeaderRow2();
            tableHeaderRow2.setPrefHeight(this.tableColumnHeaderHeight);
         }

         ScrollBar hBar = this.skin.getHBar();
         double hBarHeight = hBar.isVisible() && this.tableView.getItems() != null && !this.tableView.getItems().isEmpty()
            ? this.snapSize(hBar.getHeight())
            : 0.0;
         this.innerTableView
            .resizeRelocate(
               x,
               0.0,
               this.innerRowHeaderWidth.get(),
               this.tableView.getHeight() - hBarHeight - this.tableView.snappedTopInset() - this.tableView.snappedBottomInset()
            );
         if (!this.innerTableView.getColumns().isEmpty()) {
            ((TableColumn)this.innerTableView.getColumns().get(0)).setPrefWidth(this.innerRowHeaderWidth.get());
         }

         Label label;
         if (this.getChildren().size() == 1) {
            label = new Label("");
            label.getStyleClass().setAll(new String[]{"hbar"});
            this.getChildren().add(label);
         } else {
            label = (Label)this.getChildren().get(1);
         }

         label.resizeRelocate(this.snappedLeftInset(), this.getHeight() - this.snappedBottomInset() - hBarHeight, this.innerRowHeaderWidth.get(), hBarHeight);
      } else {
         this.getChildren().clear();
         this.innerRowHeaderWidth.setValue(0);
      }
   }

   private void setContent() {
      this.innerTableView.getColumns().clear();
      if (!this.tableView.getVisibleLeafColumns().isEmpty()) {
         if (this.tableView.getRowHeader() != null) {
            this.innerTableView.getColumns().add(this.tableView.getRowHeader());
         } else {
            this.innerTableView.getColumns().add(this.getDefaultTableColumn());
         }
      }
   }

   private TableColumn2<S, String> getDefaultTableColumn() {
      TableColumn2<S, String> column;
      if (this.tableView instanceof FilteredTableView) {
         column = new FilteredTableColumn<>();
         ((FilteredTableColumn)column).setOnFilterAction(e -> {
            if (((FilteredTableView)this.tableView).getPredicate() != null) {
               ((FilteredTableView)this.tableView).resetFilter();
            }
         });
      } else {
         column = new TableColumn2<>();
      }

      column.setSortable(false);
      column.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getTableView().getItems().indexOf(p.getValue()) + 1)));
      return column;
   }

   private void setScrollbars() {
      ScrollBar scrollBarParent = this.skin.getVBar();
      ScrollBar scrollBar = this.innerSkin.getVBar();
      scrollBar.setMin(scrollBarParent.getMin());
      scrollBar.setMax(scrollBarParent.getMax());
      scrollBar.valueProperty().bindBidirectional(scrollBarParent.valueProperty());
      this.innerSkin.getFlow().adjustedPixelsProperty().bindBidirectional(this.skin.getFlow().adjustedPixelsProperty());
   }
}
