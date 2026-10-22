package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import java.util.Objects;
import org.eclipse.jetty.websocket.core.CoreSession;

public abstract class AbstractMessageSink implements MessageSink {
   private final CoreSession session;
   private final MethodHandle methodHandle;
   private final boolean autoDemand;

   public AbstractMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      this.session = Objects.requireNonNull(session, "CoreSession");
      this.methodHandle = Objects.requireNonNull(methodHandle, "MethodHandle");
      this.autoDemand = autoDemand;
   }

   public CoreSession getCoreSession() {
      return this.session;
   }

   public MethodHandle getMethodHandle() {
      return this.methodHandle;
   }

   public boolean isAutoDemand() {
      return this.autoDemand;
   }

   protected void autoDemand() {
      if (this.isAutoDemand()) {
         this.getCoreSession().demand();
      }
   }
}
