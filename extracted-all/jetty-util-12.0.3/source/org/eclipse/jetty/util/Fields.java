package org.eclipse.jetty.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fields implements Iterable<Fields.Field> {
   public static final Fields EMPTY = new Fields(Collections.emptyMap());
   private final Map<String, Fields.Field> fields;

   public Fields() {
      this(false);
   }

   public Fields(boolean caseSensitive) {
      this((Map<String, Fields.Field>)(caseSensitive ? new LinkedHashMap<>() : new TreeMap<>(String::compareToIgnoreCase)));
   }

   public Fields(Map<String, Fields.Field> fields) {
      this.fields = fields;
   }

   public Fields asImmutable() {
      return new Fields(Collections.unmodifiableMap(this.fields));
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (obj instanceof Fields that) {
         if (this.getSize() != that.getSize()) {
            return false;
         } else if (!this.fields.getClass().equals(that.fields.getClass())) {
            return false;
         } else {
            for (Entry<String, Fields.Field> entry : this.fields.entrySet()) {
               String name = entry.getKey();
               Fields.Field value = entry.getValue();
               if (!value.equals(that.get(name))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.fields.hashCode();
   }

   public Set<String> getNames() {
      Set<String> result = new LinkedHashSet<>();

      for (Fields.Field field : this.fields.values()) {
         result.add(field.getName());
      }

      return result;
   }

   public Stream<Fields.Field> stream() {
      return this.fields.values().stream();
   }

   public Fields.Field get(String name) {
      return this.fields.get(name);
   }

   public String getValue(String name) {
      Fields.Field field = this.get(name);
      return field == null ? null : field.getValue();
   }

   public List<String> getValues(String name) {
      Fields.Field field = this.get(name);
      return field == null ? null : field.getValues();
   }

   public List<String> getValuesOrEmpty(String name) {
      Fields.Field field = this.get(name);
      return field == null ? Collections.emptyList() : field.getValues();
   }

   public void put(String name, String value) {
      Fields.Field field = new Fields.Field(name, value);
      this.fields.put(name, field);
   }

   public void put(Fields.Field field) {
      if (field != null) {
         String s = field.getName();
         this.fields.put(s, field);
      }
   }

   public void add(String name, String value) {
      this.fields.compute(name, (k, f) -> f == null ? new Fields.Field(name, value) : new Fields.Field(f.getName(), f.getValues(), value));
   }

   public void add(Fields.Field field) {
      String s = field.getName();
      this.fields.compute(s, (k, f) -> f == null ? field : new Fields.Field(f.getName(), f.getValues(), field.getValues()));
   }

   public void addAll(Fields fields) {
      for (Fields.Field field : fields) {
         this.add(field);
      }
   }

   public Fields.Field remove(String name) {
      return this.fields.remove(name);
   }

   public void clear() {
      this.fields.clear();
   }

   public boolean isEmpty() {
      return this.fields.isEmpty();
   }

   public int getSize() {
      return this.fields.size();
   }

   @Override
   public Iterator<Fields.Field> iterator() {
      return this.fields.values().iterator();
   }

   public Map<String, String[]> toStringArrayMap() {
      Map<String, String[]> result = new LinkedHashMap<>();
      this.fields.forEach((k, f) -> result.put(f.getName(), f.getValues().toArray(new String[0])));
      return result;
   }

   @Override
   public String toString() {
      return this.fields.values().stream().map(Fields.Field::toString).collect(Collectors.joining(",", "[", "]"));
   }

   public static Fields combine(Fields a, Fields b) {
      if (b == null || b.isEmpty()) {
         return a == null ? EMPTY : a;
      } else if (a != null && !a.isEmpty()) {
         Fields fields = new Fields();
         fields.addAll(a);
         fields.addAll(b);
         return fields;
      } else {
         return b;
      }
   }

   public static class Field {
      private final String name;
      private final List<String> values;

      public Field(String name, String value) {
         this(name, List.of(value));
      }

      private Field(String name, List<String> values, String... moreValues) {
         this(name, values, List.of(moreValues));
      }

      private Field(String name, List<String> values, List<String> moreValues) {
         this.name = name;
         List<String> list = new ArrayList<>(values.size() + moreValues.size());
         list.addAll(values);
         list.addAll(moreValues);
         this.values = List.copyOf(list);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            Fields.Field that = (Fields.Field)obj;
            return this.name.equals(that.name) && this.values.equals(that.values);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.name, this.values);
      }

      public String getName() {
         return this.name;
      }

      public String getValue() {
         return this.values.get(0);
      }

      public Integer getValueAsInt() {
         String value = this.getValue();
         return value == null ? null : Integer.parseInt(value);
      }

      public List<String> getValues() {
         return this.values;
      }

      public boolean hasMultipleValues() {
         return this.values.size() > 1;
      }

      @Override
      public String toString() {
         return String.format("%s=%s", this.name, this.values);
      }
   }
}
