package org.glassfish.jersey.internal.guava;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Joiner {
   private final String separator;

   private Joiner(String separator) {
      this.separator = Preconditions.checkNotNull(separator);
   }

   public static Joiner on() {
      return new Joiner(", ");
   }

   private <A extends Appendable> A appendTo(A appendable, Iterator<?> parts) throws IOException {
      Preconditions.checkNotNull(appendable);
      if (parts.hasNext()) {
         appendable.append(this.toString(parts.next()));

         while (parts.hasNext()) {
            appendable.append(this.separator);
            appendable.append(this.toString(parts.next()));
         }
      }

      return appendable;
   }

   private StringBuilder appendTo(StringBuilder builder, Iterator<?> parts) {
      try {
         this.appendTo(builder, parts);
         return builder;
      } catch (IOException var4) {
         throw new AssertionError(var4);
      }
   }

   public Joiner.MapJoiner withKeyValueSeparator() {
      return new Joiner.MapJoiner(this, "=");
   }

   private CharSequence toString(Object part) {
      Preconditions.checkNotNull(part);
      return (CharSequence)(part instanceof CharSequence ? (CharSequence)part : part.toString());
   }

   public static final class MapJoiner {
      private final Joiner joiner;
      private final String keyValueSeparator;

      private MapJoiner(Joiner joiner, String keyValueSeparator) {
         this.joiner = joiner;
         this.keyValueSeparator = Preconditions.checkNotNull(keyValueSeparator);
      }

      public StringBuilder appendTo(StringBuilder builder, Map<?, ?> map) {
         return this.appendTo(builder, map.entrySet());
      }

      public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Entry<?, ?>> parts) throws IOException {
         Preconditions.checkNotNull(appendable);
         if (parts.hasNext()) {
            Entry<?, ?> entry = (Entry<?, ?>)parts.next();
            appendable.append(this.joiner.toString(entry.getKey()));
            appendable.append(this.keyValueSeparator);
            appendable.append(this.joiner.toString(entry.getValue()));

            while (parts.hasNext()) {
               appendable.append(this.joiner.separator);
               Entry<?, ?> e = (Entry<?, ?>)parts.next();
               appendable.append(this.joiner.toString(e.getKey()));
               appendable.append(this.keyValueSeparator);
               appendable.append(this.joiner.toString(e.getValue()));
            }
         }

         return appendable;
      }

      public StringBuilder appendTo(StringBuilder builder, Iterable<? extends Entry<?, ?>> entries) {
         return this.appendTo(builder, entries.iterator());
      }

      public StringBuilder appendTo(StringBuilder builder, Iterator<? extends Entry<?, ?>> entries) {
         try {
            this.appendTo(builder, entries);
            return builder;
         } catch (IOException var4) {
            throw new AssertionError(var4);
         }
      }
   }
}
