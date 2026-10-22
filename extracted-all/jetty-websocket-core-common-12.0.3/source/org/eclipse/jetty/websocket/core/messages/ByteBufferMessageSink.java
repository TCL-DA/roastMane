package org.eclipse.jetty.websocket.core.messages;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import org.eclipse.jetty.io.ByteBufferCallbackAccumulator;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.exception.InvalidSignatureException;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;

public class ByteBufferMessageSink extends AbstractMessageSink {
   private ByteBufferCallbackAccumulator accumulator;

   public ByteBufferMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand) {
      this(session, methodHandle, autoDemand, true);
   }

   protected ByteBufferMessageSink(CoreSession session, MethodHandle methodHandle, boolean autoDemand, boolean validateSignature) {
      super(session, methodHandle, autoDemand);
      if (validateSignature) {
         MethodType onMessageType = MethodType.methodType(void.class, ByteBuffer.class);
         if (methodHandle.type() != onMessageType) {
            throw InvalidSignatureException.build(onMessageType, methodHandle.type());
         }
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

         if (frame.isFin() && this.accumulator == null) {
            this.invoke(this.getMethodHandle(), frame.getPayload(), callback);
            this.autoDemand();
            return;
         }

         if (frame.isFin() || frame.hasPayload()) {
            if (this.accumulator == null) {
               this.accumulator = new ByteBufferCallbackAccumulator();
            }

            this.accumulator.addEntry(frame.getPayload(), callback);
            if (frame.isFin()) {
               ByteBufferPool bufferPool = this.getCoreSession().getByteBufferPool();
               RetainableByteBuffer buffer = bufferPool.acquire(this.accumulator.getLength(), false);
               ByteBuffer byteBuffer = buffer.getByteBuffer();
               this.accumulator.writeTo(byteBuffer);
               callback = Callback.from(buffer::release);
               this.invoke(this.getMethodHandle(), byteBuffer, callback);
               this.autoDemand();
            } else {
               this.getCoreSession().demand();
            }

            return;
         }

         callback.succeeded();
         this.getCoreSession().demand();
      } catch (Throwable var13) {
         if (this.accumulator != null) {
            this.accumulator.fail(var13);
         }

         callback.failed(var13);
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

   protected void invoke(MethodHandle methodHandle, ByteBuffer byteBuffer, Callback callback) throws Throwable {
      methodHandle.invoke((ByteBuffer)byteBuffer);
      callback.succeeded();
   }
}
