package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.ByteBufferPool.NonPooling;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.compression.CompressionPool;
import org.eclipse.jetty.util.compression.InflaterPool;

public class GZIPContentDecoder implements Destroyable {
   private static final long UINT_MAX = 4294967295L;
   private final List<RetainableByteBuffer> _inflateds = new ArrayList<>();
   private final ByteBufferPool _pool;
   private final int _bufferSize;
   private CompressionPool<Inflater>.Entry _inflaterEntry;
   private Inflater _inflater;
   private GZIPContentDecoder.State _state;
   private int _size;
   private long _value;
   private byte _flags;
   private RetainableByteBuffer _inflated;

   public GZIPContentDecoder() {
      this(null, 2048);
   }

   public GZIPContentDecoder(int bufferSize) {
      this(null, bufferSize);
   }

   public GZIPContentDecoder(ByteBufferPool byteBufferPool, int bufferSize) {
      this(new InflaterPool(0, true), byteBufferPool, bufferSize);
   }

   public GZIPContentDecoder(InflaterPool inflaterPool, ByteBufferPool byteBufferPool, int bufferSize) {
      this._inflaterEntry = inflaterPool.acquire();
      this._inflater = (Inflater)this._inflaterEntry.get();
      this._bufferSize = bufferSize;
      this._pool = (ByteBufferPool)(byteBufferPool != null ? byteBufferPool : new NonPooling());
      this.reset();
   }

   public RetainableByteBuffer decode(ByteBuffer compressed) {
      this.decodeChunks(compressed);
      if (this._inflateds.isEmpty()) {
         if (this._inflated != null
            && this._inflated.hasRemaining()
            && this._state != GZIPContentDecoder.State.CRC
            && this._state != GZIPContentDecoder.State.ISIZE) {
            RetainableByteBuffer result = this._inflated;
            this._inflated = null;
            return result;
         } else {
            return this.acquire(0);
         }
      } else {
         this._inflateds.add(this._inflated);
         this._inflated = null;
         int length = this._inflateds.stream().mapToInt(RetainableByteBuffer::remaining).sum();
         RetainableByteBuffer result = this.acquire(length);

         for (RetainableByteBuffer buffer : this._inflateds) {
            BufferUtil.append(result.getByteBuffer(), buffer.getByteBuffer());
            buffer.release();
         }

         this._inflateds.clear();
         return result;
      }
   }

   protected boolean decodedChunk(RetainableByteBuffer chunk) {
      chunk.retain();
      if (this._inflated != null) {
         this._inflateds.add(this._inflated);
      }

      this._inflated = chunk;
      return false;
   }

