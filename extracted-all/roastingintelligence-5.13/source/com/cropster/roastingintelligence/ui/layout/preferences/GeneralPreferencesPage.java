package com.cropster.roastingintelligence.ui.layout.preferences;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.LocaleListCell;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import org.cropster.roastingintelligence.Defaults;

public class GeneralPreferencesPage extends GridPane implements Initializable {
   @FXML
   private ComboBox<Locale> localeComboBox;
   @FXML
   private Label localeChangedLabel;
   @FXML
   private Button resetHintsButton;

   public GeneralPreferencesPage() {
      JavaFxUtil.loadComponent(this);
   }

   public void initialize(URL location, ResourceBundle resources) {
      Callback<ListView<Locale>, ListCell<Locale>> cellFactory = v -> new LocaleListCell();
      ArrayList<Locale> locales = new ArrayList<>();
      Collections.addAll(locales, Defaults.LOCALES);
      Collections.sort(locales, (o1, o2) -> o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName()));
      locales.add(0, LocaleListCell.SYSTEM_DEFAULT);
      this.localeComboBox.getItems().setAll(locales);
      this.localeComboBox.setButtonCell((ListCell)cellFactory.call(null));
      this.localeComboBox.setCellFactory(cellFactory);
      this.localeComboBox.valueProperty().addListener((s, o, n) -> this.localeChangedLabel.setVisible(true));
      this.localeChangedLabel.managedProperty().bind(this.localeChangedLabel.visibleProperty());
      this.resetChangeLabel();
   }

   public Locale getLocale() {
      Locale locale = (Locale)this.localeComboBox.getValue();
      return locale == LocaleListCell.SYSTEM_DEFAULT ? null : locale;
   }

   public void setLocale(Locale locale) {
      this.localeComboBox.setValue(locale != null ? locale : LocaleListCell.SYSTEM_DEFAULT);
   }

   public void resetChangeLabel() {
      this.localeChangedLabel.setVisible(false);
   }

   public void setResetHintsHandler(EventHandler<ActionEvent> resetHintsHandler) {
      this.resetHintsButton.setOnAction(resetHintsHandler);
   }
}
