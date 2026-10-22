package org.glassfish.jersey.uri.internal;

import java.util.Collection;
import java.util.Map;
import org.glassfish.jersey.uri.UriComponent;

class TemplateVariable extends UriPart {
   protected final TemplateVariable.Position position;
   protected int len = -1;
   protected boolean star = false;

   TemplateVariable(String part, TemplateVariable.Position position) {
      super(part);
      this.position = position;
   }

   static TemplateVariable createTemplateVariable(char type, String part, TemplateVariable.Position position) {
      TemplateVariable newType;
      switch (type) {
         case '#':
            newType = new TemplateVariable.HashTemplateVariable(part, position);
            break;
         case '$':
         case '%':
         case '\'':
         case '(':
         case ')':
         case '*':
         case ',':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
         case ':':
         case '<':
         case '=':
         case '>':
         default:
            newType = new TemplateVariable.PathTemplateVariable(part, position);
            break;
         case '&':
            newType = new TemplateVariable.QueryContinuationTemplateVariable(part, position);
            break;
         case '+':
            newType = new TemplateVariable(part, position);
            break;
         case '-':
            newType = new TemplateVariable.MinusTemplateVariable(part, position);
            break;
         case '.':
            newType = new TemplateVariable.DotTemplateVariable(part, position);
            break;
         case '/':
            newType = new TemplateVariable.SlashTemplateVariable(part, position);
            break;
         case ';':
            newType = new TemplateVariable.MatrixTemplateVariable(part, position);
            break;
         case '?':
            newType = new TemplateVariable.QueryTemplateVariable(part, position);
      }

      return newType;
   }

   @Override
   public boolean isTemplate() {
      return true;
   }

   @Override
   public String getGroup() {
      StringBuilder sb = new StringBuilder();
      if (this.position.isFirst()) {
         sb.append('{');
      } else {
         sb.append(',');
      }

      sb.append(this.getPart());
      if (this.position.isLast()) {
         sb.append('}');
      }

      return sb.toString();
   }

   @Override
   public String resolve(Object value, UriComponent.Type type, boolean encode) {
      if (value == null) {
         return "";
      } else {
         return this.position.isFirst() ? this.plainResolve(value, type, encode) : this.separator() + this.plainResolve(value, type, encode);
      }
   }

   protected char separator() {
      return ',';
   }

   protected char keyValueSeparator() {
      return (char)(this.star ? '=' : ',');
   }

   protected String plainResolve(Object value, UriComponent.Type componentType, boolean encode) {
      if (Collection.class.isInstance(value)) {
         return ((Collection)value)
            .stream()
            .map(a -> this.plainResolve(a, componentType, encode))
            .reduce("", (a, b) -> a + (a.isEmpty() ? b : this.separator() + b));
      } else {
         return Map.class.isInstance(value)
            ? ((Map)value)
               .entrySet()
               .stream()
               .map(
                  e -> this.plainResolve(e.getKey(), componentType, encode) + this.keyValueSeparator() + this.plainResolve(e.getValue(), componentType, encode)
               )
               .reduce("", (a, b) -> a + (a.isEmpty() ? b : this.separator() + b))
            : this.plainResolve(value.toString(), componentType, encode);
      }
   }

   protected String plainResolve(String value, UriComponent.Type componentType, boolean encode) {
      String val = this.len == -1 ? value : value.substring(0, Math.min(value.length(), this.len));
      return this.encode(val, componentType, encode);
   }

   protected String encode(String toEncode, UriComponent.Type componentType, boolean encode) {
      if (componentType == null) {
         componentType = this.getDefaultType();
      }

      return UriPart.percentEncode(toEncode, componentType, encode);
   }

   protected UriComponent.Type getDefaultType() {
      return UriComponent.Type.PATH;
   }

   void setLength(int len) {
      this.len = len;
   }

   void setStar(boolean b) {
      this.star = b;
   }

