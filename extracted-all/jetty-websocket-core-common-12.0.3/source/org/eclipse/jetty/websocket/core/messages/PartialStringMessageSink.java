package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.exception.BadPayloadException;

public class PartialStringMessageSink extends AbstractMessageSink {
   private Utf8StringBuilder accumulator;

   public PartialStringMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
   }

   @Override
   public void accept(Frame frame, Callback callback) {
      try {
         if (this.accumulator == null) {
            this.accumulator = new Utf8StringBuilder(this.getCoreSession().getInputBufferSize());
         }

         this.accumulator.append(frame.getPayload());
         if (frame.isFin()) {
            String complete = this.accumulator.takeCompleteString(BadPayloadException.InvalidUtf8::new);
            this.getMethodHandle().invoke((String)complete, (boolean)true);
         } else {
            String partial = this.accumulator.takePartialString(BadPayloadException.InvalidUtf8::new);
            this.getMethodHandle().invoke((String)partial, (boolean)false);
         }

         callback.succeeded();
         this.autoDemand();
      } catch (Throwable var7) {
         callback.failed(var7);
      } finally {
         if (frame.isFin()) {
            this.accumulator = null;
         }
      }
   }

   @Override
   public void fail(Throwable failure) {
      if (this.accumulator != null) {
         this.accumulator.reset();
      }
   }
}
