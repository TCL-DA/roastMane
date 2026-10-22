package impl.org.controlsfx.tableview2.filter.parser.aggregate;

import impl.org.controlsfx.i18n.Localization;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.controlsfx.control.tableview2.filter.parser.Parser;

public enum AggregatorsParser implements Aggregator {
   AND("text.and") {
      @Override
      public <T> Predicate<T> aggregate(String lhs, String rhs, Parser<T> parser) {
         if (parser.isValid(lhs) && parser.isValid(rhs)) {
            Predicate<T> parsedLhs = parser.parse(lhs);
            Predicate<T> parsedRhs = parser.parse(rhs);
            if (parsedLhs != null && parsedRhs != null) {
               return parsedLhs.and(parsedRhs);
            }
         }

         return null;
      }
   },
   OR("text.or") {
      @Override
      public <T> Predicate<T> aggregate(String lhs, String rhs, Parser<T> parser) {
         if (parser.isValid(lhs) && parser.isValid(rhs)) {
            Predicate<T> parsedLhs = parser.parse(lhs);
            Predicate<T> parsedRhs = parser.parse(rhs);
            if (parsedLhs != null && parsedRhs != null) {
               return parsedLhs.or(parsedRhs);
            }
         }

         return null;
      }
   };

   private final String type;

   private AggregatorsParser(String type) {
      this.type = " " + i18nString(type) + " ";
   }

   public String get() {
      return this.type;
   }

   public static Stream<String> getStrings() {
      return Arrays.stream(values()).map(AggregatorsParser::get);
   }

   public static Optional<AggregatorsParser.Aggregation> findParser(String text) {
      AggregatorsParser minAggregator = null;
      int indexOf = text.length();

      for (AggregatorsParser aggregatorsParser : values()) {
         int textIndexOf = text.indexOf(aggregatorsParser.get());
         if (textIndexOf != -1 && textIndexOf < indexOf) {
            indexOf = textIndexOf;
            minAggregator = aggregatorsParser;
         }
      }

      if (minAggregator != null) {
         String lhs = text.substring(0, indexOf);
         String rhs = text.substring(indexOf + minAggregator.get().length(), text.length());
         return Optional.of(new AggregatorsParser.Aggregation(lhs.trim(), rhs.trim(), minAggregator));
      } else {
         return Optional.empty();
      }
   }

   private static String i18nString(String key) {
      return Localization.localize(Localization.asKey("parser.text.operator." + key));
   }

   public static class Aggregation {
      private final String lhs;
      private final String rhs;
      private final AggregatorsParser parser;

      Aggregation(String lhs, String rhs, AggregatorsParser parser) {
         this.lhs = lhs;
         this.rhs = rhs;
         this.parser = parser;
      }

      public String getLhs() {
         return this.lhs;
      }

      public String getRhs() {
         return this.rhs;
      }

      public AggregatorsParser getParser() {
         return this.parser;
      }
   }
}
