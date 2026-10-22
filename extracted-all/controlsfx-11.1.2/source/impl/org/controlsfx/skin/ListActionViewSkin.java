package impl.org.controlsfx.skin;

import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.ListActionView;
import org.controlsfx.control.action.Action;
import org.controlsfx.control.action.ActionUtils;

public class ListActionViewSkin<T> extends SkinBase<ListActionView<T>> {
   private BorderPane borderPane = new BorderPane();
   private ListView<T> listView;

   public ListActionViewSkin(ListActionView<T> control) {
      super(control);
      this.borderPane.getStyleClass().add("container");
      this.listView = this.createListView();
      this.listView.cellFactoryProperty().bind(((ListActionView)this.getSkinnable()).cellFactoryProperty());
      Bindings.bindContentBidirectional(this.listView.getItems(), ((ListActionView)this.getSkinnable()).getItems());
      control.getActions().addListener(o -> this.update());
      control.sideProperty().addListener(o -> this.update());
      this.update();
      this.getChildren().add(this.borderPane);
   }

   private void update() {
      this.borderPane.getChildren().clear();
      this.borderPane.setCenter(this.listView);
      if (!((ListActionView)this.getSkinnable()).getActions().isEmpty()) {
         Side side = ((ListActionView)this.getSkinnable()).getSide();
         switch (side) {
            case TOP:
               this.borderPane.setTop(this.createButtonBar(Orientation.HORIZONTAL));
               break;
            case RIGHT:
               this.borderPane.setRight(this.createButtonBar(Orientation.VERTICAL, "right"));
               break;
            case BOTTOM:
               this.borderPane.setBottom(this.createButtonBar(Orientation.HORIZONTAL, "bottom"));
               break;
            case LEFT:
               this.borderPane.setLeft(this.createButtonBar(Orientation.VERTICAL));
         }
      }
   }

   private ToolBar createButtonBar(Orientation orientation, String... styleClass) {
      ToolBar box = new ToolBar();
      box.setOrientation(orientation);
      box.getStyleClass().addAll(styleClass);
      box.getItems().addAll(this.createButtonsFromActions());
      return box;
   }

   private ObservableList<Node> createButtonsFromActions() {
      return ((ListActionView)this.getSkinnable())
         .getActions()
         .stream()
         .peek(this::initializeListAction)
         .map(this::createActionNode)
         .collect(Collectors.toCollection(FXCollections::observableArrayList));
   }

   private void initializeListAction(Action action) {
      if (action instanceof ListActionView.ListAction) {
         ((ListActionView.ListAction)action).initialize(this.listView);
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
         ((ListActionView)this.getSkinnable()).getScene().getAccelerators().put(action.getAccelerator(), button::fire);
      }

      return button;
   }

   protected ListView<T> createListView() {
      ListView<T> view = new ListView();
      view.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      return view;
   }
}
