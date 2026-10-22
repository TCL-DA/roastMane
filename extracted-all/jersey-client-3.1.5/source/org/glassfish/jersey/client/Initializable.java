package org.glassfish.jersey.client;

public interface Initializable<T extends Initializable<T>> {
   T preInitialize();

   ClientConfig getConfiguration();
}
