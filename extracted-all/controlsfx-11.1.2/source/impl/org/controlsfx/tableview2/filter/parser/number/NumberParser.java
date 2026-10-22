package impl.org.controlsfx.tableview2.filter.parser.number;

import impl.org.controlsfx.i18n.Localization;
import impl.org.controlsfx.tableview2.filter.parser.Operation;
import impl.org.controlsfx.tableview2.filter.parser.aggregate.AggregatorsParser;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.controlsfx.control.tableview2.filter.parser.Parser;

public class NumberParser<T extends Number> implements Parser<T> {
   private String errorString = "";

   @Override
   public Predicate<T> parse(String text) {
      this.errorString = "";
      Predicate<T> aggregation = this.aggregate(text);
      if (aggregation == null) {
         Optional<NumberParser.NumberOperation> optionalOperation = Stream.of(NumberParser.NumberOperation.values())
            .filter(opr -> text.startsWith(opr.get()))
            .filter(opr -> text.length() > opr.length())
            .findFirst();
         if (optionalOperation.isPresent()) {
            NumberParser.NumberOperation operation = optionalOperation.get();
            String numText = this.trim(text, operation.length());
            if (!this.isNumeric(numText)) {
               this.errorString = Localization.localize(Localization.asKey("parser.text.error.number.input"));
               return null;
            } else {
               return (Predicate<T>)operation.operate(Double.valueOf(this.convert(numText)));
            }
         } else {
            this.errorString = Localization.localize(Localization.asKey("parser.text.error.start.operator"));
            return null;
         }
      } else {
         return aggregation;
      }
   }

   @Override
   public List<String> operators() {
      return Stream.concat(Arrays.stream(NumberParser.NumberOperation.values()).map(Operation::get), AggregatorsParser.getStrings())
         .collect(Collectors.toList());
   }

   @Override
   public String getSymbol(String text) {
      return Arrays.stream(NumberParser.NumberOperation.values())
         .filter(op -> op.get().equals(text))
         .map(Operation::getSymbol)
         .findFirst()
         .orElse(i18nString("symbol.default"));
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

   private boolean isNumeric(String str) {
      return !str.isEmpty() && str.matches("-?\\d+(\\.\\d+)?");
   }

   private String trim(String text, int startIndex) {
      return text.substring(startIndex, text.length()).trim();
   }

   private double convert(String numText) {
      return Double.parseDouble(numText);
   }

   private static String i18nString(String key) {
      return Localization.localize(Localization.asKey("parser.text.operator." + key));
   }

   private static enum NumberOperation implements Operation<Number, Number> {
      EQUALS("text.equals", "symbol.equals") {
         public Predicate<Number> operate(Number num) {
            return t -> t != null && t.doubleValue() == num.doubleValue();
         }
      },
      NOT_EQUALS("text.notequals", "symbol.notequals") {
         public Predicate<Number> operate(Number num) {
            return t -> t != null && t.doubleValue() != num.doubleValue();
         }
      },
      GREATER_THAN_EQUALS("text.greaterthanequals", "symbol.greaterthanequals") {
         public Predicate<Number> operate(Number num) {
            return t -> t != null && t.doubleValue() >= num.doubleValue();
         }
      },
      GREATER_THAN("text.greaterthan", "symbol.greaterthan") {
         public Predicate<Number> operate(Number num) {
            return t -> t != null && t.doubleValue() > num.doubleValue();
         }
      },
      LESS_THAN_EQUALS("text.lessthanequals", "symbol.lessthanequals") {
         public Predicate<Number> operate(Number num) {
            return t -> t != null && t.doubleValue() <= num.doubleValue();
         }
      },
      LESS_THAN("text.lessthan", "symbol.lessthan") {
         public Predicate<Number> operate(Number num) {
            return t -> t != null && t.doubleValue() < num.doubleValue();
         }
      };

      private final String opr;
      private final String symbol;

      private NumberOperation(String opr, String symbol) {
         this.opr = NumberParser.i18nString(opr);
         this.symbol = NumberParser.i18nString(symbol);
      }

      @Override
      public int length() {
         return this.opr.length();
      }

      @Override
      public String get() {
         return this.opr;
      }

      @Override
      public String getSymbol() {
         return this.symbol;
      }
   }
}
