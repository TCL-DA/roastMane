package org.eclipse.jetty.websocket.core.internal;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingNestedCallback;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.websocket.core.CloseStatus;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.FrameHandler;
import org.eclipse.jetty.websocket.core.exception.BadPayloadException;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler implements FrameHandler {
   private static final Logger LOG = LoggerFactory.getLogger(MessageHandler.class);
   private CoreSession coreSession;
   private Utf8StringBuilder textMessageBuffer;
   private ByteArrayOutputStream binaryMessageBuffer;
   private byte dataType = -1;

   public static MessageHandler from(final Consumer<String> onText, final Consumer<ByteBuffer> onBinary) {
      return new MessageHandler() {
         @Override
         protected void onText(String message, Callback callback) {
            if (onText == null) {
               super.onText(message, callback);
            } else {
               try {
                  onText.accept(message);
                  callback.succeeded();
               } catch (Throwable var4) {
                  callback.failed(var4);
               }
            }
         }

         @Override
         protected void onBinary(ByteBuffer message, Callback callback) {
            if (onBinary == null) {
               super.onBinary(message, callback);
            } else {
               try {
                  onBinary.accept(message);
                  callback.succeeded();
               } catch (Throwable var4) {
                  callback.failed(var4);
               }
            }
         }
      };
   }

   public CoreSession getCoreSession() {
      return this.coreSession;
   }

   private Utf8StringBuilder getTextMessageBuffer() {
      if (this.textMessageBuffer == null) {
         this.textMessageBuffer = new Utf8StringBuilder();
      }

      return this.textMessageBuffer;
   }

   private ByteArrayOutputStream getBinaryMessageBuffer() {
      if (this.binaryMessageBuffer == null) {
         this.binaryMessageBuffer = new ByteArrayOutputStream();
      }

      return this.binaryMessageBuffer;
   }

   @Override
   public void onOpen(CoreSession coreSession, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onOpen {}", coreSession);
      }

      this.coreSession = coreSession;
      callback.succeeded();
      coreSession.demand();
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onFrame {}", frame);
      }

      switch (frame.getOpCode()) {
         case 0:
            this.onContinuationFrame(frame, callback);
            if (frame.isFin()) {
               this.dataType = -1;
            }
            break;
         case 1:
            this.dataType = 1;
            this.onTextFrame(frame, callback);
            break;
         case 2:
            this.dataType = 2;
            this.onBinaryFrame(frame, callback);
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            callback.failed(new IllegalStateException());
            break;
         case 8:
            this.onCloseFrame(frame, callback);
            break;
         case 9:
            this.onPingFrame(frame, callback);
            break;
         case 10:
            this.onPongFrame(frame, callback);
      }
   }

   @Override
   public void onError(Throwable cause, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onError ", cause);
      }

      callback.succeeded();
   }

   @Override
   public void onClosed(CloseStatus closeStatus, Callback callback) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onClosed {}", closeStatus);
      }

      if (this.textMessageBuffer != null) {
         this.textMessageBuffer.reset();
         this.textMessageBuffer = null;
      }

      if (this.binaryMessageBuffer != null) {
         this.binaryMessageBuffer.reset();
         this.binaryMessageBuffer = null;
      }

      callback.succeeded();
   }

   protected void onTextFrame(Frame frame, Callback callback) {
      try {
         Utf8StringBuilder textBuffer = this.getTextMessageBuffer();
         if (frame.hasPayload()) {
            long maxSize = this.coreSession.getMaxTextMessageSize();
            long currentSize = (long)(frame.getPayload().remaining() + textBuffer.length());
            if (currentSize > maxSize) {
               throw new MessageTooLargeException("Message larger than " + maxSize + " bytes");
            }

            textBuffer.append(frame.getPayload());
         }

         if (frame.isFin()) {
            this.onText(textBuffer.takeCompleteString(BadPayloadException.InvalidUtf8::new), callback);
            textBuffer.reset();
         } else {
            if (textBuffer.hasCodingErrors()) {
               throw new BadPayloadException.InvalidUtf8();
            }

            callback.succeeded();
         }

         this.coreSession.demand();
      } catch (Throwable var8) {
         callback.failed(var8);
      }
   }

   protected void onBinaryFrame(Frame frame, Callback callback) {
      try {
         ByteArrayOutputStream binaryBuffer = this.getBinaryMessageBuffer();
         if (frame.hasPayload()) {
            long maxSize = this.coreSession.getMaxBinaryMessageSize();
            long currentSize = (long)(frame.getPayload().remaining() + binaryBuffer.size());
            if (currentSize > maxSize) {
               throw new MessageTooLargeException("Message larger than " + maxSize + " bytes");
            }

            BufferUtil.writeTo(frame.getPayload(), binaryBuffer);
         }

         if (frame.isFin()) {
            this.onBinary(BufferUtil.toBuffer(binaryBuffer.toByteArray()), callback);
            binaryBuffer.reset();
         } else {
            callback.succeeded();
         }

         this.coreSession.demand();
      } catch (Throwable var8) {
         callback.failed(var8);
      }
   }

   protected void onContinuationFrame(Frame frame, Callback callback) {
      switch (this.dataType) {
         case 1:
            this.onTextFrame(frame, callback);
            break;
         case 2:
            this.onBinaryFrame(frame, callback);
            break;
         default:
            throw new IllegalStateException();
      }
   }

   protected void onPingFrame(Frame frame, Callback callback) {
      this.coreSession.sendFrame(new Frame((byte)10, true, frame.getPayload()), Callback.from(() -> this.coreSession.demand(), callback), false);
   }

   protected void onPongFrame(Frame frame, Callback callback) {
      callback.succeeded();
      this.coreSession.demand();
   }

   protected void onCloseFrame(Frame frame, Callback callback) {
      callback.succeeded();
   }

   protected void onText(String message, Callback callback) {
      callback.failed(new BadPayloadException("Text Not Accepted"));
   }

   protected void onBinary(ByteBuffer message, Callback callback) {
      callback.failed(new BadPayloadException("Binary Not Accepted"));
   }

   public void sendText(String message, Callback callback, boolean batch) {
      this.getCoreSession().sendFrame(new Frame((byte)1, true, message), callback, batch);
   }

   public void sendText(Callback callback, final boolean batch, final String... parts) {
      if (parts != null && parts.length != 0) {
         if (parts.length == 1) {
            this.sendText(parts[0], callback, batch);
         } else {
            (new IteratingNestedCallback(callback) {
               int i = 0;

               protected Action process() {
                  if (this.i + 1 > parts.length) {
                     return Action.SUCCEEDED;
                  } else {
                     String part = parts[this.i++];
                     MessageHandler.this.getCoreSession().sendFrame(new Frame((byte)(this.i == 1 ? 1 : 0), this.i == parts.length, part), this, batch);
                     return Action.SCHEDULED;
                  }
               }
            }).iterate();
         }
      } else {
         callback.succeeded();
      }
   }

   public void sendBinary(ByteBuffer message, Callback callback, boolean batch) {
      this.getCoreSession().sendFrame(new Frame((byte)2, true, message), callback, batch);
   }

   public void sendBinary(Callback callback, final boolean batch, final ByteBuffer... parts) {
      if (parts != null && parts.length != 0) {
         if (parts.length == 1) {
            this.sendBinary(parts[0], callback, batch);
         } else {
            (new IteratingNestedCallback(callback) {
               int i = 0;

               protected Action process() {
                  if (this.i + 1 > parts.length) {
                     return Action.SUCCEEDED;
                  } else {
                     ByteBuffer part = parts[this.i++];
                     MessageHandler.this.getCoreSession().sendFrame(new Frame((byte)(this.i == 1 ? 2 : 0), this.i == parts.length, part), this, batch);
                     return Action.SCHEDULED;
                  }
               }
            }).iterate();
         }
      } else {
         callback.succeeded();
      }
   }
}
