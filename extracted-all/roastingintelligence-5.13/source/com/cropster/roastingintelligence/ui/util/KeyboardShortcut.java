package com.cropster.roastingintelligence.ui.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCombination.Modifier;

public enum KeyboardShortcut {
   ADD(new KeyCodeCombination(KeyCode.ADD, new Modifier[0])),
   ARROW_UP(new KeyCodeCombination(KeyCode.UP, new Modifier[0])),
   ARROW_DOWN(new KeyCodeCombination(KeyCode.DOWN, new Modifier[0])),
   ESC(new KeyCodeCombination(KeyCode.ESCAPE, new Modifier[0])),
   F1(new KeyCodeCombination(KeyCode.F1, new Modifier[0])),
   F11(new KeyCodeCombination(KeyCode.F11, new Modifier[0])),
   SUBTRACT(new KeyCodeCombination(KeyCode.SUBTRACT, new Modifier[0])),
   SHIFT_ENTER(new KeyCodeCombination(KeyCode.ENTER, new Modifier[]{KeyCombination.SHIFT_DOWN})),
   CTRL_ARROW_LEFT(new KeyCodeCombination(KeyCode.LEFT, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_ARROW_RIGHT(new KeyCodeCombination(KeyCode.RIGHT, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_COMMA(new KeyCodeCombination(KeyCode.COMMA, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_E(new KeyCodeCombination(KeyCode.E, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_ENTER(new KeyCodeCombination(KeyCode.ENTER, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_ESC(new KeyCodeCombination(KeyCode.ESCAPE, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_F11(new KeyCodeCombination(KeyCode.F11, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_P(new KeyCodeCombination(KeyCode.P, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_R(new KeyCodeCombination(KeyCode.R, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_S(new KeyCodeCombination(KeyCode.S, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_WHEEL_CHART("Mouse wheel", KeyCombination.SHORTCUT_DOWN),
   CTRL_WHEEL_SLIDER("Mouse wheel", KeyCombination.SHORTCUT_DOWN),
   CTRL_1(new KeyCodeCombination(KeyCode.DIGIT1, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_2(new KeyCodeCombination(KeyCode.DIGIT2, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_3(new KeyCodeCombination(KeyCode.DIGIT3, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_4(new KeyCodeCombination(KeyCode.DIGIT4, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   CTRL_5(new KeyCodeCombination(KeyCode.DIGIT5, new Modifier[]{KeyCombination.SHORTCUT_DOWN})),
   SHIFT_CTRL_A(new KeyCodeCombination(KeyCode.A, new Modifier[]{KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN})),
   SHIFT_CTRL_ENTER(new KeyCodeCombination(KeyCode.ENTER, new Modifier[]{KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN})),
   SHIFT_CTRL_COMMA(new KeyCodeCombination(KeyCode.COMMA, new Modifier[]{KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN}));

   private final String customKeyCode;
   private final KeyCodeCombination keyCodeCombination;

   private KeyboardShortcut(KeyCodeCombination keyCodeCombination) {
      this.customKeyCode = null;
      this.keyCodeCombination = keyCodeCombination;
   }

   private KeyboardShortcut(String customKeyCode, Modifier... modifiers) {
      this.customKeyCode = customKeyCode;
      this.keyCodeCombination = new KeyCodeCombination(KeyCode.UNDERSCORE, modifiers);
   }

   public String getCustomKeyCode() {
      return this.customKeyCode;
   }

   public boolean isCustom() {
      return this.customKeyCode != null;
   }

   public KeyCodeCombination getKeyCodeCombination() {
      return this.keyCodeCombination;
   }

   public boolean match(KeyEvent e) {
      return this.keyCodeCombination.match(e);
   }
}
