package org.eclipse.jetty.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.component.Dumpable;

@Deprecated
public class AttributesMap implements Attributes, Dumpable {
   private final AtomicReference<ConcurrentMap<String, Object>> _map = new AtomicReference<>();

   public AttributesMap() {
   }

   public AttributesMap(AttributesMap attributes) {
      ConcurrentMap<String, Object> map = attributes.map();
      if (map != null) {
         this._map.set(new ConcurrentHashMap<>(map));
      }
   }

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

   @Override
   public Set<String> getAttributeNameSet() {
      return Collections.unmodifiableSet(this.keySet());
   }

   public Set<Entry<String, Object>> getAttributeEntrySet() {
      Map<String, Object> map = this.map();
      return map == null ? Collections.emptySet() : map.entrySet();
   }

   public static Enumeration<String> getAttributeNamesCopy(Attributes attrs) {
      if (attrs instanceof AttributesMap) {
         return Collections.enumeration(((AttributesMap)attrs).keySet());
      } else {
         List<String> names = new ArrayList<>(attrs.getAttributeNameSet());
         return Collections.enumeration(names);
      }
   }

   public static Set<String> getAttributeNameSetCopy(Attributes attrs) {
      if (attrs instanceof AttributesMap) {
         return ((AttributesMap)attrs).keySet();
      } else {
         List<String> names = new ArrayList<>(attrs.getAttributeNameSet());
         return new HashSet<>(names);
      }
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
