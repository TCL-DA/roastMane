package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MessageBox extends HBox implements Initializable {
   @FXML
   private Label iconLabel;
   @FXML
   private VBox messageWrapper;
   @FXML
   private Text header;
   @FXML
   private TextFlow contents;
   private final ObjectProperty<Node> graphic;
   private final StringProperty title = new SimpleStringProperty();
   private final StringProperty text = new SimpleStringProperty();

   public MessageBox() {
      JavaFxUtil.loadComponent(this);
      this.graphic = this.iconLabel.graphicProperty();
      this.graphic.set(Icon.INFO.load("icon--info"));
      this.iconLabel.visibleProperty().bind(this.graphic.isNotNull());
      this.title.addListener((s, o, n) -> {
         if (n != null) {
            this.header.setText(n);
            this.header.setVisible(true);
            this.header.setManaged(true);
         } else {
            this.header.setVisible(false);
            this.header.setManaged(false);
         }
      });
      this.text.addListener((s, o, n) -> this.updateContents(n));
   }

   private void updateContents(String string) {
      this.contents.getChildren().clear();
      if (string != null) {
         this.contents.getChildren().addAll(JavaFxUtil.parseHtmlToTextNodes(string));
      }
   }

   public ObjectProperty<Node> graphicProperty() {
      return this.graphic;
   }

   public Node getGraphic() {
      return (Node)this.graphic.get();
   }

   public void setGraphic(Node graphic) {
      this.graphic.set(graphic);
   }

   public TextFlow getContents() {
      return this.contents;
   }

   public StringProperty titleProperty() {
      return this.title;
   }

   public String getTitle() {
      return (String)this.title.get();
   }

   public void setTitle(String title) {
      if (title == null) {
         this.messageWrapper.getChildren().removeIf(n -> "header".equals(n.getId()));
      } else {
         this.title.set(title);
      }
   }

   public StringProperty textProperty() {
      return this.text;
   }

   public String getText() {
      return (String)this.text.get();
   }

   public void setText(String text) {
      this.text.set(text);
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      JavaFxUtil.setDefaultLineSpacing(this);
   }
}
