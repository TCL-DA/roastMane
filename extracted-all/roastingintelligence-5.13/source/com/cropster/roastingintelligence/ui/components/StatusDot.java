package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.shape.SVGPath;

public class StatusDot extends SVGPath {
   @FXML
   private SVGPath statusIcon;
   private final ObjectProperty<StatusDot.Status> statusProperty = new SimpleObjectProperty(StatusDot.Status.WARNING);

   public StatusDot() {
      JavaFxUtil.loadComponent(this);
      this.statusProperty.addListener((v, o, n) -> {
         this.statusIcon.getStyleClass().remove(this.getStyleClass(o));
         this.statusIcon.getStyleClass().add(this.getStyleClass(n));
      });
   }

   private String getStyleClass(StatusDot.Status status) {
      switch (status) {
         case ON:
         case SUCCESS:
            return "status-icon--success";
         case WARNING:
            return "status-icon--warning";
         case ERROR:
            return "status-icon--error";
         default:
            return "status-icon--off";
      }
   }

   public StatusDot.Status getStatus() {
      return (StatusDot.Status)this.statusProperty.get();
   }

   public void setStatus(StatusDot.Status status) {
      this.statusProperty.set(status);
   }

   public ObjectProperty<StatusDot.Status> statusProperty() {
      return this.statusProperty;
   }

   public static enum Status {
      SUCCESS,
      WARNING,
      ERROR,
      ON,
      OFF;
   }
}
