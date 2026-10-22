package com.cropster.roastingintelligence.ui.layout;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.MacOsFxUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SupportSidebar extends DetachableSidebar implements Initializable {
   @FXML
   private TextField subjectField;
   @FXML
   private TextArea descriptionField;
   @FXML
   private Button submitButton;
   private StringProperty subject;
   private StringProperty description;

   public SupportSidebar() {
      JavaFxUtil.loadComponent(this);
   }

   @Override
   public void initialize(URL location, ResourceBundle resources) {
      super.initialize(location, resources);
      this.subject = this.subjectField.textProperty();
      this.description = this.descriptionField.textProperty();
      this.submitButton.disableProperty().bind(Bindings.or(this.subject.isEmpty(), this.description.isEmpty()));
      MacOsFxUtil.installImeCompositionResetWorkaround(this.subjectField);
      MacOsFxUtil.installImeCompositionResetWorkaround(this.descriptionField);
   }

   public Button getSubmitButton() {
      return this.submitButton;
   }

   public StringProperty subjectProperty() {
      return this.subject;
   }

   public String getSubject() {
      return (String)this.subject.get();
   }

   public void setSubject(String subject) {
      this.subject.set(subject);
   }

   public StringProperty descriptionProperty() {
      return this.description;
   }

   public String getDescription() {
      return (String)this.description.get();
   }

   public void setDescription(String description) {
      this.description.set(description);
   }

   public void reset() {
      this.subjectField.setText(null);
      this.descriptionField.setText(null);
   }
}
