package org.eclipse.jetty.io;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.EventListener;
import java.util.concurrent.TimeoutException;

public interface Connection extends Closeable {
   void addEventListener(EventListener var1);

   void removeEventListener(EventListener var1);

   void onOpen();

   void onClose(Throwable var1);

   EndPoint getEndPoint();

   @Override
   void close();

   boolean onIdleExpired(TimeoutException var1);

   long getMessagesIn();

   long getMessagesOut();

   long getBytesIn();

   long getBytesOut();

   long getCreatedTimeStamp();

   public interface Listener extends EventListener {
      default void onOpened(Connection connection) {
      }

      default void onClosed(Connection connection) {
      }
   }

   public interface UpgradeFrom {
      ByteBuffer onUpgradeFrom();
   }

   public interface UpgradeTo {
      void onUpgradeTo(ByteBuffer var1);
   }
}
