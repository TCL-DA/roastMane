package org.eclipse.jetty.client;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;

public interface HttpUpgrader {
   String PROTOCOL_ATTRIBUTE = HttpUpgrader.class.getName() + ".protocol";

   void prepare(Request var1);

   void upgrade(Response var1, EndPoint var2, Callback var3);

   public interface Factory {
      HttpUpgrader newHttpUpgrader(HttpVersion var1);
   }
}
