package org.glassfish.jersey.internal.util.collection;

import java.io.Serializable;

public interface KeyComparator<K> extends Serializable {
   boolean equals(K var1, K var2);

   int hash(K var1);
}
