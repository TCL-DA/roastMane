package org.eclipse.jetty.websocket.core.internal;

import java.nio.ByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.websocket.core.Frame;

public class Generator {
   public static final int MAX_HEADER_LENGTH = 28;

   public void generateWholeFrame(Frame frame, ByteBuffer buffer) {
      this.generateHeader(frame, buffer);
      this.generatePayload(frame, buffer);
   }

   public void generateHeader(Frame frame, ByteBuffer buffer) {
      int pos = BufferUtil.flipToFill(buffer);
      byte b = (byte)0;
      if (frame.isFin()) {
         b = (byte)(b | 128);
      }

      if (frame.isRsv1()) {
         b = (byte)(b | 64);
      }

      if (frame.isRsv2()) {
         b = (byte)(b | 32);
      }

      if (frame.isRsv3()) {
         b = (byte)(b | 16);
      }

      byte opcode = frame.getOpCode();
      b = (byte)(b | opcode & 15);
      buffer.put(b);
      b = (byte)(frame.isMasked() ? -128 : 0);
      int payloadLength = frame.getPayloadLength();
      if (payloadLength > 65535) {
         byte var9 = (byte)(b | 127);
         buffer.put(var9);
         buffer.put((byte)0);
         buffer.put((byte)0);
         buffer.put((byte)0);
         buffer.put((byte)0);
         buffer.put((byte)(payloadLength >> 24 & 0xFF));
         buffer.put((byte)(payloadLength >> 16 & 0xFF));
         buffer.put((byte)(payloadLength >> 8 & 0xFF));
         buffer.put((byte)(payloadLength & 0xFF));
      } else if (payloadLength >= 126) {
         byte var10 = (byte)(b | 126);
         buffer.put(var10);
         buffer.put((byte)(payloadLength >> 8));
         buffer.put((byte)(payloadLength & 0xFF));
      } else {
         byte var11 = (byte)(b | payloadLength & 127);
         buffer.put(var11);
      }

      if (frame.isMasked()) {
         buffer.put(frame.getMask());
      }

      BufferUtil.flipToFlush(buffer, pos);
   }

   public void generatePayload(Frame frame, ByteBuffer buffer) {
      ByteBuffer payload = frame.getPayload();
      if (BufferUtil.hasContent(payload)) {
         int pos = BufferUtil.flipToFill(buffer);
         if (frame.isMasked()) {
            this.maskPayload(buffer, frame);
         } else {
            buffer.put(payload.slice());
         }

         BufferUtil.flipToFlush(buffer, pos);
      }
   }

   private void maskPayload(ByteBuffer buffer, Frame frame) {
      byte[] mask = frame.getMask();
      int maskInt = 0;

      for (byte maskByte : mask) {
         maskInt = (maskInt << 8) + (maskByte & 255);
      }

      ByteBuffer payload = frame.getPayload();
      if (payload != null && payload.remaining() > 0) {
         int maskOffset = 0;
         int start = payload.position();
         int end = payload.limit();

         int remaining;
         while ((remaining = end - start) > 0) {
            if (remaining >= 4) {
               buffer.putInt(payload.getInt(start) ^ maskInt);
               start += 4;
            } else {
               buffer.put((byte)(payload.get(start) ^ mask[maskOffset & 3]));
               start++;
               maskOffset++;
            }
         }
      }
   }
}
