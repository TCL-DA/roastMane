package javafx.scene.input;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.Locale;

public abstract class KeyCombination {
   public static final KeyCombination.Modifier SHIFT_DOWN = new KeyCombination.Modifier(KeyCode.SHIFT, KeyCombination.ModifierValue.DOWN);
   public static final KeyCombination.Modifier SHIFT_ANY = new KeyCombination.Modifier(KeyCode.SHIFT, KeyCombination.ModifierValue.ANY);
   public static final KeyCombination.Modifier CONTROL_DOWN = new KeyCombination.Modifier(KeyCode.CONTROL, KeyCombination.ModifierValue.DOWN);
   public static final KeyCombination.Modifier CONTROL_ANY = new KeyCombination.Modifier(KeyCode.CONTROL, KeyCombination.ModifierValue.ANY);
   public static final KeyCombination.Modifier ALT_DOWN = new KeyCombination.Modifier(KeyCode.ALT, KeyCombination.ModifierValue.DOWN);
   public static final KeyCombination.Modifier ALT_ANY = new KeyCombination.Modifier(KeyCode.ALT, KeyCombination.ModifierValue.ANY);
   public static final KeyCombination.Modifier META_DOWN = new KeyCombination.Modifier(KeyCode.META, KeyCombination.ModifierValue.DOWN);
   public static final KeyCombination.Modifier META_ANY = new KeyCombination.Modifier(KeyCode.META, KeyCombination.ModifierValue.ANY);
   public static final KeyCombination.Modifier SHORTCUT_DOWN = new KeyCombination.Modifier(KeyCode.SHORTCUT, KeyCombination.ModifierValue.DOWN);
   public static final KeyCombination.Modifier SHORTCUT_ANY = new KeyCombination.Modifier(KeyCode.SHORTCUT, KeyCombination.ModifierValue.ANY);
   private static final KeyCombination.Modifier[] POSSIBLE_MODIFIERS = new KeyCombination.Modifier[]{
      SHIFT_DOWN, SHIFT_ANY, CONTROL_DOWN, CONTROL_ANY, ALT_DOWN, ALT_ANY, META_DOWN, META_ANY, SHORTCUT_DOWN, SHORTCUT_ANY
   };
   public static final KeyCombination NO_MATCH = new KeyCombination() {
      @Override
      public boolean match(KeyEvent var1) {
         return false;
      }
   };
   private final KeyCombination.ModifierValue shift;
   private final KeyCombination.ModifierValue control;
   private final KeyCombination.ModifierValue alt;
   private final KeyCombination.ModifierValue meta;
   private final KeyCombination.ModifierValue shortcut;

   public final KeyCombination.ModifierValue getShift() {
      return this.shift;
   }

   public final KeyCombination.ModifierValue getControl() {
      return this.control;
   }

   public final KeyCombination.ModifierValue getAlt() {
      return this.alt;
   }

   public final KeyCombination.ModifierValue getMeta() {
      return this.meta;
   }

   public final KeyCombination.ModifierValue getShortcut() {
      return this.shortcut;
   }

   protected KeyCombination(
      KeyCombination.ModifierValue var1,
      KeyCombination.ModifierValue var2,
      KeyCombination.ModifierValue var3,
      KeyCombination.ModifierValue var4,
      KeyCombination.ModifierValue var5
   ) {
      if (var1 != null && var2 != null && var3 != null && var4 != null && var5 != null) {
         this.shift = var1;
         this.control = var2;
         this.alt = var3;
         this.meta = var4;
         this.shortcut = var5;
      } else {
         throw new NullPointerException("Modifier value must not be null!");
      }
   }

   protected KeyCombination(KeyCombination.Modifier... var1) {
      this(
         getModifierValue(var1, KeyCode.SHIFT),
         getModifierValue(var1, KeyCode.CONTROL),
         getModifierValue(var1, KeyCode.ALT),
         getModifierValue(var1, KeyCode.META),
         getModifierValue(var1, KeyCode.SHORTCUT)
      );
   }

   public boolean match(KeyEvent var1) {
      KeyCode var2 = Toolkit.getToolkit().getPlatformShortcutKey();
      return test(KeyCode.SHIFT, this.shift, var2, this.shortcut, var1.isShiftDown())
         && test(KeyCode.CONTROL, this.control, var2, this.shortcut, var1.isControlDown())
         && test(KeyCode.ALT, this.alt, var2, this.shortcut, var1.isAltDown())
         && test(KeyCode.META, this.meta, var2, this.shortcut, var1.isMetaDown());
   }

   public String getName() {
      StringBuilder var1 = new StringBuilder();
      this.addModifiersIntoString(var1);
      return var1.toString();
   }

