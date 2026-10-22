package org.glassfish.jersey.internal.util.collection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Deprecated
public interface TransferQueue<E> extends BlockingQueue<E> {
   boolean tryTransfer(E var1);

   void transfer(E var1) throws InterruptedException;

   boolean tryTransfer(E var1, long var2, TimeUnit var4) throws InterruptedException;

   boolean hasWaitingConsumer();

   int getWaitingConsumerCount();
}
