package org.glassfish.jersey.internal.guava;

@Deprecated
abstract class GenericMapMaker<K0, V0> {
   <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener() {
      return GenericMapMaker.NullListener.INSTANCE;
   }

   static enum NullListener implements MapMaker.RemovalListener<Object, Object> {
      INSTANCE;

      @Override
      public void onRemoval(MapMaker.RemovalNotification<Object, Object> notification) {
      }
   }
}
