package com.sun.javafx.webkit;

import java.util.Map;
import javafx.scene.input.KeyCode;

public final class KeyCodeMap {
   private static final Map<KeyCode, KeyCodeMap.Entry> MAP = Map.ofEntries(
      entry(KeyCode.ENTER, 13, "Enter"),
      entry(KeyCode.BACK_SPACE, 8),
      entry(KeyCode.TAB, 9),
      entry(KeyCode.CANCEL, 3),
      entry(KeyCode.CLEAR, 12, "Clear"),
      entry(KeyCode.SHIFT, 16, "Shift"),
      entry(KeyCode.CONTROL, 17, "Control"),
      entry(KeyCode.ALT, 18, "Alt"),
      entry(KeyCode.PAUSE, 19, "Pause"),
      entry(KeyCode.CAPS, 20, "CapsLock"),
      entry(KeyCode.ESCAPE, 27),
      entry(KeyCode.SPACE, 32),
      entry(KeyCode.PAGE_UP, 33, "PageUp"),
      entry(KeyCode.PAGE_DOWN, 34, "PageDown"),
      entry(KeyCode.END, 35, "End"),
      entry(KeyCode.HOME, 36, "Home"),
      entry(KeyCode.LEFT, 37, "Left"),
      entry(KeyCode.UP, 38, "Up"),
      entry(KeyCode.RIGHT, 39, "Right"),
      entry(KeyCode.DOWN, 40, "Down"),
      entry(KeyCode.COMMA, 188),
      entry(KeyCode.MINUS, 189),
      entry(KeyCode.PERIOD, 190),
      entry(KeyCode.SLASH, 191),
      entry(KeyCode.DIGIT0, 48),
      entry(KeyCode.DIGIT1, 49),
      entry(KeyCode.DIGIT2, 50),
      entry(KeyCode.DIGIT3, 51),
      entry(KeyCode.DIGIT4, 52),
      entry(KeyCode.DIGIT5, 53),
      entry(KeyCode.DIGIT6, 54),
      entry(KeyCode.DIGIT7, 55),
      entry(KeyCode.DIGIT8, 56),
      entry(KeyCode.DIGIT9, 57),
      entry(KeyCode.SEMICOLON, 186),
      entry(KeyCode.EQUALS, 187),
      entry(KeyCode.A, 65),
      entry(KeyCode.B, 66),
      entry(KeyCode.C, 67),
      entry(KeyCode.D, 68),
      entry(KeyCode.E, 69),
      entry(KeyCode.F, 70),
      entry(KeyCode.G, 71),
      entry(KeyCode.H, 72),
      entry(KeyCode.I, 73),
      entry(KeyCode.J, 74),
      entry(KeyCode.K, 75),
      entry(KeyCode.L, 76),
      entry(KeyCode.M, 77),
      entry(KeyCode.N, 78),
      entry(KeyCode.O, 79),
      entry(KeyCode.P, 80),
      entry(KeyCode.Q, 81),
      entry(KeyCode.R, 82),
      entry(KeyCode.S, 83),
      entry(KeyCode.T, 84),
      entry(KeyCode.U, 85),
      entry(KeyCode.V, 86),
      entry(KeyCode.W, 87),
      entry(KeyCode.X, 88),
      entry(KeyCode.Y, 89),
      entry(KeyCode.Z, 90),
      entry(KeyCode.OPEN_BRACKET, 219),
      entry(KeyCode.BACK_SLASH, 220),
      entry(KeyCode.CLOSE_BRACKET, 221),
      entry(KeyCode.NUMPAD0, 96),
      entry(KeyCode.NUMPAD1, 97),
      entry(KeyCode.NUMPAD2, 98),
      entry(KeyCode.NUMPAD3, 99),
      entry(KeyCode.NUMPAD4, 100),
      entry(KeyCode.NUMPAD5, 101),
      entry(KeyCode.NUMPAD6, 102),
      entry(KeyCode.NUMPAD7, 103),
      entry(KeyCode.NUMPAD8, 104),
      entry(KeyCode.NUMPAD9, 105),
      entry(KeyCode.MULTIPLY, 106),
      entry(KeyCode.ADD, 107),
      entry(KeyCode.SEPARATOR, 108),
      entry(KeyCode.SUBTRACT, 109),
      entry(KeyCode.DECIMAL, 110),
      entry(KeyCode.DIVIDE, 111),
      entry(KeyCode.DELETE, 46, "U+007F"),
      entry(KeyCode.NUM_LOCK, 144),
      entry(KeyCode.SCROLL_LOCK, 145, "Scroll"),
      entry(KeyCode.F1, 112, "F1"),
      entry(KeyCode.F2, 113, "F2"),
      entry(KeyCode.F3, 114, "F3"),
      entry(KeyCode.F4, 115, "F4"),
      entry(KeyCode.F5, 116, "F5"),
      entry(KeyCode.F6, 117, "F6"),
      entry(KeyCode.F7, 118, "F7"),
      entry(KeyCode.F8, 119, "F8"),
      entry(KeyCode.F9, 120, "F9"),
      entry(KeyCode.F10, 121, "F10"),
      entry(KeyCode.F11, 122, "F11"),
      entry(KeyCode.F12, 123, "F12"),
      entry(KeyCode.F13, 124, "F13"),
      entry(KeyCode.F14, 125, "F14"),
      entry(KeyCode.F15, 126, "F15"),
      entry(KeyCode.F16, 127, "F16"),
      entry(KeyCode.F17, 128, "F17"),
      entry(KeyCode.F18, 129, "F18"),
      entry(KeyCode.F19, 130, "F19"),
      entry(KeyCode.F20, 131, "F20"),
      entry(KeyCode.F21, 132, "F21"),
      entry(KeyCode.F22, 133, "F22"),
      entry(KeyCode.F23, 134, "F23"),
      entry(KeyCode.F24, 135, "F24"),
      entry(KeyCode.PRINTSCREEN, 44, "PrintScreen"),
      entry(KeyCode.INSERT, 45, "Insert"),
      entry(KeyCode.HELP, 47, "Help"),
      entry(KeyCode.META, 0, "Meta"),
      entry(KeyCode.BACK_QUOTE, 192),
      entry(KeyCode.QUOTE, 222),
      entry(KeyCode.KP_UP, 38, "Up"),
      entry(KeyCode.KP_DOWN, 40, "Down"),
      entry(KeyCode.KP_LEFT, 37, "Left"),
      entry(KeyCode.KP_RIGHT, 39, "Right"),
      entry(KeyCode.AMPERSAND, 55),
      entry(KeyCode.ASTERISK, 56),
      entry(KeyCode.QUOTEDBL, 222),
      entry(KeyCode.LESS, 188),
      entry(KeyCode.GREATER, 190),
      entry(KeyCode.BRACELEFT, 219),
      entry(KeyCode.BRACERIGHT, 221),
      entry(KeyCode.AT, 50),
      entry(KeyCode.COLON, 186),
      entry(KeyCode.CIRCUMFLEX, 54),
      entry(KeyCode.DOLLAR, 52),
      entry(KeyCode.EXCLAMATION_MARK, 49),
      entry(KeyCode.LEFT_PARENTHESIS, 57),
      entry(KeyCode.NUMBER_SIGN, 51),
      entry(KeyCode.PLUS, 187),
      entry(KeyCode.RIGHT_PARENTHESIS, 48),
      entry(KeyCode.UNDERSCORE, 189),
      entry(KeyCode.WINDOWS, 91, "Win"),
      entry(KeyCode.CONTEXT_MENU, 93),
      entry(KeyCode.FINAL, 24),
      entry(KeyCode.CONVERT, 28),
      entry(KeyCode.NONCONVERT, 29),
      entry(KeyCode.ACCEPT, 30),
      entry(KeyCode.MODECHANGE, 31),
      entry(KeyCode.KANA, 21),
      entry(KeyCode.KANJI, 25),
      entry(KeyCode.ALT_GRAPH, 165),
      entry(KeyCode.PLAY, 250),
      entry(KeyCode.TRACK_PREV, 177),
      entry(KeyCode.TRACK_NEXT, 176),
      entry(KeyCode.VOLUME_UP, 175),
      entry(KeyCode.VOLUME_DOWN, 174),
      entry(KeyCode.MUTE, 173)
   );

   private static Map.Entry<KeyCode, KeyCodeMap.Entry> entry(KeyCode var0, int var1, String var2) {
      return Map.entry(var0, new KeyCodeMap.Entry(var1, var2));
   }

   private static Map.Entry<KeyCode, KeyCodeMap.Entry> entry(KeyCode var0, int var1) {
      return entry(var0, var1, null);
   }

   public static KeyCodeMap.Entry lookup(KeyCode var0) {
      KeyCodeMap.Entry var1 = MAP.get(var0);
      if (var1 == null || var1.getKeyIdentifier() == null) {
         int var2 = var1 != null ? var1.getWindowsVirtualKeyCode() : 0;
         String var3 = String.format("U+%04X", var2);
         var1 = new KeyCodeMap.Entry(var2, var3);
      }

      return var1;
   }

   public static final class Entry {
      private final int windowsVirtualKeyCode;
      private final String keyIdentifier;

      private Entry(int var1, String var2) {
         this.windowsVirtualKeyCode = var1;
         this.keyIdentifier = var2;
      }

      public int getWindowsVirtualKeyCode() {
         return this.windowsVirtualKeyCode;
      }

      public String getKeyIdentifier() {
         return this.keyIdentifier;
      }
   }
}
