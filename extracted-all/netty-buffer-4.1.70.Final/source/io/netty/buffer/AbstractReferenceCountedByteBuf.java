package io.netty.buffer;

import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf {
   private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCountedByteBuf.class, "refCnt");
   private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      AbstractReferenceCountedByteBuf.class, "refCnt"
   );
   private static final ReferenceCountUpdater<AbstractReferenceCountedByteBuf> updater = new ReferenceCountUpdater<AbstractReferenceCountedByteBuf>() {
      protected AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> updater() {
         return AbstractReferenceCountedByteBuf.AIF_UPDATER;
      }

      protected long unsafeOffset() {
         return AbstractReferenceCountedByteBuf.REFCNT_FIELD_OFFSET;
      }
   };
   private volatile int refCnt = updater.initialValue();

   protected AbstractReferenceCountedByteBuf(int maxCapacity) {
      super(maxCapacity);
   }

   @Override
   boolean isAccessible() {
      return updater.isLiveNonVolatile(this);
   }

   public int refCnt() {
      return updater.refCnt(this);
   }

   protected final void setRefCnt(int refCnt) {
      updater.setRefCnt(this, refCnt);
   }

   protected final void resetRefCnt() {
      updater.resetRefCnt(this);
   }

   @Override
   public ByteBuf retain() {
      return (ByteBuf)updater.retain(this);
   }

   @Override
   public ByteBuf retain(int increment) {
      return (ByteBuf)updater.retain(this, increment);
   }

   @Override
   public ByteBuf touch() {
      return this;
   }

   @Override
   public ByteBuf touch(Object hint) {
      return this;
   }

   public boolean release() {
      return this.handleRelease(updater.release(this));
   }

   public boolean release(int decrement) {
      return this.handleRelease(updater.release(this, decrement));
   }

   private boolean handleRelease(boolean result) {
      if (result) {
         this.deallocate();
      }

      return result;
   }

   protected abstract void deallocate();
}
