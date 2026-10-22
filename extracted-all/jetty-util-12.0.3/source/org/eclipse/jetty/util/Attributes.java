package org.eclipse.jetty.util;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.component.Dumpable;

public interface Attributes {
   Attributes NULL = new Attributes() {
      @Override
      public Object removeAttribute(String name) {
         return null;
      }

      @Override
      public Object setAttribute(String name, Object attribute) {
         return null;
      }

      @Override
      public Object getAttribute(String name) {
         return null;
      }

      @Override
      public Set<String> getAttributeNameSet() {
         return Collections.emptySet();
      }

      @Override
      public void clearAttributes() {
      }

      @Override
      public Map<String, Object> asAttributeMap() {
         return Collections.emptyMap();
      }
   };

   Object removeAttribute(String var1);

   Object setAttribute(String var1, Object var2);

   Object getAttribute(String var1);

   Set<String> getAttributeNameSet();

   default Map<String, Object> asAttributeMap() {
      return new AbstractMap<String, Object>() {
         private final Set<String> _attributeNameSet = Attributes.this.getAttributeNameSet();
         private final AbstractSet<Entry<String, Object>> _entrySet = new AbstractSet<Entry<String, Object>>() {
            @Override
            public Iterator<Entry<String, Object>> iterator() {
               final Iterator<String> names = _attributeNameSet.iterator();
               return new Iterator<Entry<String, Object>>() {
                  @Override
                  public boolean hasNext() {
                     return names.hasNext();
                  }

                  public Entry<String, Object> next() {
                     String name = names.next();
                     return new SimpleEntry<>(name, Attributes.this.getAttribute(name));
                  }
               };
            }

            @Override
            public int size() {
               return _attributeNameSet.size();
            }
         };

         @Override
         public int size() {
            return this._attributeNameSet.size();
         }

         @Override
         public Set<Entry<String, Object>> entrySet() {
            return this._entrySet;
         }
      };
   }

   default void clearAttributes() {
      for (String name : this.getAttributeNameSet()) {
         this.removeAttribute(name);
      }
   }

   static Attributes unwrap(Attributes attributes) {
      while (attributes instanceof Attributes.Wrapper) {
         attributes = ((Attributes.Wrapper)attributes).getWrapped();
      }

      return attributes;
   }

   static <T extends Attributes.Wrapper> T unwrap(Attributes attributes, Class<T> target) {
      while (!target.isAssignableFrom(attributes.getClass())) {
         if (!(attributes instanceof Attributes.Wrapper wrapper)) {
            return null;
         }

         attributes = wrapper.getWrapped();
      }

      return (T)attributes;
   }

   public static class Layer implements Attributes {
      private static final Object REMOVED = new Object() {
         @Override
         public String toString() {
            return "REMOVED";
         }
      };
      private final Attributes _persistent;
      private final Attributes _layer;

      public Layer(Attributes persistent) {
         this(persistent, new Attributes.Mapped());
      }

      public Layer(Attributes persistent, Attributes layer) {
         this._persistent = persistent;
         this._layer = layer;
      }

      public Attributes getPersistentAttributes() {
         return this._persistent;
      }

      @Override
      public Object removeAttribute(String name) {
         Object p = this._persistent.getAttribute(name);

         Object var4;
         try {
            Object v = this._layer.setAttribute(name, REMOVED);
            if (v == REMOVED) {
               return null;
            }

            if (v == null) {
               return p;
            }

            var4 = v;
         } finally {
            if (p == null) {
               this._layer.removeAttribute(name);
            }
         }

         return var4;
      }

      @Override
      public Object setAttribute(String name, Object attribute) {
         if (attribute == null) {
            return this.removeAttribute(name);
         } else {
            Object v = this._layer.setAttribute(name, attribute);
            return v == REMOVED ? null : v;
         }
      }

      @Override
      public Object getAttribute(String name) {
         Object v = this._layer.getAttribute(name);
         if (v != null) {
            return v == REMOVED ? null : v;
         } else {
            return this._persistent.getAttribute(name);
         }
      }

      @Override
      public Set<String> getAttributeNameSet() {
         Set<String> names = new HashSet<>(this._persistent.getAttributeNameSet());

         for (String name : this._layer.getAttributeNameSet()) {
            if (this._layer.getAttribute(name) == REMOVED) {
               names.remove(name);
            } else {
               names.add(name);
            }
         }

         return Collections.unmodifiableSet(names);
      }

      @Override
      public void clearAttributes() {
         this._layer.clearAttributes();

         for (String name : this._persistent.getAttributeNameSet()) {
            this._layer.setAttribute(name, REMOVED);
         }
      }

      @Override
      public int hashCode() {
         int hash = 113;

         for (String name : this.getAttributeNameSet()) {
            hash += name.hashCode() ^ this.getAttribute(name).hashCode();
         }

         return hash;
      }

