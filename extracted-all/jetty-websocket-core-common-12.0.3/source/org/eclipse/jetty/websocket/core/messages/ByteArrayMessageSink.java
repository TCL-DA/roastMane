package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import org.eclipse.jetty.io.ByteBufferCallbackAccumulator;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.exception.InvalidSignatureException;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;

public class ByteArrayMessageSink extends AbstractMessageSink {
   private ByteBufferCallbackAccumulator accumulator;

   public ByteArrayMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      super(session, methodHandle, autoDemand);
      MethodType onMessageType = MethodType.methodType(void.class, byte[].class, int.class, int.class);
      if (methodHandle.type().changeReturnType(void.class) != onMessageType.changeReturnType(void.class)) {
         throw InvalidSignatureException.build(onMessageType, methodHandle.type());
      }
   }

   @Override
   public void accept(Frame frame, Callback callback) {
      try {
         long size = (long)((this.accumulator == null ? 0 : this.accumulator.getLength()) + frame.getPayloadLength());
         long maxSize = this.getCoreSession().getMaxBinaryMessageSize();
         if (maxSize > 0L && size > maxSize) {
            callback.failed(new MessageTooLargeException(String.format("Binary message too large: %,d > %,d", size, maxSize)));
            return;
         }

         ByteBuffer payload = frame.getPayload();
         if (frame.isFin() && this.accumulator == null) {
            byte[] buf = BufferUtil.toArray(payload);
            this.getMethodHandle().invoke((byte[])buf, (int)0, (int)buf.length);
            callback.succeeded();
            this.autoDemand();
            return;
         }

         if (frame.isFin() || frame.hasPayload()) {
            if (this.accumulator == null) {
               this.accumulator = new ByteBufferCallbackAccumulator();
            }

            this.accumulator.addEntry(payload, callback);
            if (frame.isFin()) {
               callback = Callback.NOOP;
               byte[] buf = this.accumulator.takeByteArray();
               this.getMethodHandle().invoke((byte[])buf, (int)0, (int)buf.length);
               this.autoDemand();
            } else {
               this.getCoreSession().demand();
            }

            return;
         }

         callback.succeeded();
         this.getCoreSession().demand();
      } catch (Throwable var12) {
         if (this.accumulator != null) {
            this.accumulator.fail(var12);
         }

         callback.failed(var12);
         return;
      } finally {
         if (frame.isFin()) {
            this.accumulator = null;
         }
      }
   }

   @Override
   public void fail(Throwable failure) {
      if (this.accumulator != null) {
         this.accumulator.fail(failure);
      }
   }
}
