package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.server.ResourceFinder;

public abstract class AbstractResourceFinderAdapter implements ResourceFinder {
   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void close() {
   }
}
