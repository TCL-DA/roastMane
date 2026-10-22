package org.eclipse.jetty.server;

import java.nio.ByteBuffer;
import java.util.List;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;

public interface ConnectionFactory {
   String getProtocol();

   List<String> getProtocols();

   Connection newConnection(Connector var1, EndPoint var2);

   public interface Configuring extends ConnectionFactory {
      void configure(Connector var1);
   }

   public interface Detecting extends ConnectionFactory {
      ConnectionFactory.Detecting.Detection detect(ByteBuffer var1);

      public static enum Detection {
         RECOGNIZED,
         NOT_RECOGNIZED,
         NEED_MORE_BYTES;
      }
   }

   public interface Upgrading extends ConnectionFactory {
      Connection upgradeConnection(Connector var1, EndPoint var2, org.eclipse.jetty.http.MetaData.Request var3, Mutable var4) throws BadMessageException;
   }
}
