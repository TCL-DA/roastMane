package org.eclipse.jetty.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

class MutableHttpFields implements HttpFields.Mutable {
   private static final int INITIAL_SIZE = 16;
   private static final int SIZE_INCREMENT = 4;
   private HttpField[] _fields;
   private boolean _immutable;
   private int _size;

   protected MutableHttpFields() {
      this(16);
   }

   protected MutableHttpFields(int capacity) {
      this._fields = new HttpField[capacity];
   }

   protected MutableHttpFields(HttpFields fields) {
      if (fields instanceof HttpFields.ImmutableHttpFields immutable) {
         this._immutable = true;
         this._fields = immutable._fields;
         this._size = immutable._size;
      } else if (fields != null) {
         this._fields = new HttpField[fields.size() + 4];
         this.add(fields);
      } else {
         this._fields = new HttpField[16];
      }
   }

   protected MutableHttpFields(HttpFields fields, HttpField replaceField) {
      this._fields = new HttpField[fields.size() + 4];
      this._size = 0;
      boolean put = false;

      for (HttpField f : fields) {
         if (replaceField.isSameName(f)) {
            if (!put) {
               this._fields[this._size++] = replaceField;
            }

            put = true;
         } else {
            this._fields[this._size++] = f;
         }
      }

      if (!put) {
         this._fields[this._size++] = replaceField;
      }
   }

   protected MutableHttpFields(HttpFields fields, EnumSet<HttpHeader> removeFields) {
      this._fields = new HttpField[fields.size() + 4];
      this._size = 0;

      for (HttpField f : fields) {
         if (f.getHeader() == null || !removeFields.contains(f.getHeader())) {
            this._fields[this._size++] = f;
         }
      }
   }

   @Override
   public HttpFields.Mutable add(HttpField field) {
      if (field != null) {
         if (this._immutable || this._size == this._fields.length) {
            this._immutable = false;
            this._fields = Arrays.copyOf(this._fields, this._size + 4);
         }

         this._fields[this._size++] = field;
      }

      return this;
   }

   @Override
   public HttpFields.Mutable add(HttpFields fields) {
      if (fields.size() == 0) {
         return this;
      } else {
         if (this._immutable || this._size + fields.size() >= this._fields.length) {
            this._immutable = false;
            this._fields = Arrays.copyOf(this._fields, this._size + fields.size() + 4);
         }

         if (fields instanceof org.eclipse.jetty.http.ImmutableHttpFields immutable) {
            System.arraycopy(immutable._fields, 0, this._fields, this._size, immutable._size);
            this._size = this._size + immutable._size;
         } else if (fields instanceof org.eclipse.jetty.http.MutableHttpFields mutable) {
            System.arraycopy(mutable._fields, 0, this._fields, this._size, mutable._size);
            this._size = this._size + mutable._size;
         } else {
            for (HttpField f : fields) {
               this._fields[this._size++] = f;
            }
         }

         return this;
      }
   }

   @Override
   public HttpFields asImmutable() {
      this._immutable = true;
      return new HttpFields.ImmutableHttpFields(this._fields, this._size);
   }

   private void copyImmutable() {
      if (this._immutable) {
         this._immutable = false;
         this._fields = Arrays.copyOf(this._fields, this._fields.length);
      }
   }

   @Override
   public HttpFields.Mutable clear() {
      if (this._immutable) {
         this._fields = new HttpField[this._fields.length];
         this._immutable = false;
      }

      this._size = 0;
      return this;
   }

