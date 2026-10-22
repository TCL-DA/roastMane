package org.glassfish.jersey.internal.guava;

final class ObjectArrays {
   static final Object[] EMPTY_ARRAY = new Object[0];

   private ObjectArrays() {
   }

   public static <T> T[] newArray(T[] reference, int length) {
      return (T[])Platform.newArray(reference, length);
   }

   static <T> T[] arraysCopyOf(T[] original, int newLength) {
      T[] copy = (T[])newArray(original, newLength);
      System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
      return copy;
   }
}
