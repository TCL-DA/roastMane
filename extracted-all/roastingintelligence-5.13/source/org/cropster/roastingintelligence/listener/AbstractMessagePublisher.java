package org.cropster.roastingintelligence.listener;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractMessagePublisher {
   private final List<MessageListener> _messageListeners = new CopyOnWriteArrayList<>();

   public Collection<MessageListener> listeners() {
      return this._messageListeners;
   }

   protected void publishMessage(MessageListener.MessageType type, String message) {
      for (MessageListener listener : this._messageListeners) {
         listener.publishMessage(type, message);
      }
   }
}
