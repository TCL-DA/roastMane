package org.eclipse.jetty.websocket.core.internal;

import java.io.IOException;
import java.util.Map;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.websocket.core.AbstractExtension;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.WebSocketComponents;
import org.eclipse.jetty.websocket.core.exception.BadPayloadException;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;
import org.eclipse.jetty.websocket.core.util.FrameValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationExtension extends AbstractExtension {
   private static final Logger LOG = LoggerFactory.getLogger(ValidationExtension.class);
   private FrameSequence incomingSequence = null;
   private FrameSequence outgoingSequence = null;
   private boolean incomingFrameValidation = false;
   private boolean outgoingFrameValidation = false;
   private NullAppendable incomingUtf8Validation = null;
   private NullAppendable outgoingUtf8Validation = null;
   private byte continuedOutOpCode = -1;
   private byte continuedInOpCode = -1;

   @Override
   public String getName() {
      return "@validation";
   }

   @Override
   public void onFrame(Frame frame, Callback callback) {
      try {
         if (this.incomingSequence != null) {
            this.incomingSequence.check(frame.getOpCode(), frame.isFin());
         }

         if (this.incomingFrameValidation) {
            FrameValidation.assertValidIncoming(frame, this.getCoreSession());
         }

         if (this.incomingUtf8Validation != null) {
            this.validateUTF8(frame, this.incomingUtf8Validation, this.continuedInOpCode);
         }

         this.continuedInOpCode = this.recordLastOpCode(frame, this.continuedInOpCode);
         this.nextIncomingFrame(frame, callback);
      } catch (Throwable var4) {
         callback.failed(var4);
      }
   }

   @Override
   public void sendFrame(Frame frame, Callback callback, boolean batch) {
      try {
         if (this.outgoingSequence != null) {
            this.outgoingSequence.check(frame.getOpCode(), frame.isFin());
         }

         if (this.outgoingFrameValidation) {
            FrameValidation.assertValidOutgoing(frame, this.getCoreSession());
         }

         if (this.outgoingUtf8Validation != null) {
            this.validateUTF8(frame, this.outgoingUtf8Validation, this.continuedOutOpCode);
         }

         this.continuedOutOpCode = this.recordLastOpCode(frame, this.continuedOutOpCode);
         this.nextOutgoingFrame(frame, callback, batch);
      } catch (Throwable var5) {
         callback.failed(var5);
      }
   }

   @Override
   public void init(ExtensionConfig config, WebSocketComponents components) {
      super.init(config, components);
      Map<String, String> parameters = config.getParameters();
      if (parameters.containsKey("outgoing-sequence")) {
         this.outgoingSequence = new FrameSequence();
      }

      if (parameters.containsKey("incoming-sequence")) {
         this.incomingSequence = new FrameSequence();
      }

      if (parameters.containsKey("incoming-frame")) {
         this.incomingFrameValidation = true;
      }

      if (parameters.containsKey("outgoing-frame")) {
         this.outgoingFrameValidation = true;
      }

      if (parameters.containsKey("incoming-utf8")) {
         this.incomingUtf8Validation = new NullAppendable();
      }

      if (parameters.containsKey("outgoing-utf8")) {
         this.outgoingUtf8Validation = new NullAppendable();
      }
   }

   private void validateUTF8(Frame frame, NullAppendable appendable, byte continuedOpCode) throws IOException {
      if (!frame.isControlFrame() && (frame.getOpCode() == 1 || continuedOpCode == 1)) {
         if (frame.hasPayload()) {
            appendable.append(frame.getPayload().slice());
         }

         if (frame.isFin()) {
            appendable.complete();
         }

         if (appendable.hasCodingErrors()) {
            throw new BadPayloadException("Invalid UTF-8 encountered");
         }
      }
   }

   public byte recordLastOpCode(Frame frame, byte prevOpcode) throws ProtocolException {
      byte opcode = prevOpcode;
      boolean fin = frame.isFin();
      if (fin) {
         opcode = -1;
      } else if (prevOpcode != 0) {
         opcode = frame.getOpCode();
      }

      return opcode;
   }
}
