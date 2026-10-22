package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.exception.BadPayloadException;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;

public class StringMessageSink extends AbstractMessageSink {
   private Utf8StringBuilder out;
   private int size = 0;

   public StringMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
   }

   @Override
   public void accept(Frame frame, Callback callback) {
      try {
         this.size = this.size + frame.getPayloadLength();
         long maxSize = this.getCoreSession().getMaxTextMessageSize();
         if (maxSize <= 0L || (long)this.size <= maxSize) {
            if (this.out == null) {
               this.out = new Utf8StringBuilder(this.getCoreSession().getInputBufferSize());
            }

            this.out.append(frame.getPayload());
            if (frame.isFin()) {
               this.getMethodHandle().invoke((String)this.out.takeCompleteString(BadPayloadException.InvalidUtf8::new));
               callback.succeeded();
               this.autoDemand();
            } else {
               callback.succeeded();
               this.getCoreSession().demand();
            }

            return;
         }

         callback.failed(new MessageTooLargeException(String.format("Text message too large: %,d > %,d", this.size, maxSize)));
      } catch (Throwable var8) {
         callback.failed(var8);
         return;
      } finally {
         if (frame.isFin()) {
            this.size = 0;
            this.out = null;
         }
      }
   }

   @Override
   public void fail(Throwable failure) {
      if (this.out != null) {
         this.out.reset();
      }
   }
}
