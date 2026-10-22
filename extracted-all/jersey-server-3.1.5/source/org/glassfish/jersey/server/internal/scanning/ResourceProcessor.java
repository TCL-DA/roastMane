package org.glassfish.jersey.server.internal.scanning;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceProcessor {
   boolean accept(String var1);

   void process(String var1, InputStream var2) throws IOException;
}
