package org.eclipse.jetty.http;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface HttpFields extends Iterable<HttpField>, Supplier<HttpFields> {
   HttpField EXPIRES_01JAN1970 = new PreEncodedHttpField(HttpHeader.EXPIRES, DateGenerator.__01Jan1970);
   HttpField CONNECTION_CLOSE = new PreEncodedHttpField(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.asString());
   HttpField CONNECTION_KEEPALIVE = new PreEncodedHttpField(HttpHeader.CONNECTION, HttpHeaderValue.KEEP_ALIVE.asString());
   HttpField CONTENT_LENGTH_0 = new PreEncodedHttpField(HttpHeader.CONTENT_LENGTH, 0L);
   HttpFields EMPTY = new EmptyHttpFields();

   static HttpFields.Mutable build() {
      return new org.eclipse.jetty.http.MutableHttpFields();
   }

   static HttpFields.Mutable build(int capacity) {
      return new org.eclipse.jetty.http.MutableHttpFields(capacity);
   }

   static HttpFields.Mutable build(HttpFields fields) {
      return new org.eclipse.jetty.http.MutableHttpFields(fields);
   }

   static HttpFields.Mutable build(HttpFields fields, HttpField replaceField) {
      return new org.eclipse.jetty.http.MutableHttpFields(fields, replaceField);
   }

   static HttpFields.Mutable build(HttpFields fields, EnumSet<HttpHeader> removeFields) {
      return new org.eclipse.jetty.http.MutableHttpFields(fields, removeFields);
   }

   static HttpFields from(HttpField... fields) {
      return new org.eclipse.jetty.http.ImmutableHttpFields(fields);
   }

   default HttpFields get() {
      return this;
   }

   @Override
   default Iterator<HttpField> iterator() {
      return this.listIterator();
   }

   default ListIterator<HttpField> listIterator() {
      return this.listIterator(0);
   }

   ListIterator<HttpField> listIterator(int var1);

   default HttpFields asImmutable() {
      return build(this).asImmutable();
   }

   default String asString() {
      StringBuilder buffer = new StringBuilder();

      for (HttpField field : this) {
         if (field != null) {
            String tmp = field.getName();
            if (tmp != null) {
               buffer.append(tmp);
            }

            buffer.append(": ");
            tmp = field.getValue();
            if (tmp != null) {
               buffer.append(tmp);
            }

            buffer.append("\r\n");
         }
      }

      buffer.append("\r\n");
      return buffer.toString();
   }

   default boolean contains(HttpField field) {
      for (HttpField f : this) {
         if (f.isSameName(field) && (f.equals(field) || f.contains(field.getValue()))) {
            return true;
         }
      }

      return false;
   }

   default boolean contains(HttpHeader header, String value) {
      for (HttpField f : this) {
         if (f.getHeader() == header && f.contains(value)) {
            return true;
         }
      }

      return false;
   }

   default boolean containsLast(HttpHeader header, String value) {
      ListIterator<HttpField> i = this.listIterator(this.size());

      while (i.hasPrevious()) {
         HttpField f = i.previous();
         if (f.getHeader() == header) {
            return f.containsLast(value);
         }
      }

      return false;
   }

   default boolean contains(String name, String value) {
      for (HttpField f : this) {
         if (f.is(name) && f.contains(value)) {
            return true;
         }
      }

      return false;
   }

   default boolean contains(HttpHeader header) {
      for (HttpField f : this) {
         if (f.getHeader() == header) {
            return true;
         }
      }

      return false;
   }

   default boolean contains(EnumSet<HttpHeader> headers) {
      for (HttpField f : this) {
         if (headers.contains(f.getHeader())) {
            return true;
         }
      }

      return false;
   }

   default boolean contains(String name) {
      for (HttpField f : this) {
         if (f.is(name)) {
            return true;
         }
      }

      return false;
   }

   default String get(HttpHeader header) {
      for (HttpField f : this) {
         if (f.getHeader() == header) {
            return f.getValue();
         }
      }

      return null;
   }

   default String getLast(HttpHeader header) {
      ListIterator<HttpField> i = this.listIterator(this.size());

      while (i.hasPrevious()) {
         HttpField f = i.previous();
         if (f.getHeader() == header) {
            return f.getValue();
         }
      }

      return null;
   }

   default String get(String name) {
      for (HttpField f : this) {
         if (f.is(name)) {
            return f.getValue();
         }
      }

      return null;
   }

   default List<String> getCSV(HttpHeader header, boolean keepQuotes) {
      QuotedCSV values = null;

      for (HttpField f : this) {
         if (f.getHeader() == header) {
            if (values == null) {
               values = new QuotedCSV(keepQuotes);
            }

            values.addValue(f.getValue());
         }
      }

      return values == null ? Collections.emptyList() : values.getValues();
   }

   default List<String> getCSV(String name, boolean keepQuotes) {
      QuotedCSV values = null;

      for (HttpField f : this) {
         if (f.is(name)) {
            if (values == null) {
               values = new QuotedCSV(keepQuotes);
            }

            values.addValue(f.getValue());
         }
      }

      return values == null ? Collections.emptyList() : values.getValues();
   }

   default long getDateField(String name) {
      return parseDateField(this.getField(name));
   }

   default long getDateField(HttpHeader header) {
      return parseDateField(this.getField(header));
   }

   private static long parseDateField(HttpField field) {
      if (field == null) {
         return -1L;
      } else {
         String val = HttpField.getValueParameters(field.getValue(), null);
         if (val == null) {
            return -1L;
         } else {
            long date = DateParser.parseDate(val);
            if (date == -1L) {
               throw new IllegalArgumentException("Cannot convert date: " + val);
            } else {
               return date;
            }
         }
      }
   }

   default HttpField getField(int index) {
      int i = 0;

      for (HttpField f : this) {
         if (i++ == index) {
            return f;
         }
      }

      return null;
   }

   default HttpField getField(HttpHeader header) {
      for (HttpField f : this) {
         if (f.getHeader() == header) {
            return f;
         }
      }

      return null;
   }

   default HttpField getField(String name) {
      for (HttpField f : this) {
         if (f.is(name)) {
            return f;
         }
      }

      return null;
   }

   @Deprecated
   default Enumeration<String> getFieldNames() {
      return Collections.enumeration(this.getFieldNamesCollection());
   }

   default Set<String> getFieldNamesCollection() {
      return this.stream().map(HttpField::getName).collect(Collectors.toSet());
   }

   default List<HttpField> getFields(HttpHeader header) {
      return this.getFields(header, (f, h) -> f.getHeader() == h);
   }

   default List<HttpField> getFields(String name) {
      return this.getFields(name, (f, n) -> f.is(name));
   }

   private <T> List<HttpField> getFields(T header, BiPredicate<HttpField, T> predicate) {
      return this.stream().filter(f -> predicate.test(f, header)).collect(Collectors.toList());
   }

   default long getLongField(String name) throws NumberFormatException {
      HttpField field = this.getField(name);
      return field == null ? -1L : field.getLongValue();
   }

   default long getLongField(HttpHeader header) throws NumberFormatException {
      HttpField field = this.getField(header);
      return field == null ? -1L : field.getLongValue();
   }

   default List<String> getQualityCSV(HttpHeader header) {
      return this.getQualityCSV(header, null);
   }

   default List<String> getQualityCSV(HttpHeader header, ToIntFunction<String> secondaryOrdering) {
      QuotedQualityCSV values = null;

      for (HttpField f : this) {
         if (f.getHeader() == header) {
            if (values == null) {
               values = new QuotedQualityCSV(secondaryOrdering);
            }

            values.addValue(f.getValue());
         }
      }

      return values == null ? Collections.emptyList() : values.getValues();
   }

   default List<String> getQualityCSV(String name) {
      QuotedQualityCSV values = null;

      for (HttpField f : this) {
         if (f.is(name)) {
            if (values == null) {
               values = new QuotedQualityCSV();
            }

            values.addValue(f.getValue());
         }
      }

      return values == null ? Collections.emptyList() : values.getValues();
   }

   default Enumeration<String> getValues(final String name) {
      final Iterator<HttpField> i = this.iterator();
      return new Enumeration<String>() {
         HttpField _field;

         @Override
         public boolean hasMoreElements() {
            if (this._field != null) {
               return true;
            } else {
               while (i.hasNext()) {
                  HttpField f = i.next();
                  if (f.is(name) && f.getValue() != null) {
                     this._field = f;
                     return true;
                  }
               }

               return false;
            }
         }

         public String nextElement() {
            if (this.hasMoreElements()) {
               String value = this._field.getValue();
               this._field = null;
               return value;
            } else {
               throw new NoSuchElementException();
            }
         }
      };
   }

   default List<String> getValuesList(HttpHeader header) {
      List<String> list = new ArrayList<>();

      for (HttpField f : this) {
         if (f.getHeader() == header) {
            list.add(f.getValue());
         }
      }

      return list;
   }

   default List<String> getValuesList(String name) {
      List<String> list = new ArrayList<>();

      for (HttpField f : this) {
         if (f.is(name)) {
            list.add(f.getValue());
         }
      }

      return list;
   }

   default boolean isEqualTo(HttpFields that) {
      if (this.size() != that.size()) {
         return false;
      } else {
         Iterator<HttpField> i = that.iterator();

         for (HttpField f : this) {
            if (!i.hasNext()) {
               return false;
            }

            if (!f.equals(i.next())) {
               return false;
            }
         }

         return !i.hasNext();
      }
   }

   default int size() {
      int size = 0;

      for (HttpField ignored : this) {
         size++;
      }

      return size;
   }

   default Stream<HttpField> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   @Deprecated(
      forRemoval = true
   )
   public static class ImmutableHttpFields extends org.eclipse.jetty.http.ImmutableHttpFields {
      protected ImmutableHttpFields(HttpField[] fields, int size) {
         super(fields, size);
      }
   }

   public interface Mutable extends HttpFields {
      default HttpFields.Mutable add(String name, String value) {
         if (value == null) {
            throw new IllegalArgumentException("null value");
         } else {
            return this.add(new HttpField(name, value));
         }
      }

      default HttpFields.Mutable add(String name, long value) {
         return this.add(new HttpField.LongValueHttpField(name, value));
      }

      default HttpFields.Mutable add(HttpHeader header, HttpHeaderValue value) {
         return this.add(header, value.toString());
      }

      default HttpFields.Mutable add(HttpHeader header, String value) {
         if (value == null) {
            throw new IllegalArgumentException("null value");
         } else {
            return this.add(new HttpField(header, value));
         }
      }

      default HttpFields.Mutable add(HttpHeader header, long value) {
         return this.add(new HttpField.LongValueHttpField(header, value));
      }

      default HttpFields.Mutable add(HttpField field) {
         ListIterator<HttpField> i = this.listIterator(this.size());
         i.add(field);
         return this;
      }

      default HttpFields.Mutable add(HttpFields fields) {
         for (HttpField field : fields) {
            this.add(field);
         }

         return this;
      }

      default HttpFields.Mutable addCSV(HttpHeader header, String... values) {
         QuotedCSV existing = null;

         for (HttpField f : this) {
            if (f.getHeader() == header) {
               if (existing == null) {
                  existing = new QuotedCSV(false);
               }

               existing.addValue(f.getValue());
            }
         }

         String value = formatCsvExcludingExisting(existing, values);
         if (value != null) {
            this.add(header, value);
         }

         return this;
      }

      default HttpFields.Mutable addCSV(String name, String... values) {
         QuotedCSV existing = null;

         for (HttpField f : this) {
            if (f.is(name)) {
               if (existing == null) {
                  existing = new QuotedCSV(false);
               }

               existing.addValue(f.getValue());
            }
         }

         String value = formatCsvExcludingExisting(existing, values);
         if (value != null) {
            this.add(name, value);
         }

         return this;
      }

      default HttpFields.Mutable addDateField(String name, long date) {
         this.add(name, DateGenerator.formatDate(date));
         return this;
      }

      default HttpFields.Mutable clear() {
         Iterator<HttpField> i = this.iterator();

         while (i.hasNext()) {
            i.next();
            i.remove();
         }

         return this;
      }

      default void ensureField(HttpField field) {
         HttpHeader header = field.getHeader();
         if (field.getValue().indexOf(44) < 0) {
            if (header != null) {
               this.computeField(header, (h, l) -> computeEnsure(field, l));
            } else {
               this.computeField(field.getName(), (h, l) -> computeEnsure(field, l));
            }
         } else if (header != null) {
            this.computeField(header, (h, l) -> computeEnsure(field, field.getValues(), l));
         } else {
            this.computeField(field.getName(), (h, l) -> computeEnsure(field, field.getValues(), l));
         }
      }

      default HttpFields.Mutable put(HttpField field) {
         boolean put = false;
         ListIterator<HttpField> i = this.listIterator();

         while (i.hasNext()) {
            HttpField f = i.next();
            if (f.isSameName(field)) {
               if (put) {
                  i.remove();
               } else {
                  i.set(field);
                  put = true;
               }
            }
         }

         if (!put) {
            this.add(field);
         }

         return this;
      }

      default HttpFields.Mutable put(String name, String value) {
         return value == null ? this.remove(name) : this.put(new HttpField(name, value));
      }

      default HttpFields.Mutable put(HttpHeader header, HttpHeaderValue value) {
         return value == null ? this.remove(header) : this.put(new HttpField(header, value.toString()));
      }

      default HttpFields.Mutable put(HttpHeader header, String value) {
         return value == null ? this.remove(header) : this.put(new HttpField(header, value));
      }

      default HttpFields.Mutable put(String name, List<String> list) {
         boolean first = true;

         for (String s : list) {
            HttpField field = new HttpField(name, s);
            if (first) {
               this.put(field);
            } else {
               this.add(field);
            }

            first = false;
         }

         return this;
      }

      default HttpFields.Mutable putDate(HttpHeader name, long date) {
         return this.put(name, DateGenerator.formatDate(date));
      }

      default HttpFields.Mutable putDate(String name, long date) {
         return this.put(name, DateGenerator.formatDate(date));
      }

      default HttpFields.Mutable put(HttpHeader header, long value) {
         return value == 0L && header == HttpHeader.CONTENT_LENGTH
            ? this.put(HttpFields.CONTENT_LENGTH_0)
            : this.put(new HttpField.LongValueHttpField(header, value));
      }

      default HttpFields.Mutable put(String name, long value) {
         return value == 0L && HttpHeader.CONTENT_LENGTH.is(name)
            ? this.put(HttpFields.CONTENT_LENGTH_0)
            : this.put(new HttpField.LongValueHttpField(name, value));
      }

      default HttpFields.Mutable computeField(HttpHeader header, BiFunction<HttpHeader, List<HttpField>, HttpField> computeFn) {
         return this.put(computeFn.apply(header, this.stream().filter(f -> f.getHeader() == header).collect(Collectors.toList())));
      }

      default HttpFields.Mutable computeField(String name, BiFunction<String, List<HttpField>, HttpField> computeFn) {
         return this.put(computeFn.apply(name, this.stream().filter(f -> f.is(name)).collect(Collectors.toList())));
      }

      default HttpFields.Mutable remove(HttpHeader header) {
         Iterator<HttpField> i = this.iterator();

         while (i.hasNext()) {
            HttpField f = i.next();
            if (f.getHeader() == header) {
               i.remove();
            }
         }

         return this;
      }

      default HttpFields.Mutable remove(EnumSet<HttpHeader> headers) {
         Iterator<HttpField> i = this.iterator();

         while (i.hasNext()) {
            HttpField f = i.next();
            HttpHeader h = f.getHeader();
            if (h != null && headers.contains(h)) {
               i.remove();
            }
         }

         return this;
      }

      default HttpFields.Mutable remove(String name) {
         ListIterator<HttpField> i = this.listIterator();

         while (i.hasNext()) {
            HttpField f = i.next();
            if (f.is(name)) {
               i.remove();
            }
         }

         return this;
      }

      private static String formatCsvExcludingExisting(QuotedCSV existing, String... values) {
         boolean add = true;
         if (existing != null && !existing.isEmpty()) {
            add = false;
            int i = values.length;

            while (i-- > 0) {
               String unquoted = QuotedCSV.unquote(values[i]);
               if (existing.getValues().contains(unquoted)) {
                  values[i] = null;
               } else {
                  add = true;
               }
            }
         }

         if (add) {
            StringBuilder value = new StringBuilder();

            for (String v : values) {
               if (v != null) {
                  if (!value.isEmpty()) {
                     value.append(", ");
                  }

                  value.append(v);
               }
            }

            if (!value.isEmpty()) {
               return value.toString();
            }
         }

         return null;
      }

      private static HttpField computeEnsure(HttpField ensure, List<HttpField> fields) {
         if (fields != null && !fields.isEmpty()) {
            String ensureValue = ensure.getValue();
            if (fields.size() == 1) {
               HttpField f = fields.get(0);
               return f.contains(ensureValue) ? f : new HttpField(ensure.getHeader(), ensure.getName(), f.getValue() + ", " + ensureValue);
            } else {
               StringBuilder v = new StringBuilder();

               for (HttpField f : fields) {
                  if (!v.isEmpty()) {
                     v.append(", ");
                  }

                  v.append(f.getValue());
                  if (ensureValue != null && f.contains(ensureValue)) {
                     ensureValue = null;
                  }
               }

               if (ensureValue != null) {
                  v.append(", ").append(ensureValue);
               }

               return new HttpField(ensure.getHeader(), ensure.getName(), v.toString());
            }
         } else {
            return ensure;
         }
      }

      private static HttpField computeEnsure(HttpField ensure, String[] values, List<HttpField> fields) {
         if (fields != null && !fields.isEmpty()) {
            if (fields.size() == 1) {
               HttpField f = fields.get(0);
               int ensured = values.length;

               for (int i = 0; i < values.length; i++) {
                  if (f.contains(values[i])) {
                     ensured--;
                     values[i] = null;
                  }
               }

               if (ensured == 0) {
                  return f;
               } else if (ensured == values.length) {
                  return new HttpField(ensure.getHeader(), ensure.getName(), f.getValue() + ", " + ensure.getValue());
               } else {
                  StringBuilder v = new StringBuilder(f.getValue());

                  for (String value : values) {
                     if (value != null) {
                        v.append(", ").append(value);
                     }
                  }

                  return new HttpField(ensure.getHeader(), ensure.getName(), v.toString());
               }
            } else {
               StringBuilder v = new StringBuilder();
               int ensured = values.length;

               for (HttpField f : fields) {
                  if (!v.isEmpty()) {
                     v.append(", ");
                  }

                  v.append(f.getValue());

                  for (int ix = 0; ix < values.length; ix++) {
                     if (values[ix] != null && f.contains(values[ix])) {
                        ensured--;
                        values[ix] = null;
                     }
                  }
               }

               if (ensured == values.length) {
                  v.append(", ").append(ensure.getValue());
               } else if (ensured > 0) {
                  for (String valuex : values) {
                     if (valuex != null) {
                        v.append(", ").append(valuex);
                     }
                  }
               }

               return new HttpField(ensure.getHeader(), ensure.getName(), v.toString());
            }
         } else {
            return ensure;
         }
      }

      public static class Wrapper implements HttpFields.Mutable {
         private final HttpFields.Mutable _fields;

         public Wrapper(HttpFields.Mutable fields) {
            this._fields = fields;
         }

         public HttpField onAddField(HttpField field) {
            return field;
         }

         public boolean onRemoveField(HttpField field) {
            return true;
         }

         public HttpField onReplaceField(HttpField oldField, HttpField newField) {
            return newField;
         }

         @Override
         public int size() {
            return this._fields.size();
         }

         @Override
         public Stream<HttpField> stream() {
            return this._fields.stream();
         }

         @Override
         public HttpFields.Mutable add(HttpField field) {
            if (field != null) {
               field = this.onAddField(field);
               if (field != null) {
                  this._fields.add(field);
               }
            }

            return this;
         }

         @Override
         public HttpFields.Mutable put(HttpField field) {
            int put = -1;
            ListIterator<HttpField> i = this._fields.listIterator();

            while (i.hasNext()) {
               HttpField f = i.next();
               if (f.isSameName(field)) {
                  if (put < 0) {
                     put = i.previousIndex();
                  } else if (this.onRemoveField(f)) {
                     i.remove();
                  }
               }
            }

            if (put < 0) {
               field = this.onAddField(field);
               if (field != null) {
                  this.add(field);
               }
            } else {
               i = this._fields.listIterator(put);
               HttpField old = i.next();
               field = this.onReplaceField(old, field);
               if (field != null) {
                  i.set(field);
               }
            }

            return this;
         }

         @Override
         public HttpFields.Mutable clear() {
            this._fields.clear();
            return this;
         }

         @Override
         public ListIterator<HttpField> listIterator(int index) {
            final ListIterator<HttpField> i = this._fields.listIterator(index);
            return new ListIterator<HttpField>() {
               HttpField last;

               @Override
               public boolean hasNext() {
                  return i.hasNext();
               }

               public HttpField next() {
                  return this.last = i.next();
               }

               @Override
               public boolean hasPrevious() {
                  return i.hasPrevious();
               }

               public HttpField previous() {
                  return this.last = i.previous();
               }

               @Override
               public int nextIndex() {
                  return i.nextIndex();
               }

               @Override
               public int previousIndex() {
                  return i.previousIndex();
               }

               @Override
               public void remove() {
                  if (this.last != null && Wrapper.this.onRemoveField(this.last)) {
                     this.last = null;
                     i.remove();
                  }
               }

               public void set(HttpField field) {
                  if (field == null) {
                     if (this.last != null && Wrapper.this.onRemoveField(this.last)) {
                        this.last = null;
                        i.remove();
                     }
                  } else if (this.last != null) {
                     field = Wrapper.this.onReplaceField(this.last, field);
                     if (field != null) {
                        this.last = null;
                        i.set(field);
                     }
                  }
               }

               public void add(HttpField field) {
                  if (field != null) {
                     field = Wrapper.this.onAddField(field);
                     if (field != null) {
                        this.last = null;
                        i.add(field);
                     }
                  }
               }
            };
         }
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static class MutableHttpFields extends org.eclipse.jetty.http.MutableHttpFields {
   }
}
