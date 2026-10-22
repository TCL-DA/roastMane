package impl.org.controlsfx.tableview2.filter.parser;

import java.util.function.Predicate;

public interface Operation<T, S> {
   String get();

   String getSymbol();

   int length();

   Predicate<T> operate(S var1);
}
