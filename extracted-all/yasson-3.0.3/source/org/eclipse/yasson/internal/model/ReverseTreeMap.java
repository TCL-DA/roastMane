package org.eclipse.yasson.internal.model;

import java.util.Comparator;
import java.util.TreeMap;

public class ReverseTreeMap<K extends Comparable<? super K>, V> extends TreeMap<K, V> {
   public ReverseTreeMap() {
      super(Comparator.reverseOrder());
   }
}
