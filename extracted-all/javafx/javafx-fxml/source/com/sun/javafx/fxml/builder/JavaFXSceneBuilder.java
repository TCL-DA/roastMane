package com.sun.javafx.fxml.builder;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Builder;

@DefaultProperty("root")
public class JavaFXSceneBuilder implements Builder<Scene> {
   private Parent root = null;
   private double width = -1.0;
   private double height = -1.0;
   private Paint fill = Color.WHITE;
   private ArrayList<String> stylesheets = new ArrayList<>();

   public Parent getRoot() {
      return this.root;
   }

   public void setRoot(Parent var1) {
      this.root = var1;
   }

   public double getWidth() {
      return this.width;
   }

   public void setWidth(double var1) {
      if (var1 < -1.0) {
         throw new IllegalArgumentException();
      } else {
         this.width = var1;
      }
   }

   public double getHeight() {
      return this.height;
   }

   public void setHeight(double var1) {
      if (var1 < -1.0) {
         throw new IllegalArgumentException();
      } else {
         this.height = var1;
      }
   }

   public Paint getFill() {
      return this.fill;
   }

   public void setFill(Paint var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.fill = var1;
      }
   }

   public List<String> getStylesheets() {
      return this.stylesheets;
   }

   public Scene build() {
      Scene var1 = new Scene(this.root, this.width, this.height, this.fill);

      for (String var3 : this.stylesheets) {
         var1.getStylesheets().add(var3);
      }

      return var1;
   }
}