   protected void decodeChunks(ByteBuffer compressed) {
      RetainableByteBuffer buffer = null;

      try {
         label223:
         while (true) {
            switch (this._state) {
               case INITIAL:
                  this._state = GZIPContentDecoder.State.ID;
                  break;
               case FLAGS:
                  if ((this._flags & 4) == 4) {
                     this._state = GZIPContentDecoder.State.EXTRA_LENGTH;
                     this._size = 0;
                     this._value = 0L;
                  } else if ((this._flags & 8) == 8) {
                     this._state = GZIPContentDecoder.State.NAME;
                  } else if ((this._flags & 16) == 16) {
                     this._state = GZIPContentDecoder.State.COMMENT;
                  } else {
                     if ((this._flags & 2) != 2) {
                        this._state = GZIPContentDecoder.State.DATA;
                        continue;
                     }

                     this._state = GZIPContentDecoder.State.HCRC;
                     this._size = 0;
                     this._value = 0L;
                  }
                  break;
               case DATA:
                  while (true) {
                     if (buffer == null) {
                        buffer = this.acquire(this._bufferSize);
                     }

                     try {
                        ByteBuffer decoded = buffer.getByteBuffer();
                        int pos = BufferUtil.flipToFill(decoded);
                        this._inflater.inflate(decoded);
                        BufferUtil.flipToFlush(decoded, pos);
                     } catch (DataFormatException var9) {
                        throw new ZipException(var9.getMessage());
                     }

                     if (buffer.hasRemaining()) {
                        boolean stop = this.decodedChunk(buffer);
                        buffer.release();
                        buffer = null;
                        if (stop) {
                           return;
                        }
                     } else if (this._inflater.needsInput()) {
                        if (!compressed.hasRemaining()) {
                           return;
                        }

                        this._inflater.setInput(compressed);
                     } else if (this._inflater.finished()) {
                        this._state = GZIPContentDecoder.State.CRC;
                        this._size = 0;
                        this._value = 0L;
                        continue label223;
                     }
                  }
            }

            if (!compressed.hasRemaining()) {
               return;
            }

            byte currByte = compressed.get();
            switch (this._state) {
               case ID:
                  this._value = this._value + (long)((currByte & 255) << 8 * this._size);
                  this._size++;
                  if (this._size == 2) {
                     if (this._value != 35615L) {
                        throw new ZipException("Invalid gzip bytes");
                     }

                     this._state = GZIPContentDecoder.State.CM;
                  }
                  break;
               case CM:
                  if ((currByte & 255) != 8) {
                     throw new ZipException("Invalid gzip compression method");
                  }

                  this._state = GZIPContentDecoder.State.FLG;
                  break;
               case FLG:
                  this._flags = currByte;
                  this._state = GZIPContentDecoder.State.MTIME;
                  this._size = 0;
                  this._value = 0L;
                  break;
               case MTIME:
                  this._size++;
                  if (this._size == 4) {
                     this._state = GZIPContentDecoder.State.XFL;
                  }
                  break;
               case XFL:
                  this._state = GZIPContentDecoder.State.OS;
                  break;
               case OS:
                  this._state = GZIPContentDecoder.State.FLAGS;
                  break;
               case FLAGS:
               case DATA:
               default:
                  throw new ZipException();
               case EXTRA_LENGTH:
                  this._value = this._value + (long)((currByte & 255) << 8 * this._size);
                  this._size++;
                  if (this._size == 2) {
                     this._state = GZIPContentDecoder.State.EXTRA;
                  }
                  break;
               case EXTRA:
                  this._value--;
                  if (this._value == 0L) {
                     this._flags &= -5;
                     this._state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case NAME:
                  if (currByte == 0) {
                     this._flags &= -9;
                     this._state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case COMMENT:
                  if (currByte == 0) {
                     this._flags &= -17;
                     this._state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case HCRC:
                  this._size++;
                  if (this._size == 2) {
                     this._flags &= -3;
                     this._state = GZIPContentDecoder.State.FLAGS;
                  }
                  break;
               case CRC:
                  this._value = this._value + (long)((currByte & 255) << 8 * this._size);
                  this._size++;
                  if (this._size == 4) {
                     this._state = GZIPContentDecoder.State.ISIZE;
                     this._size = 0;
                     this._value = 0L;
                  }
                  break;
               case ISIZE:
                  this._value = this._value | ((long)currByte & 255L) << 8 * this._size;
                  this._size++;
                  if (this._size == 4) {
                     if (this._value != (this._inflater.getBytesWritten() & 4294967295L)) {
                        throw new ZipException("Invalid input size");
                     }

                     this.reset();
                     return;
                  }
            }
         }
      } catch (ZipException var10) {
         throw new RuntimeException(var10);
      } finally {
         if (buffer != null) {
            buffer.release();
         }
      }
   }

   private void reset() {
      this._inflater.reset();
      this._state = GZIPContentDecoder.State.INITIAL;
      this._size = 0;
      this._value = 0L;
      this._flags = 0;
   }

   public void destroy() {
      this._inflaterEntry.release();
      this._inflaterEntry = null;
      this._inflater = null;
   }

   public boolean isFinished() {
      return this._state == GZIPContentDecoder.State.INITIAL;
   }

   public RetainableByteBuffer acquire(int capacity) {
      return capacity == 0 ? RetainableByteBuffer.EMPTY : this._pool.acquire(capacity, false);
   }

   private static enum State {
      INITIAL,
      ID,
      CM,
      FLG,
      MTIME,
      XFL,
      OS,
      FLAGS,
      EXTRA_LENGTH,
      EXTRA,
      NAME,
      COMMENT,
      HCRC,
      DATA,
      CRC,
      ISIZE;
   }
}
