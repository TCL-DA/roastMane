package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

public class InfoBox extends HBox implements Initializable {
   private final StringProperty text = new SimpleStringProperty();

   public InfoBox() {
      JavaFxUtil.loadComponent(this);
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
