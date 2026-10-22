package org.eclipse.jetty.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.StringUtil;

public class HttpTester {
   public static HttpTester.Input from(String string) {
      return from(BufferUtil.toBuffer(string));
   }

   public static HttpTester.Input from(ByteBuffer data) {
      return new HttpTester.Input(data) {
         @Override
         public int fillBuffer() {
            this._eof = true;
            return -1;
         }
      };
   }

   public static HttpTester.Input from(final InputStream stream) {
      return new HttpTester.Input() {
         @Override
         public int fillBuffer() throws IOException {
            BufferUtil.compact(this._buffer);
            int len = stream.read(this._buffer.array(), this._buffer.arrayOffset() + this._buffer.limit(), BufferUtil.space(this._buffer));
            if (len < 0) {
               this._eof = true;
            } else {
               this._buffer.limit(this._buffer.limit() + len);
            }

            return len;
         }
      };
   }

   public static HttpTester.Input from(final ReadableByteChannel channel) {
      return new HttpTester.Input() {
         @Override
         public int fillBuffer() throws IOException {
            BufferUtil.compact(this._buffer);
            int pos = BufferUtil.flipToFill(this._buffer);
            int len = channel.read(this._buffer);
            if (len < 0) {
               this._eof = true;
            }

            BufferUtil.flipToFlush(this._buffer, pos);
            return len;
         }
      };
   }

   public static HttpTester.Request newRequest() {
      HttpTester.Request r = new HttpTester.Request();
      r.setMethod(HttpMethod.GET.asString());
      r.setURI("/");
      r.setVersion(HttpVersion.HTTP_1_1);
      r.setHeader("Host", "localhost");
      return r;
   }

   public static HttpTester.Request parseRequest(String request) {
      return parseRequest(BufferUtil.toBuffer(request));
   }

   public static HttpTester.Request parseRequest(ByteBuffer buffer) {
      try {
         return parseRequest(from(buffer));
      } catch (IOException var2) {
         throw new UncheckedIOException(var2);
      }
   }

   public static HttpTester.Request parseRequest(InputStream stream) throws IOException {
      return parseRequest(from(stream));
   }

   public static HttpTester.Request parseRequest(ReadableByteChannel channel) throws IOException {
      return parseRequest(from(channel));
   }

   public static HttpTester.Request parseRequest(HttpTester.Input input) throws IOException {
      HttpParser parser = input.takeHttpParser();
      HttpTester.Request request;
      if (parser != null) {
         request = (HttpTester.Request)parser.getHandler();
      } else {
         request = newRequest();
         parser = new HttpParser(request);
      }

      parse(input, parser);
      if (request.isComplete()) {
         return request;
      } else {
         input.setHttpParser(parser);
         return null;
      }
   }

   public static HttpTester.Response parseHeadResponse(String response) {
      return parseResponse(response, true);
   }

   public static HttpTester.Response parseResponse(String response) {
      HttpTester.Response r = new HttpTester.Response();
      HttpParser parser = new HttpParser(r);
      parser.parseNext(BufferUtil.toBuffer(response));
      return r;
   }

   private static HttpTester.Response parseResponse(String response, boolean head) {
      return parseResponse(BufferUtil.toBuffer(response), head);
   }

   public static HttpTester.Response parseHeadResponse(ByteBuffer response) {
      return parseResponse(response, true);
   }

   public static HttpTester.Response parseResponse(ByteBuffer response) {
      return parseResponse(response, false);
   }

   private static HttpTester.Response parseResponse(ByteBuffer response, boolean head) {
      try {
         return parseResponse(from(response), head);
      } catch (IOException var3) {
         throw new UncheckedIOException(var3);
      }
   }

   public static HttpTester.Response parseResponse(InputStream stream) throws IOException {
      return parseResponse(from(stream));
   }

   public static HttpTester.Response parseResponse(ReadableByteChannel channel) throws IOException {
      return parseResponse(from(channel));
   }

