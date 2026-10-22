package org.eclipse.jetty.http;

import java.util.Arrays;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

class ImmutableHttpFields implements HttpFields {
   final HttpField[] _fields;
   final int _size;

   protected ImmutableHttpFields(HttpField[] fields) {
      this(fields, fields.length);
   }

   protected ImmutableHttpFields(HttpField[] fields, int size) {
      this._fields = fields;
      this._size = size;
   }

   @Override
   public HttpFields asImmutable() {
      return this;
   }

   @Override
   public int hashCode() {
      int hash = 1993;
      int i = this._size;

      while (i-- > 0) {
         hash ^= this._fields[i].hashCode();
      }

      return hash;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof org.eclipse.jetty.http.ImmutableHttpFields) ? false : this.isEqualTo((HttpFields)o);
      }
   }

   @Override
   public String get(String name) {
      for (HttpField f : this._fields) {
         if (f != null && f.is(name)) {
            return f.getValue();
         }
      }

      return null;
   }

   @Override
   public String get(HttpHeader header) {
      for (HttpField f : this._fields) {
         if (f != null && f.getHeader() == header) {
            return f.getValue();
         }
      }

      return null;
   }

   @Override
   public HttpField getField(HttpHeader header) {
      for (HttpField f : this._fields) {
         if (f != null && f.getHeader() == header) {
            return f;
         }
      }

      return null;
   }

   @Override
   public HttpField getField(String name) {
      for (HttpField f : this._fields) {
         if (f != null && f.is(name)) {
            return f;
         }
      }

      return null;
   }

   @Override
   public HttpField getField(int index) {
      if (index >= this._size) {
         throw new NoSuchElementException();
      } else {
         return this._fields[index];
      }
   }

   @Override
   public ListIterator<HttpField> listIterator(int index) {
      return new org.eclipse.jetty.http.ImmutableHttpFields.Listerator(index);
   }

   @Override
   public int size() {
      return this._size;
   }

   @Override
   public Stream<HttpField> stream() {
      return Arrays.stream(this._fields).filter(Objects::nonNull);
   }

   @Override
   public String toString() {
      return this.asString();
   }

   private class Listerator implements ListIterator<HttpField> {
      private int _index;
      private int _last = -1;

      Listerator(int index) {
         if (index >= 0 && index <= ImmutableHttpFields.this._size) {
            this._index = index;
         } else {
            throw new NoSuchElementException(Integer.toString(index));
         }
      }

      public void add(HttpField field) {
         throw new UnsupportedOperationException();
      }

      @Override
      public boolean hasNext() {
         return this._index < ImmutableHttpFields.this._size;
      }

      @Override
      public boolean hasPrevious() {
         return this._index > 0;
      }

      public HttpField next() {
         if (this._index >= ImmutableHttpFields.this._size) {
            throw new NoSuchElementException(Integer.toString(this._index));
         } else {
            this._last = this._index++;
            return ImmutableHttpFields.this._fields[this._last];
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
            return ImmutableHttpFields.this._fields[this._last];
         }
      }

      @Override
      public int previousIndex() {
         return this._index - 1;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      public void set(HttpField field) {
         throw new UnsupportedOperationException();
      }
   }
}
