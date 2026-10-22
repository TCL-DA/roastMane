package org.eclipse.jetty.io.content;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.util.MathUtils;
import org.eclipse.jetty.util.thread.AutoLock;

public class ContentSourcePublisher implements Publisher<Content.Chunk> {
   private final Content.Source content;

   public ContentSourcePublisher(Content.Source content) {
      this.content = content;
   }

   @Override
   public void subscribe(Subscriber<? super Content.Chunk> subscriber) {
      subscriber.onSubscribe(new ContentSourcePublisher.SubscriptionImpl(this.content, subscriber));
   }

   private static class SubscriptionImpl implements Subscription {
      private final AutoLock lock = new AutoLock();
      private final Content.Source content;
      private final Subscriber<? super Content.Chunk> subscriber;
      private long demand;
      private boolean stalled;
      private boolean cancelled;
      private boolean terminated;

      public SubscriptionImpl(Content.Source content, Subscriber<? super Content.Chunk> subscriber) {
         this.content = content;
         this.subscriber = subscriber;
         this.stalled = true;
      }

      @Override
      public void request(long n) {
         boolean process = false;
         Throwable failure = null;
         AutoLock ignored = this.lock.lock();

         label57: {
            try {
               if (!this.cancelled && !this.terminated) {
                  if (n <= 0L) {
                     this.terminated = true;
                     failure = new IllegalArgumentException("invalid demand " + n);
                  }

                  this.demand = MathUtils.cappedAdd(this.demand, n);
                  if (this.stalled) {
                     this.stalled = false;
                     process = true;
                  }
                  break label57;
               }
            } catch (Throwable var9) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (ignored != null) {
               ignored.close();
            }

            return;
         }

         if (ignored != null) {
            ignored.close();
         }

         if (failure != null) {
            this.subscriber.onError(failure);
         } else if (process) {
            this.process();
         }
      }

      @Override
      public void cancel() {
         AutoLock ignored = this.lock.lock();

         try {
            this.cancelled = true;
         } catch (Throwable var5) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      private void process() {
         while (true) {
            AutoLock ignored = this.lock.lock();

            label76: {
               try {
                  if (this.demand <= 0L) {
                     this.stalled = true;
                     break label76;
                  }

                  this.demand--;
               } catch (Throwable var8) {
                  if (ignored != null) {
                     try {
                        ignored.close();
                     } catch (Throwable var6) {
                        var8.addSuppressed(var6);
                     }
                  }

                  throw var8;
               }

               if (ignored != null) {
                  ignored.close();
               }

               Content.Chunk chunk = this.content.read();
               if (chunk == null) {
                  AutoLock ignoredx = this.lock.lock();

                  try {
                     this.demand++;
                     this.stalled = true;
                  } catch (Throwable var7) {
                     if (ignoredx != null) {
                        try {
                           ignoredx.close();
                        } catch (Throwable var5) {
                           var7.addSuppressed(var5);
                        }
                     }

                     throw var7;
                  }

                  if (ignoredx != null) {
                     ignoredx.close();
                  }

                  this.content.demand(this::process);
                  return;
               }

               if (Content.Chunk.isFailure(chunk)) {
                  this.terminate();
                  this.subscriber.onError(chunk.getFailure());
                  return;
               }

               this.subscriber.onNext(chunk);
               chunk.release();
               if (!chunk.isLast()) {
                  continue;
               }

               this.terminate();
               this.subscriber.onComplete();
               return;
            }

            if (ignored != null) {
               ignored.close();
            }

            return;
         }
      }

      private void terminate() {
         AutoLock ignored = this.lock.lock();

         try {
            this.terminated = true;
         } catch (Throwable var5) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignored != null) {
            ignored.close();
         }
      }
   }
}
