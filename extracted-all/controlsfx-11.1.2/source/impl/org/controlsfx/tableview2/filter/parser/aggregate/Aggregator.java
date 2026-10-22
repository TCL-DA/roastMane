package impl.org.controlsfx.tableview2.filter.parser.aggregate;

import java.util.function.Predicate;
import org.controlsfx.control.tableview2.filter.parser.Parser;

public interface Aggregator {
   <T> Predicate<T> aggregate(String var1, String var2, Parser<T> var3);
}
