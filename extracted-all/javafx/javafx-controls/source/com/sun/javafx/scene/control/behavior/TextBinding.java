package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import java.util.Objects;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCombination.Modifier;

public class TextBinding {
   private final char MNEMONIC_SYMBOL = '_';
   private String sourceText = null;
   private String text = null;
   private String mnemonic = null;
   private KeyCombination mnemonicKeyCombination = null;
   private int mnemonicIndex = -1;
   private String extendedMnemonicText = null;

   public String getText() {
      return this.text;
   }

   public String getMnemonic() {
      return this.mnemonic;
   }

   public KeyCombination getMnemonicKeyCombination() {
      if (this.mnemonic != null && this.mnemonicKeyCombination == null) {
         this.mnemonicKeyCombination = new TextBinding.MnemonicKeyCombination(this.mnemonic);
      }

      return this.mnemonicKeyCombination;
   }

   public int getMnemonicIndex() {
      return this.mnemonicIndex;
   }

   public String getExtendedMnemonicText() {
      return this.extendedMnemonicText;
   }

   public TextBinding(String var1) {
      this.update(var1);
   }

   public void update(String var1) {
      if (!Objects.equals(this.sourceText, var1)) {
         this.sourceText = var1;
         this.mnemonic = null;
         this.mnemonicKeyCombination = null;
         this.mnemonicIndex = -1;
         this.extendedMnemonicText = null;
         this.parseAndSplit(var1);
      }
   }

   private void parseAndSplit(String var1) {
      if (var1 != null && var1.length() != 0) {
         StringBuilder var2 = new StringBuilder(var1.length());
         int var3 = 0;

         for (int var4 = var1.length(); var3 < var4; var3++) {
            if (this.isEscapedMnemonicSymbol(var1, var3)) {
               var2.append(var1.charAt(var3++));
            } else {
               if (this.isExtendedMnemonic(var1, var3)) {
                  this.mnemonic = String.valueOf(var1.charAt(var3 + 2));
                  this.mnemonicIndex = var3;
                  this.extendedMnemonicText = var1.substring(var3 + 1, var3 + 4);
                  var3 += 4;
                  break;
               }

               if (this.isSimpleMnemonic(var1, var3)) {
                  char var5 = var1.charAt(var3 + 1);
                  this.mnemonic = String.valueOf(var5);
                  this.mnemonicIndex = var3++;
                  break;
               }

               var2.append(var1.charAt(var3));
            }
         }

         if (var1.length() > var3) {
            var2.append(var1.substring(var3));
         }

         this.text = var2.toString();
      } else {
         this.text = var1;
      }
   }

   private boolean isEscapedMnemonicSymbol(String var1, int var2) {
      return var1.length() > var2 + 1 && var1.charAt(var2) == '_' && var1.charAt(var2 + 1) == '_';
   }

   private boolean isSimpleMnemonic(String var1, int var2) {
      return var1.length() > var2 + 1 && var1.charAt(var2) == '_' && var1.charAt(var2 + 1) != '_' && !Character.isWhitespace(var1.charAt(var2 + 1));
   }

   private boolean isExtendedMnemonic(String var1, int var2) {
      return var1.length() > var2 + 3
         && var1.charAt(var2) == '_'
         && var1.charAt(var2 + 1) == '('
         && !Character.isWhitespace(var1.charAt(var2 + 2))
         && var1.charAt(var2 + 3) == ')';
   }

   public static class MnemonicKeyCombination extends KeyCombination {
      private String character = "";

      public MnemonicKeyCombination(String var1) {
         super(new Modifier[]{PlatformUtil.isMac() ? KeyCombination.META_DOWN : KeyCombination.ALT_DOWN});
         this.character = var1;
      }

      public final String getCharacter() {
         return this.character;
      }

      public boolean match(KeyEvent var1) {
         String var2 = var1.getText();
         return var2 != null && !var2.isEmpty() && var2.equalsIgnoreCase(this.getCharacter()) && super.match(var1);
      }

      public String getName() {
         StringBuilder var1 = new StringBuilder();
         var1.append(super.getName());
         if (var1.length() > 0) {
            var1.append("+");
         }

         return var1.append('\'').append(this.character.replace("'", "\\'")).append('\'').toString();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof TextBinding.MnemonicKeyCombination)
               ? false
               : this.character.equals(((TextBinding.MnemonicKeyCombination)var1).getCharacter()) && super.equals(var1);
         }
      }

      public int hashCode() {
         return 23 * super.hashCode() + this.character.hashCode();
      }
   }
}
