package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class UserPanel extends StackPane implements Initializable {
   public static final String CURRENT_USER_SELECTION = org.cropster.roastingintelligence.util.Messages.m_("Default (Current user)");
   private final StringProperty userEmail = new SimpleStringProperty();
   private final StringProperty userInfo = new SimpleStringProperty();
   @FXML
   private Button toggleButton;
   @FXML
   private Label userName;
   @FXML
   private ComboBox<String> workerSelect;

   public UserPanel() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL url, ResourceBundle resourceBundle) {
      this.toggleButton
         .visibleProperty()
         .bind(Bindings.createBooleanBinding(() -> this.workerSelect.getItems().size() > 1, new Observable[]{this.workerSelect.itemsProperty()}));
      this.userName.tooltipProperty().bind(Bindings.createObjectBinding(() -> {
         String tooltipString = this.getUserInfo();
         if (tooltipString != null) {
            if (!tooltipString.equals(this.getUserEmail())) {
               tooltipString = tooltipString + " (" + this.getUserEmail() + ")";
            }

            return new Tooltip(tooltipString);
         } else {
            return null;
         }
      }, new Observable[]{this.userInfoProperty(), this.userEmailProperty()}));
      this.userName.textProperty().bind(this.userInfoProperty());
   }

   public void populateWorkerSelection(Collection<String> workers, String currentWorker) {
      if (this.workerSelect.getParent().isVisible()) {
         this.toggleUserPane();
      }

      ObservableList<String> items = FXCollections.observableArrayList();
      items.add(CURRENT_USER_SELECTION);
      items.addAll(workers);
      this.workerSelect.setItems(items);
      this.workerSelect.getSelectionModel().select(currentWorker);
   }

   public StringProperty userInfoProperty() {
      return this.userInfo;
   }

   public String getUserInfo() {
      return (String)this.userInfo.get();
   }

   public void setUserInfo(String userInfo) {
      this.userInfo.set(userInfo);
   }

   public StringProperty userEmailProperty() {
      return this.userEmail;
   }

   public String getUserEmail() {
      return (String)this.userEmail.get();
   }

   public void setUserEmail(String userEmail) {
      this.userEmail.set(userEmail);
   }

   public ReadOnlyObjectProperty<String> workerProperty() {
      return this.workerSelect.getSelectionModel().selectedItemProperty();
   }

   public String getWorker() {
      return (String)this.workerSelect.getValue();
   }

   public void setWorker(String worker) {
      this.workerSelect.setValue(worker);
   }

   @FXML
   protected void toggleUserPane() {
      this.getChildren().stream().forEach(c -> c.setVisible(!c.isVisible()));
   }
}