      @Override
      public boolean equals(Object o) {
         if (o instanceof Attributes a) {
            Set<String> ours = this.getAttributeNameSet();
            Set<String> theirs = this.getAttributeNameSet();
            if (!ours.equals(theirs)) {
               return false;
            } else {
               for (String s : ours) {
                  if (!Objects.equals(this.getAttribute(s), a.getAttribute(s))) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   public static class Lazy implements Attributes, Dumpable {
      private final AtomicReference<ConcurrentMap<String, Object>> _map = new AtomicReference<>();

      private ConcurrentMap<String, Object> map() {
         return this._map.get();
      }

      private ConcurrentMap<String, Object> ensureMap() {
         ConcurrentHashMap var2;
         do {
            ConcurrentMap<String, Object> map = this.map();
            if (map != null) {
               return map;
            }

            var2 = new ConcurrentHashMap();
         } while (!this._map.compareAndSet(null, var2));

         return var2;
      }

      @Override
      public Map<String, Object> asAttributeMap() {
         return this.ensureMap();
      }

      @Override
      public Object removeAttribute(String name) {
         Map<String, Object> map = this.map();
         return map == null ? null : map.remove(name);
      }

      @Override
      public Object setAttribute(String name, Object attribute) {
         return attribute == null ? this.removeAttribute(name) : this.ensureMap().put(name, attribute);
      }

      @Override
      public Object getAttribute(String name) {
         Map<String, Object> map = this.map();
         return map == null ? null : map.get(name);
      }

      public Collection<Object> getAttributeEntriesSet() {
         return this.map().values();
      }

      @Override
      public Set<String> getAttributeNameSet() {
         return Collections.unmodifiableSet(this.keySet());
      }

      @Override
      public void clearAttributes() {
         Map<String, Object> map = this.map();
         if (map != null) {
            map.clear();
         }
      }

      public int size() {
         Map<String, Object> map = this.map();
         return map == null ? 0 : map.size();
      }

      @Override
      public String toString() {
         Map<String, Object> map = this.map();
         return map == null ? "{}" : map.toString();
      }

      private Set<String> keySet() {
         Map<String, Object> map = this.map();
         return map == null ? Collections.emptySet() : map.keySet();
      }

      public void addAll(Attributes attributes) {
         for (String name : attributes.getAttributeNameSet()) {
            this.setAttribute(name, attributes.getAttribute(name));
         }
      }

      @Override
      public String dump() {
         return Dumpable.dump(this);
      }

      @Override
      public void dump(Appendable out, String indent) throws IOException {
         Dumpable.dumpObjects(out, indent, String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode()), this.map());
      }
   }

   public static class Mapped implements Attributes {
      private final Map<String, Object> _map;
      private final Set<String> _names;

      public Mapped() {
         this(new ConcurrentHashMap<>());
      }

      public Mapped(Map<String, Object> map) {
         this._map = Objects.requireNonNull(map);
         this._names = Collections.unmodifiableSet(this._map.keySet());
      }

      public Mapped(Attributes.Mapped attributes) {
         this();
         this._map.putAll(attributes._map);
      }

      @Override
      public Map<String, Object> asAttributeMap() {
         return this._map;
      }

      @Override
      public Object removeAttribute(String name) {
         return this._map.remove(name);
      }

      @Override
      public Object setAttribute(String name, Object attribute) {
         return attribute == null ? this._map.remove(name) : this._map.put(name, attribute);
      }

      @Override
      public Object getAttribute(String name) {
         return this._map.get(name);
      }

      @Override
      public Set<String> getAttributeNameSet() {
         return this._names;
      }

      @Override
      public void clearAttributes() {
         this._map.clear();
      }

      public int size() {
         return this._map.size();
      }

      @Override
      public String toString() {
         return this._map.toString();
      }

      public void addAll(Attributes attributes) {
         for (String name : attributes.getAttributeNameSet()) {
            this.setAttribute(name, attributes.getAttribute(name));
         }
      }

      public Set<Entry<String, Object>> getAttributeEntrySet() {
         return this._map.entrySet();
      }

      @Override
      public int hashCode() {
         return this._map.hashCode();
      }

      @Override
      public boolean equals(Object o) {
         if (o instanceof Attributes a) {
            for (Entry<String, Object> e : this._map.entrySet()) {
               if (!Objects.equals(e.getValue(), a.getAttribute(e.getKey()))) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static class Wrapper implements Attributes {
      private final Attributes _wrapped;

      public Wrapper(Attributes wrapped) {
         this._wrapped = Objects.requireNonNull(wrapped);
      }

      public Attributes getWrapped() {
         return this._wrapped;
      }

      @Override
      public Object removeAttribute(String name) {
         return this.getWrapped().removeAttribute(name);
      }

      @Override
      public Object setAttribute(String name, Object attribute) {
         return this.getWrapped().setAttribute(name, attribute);
      }

      @Override
      public Object getAttribute(String name) {
         return this.getWrapped().getAttribute(name);
      }

      @Override
      public Set<String> getAttributeNameSet() {
         return this.getWrapped().getAttributeNameSet();
      }

      @Override
      public void clearAttributes() {
         this.getWrapped().clearAttributes();
      }

      @Override
      public int hashCode() {
         return this.getWrapped().hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         return this.getWrapped().equals(obj);
      }
   }
}
