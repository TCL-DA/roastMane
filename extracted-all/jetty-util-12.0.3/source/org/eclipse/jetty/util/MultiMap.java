package org.eclipse.jetty.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MultiMap<V> extends LinkedHashMap<String, List<V>> {
   public MultiMap() {
   }

   public MultiMap(Map<String, List<V>> map) {
      super(map);
   }

   public MultiMap(MultiMap<V> map) {
      super(map);
   }

   public List<V> getValues(String name) {
      List<V> vals = (List<V>)super.get(name);
      return vals != null && !vals.isEmpty() ? vals : null;
   }

   public V getValue(String name) {
      return this.getValue(name, 0);
   }

   public V getValue(String name, int i) {
      List<V> vals = this.getValues(name);
      if (vals == null) {
         return null;
      } else {
         return i == 0 && vals.isEmpty() ? null : vals.get(i);
      }
   }

   public String getString(String name) {
      List<V> vals = this.get(name);
      if (vals != null && !vals.isEmpty()) {
         if (vals.size() == 1) {
            return vals.get(0).toString();
         } else {
            StringBuilder values = new StringBuilder(128);

            for (V e : vals) {
               if (e != null) {
                  if (values.length() > 0) {
                     values.append(',');
                  }

                  values.append(e.toString());
               }
            }

            return values.toString();
         }
      } else {
         return null;
      }
   }

   public List<V> put(String name, V value) {
      if (value == null) {
         return (List<V>)super.put(name, null);
      } else {
         List<V> vals = new ArrayList<>();
         vals.add(value);
         return this.put(name, vals);
      }
   }

   public void putAllValues(Map<String, V> input) {
      for (Entry<String, V> entry : input.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }
   }

   public List<V> putValues(String name, List<V> values) {
      return (List<V>)super.put(name, (V)values);
   }

   @SafeVarargs
   public final List<V> putValues(String name, V... values) {
      List<V> list = new ArrayList<>();
      list.addAll(Arrays.asList(values));
      return (List<V>)super.put(name, (V)list);
   }

   public void add(String name, V value) {
      List<V> lo = this.get(name);
      if (lo == null) {
         lo = new ArrayList<>();
      }

      lo.add(value);
      super.put(name, (V)lo);
   }

   public void addValues(String name, List<V> values) {
      List<V> lo = this.get(name);
      if (lo == null) {
         lo = new ArrayList<>();
      }

      lo.addAll(values);
      this.put(name, lo);
   }

   public void addValues(String name, V[] values) {
      List<V> lo = this.get(name);
      if (lo == null) {
         lo = new ArrayList<>();
      }

      lo.addAll(Arrays.asList(values));
      this.put(name, lo);
   }

   public boolean addAllValues(MultiMap<V> map) {
      boolean merged = false;
      if (map != null && !map.isEmpty()) {
         for (Entry<String, List<V>> entry : map.entrySet()) {
            String name = entry.getKey();
            List<V> values = entry.getValue();
            if (this.containsKey(name)) {
               merged = true;
            }

            this.addValues(name, values);
         }

         return merged;
      } else {
         return merged;
      }
   }

   public boolean removeValue(String name, V value) {
      List<V> lo = this.get(name);
      if (lo != null && !lo.isEmpty()) {
         boolean ret = lo.remove(value);
         if (lo.isEmpty()) {
            this.remove(name);
         } else {
            this.put(name, lo);
         }

         return ret;
      } else {
         return false;
      }
   }

   public boolean containsSimpleValue(V value) {
      for (List<V> vals : this.values()) {
         if (vals.size() == 1 && vals.contains(value)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String toString() {
      Iterator<Entry<String, List<V>>> iter = this.entrySet().iterator();
      StringBuilder sb = new StringBuilder();
      sb.append('{');

      for (boolean delim = false; iter.hasNext(); delim = true) {
         Entry<String, List<V>> e = iter.next();
         if (delim) {
            sb.append(", ");
         }

         String key = e.getKey();
         List<V> vals = e.getValue();
         sb.append(key);
         sb.append('=');
         if (vals.size() == 1) {
            sb.append(vals.get(0));
         } else {
            sb.append(vals);
         }
      }

      sb.append('}');
      return sb.toString();
   }

   public Map<String, String[]> toStringArrayMap() {
      Map<String, String[]> map = new LinkedHashMap<String, String[]>(this.size() * 3 / 2) {
         @Override
         public String toString() {
            StringBuilder b = new StringBuilder();
            b.append('{');

            for (String k : super.keySet()) {
               if (b.length() > 1) {
                  b.append(',');
               }

               b.append(k);
               b.append('=');
               b.append(Arrays.asList((String[])super.get(k)));
            }

            b.append('}');
            return b.toString();
         }
      };

      for (Entry<String, List<V>> entry : this.entrySet()) {
         String[] a = null;
         if (entry.getValue() != null) {
            a = new String[entry.getValue().size()];
            a = entry.getValue().toArray(a);
         }

         map.put(entry.getKey(), a);
      }

      return map;
   }
}
