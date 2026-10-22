package org.controlsfx.control.tableview2.filter.popupfilter;

import impl.org.controlsfx.tableview2.filter.parser.string.StringParser;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.StringConverter;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.filter.parser.Parser;

public class PopupStringFilter<S, T> extends PopupFilter<S, T> {
   private final StringParser<T> stringParser;
   private final BooleanProperty caseSensitive = new SimpleBooleanProperty(this, "caseSensitive", true) {
      protected void invalidated() {
         PopupStringFilter.this.stringParser.setCaseSensitive(this.get());
      }
   };
   private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this, "converter", defaultStringConverter()) {
      protected void invalidated() {
         PopupStringFilter.this.stringParser.setConverter((StringConverter<T>)this.get());
      }
   };

   private static <T> StringConverter<T> defaultStringConverter() {
      return new StringConverter<T>() {
         public String toString(T t) {
            return t == null ? null : t.toString();
         }

         public T fromString(String string) {
            return (T)string;
         }
      };
   }

   public PopupStringFilter(FilteredTableColumn<S, T> tableColumn) {
      super(tableColumn);
      this.stringParser = new StringParser<>(this.caseSensitive.get(), this.getConverter());
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
      return this.stringParser.operators();
   }

   @Override
   public Parser<T> getParser() {
      return this.stringParser;
   }

   public final BooleanProperty caseSensitiveProperty() {
      return this.caseSensitive;
   }

   public final ObjectProperty<StringConverter<T>> converterProperty() {
      return this.converter;
   }

   public final void setConverter(StringConverter<T> value) {
      this.converterProperty().set(value);
   }

   public final StringConverter<T> getConverter() {
      return (StringConverter<T>)this.converterProperty().get();
   }
}
