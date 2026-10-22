package org.glassfish.jersey.internal.util.collection;

import jakarta.ws.rs.core.AbstractMultivaluedMap;

public class StringKeyIgnoreCaseMultivaluedMap<V> extends AbstractMultivaluedMap<String, V> {
   public StringKeyIgnoreCaseMultivaluedMap() {
      super(new KeyComparatorLinkedHashMap<>(StringIgnoreCaseKeyComparator.SINGLETON));
   }
}
