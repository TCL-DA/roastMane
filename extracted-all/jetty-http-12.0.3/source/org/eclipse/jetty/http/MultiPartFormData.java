package org.eclipse.jetty.http;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.io.content.ContentSourceCompletableFuture;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiPartFormData {
   private static final Logger LOG = LoggerFactory.getLogger(MultiPartFormData.class);

   private MultiPartFormData() {
   }

   public static CompletableFuture<MultiPartFormData.Parts> from(
      Attributes attributes, String boundary, Function<MultiPartFormData.Parser, CompletableFuture<MultiPartFormData.Parts>> parse
   ) {
      CompletableFuture<MultiPartFormData.Parts> futureParts = (CompletableFuture<MultiPartFormData.Parts>)attributes.getAttribute(
         MultiPartFormData.class.getName()
      );
      if (futureParts == null) {
         futureParts = parse.apply(new MultiPartFormData.Parser(boundary));
         attributes.setAttribute(MultiPartFormData.class.getName(), futureParts);
      }

      return futureParts;
   }

   public static class ContentSource extends MultiPart.AbstractContentSource {
      public ContentSource(String boundary) {
         super(boundary);
      }

      @Override
      protected HttpFields customizePartHeaders(MultiPart.Part part) {
         HttpFields headers = super.customizePartHeaders(part);
         if (headers.contains(HttpHeader.CONTENT_DISPOSITION)) {
            return headers;
         } else {
            String value = "form-data";
            String name = part.getName();
            if (name != null) {
               value = value + "; name=" + QuotedCSV.quote(name);
            }

            String fileName = part.getFileName();
            if (fileName != null) {
               value = value + "; filename=" + QuotedCSV.quote(fileName);
            }

            return HttpFields.build(headers).put(HttpHeader.CONTENT_DISPOSITION, value);
         }
      }
   }

   public static class Parser {
      private final MultiPartFormData.Parser.PartsListener listener = new MultiPartFormData.Parser.PartsListener();
      private final MultiPart.Parser parser;
      private boolean useFilesForPartsWithoutFileName;
      private Path filesDirectory;
      private long maxFileSize = -1L;
      private long maxMemoryFileSize;
      private long maxLength = -1L;
      private long length;
      private MultiPartFormData.Parts parts;

      public Parser(String boundary) {
         this.parser = new MultiPart.Parser(Objects.requireNonNull(boundary), this.listener);
      }

      public CompletableFuture<MultiPartFormData.Parts> parse(Source content) {
         ContentSourceCompletableFuture<MultiPartFormData.Parts> futureParts = new ContentSourceCompletableFuture<MultiPartFormData.Parts>(content) {
            protected MultiPartFormData.Parts parse(Chunk chunk) throws Throwable {
               if (Parser.this.listener.isFailed()) {
                  throw Parser.this.listener.failure;
               } else {
                  Parser.this.length = Parser.this.length + (long)chunk.getByteBuffer().remaining();
                  long max = Parser.this.getMaxLength();
                  if (max >= 0L && Parser.this.length > max) {
                     throw new IllegalStateException("max length exceeded: %d".formatted(max));
                  } else {
                     Parser.this.parser.parse(chunk);
                     if (Parser.this.listener.isFailed()) {
                        throw Parser.this.listener.failure;
                     } else {
                        return Parser.this.parts;
                     }
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

      public Charset getDefaultCharset() {
         return this.listener.getDefaultCharset();
      }

      public int getPartHeadersMaxLength() {
         return this.parser.getPartHeadersMaxLength();
      }

      public void setPartHeadersMaxLength(int partHeadersMaxLength) {
         this.parser.setPartHeadersMaxLength(partHeadersMaxLength);
      }

      public boolean isUseFilesForPartsWithoutFileName() {
         return this.useFilesForPartsWithoutFileName;
      }

      public void setUseFilesForPartsWithoutFileName(boolean useFilesForPartsWithoutFileName) {
         this.useFilesForPartsWithoutFileName = useFilesForPartsWithoutFileName;
      }

      public Path getFilesDirectory() {
         return this.filesDirectory;
      }

      public void setFilesDirectory(Path filesDirectory) {
         this.filesDirectory = filesDirectory;
      }

      public long getMaxFileSize() {
         return this.maxFileSize;
      }

      public void setMaxFileSize(long maxFileSize) {
         this.maxFileSize = maxFileSize;
      }

      public long getMaxMemoryFileSize() {
         return this.maxMemoryFileSize;
      }

      public void setMaxMemoryFileSize(long maxMemoryFileSize) {
         this.maxMemoryFileSize = maxMemoryFileSize;
      }

      public long getMaxLength() {
         return this.maxLength;
      }

      public void setMaxLength(long maxLength) {
         this.maxLength = maxLength;
      }

      public long getMaxParts() {
         return this.parser.getMaxParts();
      }

      public void setMaxParts(long maxParts) {
         this.parser.setMaxParts(maxParts);
      }

      int getPartsSize() {
         return this.listener.getPartsSize();
      }

      private class PartsListener extends MultiPart.AbstractPartsListener {
         private final AutoLock lock = new AutoLock();
         private final List<MultiPart.Part> parts = new ArrayList<>();
         private final List<Chunk> partChunks = new ArrayList<>();
         private long fileSize;
         private long memoryFileSize;
         private Path filePath;
         private SeekableByteChannel fileChannel;
         private Throwable failure;

         @Override
         public void onPartContent(Chunk chunk) {
            ByteBuffer buffer = chunk.getByteBuffer();
            String fileName = this.getFileName();
            if (fileName != null || Parser.this.isUseFilesForPartsWithoutFileName()) {
               long maxFileSize = Parser.this.getMaxFileSize();
               this.fileSize = this.fileSize + (long)buffer.remaining();
               if (maxFileSize >= 0L && this.fileSize > maxFileSize) {
                  this.onFailure(new IllegalStateException("max file size exceeded: %d".formatted(maxFileSize)));
                  return;
               }

               long maxMemoryFileSize = Parser.this.getMaxMemoryFileSize();
               if (maxMemoryFileSize >= 0L) {
                  this.memoryFileSize = this.memoryFileSize + (long)buffer.remaining();
                  if (this.memoryFileSize > maxMemoryFileSize) {
                     try {
                        if (this.ensureFileChannel()) {
                           AutoLock ignored = this.lock.lock();

                           List<Chunk> partChunks;
                           try {
                              partChunks = List.copyOf(this.partChunks);
                           } catch (Throwable var17) {
                              if (ignored != null) {
                                 try {
                                    ignored.close();
                                 } catch (Throwable var14) {
                                    var17.addSuppressed(var14);
                                 }
                              }

                              throw var17;
                           }

                           if (ignored != null) {
                              ignored.close();
                           }

                           for (Chunk c : partChunks) {
                              this.write(c.getByteBuffer());
                           }
                        }

                        this.write(buffer);
                        if (chunk.isLast()) {
                           this.close();
                        }
                     } catch (Throwable var18) {
                        this.onFailure(var18);
                     }

                     AutoLock ignored = this.lock.lock();

                     try {
                        this.partChunks.forEach(Retainable::release);
                        this.partChunks.clear();
                     } catch (Throwable var16) {
                        if (ignored != null) {
                           try {
                              ignored.close();
                           } catch (Throwable var13) {
                              var16.addSuppressed(var13);
                           }
                        }

                        throw var16;
                     }

                     if (ignored != null) {
                        ignored.close();
                     }

                     return;
                  }
               }
            }

            chunk.retain();
            AutoLock ignored = this.lock.lock();

            try {
               this.partChunks.add(chunk);
            } catch (Throwable var15) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var12) {
                     var15.addSuppressed(var12);
                  }
               }

               throw var15;
            }

            if (ignored != null) {
               ignored.close();
            }
         }

         private void write(ByteBuffer buffer) throws Exception {
            int remaining = buffer.remaining();

            while (remaining > 0) {
               SeekableByteChannel channel = this.fileChannel();
               if (channel == null) {
                  throw new IllegalStateException();
               }

               int written = channel.write(buffer);
               if (written == 0) {
                  throw new NonWritableChannelException();
               }

               remaining -= written;
            }
         }

         private void close() {
            try {
               Closeable closeable = this.fileChannel();
               if (closeable != null) {
                  closeable.close();
               }
            } catch (Throwable var2) {
               this.onFailure(var2);
            }
         }

         @Override
         public void onPart(String name, String fileName, HttpFields headers) {
            this.fileSize = 0L;
            this.memoryFileSize = 0L;
            AutoLock ignored = this.lock.lock();

            try {
               MultiPart.Part part;
               if (this.fileChannel != null) {
                  part = new MultiPart.PathPart(name, fileName, headers, this.filePath);
               } else {
                  part = new MultiPart.ChunksPart(name, fileName, headers, List.copyOf(this.partChunks));
               }

               this.filePath = null;
               this.fileChannel = null;
               this.partChunks.forEach(Retainable::release);
               this.partChunks.clear();
               this.parts.add(part);
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
               List<MultiPart.Part> result = List.copyOf(this.parts);
               Parser.this.parts = new MultiPartFormData.Parts(result);
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

         Charset getDefaultCharset() {
            AutoLock ignored = this.lock.lock();

            Charset var2;
            try {
               var2 = this.parts
                  .stream()
                  .filter(part -> "_charset_".equals(part.getName()))
                  .map(part -> part.getContentAsString(StandardCharsets.US_ASCII))
                  .map(Charset::forName)
                  .findFirst()
                  .orElse(null);
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

         int getPartsSize() {
            AutoLock ignored = this.lock.lock();

            int var2;
            try {
               var2 = this.parts.size();
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
               this.close();
               this.delete();
               return;
            }

            if (ignored != null) {
               ignored.close();
            }
         }

         private SeekableByteChannel fileChannel() {
            AutoLock ignored = this.lock.lock();

            SeekableByteChannel var2;
            try {
               var2 = this.fileChannel;
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

         private void delete() {
            try {
               Path path = null;
               AutoLock ignored = this.lock.lock();

               try {
                  if (this.filePath != null) {
                     path = this.filePath;
                  }

                  this.filePath = null;
                  this.fileChannel = null;
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

               if (path != null) {
                  Files.delete(path);
               }
            } catch (Throwable var7) {
               if (MultiPartFormData.LOG.isTraceEnabled()) {
                  MultiPartFormData.LOG.trace("IGNORED", var7);
               }
            }
         }

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

         private boolean ensureFileChannel() {
            AutoLock ignored = this.lock.lock();

            boolean var6;
            label43: {
               try {
                  if (this.fileChannel != null) {
                     var6 = false;
                     break label43;
                  }

                  this.createFileChannel();
                  var6 = true;
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

               return var6;
            }

            if (ignored != null) {
               ignored.close();
            }

            return var6;
         }

         private void createFileChannel() {
            try {
               AutoLock ignored = this.lock.lock();

               try {
                  Path directory = Parser.this.getFilesDirectory();
                  Files.createDirectories(directory);
                  String fileName = "MultiPart";
                  this.filePath = Files.createTempFile(directory, fileName, "");
                  this.fileChannel = Files.newByteChannel(this.filePath, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
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
            } catch (Throwable var6) {
               this.onFailure(var6);
            }
         }
      }
   }

   public static class Parts implements Iterable<MultiPart.Part>, Closeable {
      private final List<MultiPart.Part> parts;

      private Parts(List<MultiPart.Part> parts) {
         this.parts = parts;
      }

      public MultiPart.Part get(int index) {
         return this.parts.get(index);
      }

      public MultiPart.Part getFirst(String name) {
         return this.parts.stream().filter(part -> part.getName().equals(name)).findFirst().orElse(null);
      }

      public List<MultiPart.Part> getAll(String name) {
         return this.parts.stream().filter(part -> part.getName().equals(name)).toList();
      }

      public int size() {
         return this.parts.size();
      }

      @Override
      public Iterator<MultiPart.Part> iterator() {
         return this.parts.iterator();
      }

      @Override
      public void close() {
         for (MultiPart.Part p : this.parts) {
            IO.close(p);
         }
      }
   }
}
