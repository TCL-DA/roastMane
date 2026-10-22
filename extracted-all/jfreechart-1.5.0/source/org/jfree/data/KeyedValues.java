package org.jfree.data;

import java.util.List;

public interface KeyedValues extends Values {
   Comparable getKey(int var1);

   int getIndex(Comparable var1);

   List getKeys();

   Number getValue(Comparable var1);
}
