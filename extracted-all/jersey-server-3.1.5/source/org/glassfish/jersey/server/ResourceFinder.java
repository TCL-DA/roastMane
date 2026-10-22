package org.glassfish.jersey.server;

import java.io.InputStream;
import java.util.Iterator;

public interface ResourceFinder extends Iterator<String>, AutoCloseable {
   InputStream open();

   @Override
   void close();

   void reset();

   @Override
   void remove();
}
