package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

final class VoidChannelGroupFuture implements ChannelGroupFuture {
   private static final Iterator<ChannelFuture> EMPTY = Collections.<ChannelFuture>emptyList().iterator();
   private final ChannelGroup group;

   VoidChannelGroupFuture(ChannelGroup group) {
      this.group = group;
   }

   @Override
   public ChannelGroup group() {
      return this.group;
   }

   @Override
   public ChannelFuture find(Channel channel) {
      return null;
   }

   @Override
   public boolean isSuccess() {
      return false;
   }

   @Override
   public ChannelGroupException cause() {
      return null;
   }

   @Override
   public boolean isPartialSuccess() {
      return false;
   }

   @Override
   public boolean isPartialFailure() {
      return false;
   }

   @Override
   public ChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
      throw reject();
   }

   @Override
   public ChannelGroupFuture await() {
      throw reject();
   }

   @Override
   public ChannelGroupFuture awaitUninterruptibly() {
      throw reject();
   }

   @Override
   public ChannelGroupFuture syncUninterruptibly() {
      throw reject();
   }

   @Override
   public ChannelGroupFuture sync() {
      throw reject();
   }

   @Override
   public Iterator<ChannelFuture> iterator() {
      return EMPTY;
   }

   public boolean isCancellable() {
      return false;
   }

   public boolean await(long timeout, TimeUnit unit) {
      throw reject();
   }

   public boolean await(long timeoutMillis) {
      throw reject();
   }

   public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
      throw reject();
   }

   public boolean awaitUninterruptibly(long timeoutMillis) {
      throw reject();
   }

   public Void getNow() {
      return null;
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      return false;
   }

   public boolean isCancelled() {
      return false;
   }

   public boolean isDone() {
      return false;
   }

   public Void get() {
      throw reject();
   }

   public Void get(long timeout, TimeUnit unit) {
      throw reject();
   }

   private static RuntimeException reject() {
      return new IllegalStateException("void future");
   }
}
