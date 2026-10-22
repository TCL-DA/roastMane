package org.jvnet.mimepull;

import java.util.ArrayList;
import java.util.Collection;

final class FinalArrayList<T> extends ArrayList<T> {
   private static final long serialVersionUID = 7996571518730043311L;

   public FinalArrayList(int initialCapacity) {
      super(initialCapacity);
   }

   public FinalArrayList() {
   }

   public FinalArrayList(Collection<? extends T> ts) {
      super(ts);
   }
}