   private static class DotTemplateVariable extends TemplateVariable.MinusTemplateVariable {
      protected DotTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position);
      }

      @Override
      public String resolve(Object value, UriComponent.Type type, boolean encode) {
         return value == null ? "" : '.' + this.plainResolve(value, type, encode);
      }

      @Override
      protected char separator() {
         return this.star ? '.' : super.separator();
      }
   }

   private abstract static class ExtendedVariable extends TemplateVariable {
      private final Character firstSymbol;
      private final char separator;
      protected final boolean appendEmpty;

      protected ExtendedVariable(String part, TemplateVariable.Position position, Character firstSymbol, char separator, boolean appendEmpty) {
         super(part, position);
         this.firstSymbol = firstSymbol;
         this.separator = separator;
         this.appendEmpty = appendEmpty;
      }

      @Override
      public String resolve(Object value, UriComponent.Type componentType, boolean encode) {
         if (value == null) {
            return "";
         } else {
            String sValue = super.plainResolve(value, componentType, encode);
            StringBuilder sb = new StringBuilder();
            if (this.position.isFirst()) {
               sb.append(this.firstSymbol);
            } else {
               sb.append(this.separator);
            }

            if (!this.star) {
               sb.append(this.getPart());
               if (this.appendEmpty || !sValue.isEmpty()) {
                  sb.append('=').append(sValue);
               }
            } else if (!Map.class.isInstance(value)) {
               String[] split = sValue.split(String.valueOf(this.separator()));

               for (int i = 0; i != split.length; i++) {
                  sb.append(this.getPart());
                  sb.append('=').append(split[i]);
                  if (i != split.length - 1) {
                     sb.append(this.separator);
                  }
               }
            } else if (Map.class.isInstance(value)) {
               sb.append(sValue);
            }

            return sb.toString();
         }
      }

      @Override
      protected char separator() {
         return this.star ? this.separator : super.separator();
      }
   }

   private static class HashTemplateVariable extends TemplateVariable {
      protected HashTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position);
      }

      @Override
      public String resolve(Object value, UriComponent.Type type, boolean encode) {
         return (value != null && this.position.isFirst() ? "#" : "") + super.resolve(value, type, encode);
      }

      @Override
      protected UriComponent.Type getDefaultType() {
         return UriComponent.Type.PATH;
      }
   }

   private static class MatrixTemplateVariable extends TemplateVariable.ExtendedVariable {
      protected MatrixTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position, ';', ';', false);
      }

      @Override
      protected UriComponent.Type getDefaultType() {
         return UriComponent.Type.QUERY;
      }

      @Override
      public String resolve(Object value, UriComponent.Type componentType, boolean encode) {
         return super.resolve(value, this.getDefaultType(), encode);
      }
   }

   private static class MinusTemplateVariable extends TemplateVariable {
      protected MinusTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position);
      }

      @Override
      protected String encode(String toEncode, UriComponent.Type componentType, boolean encode) {
         return super.encode(toEncode, UriComponent.Type.QUERY, encode);
      }

      @Override
      protected UriComponent.Type getDefaultType() {
         return UriComponent.Type.QUERY;
      }
   }

   private static class PathTemplateVariable extends TemplateVariable {
      protected PathTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position);
      }

      @Override
      public boolean throwWhenNoTemplateArg() {
         return true;
      }

      @Override
      protected UriComponent.Type getDefaultType() {
         return UriComponent.Type.PATH;
      }
   }

   static enum Position {
      FIRST((byte)12),
      MIDDLE((byte)10),
      LAST((byte)9),
      SINGLE((byte)15);

      final byte val;

      private Position(byte val) {
         this.val = val;
      }

      boolean isLast() {
         return (this.val & LAST.val) == LAST.val;
      }

      boolean isFirst() {
         return (this.val & FIRST.val) == FIRST.val;
      }
   }

   private static class QueryContinuationTemplateVariable extends TemplateVariable.ExtendedVariable {
      protected QueryContinuationTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position, '&', '&', true);
      }

      @Override
      protected UriComponent.Type getDefaultType() {
         return UriComponent.Type.QUERY;
      }

      @Override
      public String resolve(Object value, UriComponent.Type componentType, boolean encode) {
         return super.resolve(value, this.getDefaultType(), encode);
      }
   }

   private static class QueryTemplateVariable extends TemplateVariable.ExtendedVariable {
      protected QueryTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position, '?', '&', true);
      }
   }

   private static class SlashTemplateVariable extends TemplateVariable.MinusTemplateVariable {
      protected SlashTemplateVariable(String part, TemplateVariable.Position position) {
         super(part, position);
      }

      @Override
      public String resolve(Object value, UriComponent.Type type, boolean encode) {
         return value == null ? "" : '/' + this.plainResolve(value, type, encode);
      }

      @Override
      protected char separator() {
         return this.star ? '/' : super.separator();
      }
   }
}
