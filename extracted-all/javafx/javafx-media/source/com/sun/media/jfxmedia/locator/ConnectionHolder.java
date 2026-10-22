package com.sun.media.jfxmedia.locator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ConnectionHolder {
   private static int DEFAULT_BUFFER_SIZE = 4096;
   ReadableByteChannel channel;
   ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);

   static ConnectionHolder createMemoryConnectionHolder(ByteBuffer var0) {
      return new ConnectionHolder.MemoryConnectionHolder(var0);
   }

   static ConnectionHolder createURIConnectionHolder(URI var0, Map<String, Object> var1) throws IOException {
      return new ConnectionHolder.URIConnectionHolder(var0, var1);
   }

   static ConnectionHolder createFileConnectionHolder(URI var0) throws IOException {
      return new ConnectionHolder.FileConnectionHolder(var0);
   }

   static ConnectionHolder createHLSConnectionHolder(URI var0) throws IOException {
      return new HLSConnectionHolder(var0);
   }

   public int readNextBlock() throws IOException {
      this.buffer.rewind();
      if (this.buffer.limit() < this.buffer.capacity()) {
         this.buffer.limit(this.buffer.capacity());
      }

      if (null == this.channel) {
         throw new ClosedChannelException();
      } else {
         return this.channel.read(this.buffer);
      }
   }

   public ByteBuffer getBuffer() {
      return this.buffer;
   }

   abstract int readBlock(long var1, int var3) throws IOException;

   abstract boolean needBuffer();

   abstract boolean isSeekable();

   abstract boolean isRandomAccess();

   public abstract long seek(long var1);

   public void closeConnection() {
      try {
         if (this.channel != null) {
            this.channel.close();
         }
      } catch (IOException var5) {
      } finally {
         this.channel = null;
      }
   }

   int property(int var1, int var2) {
      return 0;
   }

   int getStreamSize() {
      return -1;
   }

   private static class FileConnectionHolder extends ConnectionHolder {
      private RandomAccessFile file = null;

      FileConnectionHolder(URI var1) throws IOException {
         this.channel = this.openFile(var1);
      }

      @Override
      boolean needBuffer() {
         return false;
      }

      @Override
      boolean isRandomAccess() {
         return true;
      }

      @Override
      boolean isSeekable() {
         return true;
      }

      @Override
      public long seek(long var1) {
         try {
            ((FileChannel)this.channel).position(var1);
            return var1;
         } catch (IOException var4) {
            return -1L;
         }
      }

      @Override
      int readBlock(long var1, int var3) throws IOException {
         if (null == this.channel) {
            throw new ClosedChannelException();
         } else {
            if (this.buffer.capacity() < var3) {
               this.buffer = ByteBuffer.allocateDirect(var3);
            }

            this.buffer.rewind().limit(var3);
            return ((FileChannel)this.channel).read(this.buffer, var1);
         }
      }

      private ReadableByteChannel openFile(URI var1) throws IOException {
         if (this.file != null) {
            this.file.close();
         }

         this.file = new RandomAccessFile(new File(var1), "r");
         return this.file.getChannel();
      }

      @Override
      public void closeConnection() {
         super.closeConnection();
         if (this.file != null) {
            try {
               this.file.close();
            } catch (IOException var5) {
            } finally {
               this.file = null;
            }
         }
      }
   }

   private static class MemoryConnectionHolder extends ConnectionHolder {
      private final ByteBuffer backingBuffer;

      public MemoryConnectionHolder(ByteBuffer var1) {
         if (null == var1) {
            throw new IllegalArgumentException("Can't connect to null buffer...");
         } else {
            if (var1.isDirect()) {
               this.backingBuffer = var1.duplicate();
            } else {
               this.backingBuffer = ByteBuffer.allocateDirect(var1.capacity());
               this.backingBuffer.put(var1);
            }

            this.backingBuffer.rewind();
            this.channel = new ReadableByteChannel() {
               @Override
               public int read(ByteBuffer var1) throws IOException {
                  if (MemoryConnectionHolder.this.backingBuffer.remaining() <= 0) {
                     return -1;
                  } else {
                     int var2;
                     if (var1.equals(MemoryConnectionHolder.this.buffer)) {
                        var2 = Math.min(ConnectionHolder.DEFAULT_BUFFER_SIZE, MemoryConnectionHolder.this.backingBuffer.remaining());
                        if (var2 > 0) {
                           MemoryConnectionHolder.this.buffer = MemoryConnectionHolder.this.backingBuffer.slice();
                           MemoryConnectionHolder.this.buffer.limit(var2);
                        }
                     } else {
                        var2 = Math.min(var1.remaining(), MemoryConnectionHolder.this.backingBuffer.remaining());
                        if (var2 > 0) {
                           MemoryConnectionHolder.this.backingBuffer.limit(MemoryConnectionHolder.this.backingBuffer.position() + var2);
                           var1.put(MemoryConnectionHolder.this.backingBuffer);
                           MemoryConnectionHolder.this.backingBuffer.limit(MemoryConnectionHolder.this.backingBuffer.capacity());
                        }
                     }

                     return var2;
                  }
               }

               @Override
               public boolean isOpen() {
                  return true;
               }

               @Override
               public void close() throws IOException {
               }
            };
         }
      }

      @Override
      int readBlock(long var1, int var3) throws IOException {
         if (null == this.channel) {
            throw new ClosedChannelException();
         } else if ((int)var1 > this.backingBuffer.capacity()) {
            return -1;
         } else {
            this.backingBuffer.position((int)var1);
            this.buffer = this.backingBuffer.slice();
            int var4 = Math.min(this.backingBuffer.remaining(), var3);
            this.buffer.limit(var4);
            this.backingBuffer.position(this.backingBuffer.position() + var4);
            return var4;
         }
      }

      @Override
      boolean needBuffer() {
         return false;
      }

      @Override
      boolean isSeekable() {
         return true;
      }

      @Override
      boolean isRandomAccess() {
         return true;
      }

      @Override
      public long seek(long var1) {
         if ((int)var1 < this.backingBuffer.capacity()) {
            this.backingBuffer.limit(this.backingBuffer.capacity());
            this.backingBuffer.position((int)var1);
            return var1;
         } else {
            return -1L;
         }
      }

      @Override
      public void closeConnection() {
         this.channel = null;
      }
   }

   private static class URIConnectionHolder extends ConnectionHolder {
      private URI uri;
      private URLConnection urlConnection;

      URIConnectionHolder(URI var1, Map<String, Object> var2) throws IOException {
         this.uri = var1;
         this.urlConnection = var1.toURL().openConnection();
         if (var2 != null) {
            for (Entry var4 : var2.entrySet()) {
               Object var5 = var4.getValue();
               if (var5 instanceof String) {
                  this.urlConnection.setRequestProperty((String)var4.getKey(), (String)var5);
               }
            }
         }

         this.channel = this.openChannel(null);
      }

      @Override
      boolean needBuffer() {
         String var1 = this.uri.getScheme().toLowerCase();
         return "http".equals(var1) || "https".equals(var1);
      }

      @Override
      boolean isSeekable() {
         return this.urlConnection instanceof HttpURLConnection || this.urlConnection instanceof JarURLConnection || this.isJRT() || this.isResource();
      }

      @Override
      boolean isRandomAccess() {
         return false;
      }

      @Override
      int readBlock(long var1, int var3) throws IOException {
         throw new IOException();
      }

      @Override
      public long seek(long var1) {
         if (this.urlConnection instanceof HttpURLConnection) {
            URLConnection var14 = null;

            long var16;
            try {
               var14 = this.uri.toURL().openConnection();
               HttpURLConnection var4 = (HttpURLConnection)var14;
               var4.setRequestMethod("GET");
               var4.setUseCaches(false);
               var4.setRequestProperty("Range", "bytes=" + var1 + "-");
               if (var4.getResponseCode() != 206) {
                  return -1L;
               }

               this.closeConnection();
               this.urlConnection = var14;
               var14 = null;
               this.channel = this.openChannel(null);
               var16 = var1;
            } catch (IOException var11) {
               return -1L;
            } finally {
               if (var14 != null) {
                  Locator.closeConnection(var14);
               }
            }

            return var16;
         } else if (!(this.urlConnection instanceof JarURLConnection) && !this.isJRT() && !this.isResource()) {
            return -1L;
         } else {
            try {
               this.closeConnection();
               this.urlConnection = this.uri.toURL().openConnection();
               long var3 = var1;
               InputStream var5 = this.urlConnection.getInputStream();

               do {
                  long var6 = var5.skip(var3);
                  var3 -= var6;
               } while (var3 > 0L);

               this.channel = this.openChannel(var5);
               return var1;
            } catch (IOException var13) {
               return -1L;
            }
         }
      }

      @Override
      public void closeConnection() {
         super.closeConnection();
         Locator.closeConnection(this.urlConnection);
         this.urlConnection = null;
      }

      private ReadableByteChannel openChannel(InputStream var1) throws IOException {
         return var1 == null ? Channels.newChannel(this.urlConnection.getInputStream()) : Channels.newChannel(var1);
      }

      private boolean isJRT() {
         String var1 = this.uri.getScheme().toLowerCase();
         return "jrt".equals(var1);
      }

      private boolean isResource() {
         String var1 = this.uri.getScheme().toLowerCase();
         return "resource".equals(var1);
      }
   }
}
