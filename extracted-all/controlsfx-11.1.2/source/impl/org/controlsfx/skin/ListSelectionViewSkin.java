package impl.org.controlsfx.skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ListActionView;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

public class ListSelectionViewSkin<T> extends SkinBase<ListSelectionView<T>> {
   private GridPane gridPane;
   private ListView<T> sourceListView = Objects.requireNonNull(this.createSourceListView(), "source list view can not be null");
   private ListView<T> targetListView;
   private ListActionView<T> sourceListActionView;
   private ListActionView<T> targetListActionView;

   public ListSelectionViewSkin(ListSelectionView<T> view) {
      super(view);
      this.sourceListView.setId("source-list-view");
      this.sourceListActionView = this.createListActionView(this.sourceListView);
      Bindings.bindContent(this.sourceListActionView.getActions(), view.getSourceActions());
      Bindings.bindContentBidirectional(this.sourceListActionView.getItems(), view.getSourceItems());
      this.targetListView = Objects.requireNonNull(this.createTargetListView(), "target list view can not be null");
      this.targetListView.setId("target-list-view");
      this.targetListActionView = this.createListActionView(this.targetListView);
      Bindings.bindContent(this.targetListActionView.getActions(), view.getTargetActions());
      Bindings.bindContentBidirectional(this.targetListActionView.getItems(), view.getTargetItems());
      this.sourceListActionView.cellFactoryProperty().bind(view.cellFactoryProperty());
      this.targetListActionView.cellFactoryProperty().bind(view.cellFactoryProperty());
      this.gridPane = this.createGridPane();
      this.getChildren().add(this.gridPane);
      InvalidationListener updateListener = o -> this.updateView();
      view.sourceHeaderProperty().addListener(updateListener);
      view.sourceFooterProperty().addListener(updateListener);
      view.targetHeaderProperty().addListener(updateListener);
      view.targetFooterProperty().addListener(updateListener);
      view.getActions().addListener(updateListener);
      this.updateView();
      this.getSourceListView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
         if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            this.moveToTarget();
         }
      });
      this.getTargetListView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
         if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            this.moveToSource();
         }
      });
      view.orientationProperty().addListener(observable -> this.updateView());
   }

   private GridPane createGridPane() {
      GridPane gridPane = new GridPane();
      gridPane.getStyleClass().add("grid-pane");
      return gridPane;
   }

   private void setHorizontalViewConstraints() {
      this.gridPane.getColumnConstraints().clear();
      this.gridPane.getRowConstraints().clear();
      ColumnConstraints col1 = new ColumnConstraints();
      col1.setFillWidth(true);
      col1.setHgrow(Priority.ALWAYS);
      col1.setMaxWidth(Double.MAX_VALUE);
      col1.setPrefWidth(200.0);
      ColumnConstraints col2 = new ColumnConstraints();
      col2.setFillWidth(true);
      col2.setHgrow(Priority.NEVER);
      ColumnConstraints col3 = new ColumnConstraints();
      col3.setFillWidth(true);
      col3.setHgrow(Priority.ALWAYS);
      col3.setMaxWidth(Double.MAX_VALUE);
      col3.setPrefWidth(200.0);
      this.gridPane.getColumnConstraints().addAll(new ColumnConstraints[]{col1, col2, col3});
      RowConstraints row1 = new RowConstraints();
      row1.setFillHeight(true);
      row1.setVgrow(Priority.NEVER);
      RowConstraints row2 = new RowConstraints();
      row2.setMaxHeight(Double.MAX_VALUE);
      row2.setPrefHeight(200.0);
      row2.setVgrow(Priority.ALWAYS);
      RowConstraints row3 = new RowConstraints();
      row3.setFillHeight(true);
      row3.setVgrow(Priority.NEVER);
      this.gridPane.getRowConstraints().addAll(new RowConstraints[]{row1, row2, row3});
   }

   private void setVerticalViewConstraints() {
      this.gridPane.getColumnConstraints().clear();
      this.gridPane.getRowConstraints().clear();
      ColumnConstraints col1 = new ColumnConstraints();
      col1.setFillWidth(true);
      col1.setHgrow(Priority.ALWAYS);
      col1.setMaxWidth(Double.MAX_VALUE);
      col1.setPrefWidth(200.0);
      this.gridPane.getColumnConstraints().addAll(new ColumnConstraints[]{col1});
      RowConstraints row1 = new RowConstraints();
      row1.setFillHeight(true);
      row1.setVgrow(Priority.NEVER);
      RowConstraints row2 = new RowConstraints();
      row2.setMaxHeight(Double.MAX_VALUE);
      row2.setPrefHeight(200.0);
      row2.setVgrow(Priority.ALWAYS);
      RowConstraints row3 = new RowConstraints();
      row3.setFillHeight(true);
      row3.setVgrow(Priority.NEVER);
      RowConstraints row4 = new RowConstraints();
      row4.setFillHeight(true);
      row4.setVgrow(Priority.NEVER);
      RowConstraints row5 = new RowConstraints();
      row5.setFillHeight(true);
      row5.setVgrow(Priority.NEVER);
      RowConstraints row6 = new RowConstraints();
      row6.setMaxHeight(Double.MAX_VALUE);
      row6.setPrefHeight(200.0);
      row6.setVgrow(Priority.ALWAYS);
      RowConstraints row7 = new RowConstraints();
      row7.setFillHeight(true);
      row7.setVgrow(Priority.NEVER);
      this.gridPane.getRowConstraints().addAll(new RowConstraints[]{row1, row2, row3, row4, row5, row6, row7});
   }

   private VBox createVerticalButtonBox() {
      VBox box = new VBox(5.0);
      box.setFillWidth(true);
      box.getChildren().addAll(this.createButtonsFromActions());
      return box;
   }

   private HBox createHorizontalButtonBox() {
      HBox box = new HBox(5.0);
      box.setFillHeight(true);
      box.getChildren().addAll(this.createButtonsFromActions());
      return box;
   }

   private void updateView() {
      this.gridPane.getChildren().clear();
      Node sourceHeader = ((ListSelectionView)this.getSkinnable()).getSourceHeader();
      Node targetHeader = ((ListSelectionView)this.getSkinnable()).getTargetHeader();
      Node sourceFooter = ((ListSelectionView)this.getSkinnable()).getSourceFooter();
      Node targetFooter = ((ListSelectionView)this.getSkinnable()).getTargetFooter();
      StackPane stackPane = new StackPane();
      stackPane.setAlignment(Pos.CENTER);
      Orientation orientation = ((ListSelectionView)this.getSkinnable()).getOrientation();
      if (orientation == Orientation.HORIZONTAL) {
         this.setHorizontalViewConstraints();
         if (sourceHeader != null) {
            this.gridPane.add(sourceHeader, 0, 0);
         }

         if (targetHeader != null) {
            this.gridPane.add(targetHeader, 2, 0);
         }

         if (this.sourceListActionView != null) {
            this.sourceListActionView.setSide(Side.LEFT);
            this.gridPane.add(this.sourceListActionView, 0, 1);
         }

         if (this.targetListActionView != null) {
            this.targetListActionView.setSide(Side.RIGHT);
            this.gridPane.add(this.targetListActionView, 2, 1);
         }

         if (sourceFooter != null) {
            this.gridPane.add(sourceFooter, 0, 2);
         }

         if (targetFooter != null) {
            this.gridPane.add(targetFooter, 2, 2);
         }

         stackPane.getChildren().add(this.createVerticalButtonBox());
         this.gridPane.add(stackPane, 1, 1);
      } else {
         this.setVerticalViewConstraints();
         if (sourceHeader != null) {
            this.gridPane.add(sourceHeader, 0, 0);
         }

         if (targetHeader != null) {
            this.gridPane.add(targetHeader, 0, 4);
         }

         if (this.sourceListActionView != null) {
            this.sourceListActionView.setSide(Side.RIGHT);
            this.gridPane.add(this.sourceListActionView, 0, 1);
         }

         if (this.targetListActionView != null) {
            this.targetListActionView.setSide(Side.RIGHT);
            this.gridPane.add(this.targetListActionView, 0, 5);
         }

         if (sourceFooter != null) {
            this.gridPane.add(sourceFooter, 0, 2);
         }

         if (targetFooter != null) {
            this.gridPane.add(targetFooter, 0, 6);
         }

         stackPane.getChildren().add(this.createHorizontalButtonBox());
         this.gridPane.add(stackPane, 0, 3);
      }
   }

   private void moveToTarget() {
      this.move(this.getSourceListView(), this.getTargetListView());
      this.getSourceListView().getSelectionModel().clearSelection();
   }

   private void moveToSource() {
      this.move(this.getTargetListView(), this.getSourceListView());
      this.getTargetListView().getSelectionModel().clearSelection();
   }

   private void move(ListView<T> viewA, ListView<T> viewB) {
      List<T> selectedItems = new ArrayList<>(viewA.getSelectionModel().getSelectedItems());
      this.move(viewA, viewB, selectedItems);
   }

   private void move(ListView<T> viewA, ListView<T> viewB, List<T> items) {
      viewA.getItems().removeAll(items);
      viewB.getItems().addAll(items);
   }

   private ObservableList<Node> createButtonsFromActions() {
      return ((ListSelectionView)this.getSkinnable())
         .getActions()
         .stream()
         .peek(this::initializeListSelectionAction)
         .map(this::createActionNode)
         .collect(Collectors.toCollection(FXCollections::observableArrayList));
   }

   private void initializeListSelectionAction(Action action) {
      if (action instanceof ListSelectionView.ListSelectionAction) {
         ((ListSelectionView.ListSelectionAction)action).initialize(this.sourceListView, this.targetListView);
      }
   }

   private Node createActionNode(Action action) {
      if (action == ActionUtils.ACTION_SEPARATOR) {
         return new Separator();
      } else if (action == ActionUtils.ACTION_SPAN) {
         Pane span = new Pane();
         HBox.setHgrow(span, Priority.ALWAYS);
         VBox.setVgrow(span, Priority.ALWAYS);
         return span;
      } else {
         return this.createActionButton(action);
      }
   }

   private Button createActionButton(Action action) {
      Button button = ActionUtils.createButton(action);
      button.setMaxWidth(Double.MAX_VALUE);
      if (action.getAccelerator() != null) {
         ((ListSelectionView)this.getSkinnable()).getScene().getAccelerators().put(action.getAccelerator(), button::fire);
      }

      return button;
   }

   public final ListView<T> getSourceListView() {
      return this.sourceListView;
   }

   public final ListView<T> getTargetListView() {
      return this.targetListView;
   }

   protected ListView<T> createSourceListView() {
      return this.createListView();
   }

   protected ListView<T> createTargetListView() {
      return this.createListView();
   }

   private ListView<T> createListView() {
      ListView<T> view = new ListView();
      view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      return view;
   }

   private ListActionView<T> createListActionView(final ListView<T> listView) {
      ListActionView<T> listActionView = new ListActionView<>();
      listActionView.setSkin(new ListActionViewSkin<T>(listActionView) {
         @Override
         public ListView<T> createListView() {
            return listView;
         }
      });
      return listActionView;
   }
}
