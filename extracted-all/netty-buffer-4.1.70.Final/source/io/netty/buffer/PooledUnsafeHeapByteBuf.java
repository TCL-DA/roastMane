package io.netty.buffer;

import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ObjectPool.Handle;
import io.netty.util.internal.ObjectPool.ObjectCreator;

final class PooledUnsafeHeapByteBuf extends PooledHeapByteBuf {
   private static final ObjectPool<PooledUnsafeHeapByteBuf> RECYCLER = ObjectPool.newPool(new ObjectCreator<PooledUnsafeHeapByteBuf>() {
      public PooledUnsafeHeapByteBuf newObject(Handle<PooledUnsafeHeapByteBuf> handle) {
         return new PooledUnsafeHeapByteBuf(handle, 0);
      }
   });

   static PooledUnsafeHeapByteBuf newUnsafeInstance(int maxCapacity) {
      PooledUnsafeHeapByteBuf buf = (PooledUnsafeHeapByteBuf)RECYCLER.get();
      buf.reuse(maxCapacity);
      return buf;
   }

   private PooledUnsafeHeapByteBuf(Handle<PooledUnsafeHeapByteBuf> recyclerHandle, int maxCapacity) {
      super(recyclerHandle, maxCapacity);
   }

   @Override
   protected byte _getByte(int index) {
      return UnsafeByteBufUtil.getByte(this.memory, this.idx(index));
   }

   @Override
   protected short _getShort(int index) {
      return UnsafeByteBufUtil.getShort(this.memory, this.idx(index));
   }

   @Override
   protected short _getShortLE(int index) {
      return UnsafeByteBufUtil.getShortLE(this.memory, this.idx(index));
   }

   @Override
   protected int _getUnsignedMedium(int index) {
      return UnsafeByteBufUtil.getUnsignedMedium(this.memory, this.idx(index));
   }

   @Override
   protected int _getUnsignedMediumLE(int index) {
      return UnsafeByteBufUtil.getUnsignedMediumLE(this.memory, this.idx(index));
   }

   @Override
   protected int _getInt(int index) {
      return UnsafeByteBufUtil.getInt(this.memory, this.idx(index));
   }

   @Override
   protected int _getIntLE(int index) {
      return UnsafeByteBufUtil.getIntLE(this.memory, this.idx(index));
   }

   @Override
   protected long _getLong(int index) {
      return UnsafeByteBufUtil.getLong(this.memory, this.idx(index));
   }

   @Override
   protected long _getLongLE(int index) {
      return UnsafeByteBufUtil.getLongLE(this.memory, this.idx(index));
   }

   @Override
   protected void _setByte(int index, int value) {
      UnsafeByteBufUtil.setByte(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setShort(int index, int value) {
      UnsafeByteBufUtil.setShort(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setShortLE(int index, int value) {
      UnsafeByteBufUtil.setShortLE(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setMedium(int index, int value) {
      UnsafeByteBufUtil.setMedium(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setMediumLE(int index, int value) {
      UnsafeByteBufUtil.setMediumLE(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setInt(int index, int value) {
      UnsafeByteBufUtil.setInt(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setIntLE(int index, int value) {
      UnsafeByteBufUtil.setIntLE(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setLong(int index, long value) {
      UnsafeByteBufUtil.setLong(this.memory, this.idx(index), value);
   }

   @Override
   protected void _setLongLE(int index, long value) {
      UnsafeByteBufUtil.setLongLE(this.memory, this.idx(index), value);
   }

   @Override
   public ByteBuf setZero(int index, int length) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.checkIndex(index, length);
         UnsafeByteBufUtil.setZero(this.memory, this.idx(index), length);
         return this;
      } else {
         return super.setZero(index, length);
      }
   }

   @Override
   public ByteBuf writeZero(int length) {
      if (PlatformDependent.javaVersion() >= 7) {
         this.ensureWritable(length);
         int wIndex = this.writerIndex;
         UnsafeByteBufUtil.setZero(this.memory, this.idx(wIndex), length);
         this.writerIndex = wIndex + length;
         return this;
      } else {
         return super.writeZero(length);
      }
   }

   @Deprecated
   @Override
   protected SwappedByteBuf newSwappedByteBuf() {
      return (SwappedByteBuf)(PlatformDependent.isUnaligned() ? new UnsafeHeapSwappedByteBuf(this) : super.newSwappedByteBuf());
   }
}
