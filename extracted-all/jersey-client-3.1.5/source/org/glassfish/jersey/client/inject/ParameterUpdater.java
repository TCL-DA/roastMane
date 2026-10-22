package org.glassfish.jersey.client.inject;

public interface ParameterUpdater<T, R> {
   String getName();

   String getDefaultValueString();

   R update(T var1);
}
