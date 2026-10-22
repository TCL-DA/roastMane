package org.apache.logging.log4j.util;

@FunctionalInterface
public interface BiConsumer<K, V> extends java.util.function.BiConsumer<K, V> {
   @Override
   void accept(K k, V v);
}
