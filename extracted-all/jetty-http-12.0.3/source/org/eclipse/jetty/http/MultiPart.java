package org.eclipse.jetty.http;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.jetty.io.Retainable;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.io.content.ByteBufferContentSource;
import org.eclipse.jetty.io.content.ChunksContentSource;
import org.eclipse.jetty.io.content.PathContentSource;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.SearchPattern;
import org.eclipse.jetty.util.StaticException;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.SerializedInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiPart {
   private static final Logger LOG = LoggerFactory.getLogger(MultiPart.class);
   private static final QuotedStringTokenizer CONTENT_DISPOSITION_TOKENIZER = QuotedStringTokenizer.builder()
      .delimiters(";")
      .ignoreOptionalWhiteSpace()
      .allowEmbeddedQuotes()
      .allowEscapeOnlyForQuotes()
      .build();
   private static final int MAX_BOUNDARY_LENGTH = 70;

   private MultiPart() {
   }

   public static String extractBoundary(String contentType) {
      Map<String, String> parameters = new HashMap<>();
      HttpField.getValueParameters(contentType, parameters);
      return CONTENT_DISPOSITION_TOKENIZER.unquote(parameters.get("boundary"));
   }

   public static String generateBoundary(String prefix, int randomLength) {
      if (prefix == null && randomLength < 1) {
         throw new IllegalArgumentException("invalid boundary length");
      } else {
         StringBuilder builder = new StringBuilder(prefix == null ? "" : prefix);
         int length = builder.length();

         while (builder.length() < length + randomLength) {
            long rnd = ThreadLocalRandom.current().nextLong();
            builder.append(Long.toString(rnd < 0L ? -rnd : rnd, 36));
         }

         builder.setLength(Math.min(length + randomLength, 70));
         return builder.toString();
      }
   }

   public abstract static class AbstractContentSource implements Source, Closeable {
      private final AutoLock lock = new AutoLock();
      private final SerializedInvoker invoker = new SerializedInvoker();
      private final Queue<MultiPart.Part> parts = new ArrayDeque<>();
      private final String boundary;
      private final ByteBuffer firstBoundary;
      private final ByteBuffer middleBoundary;
      private final ByteBuffer onlyBoundary;
      private final ByteBuffer lastBoundary;
      private int partHeadersMaxLength = -1;
      private MultiPart.AbstractContentSource.State state = MultiPart.AbstractContentSource.State.FIRST;
      private boolean closed;
      private Runnable demand;
      private Chunk errorChunk;
      private MultiPart.Part part;

      public AbstractContentSource(String boundary) {
         if (!boundary.isBlank() && boundary.length() <= 70) {
            boundary = boundary.stripTrailing();
            this.boundary = boundary;
            String firstBoundaryLine = "--" + boundary + "\r\n";
            this.firstBoundary = ByteBuffer.wrap(firstBoundaryLine.getBytes(StandardCharsets.US_ASCII));
            String middleBoundaryLine = "\r\n" + firstBoundaryLine;
            this.middleBoundary = ByteBuffer.wrap(middleBoundaryLine.getBytes(StandardCharsets.US_ASCII));
            String onlyBoundaryLine = "--" + boundary + "--\r\n";
            this.onlyBoundary = ByteBuffer.wrap(onlyBoundaryLine.getBytes(StandardCharsets.US_ASCII));
            String lastBoundaryLine = "\r\n" + onlyBoundaryLine;
            this.lastBoundary = ByteBuffer.wrap(lastBoundaryLine.getBytes(StandardCharsets.US_ASCII));
         } else {
            throw new IllegalArgumentException("Invalid boundary: must consists of 1 to 70 characters");
         }
      }

      public String getBoundary() {
         return this.boundary;
      }

      public int getPartHeadersMaxLength() {
         return this.partHeadersMaxLength;
      }

      public void setPartHeadersMaxLength(int partHeadersMaxLength) {
         this.partHeadersMaxLength = partHeadersMaxLength;
      }

      public boolean addPart(MultiPart.Part part) {
         AutoLock ignored = this.lock.lock();

         boolean wasEmpty;
         label49: {
            boolean var4;
            try {
               if (!this.closed && this.errorChunk == null) {
                  wasEmpty = this.parts.isEmpty();
                  this.parts.offer(part);
                  break label49;
               }

               var4 = false;
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

            return var4;
         }

         if (ignored != null) {
            ignored.close();
         }

         if (wasEmpty) {
            this.invoker.run(this::invokeDemandCallback);
         }

         return true;
      }

      @Override
      public void close() {
         AutoLock ignored = this.lock.lock();

         boolean wasEmpty;
         try {
            this.closed = true;
            wasEmpty = this.parts.isEmpty();
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

         if (wasEmpty) {
            this.invoker.run(this::invokeDemandCallback);
         }
      }

      public long getLength() {
         return -1L;
      }

      public Chunk read() {
         AutoLock ignored = this.lock.lock();

         Chunk chunk;
         label158: {
            try {
               if (this.errorChunk != null) {
                  chunk = this.errorChunk;
                  break label158;
               }
            } catch (Throwable var10) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if (ignored != null) {
               ignored.close();
            }
            return switch (this.state) {
               case FIRST -> {
                  AutoLock ignored = this.lock.lock();

                  label160: {
                     label161: {
                        try {
                           if (!this.parts.isEmpty()) {
                              this.part = this.parts.poll();
                              this.state = MultiPart.AbstractContentSource.State.HEADERS;
                              var20 = Chunk.from(this.firstBoundary.slice(), false);
                              break label160;
                           }

                           if (!this.closed) {
                              var19 = null;
                              break label161;
                           }

                           this.state = MultiPart.AbstractContentSource.State.COMPLETE;
                           var18 = Chunk.from(this.onlyBoundary.slice(), true);
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

                        yield var18;
                     }

                     if (ignored != null) {
                        ignored.close();
                     }

                     yield var19;
                  }

                  if (ignored != null) {
                     ignored.close();
                  }

                  yield var20;
               }
               case MIDDLE -> {
                  this.part = null;
                  AutoLock ignored = this.lock.lock();

                  label164: {
                     label165: {
                        try {
                           if (!this.parts.isEmpty()) {
                              this.part = this.parts.poll();
                              this.state = MultiPart.AbstractContentSource.State.HEADERS;
                              var17 = Chunk.from(this.middleBoundary.slice(), false);
                              break label164;
                           }

                           if (!this.closed) {
                              var16 = null;
                              break label165;
                           }

                           this.state = MultiPart.AbstractContentSource.State.COMPLETE;
                           var15 = Chunk.from(this.lastBoundary.slice(), true);
                        } catch (Throwable var9) {
                           if (ignored != null) {
                              try {
                                 ignored.close();
                              } catch (Throwable var5) {
                                 var9.addSuppressed(var5);
                              }
                           }

                           throw var9;
                        }

                        if (ignored != null) {
                           ignored.close();
                        }

                        yield var15;
                     }

                     if (ignored != null) {
                        ignored.close();
                     }

                     yield var16;
                  }

                  if (ignored != null) {
                     ignored.close();
                  }

                  yield var17;
               }
               case HEADERS -> {
                  HttpFields headers = this.customizePartHeaders(this.part);
                  Utf8StringBuilder builder = new Utf8StringBuilder(4096);
                  headers.forEach(field -> {
                     HttpHeader header = field.getHeader();
                     if (header != null) {
                        builder.append(header.getBytesColonSpace());
                     } else {
                        builder.append(field.getName());
                        builder.append(": ");
                     }

                     this.checkPartHeadersLength(builder);
                     String value = field.getValue();
                     if (value != null) {
                        builder.append(value);
                        this.checkPartHeadersLength(builder);
                     }

                     builder.append("\r\n");
                  });
                  builder.append("\r\n");
                  ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(builder.toCompleteString());
                  this.state = MultiPart.AbstractContentSource.State.CONTENT;
                  Chunk var14 = Chunk.from(byteBuffer, false);
                  yield var14;
               }
               case CONTENT -> {
                  chunk = this.part.getContentSource().read();
                  if (chunk == null || Chunk.isFailure(chunk)) {
                     yield chunk;
                  } else if (!chunk.isLast()) {
                     yield chunk;
                  } else {
                     this.state = MultiPart.AbstractContentSource.State.MIDDLE;
                     if (chunk.hasRemaining()) {
                        Chunk var12 = Chunk.asChunk(chunk.getByteBuffer(), false, chunk);
                        yield var12;
                     } else {
                        chunk.release();
                        Chunk var13 = Chunk.EMPTY;
                        yield var13;
                     }
                  }
               }
               case COMPLETE -> {
                  Chunk var11 = Chunk.EOF;
                  yield var11;
               }
            };
         }

         if (ignored != null) {
            ignored.close();
         }

         return chunk;
      }

      protected HttpFields customizePartHeaders(MultiPart.Part part) {
         return part.getHeaders();
      }

      private void checkPartHeadersLength(Utf8StringBuilder builder) {
         int max = this.getPartHeadersMaxLength();
         if (max > 0 && builder.length() > max) {
            throw new IllegalStateException("headers max length exceeded: %d".formatted(max));
         }
      }

      public void demand(Runnable demandCallback) {
         MultiPart.Part part = null;
         boolean invoke = false;
         AutoLock ignored = this.lock.lock();

         try {
            if (this.demand != null) {
               throw new IllegalStateException("demand pending");
            }

            this.demand = Objects.requireNonNull(demandCallback);
            if (this.state == MultiPart.AbstractContentSource.State.CONTENT) {
               part = this.part;
            } else {
               invoke = !this.parts.isEmpty() || this.closed || this.errorChunk != null;
            }
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

         if (part != null) {
            part.getContentSource().demand(() -> {
               AutoLock ignoredAgain = this.lock.lock();

               try {
                  this.demand = null;
               } catch (Throwable var6) {
                  if (ignoredAgain != null) {
                     try {
                        ignoredAgain.close();
                     } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                     }
                  }

                  throw var6;
               }

               if (ignoredAgain != null) {
                  ignoredAgain.close();
               }

               demandCallback.run();
            });
         } else if (invoke) {
            this.invoker.run(this::invokeDemandCallback);
         }
      }

      public void fail(Throwable failure) {
         AutoLock ignored = this.lock.lock();

         label44: {
            MultiPart.Part part;
            List<MultiPart.Part> drained;
            try {
               if (this.errorChunk != null) {
                  break label44;
               }

               this.errorChunk = Chunk.from(failure);
               drained = List.copyOf(this.parts);
               this.parts.clear();
               part = this.part;
               this.part = null;
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

            if (part != null) {
               part.fail(failure);
            }

            drained.forEach(p -> p.fail(failure));
            this.invoker.run(this::invokeDemandCallback);
            return;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      private void invokeDemandCallback() {
         AutoLock ignored = this.lock.lock();

         Runnable callback;
         try {
            callback = this.demand;
            this.demand = null;
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

         if (callback != null) {
            this.runDemandCallback(callback);
         }
      }

      private void runDemandCallback(Runnable callback) {
         try {
            callback.run();
         } catch (Throwable var3) {
            this.fail(var3);
         }
      }

      private static enum State {
         FIRST,
         MIDDLE,
         HEADERS,
         CONTENT,
         COMPLETE;
      }
   }

   public abstract static class AbstractPartsListener implements MultiPart.Parser.Listener {
      private static final Logger LOG = LoggerFactory.getLogger(MultiPart.AbstractPartsListener.class);
      private final HttpFields.Mutable fields = HttpFields.build();
      private String name;
      private String fileName;

      public String getName() {
         return this.name;
      }

      public String getFileName() {
         return this.fileName;
      }

      @Override
      public void onPartHeader(String headerName, String headerValue) {
         if (HttpHeader.CONTENT_DISPOSITION.is(headerName)) {
            String namePrefix = "name=";
            String fileNamePrefix = "filename=";
            Iterator<String> tokens = MultiPart.CONTENT_DISPOSITION_TOKENIZER.tokenize(headerValue);

            while (tokens.hasNext()) {
               String token = tokens.next();
               String lowerToken = StringUtil.asciiToLowerCase(token);
               if (lowerToken.startsWith(namePrefix)) {
                  int index = lowerToken.indexOf(namePrefix);
                  String value = token.substring(index + namePrefix.length()).trim();
                  this.name = MultiPart.CONTENT_DISPOSITION_TOKENIZER.unquote(value);
               } else if (lowerToken.startsWith(fileNamePrefix)) {
                  this.fileName = this.fileNameValue(token);
               }
            }
         }

         this.fields.add(new HttpField(headerName, headerValue));
      }

      private String fileNameValue(String token) {
         int idx = token.indexOf(61);
         String value = token.substring(idx + 1).trim();
         if (!value.matches(".??[a-zA-Z]:\\\\[^\\\\].*")) {
            return MultiPart.CONTENT_DISPOSITION_TOKENIZER.unquote(value);
         } else {
            char first = value.charAt(0);
            if (first == '"' || first == '\'') {
               value = value.substring(1);
            }

            char last = value.charAt(value.length() - 1);
            if (last == '"' || last == '\'') {
               value = value.substring(0, value.length() - 1);
            }

            return value;
         }
      }

      @Override
      public void onPartEnd() {
         String name = this.getName();
         this.name = null;
         String fileName = this.getFileName();
         this.fileName = null;
         HttpFields headers = this.fields.asImmutable();
         this.fields.clear();
         this.notifyPart(name, fileName, headers);
      }

      public abstract void onPart(String var1, String var2, HttpFields var3);

      private void notifyPart(String name, String fileName, HttpFields headers) {
         try {
            this.onPart(name, fileName, headers);
         } catch (Throwable var5) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying part {}", name, var5);
            }
         }
      }
   }

   public static class ByteBufferPart extends MultiPart.Part {
      private final List<ByteBuffer> content;

      public ByteBufferPart(String name, String fileName, HttpFields fields, ByteBuffer... buffers) {
         this(name, fileName, fields, List.of(buffers));
      }

      public ByteBufferPart(String name, String fileName, HttpFields fields, List<ByteBuffer> content) {
         super(name, fileName, fields);
         this.content = content;
      }

      @Override
      public Source newContentSource() {
         return new ByteBufferContentSource(this.content);
      }

      @Override
      public String toString() {
         return "%s@%x[name=%s,fileName=%s,length=%d]"
            .formatted(this.getClass().getSimpleName(), this.hashCode(), this.getName(), this.getFileName(), this.getLength());
      }
   }

   public static class ChunksPart extends MultiPart.Part {
      private final AutoLock lock = new AutoLock();
      private final List<Chunk> content = new ArrayList<>();
      private final List<Source> contentSources = new ArrayList<>();
      private boolean closed = false;

      public ChunksPart(String name, String fileName, HttpFields fields, List<Chunk> content) {
         super(name, fileName, fields);
         this.content.addAll(content);
         content.forEach(Retainable::retain);
      }

      @Override
      public Source newContentSource() {
         AutoLock l = this.lock.lock();

         Object var7;
         label43: {
            ChunksContentSource var4;
            try {
               if (this.closed) {
                  var7 = null;
                  break label43;
               }

               List<Chunk> chunks = this.content.stream().map(chunk -> Chunk.from(chunk.getByteBuffer().slice(), chunk.isLast())).toList();
               ChunksContentSource newContentSource = new ChunksContentSource(chunks);
               chunks.forEach(Retainable::release);
               this.contentSources.add(newContentSource);
               var4 = newContentSource;
            } catch (Throwable var6) {
               if (l != null) {
                  try {
                     l.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (l != null) {
               l.close();
            }

            return var4;
         }

         if (l != null) {
            l.close();
         }

         return (Source)var7;
      }

      @Override
      public void fail(Throwable t) {
         AutoLock l = this.lock.lock();

         List<Source> contentSourcesToFail;
         try {
            this.closed = true;
            this.content.forEach(Retainable::release);
            this.content.clear();
            contentSourcesToFail = List.copyOf(this.contentSources);
            this.contentSources.clear();
         } catch (Throwable var7) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (l != null) {
            l.close();
         }

         contentSourcesToFail.forEach(c -> c.fail(t));
         super.fail(t);
      }

      @Override
      public String toString() {
         return "%s@%x[name=%s,fileName=%s,length=%d]"
            .formatted(this.getClass().getSimpleName(), this.hashCode(), this.getName(), this.getFileName(), this.getLength());
      }
   }

   public static class ContentSourcePart extends MultiPart.Part {
      private Source content;

      public ContentSourcePart(String name, String fileName, HttpFields fields, Source content) {
         super(name, fileName, fields);
         this.content = Objects.requireNonNull(content);
      }

      @Override
      public Source newContentSource() {
         Source c = this.content;
         this.content = null;
         return c;
      }

      @Override
      public String toString() {
         return "%s@%x[name=%s,fileName=%s,length=%d]"
            .formatted(this.getClass().getSimpleName(), this.hashCode(), this.getName(), this.getFileName(), this.getLength());
      }
   }

   public static class Parser {
      private static final Logger LOG = LoggerFactory.getLogger(MultiPart.Parser.class);
      private static final ByteBuffer CR = StandardCharsets.US_ASCII.encode("\r");
      private final Utf8StringBuilder text = new Utf8StringBuilder();
      private final String boundary;
      private final SearchPattern boundaryFinder;
      private final MultiPart.Parser.Listener listener;
      private int partHeadersLength;
      private int partHeadersMaxLength = -1;
      private MultiPart.Parser.State state;
      private int partialBoundaryMatch;
      private boolean crFlag;
      private boolean crContent;
      private int trailingWhiteSpaces;
      private String fieldName;
      private String fieldValue;
      private long maxParts = 1000L;
      private int numParts;

      public Parser(String boundary, MultiPart.Parser.Listener listener) {
         this.boundary = boundary;
         this.boundaryFinder = SearchPattern.compile("\n--" + boundary);
         this.listener = listener;
         this.reset();
      }

      public String getBoundary() {
         return this.boundary;
      }

      public int getPartHeadersMaxLength() {
         return this.partHeadersMaxLength;
      }

      public void setPartHeadersMaxLength(int partHeadersMaxLength) {
         this.partHeadersMaxLength = partHeadersMaxLength;
      }

      public long getMaxParts() {
         return this.maxParts;
      }

      public void setMaxParts(long maxParts) {
         this.maxParts = maxParts;
      }

      public void reset() {
         this.text.reset();
         this.partHeadersLength = 0;
         this.state = MultiPart.Parser.State.PREAMBLE;
         this.partialBoundaryMatch = 1;
         this.crFlag = false;
         this.crContent = false;
         this.trailingWhiteSpaces = 0;
         this.fieldName = null;
         this.fieldValue = null;
      }

      public void parse(Chunk chunk) {
         ByteBuffer buffer = chunk.getByteBuffer();
         boolean last = chunk.isLast();

         try {
            while (buffer.hasRemaining()) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("parse {} {}", this.state, BufferUtil.toDetailString(buffer));
               }

               switch (this.state) {
                  case PREAMBLE:
                     if (this.parsePreamble(buffer)) {
                        this.state = MultiPart.Parser.State.BOUNDARY;
                     }
                     break;
                  case BOUNDARY:
                     HttpTokens.Token token = this.next(buffer);
                     HttpTokens.Type type = token.getType();
                     if (type != HttpTokens.Type.CR) {
                        if (type == HttpTokens.Type.LF) {
                           this.numParts++;
                           if (this.maxParts >= 0L && (long)this.numParts > this.maxParts) {
                              throw new IllegalStateException(String.format("Form with too many keys [%d > %d]", this.numParts, this.maxParts));
                           }

                           this.notifyPartBegin();
                           this.state = MultiPart.Parser.State.HEADER_START;
                           this.trailingWhiteSpaces = 0;
                           this.text.reset();
                           this.partHeadersLength = 0;
                        } else if (token.getByte() == 45) {
                           this.state = MultiPart.Parser.State.BOUNDARY_CLOSE;
                        } else if (type != HttpTokens.Type.SPACE && type != HttpTokens.Type.HTAB) {
                           throw new BadMessageException("bad last boundary");
                        }
                     }
                     break;
                  case BOUNDARY_CLOSE:
                     HttpTokens.Token token = this.next(buffer);
                     if (token.getByte() != 45) {
                        throw new BadMessageException("bad last boundary");
                     }

                     this.state = MultiPart.Parser.State.EPILOGUE;
                     break;
                  case HEADER_START:
                     this.state = this.parseHeaderStart(buffer);
                     break;
                  case HEADER_NAME:
                     if (this.parseHeaderName(buffer)) {
                        this.state = MultiPart.Parser.State.HEADER_VALUE;
                     }
                     break;
                  case HEADER_VALUE:
                     if (this.parseHeaderValue(buffer)) {
                        this.state = MultiPart.Parser.State.HEADER_START;
                     }
                     break;
                  case CONTENT_START:
                     if (this.parseContent(chunk)) {
                        this.state = MultiPart.Parser.State.BOUNDARY;
                     } else {
                        this.state = MultiPart.Parser.State.CONTENT;
                     }
                     break;
                  case CONTENT:
                     if (this.parseContent(chunk)) {
                        this.state = MultiPart.Parser.State.BOUNDARY;
                     }
                     break;
                  case EPILOGUE:
                     buffer.position(buffer.limit());
               }
            }

            if (last) {
               if (this.state != MultiPart.Parser.State.EPILOGUE) {
                  throw new EOFException("unexpected EOF");
               }

               this.notifyComplete();
            }
         } catch (Throwable var6) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("parse failure {} {}", new Object[]{this.state, BufferUtil.toDetailString(buffer), var6});
            }

            buffer.position(buffer.limit());
            this.notifyFailure(var6);
         }
      }

      private HttpTokens.Token next(ByteBuffer buffer) {
         byte b = buffer.get();
         HttpTokens.Token t = HttpTokens.TOKENS[b & 255];
         switch (t.getType()) {
            case CNTL:
               throw new BadMessageException("invalid byte " + Integer.toHexString(t.getChar()));
            case LF:
               this.crFlag = false;
               break;
            case CR:
               if (this.crFlag) {
                  throw new BadMessageException("invalid EOL");
               }

               this.crFlag = true;
               break;
            default:
               if (this.crFlag) {
                  throw new BadMessageException("invalid EOL");
               }
         }

         return t;
      }

      private boolean parsePreamble(ByteBuffer buffer) {
         if (this.partialBoundaryMatch > 0) {
            int boundaryMatch = this.boundaryFinder.startsWith(buffer, this.partialBoundaryMatch);
            if (boundaryMatch > 0) {
               if (boundaryMatch == this.boundaryFinder.getLength()) {
                  buffer.position(buffer.position() + boundaryMatch - this.partialBoundaryMatch);
                  this.partialBoundaryMatch = 0;
                  return true;
               }

               buffer.position(buffer.limit());
               this.partialBoundaryMatch = boundaryMatch;
               return false;
            }

            this.partialBoundaryMatch = 0;
         }

         int boundaryOffset = this.boundaryFinder.match(buffer);
         if (boundaryOffset >= 0) {
            buffer.position(buffer.position() + boundaryOffset + this.boundaryFinder.getLength());
            return true;
         } else {
            this.partialBoundaryMatch = this.boundaryFinder.endsWith(buffer);
            buffer.position(buffer.limit());
            return false;
         }
      }

      private MultiPart.Parser.State parseHeaderStart(ByteBuffer buffer) {
         while (buffer.hasRemaining()) {
            HttpTokens.Token token = this.next(buffer);
            switch (token.getType()) {
               case LF:
                  this.notifyPartHeaders();
                  this.partialBoundaryMatch = 1;
                  return MultiPart.Parser.State.CONTENT_START;
               case CR:
                  break;
               case COLON:
                  throw new BadMessageException("invalid empty header name");
               default:
                  if (Character.isWhitespace(token.getByte())) {
                     if (this.text.length() != 0) {
                        continue;
                     }

                     throw new BadMessageException("invalid leading whitespace before header");
                  }

                  this.incrementAndCheckPartHeadersLength();
                  this.text.append(token.getByte());
                  return MultiPart.Parser.State.HEADER_NAME;
            }
         }

         return MultiPart.Parser.State.HEADER_START;
      }

      private boolean parseHeaderName(ByteBuffer buffer) {
         while (buffer.hasRemaining()) {
            HttpTokens.Token token = this.next(buffer);
            switch (token.getType()) {
               case COLON:
                  this.incrementAndCheckPartHeadersLength();
                  this.fieldName = this.text.takeCompleteString(null);
                  this.trailingWhiteSpaces = 0;
                  return true;
               case ALPHA:
               case DIGIT:
               case TCHAR:
                  byte current = token.getByte();
                  if (this.trailingWhiteSpaces > 0) {
                     throw new BadMessageException("invalid header name");
                  }

                  this.incrementAndCheckPartHeadersLength();
                  this.text.append(current);
                  break;
               default:
                  byte current = token.getByte();
                  if (!Character.isWhitespace((int)current)) {
                     throw new BadMessageException("invalid header name");
                  }

                  this.incrementAndCheckPartHeadersLength();
                  this.trailingWhiteSpaces++;
            }
         }

         return false;
      }

      private boolean parseHeaderValue(ByteBuffer buffer) {
         while (buffer.hasRemaining()) {
            HttpTokens.Token token = this.next(buffer);
            switch (token.getType()) {
               case LF:
                  this.fieldValue = this.text.toCompleteString().stripTrailing();
                  this.text.reset();
                  this.notifyPartHeader(this.fieldName, this.fieldValue);
                  this.fieldName = null;
                  this.fieldValue = null;
                  return true;
               case CR:
                  break;
               default:
                  byte current = token.getByte();
                  this.incrementAndCheckPartHeadersLength();
                  if (Character.isWhitespace((int)current)) {
                     if (this.text.length() > 0) {
                        this.text.append(" ");
                     }
                  } else {
                     this.text.append(current);
                  }
            }
         }

         return false;
      }

      private void incrementAndCheckPartHeadersLength() {
         this.partHeadersLength++;
         int max = this.getPartHeadersMaxLength();
         if (max > 0 && this.partHeadersLength > max) {
            throw new IllegalStateException("headers max length exceeded: %d".formatted(max));
         }
      }

      private boolean parseContent(Chunk chunk) {
         ByteBuffer buffer = chunk.getByteBuffer();
         if (this.partialBoundaryMatch > 0) {
            int boundaryMatch = this.boundaryFinder.startsWith(buffer, this.partialBoundaryMatch);
            if (boundaryMatch > 0) {
               if (boundaryMatch == this.boundaryFinder.getLength()) {
                  buffer.position(buffer.position() + boundaryMatch - this.partialBoundaryMatch);
                  this.partialBoundaryMatch = 0;
                  this.notifyPartContent(Chunk.EOF);
                  this.notifyPartEnd();
                  return true;
               } else {
                  buffer.position(buffer.limit());
                  this.partialBoundaryMatch = boundaryMatch;
                  return false;
               }
            } else if (this.state == MultiPart.Parser.State.CONTENT_START) {
               this.partialBoundaryMatch = 0;
               return false;
            } else {
               if (this.crContent) {
                  this.crContent = false;
                  Chunk partContentChunk = Chunk.from(CR.slice(), false);
                  this.notifyPartContent(partContentChunk);
                  partContentChunk.release();
               }

               ByteBuffer content = ByteBuffer.wrap(this.boundaryFinder.getPattern(), 0, this.partialBoundaryMatch);
               this.partialBoundaryMatch = 0;
               Chunk partContentChunk = Chunk.from(content, false);
               this.notifyPartContent(partContentChunk);
               partContentChunk.release();
               return false;
            }
         } else {
            int boundaryOffset = this.boundaryFinder.match(buffer);
            if (boundaryOffset >= 0) {
               int position = buffer.position();
               int length = boundaryOffset;
               if (boundaryOffset > 0 && buffer.get(position + boundaryOffset - 1) == 13) {
                  length = boundaryOffset - 1;
               }

               Chunk content = this.asSlice(chunk, position, length, true);
               buffer.position(position + boundaryOffset + this.boundaryFinder.getLength());
               this.notifyPartContent(content);
               this.notifyPartEnd();
               return true;
            } else {
               this.partialBoundaryMatch = this.boundaryFinder.endsWith(buffer);
               if (this.partialBoundaryMatch > 0) {
                  int limit = buffer.limit();
                  int sliceLimit = limit - this.partialBoundaryMatch;
                  if (sliceLimit > 0 && buffer.get(sliceLimit - 1) == 13) {
                     this.crContent = true;
                     sliceLimit--;
                  }

                  int position = buffer.position();
                  Chunk content = this.asSlice(chunk, position, sliceLimit - position, false);
                  buffer.position(limit);
                  if (content.hasRemaining()) {
                     this.notifyPartContent(content);
                  }

                  return false;
               } else {
                  if (this.crContent) {
                     this.crContent = false;
                     this.notifyPartContent(Chunk.from(CR.slice(), false));
                  }

                  int sliceLimitx = buffer.limit();
                  if (buffer.get(sliceLimitx - 1) == 13) {
                     this.crContent = true;
                     sliceLimitx--;
                  }

                  int position = buffer.position();
                  Chunk content = this.asSlice(chunk, position, sliceLimitx - position, false);
                  buffer.position(buffer.limit());
                  if (content.hasRemaining()) {
                     this.notifyPartContent(content);
                  }

                  return false;
               }
            }
         }
      }

      private Chunk asSlice(Chunk chunk, int position, int length, boolean last) {
         if (chunk.isLast() && !chunk.hasRemaining()) {
            return chunk;
         } else if (length == 0) {
            return last ? Chunk.EOF : Chunk.EMPTY;
         } else {
            return Chunk.asChunk(chunk.getByteBuffer().slice(position, length), last, chunk);
         }
      }

      private void notifyPartBegin() {
         try {
            this.listener.onPartBegin();
         } catch (Throwable var2) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying listener {}", this.listener, var2);
            }
         }
      }

      private void notifyPartHeader(String name, String value) {
         try {
            this.listener.onPartHeader(name, value);
         } catch (Throwable var4) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying listener {}", this.listener, var4);
            }
         }
      }

      private void notifyPartHeaders() {
         try {
            this.listener.onPartHeaders();
         } catch (Throwable var2) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying listener {}", this.listener, var2);
            }
         }
      }

      private void notifyPartContent(Chunk chunk) {
         try {
            this.listener.onPartContent(chunk);
         } catch (Throwable var3) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying listener {}", this.listener, var3);
            }
         }
      }

      private void notifyPartEnd() {
         try {
            this.listener.onPartEnd();
         } catch (Throwable var2) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying listener {}", this.listener, var2);
            }
         }
      }

      private void notifyComplete() {
         try {
            this.listener.onComplete();
         } catch (Throwable var2) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying listener {}", this.listener, var2);
            }
         }
      }

      private void notifyFailure(Throwable failure) {
         try {
            this.listener.onFailure(failure);
         } catch (Throwable var3) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("failure while notifying listener {}", this.listener, var3);
            }
         }
      }

      public interface Listener {
         default void onPartBegin() {
         }

         default void onPartHeader(String name, String value) {
         }

         default void onPartHeaders() {
         }

         default void onPartContent(Chunk chunk) {
         }

         default void onPartEnd() {
         }

         default void onComplete() {
         }

         default void onFailure(Throwable failure) {
         }
      }

      private static enum State {
         PREAMBLE,
         BOUNDARY,
         BOUNDARY_CLOSE,
         HEADER_START,
         HEADER_NAME,
         HEADER_VALUE,
         CONTENT_START,
         CONTENT,
         EPILOGUE;
      }
   }

   public abstract static class Part implements Closeable {
      static final Throwable CLOSE_EXCEPTION = new StaticException("Closed");
      private final AutoLock lock = new AutoLock();
      private final String name;
      private final String fileName;
      private final HttpFields fields;
      private Path path;
      private Source contentSource;
      private boolean temporary;

      public Part(String name, String fileName, HttpFields fields) {
         this(name, fileName, fields, null);
      }

      private Part(String name, String fileName, HttpFields fields, Path path) {
         this.name = name;
         this.fileName = fileName;
         this.fields = fields != null ? fields : HttpFields.EMPTY;
         this.path = path;
         this.temporary = true;
      }

      private Path getPath() {
         AutoLock ignored = this.lock.lock();

         Path var2;
         try {
            var2 = this.path;
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

      public String getName() {
         return this.name;
      }

      public String getFileName() {
         return this.fileName;
      }

      public Source getContentSource() {
         AutoLock ignored = this.lock.lock();

         Source var2;
         try {
            if (this.contentSource == null) {
               this.contentSource = this.newContentSource();
            }

            var2 = this.contentSource;
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

      public abstract Source newContentSource();

      public long getLength() {
         return this.getContentSource().getLength();
      }

      public String getContentAsString(Charset defaultCharset) {
         try {
            String contentType = this.getHeaders().get(HttpHeader.CONTENT_TYPE);
            String charsetName = MimeTypes.getCharsetFromContentType(contentType);
            Charset charset = defaultCharset != null ? defaultCharset : StandardCharsets.UTF_8;
            if (charsetName != null) {
               charset = Charset.forName(charsetName);
            }

            return Source.asString(this.newContentSource(), charset);
         } catch (IOException var5) {
            throw new UncheckedIOException(var5);
         }
      }

      public HttpFields getHeaders() {
         return this.fields;
      }

      public void writeTo(Path path) throws IOException {
         Path currentPath = this.getPath();
         Path newPath;
         if (currentPath == null) {
            try (OutputStream out = Files.newOutputStream(path)) {
               IO.copy(Source.asInputStream(this.newContentSource()), out);
            }

            newPath = path;
         } else {
            newPath = Files.move(currentPath, path, StandardCopyOption.REPLACE_EXISTING);
         }

         AutoLock ignored = this.lock.lock();

         try {
            this.temporary = false;
            this.path = newPath;
         } catch (Throwable var9) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var7) {
                  var9.addSuppressed(var7);
               }
            }

            throw var9;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      public void delete() throws IOException {
         Path path = this.getPath();
         if (path != null) {
            Files.delete(path);
            AutoLock ignored = this.lock.lock();

            try {
               this.path = null;
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
      }

      @Override
      public void close() {
         this.fail(CLOSE_EXCEPTION);
      }

      public void fail(Throwable t) {
         try {
            this.getContentSource().fail(t);
            Path path = null;
            AutoLock ignored = this.lock.lock();

            try {
               if (this.temporary) {
                  path = this.path;
                  this.path = null;
               }
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

            if (path != null) {
               Files.delete(path);
            }
         } catch (Throwable var8) {
            if (MultiPart.LOG.isDebugEnabled()) {
               MultiPart.LOG.debug("Error closing part {}", this, var8);
            }
         }
      }
   }

   public static class PathPart extends MultiPart.Part {
      public PathPart(String name, String fileName, HttpFields fields, Path path) {
         super(name, fileName, fields, path);
      }

      @Override
      public Path getPath() {
         return super.getPath();
      }

      @Override
      public Source newContentSource() {
         return new PathContentSource(this.getPath());
      }

      @Override
      public String toString() {
         return "%s@%x[name=%s,fileName=%s,path=%s]"
            .formatted(this.getClass().getSimpleName(), this.hashCode(), this.getName(), this.getFileName(), this.getPath());
      }
   }
}