   @Override
   public int hashCode() {
      int hash = 2099;
      int i = this._size;

      while (i-- > 0) {
         HttpField field = this._fields[i];
         if (field != null) {
            hash ^= field.hashCode();
         }
      }

      return hash;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof org.eclipse.jetty.http.MutableHttpFields) ? false : this.isEqualTo((HttpFields)o);
      }
   }

   @Override
   public HttpField getField(int index) {
      if (index < this._size && index >= 0) {
         return this._fields[index];
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public HttpField getField(HttpHeader header) {
      for (int i = 0; i < this._size; i++) {
         HttpField f = this._fields[i];
         if (f != null && f.getHeader() == header) {
            return f;
         }
      }

      return null;
   }

   @Override
   public HttpField getField(String name) {
      for (int i = 0; i < this._size; i++) {
         HttpField f = this._fields[i];
         if (f != null && f.is(name)) {
            return f;
         }
      }

      return null;
   }

   @Override
   public Iterator<HttpField> iterator() {
      return new Iterator<HttpField>() {
         int _index = 0;

         @Override
         public boolean hasNext() {
            return this._index < MutableHttpFields.this._size;
         }

         public HttpField next() {
            return MutableHttpFields.this._fields[this._index++];
         }

         @Override
         public void remove() {
            if (MutableHttpFields.this._size == 0) {
               throw new IllegalStateException();
            } else {
               MutableHttpFields.this.remove(--this._index);
            }
         }
      };
   }

   @Override
   public ListIterator<HttpField> listIterator() {
      return this.listIterator(0);
   }

   @Override
   public ListIterator<HttpField> listIterator(int index) {
      this.copyImmutable();
      return new org.eclipse.jetty.http.MutableHttpFields.Listerator(index);
   }

   @Override
   public HttpFields.Mutable put(HttpField field) {
      this.copyImmutable();
      boolean put = false;

      for (int i = 0; i < this._size; i++) {
         HttpField f = this._fields[i];
         if (f.isSameName(field)) {
            if (put) {
               System.arraycopy(this._fields, i + 1, this._fields, i, this._size-- - i-- - 1);
            } else {
               this._fields[i] = field;
               put = true;
            }
         }
      }

      if (!put) {
         this.add(field);
      }

      return this;
   }

   @Override
   public HttpFields.Mutable put(String name, String value) {
      return value == null ? this.remove(name) : this.put(new HttpField(name, value));
   }

   @Override
   public HttpFields.Mutable put(HttpHeader header, HttpHeaderValue value) {
      return this.put(header, value.toString());
   }

   @Override
   public HttpFields.Mutable put(HttpHeader header, String value) {
      return value == null ? this.remove(header) : this.put(new HttpField(header, value));
   }

   @Override
   public HttpFields.Mutable put(String name, List<String> list) {
      Objects.requireNonNull(name);
      Objects.requireNonNull(list);
      this.remove(name);

      for (String v : list) {
         if (v != null) {
            this.add(name, v);
         }
      }

      return this;
   }

   @Override
   public HttpFields.Mutable computeField(HttpHeader header, BiFunction<HttpHeader, List<HttpField>, HttpField> computeFn) {
      return this.computeField(header, computeFn, (f, h) -> f.getHeader() == h);
   }

   @Override
   public HttpFields.Mutable computeField(String name, BiFunction<String, List<HttpField>, HttpField> computeFn) {
      return this.computeField(name, computeFn, HttpField::is);
   }

   public <T> HttpFields.Mutable computeField(T header, BiFunction<T, List<HttpField>, HttpField> computeFn, BiPredicate<HttpField, T> matcher) {
      this.copyImmutable();
      int first = -1;

      for (int i = 0; i < this._size; i++) {
         HttpField f = this._fields[i];
         if (matcher.test(f, header)) {
            first = i;
            break;
         }
      }

      if (first < 0) {
         HttpField newField = computeFn.apply(header, null);
         if (newField != null) {
            this.add(newField);
         }

         return this;
      } else {
         List<HttpField> found = null;

         for (int ix = first + 1; ix < this._size; ix++) {
            HttpField f = this._fields[ix];
            if (matcher.test(f, header)) {
               if (found == null) {
                  found = new ArrayList<>();
                  found.add(this._fields[first]);
               }

               found.add(f);
               this.remove(ix--);
            }
         }

         if (found == null) {
            found = Collections.singletonList(this._fields[first]);
         } else {
            found = Collections.unmodifiableList(found);
         }

         HttpField newField = computeFn.apply(header, found);
         if (newField == null) {
            this.remove(first);
         } else {
            this._fields[first] = newField;
         }

         return this;
      }
   }

   @Override
   public HttpFields.Mutable remove(HttpHeader name) {
      for (int i = 0; i < this._size; i++) {
         HttpField f = this._fields[i];
         if (f.getHeader() == name) {
            this.remove(i--);
         }
      }

      return this;
   }

   @Override
   public HttpFields.Mutable remove(EnumSet<HttpHeader> headers) {
      for (int i = 0; i < this._size; i++) {
         HttpField f = this._fields[i];
         if (headers.contains(f.getHeader())) {
            this.remove(i--);
         }
      }

      return this;
   }

   @Override
   public HttpFields.Mutable remove(String name) {
      for (int i = 0; i < this._size; i++) {
         HttpField f = this._fields[i];
         if (f.is(name)) {
            this.remove(i--);
         }
      }

      return this;
   }

   private void remove(int i) {
      this._size--;
      if (this._immutable) {
         this._immutable = false;
         HttpField[] fields = this._fields;
         this._fields = new HttpField[fields.length];
         System.arraycopy(fields, 0, this._fields, 0, i);
         System.arraycopy(fields, i + 1, this._fields, i, this._size - i);
      } else {
         System.arraycopy(this._fields, i + 1, this._fields, i, this._size - i);
      }

      this._fields[this._size] = null;
   }

   @Override
   public int size() {
      return this._size;
   }

   @Override
   public Stream<HttpField> stream() {
      return Arrays.stream(this._fields, 0, this._size);
   }

   @Override
   public String toString() {
      return this.asString();
   }

   private class Listerator implements ListIterator<HttpField> {
      private int _index;
      private int _last = -1;

      Listerator(int index) {
         if (index >= 0 && index <= MutableHttpFields.this._size) {
            this._index = index;
         } else {
            throw new NoSuchElementException(Integer.toString(index));
         }
      }

      public void add(HttpField field) {
         if (field != null) {
            int last = MutableHttpFields.this._size++;
            if (MutableHttpFields.this._fields.length < MutableHttpFields.this._size) {
               MutableHttpFields.this._fields = Arrays.copyOf(MutableHttpFields.this._fields, MutableHttpFields.this._fields.length + 4);
            }

            System.arraycopy(MutableHttpFields.this._fields, this._index, MutableHttpFields.this._fields, this._index + 1, last - this._index);
            MutableHttpFields.this._fields[this._index++] = field;
            this._last = -1;
         }
      }

      @Override
      public boolean hasNext() {
         return this._index < MutableHttpFields.this._size;
      }

      @Override
      public boolean hasPrevious() {
         return this._index > 0;
      }

      public HttpField next() {
         if (this._index >= MutableHttpFields.this._size) {
            throw new NoSuchElementException(Integer.toString(this._index));
         } else {
            this._last = this._index++;
            return MutableHttpFields.this._fields[this._last];
         }
      }

      @Override
      public int nextIndex() {
         return this._index + 1;
      }

      public HttpField previous() {
         if (this._index <= 0) {
            throw new NoSuchElementException(Integer.toString(this._index - 1));
         } else {
            this._last = --this._index;
            return MutableHttpFields.this._fields[this._last];
         }
      }

      @Override
      public int previousIndex() {
         return this._index - 1;
      }

      @Override
      public void remove() {
         if (this._last < 0) {
            throw new IllegalStateException();
         } else {
            MutableHttpFields.this.remove(this._last);
            this._index = this._last;
            this._last = -1;
         }
      }

      public void set(HttpField field) {
         if (this._last < 0) {
            throw new IllegalStateException();
         } else {
            if (field == null) {
               this.remove();
            } else {
               MutableHttpFields.this._fields[this._last] = field;
            }
         }
      }
   }
}
