package org.cropster.roastingintelligence.listener;

public interface MessageListener {
   void publishMessage(MessageListener.MessageType var1, String var2);

   public static enum MessageType {
      TRACE,
      INFO,
      SUCCESS,
      WARN,
      ERROR,
      CRITICAL,
      FATAL;
   }
}
