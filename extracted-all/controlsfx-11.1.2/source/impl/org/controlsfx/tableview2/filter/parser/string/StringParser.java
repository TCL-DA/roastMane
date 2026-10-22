package impl.org.controlsfx.tableview2.filter.parser.string;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.tableview2.filter.parser.Operation;
import impl.org.controlsfx.tableview2.filter.parser.aggregate.AggregatorsParser;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.StringConverter;
import org.controlsfx.control.tableview2.filter.parser.Parser;

public class StringParser<T> implements Parser<T> {
   private StringConverter<T> converter;
   private boolean caseSensitive;
   private String errorString = "";
   private final List<Operation<T, String>> operations = Arrays.asList(
      new StringParser.BeginsWith(), new StringParser.EndsWith(), new StringParser.Contains(), new StringParser.EqualsTo(), new StringParser.NotEqualsTo()
   );

   public StringParser() {
   }

   public StringParser(boolean caseSensitive) {
      this(caseSensitive, null);
   }

   public StringParser(boolean caseSensitive, StringConverter<T> converter) {
      this.caseSensitive = caseSensitive;
      this.converter = converter;
   }

   public StringConverter<T> getConverter() {
      return this.converter;
   }

   public void setConverter(StringConverter<T> converter) {
      this.converter = converter;
   }

   public boolean isCaseSensitive() {
      return this.caseSensitive;
   }

   public void setCaseSensitive(boolean caseSensitive) {
      this.caseSensitive = caseSensitive;
   }

   @Override
   public Predicate<T> parse(String text) {
      this.errorString = "";
      Predicate<T> aggregation = this.aggregate(text);
      if (aggregation == null) {
         Optional<Operation<T, String>> optionalOperation = this.operations
            .stream()
            .filter(oprx -> text.startsWith(oprx.get()))
            .filter(oprx -> text.length() > oprx.length())
            .findFirst();
         if (optionalOperation.isPresent()) {
            Operation<T, String> opr = optionalOperation.get();
            String trimText = this.trim(text, opr.length());
            return trimText.isEmpty() ? null : opr.operate(trimText);
         } else {
            this.errorString = Localization.localize(Localization.asKey("parser.text.error.start.operator"));
            return null;
         }
      } else {
         return aggregation;
      }
   }

   @Override
   public boolean isValid(String text) {
      this.parse(text);
      return this.errorString.isEmpty();
   }

   @Override
   public String getErrorMessage() {
      return this.errorString;
   }

   private String trim(String text, int typeLength) {
      String trimmedText = text.substring(typeLength, text.length()).trim();
      if (!trimmedText.startsWith("\"")) {
         this.errorString = trimmedText + " " + Localization.localize(Localization.asKey("parser.text.error.string.start"));
      } else if (!trimmedText.endsWith("\"")) {
         this.errorString = trimmedText.substring(1) + " " + Localization.localize(Localization.asKey("parser.text.error.string.end"));
      } else if (trimmedText.length() > 2) {
         return trimmedText.substring(1, trimmedText.length() - 1);
      }

      return "";
   }

   @Override
   public List<String> operators() {
      return Stream.concat(this.operations.stream().map(Operation::get), AggregatorsParser.getStrings()).collect(Collectors.toList());
   }

   @Override
   public String getSymbol(String text) {
      return this.operations.stream().filter(op -> op.get().equals(text)).map(Operation::getSymbol).findFirst().orElse(i18nString("symbol.default"));
   }

   private String casedString(String string) {
      return this.caseSensitive ? string : string.toUpperCase();
   }

   private static String i18nString(String key) {
      return Localization.localize(Localization.asKey("parser.text.operator." + key));
   }

   class BeginsWith implements Operation<T, String> {
      @Override
      public String get() {
         return StringParser.i18nString("text.beginswith");
      }

      @Override
      public int length() {
         return this.get().length();
      }

      public Predicate<T> operate(String text) {
         return StringParser.this.converter != null
            ? t -> t != null && StringParser.this.casedString(StringParser.this.converter.toString(t)).startsWith(StringParser.this.casedString(text))
            : t -> StringParser.this.casedString(String.valueOf(t)).startsWith(StringParser.this.casedString(text));
      }

      @Override
      public String getSymbol() {
         return StringParser.i18nString("symbol.beginswith." + (StringParser.this.caseSensitive ? "sensitive" : "insensitive"));
      }
   }

   class Contains implements Operation<T, String> {
      @Override
      public String get() {
         return StringParser.i18nString("text.contains");
      }

      @Override
      public int length() {
         return this.get().length();
      }

      public Predicate<T> operate(String text) {
         return StringParser.this.converter != null
            ? t -> t != null && StringParser.this.casedString(StringParser.this.converter.toString(t)).contains(StringParser.this.casedString(text))
            : t -> StringParser.this.casedString(String.valueOf(t)).contains(StringParser.this.casedString(text));
      }

      @Override
      public String getSymbol() {
         return StringParser.i18nString("symbol.contains." + (StringParser.this.caseSensitive ? "sensitive" : "insensitive"));
      }
   }

   class EndsWith implements Operation<T, String> {
      @Override
      public String get() {
         return StringParser.i18nString("text.endswith");
      }

      @Override
      public int length() {
         return this.get().length();
      }

      public Predicate<T> operate(String text) {
         return StringParser.this.converter != null
            ? t -> t != null && StringParser.this.casedString(StringParser.this.converter.toString(t)).endsWith(StringParser.this.casedString(text))
            : t -> StringParser.this.casedString(String.valueOf(t)).endsWith(StringParser.this.casedString(text));
      }

      @Override
      public String getSymbol() {
         return StringParser.i18nString("symbol.endswith." + (StringParser.this.caseSensitive ? "sensitive" : "insensitive"));
      }
   }

   class EqualsTo implements Operation<T, String> {
      @Override
      public String get() {
         return StringParser.i18nString("text.equalsto");
      }

      @Override
      public int length() {
         return this.get().length();
      }

      public Predicate<T> operate(String text) {
         return StringParser.this.converter != null
            ? t -> t != null && StringParser.this.casedString(StringParser.this.converter.toString(t)).equals(StringParser.this.casedString(text))
            : t -> StringParser.this.casedString(String.valueOf(t)).equals(StringParser.this.casedString(text));
      }

      @Override
      public String getSymbol() {
         return StringParser.i18nString("symbol.equalsto." + (StringParser.this.caseSensitive ? "sensitive" : "insensitive"));
      }
   }

   class NotEqualsTo implements Operation<T, String> {
      @Override
      public String get() {
         return StringParser.i18nString("text.notequalsto");
      }

      @Override
      public int length() {
         return this.get().length();
      }

      public Predicate<T> operate(String text) {
         return StringParser.this.converter != null
            ? t -> t != null && !StringParser.this.casedString(StringParser.this.converter.toString(t)).equals(StringParser.this.casedString(text))
            : t -> !StringParser.this.casedString(String.valueOf(t)).equals(StringParser.this.casedString(text));
      }

      @Override
      public String getSymbol() {
         return StringParser.i18nString("symbol.notequalsto." + (StringParser.this.caseSensitive ? "sensitive" : "insensitive"));
      }
   }
}
