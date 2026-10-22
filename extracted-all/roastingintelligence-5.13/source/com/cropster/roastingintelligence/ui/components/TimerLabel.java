package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.util.text.DurationFormat;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.cropster.roastingintelligence.Defaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerLabel extends VBox implements Initializable {
   private static final Logger LOGGER = LoggerFactory.getLogger(TimerLabel.class);
   private static Font font;
   @FXML
   private Label textLabel;
   @FXML
   private Label timerLabel;
   private StringProperty text;
   private final ObjectProperty<Long> time = new SimpleObjectProperty();
   private final DurationFormat durationFormat = DurationFormat.getDurationInstance(true);

   public TimerLabel() {
      JavaFxUtil.loadComponent(this);
      this.timerLabel.setFont(font);
   }

   public void initialize(URL location, ResourceBundle resources) {
      this.text = this.textLabel.textProperty();
      this.timerLabel
         .textProperty()
         .bind(Bindings.createStringBinding(() -> this.time.get() != null ? this.durationFormat.format(this.time.get()) : "--:--", new Observable[]{this.time}));
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

   public ObjectProperty<Long> timeProperty() {
      return this.time;
   }

   public Long getTime() {
      return (Long)this.time.get();
   }

   public void setTime(Long time) {
      this.time.set(time);
   }

   static {
      try {
         if (Defaults.LOCALE_ARABIC.getLanguage().equals(Locale.getDefault().getLanguage())) {
            font = Font.font(Font.getDefault().getSize() * 2.4);
         } else {
            font = Font.loadFont(JavaFxUtil.FONT_MONOSPACE_URL.openStream(), Font.getDefault().getSize() * 2.4);
         }
      } catch (Exception var1) {
         LOGGER.error("Error loading font {}", JavaFxUtil.FONT_MONOSPACE_URL, var1);
      }
   }
}
