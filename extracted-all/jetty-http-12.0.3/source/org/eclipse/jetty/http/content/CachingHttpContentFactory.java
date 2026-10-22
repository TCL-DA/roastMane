package org.eclipse.jetty.http.content;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.time.Instant;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.ByteBufferPool.NonPooling;
import org.eclipse.jetty.io.Retainable.ReferenceCounter;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingHttpContentFactory implements HttpContent.Factory {
   private static final Logger LOG = LoggerFactory.getLogger(CachingHttpContentFactory.class);
   private static final int DEFAULT_MAX_CACHED_FILE_SIZE = 134217728;
   private static final int DEFAULT_MAX_CACHED_FILES = 2048;
   private static final long DEFAULT_MAX_CACHE_SIZE = 268435456L;
   private final HttpContent.Factory _authority;
   private final ConcurrentHashMap<String, CachingHttpContentFactory.CachingHttpContent> _cache = new ConcurrentHashMap<>();
   private final AtomicLong _cachedSize = new AtomicLong();
   private final ByteBufferPool _bufferPool;
   private int _maxCachedFileSize = 134217728;
   private int _maxCachedFiles = 2048;
   private long _maxCacheSize = 268435456L;
   private boolean _useDirectByteBuffers = true;

   public CachingHttpContentFactory(HttpContent.Factory authority, ByteBufferPool bufferPool) {
      this._authority = authority;
      this._bufferPool = (ByteBufferPool)(bufferPool != null ? bufferPool : new NonPooling());
   }

   protected ConcurrentMap<String, CachingHttpContentFactory.CachingHttpContent> getCache() {
      return this._cache;
   }

   public long getCachedSize() {
      return this._cachedSize.get();
   }

   public int getCachedFiles() {
      return this._cache.size();
   }

   public int getMaxCachedFileSize() {
      return this._maxCachedFileSize;
   }

   public void setMaxCachedFileSize(int maxCachedFileSize) {
      this._maxCachedFileSize = maxCachedFileSize;
      this.shrinkCache();
   }

   public long getMaxCacheSize() {
      return this._maxCacheSize;
   }

   public void setMaxCacheSize(long maxCacheSize) {
      this._maxCacheSize = maxCacheSize;
      this.shrinkCache();
   }

   public int getMaxCachedFiles() {
      return this._maxCachedFiles;
   }

   public void setMaxCachedFiles(int maxCachedFiles) {
      this._maxCachedFiles = maxCachedFiles;
      this.shrinkCache();
   }

   public boolean isUseDirectByteBuffers() {
      return this._useDirectByteBuffers;
   }

   public void setUseDirectByteBuffers(boolean useDirectByteBuffers) {
      this._useDirectByteBuffers = useDirectByteBuffers;
   }

   private void shrinkCache() {
      for (int numCacheEntries = this._cache.size();
         numCacheEntries > 0 && (numCacheEntries > this._maxCachedFiles || this._cachedSize.get() > this._maxCacheSize);
         numCacheEntries = this._cache.size()
      ) {
         SortedSet<CachingHttpContentFactory.CachingHttpContent> sorted = new TreeSet<>((c1, c2) -> {
            long delta = NanoTime.elapsed(c2.getLastAccessedNanos(), c1.getLastAccessedNanos());
            if (delta != 0L) {
               return delta < 0L ? -1 : 1;
            } else {
               delta = c1.getContentLengthValue() - c2.getContentLengthValue();
               if (delta != 0L) {
                  return delta < 0L ? -1 : 1;
               } else {
                  return c1.getKey().compareTo(c2.getKey());
               }
            }
         });
         sorted.addAll(this._cache.values());

         for (CachingHttpContentFactory.CachingHttpContent content : sorted) {
            if (this._cache.size() > this._maxCachedFiles || this._cachedSize.get() > this._maxCacheSize) {
               this.removeFromCache(content);
               continue;
            }
         }
      }
   }

   protected void removeFromCache(CachingHttpContentFactory.CachingHttpContent content) {
      CachingHttpContentFactory.CachingHttpContent removed = this._cache.remove(content.getKey());
      if (removed != null) {
         removed.release();
         this._cachedSize.addAndGet(-removed.getBytesOccupied());
      }
   }

   public void flushCache() {
      for (CachingHttpContentFactory.CachingHttpContent content : this._cache.values()) {
         this.removeFromCache(content);
      }
   }

   protected boolean isCacheable(HttpContent httpContent) {
      if (httpContent == null) {
         return true;
      } else if (httpContent.getResource().isDirectory()) {
         return false;
      } else if (this._maxCachedFiles <= 0) {
         return false;
      } else {
         long len = httpContent.getBytesOccupied();
         return len <= (long)this._maxCachedFileSize && len <= this._maxCacheSize;
      }
   }

   @Override
   public HttpContent getContent(String path) throws IOException {
      CachingHttpContentFactory.CachingHttpContent cachingHttpContent = this._cache.get(path);
      if (cachingHttpContent != null) {
         cachingHttpContent.setLastAccessedNanos(NanoTime.now());
         if (cachingHttpContent.isValid()) {
            if (cachingHttpContent.retain()) {
               return cachingHttpContent instanceof CachingHttpContentFactory.NotFoundHttpContent ? null : cachingHttpContent;
            }
         } else {
            this.removeFromCache(cachingHttpContent);
         }
      }

      HttpContent httpContent = this._authority.getContent(path);
      if (!this.isCacheable(httpContent)) {
         return httpContent;
      } else {
         AtomicBoolean added = new AtomicBoolean();
         cachingHttpContent = this._cache
            .computeIfAbsent(
               path,
               key -> {
                  CachingHttpContentFactory.CachingHttpContent cachingContent = httpContent == null
                     ? this.newNotFoundContent(key)
                     : this.newCachedContent(key, httpContent);
                  added.set(true);
                  this._cachedSize.addAndGet(cachingContent.getBytesOccupied());
                  return cachingContent;
               }
            );
         if (!cachingHttpContent.retain()) {
            return httpContent;
         } else {
            if (added.get()) {
               this.shrinkCache();
            } else if (httpContent != null) {
               httpContent.release();
            }

            return cachingHttpContent instanceof CachingHttpContentFactory.NotFoundHttpContent ? null : cachingHttpContent;
         }
      }
   }

   protected CachingHttpContentFactory.CachingHttpContent newCachedContent(String p, HttpContent httpContent) {
      return new CachingHttpContentFactory.CachedHttpContent(p, httpContent);
   }

   protected CachingHttpContentFactory.CachingHttpContent newNotFoundContent(String p) {
      return new CachingHttpContentFactory.NotFoundHttpContent(p);
   }

   protected class CachedHttpContent extends HttpContent.Wrapper implements CachingHttpContentFactory.CachingHttpContent {
      private final RetainableByteBuffer _buffer;
      private final String _cacheKey;
      private final HttpField _etagField;
      private final long _contentLengthValue;
      private volatile long _lastAccessed;
      private final Set<CompressedContentFormat> _compressedFormats;
      private final String _lastModifiedValue;
      private final String _characterEncoding;
      private final MimeTypes.Type _mimeType;
      private final HttpField _contentLength;
      private final Instant _lastModifiedInstant;
      private final HttpField _lastModified;
      private final long _bytesOccupied;
      private final boolean _isValid;
      private final ReferenceCounter _referenceCount = new ReferenceCounter();

      public CachedHttpContent(String key, HttpContent httpContent) {
         super(httpContent);
         this._cacheKey = key;
         HttpField etagField = httpContent.getETag();
         String eTagValue = httpContent.getETagValue();
         if (StringUtil.isNotBlank(eTagValue)) {
            etagField = new PreEncodedHttpField(HttpHeader.ETAG, eTagValue);
         }

         this._etagField = etagField;
         this._contentLengthValue = httpContent.getContentLengthValue();
         boolean isValid = true;
         ByteBuffer byteBuffer = httpContent.getByteBuffer();
         RetainableByteBuffer buffer;
         if (byteBuffer == null) {
            buffer = CachingHttpContentFactory.this._bufferPool.acquire((int)this._contentLengthValue, CachingHttpContentFactory.this._useDirectByteBuffers);

            try {
               if (this._contentLengthValue <= (long)CachingHttpContentFactory.this._maxCachedFileSize) {
                  try (ReadableByteChannel readableByteChannel = httpContent.getResource().newReadableByteChannel()) {
                     byteBuffer = buffer.getByteBuffer();
                     int read = BufferUtil.readFrom(readableByteChannel, byteBuffer);
                     if ((long)read != this._contentLengthValue) {
                        buffer.release();
                        buffer = null;
                        isValid = false;
                     }
                  }
               }
            } catch (Throwable var14) {
               if (buffer != null) {
                  buffer.release();
               }

               buffer = null;
               isValid = false;
               CachingHttpContentFactory.LOG.warn("Failed to read Resource", var14);
            }
         } else {
            buffer = RetainableByteBuffer.wrap(byteBuffer);
         }

         this._buffer = buffer;
         this._isValid = isValid;
         this._bytesOccupied = httpContent.getBytesOccupied();
         this._lastModifiedValue = httpContent.getLastModifiedValue();
         this._characterEncoding = httpContent.getCharacterEncoding();
         this._compressedFormats = httpContent.getPreCompressedContentFormats();
         this._mimeType = httpContent.getMimeType();
         this._contentLength = httpContent.getContentLength();
         this._lastModifiedInstant = httpContent.getLastModifiedInstant();
         this._lastModified = httpContent.getLastModified();
         this._lastAccessed = NanoTime.now();
      }

      @Override
      public long getContentLengthValue() {
         return this._contentLengthValue;
      }

      @Override
      public ByteBuffer getByteBuffer() {
         return this._buffer == null ? null : this._buffer.getByteBuffer().asReadOnlyBuffer();
      }

      @Override
      public long getBytesOccupied() {
         return this._bytesOccupied;
      }

      @Override
      public long getLastAccessedNanos() {
         return this._lastAccessed;
      }

      @Override
      public void setLastAccessedNanos(long nanosTime) {
         this._lastAccessed = nanosTime;
      }

      @Override
      public String getKey() {
         return this._cacheKey;
      }

      @Override
      public boolean retain() {
         return this._referenceCount.tryRetain();
      }

      @Override
      public void release() {
         if (this._referenceCount.release()) {
            if (this._buffer != null) {
               this._buffer.release();
            }

            super.release();
         }
      }

      @Override
      public Set<CompressedContentFormat> getPreCompressedContentFormats() {
         return this._compressedFormats;
      }

      @Override
      public HttpField getETag() {
         return this._etagField;
      }

      @Override
      public String getETagValue() {
         return this._etagField == null ? null : this._etagField.getValue();
      }

      @Override
      public String getCharacterEncoding() {
         return this._characterEncoding;
      }

      @Override
      public MimeTypes.Type getMimeType() {
         return this._mimeType;
      }

      @Override
      public HttpField getContentLength() {
         return this._contentLength;
      }

      @Override
      public Instant getLastModifiedInstant() {
         return this._lastModifiedInstant;
      }

      @Override
      public HttpField getLastModified() {
         return this._lastModified;
      }

      @Override
      public String getLastModifiedValue() {
         return this._lastModifiedValue;
      }

      @Override
      public boolean isValid() {
         return this._isValid;
      }
   }

   protected interface CachingHttpContent extends HttpContent {
      long getLastAccessedNanos();

      void setLastAccessedNanos(long var1);

      String getKey();

      boolean isValid();

      boolean retain();
   }

   protected static class NotFoundHttpContent implements CachingHttpContentFactory.CachingHttpContent {
      private volatile long _lastAccessed;
      private final String _key;

      public NotFoundHttpContent(String key) {
         this._key = key;
         this._lastAccessed = NanoTime.now();
      }

      @Override
      public String getKey() {
         return this._key;
      }

      @Override
      public long getLastAccessedNanos() {
         return this._lastAccessed;
      }

      @Override
      public void setLastAccessedNanos(long nanosTime) {
         this._lastAccessed = nanosTime;
      }

      @Override
      public HttpField getContentType() {
         return null;
      }

      @Override
      public String getContentTypeValue() {
         return null;
      }

      @Override
      public String getCharacterEncoding() {
         return null;
      }

      @Override
      public MimeTypes.Type getMimeType() {
         return null;
      }

      @Override
      public HttpField getContentEncoding() {
         return null;
      }

      @Override
      public String getContentEncodingValue() {
         return null;
      }

      @Override
      public HttpField getContentLength() {
         return null;
      }

      @Override
      public long getContentLengthValue() {
         return 0L;
      }

      @Override
      public Instant getLastModifiedInstant() {
         return null;
      }

      @Override
      public HttpField getLastModified() {
         return null;
      }

      @Override
      public String getLastModifiedValue() {
         return null;
      }

      @Override
      public HttpField getETag() {
         return null;
      }

      @Override
      public String getETagValue() {
         return null;
      }

      @Override
      public Resource getResource() {
         return null;
      }

      @Override
      public ByteBuffer getByteBuffer() {
         return null;
      }

      @Override
      public Set<CompressedContentFormat> getPreCompressedContentFormats() {
         return null;
      }

      @Override
      public void release() {
      }

      @Override
      public boolean isValid() {
         return true;
      }

      @Override
      public boolean retain() {
         return true;
      }
   }
}
