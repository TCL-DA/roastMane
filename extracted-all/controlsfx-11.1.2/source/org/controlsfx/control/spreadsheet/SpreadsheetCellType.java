package org.controlsfx.control.spreadsheet;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public abstract class SpreadsheetCellType<T> {
   protected StringConverter<T> converter;
   public static final SpreadsheetCellType<Object> OBJECT = new SpreadsheetCellType.ObjectType();
   public static final SpreadsheetCellType.StringType STRING = new SpreadsheetCellType.StringType();
   public static final SpreadsheetCellType.DoubleType DOUBLE = new SpreadsheetCellType.DoubleType();
   public static final SpreadsheetCellType.IntegerType INTEGER = new SpreadsheetCellType.IntegerType();
   public static final SpreadsheetCellType.DateType DATE = new SpreadsheetCellType.DateType();

   public SpreadsheetCellType() {
   }

   public SpreadsheetCellType(StringConverter<T> converter) {
      this.converter = converter;
   }

   public abstract SpreadsheetCellEditor createEditor(SpreadsheetView var1);

   public String toString(T object, String format) {
      return this.toString(object);
   }

   public abstract String toString(T var1);

   public boolean match(Object value) {
      return this.match(value, Collections.emptyList());
   }

   public abstract boolean match(Object var1, Object... var2);

   public boolean isError(Object value) {
      return false;
   }

   public boolean acceptDrop() {
      return false;
   }

   public abstract T convertValue(Object var1);

   public static final SpreadsheetCellType.ListType LIST(List<String> items) {
      return new SpreadsheetCellType.ListType(items);
   }

   public static class DateType extends SpreadsheetCellType<LocalDate> {
      public DateType() {
         this(new StringConverterWithFormat<LocalDate>() {
            public String toString(LocalDate item) {
               return this.toStringFormat(item, "");
            }

            public LocalDate fromString(String str) {
               try {
                  return LocalDate.parse(str);
               } catch (Exception var3) {
                  return null;
               }
            }

            public String toStringFormat(LocalDate item, String format) {
               if ("".equals(format) && item != null) {
                  return item.toString();
               } else {
                  return item != null ? item.format(DateTimeFormatter.ofPattern(format)) : "";
               }
            }
         });
      }

      public DateType(StringConverter<LocalDate> converter) {
         super(converter);
      }

      @Override
      public String toString() {
         return "date";
      }

      public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan, LocalDate value) {
         SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
         cell.setItem(value);
         return cell;
      }

      @Override
      public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
         return new SpreadsheetCellEditor.DateEditor(view, this.converter);
      }

      @Override
      public boolean match(Object value, Object... options) {
         if (value instanceof LocalDate) {
            return true;
         } else {
            try {
               LocalDate temp = (LocalDate)this.converter.fromString(value == null ? null : value.toString());
               return temp != null;
            } catch (Exception var4) {
               return false;
            }
         }
      }

      public LocalDate convertValue(Object value) {
         if (value instanceof LocalDate) {
            return (LocalDate)value;
         } else {
            try {
               return (LocalDate)this.converter.fromString(value == null ? null : value.toString());
            } catch (Exception var3) {
               return null;
            }
         }
      }

      public String toString(LocalDate item) {
         return this.converter.toString(item);
      }

      public String toString(LocalDate item, String format) {
         return ((StringConverterWithFormat)this.converter).toStringFormat(item, format);
      }
   }

   public static class DoubleType extends SpreadsheetCellType<Double> {
      public DoubleType() {
         this(new StringConverterWithFormat<Double>(new DoubleStringConverter()) {
            public String toString(Double item) {
               return this.toStringFormat(item, "");
            }

            public Double fromString(String str) {
               return str != null && !str.isEmpty() && !"NaN".equals(str) ? (Double)this.myConverter.fromString(str) : Double.NaN;
            }

            public String toStringFormat(Double item, String format) {
               try {
                  return item != null && !Double.isNaN(item) ? new DecimalFormat(format).format(item) : "";
               } catch (Exception var4) {
                  return this.myConverter.toString(item);
               }
            }
         });
      }

      public DoubleType(StringConverter<Double> converter) {
         super(converter);
      }

      @Override
      public String toString() {
         return "double";
      }

      public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan, Double value) {
         SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
         cell.setItem(value);
         return cell;
      }

      @Override
      public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
         return new SpreadsheetCellEditor.DoubleEditor(view);
      }

      @Override
      public boolean match(Object value, Object... options) {
         if (value instanceof Double) {
            return true;
         } else {
            try {
               this.converter.fromString(value == null ? null : value.toString());
               return true;
            } catch (Exception var4) {
               return false;
            }
         }
      }

      public Double convertValue(Object value) {
         if (value instanceof Double) {
            return (Double)value;
         } else {
            try {
               return (Double)this.converter.fromString(value == null ? null : value.toString());
            } catch (Exception var3) {
               return null;
            }
         }
      }

      public String toString(Double item) {
         return this.converter.toString(item);
      }

      public String toString(Double item, String format) {
         return ((StringConverterWithFormat)this.converter).toStringFormat(item, format);
      }
   }

   public static class IntegerType extends SpreadsheetCellType<Integer> {
      public IntegerType() {
         this(new IntegerStringConverter() {
            public String toString(Integer item) {
               return item != null && !Double.isNaN((double)item.intValue()) ? super.toString(item) : "";
            }

            public Integer fromString(String str) {
               if (str != null && !str.isEmpty() && !"NaN".equals(str)) {
                  try {
                     Double temp = Double.parseDouble(str);
                     return temp.intValue();
                  } catch (Exception var3) {
                     return super.fromString(str);
                  }
               } else {
                  return null;
               }
            }
         });
      }

      public IntegerType(IntegerStringConverter converter) {
         super(converter);
      }

      @Override
      public String toString() {
         return "Integer";
      }

      public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan, Integer value) {
         SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
         cell.setItem(value);
         return cell;
      }

      @Override
      public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
         return new SpreadsheetCellEditor.IntegerEditor(view);
      }

      @Override
      public boolean match(Object value, Object... options) {
         if (value instanceof Integer) {
            return true;
         } else {
            try {
               this.converter.fromString(value == null ? null : value.toString());
               return true;
            } catch (Exception var4) {
               return false;
            }
         }
      }

      public Integer convertValue(Object value) {
         if (value instanceof Integer) {
            return (Integer)value;
         } else {
            try {
               return (Integer)this.converter.fromString(value == null ? null : value.toString());
            } catch (Exception var3) {
               return null;
            }
         }
      }

      public String toString(Integer item) {
         return this.converter.toString(item);
      }
   }

   public static class ListType extends SpreadsheetCellType<String> {
      protected final List<String> items;

      public ListType(final List<String> items) {
         super(new DefaultStringConverter() {
            public String fromString(String str) {
               return str != null && items.contains(str) ? str : null;
            }
         });
         this.items = items;
      }

      @Override
      public String toString() {
         return "list";
      }

      public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan, String value) {
         SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
         if (this.items != null && this.items.size() > 0) {
            if (value != null && this.items.contains(value)) {
               cell.setItem(value);
            } else {
               cell.setItem(this.items.get(0));
            }
         }

         return cell;
      }

      @Override
      public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
         return new SpreadsheetCellEditor.ListEditor(view, this.items);
      }

      @Override
      public boolean match(Object value, Object... options) {
         return value instanceof String && this.items.contains(value.toString()) ? true : this.items.contains(value == null ? null : value.toString());
      }

      public String convertValue(Object value) {
         return (String)this.converter.fromString(value == null ? null : value.toString());
      }

      public String toString(String item) {
         return this.converter.toString(item);
      }
   }

   public static class ObjectType extends SpreadsheetCellType<Object> {
      public ObjectType() {
         this(new StringConverterWithFormat<Object>() {
            public Object fromString(String arg0) {
               return arg0;
            }

            public String toString(Object arg0) {
               return arg0 == null ? "" : arg0.toString();
            }
         });
      }

      public ObjectType(StringConverterWithFormat<Object> converter) {
         super(converter);
      }

      @Override
      public String toString() {
         return "object";
      }

      @Override
      public boolean match(Object value, Object... options) {
         return true;
      }

      public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan, Object value) {
         SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
         cell.setItem(value);
         return cell;
      }

      @Override
      public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
         return new SpreadsheetCellEditor.ObjectEditor(view);
      }

      @Override
      public Object convertValue(Object value) {
         return value;
      }

      @Override
      public String toString(Object item) {
         return this.converter.toString(item);
      }
   }

   public static class StringType extends SpreadsheetCellType<String> {
      public StringType() {
         this(new DefaultStringConverter());
      }

      public StringType(StringConverter<String> converter) {
         super(converter);
      }

      @Override
      public String toString() {
         return "string";
      }

      @Override
      public boolean match(Object value, Object... options) {
         return true;
      }

      public SpreadsheetCell createCell(int row, int column, int rowSpan, int columnSpan, String value) {
         SpreadsheetCell cell = new SpreadsheetCellBase(row, column, rowSpan, columnSpan, this);
         cell.setItem(value);
         return cell;
      }

      @Override
      public SpreadsheetCellEditor createEditor(SpreadsheetView view) {
         return new SpreadsheetCellEditor.StringEditor(view);
      }

      public String convertValue(Object value) {
         String convertedValue = (String)this.converter.fromString(value == null ? null : value.toString());
         return convertedValue != null && !convertedValue.equals("") ? convertedValue : null;
      }

      public String toString(String item) {
         return this.converter.toString(item);
      }
   }
}
