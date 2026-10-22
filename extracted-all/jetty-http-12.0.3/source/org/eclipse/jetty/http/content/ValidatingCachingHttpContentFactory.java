package org.eclipse.jetty.http.content;

import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.thread.Scheduler;

public class ValidatingCachingHttpContentFactory extends CachingHttpContentFactory implements Runnable {
   private final Scheduler _scheduler;
   private final long _sweepDelay;
   private final long _validationTime;
   private final long _maxCacheIdleTime;

   public ValidatingCachingHttpContentFactory(
      @Name("authority") HttpContent.Factory authority, @Name("validationPeriod") long validationPeriod, @Name("bufferPool") ByteBufferPool bufferPool
   ) {
      this(authority, validationPeriod, bufferPool, null, -1L, -1L);
   }

   public ValidatingCachingHttpContentFactory(
      @Name("authority") HttpContent.Factory authority,
      @Name("validationPeriod") long validationPeriod,
      @Name("byteBufferPool") ByteBufferPool bufferPool,
      @Name("scheduler") Scheduler scheduler,
      @Name("sweepPeriod") long sweepPeriod,
      @Name("idleTimeout") long idleTimeout
   ) {
      super(authority, bufferPool);
      this._validationTime = validationPeriod;
      this._scheduler = scheduler;
      this._sweepDelay = sweepPeriod;
      this._maxCacheIdleTime = idleTimeout;
      if (scheduler != null && sweepPeriod > 0L) {
         this.schedule();
      }
   }

   @Override
   protected boolean isCacheable(HttpContent httpContent) {
      return httpContent == null ? this._validationTime != 0L : super.isCacheable(httpContent);
   }

   private void schedule() {
      this._scheduler.schedule(this, this._sweepDelay, TimeUnit.MILLISECONDS);
   }

   @Override
   public void run() {
      try {
         ConcurrentMap<String, CachingHttpContentFactory.CachingHttpContent> cache = this.getCache();

         for (Entry<String, CachingHttpContentFactory.CachingHttpContent> entry : cache.entrySet()) {
            CachingHttpContentFactory.CachingHttpContent value = entry.getValue();
            if (this._maxCacheIdleTime > 0L && NanoTime.since(value.getLastAccessedNanos()) > TimeUnit.MILLISECONDS.toNanos(this._maxCacheIdleTime)) {
               this.removeFromCache(value);
            } else if (!value.isValid()) {
               this.removeFromCache(value);
            }
         }
      } finally {
         this.schedule();
      }
   }

   @Override
   protected CachingHttpContentFactory.CachingHttpContent newCachedContent(String p, HttpContent httpContent) {
      return new ValidatingCachingHttpContentFactory.ValidatingCachedContent(p, httpContent, this._validationTime);
   }

   @Override
   protected CachingHttpContentFactory.CachingHttpContent newNotFoundContent(String p) {
      return new ValidatingCachingHttpContentFactory.ValidatingNotFoundContent(p, this._validationTime);
   }

   protected class ValidatingCachedContent extends CachingHttpContentFactory.CachedHttpContent {
      private final long _validationTime;
      private final AtomicLong _lastValidated = new AtomicLong();

      public ValidatingCachedContent(String key, HttpContent httpContent, long validationTime) {
         super(key, httpContent);
         this._lastValidated.set(NanoTime.now());
         this._validationTime = validationTime;
      }

      @Override
      public boolean isValid() {
         if (this._validationTime < 0L) {
            return true;
         } else {
            if (this._validationTime > 0L) {
               long now = NanoTime.now();
               if (this._lastValidated
                     .updateAndGet(lastChecked -> NanoTime.elapsed(lastChecked, now) > TimeUnit.MILLISECONDS.toNanos(this._validationTime) ? now : lastChecked)
                  != now) {
                  return true;
               }
            }

            return Objects.equals(this.getLastModifiedInstant(), this.getWrapped().getLastModifiedInstant());
         }
      }
   }

   protected static class ValidatingNotFoundContent extends CachingHttpContentFactory.NotFoundHttpContent {
      private final long _validationTime;
      private final AtomicLong _lastValidated = new AtomicLong();

      public ValidatingNotFoundContent(String key, long validationTime) {
         super(key);
         this._validationTime = validationTime;
         this._lastValidated.set(NanoTime.now());
      }

      @Override
      public boolean isValid() {
         if (this._validationTime < 0L) {
            return true;
         } else {
            return this._validationTime > 0L ? NanoTime.since(this._lastValidated.get()) < TimeUnit.MILLISECONDS.toNanos(this._validationTime) : false;
         }
      }
   }
}