   public static HttpTester.Response parseResponse(HttpTester.Input input) throws IOException {
      return parseResponse(input, false);
   }

   public static HttpTester.Response parseResponse(HttpTester.Input input, boolean head) throws IOException {
      HttpParser parser = input.takeHttpParser();
      HttpTester.Response response;
      if (parser != null) {
         response = (HttpTester.Response)parser.getHandler();
      } else {
         response = new HttpTester.Response();
         parser = new HttpParser(response);
      }

      parser.setHeadResponse(head);
      parse(input, parser);
      if (response.isComplete()) {
         return response;
      } else {
         input.setHttpParser(parser);
         return null;
      }
   }

   private static void parse(HttpTester.Input input, HttpParser parser) throws IOException {
      ByteBuffer buffer = input.getBuffer();

      while (!BufferUtil.hasContent(buffer) || !parser.parseNext(buffer)) {
         int len = input.fillBuffer();
         if (len == 0) {
            break;
         }

         if (len < 0) {
            parser.atEOF();
            parser.parseNext(buffer);
            break;
         }
      }
   }

   private HttpTester() {
   }

   public abstract static class Input {
      protected final ByteBuffer _buffer;
      protected boolean _eof = false;
      protected HttpParser _parser;

      public Input() {
         this(BufferUtil.allocate(8192));
      }

      Input(ByteBuffer buffer) {
         this._buffer = buffer;
      }

      public ByteBuffer getBuffer() {
         return this._buffer;
      }

      public void setHttpParser(HttpParser parser) {
         this._parser = parser;
      }

      public HttpParser takeHttpParser() {
         HttpParser p = this._parser;
         this._parser = null;
         return p;
      }

      public boolean isEOF() {
         return BufferUtil.isEmpty(this._buffer) && this._eof;
      }

      public abstract int fillBuffer() throws IOException;
   }

   public abstract static class Message extends org.eclipse.jetty.http.MutableHttpFields implements HttpParser.HttpHandler {
      boolean _earlyEOF;
      boolean _complete = false;
      ByteArrayOutputStream _content;
      HttpVersion _version = HttpVersion.HTTP_1_0;

      public boolean isComplete() {
         return this._complete;
      }

      public HttpVersion getVersion() {
         return this._version;
      }

      public void setVersion(String version) {
         this.setVersion((HttpVersion)HttpVersion.CACHE.get(version));
      }

      public void setVersion(HttpVersion version) {
         this._version = version;
      }

