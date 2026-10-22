package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PercentageLabel extends VBox implements Initializable {
   private static final Logger LOGGER = LoggerFactory.getLogger(PercentageLabel.class);
   private static Font font;
   @FXML
   private Label textLabel;
   @FXML
   private Label percentageLabel;
   private StringProperty text;
   private final DoubleProperty percentage = new SimpleDoubleProperty();
   private final NumberFormat percentageFormat = NumberFormat.getPercentInstance();

   public PercentageLabel() {
      JavaFxUtil.loadComponent(this);
      this.percentageLabel.setFont(font);
      this.percentageFormat.setMinimumFractionDigits(1);
      this.percentageFormat.setMaximumFractionDigits(1);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.text = this.textLabel.textProperty();
      this.percentageLabel
         .textProperty()
         .bind(Bindings.createStringBinding(() -> this.percentageFormat.format(this.percentage.get()), new Observable[]{this.percentage}));
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

   public DoubleProperty percentageProperty() {
      return this.percentage;
   }

   public double getPercentage() {
      return this.percentage.get();
   }

   public void setPercentage(double percentage) {
      this.percentage.set(percentage);
   }

   static {
      try {
         font = Font.loadFont(JavaFxUtil.FONT_MONOSPACE_URL.openStream(), Font.getDefault().getSize() * 2.4);
      } catch (Exception var1) {
         LOGGER.error("Error loading font {}", JavaFxUtil.FONT_MONOSPACE_URL, var1);
      }
   }
}
