package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Cleaner;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.eclipse.jetty.util.FileID;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLResourceFactory implements ResourceFactory {
   static Consumer<InputStream> ON_SWEEP_LISTENER;
   private int connectTimeout = 0;
   private int readTimeout = 0;
   private boolean useCaches = true;

   public int getConnectTimeout() {
      return this.connectTimeout;
   }

   public void setConnectTimeout(int connectTimeout) {
      this.connectTimeout = connectTimeout;
   }

   public int getReadTimeout() {
      return this.readTimeout;
   }

   public void setReadTimeout(int readTimeout) {
      this.readTimeout = readTimeout;
   }

   public boolean isUseCaches() {
      return this.useCaches;
   }

   public void setUseCaches(boolean useCaches) {
      this.useCaches = useCaches;
   }

   @Override
   public Resource newResource(URI uri) {
      try {
         return new URLResourceFactory.URLResource(uri, this.connectTimeout, this.readTimeout, this.useCaches);
      } catch (MalformedURLException var3) {
         throw new RuntimeException("Bad URI: " + uri, var3);
      }
   }

   private static class URLResource extends Resource {
      private static final Logger LOG = LoggerFactory.getLogger(URLResourceFactory.URLResource.class);
      private static final Cleaner CLEANER = Cleaner.create();
      private final AutoLock lock = new AutoLock();
      private final URI uri;
      private final URL url;
      private final int connectTimeout;
      private final int readTimeout;
      private final boolean useCaches;
      private URLConnection connection;
      private URLResourceFactory.URLResource.InputStreamReference inputStreamReference = null;

      public URLResource(URI uri, int connectTimeout, int readTimeout, boolean useCaches) throws MalformedURLException {
         this.uri = uri;
         this.url = uri.toURL();
         this.connectTimeout = connectTimeout;
         this.readTimeout = readTimeout;
         this.useCaches = useCaches;
      }

      protected boolean checkConnection() throws IOException {
         boolean e;
         try (AutoLock l = this.lock.lock()) {
            if (this.connection == null) {
               try {
                  this.connection = this.url.openConnection();
                  this.connection.setConnectTimeout(this.connectTimeout);
                  this.connection.setReadTimeout(this.readTimeout);
                  this.connection.setUseCaches(this.useCaches);
               } catch (IOException var5) {
                  LOG.trace("IGNORED", var5);
               }
            }

            e = this.connection != null;
         }

         return e;
      }

      @Override
      public Path getPath() {
         return null;
      }

      @Override
      public boolean isContainedIn(Resource container) {
         return false;
      }

      @Override
      public boolean isDirectory() {
         return this.exists() && this.uri.getSchemeSpecificPart().endsWith("/");
      }

      @Override
      public boolean isReadable() {
         return this.exists();
      }

      @Override
      public URI getURI() {
         return URIUtil.correctFileURI(this.uri);
      }

      @Override
      public String getName() {
         return this.uri.toASCIIString();
      }

      @Override
      public String getFileName() {
         return FileID.getFileName(this.uri);
      }

      @Override
      public Resource resolve(String subUriPath) {
         if (URIUtil.isNotNormalWithinSelf(subUriPath)) {
            throw new IllegalArgumentException(subUriPath);
         } else if ("/".equals(subUriPath)) {
            return this;
         } else {
            URI newURI = URIUtil.addPath(this.uri, subUriPath);

            try {
               return new URLResourceFactory.URLResource(newURI, this.connectTimeout, this.readTimeout, this.useCaches);
            } catch (MalformedURLException var4) {
               return null;
            }
         }
      }

      @Override
      public boolean exists() {
         try (AutoLock l = this.lock.lock()) {
            if (this.checkConnection()) {
               if (this.inputStreamReference == null || this.inputStreamReference.get() == null) {
                  this.inputStreamReference = new URLResourceFactory.URLResource.InputStreamReference(this.connection.getInputStream());
                  CLEANER.register(this, this.inputStreamReference);
               }

               return true;
            } else {
               return false;
            }
         } catch (IOException var6) {
            LOG.trace("IGNORED", var6);
            return false;
         }
      }

      @Override
      public InputStream newInputStream() throws IOException {
         InputStream stream;
         try (AutoLock l = this.lock.lock()) {
            if (!this.checkConnection()) {
               throw new IOException("Invalid resource");
            }

            try {
               if (this.inputStreamReference != null) {
                  stream = this.inputStreamReference.getAndSet(null);
                  this.inputStreamReference = null;
                  if (stream != null) {
                     return stream;
                  }
               }

               stream = this.connection.getInputStream();
            } finally {
               this.connection = null;
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Connection nulled");
               }
            }
         }

         return stream;
      }

      @Override
      public Instant lastModified() {
         try {
            Instant var2;
            try (AutoLock l = this.lock.lock()) {
               if (!this.checkConnection()) {
                  throw new IOException("Invalid resource");
               }

               var2 = Instant.ofEpochMilli(this.connection.getLastModified());
            }

            return var2;
         } catch (IOException var6) {
            return Instant.EPOCH;
         }
      }

      @Override
      public long length() {
         try {
            long var2;
            try (AutoLock l = this.lock.lock()) {
               if (!this.checkConnection()) {
                  throw new IOException("Invalid resource");
               }

               var2 = this.connection.getContentLengthLong();
            }

            return var2;
         } catch (IOException var6) {
            return -1L;
         }
      }

      @Override
      public ReadableByteChannel newReadableByteChannel() throws IOException {
         return Channels.newChannel(this.newInputStream());
      }

      @Override
      public URI getRealURI() {
         return this.getURI();
      }

      @Override
      public String toString() {
         return String.format("URLResource@%X(%s)", this.uri.hashCode(), this.uri.toASCIIString());
      }

      private static class InputStreamReference extends AtomicReference<InputStream> implements Runnable {
         public InputStreamReference(InputStream initialValue) {
            super(initialValue);
         }

         @Override
         public void run() {
            InputStream in = this.getAndSet(null);
            if (in != null) {
               IO.close(in);
            }

            if (URLResourceFactory.ON_SWEEP_LISTENER != null) {
               URLResourceFactory.ON_SWEEP_LISTENER.accept(in);
            }
         }
      }
   }
}
