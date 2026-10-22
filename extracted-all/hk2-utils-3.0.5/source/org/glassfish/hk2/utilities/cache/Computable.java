package org.glassfish.hk2.utilities.cache;

public interface Computable<K, V> {
   V compute(K var1) throws ComputationErrorException;
}