      public void setContent(byte[] bytes) {
         try {
            this._content = new ByteArrayOutputStream();
            this._content.write(bytes);
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }

      public void setContent(String content) {
         try {
            this._content = new ByteArrayOutputStream();
            this._content.write(StringUtil.getBytes(content));
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }

      public void setContent(ByteBuffer content) {
         try {
            this._content = new ByteArrayOutputStream();
            this._content.write(BufferUtil.toArray(content));
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }

      public byte[] getContentBytes() {
         return this._content == null ? null : this._content.toByteArray();
      }

      public ByteBuffer getContentByteBuffer() {
         return ByteBuffer.wrap(this.getContentBytes());
      }

      public String getContent() {
         if (this._content == null) {
            return null;
         } else {
            String contentType = this.get(HttpHeader.CONTENT_TYPE);
            String encoding = MimeTypes.getCharsetFromContentType(contentType);
            Charset charset = encoding == null ? StandardCharsets.UTF_8 : Charset.forName(encoding);
            return this._content.toString(charset);
         }
      }

      @Override
      public void parsedHeader(HttpField field) {
         this.add(field.getName(), field.getValue());
      }

      @Override
      public boolean contentComplete() {
         return false;
      }

      @Override
      public boolean messageComplete() {
         this._complete = true;
         return true;
      }

      @Override
      public boolean headerComplete() {
         this._content = new ByteArrayOutputStream();
         return false;
      }

      @Override
      public void earlyEOF() {
         this._earlyEOF = true;
      }

      public boolean isEarlyEOF() {
         return this._earlyEOF;
      }

      @Override
      public boolean content(ByteBuffer ref) {
         try {
            this._content.write(BufferUtil.toArray(ref));
            return false;
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }

      @Override
      public void badMessage(HttpException failure) {
         HttpException.throwAsUnchecked(failure);
      }

      public ByteBuffer generate() {
         try {
            HttpGenerator generator = new HttpGenerator();
            MetaData info = this.getMetaData();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteBuffer header = null;
            ByteBuffer chunk = null;
            ByteBuffer content = this._content == null ? null : ByteBuffer.wrap(this._content.toByteArray());

            while (!generator.isEnd()) {
               HttpGenerator.Result result = info instanceof MetaData.Request
                  ? generator.generateRequest((MetaData.Request)info, header, chunk, content, true)
                  : generator.generateResponse((MetaData.Response)info, false, header, chunk, content, true);
               switch (result) {
                  case NEED_HEADER:
                     header = BufferUtil.allocate(8192);
                     break;
                  case HEADER_OVERFLOW:
                     if (header.capacity() >= 32768) {
                        throw new BadMessageException(500, "Header too large");
                     }

                     header = BufferUtil.allocate(32768);
                     break;
                  case NEED_CHUNK:
                     chunk = BufferUtil.allocate(12);
                     break;
                  case NEED_CHUNK_TRAILER:
                     chunk = BufferUtil.allocate(8192);
                     break;
                  case NEED_INFO:
                     throw new IllegalStateException();
                  case FLUSH:
                     if (BufferUtil.hasContent(header)) {
                        out.write(BufferUtil.toArray(header));
                        BufferUtil.clear(header);
                     }

                     if (BufferUtil.hasContent(chunk)) {
                        out.write(BufferUtil.toArray(chunk));
                        BufferUtil.clear(chunk);
                     }

                     if (BufferUtil.hasContent(content)) {
                        out.write(BufferUtil.toArray(content));
                        BufferUtil.clear(content);
                     }
                     break;
                  case SHUTDOWN_OUT:
                     return ByteBuffer.wrap(out.toByteArray());
               }
            }

            return ByteBuffer.wrap(out.toByteArray());
         } catch (IOException var8) {
            throw new RuntimeException(var8);
         }
      }

      public abstract MetaData getMetaData();
   }

   public static class Request extends HttpTester.Message implements HttpParser.RequestHandler {
      private String _method;
      private String _uri;

      @Override
      public void startRequest(String method, String uri, HttpVersion version) {
         this._method = method;
         this._uri = uri;
         this._version = version;
      }

      public String getMethod() {
         return this._method;
      }

      public String getURI() {
         return this._uri;
      }

      public void setMethod(String method) {
         this._method = method;
      }

      public void setURI(String uri) {
         this._uri = uri;
      }

      public MetaData.Request getMetaData() {
         return new MetaData.Request(this._method, HttpURI.from(this._uri), this._version, this, this._content == null ? 0L : (long)this._content.size());
      }

      @Override
      public String toString() {
         return String.format("%s %s %s\n%s\n", this._method, this._uri, this._version, super.toString());
      }

      public void setHeader(String name, String value) {
         this.put(name, value);
      }
   }

   public static class Response extends HttpTester.Message implements HttpParser.ResponseHandler {
      private int _status;
      private String _reason;

      @Override
      public void startResponse(HttpVersion version, int status, String reason) {
         this._version = version;
         this._status = status;
         this._reason = reason;
      }

      public int getStatus() {
         return this._status;
      }

      public String getReason() {
         return this._reason;
      }

      public MetaData.Response getMetaData() {
         return new MetaData.Response(this._status, this._reason, this._version, this, this._content == null ? -1L : (long)this._content.size());
      }

      @Override
      public String toString() {
         return String.format("%s %s %s\n%s\n", this._version, this._status, this._reason, super.toString());
      }
   }
}
