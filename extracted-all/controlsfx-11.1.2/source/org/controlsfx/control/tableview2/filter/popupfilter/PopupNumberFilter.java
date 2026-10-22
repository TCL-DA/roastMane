package org.controlsfx.control.tableview2.filter.popupfilter;

import impl.org.controlsfx.tableview2.filter.parser.number.NumberParser;
import java.util.List;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.parser.Parser;

public class PopupNumberFilter<S, T extends Number> extends PopupFilter<S, T> {
   private final NumberParser<T> numberParser = new NumberParser<>();

   public PopupNumberFilter(FilteredTableColumn<S, T> tableColumn) {
      super(tableColumn);
      this.text.addListener((obs, ov, nv) -> {
         if (nv != null && !nv.isEmpty()) {
            tableColumn.setPredicate(this.getParser().parse(nv));
         } else {
            tableColumn.setPredicate(null);
         }
      });
   }

   @Override
   public List<String> getOperations() {
      return this.numberParser.operators();
   }

   @Override
   public Parser<T> getParser() {
      return this.numberParser;
   }
}
