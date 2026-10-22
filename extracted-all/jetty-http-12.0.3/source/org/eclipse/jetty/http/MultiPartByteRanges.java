package org.eclipse.jetty.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.io.content.ContentSourceCompletableFuture;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.thread.AutoLock;

public class MultiPartByteRanges {
   private MultiPartByteRanges() {
   }

   public static class ContentSource extends MultiPart.AbstractContentSource {
      public ContentSource(String boundary) {
         super(boundary);
      }

      @Override
      public boolean addPart(MultiPart.Part part) {
         return part instanceof MultiPartByteRanges.Part ? super.addPart(part) : false;
      }
   }

   public static class InputStreamContentSource extends org.eclipse.jetty.io.content.InputStreamContentSource {
      private long toRead;

      public InputStreamContentSource(InputStream inputStream, ByteRange byteRange) throws IOException {
         super(inputStream);
         inputStream.skipNBytes(byteRange.first());
         this.toRead = byteRange.getLength();
      }

      protected int fillBufferFromInputStream(InputStream inputStream, byte[] buffer) throws IOException {
         if (this.toRead == 0L) {
            return -1;
         } else {
            int toReadInt = (int)Math.min(2147483647L, this.toRead);
            int len = Math.min(toReadInt, buffer.length);
            int read = inputStream.read(buffer, 0, len);
            this.toRead -= (long)read;
            return read;
         }
      }
   }

   public static class Parser {
      private final MultiPartByteRanges.Parser.PartsListener listener = new MultiPartByteRanges.Parser.PartsListener();
      private final MultiPart.Parser parser;
      private MultiPartByteRanges.Parts parts;

      public Parser(String boundary) {
         this.parser = new MultiPart.Parser(boundary, this.listener);
      }

      public CompletableFuture<MultiPartByteRanges.Parts> parse(Source content) {
         ContentSourceCompletableFuture<MultiPartByteRanges.Parts> futureParts = new ContentSourceCompletableFuture<MultiPartByteRanges.Parts>(content) {
            protected MultiPartByteRanges.Parts parse(Chunk chunk) throws Throwable {
               if (Parser.this.listener.isFailed()) {
                  throw Parser.this.listener.failure;
               } else {
                  Parser.this.parser.parse(chunk);
                  if (Parser.this.listener.isFailed()) {
                     throw Parser.this.listener.failure;
                  } else {
                     return Parser.this.parts;
                  }
               }
            }

            public boolean completeExceptionally(Throwable failure) {
               boolean failed = super.completeExceptionally(failure);
               if (failed) {
                  Parser.this.listener.fail(failure);
               }

               return failed;
            }
         };
         futureParts.parse();
         return futureParts;
      }

      public String getBoundary() {
         return this.parser.getBoundary();
      }

      private class PartsListener extends MultiPart.AbstractPartsListener {
         private final AutoLock lock = new AutoLock();
         private final List<Chunk> partChunks = new ArrayList<>();
         private final List<MultiPart.Part> parts = new ArrayList<>();
         private Throwable failure;

         private boolean isFailed() {
            AutoLock ignored = this.lock.lock();

            boolean var2;
            try {
               var2 = this.failure != null;
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

            return var2;
         }

         @Override
         public void onPartContent(Chunk chunk) {
            AutoLock ignored = this.lock.lock();

            try {
               chunk.retain();
               this.partChunks.add(chunk);
            } catch (Throwable var6) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (ignored != null) {
               ignored.close();
            }
         }

         @Override
         public void onPart(String name, String fileName, HttpFields headers) {
            AutoLock ignored = this.lock.lock();

            try {
               this.parts.add(new MultiPart.ChunksPart(name, fileName, headers, List.copyOf(this.partChunks)));
               this.partChunks.forEach(Retainable::release);
               this.partChunks.clear();
            } catch (Throwable var8) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (ignored != null) {
               ignored.close();
            }
         }

         @Override
         public void onComplete() {
            super.onComplete();
            AutoLock ignored = this.lock.lock();

            try {
               List<MultiPart.Part> copy = List.copyOf(this.parts);
               Parser.this.parts = new MultiPartByteRanges.Parts(Parser.this.getBoundary(), copy);
            } catch (Throwable var6) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (ignored != null) {
               ignored.close();
            }
         }

         @Override
         public void onFailure(Throwable failure) {
            this.fail(failure);
         }

         private void fail(Throwable cause) {
            AutoLock ignored = this.lock.lock();

            label40: {
               List<MultiPart.Part> partsToFail;
               try {
                  if (this.failure != null) {
                     break label40;
                  }

                  this.failure = cause;
                  partsToFail = List.copyOf(this.parts);
                  this.parts.clear();
                  this.partChunks.forEach(Retainable::release);
                  this.partChunks.clear();
               } catch (Throwable var7) {
                  if (ignored != null) {
                     try {
                        ignored.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (ignored != null) {
                  ignored.close();
               }

               partsToFail.forEach(p -> p.fail(cause));
               return;
            }

            if (ignored != null) {
               ignored.close();
            }
         }
      }
   }

   public static class Part extends MultiPart.Part {
      private final Resource resource;
      private final ByteRange byteRange;

      public Part(String contentType, Resource resource, ByteRange byteRange, long contentLength) {
         this(
            HttpFields.build().put(HttpHeader.CONTENT_TYPE, contentType).put(HttpHeader.CONTENT_RANGE, byteRange.toHeaderValue(contentLength)),
            resource,
            byteRange
         );
      }

      public Part(HttpFields headers, Resource resource, ByteRange byteRange) {
         super(null, null, headers);
         this.resource = resource;
         this.byteRange = byteRange;
      }

      @Override
      public Source newContentSource() {
         Path path = this.resource.getPath();
         if (path != null) {
            return new MultiPartByteRanges.PathContentSource(path, this.byteRange);
         } else {
            try {
               return new MultiPartByteRanges.InputStreamContentSource(this.resource.newInputStream(), this.byteRange);
            } catch (IOException var3) {
               throw new RuntimeIOException(var3);
            }
         }
      }
   }

   public static class Parts implements Iterable<MultiPart.Part> {
      private final String boundary;
      private final List<MultiPart.Part> parts;

      private Parts(String boundary, List<MultiPart.Part> parts) {
         this.boundary = boundary;
         this.parts = parts;
      }

      public String getBoundary() {
         return this.boundary;
      }

      public MultiPart.Part get(int index) {
         return this.parts.get(index);
      }

      public int size() {
         return this.parts.size();
      }

      @Override
      public Iterator<MultiPart.Part> iterator() {
         return this.parts.iterator();
      }
   }

   public static class PathContentSource extends org.eclipse.jetty.io.content.PathContentSource {
      private final ByteRange byteRange;
      private long toRead;

      public PathContentSource(Path path, ByteRange byteRange) {
         super(path);
         this.byteRange = byteRange;
      }

      protected SeekableByteChannel open() throws IOException {
         SeekableByteChannel channel = super.open();
         channel.position(this.byteRange.first());
         this.toRead = this.byteRange.getLength();
         return channel;
      }

      protected int read(SeekableByteChannel channel, ByteBuffer byteBuffer) throws IOException {
         int read = super.read(channel, byteBuffer);
         if (read <= 0) {
            return read;
         } else {
            read = (int)Math.min((long)read, this.toRead);
            this.toRead -= (long)read;
            byteBuffer.position(read);
            return read;
         }
      }

      protected boolean isReadComplete(long read) {
         return read == this.byteRange.getLength();
      }
   }
}