   public String getDisplayText() {
      StringBuilder var1 = new StringBuilder();
      if (PlatformUtil.isMac()) {
         if (this.getControl() == KeyCombination.ModifierValue.DOWN) {
            var1.append("⌃");
         }

         if (this.getAlt() == KeyCombination.ModifierValue.DOWN) {
            var1.append("⌥");
         }

         if (this.getShift() == KeyCombination.ModifierValue.DOWN) {
            var1.append("⇧");
         }

         if (this.getMeta() == KeyCombination.ModifierValue.DOWN || this.getShortcut() == KeyCombination.ModifierValue.DOWN) {
            var1.append("⌘");
         }
      } else {
         if (this.getControl() == KeyCombination.ModifierValue.DOWN || this.getShortcut() == KeyCombination.ModifierValue.DOWN) {
            var1.append("Ctrl+");
         }

         if (this.getAlt() == KeyCombination.ModifierValue.DOWN) {
            var1.append("Alt+");
         }

         if (this.getShift() == KeyCombination.ModifierValue.DOWN) {
            var1.append("Shift+");
         }

         if (this.getMeta() == KeyCombination.ModifierValue.DOWN) {
            var1.append("Meta+");
         }
      }

      return var1.toString();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof KeyCombination)) {
         return false;
      } else {
         KeyCombination var2 = (KeyCombination)var1;
         return this.shift == var2.shift && this.control == var2.control && this.alt == var2.alt && this.meta == var2.meta && this.shortcut == var2.shortcut;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 7;
      var1 = 23 * var1 + this.shift.hashCode();
      var1 = 23 * var1 + this.control.hashCode();
      var1 = 23 * var1 + this.alt.hashCode();
      var1 = 23 * var1 + this.meta.hashCode();
      return 23 * var1 + this.shortcut.hashCode();
   }

   @Override
   public String toString() {
      return this.getName();
   }

   public static KeyCombination valueOf(String var0) {
      ArrayList var1 = new ArrayList(4);
      String[] var2 = splitName(var0);
      KeyCode var3 = null;
      String var4 = null;

      for (String var8 : var2) {
         if (var8.length() > 2 && var8.charAt(0) == '\'' && var8.charAt(var8.length() - 1) == '\'') {
            if (var3 != null || var4 != null) {
               throw new IllegalArgumentException("Cannot parse key binding " + var0);
            }

            var4 = var8.substring(1, var8.length() - 1).replace("\\'", "'");
         } else {
            String var9 = normalizeToken(var8);
            KeyCombination.Modifier var10 = getModifier(var9);
            if (var10 != null) {
               var1.add(var10);
            } else {
               if (var3 != null || var4 != null) {
                  throw new IllegalArgumentException("Cannot parse key binding " + var0);
               }

               var3 = KeyCode.getKeyCode(var9);
               if (var3 == null) {
                  var4 = var8;
               }
            }
         }
      }

      if (var3 == null && var4 == null) {
         throw new IllegalArgumentException("Cannot parse key binding " + var0);
      } else {
         KeyCombination.Modifier[] var11 = var1.toArray(new KeyCombination.Modifier[var1.size()]);
         return (KeyCombination)(var3 != null ? new KeyCodeCombination(var3, var11) : new KeyCharacterCombination(var4, var11));
      }
   }

   public static KeyCombination keyCombination(String var0) {
      return valueOf(var0);
   }

   private void addModifiersIntoString(StringBuilder var1) {
      addModifierIntoString(var1, KeyCode.SHIFT, this.shift);
      addModifierIntoString(var1, KeyCode.CONTROL, this.control);
      addModifierIntoString(var1, KeyCode.ALT, this.alt);
      addModifierIntoString(var1, KeyCode.META, this.meta);
      addModifierIntoString(var1, KeyCode.SHORTCUT, this.shortcut);
   }

   private static void addModifierIntoString(StringBuilder var0, KeyCode var1, KeyCombination.ModifierValue var2) {
      if (var2 != KeyCombination.ModifierValue.UP) {
         if (var0.length() > 0) {
            var0.append("+");
         }

         if (var2 == KeyCombination.ModifierValue.ANY) {
            var0.append("Ignore ");
         }

         var0.append(var1.getName());
      }
   }

   private static boolean test(KeyCode var0, KeyCombination.ModifierValue var1, KeyCode var2, KeyCombination.ModifierValue var3, boolean var4) {
      KeyCombination.ModifierValue var5 = var0 == var2 ? resolveModifierValue(var1, var3) : var1;
      return test(var5, var4);
   }

   private static boolean test(KeyCombination.ModifierValue var0, boolean var1) {
      switch (var0) {
         case DOWN:
            return var1;
         case UP:
            return !var1;
         case ANY:
         default:
            return true;
      }
   }

   private static KeyCombination.ModifierValue resolveModifierValue(KeyCombination.ModifierValue var0, KeyCombination.ModifierValue var1) {
      if (var0 == KeyCombination.ModifierValue.DOWN || var1 == KeyCombination.ModifierValue.DOWN) {
         return KeyCombination.ModifierValue.DOWN;
      } else {
         return var0 != KeyCombination.ModifierValue.ANY && var1 != KeyCombination.ModifierValue.ANY
            ? KeyCombination.ModifierValue.UP
            : KeyCombination.ModifierValue.ANY;
      }
   }

   static KeyCombination.Modifier getModifier(String var0) {
      for (KeyCombination.Modifier var4 : POSSIBLE_MODIFIERS) {
         if (var4.toString().equals(var0)) {
            return var4;
         }
      }

      return null;
   }

   private static KeyCombination.ModifierValue getModifierValue(KeyCombination.Modifier[] var0, KeyCode var1) {
      KeyCombination.ModifierValue var2 = KeyCombination.ModifierValue.UP;

      for (KeyCombination.Modifier var6 : var0) {
         if (var6 == null) {
            throw new NullPointerException("Modifier must not be null!");
         }

         if (var6.getKey() == var1) {
            if (var2 != KeyCombination.ModifierValue.UP) {
               throw new IllegalArgumentException(var6.getValue() != var2 ? "Conflicting modifiers specified!" : "Duplicate modifiers specified!");
            }

            var2 = var6.getValue();
         }
      }

      return var2;
   }

   private static String normalizeToken(String var0) {
      String[] var1 = var0.split("\\s+");
      StringBuilder var2 = new StringBuilder();

      for (String var6 : var1) {
         if (var2.length() > 0) {
            var2.append(' ');
         }

         var2.append(var6.substring(0, 1).toUpperCase(Locale.ROOT));
         var2.append(var6.substring(1).toLowerCase(Locale.ROOT));
      }

      return var2.toString();
   }

   private static String[] splitName(String var0) {
      ArrayList var1 = new ArrayList();
      char[] var2 = var0.trim().toCharArray();
      int var3 = 0;
      int var4 = 0;
      int var5 = -1;

      for (int var6 = 0; var6 < var2.length; var6++) {
         char var7 = var2[var6];
         switch (var3) {
            case 0:
               switch (var7) {
                  case '\t':
                  case '\n':
                  case '\u000b':
                  case '\f':
                  case '\r':
                  case ' ':
                     var5 = var6;
                     var3 = 1;
                     continue;
                  case '\'':
                     if (var6 == 0 || var2[var6 - 1] != '\\') {
                        var3 = 3;
                     }
                     continue;
                  case '+':
                     var5 = var6;
                     var3 = 2;
                  default:
                     continue;
               }
            case 1:
               switch (var7) {
                  case '\t':
                  case '\n':
                  case '\u000b':
                  case '\f':
                  case '\r':
                  case ' ':
                     continue;
                  case '\'':
                     var3 = 3;
                     var5 = -1;
                     continue;
                  case '+':
                     var3 = 2;
                     continue;
                  default:
                     var3 = 0;
                     var5 = -1;
                     continue;
               }
            case 2:
               switch (var7) {
                  case '\t':
                  case '\n':
                  case '\u000b':
                  case '\f':
                  case '\r':
                  case ' ':
                     continue;
                  case '+':
                     throw new IllegalArgumentException("Cannot parse key binding " + var0);
                  default:
                     if (var5 <= var4) {
                        throw new IllegalArgumentException("Cannot parse key binding " + var0);
                     }

                     var1.add(new String(var2, var4, var5 - var4));
                     var4 = var6;
                     var5 = -1;
                     var3 = var7 == '\'' ? 3 : 0;
                     continue;
               }
            case 3:
               if (var7 == '\'' && var2[var6 - 1] != '\\') {
                  var3 = 0;
               }
         }
      }

      switch (var3) {
         case 0:
         case 1:
            var1.add(new String(var2, var4, var2.length - var4));
         default:
            return var1.toArray(new String[var1.size()]);
         case 2:
         case 3:
            throw new IllegalArgumentException("Cannot parse key binding " + var0);
      }
   }

   public static final class Modifier {
      private final KeyCode key;
      private final KeyCombination.ModifierValue value;

      private Modifier(KeyCode var1, KeyCombination.ModifierValue var2) {
         this.key = var1;
         this.value = var2;
      }

      public KeyCode getKey() {
         return this.key;
      }

      public KeyCombination.ModifierValue getValue() {
         return this.value;
      }

      @Override
      public String toString() {
         return (this.value == KeyCombination.ModifierValue.ANY ? "Ignore " : "") + this.key.getName();
      }
   }

   public static enum ModifierValue {
      DOWN,
      UP,
      ANY;
   }
}
