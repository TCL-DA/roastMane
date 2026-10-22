package org.eclipse.jetty.websocket.core.util;

import org.eclipse.jetty.websocket.core.Behavior;
import org.eclipse.jetty.websocket.core.CloseStatus;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.OpCode;
import org.eclipse.jetty.websocket.core.exception.CloseException;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;

public class FrameValidation {
   public static void assertValidIncoming(Frame frame, CoreSession coreSession) {
      assertValidFrame(frame, coreSession);
      long maxFrameSize = coreSession.getMaxFrameSize();
      if (maxFrameSize > 0L && (long)frame.getPayloadLength() > maxFrameSize) {
         throw new MessageTooLargeException("Cannot handle payload lengths larger than " + maxFrameSize);
      } else {
         Behavior behavior = coreSession.getBehavior();
         switch (behavior) {
            case SERVER:
               if (!frame.isMasked()) {
                  throw new ProtocolException("Client MUST mask all frames (RFC-6455: Section 5.1)");
               }
               break;
            case CLIENT:
               if (frame.isMasked()) {
                  throw new ProtocolException("Server MUST NOT mask any frames (RFC-6455: Section 5.1)");
               }
               break;
            default:
               throw new IllegalStateException(behavior.toString());
         }

         if (frame.getOpCode() == 8 && !(frame instanceof Frame.Parsed)) {
            CloseStatus.getCloseStatus(frame);
         }
      }
   }

   public static void assertValidOutgoing(Frame frame, CoreSession coreSession) throws CloseException {
      assertValidFrame(frame, coreSession);
      boolean autoFragment = coreSession.isAutoFragment();
      long maxFrameSize = coreSession.getMaxFrameSize();
      if (!autoFragment && maxFrameSize > 0L && (long)frame.getPayloadLength() > maxFrameSize) {
         throw new MessageTooLargeException("Cannot handle payload lengths larger than " + maxFrameSize);
      } else {
         if (frame.getOpCode() == 8 && !(frame instanceof Frame.Parsed)) {
            CloseStatus closeStatus = CloseStatus.getCloseStatus(frame);
            if (!CloseStatus.isTransmittableStatusCode(closeStatus.getCode()) && closeStatus.getCode() != 1005) {
               throw new ProtocolException("Frame has non-transmittable status code");
            }
         }
      }
   }

   public static void assertValidFrame(Frame frame, CoreSession coreSession) {
      if (!OpCode.isKnown(frame.getOpCode())) {
         throw new ProtocolException("Unknown opcode: " + frame.getOpCode());
      } else {
         int payloadLength = frame.getPayloadLength();
         if (frame.isControlFrame()) {
            if (!frame.isFin()) {
               throw new ProtocolException("Fragmented Control Frame [" + OpCode.name(frame.getOpCode()) + "]");
            }

            if (payloadLength > 125) {
               throw new ProtocolException("Invalid control frame payload length, [" + payloadLength + "] cannot exceed [125]");
            }

            if (frame.isRsv1()) {
               throw new ProtocolException("Cannot have RSV1==true on Control frames");
            }

            if (frame.isRsv2()) {
               throw new ProtocolException("Cannot have RSV2==true on Control frames");
            }

            if (frame.isRsv3()) {
               throw new ProtocolException("Cannot have RSV3==true on Control frames");
            }
         } else {
            if (frame.isRsv1() && !coreSession.isRsv1Used()) {
               throw new ProtocolException("RSV1 not allowed to be set");
            }

            if (frame.isRsv2() && !coreSession.isRsv2Used()) {
               throw new ProtocolException("RSV2 not allowed to be set");
            }

            if (frame.isRsv3() && !coreSession.isRsv3Used()) {
               throw new ProtocolException("RSV3 not allowed to be set");
            }
         }
      }
   }
}
