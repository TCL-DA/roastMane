package impl.org.controlsfx.tableview2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javafx.scene.control.TableColumnBase;
import org.controlsfx.control.tableview2.FilteredTableColumn;

public class FilteredColumnPredicate<S, T> implements Predicate<S> {
   private final List<? extends TableColumnBase> columns;

   public FilteredColumnPredicate(TableColumnBase<S, T>... columns) {
      this(Arrays.asList(columns));
   }

   public FilteredColumnPredicate(List<? extends TableColumnBase> columns) {
      this.columns = Collections.unmodifiableList(columns);
   }

   @Override
   public boolean test(S s) {
      for (TableColumnBase<S, T> tc : this.columns) {
         if (tc instanceof FilteredTableColumn) {
            FilteredTableColumn<S, T> ftc = (FilteredTableColumn<S, T>)tc;
            if (ftc.getPredicate() != null && ftc.isFilterable()) {
               T value = (T)tc.getCellData(s);
               boolean result = this.doFilter(ftc, value);
               if (!result) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      return 59 * hash + (this.columns != null ? this.columns.hashCode() : 0);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         FilteredColumnPredicate other = (FilteredColumnPredicate)obj;
         return this.columns == other.columns || this.columns != null && this.columns.equals(other.columns);
      }
   }

   private boolean doFilter(FilteredTableColumn<S, T> filter, T value) {
      Predicate<? super T> p = filter.getPredicate();
      return p == null || p.test(value);
   }

   @Override
   public String toString() {
      return "FilteredColumnsPredicate [ columns: " + this.columns + "] ";
   }
}
