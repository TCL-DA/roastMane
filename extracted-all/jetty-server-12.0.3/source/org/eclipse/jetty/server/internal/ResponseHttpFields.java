package org.eclipse.jetty.server.internal;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHttpFields implements Mutable {
   private static final Logger LOG = LoggerFactory.getLogger(ResponseHttpFields.class);
   private final Mutable _fields = HttpFields.build();
   private final AtomicBoolean _committed = new AtomicBoolean();

   public Mutable getMutableHttpFields() {
      return this._fields;
   }

   public boolean commit() {
      boolean committed = this._committed.compareAndSet(false, true);
      if (committed && LOG.isDebugEnabled()) {
         LOG.debug("{} committed", this);
      }

      return committed;
   }

   public boolean isCommitted() {
      return this._committed.get();
   }

   public void recycle() {
      this._committed.set(false);
      this._fields.clear();
   }

   public HttpField getField(String name) {
      return this._fields.getField(name);
   }

   public HttpField getField(HttpHeader header) {
      return this._fields.getField(header);
   }

   public HttpField getField(int index) {
      return this._fields.getField(index);
   }

   public int size() {
      return this._fields.size();
   }

   public Stream<HttpField> stream() {
      return this._fields.stream();
   }

   public Mutable add(HttpField field) {
      if (field != null && !this._committed.get()) {
         this._fields.add(field);
      }

      return this;
   }

   public HttpFields asImmutable() {
      return (HttpFields)(this._committed.get() ? this : this._fields.asImmutable());
   }

   public Mutable clear() {
      if (!this._committed.get()) {
         ListIterator<HttpField> iterator = this._fields.listIterator(this._fields.size());

         while (iterator.hasPrevious()) {
            HttpField field = iterator.previous();
            if (field instanceof ResponseHttpFields.Persistent persistent) {
               iterator.set(persistent.getOriginal());
            } else {
               iterator.remove();
            }
         }
      }

      return this;
   }

   public void ensureField(HttpField field) {
      if (!this._committed.get()) {
         this._fields.ensureField(field);
      }
   }

   public Iterator<HttpField> iterator() {
      return new Iterator<HttpField>() {
         private final Iterator<HttpField> i = ResponseHttpFields.this._fields.iterator();
         private HttpField _current;

         @Override
         public boolean hasNext() {
            return this.i.hasNext();
         }

         public HttpField next() {
            this._current = this.i.next();
            return this._current;
         }

         @Override
         public void remove() {
            if (ResponseHttpFields.this._committed.get()) {
               throw new UnsupportedOperationException("Read Only");
            } else if (ResponseHttpFields.Persistent.isPersistent(this._current)) {
               throw new UnsupportedOperationException("Persistent field");
            } else if (this._current == null) {
               throw new IllegalStateException("No current field");
            } else {
               this.i.remove();
               this._current = null;
            }
         }
      };
   }

   public ListIterator<HttpField> listIterator(int index) {
      final ListIterator<HttpField> i = this._fields.listIterator(index);
      return new ListIterator<HttpField>() {
         private HttpField _current;

         @Override
         public boolean hasNext() {
            return i.hasNext();
         }

         public HttpField next() {
            this._current = i.next();
            return this._current;
         }

         @Override
         public boolean hasPrevious() {
            return i.hasPrevious();
         }

         public HttpField previous() {
            this._current = i.previous();
            return this._current;
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
            if (ResponseHttpFields.this._committed.get()) {
               throw new UnsupportedOperationException("Read Only");
            } else if (ResponseHttpFields.Persistent.isPersistent(this._current)) {
               throw new UnsupportedOperationException("Persistent field");
            } else if (this._current == null) {
               throw new IllegalStateException("No current field");
            } else {
               i.remove();
               this._current = null;
            }
         }

         public void set(HttpField field) {
            if (ResponseHttpFields.this._committed.get()) {
               throw new UnsupportedOperationException("Read Only");
            } else {
               if (this._current instanceof ResponseHttpFields.Persistent persistent) {
                  if (field == null || !field.isSameName(this._current)) {
                     throw new UnsupportedOperationException("Persistent field");
                  }

                  field = (HttpField)(field instanceof PreEncodedHttpField
                     ? new ResponseHttpFields.PersistentPreEncodedHttpField(this._current.getHeader(), field.getValue(), persistent.getOriginal())
                     : new ResponseHttpFields.PersistentHttpField(field, persistent.getOriginal()));
               }

               if (this._current == null) {
                  throw new IllegalStateException("No current field");
               } else {
                  if (field == null) {
                     i.remove();
                  } else {
                     i.set(field);
                  }

                  this._current = field;
               }
            }
         }

         public void add(HttpField field) {
            if (ResponseHttpFields.this._committed.get()) {
               throw new UnsupportedOperationException("Read Only");
            } else {
               if (field != null) {
                  i.add(field);
               }
            }
         }
      };
   }

   @Override
   public String toString() {
      return this._fields.toString();
   }

   public interface Persistent {
      static boolean isPersistent(HttpField field) {
         return field instanceof ResponseHttpFields.Persistent;
      }

      HttpField getOriginal();
   }

   public static class PersistentHttpField extends HttpField implements ResponseHttpFields.Persistent {
      private final HttpField _field;
      private final HttpField _original;

      public PersistentHttpField(HttpField field) {
         this(field, null);
      }

      PersistentHttpField(HttpField field, HttpField original) {
         super(field.getHeader(), field.getName(), field.getValue());
         this._field = field;
         this._original = (HttpField)(original == null ? this : original);
      }

      public int getIntValue() {
         return this._field.getIntValue();
      }

      public long getLongValue() {
         return (long)this._field.getIntValue();
      }

      @Override
      public HttpField getOriginal() {
         return this._original;
      }
   }

   public static class PersistentPreEncodedHttpField extends PreEncodedHttpField implements ResponseHttpFields.Persistent {
      private final HttpField _original;

      public PersistentPreEncodedHttpField(HttpHeader header, String value) {
         this(header, value, null);
      }

      PersistentPreEncodedHttpField(HttpHeader header, String value, HttpField original) {
         super(header, value);
         this._original = (HttpField)(original == null ? this : original);
      }

      @Override
      public HttpField getOriginal() {
         return this._original;
      }
   }
}
