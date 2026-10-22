package org.eclipse.jetty.websocket.core.internal;

import java.nio.ByteBuffer;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.core.Configuration;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.OpCode;
import org.eclipse.jetty.websocket.core.exception.MessageTooLargeException;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;
import org.eclipse.jetty.websocket.core.exception.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {
   private static final Logger LOG = LoggerFactory.getLogger(Parser.class);
   private final ByteBufferPool bufferPool;
   private final Configuration configuration;
   private Parser.State state = Parser.State.START;
   private byte firstByte;
   private int cursor;
   private byte[] mask;
   private int payloadLength;
   private RetainableByteBuffer aggregate;

   public Parser(ByteBufferPool bufferPool) {
      this(bufferPool, new Configuration.ConfigurationCustomizer());
   }

   public Parser(ByteBufferPool bufferPool, Configuration configuration) {
      this.bufferPool = bufferPool;
      this.configuration = configuration;
   }

   public int getPayloadLength() {
      return this.payloadLength;
   }

   public void reset() {
      this.state = Parser.State.START;
      this.firstByte = 0;
      this.mask = null;
      this.cursor = 0;
      this.aggregate = null;
      this.payloadLength = 0;
   }

   public Frame.Parsed parse(ByteBuffer buffer) throws WebSocketException {
      try {
         while (buffer.hasRemaining()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} Parsing {}", this, BufferUtil.toDetailString(buffer));
            }

            switch (this.state) {
               case START:
                  this.firstByte = buffer.get();
                  this.state = Parser.State.PAYLOAD_LEN;
                  break;
               case PAYLOAD_LEN:
                  byte bxx = buffer.get();
                  if ((bxx & 128) != 0) {
                     this.mask = new byte[4];
                  }

                  this.payloadLength = (byte)(127 & bxx);
                  if (this.payloadLength == 127) {
                     this.payloadLength = 0;
                     this.state = Parser.State.PAYLOAD_LEN_BYTES;
                     this.cursor = 8;
                  } else if (this.payloadLength == 126) {
                     this.payloadLength = 0;
                     this.state = Parser.State.PAYLOAD_LEN_BYTES;
                     this.cursor = 2;
                  } else if (this.mask != null) {
                     this.state = Parser.State.MASK;
                  } else {
                     if (this.payloadLength == 0) {
                        this.state = Parser.State.START;
                        return this.newFrame(this.firstByte, this.mask, null, null);
                     }

                     this.state = Parser.State.PAYLOAD;
                  }
                  break;
               case PAYLOAD_LEN_BYTES:
                  byte bx = buffer.get();
                  this.cursor--;
                  long longLengthAccumulator = (long)this.payloadLength;
                  longLengthAccumulator |= (long)(bx & 255) << 8 * this.cursor;
                  if (longLengthAccumulator > 2147483647L || longLengthAccumulator < 0L) {
                     throw new MessageTooLargeException("Frame payload exceeded integer max value");
                  }

                  this.payloadLength = Math.toIntExact(longLengthAccumulator);
                  if (this.cursor == 0) {
                     if (this.mask != null) {
                        this.state = Parser.State.MASK;
                     } else if (this.payloadLength != 0) {
                        this.state = Parser.State.PAYLOAD;
                     } else {
                        this.state = Parser.State.START;
                        return this.newFrame(this.firstByte, this.mask, null, null);
                     }
                  }
                  break;
               case MASK:
                  if (buffer.remaining() < 4) {
                     this.state = Parser.State.MASK_BYTES;
                     this.cursor = 4;
                  } else {
                     buffer.get(this.mask, 0, 4);
                     if (this.payloadLength != 0) {
                        this.state = Parser.State.PAYLOAD;
                     } else {
                        this.state = Parser.State.START;
                        return this.newFrame(this.firstByte, this.mask, null, null);
                     }
                  }
                  break;
               case MASK_BYTES:
                  byte b = buffer.get();
                  this.mask[4 - this.cursor] = b;
                  this.cursor--;
                  if (this.cursor != 0) {
                     break;
                  }

                  if (this.payloadLength != 0) {
                     this.state = Parser.State.PAYLOAD;
                     break;
                  } else {
                     this.state = Parser.State.START;
                     return this.newFrame(this.firstByte, this.mask, null, null);
                  }
               case PAYLOAD:
               case FRAGMENT:
                  if (this.aggregate == null) {
                     this.checkFrameSize(OpCode.getOpCode(this.firstByte), this.payloadLength);
                  }

                  Frame.Parsed frame = this.parsePayload(buffer);
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("{} parsed {}", this, frame);
                  }

                  return frame;
               default:
                  throw new IllegalStateException();
            }
         }

         return null;
      } catch (Throwable var9) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} Parse Error {}", new Object[]{this, BufferUtil.toDetailString(buffer), var9});
         }

         buffer.position(buffer.limit());
         if (!(var9 instanceof WebSocketException wse)) {
            wse = new WebSocketException(var9);
         }

         throw wse;
      } finally {
         if (this.state == Parser.State.START) {
            this.reset();
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("{} Parse exit", this);
         }
      }
   }

   protected void checkFrameSize(byte opcode, int payloadLength) throws MessageTooLargeException, ProtocolException {
      if (payloadLength < 0) {
         throw new IllegalArgumentException("Invalid payloadLength");
      } else {
         if (OpCode.isControlFrame(opcode)) {
            if (payloadLength > 125) {
               throw new ProtocolException("Invalid control frame payload length, [" + payloadLength + "] cannot exceed [125]");
            }
         } else {
            long maxFrameSize = this.configuration.getMaxFrameSize();
            if (!this.configuration.isAutoFragment() && maxFrameSize > 0L && (long)payloadLength > maxFrameSize) {
               throw new MessageTooLargeException("Cannot handle payload lengths larger than " + maxFrameSize);
            }
         }
      }
   }

   protected Frame.Parsed newFrame(byte firstByte, byte[] mask, ByteBuffer payload, Runnable releaser) {
      byte opcode = OpCode.getOpCode(firstByte);
      if (!OpCode.isKnown(opcode)) {
         throw new ProtocolException("Unknown opcode: " + opcode);
      } else {
         boolean fin = (firstByte & 128) != 0;
         if (OpCode.isControlFrame(opcode) && !fin) {
            throw new ProtocolException("Fragmented Control Frame [" + OpCode.name(opcode) + "]");
         } else {
            return new Frame.Parsed(firstByte, mask, payload, releaser);
         }
      }
   }

   private Frame.Parsed autoFragment(ByteBuffer buffer, int fragmentSize) {
      this.payloadLength -= fragmentSize;
      byte[] nextMask = null;
      if (this.mask != null) {
         int shift = fragmentSize % 4;
         nextMask = new byte[]{this.mask[shift % 4], this.mask[(1 + shift) % 4], this.mask[(2 + shift) % 4], this.mask[(3 + shift) % 4]};
      }

      ByteBuffer content = buffer.slice();
      content.limit(fragmentSize);
      buffer.position(buffer.position() + fragmentSize);
      Frame.Parsed frame = this.newFrame((byte)(this.firstByte & 127), this.mask, content, null);
      this.mask = nextMask;
      this.firstByte = (byte)(this.firstByte & 128 | 0);
      this.state = Parser.State.FRAGMENT;
      return frame;
   }

   private Frame.Parsed parsePayload(ByteBuffer buffer) {
      if (this.payloadLength == 0) {
         return null;
      } else if (BufferUtil.isEmpty(buffer)) {
         return null;
      } else {
         int available = buffer.remaining();
         boolean isDataFrame = OpCode.isDataFrame(OpCode.getOpCode(this.firstByte));
         long maxFrameSize = this.configuration.getMaxFrameSize();
         if (maxFrameSize > 0L && isDataFrame && (long)this.payloadLength > maxFrameSize) {
            return this.autoFragment(buffer, (int)Math.min((long)available, maxFrameSize));
         } else if (this.aggregate == null) {
            if (available < this.payloadLength) {
               if (this.configuration.isAutoFragment() && isDataFrame) {
                  return this.autoFragment(buffer, available);
               } else {
                  this.aggregate = this.bufferPool.acquire(this.payloadLength, false);
                  BufferUtil.append(this.aggregate.getByteBuffer(), buffer);
                  return null;
               }
            } else if (available == this.payloadLength) {
               Frame.Parsed frame = this.newFrame(this.firstByte, this.mask, buffer.slice(), null);
               buffer.position(buffer.limit());
               this.state = Parser.State.START;
               return frame;
            } else {
               int limit = buffer.limit();
               int end = buffer.position() + this.payloadLength;
               buffer.limit(end);
               Frame.Parsed frame = this.newFrame(this.firstByte, this.mask, buffer.slice(), null);
               buffer.position(end);
               buffer.limit(limit);
               this.state = Parser.State.START;
               return frame;
            }
         } else {
            int aggregated = this.aggregate.remaining();
            int expecting = this.payloadLength - aggregated;
            if (available < expecting) {
               BufferUtil.append(this.aggregate.getByteBuffer(), buffer);
               return null;
            } else if (available == expecting) {
               BufferUtil.append(this.aggregate.getByteBuffer(), buffer);
               this.state = Parser.State.START;
               RetainableByteBuffer aggregate = this.aggregate;
               return this.newFrame(this.firstByte, this.mask, aggregate.getByteBuffer(), aggregate::release);
            } else {
               int limit = buffer.limit();
               buffer.limit(buffer.position() + expecting);
               BufferUtil.append(this.aggregate.getByteBuffer(), buffer);
               buffer.limit(limit);
               this.state = Parser.State.START;
               RetainableByteBuffer aggregate = this.aggregate;
               return this.newFrame(this.firstByte, this.mask, aggregate.getByteBuffer(), aggregate::release);
            }
         }
      }
   }

   @Override
   public String toString() {
      return String.format(
         "Parser@%x[s=%s,c=%d,o=0x%x,m=%s,l=%d]",
         this.hashCode(),
         this.state,
         this.cursor,
         this.firstByte,
         this.mask == null ? "-" : StringUtil.toHexString(this.mask),
         this.payloadLength
      );
   }

   private static enum State {
      START,
      PAYLOAD_LEN,
      PAYLOAD_LEN_BYTES,
      MASK,
      MASK_BYTES,
      PAYLOAD,
      FRAGMENT;
   }
}
