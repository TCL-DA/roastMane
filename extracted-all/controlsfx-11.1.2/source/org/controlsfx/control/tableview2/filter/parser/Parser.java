package org.controlsfx.control.tableview2.filter.parser;

import impl.org.controlsfx.tableview2.filter.parser.aggregate.AggregatorsParser;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Parser<T> {
   Predicate<T> parse(String var1);

   List<String> operators();

   String getSymbol(String var1);

   boolean isValid(String var1);

   String getErrorMessage();

   default Predicate<T> aggregate(String text) {
      Optional<AggregatorsParser.Aggregation> optionalAggregation = AggregatorsParser.findParser(text);
      if (optionalAggregation.isPresent()) {
         AggregatorsParser.Aggregation aggregation = optionalAggregation.get();
         return aggregation.getParser().aggregate(aggregation.getLhs(), aggregation.getRhs(), this);
      } else {
         return null;
      }
   }
}
