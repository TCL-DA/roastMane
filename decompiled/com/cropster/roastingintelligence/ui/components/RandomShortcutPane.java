package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import java.net.URL;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class RandomShortcutPane extends GridPane implements Initializable {
   private static final Random RANDOMIZER = new Random();
   @FXML
   private Button button;
   @FXML
   private Label caption;
   @FXML
   private Label description;
   @FXML
   private KeyCodeCombinationLabel shortcut;
   private KeyboardShortcut keyboardShortcut;

   public RandomShortcutPane() {
      JavaFxUtil.loadComponent(this);
   }

   public RandomShortcutPane(EventHandler<ActionEvent> buttonEventHandler) {
      this();
      this.setButtonEventHandler(buttonEventHandler);
   }

   public void initialize(URL location, ResourceBundle resources) {
      ((ColumnConstraints)this.shortcut.getColumnConstraints().get(0)).setHgrow(Priority.NEVER);
      ((ColumnConstraints)this.shortcut.getColumnConstraints().get(1)).setMinWidth(0.0);
      this.shortcut.setStyle("-fx-hgap: 0;");
      this.button.setText(org.cropster.roastingintelligence.util.Messages.m_("See more keyboard shortcuts"));
      this.caption.setText(org.cropster.roastingintelligence.util.Messages.m_("Increase your efficiency by learning new shortcuts:"));
      this.button.setOnAction(e -> this.update());
      this.update();
   }

   public final void setButtonEventHandler(EventHandler<ActionEvent> buttonEventHandler) {
      Objects.requireNonNull(buttonEventHandler, "Null 'buttonEventHandler' parameter.");
      this.button.setOnAction(buttonEventHandler);
   }

   public void update() {
      synchronized (this) {
         this.keyboardShortcut = KeyboardShortcut.values()[RANDOMIZER.nextInt(KeyboardShortcut.values().length)];
      }

      this.description.setText(org.cropster.roastingintelligence.util.Messages.m_(this.keyboardShortcut.name()));
      if (this.keyboardShortcut.isCustom()) {
         this.shortcut.setCustomKeyCode(this.keyboardShortcut.getCustomKeyCode(), this.keyboardShortcut.getKeyCodeCombination());
      } else {
         this.shortcut.setKeyCodeCombination(this.keyboardShortcut.getKeyCodeCombination());
      }
   }
}
