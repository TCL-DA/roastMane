package org.eclipse.jetty.websocket.common;

import java.nio.ByteBuffer;
import org.eclipse.jetty.websocket.api.Frame;
import org.eclipse.jetty.websocket.api.Frame.Type;

public class JettyWebSocketFrame implements Frame {
   private final org.eclipse.jetty.websocket.core.Frame frame;

   public JettyWebSocketFrame(org.eclipse.jetty.websocket.core.Frame frame) {
      this.frame = frame;
   }

   public byte[] getMask() {
      return this.frame.getMask();
   }

   public byte getOpCode() {
      return this.frame.getOpCode();
   }

   public ByteBuffer getPayload() {
      return this.frame.getPayload().asReadOnlyBuffer();
   }

   public int getPayloadLength() {
      return this.frame.getPayloadLength();
   }

   public Type getType() {
      return Type.from(this.getOpCode());
   }

   public boolean hasPayload() {
      return this.frame.hasPayload();
   }

   public boolean isFin() {
      return this.frame.isFin();
   }

   public boolean isMasked() {
      return this.frame.isMasked();
   }

   public boolean isRsv1() {
      return this.frame.isRsv1();
   }

   public boolean isRsv2() {
      return this.frame.isRsv2();
   }

   public boolean isRsv3() {
      return this.frame.isRsv3();
   }

   @Override
   public String toString() {
      return this.frame.toString();
   }
}
