package org.glassfish.jersey.internal.util.collection;

public class StringIgnoreCaseKeyComparator implements KeyComparator<String> {
   private static final long serialVersionUID = 9106900325469360723L;
   public static final StringIgnoreCaseKeyComparator SINGLETON = new StringIgnoreCaseKeyComparator();

   public int hash(String k) {
      return k.toLowerCase().hashCode();
   }

   public boolean equals(String x, String y) {
      return x.equalsIgnoreCase(y);
   }
}
