package io.netty.util.internal.shaded.org.jctools.queues;

public final class IndexedQueueSizeUtil {
   public static int size(IndexedQueueSizeUtil.IndexedQueue iq) {
      long after = iq.lvConsumerIndex();

      long before;
      long currentProducerIndex;
      do {
         before = after;
         currentProducerIndex = iq.lvProducerIndex();
         after = iq.lvConsumerIndex();
      } while (before != after);

      long size = currentProducerIndex - after;
      if (size > 2147483647L) {
         return Integer.MAX_VALUE;
      } else if (size < 0L) {
         return 0;
      } else {
         return iq.capacity() != -1 && size > (long)iq.capacity() ? iq.capacity() : (int)size;
      }
   }

   public static boolean isEmpty(IndexedQueueSizeUtil.IndexedQueue iq) {
      return iq.lvConsumerIndex() >= iq.lvProducerIndex();
   }

   public interface IndexedQueue {
      long lvConsumerIndex();

      long lvProducerIndex();

      int capacity();
   }
}
