package org.apache.commons.collections4;

@FunctionalInterface
public interface Factory<T> {
   T create();
}
