package com.cropster.roastingintelligence.ui.components;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.KeyboardShortcut;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.SystemUtils;

public class KeyCodeCombinationLabel extends GridPane {
   private final List<Function<KeyCodeCombination, ModifierValue>> properties = Arrays.asList(
      KeyCombination::getShortcut, KeyCombination::getControl, KeyCombination::getAlt, KeyCombination::getShift, KeyCombination::getMeta
   );
   private static final String[] MODIFIER_SYMBOLS = new String[]{
      SystemUtils.IS_OS_MAC ? "⌘" : org.cropster.roastingintelligence.util.Messages.c_("Keyboard", "Ctrl"),
      org.cropster.roastingintelligence.util.Messages.c_("Keyboard", "Ctrl"),
      org.cropster.roastingintelligence.util.Messages.c_("Keyboard", "Alt"),
      "⇧",
      org.cropster.roastingintelligence.util.Messages.c_("Keyboard", "Meta")
   };
   @FXML
   private HBox keyList;
   @FXML
   private Label titleLabel;
   private final StringProperty text = new SimpleStringProperty();
   private final ObjectProperty<KeyCodeCombination> keyCodeCombination = new SimpleObjectProperty();
   private final StringProperty customKeyCode = new SimpleStringProperty(null);

   public KeyCodeCombinationLabel() {
      JavaFxUtil.loadComponent(this);
      this.keyCodeCombination.addListener((s, o, n) -> this.update());
      this.customKeyCode.addListener((s, o, n) -> this.update());
   }

   public KeyCodeCombinationLabel(KeyCodeCombination keyCodeCombination) {
      this();
      ((ColumnConstraints)this.getColumnConstraints().get(0)).setHgrow(Priority.NEVER);
      ((ColumnConstraints)this.getColumnConstraints().get(1)).setMinWidth(0.0);
      this.setKeyCodeCombination(keyCodeCombination);
      this.titleLabel.setVisible(false);
   }

   public KeyCodeCombinationLabel(String text, KeyCodeCombination keyCodeCombination) {
      this();
      this.setText(text);
      this.setKeyCodeCombination(keyCodeCombination);
   }

   public KeyCodeCombinationLabel(KeyboardShortcut shortcut) {
      this();
      this.setText(org.cropster.roastingintelligence.util.Messages.m_(shortcut.name()));
      if (shortcut.isCustom()) {
         this.setCustomKeyCode(shortcut.getCustomKeyCode(), shortcut.getKeyCodeCombination());
      } else {
         this.setKeyCodeCombination(shortcut.getKeyCodeCombination());
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

   public ObjectProperty<KeyCodeCombination> keyCodeCombinationProperty() {
      return this.keyCodeCombination;
   }

   public KeyCodeCombination getKeyCodeCombination() {
      return (KeyCodeCombination)this.keyCodeCombination.get();
   }

   public void setKeyCodeCombination(KeyCodeCombination keyCodeCombination) {
      this.customKeyCode.set(null);
      this.keyCodeCombination.set(keyCodeCombination);
   }

   public StringProperty customKeyCodeProperty() {
      return this.customKeyCode;
   }

   public String getCustomKeyCode() {
      return (String)this.customKeyCode.get();
   }

   public void setCustomKeyCode(String customKeyCode) {
      this.customKeyCode.set(customKeyCode);
   }

   public void setCustomKeyCode(String customKeyCode, KeyCodeCombination keyCodeCombination) {
      this.customKeyCode.set(customKeyCode);
      this.keyCodeCombination.set(keyCodeCombination);
   }

   public HBox getKeyList() {
      return this.keyList;
   }

   private void update() {
      this.keyList.getChildren().clear();
      KeyCodeCombination kcc = this.getKeyCodeCombination();
      if (kcc != null) {
         int i = 0;

         for (Function<KeyCodeCombination, ModifierValue> property : this.properties) {
            if (ModifierValue.DOWN.equals(property.apply(kcc))) {
               Label label = new Label(MODIFIER_SYMBOLS[i]);
               label.getStyleClass().add("keyboard-shortcut-label__modfier");
               this.keyList.getChildren().add(label);
            }

            i++;
         }
      }

      String code = "";
      if (this.customKeyCode.get() != null) {
         code = org.cropster.roastingintelligence.util.Messages.c_("Keyboard", (String)this.customKeyCode.get());
      } else if (kcc != null) {
         switch (kcc.getCode()) {
            case ADD:
               code = "+";
               break;
            case DOWN:
               code = "▼";
               break;
            case ENTER:
               code = org.cropster.roastingintelligence.util.Messages.c_("Keyboard", "Enter");
               break;
            case ESCAPE:
               code = org.cropster.roastingintelligence.util.Messages.c_("Keyboard", "Esc");
               break;
            case F1:
               code = "F1";
               break;
            case F2:
               code = "F2";
               break;
            case F3:
               code = "F3";
               break;
            case F4:
               code = "F4";
               break;
            case F5:
               code = "F5";
               break;
            case F6:
               code = "F6";
               break;
            case F7:
               code = "F7";
               break;
            case F8:
               code = "F8";
               break;
            case F9:
               code = "F9";
               break;
            case F10:
               code = "F10";
               break;
            case F11:
               code = "F11";
               break;
            case F12:
               code = "F12";
               break;
            case F13:
               code = "F13";
               break;
            case F14:
               code = "F14";
               break;
            case F15:
               code = "F15";
               break;
            case F16:
               code = "F16";
               break;
            case F17:
               code = "F17";
               break;
            case F18:
               code = "F18";
               break;
            case F19:
               code = "F19";
               break;
            case F20:
               code = "F20";
               break;
            case F21:
               code = "F21";
               break;
            case F22:
               code = "F22";
               break;
            case F23:
               code = "F23";
               break;
            case F24:
               code = "F24";
               break;
            case LEFT:
               code = "◀";
               break;
            case RIGHT:
               code = "▶";
               break;
            case SPACE:
               code = org.cropster.roastingintelligence.util.Messages.c_("Keyboard", "Space");
               break;
            case SUBTRACT:
               code = "-";
               break;
            case UP:
               code = "▲";
               break;
            default:
               code = kcc.getCode().getChar();
         }
      }

      Label label = new Label(code);
      label.getStyleClass().add("keyboard-shortcut-label__key");
      this.keyList.getChildren().add(label);
   }
}
