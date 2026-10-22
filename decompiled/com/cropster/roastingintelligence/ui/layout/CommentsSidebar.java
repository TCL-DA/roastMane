package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.components.CommentEditor;
import com.cropster.roastingintelligence.ui.components.CommentLabel;
import com.cropster.roastingintelligence.ui.components.RandomShortcutPane;
import com.cropster.roastingintelligence.ui.util.AbstractListCell;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.cropster.roastingintelligence.model.CommentListener;
import org.cropster.roastingintelligence.model.Commentable;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.RoastingComment;

public class CommentsSidebar extends DetachableSidebar implements CommentListener, Initializable {
   @FXML
   private ListView<RoastingComment> listView;
   @FXML
   private VBox infoBox;
   @FXML
   private RandomShortcutPane randomShortcutPane;
   private final ObservableList<RoastingComment> comments = FXCollections.observableArrayList();
   private CommentEditor editor = new CommentEditor();

   public CommentsSidebar() {
      JavaFxUtil.loadComponent(this);
   }

   public void init(Commentable commentable) {
      this.listView.setCellFactory(v -> new CommentsSidebar.CommentListCell(commentable));
      this.listView.setItems(this.comments);
      this.listView.setEditable(true);
      this.comments.setAll(commentable.getComments());
      commentable.addCommentListener(this);
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.infoBox.managedProperty().bind(this.infoBox.visibleProperty());
      this.infoBox.visibleProperty().bind(Bindings.createBooleanBinding(this.comments::isEmpty, new Observable[]{this.comments}));
      this.infoBox.visibleProperty().addListener((s, o, n) -> this.randomShortcutPane.update());
      this.listView.managedProperty().bind(this.listView.visibleProperty());
      this.listView.visibleProperty().bind(this.infoBox.visibleProperty().not());
      this.visibleProperty().addListener((s, o, n) -> {
         if (n) {
            this.randomShortcutPane.update();
         }
      });
      JavaFxUtil.setDefaultLineSpacing(this);
   }

   @Override
   public void commentAdded(RoastingComment comment) {
      Platform.runLater(() -> {
         this.comments.add(comment);
         FXCollections.sort(this.comments, new RoastingComment.TimeComparator());
      });
   }

   @Override
   public void commentUpdated(RoastingComment comment) {
   }

   @Override
   public void commentRemoved(RoastingComment comment) {
      this.comments.remove(comment);
   }

   public RandomShortcutPane getRandomShortcutPane() {
      return this.randomShortcutPane;
   }

   public class CommentListCell extends AbstractListCell<RoastingComment> {
      private final Commentable commentable;
      private final CommentLabel viewer;

      public CommentListCell(Commentable commentable) {
         this.commentable = commentable;
         this.viewer = new CommentLabel(this);
         this.setPrefWidth(0.0);
      }

      private Measure getTemp(RoastingComment item) {
         return this.getItem() != null ? this.commentable.getTempAtTime(item.getTime()) : null;
      }

      protected void initViewer(RoastingComment item) {
         this.viewer.init(item, this.getTemp(item));
         this.setGraphic(this.viewer);
      }

      protected void initEditor(RoastingComment item) {
         CommentsSidebar.this.editor.init(this.commentable, item, this.getTemp(item));
         CommentsSidebar.this.editor.getCancelButton().setOnAction(this::cancelEdit);
         CommentsSidebar.this.editor.getUpdateButton().setOnAction(this::commitEdit);
         this.setGraphic(CommentsSidebar.this.editor);
      }

      private void cancelEdit(ActionEvent e) {
         this.cancelEdit();
      }

      private void commitEdit(ActionEvent e) {
         this.commitEdit(CommentsSidebar.this.editor.updateAndGetComment());
      }

      public void delete() {
         this.commentable.removeComment((RoastingComment)this.getItem());
      }
   }
}
