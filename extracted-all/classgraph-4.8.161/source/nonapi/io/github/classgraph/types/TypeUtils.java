package nonapi.io.github.classgraph.types;

public final class TypeUtils {
   private TypeUtils() {
   }

   public static boolean getIdentifierToken(Parser parser, boolean stopAtDollarSign) {
      boolean consumedChar = false;

      while (parser.hasMore()) {
         char c = parser.peek();
         if (c == '/') {
            parser.appendToToken('.');
            parser.next();
            consumedChar = true;
         } else {
            if (c == ';' || c == '[' || c == '<' || c == '>' || c == ':' || stopAtDollarSign && c == '$') {
               break;
            }

            parser.appendToToken(c);
            parser.next();
            consumedChar = true;
         }
      }

      return consumedChar;
   }

   private static void appendModifierKeyword(StringBuilder buf, String modifierKeyword) {
      if (buf.length() > 0 && buf.charAt(buf.length() - 1) != ' ') {
         buf.append(' ');
      }

      buf.append(modifierKeyword);
   }

   public static void modifiersToString(int modifiers, TypeUtils.ModifierType modifierType, boolean isDefault, StringBuilder buf) {
      if ((modifiers & 1) != 0) {
         appendModifierKeyword(buf, "public");
      } else if ((modifiers & 2) != 0) {
         appendModifierKeyword(buf, "private");
      } else if ((modifiers & 4) != 0) {
         appendModifierKeyword(buf, "protected");
      }

      if (modifierType != TypeUtils.ModifierType.FIELD && (modifiers & 1024) != 0) {
         appendModifierKeyword(buf, "abstract");
      }

      if ((modifiers & 8) != 0) {
         appendModifierKeyword(buf, "static");
      }

      if (modifierType == TypeUtils.ModifierType.FIELD) {
         if ((modifiers & 64) != 0) {
            appendModifierKeyword(buf, "volatile");
         }

         if ((modifiers & 128) != 0) {
            appendModifierKeyword(buf, "transient");
         }
      }

      if ((modifiers & 16) != 0) {
         appendModifierKeyword(buf, "final");
      }

      if (modifierType == TypeUtils.ModifierType.METHOD) {
         if ((modifiers & 32) != 0) {
            appendModifierKeyword(buf, "synchronized");
         }

         if (isDefault) {
            appendModifierKeyword(buf, "default");
         }
      }

      if ((modifiers & 4096) != 0) {
         appendModifierKeyword(buf, "synthetic");
      }

      if (modifierType != TypeUtils.ModifierType.FIELD && (modifiers & 64) != 0) {
         appendModifierKeyword(buf, "bridge");
      }

      if (modifierType == TypeUtils.ModifierType.METHOD && (modifiers & 256) != 0) {
         appendModifierKeyword(buf, "native");
      }

      if (modifierType != TypeUtils.ModifierType.FIELD && (modifiers & 2048) != 0) {
         appendModifierKeyword(buf, "strictfp");
      }
   }

   public static enum ModifierType {
      CLASS,
      METHOD,
      FIELD;
   }
}
