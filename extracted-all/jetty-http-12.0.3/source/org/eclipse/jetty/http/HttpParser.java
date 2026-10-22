package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.util.Index.Builder;
import org.eclipse.jetty.util.Index.Mutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpParser {
   public static final Logger LOG = LoggerFactory.getLogger(HttpParser.class);
   public static final int INITIAL_URI_LENGTH = 256;
   private static final int MAX_CHUNK_LENGTH = 134217711;
   public static final Index<HttpField> CACHE = new Builder()
      .caseSensitive(false)
      .with(HttpFields.CONNECTION_CLOSE)
      .with(HttpFields.CONNECTION_KEEPALIVE)
      .with(new HttpField(HttpHeader.CONNECTION, HttpHeaderValue.UPGRADE))
      .with(new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip"))
      .with(new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip, deflate"))
      .with(new HttpField(HttpHeader.ACCEPT_ENCODING, "gzip, deflate, br"))
      .with(new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-US,enq=0.5"))
      .with(new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-GB,en-USq=0.8,enq=0.6"))
      .with(new HttpField(HttpHeader.ACCEPT_LANGUAGE, "en-AU,enq=0.9,it-ITq=0.8,itq=0.7,en-GBq=0.6,en-USq=0.5"))
      .with(new HttpField(HttpHeader.ACCEPT_CHARSET, "ISO-8859-1,utf-8q=0.7,*q=0.3"))
      .with(new HttpField(HttpHeader.ACCEPT, "*/*"))
      .with(new HttpField(HttpHeader.ACCEPT, "image/png,image/*q=0.8,*/*q=0.5"))
      .with(new HttpField(HttpHeader.ACCEPT, "text/html,application/xhtml+xml,application/xmlq=0.9,*/*q=0.8"))
      .with(new HttpField(HttpHeader.ACCEPT, "text/html,application/xhtml+xml,application/xmlq=0.9,image/webp,image/apng,*/*q=0.8"))
      .with(new HttpField(HttpHeader.ACCEPT_RANGES, HttpHeaderValue.BYTES))
      .with(new HttpField(HttpHeader.PRAGMA, "no-cache"))
      .with(new HttpField(HttpHeader.CACHE_CONTROL, "private, no-cache, no-cache=Set-Cookie, proxy-revalidate"))
      .with(new HttpField(HttpHeader.CACHE_CONTROL, "no-cache"))
      .with(new HttpField(HttpHeader.CACHE_CONTROL, "max-age=0"))
      .with(HttpFields.CONTENT_LENGTH_0)
      .with(new HttpField(HttpHeader.CONTENT_ENCODING, "gzip"))
      .with(new HttpField(HttpHeader.CONTENT_ENCODING, "deflate"))
      .with(new HostPortHttpField("localhost"))
      .with(new HttpField(HttpHeader.TRANSFER_ENCODING, "chunked"))
      .with(new HttpField(HttpHeader.EXPIRES, "Fri, 01 Jan 1990 00:00:00 GMT"))
      .withAll(() -> {
         Map<String, HttpField> map = new LinkedHashMap<>();

         for (String type : new String[]{"text/plain", "text/html", "text/xml", "text/json", "application/json", "application/x-www-form-urlencoded"}) {
            HttpField field = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type);
            map.put(field.toString(), field);

            for (String charset : new String[]{"utf-8", "iso-8859-1"}) {
               PreEncodedHttpField field1 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + ";charset=" + charset);
               map.put(field1.toString(), field1);
               PreEncodedHttpField field2 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + "; charset=" + charset);
               map.put(field2.toString(), field2);
               PreEncodedHttpField field3 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + ";charset=" + charset.toUpperCase(Locale.ENGLISH));
               map.put(field3.toString(), field3);
               PreEncodedHttpField field4 = new PreEncodedHttpField(HttpHeader.CONTENT_TYPE, type + "; charset=" + charset.toUpperCase(Locale.ENGLISH));
               map.put(field4.toString(), field4);
            }
         }

         return map;
      })
      .withAll(() -> {
         Map<String, HttpField> map = new LinkedHashMap<>();

         for (HttpHeader h : HttpHeader.values()) {
            HttpField httpField = new HttpField(h, (String)null);
            map.put(httpField.toString(), httpField);
         }

         return map;
      })
      .build();
   private static final Mutable<HttpField> NO_CACHE = new Builder().caseSensitive(false).mutable().maxCapacity(0).build();
   private static final int HTTP_AS_INT = 1213486160;
   private static final int HTTP_1_0_AS_INT = 791752240;
   private static final int HTTP_1_1_AS_INT = 791752241;
   private static final int HTTP_VERSION_LEN = 8;
   private static final int INT_LEN = 4;
   private static final EnumSet<HttpParser.State> __idleStates = EnumSet.of(
      HttpParser.State.START, HttpParser.State.END, HttpParser.State.CLOSE, HttpParser.State.CLOSED
   );
   private static final EnumSet<HttpParser.State> __completeStates = EnumSet.of(HttpParser.State.END, HttpParser.State.CLOSE, HttpParser.State.CLOSED);
   private static final EnumSet<HttpParser.State> __terminatedStates = EnumSet.of(HttpParser.State.CLOSE, HttpParser.State.CLOSED);
   private final boolean debugEnabled = LOG.isDebugEnabled();
   private final HttpParser.HttpHandler _handler;
   private final HttpParser.RequestHandler _requestHandler;
   private final HttpParser.ResponseHandler _responseHandler;
   private final ComplianceViolation.Listener _complianceListener;
   private final int _maxHeaderBytes;
   private final HttpCompliance _complianceMode;
   private final Utf8StringBuilder _uri = new Utf8StringBuilder(256);
   private final HttpParser.FieldCache _fieldCache = new HttpParser.FieldCache();
   private HttpField _field;
   private HttpHeader _header;
   private long _beginNanoTime;
   private String _headerString;
   private String _valueString;
   private int _responseStatus;
   private int _headerBytes;
   private String _parsedHost;
   private boolean _headerComplete;
   private volatile HttpParser.State _state = HttpParser.State.START;
   private volatile HttpParser.FieldState _fieldState = HttpParser.FieldState.FIELD;
   private volatile boolean _eof;
   private HttpMethod _method;
   private String _methodString;
   private HttpVersion _version;
   private HttpTokens.EndOfContent _endOfContent;
   private boolean _hasContentLength;
   private boolean _hasTransferEncoding;
   private long _contentLength = -1L;
   private long _contentPosition;
   private int _chunkLength;
   private int _chunkPosition;
   private boolean _headResponse;
   private boolean _cr;
   private ByteBuffer _contentChunk;
   private int _length;
   private final StringBuilder _string = new StringBuilder();

   private static HttpCompliance compliance() {
      return HttpCompliance.RFC7230;
   }

   public HttpParser(HttpParser.RequestHandler handler) {
      this(handler, -1, compliance());
   }

   public HttpParser(HttpParser.ResponseHandler handler) {
      this(handler, -1, compliance());
   }

   public HttpParser(HttpParser.RequestHandler handler, int maxHeaderBytes) {
      this(handler, maxHeaderBytes, compliance());
   }

   public HttpParser(HttpParser.ResponseHandler handler, int maxHeaderBytes) {
      this(handler, maxHeaderBytes, compliance());
   }

   public HttpParser(HttpParser.RequestHandler handler, HttpCompliance compliance) {
      this(handler, -1, compliance);
   }

   public HttpParser(HttpParser.RequestHandler handler, int maxHeaderBytes, HttpCompliance compliance) {
      this(handler, null, maxHeaderBytes, compliance == null ? compliance() : compliance);
   }

   public HttpParser(HttpParser.ResponseHandler handler, int maxHeaderBytes, HttpCompliance compliance) {
      this(null, handler, maxHeaderBytes, compliance == null ? compliance() : compliance);
   }

   private HttpParser(HttpParser.RequestHandler requestHandler, HttpParser.ResponseHandler responseHandler, int maxHeaderBytes, HttpCompliance compliance) {
      this._handler = (HttpParser.HttpHandler)(requestHandler != null ? requestHandler : responseHandler);
      this._requestHandler = requestHandler;
      this._responseHandler = responseHandler;
      this._maxHeaderBytes = maxHeaderBytes;
      this._complianceMode = compliance;
      this._complianceListener = (ComplianceViolation.Listener)(this._handler instanceof ComplianceViolation.Listener ? this._handler : null);
   }

   public long getBeginNanoTime() {
      return this._beginNanoTime;
   }

   public HttpParser.HttpHandler getHandler() {
      return this._handler;
   }

   public int getHeaderCacheSize() {
      return this._fieldCache.getCapacity();
   }

   public void setHeaderCacheSize(int headerCacheSize) {
      this._fieldCache.setCapacity(headerCacheSize);
   }

   public boolean isHeaderCacheCaseSensitive() {
      return this._fieldCache.isCaseSensitive();
   }

   public void setHeaderCacheCaseSensitive(boolean headerCacheCaseSensitive) {
      this._fieldCache.setCaseSensitive(headerCacheCaseSensitive);
   }

   protected void checkViolation(HttpCompliance.Violation violation) throws BadMessageException {
      if (violation.isAllowedBy(this._complianceMode)) {
         this.reportComplianceViolation(violation, violation.getDescription());
      } else {
         throw new BadMessageException(violation.getDescription());
      }
   }

   protected void reportComplianceViolation(HttpCompliance.Violation violation) {
      this.reportComplianceViolation(violation, violation.getDescription());
   }

   protected void reportComplianceViolation(HttpCompliance.Violation violation, String reason) {
      if (this._complianceListener != null) {
         this._complianceListener.onComplianceViolation(this._complianceMode, violation, reason);
      }
   }

   protected String caseInsensitiveHeader(String orig, String normative) {
      if (HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode)) {
         return normative;
      } else {
         if (!orig.equals(normative)) {
            this.reportComplianceViolation(HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME, orig);
         }

         return orig;
      }
   }

   public long getContentLength() {
      return this._contentLength;
   }

   public long getContentRead() {
      return this._contentPosition;
   }

   public int getHeaderLength() {
      return this._headerBytes;
   }

   public void setHeadResponse(boolean head) {
      this._headResponse = head;
   }

   protected void setResponseStatus(int status) {
      this._responseStatus = status;
   }

   public HttpParser.State getState() {
      return this._state;
   }

   public boolean inContentState() {
      return this._state.ordinal() >= HttpParser.State.CONTENT.ordinal() && this._state.ordinal() < HttpParser.State.END.ordinal();
   }

   public boolean inHeaderState() {
      return this._state.ordinal() < HttpParser.State.CONTENT.ordinal();
   }

   public boolean isChunking() {
      return this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT;
   }

   public boolean isStart() {
      return this.isState(HttpParser.State.START);
   }

   public boolean isClose() {
      return this.isState(HttpParser.State.CLOSE);
   }

   public boolean isClosed() {
      return this.isState(HttpParser.State.CLOSED);
   }

   public boolean isIdle() {
      return __idleStates.contains(this._state);
   }

   public boolean isComplete() {
      return __completeStates.contains(this._state);
   }

   public boolean isTerminated() {
      return __terminatedStates.contains(this._state);
   }

   public boolean isState(HttpParser.State state) {
      return this._state == state;
   }

   private HttpTokens.Token next(ByteBuffer buffer) {
      byte ch = buffer.get();
      HttpTokens.Token t = HttpTokens.getToken(ch);
      switch (t.getType()) {
         case CNTL:
            throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
         case LF:
            this._cr = false;
            break;
         case CR:
            if (this._cr) {
               throw new BadMessageException("Bad EOL");
            }

            if (!buffer.hasRemaining()) {
               this._cr = true;
               return null;
            }

            if (this._maxHeaderBytes > 0 && (this._state == HttpParser.State.HEADER || this._state == HttpParser.State.TRAILER)) {
               this._headerBytes++;
            }

            ch = buffer.get();
            t = HttpTokens.TOKENS[255 & ch];
            switch (t.getType()) {
               case CNTL:
                  throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
               case LF:
                  return t;
               default:
                  throw new BadMessageException("Bad EOL");
            }
         case ALPHA:
         case DIGIT:
         case TCHAR:
         case VCHAR:
         case HTAB:
         case SPACE:
         case OTEXT:
         case COLON:
            if (this._cr) {
               throw new BadMessageException("Bad EOL");
            }
      }

      return t;
   }

   private void quickStart(ByteBuffer buffer) {
      int position = buffer.position();
      if (this._requestHandler != null) {
         this._method = HttpMethod.lookAheadGet(buffer);
         if (this._method != null) {
            this._methodString = this._method.asString();
            buffer.position(position + this._methodString.length() + 1);
            this.setState(HttpParser.State.SPACE1);
            return;
         }
      } else if (this._responseHandler != null && buffer.remaining() >= 9 && buffer.getInt(position) == 1213486160) {
         int v = buffer.getInt(position + 4);
         this._version = v == 791752241 ? HttpVersion.HTTP_1_1 : (v == 791752240 ? HttpVersion.HTTP_1_0 : null);
         if (this._version != null && buffer.get(position + 8) == 32) {
            buffer.position(position + 8 + 1);
            this.setState(HttpParser.State.SPACE1);
            return;
         }
      }

      while (this._state == HttpParser.State.START && buffer.hasRemaining()) {
         HttpTokens.Token t = this.next(buffer);
         if (t == null) {
            break;
         }

         switch (t.getType()) {
            case ALPHA:
            case DIGIT:
            case TCHAR:
            case VCHAR:
               this._string.setLength(0);
               this._string.append(t.getChar());
               this.setState(this._requestHandler != null ? HttpParser.State.METHOD : HttpParser.State.RESPONSE_VERSION);
               return;
            case HTAB:
            case SPACE:
            case OTEXT:
               throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
            default:
               if (this._maxHeaderBytes > 0 && ++this._headerBytes > this._maxHeaderBytes) {
                  LOG.warn("padding is too large >{}", this._maxHeaderBytes);
                  throw new BadMessageException(400);
               }
         }
      }
   }

   private void setString(String s) {
      this._string.setLength(0);
      this._string.append(s);
      this._length = s.length();
   }

   private String takeString() {
      this._string.setLength(this._length);
      String s = this._string.toString();
      this._string.setLength(0);
      this._length = -1;
      return s;
   }

   private boolean handleHeaderContentMessage() {
      boolean handleHeader = this._handler.headerComplete();
      this._headerComplete = true;
      if (handleHeader) {
         return true;
      } else {
         this.setState(HttpParser.State.CONTENT_END);
         return this.handleContentMessage();
      }
   }

   private boolean handleContentMessage() {
      boolean handleContent = this._handler.contentComplete();
      if (handleContent) {
         return true;
      } else {
         this.setState(HttpParser.State.END);
         return this._handler.messageComplete();
      }
   }

   private boolean parseLine(ByteBuffer buffer) {
      boolean handle = false;

      while (this._state.ordinal() < HttpParser.State.HEADER.ordinal() && buffer.hasRemaining() && !handle) {
         HttpTokens.Token t = this.next(buffer);
         if (t != null) {
            if (this._maxHeaderBytes > 0 && ++this._headerBytes > this._maxHeaderBytes) {
               if (this._state == HttpParser.State.URI) {
                  LOG.warn("URI is too large >{}", this._maxHeaderBytes);
                  throw new BadMessageException(414);
               }

               if (this._requestHandler != null) {
                  LOG.warn("request is too large >{}", this._maxHeaderBytes);
               } else {
                  LOG.warn("response is too large >{}", this._maxHeaderBytes);
               }

               throw new BadMessageException(431);
            }

            switch (this._state) {
               case METHOD:
                  switch (t.getType()) {
                     case LF:
                        throw new BadMessageException("No URI");
                     case CR:
                     case VCHAR:
                     case HTAB:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                        this._string.append(t.getChar());
                        continue;
                     case SPACE:
                        this._length = this._string.length();
                        this._methodString = this.takeString();
                        HttpMethod method = (HttpMethod)HttpMethod.CACHE.get(this._methodString);
                        if (method != null) {
                           this._methodString = method.asString();
                        } else if (HttpCompliance.Violation.CASE_INSENSITIVE_METHOD.isAllowedBy(this._complianceMode)) {
                           method = (HttpMethod)HttpMethod.INSENSITIVE_CACHE.get(this._methodString);
                           if (method != null) {
                              this._methodString = method.asString();
                              this.reportComplianceViolation(HttpCompliance.Violation.CASE_INSENSITIVE_METHOD, this._methodString);
                           }
                        }

                        this.setState(HttpParser.State.SPACE1);
                        continue;
                  }
               case RESPONSE_VERSION:
                  switch (t.getType()) {
                     case LF:
                     case CR:
                        throw new BadMessageException("No Status");
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case COLON:
                        this._string.append(t.getChar());
                        continue;
                     case HTAB:
                     case OTEXT:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case SPACE:
                        this._length = this._string.length();
                        String version = this.takeString();
                        this._version = (HttpVersion)HttpVersion.CACHE.get(version);
                        this.checkVersion();
                        this.setState(HttpParser.State.SPACE1);
                        continue;
                  }
               case SPACE1:
                  switch (t.getType()) {
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case COLON:
                        if (this._responseHandler != null) {
                           if (t.getType() != HttpTokens.Type.DIGIT) {
                              throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                           }

                           this.setState(HttpParser.State.STATUS);
                           this.setResponseStatus(t.getByte() - 48);
                        } else {
                           this._uri.reset();
                           this.setState(HttpParser.State.URI);
                           if (!buffer.hasArray()) {
                              this._uri.append(t.getByte());
                              continue;
                           }

                           byte[] array = buffer.array();
                           int px = buffer.arrayOffset() + buffer.position();
                           int l = buffer.arrayOffset() + buffer.limit();
                           int i = px;

                           while (i < l && array[i] > 32) {
                              i++;
                           }

                           int len = i - px;
                           this._headerBytes += len;
                           if (this._maxHeaderBytes > 0 && ++this._headerBytes > this._maxHeaderBytes) {
                              LOG.warn("URI is too large >{}", this._maxHeaderBytes);
                              throw new BadMessageException(414);
                           }

                           this._uri.append(array, px - 1, len + 1);
                           buffer.position(i - buffer.arrayOffset());
                        }
                        continue;
                     case HTAB:
                     case OTEXT:
                     default:
                        throw new BadMessageException(this._requestHandler != null ? "No URI" : "No Status");
                     case SPACE:
                        continue;
                  }
               case STATUS:
                  assert this._responseHandler != null;

                  switch (t.getType()) {
                     case LF:
                        this._fieldCache.prepare();
                        this.setState(HttpParser.State.HEADER);
                        this._responseHandler.startResponse(this._version, this._responseStatus, null);
                        continue;
                     case DIGIT:
                        this._responseStatus = this._responseStatus * 10 + (t.getByte() - 48);
                        if (this._responseStatus >= 1000) {
                           throw new BadMessageException("Bad status");
                        }
                        continue;
                     case SPACE:
                        this.setState(HttpParser.State.SPACE2);
                        continue;
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                  }
               case URI:
                  assert this._requestHandler != null;

                  int position = buffer.position();
                  int remaining = buffer.remaining();
                  switch (t.getType()) {
                     case LF:
                        if (!HttpCompliance.Violation.HTTP_0_9.isAllowedBy(this._complianceMode)) {
                           throw new HttpException.RuntimeException(505, "HTTP/0.9 not supported");
                        }

                        this.reportComplianceViolation(HttpCompliance.Violation.HTTP_0_9, HttpCompliance.Violation.HTTP_0_9.getDescription());
                        this._requestHandler.startRequest(this._methodString, this._uri.toCompleteString(), HttpVersion.HTTP_0_9);
                        this.setState(HttpParser.State.CONTENT);
                        this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                        BufferUtil.clear(buffer);
                        handle = this.handleHeaderContentMessage();
                        continue;
                     case CR:
                     case HTAB:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case OTEXT:
                     case COLON:
                        this._uri.append(t.getByte());
                        continue;
                     case SPACE:
                        if (remaining >= 9 && buffer.getInt(position) == 1213486160) {
                           int v = buffer.getInt(position + 4);
                           HttpVersion version = v == 791752241 ? HttpVersion.HTTP_1_1 : (v == 791752240 ? HttpVersion.HTTP_1_0 : null);
                           if (version != null) {
                              int p = position + 8;
                              byte next = buffer.get(p++);
                              if (next == 13 && remaining >= 10) {
                                 next = buffer.get(p++);
                              }

                              if (next == 10) {
                                 buffer.position(p);
                                 this._version = version;
                                 this._string.setLength(0);
                                 this.checkVersion();
                                 this._fieldCache.prepare();
                                 this.setState(HttpParser.State.HEADER);
                                 this._requestHandler.startRequest(this._methodString, this._uri.toCompleteString(), this._version);
                                 continue;
                              }
                           }
                        }

                        this.setState(HttpParser.State.SPACE2);
                        continue;
                  }
               case SPACE2:
                  switch (t.getType()) {
                     case LF:
                        if (this._responseHandler != null) {
                           this._fieldCache.prepare();
                           this.setState(HttpParser.State.HEADER);
                           this._responseHandler.startResponse(this._version, this._responseStatus, null);
                        } else {
                           this.checkViolation(HttpCompliance.Violation.HTTP_0_9);
                           this._requestHandler.startRequest(this._methodString, this._uri.toCompleteString(), HttpVersion.HTTP_0_9);
                           this.setState(HttpParser.State.CONTENT);
                           this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                           BufferUtil.clear(buffer);
                           handle = this.handleHeaderContentMessage();
                        }
                        continue;
                     case CR:
                     case HTAB:
                     case OTEXT:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case COLON:
                        this._string.setLength(0);
                        this._string.append(t.getChar());
                        this._length = 1;
                        this.setState(this._responseHandler != null ? HttpParser.State.REASON : HttpParser.State.REQUEST_VERSION);
                     case SPACE:
                        continue;
                  }
               case REQUEST_VERSION:
                  switch (t.getType()) {
                     case LF:
                        if (this._version == null) {
                           this._length = this._string.length();
                           this._version = (HttpVersion)HttpVersion.CACHE.get(this.takeString());
                        }

                        this.checkVersion();
                        this._fieldCache.prepare();
                        this.setState(HttpParser.State.HEADER);
                        this._requestHandler.startRequest(this._methodString, this._uri.toCompleteString(), this._version);
                        continue;
                     case CR:
                     case HTAB:
                     case SPACE:
                     case OTEXT:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case COLON:
                        this._string.append(t.getChar());
                        continue;
                  }
               case REASON:
                  assert this._responseHandler != null;

                  switch (t.getType()) {
                     case LF:
                        String reason = this.takeString();
                        this._fieldCache.prepare();
                        this.setState(HttpParser.State.HEADER);
                        this._responseHandler.startResponse(this._version, this._responseStatus, reason);
                        continue;
                     case CR:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case OTEXT:
                     case COLON:
                        this._string.append(t.getChar());
                        this._length = this._string.length();
                        continue;
                     case HTAB:
                     case SPACE:
                        this._string.append(t.getChar());
                        continue;
                  }
               default:
                  throw new IllegalStateException(this._state.toString());
            }
         }
         break;
      }

      return handle;
   }

   private void checkVersion() {
      if (this._version == null) {
         throw new HttpException.RuntimeException(505, "Unknown Version");
      } else if (this._version.getVersion() < 10 || this._version.getVersion() > 20) {
         throw new HttpException.RuntimeException(505, "Unsupported Version");
      }
   }

   private void parsedHeader() {
      if (this._headerString != null || this._valueString != null) {
         if (this._header != null) {
            boolean addToFieldCache = false;
            switch (this._header) {
               case CONTENT_LENGTH:
                  if (this._hasTransferEncoding) {
                     this.checkViolation(HttpCompliance.Violation.TRANSFER_ENCODING_WITH_CONTENT_LENGTH);
                  }

                  long contentLength = this.convertContentLength(this._valueString);
                  if (this._hasContentLength) {
                     this.checkViolation(HttpCompliance.Violation.MULTIPLE_CONTENT_LENGTHS);
                     if (contentLength != this._contentLength) {
                        throw new BadMessageException(HttpCompliance.Violation.MULTIPLE_CONTENT_LENGTHS.getDescription());
                     }
                  }

                  this._hasContentLength = true;
                  if (this._endOfContent != HttpTokens.EndOfContent.CHUNKED_CONTENT) {
                     this._contentLength = contentLength;
                     this._endOfContent = HttpTokens.EndOfContent.CONTENT_LENGTH;
                  }
                  break;
               case TRANSFER_ENCODING:
                  this._hasTransferEncoding = true;
                  if (this._hasContentLength) {
                     this.checkViolation(HttpCompliance.Violation.TRANSFER_ENCODING_WITH_CONTENT_LENGTH);
                  }

                  if (this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT) {
                     throw new BadMessageException("Bad Transfer-Encoding, chunked not last");
                  }

                  if (HttpHeaderValue.CHUNKED.is(this._valueString)) {
                     this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
                     this._contentLength = -1L;
                  } else {
                     List<String> values = new QuotedCSV(this._valueString).getValues();
                     int chunked = -1;
                     int len = values.size();

                     for (int i = 0; i < len; i++) {
                        if (HttpHeaderValue.CHUNKED.is(values.get(i))) {
                           if (chunked != -1) {
                              throw new BadMessageException("Bad Transfer-Encoding, multiple chunked tokens");
                           }

                           chunked = i;
                           this._endOfContent = HttpTokens.EndOfContent.CHUNKED_CONTENT;
                           this._contentLength = -1L;
                        } else if (this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT) {
                           throw new BadMessageException("Bad Transfer-Encoding, chunked not last");
                        }
                     }
                  }
                  break;
               case HOST:
                  if (this._parsedHost != null) {
                     if (LOG.isWarnEnabled()) {
                        LOG.warn(
                           "Encountered multiple `Host` headers.  Previous `Host` header already seen as `{}`, new `Host` header has appeared as `{}`",
                           this._parsedHost,
                           this._valueString
                        );
                     }

                     this.checkViolation(HttpCompliance.Violation.DUPLICATE_HOST_HEADERS);
                  }

                  this._parsedHost = this._valueString;
                  if (!(this._field instanceof HostPortHttpField) && this._valueString != null && !this._valueString.isEmpty()) {
                     if (HttpCompliance.Violation.UNSAFE_HOST_HEADER.isAllowedBy(this._complianceMode)) {
                        this._field = new HostPortHttpField(
                           this._header,
                           HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode) ? this._headerString : this._header.asString(),
                           HostPort.unsafe(this._valueString)
                        );
                     } else {
                        this._field = new HostPortHttpField(
                           this._header,
                           HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode) ? this._headerString : this._header.asString(),
                           this._valueString
                        );
                     }

                     addToFieldCache = this._fieldCache.isEnabled();
                  }
                  break;
               case CONNECTION:
                  if (this._field == null) {
                     this._field = new HttpField(this._header, this.caseInsensitiveHeader(this._headerString, this._header.asString()), this._valueString);
                  }

                  if (this.getHeaderCacheSize() > 0 && this._field.contains(HttpHeaderValue.CLOSE.asString())) {
                     this._fieldCache.setCapacity(-1);
                  }
                  break;
               case AUTHORIZATION:
               case ACCEPT:
               case ACCEPT_CHARSET:
               case ACCEPT_ENCODING:
               case ACCEPT_LANGUAGE:
               case COOKIE:
               case CACHE_CONTROL:
               case USER_AGENT:
                  addToFieldCache = this._field == null && this._fieldCache.cacheable(this._header, this._valueString);
            }

            if (addToFieldCache) {
               if (this._field == null) {
                  this._field = new HttpField(this._header, this.caseInsensitiveHeader(this._headerString, this._header.asString()), this._valueString);
               }

               this._fieldCache.add(this._field);
            }
         }

         if (LOG.isDebugEnabled()) {
            LOG.debug(
               "parsedHeader({}) header={}, headerString=[{}], valueString=[{}]",
               new Object[]{this._field, this._header, this._headerString, this._valueString}
            );
         }

         this._handler.parsedHeader(this._field != null ? this._field : new HttpField(this._header, this._headerString, this._valueString));
      }

      this._headerString = this._valueString = null;
      this._header = null;
      this._field = null;
   }

   private void parsedTrailer() {
      if (this._headerString != null || this._valueString != null) {
         this._handler.parsedTrailer(this._field != null ? this._field : new HttpField(this._header, this._headerString, this._valueString));
      }

      this._headerString = this._valueString = null;
      this._header = null;
      this._field = null;
   }

   private long convertContentLength(String valueString) {
      if (valueString != null && valueString.length() != 0) {
         long value = 0L;
         int length = valueString.length();

         for (int i = 0; i < length; i++) {
            char c = valueString.charAt(i);
            if (c < '0' || c > '9') {
               throw new BadMessageException("Invalid Content-Length Value", new NumberFormatException());
            }

            value = Math.addExact(Math.multiplyExact(value, 10), (long)(c - '0'));
         }

         return value;
      } else {
         throw new BadMessageException("Invalid Content-Length Value", new NumberFormatException());
      }
   }

   protected boolean parseFields(ByteBuffer buffer) {
      while ((this._state == HttpParser.State.HEADER || this._state == HttpParser.State.TRAILER) && buffer.hasRemaining()) {
         HttpTokens.Token t = this.next(buffer);
         if (t != null) {
            if (this._maxHeaderBytes > 0 && ++this._headerBytes > this._maxHeaderBytes) {
               boolean header = this._state == HttpParser.State.HEADER;
               LOG.warn("{} is too large {}>{}", new Object[]{header ? "Header" : "Trailer", this._headerBytes, this._maxHeaderBytes});
               throw new BadMessageException(header ? 431 : 413);
            }

            switch (this._fieldState) {
               case FIELD:
                  switch (t.getType()) {
                     case LF:
                        if (this._state == HttpParser.State.HEADER) {
                           this.parsedHeader();
                        } else {
                           this.parsedTrailer();
                        }

                        this._contentPosition = 0L;
                        if (this._state == HttpParser.State.TRAILER) {
                           this.setState(HttpParser.State.END);
                           return this._handler.messageComplete();
                        }

                        if (!this._hasTransferEncoding
                           || this._endOfContent == HttpTokens.EndOfContent.CHUNKED_CONTENT
                           || this._responseHandler != null && this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT) {
                           if (this._parsedHost == null && this._version == HttpVersion.HTTP_1_1 && this._requestHandler != null) {
                              throw new BadMessageException("No Host");
                           }

                           if (this._responseHandler == null || this._responseStatus != 304 && this._responseStatus != 204 && this._responseStatus >= 200) {
                              if (this._endOfContent == HttpTokens.EndOfContent.UNKNOWN_CONTENT) {
                                 if (this._responseStatus != 0 && this._responseStatus != 304 && this._responseStatus != 204 && this._responseStatus >= 200) {
                                    this._endOfContent = HttpTokens.EndOfContent.EOF_CONTENT;
                                 } else {
                                    this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                                 }
                              }
                           } else {
                              this._endOfContent = HttpTokens.EndOfContent.NO_CONTENT;
                           }

                           boolean handle = this._handler.headerComplete();
                           this._headerComplete = true;
                           switch (this._endOfContent) {
                              case EOF_CONTENT:
                                 this.setState(HttpParser.State.EOF_CONTENT);
                                 break;
                              case CHUNKED_CONTENT:
                                 this.setState(HttpParser.State.CHUNKED_CONTENT);
                                 break;
                              default:
                                 this.setState(HttpParser.State.CONTENT);
                           }

                           return handle;
                        }

                        throw new BadMessageException("Bad Transfer-Encoding, chunked not last");
                     case CR:
                     case VCHAR:
                     case OTEXT:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                        if (this._state == HttpParser.State.HEADER) {
                           this.parsedHeader();
                        } else {
                           this.parsedTrailer();
                        }

                        if (buffer.hasRemaining()) {
                           HttpField cachedField = this._fieldCache.getBest(buffer, -1, buffer.remaining() + 1);
                           if (cachedField == null) {
                              cachedField = (HttpField)CACHE.getBest(buffer, -1, buffer.remaining() + 1);
                           }

                           if (cachedField != null) {
                              String n = cachedField.getName();
                              String v = cachedField.getValue();
                              if (HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME.isAllowedBy(this._complianceMode)) {
                                 String en = BufferUtil.toString(buffer, buffer.position() - 1, n.length(), StandardCharsets.US_ASCII);
                                 if (!n.equals(en)) {
                                    this.reportComplianceViolation(HttpCompliance.Violation.CASE_SENSITIVE_FIELD_NAME, en);
                                    n = en;
                                    cachedField = new HttpField(cachedField.getHeader(), en, v);
                                 }
                              }

                              if (v != null && this.isHeaderCacheCaseSensitive()) {
                                 String ev = BufferUtil.toString(buffer, buffer.position() + n.length() + 1, v.length(), StandardCharsets.ISO_8859_1);
                                 if (!v.equals(ev)) {
                                    v = ev;
                                    cachedField = new HttpField(cachedField.getHeader(), n, ev);
                                 }
                              }

                              this._header = cachedField.getHeader();
                              this._headerString = n;
                              int posAfterName = buffer.position() + n.length() + 1;
                              if (v != null && posAfterName + v.length() < buffer.limit()) {
                                 int posAfterValue = posAfterName + v.length();
                                 byte peek = buffer.get(posAfterValue);
                                 if (peek != 13 && peek != 10) {
                                    this.setState(HttpParser.FieldState.IN_VALUE);
                                    this.setString(v);
                                    buffer.position(posAfterValue);
                                    continue;
                                 }

                                 this._field = cachedField;
                                 this._valueString = v;
                                 buffer.position(posAfterValue + 1);
                                 if (peek == 10) {
                                    this.setState(HttpParser.FieldState.FIELD);
                                    continue;
                                 }

                                 this.setState(HttpParser.FieldState.IN_VALUE);
                                 this._cr = true;
                                 continue;
                              }

                              this.setState(HttpParser.FieldState.VALUE);
                              this._string.setLength(0);
                              this._length = 0;
                              buffer.position(posAfterName);
                              continue;
                           }
                        }

                        this.setState(HttpParser.FieldState.IN_NAME);
                        this._string.setLength(0);
                        this._string.append(t.getChar());
                        this._length = 1;
                        continue;
                     case HTAB:
                     case SPACE:
                     case COLON:
                        this.checkViolation(HttpCompliance.Violation.MULTILINE_FIELD_VALUE);
                        if (StringUtil.isEmpty(this._valueString)) {
                           this._string.setLength(0);
                           this._length = 0;
                        } else {
                           this.setString(this._valueString);
                           this._string.append(' ');
                           this._length++;
                           this._valueString = null;
                        }

                        this.setState(HttpParser.FieldState.VALUE);
                        continue;
                  }
               case IN_NAME:
                  switch (t.getType()) {
                     case LF:
                        this._headerString = this.takeString();
                        this._header = (HttpHeader)HttpHeader.CACHE.get(this._headerString);
                        this._string.setLength(0);
                        this._valueString = "";
                        this._length = -1;
                        if (HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME.isAllowedBy(this._complianceMode)) {
                           this.reportComplianceViolation(HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME, "Field " + this._headerString);
                           this.setState(HttpParser.FieldState.FIELD);
                           continue;
                        }

                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case CR:
                     case VCHAR:
                     case OTEXT:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                        this._string.append(t.getChar());
                        this._length = this._string.length();
                        continue;
                     case HTAB:
                     case SPACE:
                        if (HttpCompliance.Violation.WHITESPACE_AFTER_FIELD_NAME.isAllowedBy(this._complianceMode)) {
                           this._headerString = this.takeString();
                           this.reportComplianceViolation(HttpCompliance.Violation.WHITESPACE_AFTER_FIELD_NAME, "Space after " + this._headerString);
                           this._header = (HttpHeader)HttpHeader.CACHE.get(this._headerString);
                           this._length = -1;
                           this.setState(HttpParser.FieldState.WS_AFTER_NAME);
                           continue;
                        }

                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case COLON:
                        this._headerString = this.takeString();
                        this._header = (HttpHeader)HttpHeader.CACHE.get(this._headerString);
                        this._length = -1;
                        this.setState(HttpParser.FieldState.VALUE);
                        continue;
                  }
               case VALUE:
                  switch (t.getType()) {
                     case LF:
                        this._string.setLength(0);
                        this._valueString = "";
                        this._length = -1;
                        this.setState(HttpParser.FieldState.FIELD);
                        continue;
                     case CR:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case OTEXT:
                     case COLON:
                        this._string.append(t.getChar());
                        this._length = this._string.length();
                        this.setState(HttpParser.FieldState.IN_VALUE);
                     case HTAB:
                     case SPACE:
                        continue;
                  }
               case IN_VALUE:
                  switch (t.getType()) {
                     case LF:
                        if (this._length > 0) {
                           this._valueString = this.takeString();
                           this._length = -1;
                        }

                        this.setState(HttpParser.FieldState.FIELD);
                        continue;
                     case CR:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case OTEXT:
                     case COLON:
                        this._string.append(t.getChar());
                        this._length = this._string.length();
                        continue;
                     case HTAB:
                     case SPACE:
                        this._string.append(t.getChar());
                        continue;
                  }
               case WS_AFTER_NAME:
                  switch (t.getType()) {
                     case LF:
                        if (HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME.isAllowedBy(this._complianceMode)) {
                           this.reportComplianceViolation(HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME, "Field " + this._headerString);
                           this.setState(HttpParser.FieldState.FIELD);
                           continue;
                        }

                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case CR:
                     case ALPHA:
                     case DIGIT:
                     case TCHAR:
                     case VCHAR:
                     case OTEXT:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, t, buffer);
                     case HTAB:
                     case SPACE:
                        continue;
                     case COLON:
                        this.setState(HttpParser.FieldState.VALUE);
                        continue;
                  }
               default:
                  throw new IllegalStateException(this._state.toString());
            }
         }
         break;
      }

      return false;
   }

   public boolean parseNext(ByteBuffer buffer) {
      if (this.debugEnabled) {
         LOG.debug("parseNext s={} {}", this._state, BufferUtil.toDetailString(buffer));
      }

      try {
         if (this._state == HttpParser.State.START) {
            this._version = null;
            this._method = null;
            this._methodString = null;
            this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
            this._header = null;
            if (buffer.hasRemaining()) {
               this._beginNanoTime = NanoTime.now();
            }

            this.quickStart(buffer);
         }

         if (this._state.ordinal() < HttpParser.State.HEADER.ordinal() && this.parseLine(buffer)) {
            return true;
         }

         if (this._state == HttpParser.State.HEADER && this.parseFields(buffer)) {
            return true;
         }

         if (this._state.ordinal() >= HttpParser.State.CONTENT.ordinal() && this._state.ordinal() < HttpParser.State.TRAILER.ordinal()) {
            if (this._responseStatus > 0 && this._headResponse) {
               if (this._state != HttpParser.State.CONTENT_END) {
                  this.setState(HttpParser.State.CONTENT_END);
                  return this.handleContentMessage();
               }

               this.setState(HttpParser.State.END);
               return this._handler.messageComplete();
            }

            if (this.parseContent(buffer)) {
               return true;
            }
         }

         if (this._state == HttpParser.State.TRAILER && this.parseFields(buffer)) {
            return true;
         }

         if (this._state != HttpParser.State.END) {
            if (this.isTerminated()) {
               BufferUtil.clear(buffer);
            }
         } else {
            int whiteSpace;
            for (whiteSpace = 0; buffer.remaining() > 0; whiteSpace++) {
               byte b = buffer.get(buffer.position());
               if (b != 13 && b != 10) {
                  break;
               }

               buffer.get();
            }

            if (this.debugEnabled && whiteSpace > 0) {
               LOG.debug("Discarded {} CR or LF characters", whiteSpace);
            }
         }

         if (this.isAtEOF() && !buffer.hasRemaining()) {
            switch (this._state) {
               case START:
               case CONTENT:
               case CHUNKED_CONTENT:
               case CHUNK_SIZE:
               case CHUNK_PARAMS:
               case CHUNK:
                  this.setState(HttpParser.State.CLOSED);
                  this._handler.earlyEOF();
                  break;
               case METHOD:
               case RESPONSE_VERSION:
               case SPACE1:
               case STATUS:
               case URI:
               case SPACE2:
               case REQUEST_VERSION:
               case REASON:
               case PROXY:
               case HEADER:
               case CONTENT_END:
               default:
                  if (this.debugEnabled) {
                     LOG.debug("{} EOF in {}", this, this._state);
                  }

                  this.setState(HttpParser.State.CLOSED);
                  this._handler.badMessage(new BadMessageException(400));
                  break;
               case EOF_CONTENT:
               case TRAILER:
                  if (this._fieldState == HttpParser.FieldState.FIELD) {
                     this.setState(HttpParser.State.CONTENT_END);
                     boolean handle = this.handleContentMessage();
                     if (handle && this._state == HttpParser.State.CONTENT_END) {
                        return true;
                     }

                     this.setState(HttpParser.State.CLOSED);
                     return handle;
                  }

                  this.setState(HttpParser.State.CLOSED);
                  this._handler.earlyEOF();
                  break;
               case END:
               case CLOSE:
                  this.setState(HttpParser.State.CLOSED);
               case CLOSED:
            }
         }
      } catch (Throwable var5) {
         BufferUtil.clear(buffer);
         HttpException bad = (HttpException)(var5 instanceof HttpException http
            ? http
            : new BadMessageException(this._requestHandler != null ? "Bad Request" : "Bad Response", var5));
         this.badMessage(bad);
      }

      return false;
   }

   protected void badMessage(HttpException x) {
      if (this.debugEnabled) {
         LOG.debug("Parse exception: {} for {}", new Object[]{this, this._handler, x});
      }

      this.setState(HttpParser.State.CLOSE);
      if (this._headerComplete) {
         this._handler.earlyEOF();
      } else {
         this._handler.badMessage(x);
      }
   }

   protected boolean parseContent(ByteBuffer buffer) {
      int remaining = buffer.remaining();
      if (remaining == 0) {
         switch (this._state) {
            case CONTENT:
               long content = this._contentLength - this._contentPosition;
               if (this._endOfContent == HttpTokens.EndOfContent.NO_CONTENT || content == 0L) {
                  this.setState(HttpParser.State.CONTENT_END);
                  return this.handleContentMessage();
               }
               break;
            case CONTENT_END:
               this.setState(this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT ? HttpParser.State.CLOSED : HttpParser.State.END);
               return this._handler.messageComplete();
            default:
               return false;
         }
      }

      for (; this._state.ordinal() < HttpParser.State.TRAILER.ordinal() && remaining > 0; remaining = buffer.remaining()) {
         switch (this._state) {
            case CONTENT:
               long content = this._contentLength - this._contentPosition;
               if (this._endOfContent == HttpTokens.EndOfContent.NO_CONTENT || content == 0L) {
                  this.setState(HttpParser.State.CONTENT_END);
                  return this.handleContentMessage();
               }

               this._contentChunk = buffer.asReadOnlyBuffer();
               if (this._contentLength > -1L && (long)remaining > content) {
                  this._contentChunk.limit(this._contentChunk.position() + (int)content);
               }

               this._contentPosition = this._contentPosition + (long)this._contentChunk.remaining();
               buffer.position(buffer.position() + this._contentChunk.remaining());
               if (this._handler.content(this._contentChunk)) {
                  return true;
               }

               if (this._contentPosition == this._contentLength) {
                  this.setState(HttpParser.State.CONTENT_END);
                  return this.handleContentMessage();
               }
               break;
            case EOF_CONTENT:
               this._contentChunk = buffer.asReadOnlyBuffer();
               this._contentPosition += (long)remaining;
               buffer.position(buffer.position() + remaining);
               if (this._handler.content(this._contentChunk)) {
                  return true;
               }
               break;
            case CHUNKED_CONTENT:
               HttpTokens.Token txx = this.next(buffer);
               if (txx != null) {
                  switch (txx.getType()) {
                     case LF:
                        break;
                     case CR:
                     default:
                        throw new HttpParser.IllegalCharacterException(this._state, txx, buffer);
                     case ALPHA:
                        if (!txx.isHexDigit()) {
                           throw new HttpParser.IllegalCharacterException(this._state, txx, buffer);
                        }

                        this._chunkLength = txx.getHexDigit();
                        this._chunkPosition = 0;
                        this.setState(HttpParser.State.CHUNK_SIZE);
                        break;
                     case DIGIT:
                        this._chunkLength = txx.getHexDigit();
                        this._chunkPosition = 0;
                        this.setState(HttpParser.State.CHUNK_SIZE);
                  }
               }
               break;
            case CHUNK_SIZE:
               HttpTokens.Token tx = this.next(buffer);
               if (tx != null) {
                  switch (tx.getType()) {
                     case LF:
                        if (this._chunkLength == 0) {
                           this.setState(HttpParser.State.TRAILER);
                           if (this._handler.contentComplete()) {
                              return true;
                           }
                        } else {
                           this.setState(HttpParser.State.CHUNK);
                        }
                        break;
                     default:
                        if (tx.isHexDigit()) {
                           if (this._chunkLength > 134217711) {
                              throw new BadMessageException(413);
                           }

                           this._chunkLength = this._chunkLength * 16 + tx.getHexDigit();
                        } else {
                           if (tx.getChar() != ';') {
                              throw new HttpParser.IllegalCharacterException(this._state, tx, buffer);
                           }

                           this.setState(HttpParser.State.CHUNK_PARAMS);
                        }
                  }
               }
               break;
            case CHUNK_PARAMS:
               HttpTokens.Token t = this.next(buffer);
               if (t != null) {
                  switch (t.getType()) {
                     case LF:
                        if (this._chunkLength == 0) {
                           this.setState(HttpParser.State.TRAILER);
                           if (this._handler.contentComplete()) {
                              return true;
                           }
                        } else {
                           this.setState(HttpParser.State.CHUNK);
                        }
                  }
               }
               break;
            case CHUNK:
               int chunk = this._chunkLength - this._chunkPosition;
               if (chunk == 0) {
                  this.setState(HttpParser.State.CHUNKED_CONTENT);
               } else {
                  this._contentChunk = buffer.asReadOnlyBuffer();
                  if (remaining > chunk) {
                     this._contentChunk.limit(this._contentChunk.position() + chunk);
                  }

                  chunk = this._contentChunk.remaining();
                  this._contentPosition += (long)chunk;
                  this._chunkPosition += chunk;
                  buffer.position(buffer.position() + chunk);
                  if (this._handler.content(this._contentChunk)) {
                     return true;
                  }
               }
               break;
            case CONTENT_END:
               this.setState(this._endOfContent == HttpTokens.EndOfContent.EOF_CONTENT ? HttpParser.State.CLOSED : HttpParser.State.END);
               return this._handler.messageComplete();
         }
      }

      return false;
   }

   public boolean isAtEOF() {
      return this._eof;
   }

   public void atEOF() {
      if (this.debugEnabled) {
         LOG.debug("atEOF {}", this);
      }

      this._eof = true;
   }

   public void close() {
      if (this.debugEnabled) {
         LOG.debug("close {}", this);
      }

      this.setState(HttpParser.State.CLOSE);
   }

   public void reset() {
      if (this.debugEnabled) {
         LOG.debug("reset {}", this);
      }

      if (this._state != HttpParser.State.CLOSE && this._state != HttpParser.State.CLOSED) {
         this.setState(HttpParser.State.START);
         this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
         this._contentLength = -1L;
         this._hasContentLength = false;
         this._hasTransferEncoding = false;
         this._contentPosition = 0L;
         this._responseStatus = 0;
         this._contentChunk = null;
         this._headerBytes = 0;
         this._parsedHost = null;
         this._headerComplete = false;
      }
   }

   public void servletUpgrade() {
      this.setState(HttpParser.State.CONTENT);
      this._endOfContent = HttpTokens.EndOfContent.UNKNOWN_CONTENT;
      this._contentLength = -1L;
   }

   protected void setState(HttpParser.State state) {
      if (this.debugEnabled) {
         String info = switch (state) {
            case SPACE1 -> this._requestHandler == null ? this._version.asString() : this._methodString;
            case SPACE2 -> this._requestHandler == null ? Integer.toString(this._responseStatus) : this._uri.toCompleteString();
            case CONTENT_END, TRAILER -> Long.toString(this._contentPosition);
            default -> null;
         };
         if (info == null) {
            LOG.debug("{} --> {}", this._state, state);
         } else {
            LOG.debug("{} --> {}({})", new Object[]{this._state, state, info});
         }
      }

      this._state = state;
   }

   protected void setState(HttpParser.FieldState state) {
      if (this.debugEnabled) {
         if (state != HttpParser.FieldState.FIELD) {
            LOG.debug("{}:{} --> {}", new Object[]{this._state, this._fieldState, state});
         } else {
            LOG.debug(
               "{}:{} --> {}({}: {})",
               new Object[]{this._state, this._fieldState, state, this._field != null ? this._field : this._headerString, this._valueString}
            );
         }
      }

      this._fieldState = state;
   }

   public Index<HttpField> getFieldCache() {
      this._fieldCache.prepare();
      return this._fieldCache.getCache();
   }

   @Override
   public String toString() {
      return String.format("%s{s=%s,%d of %d}", this.getClass().getSimpleName(), this._state, this.getContentRead(), this.getContentLength());
   }

   private static class FieldCache {
      private int _size = 1024;
      private Mutable<HttpField> _cache;
      private List<HttpField> _cacheableFields;
      private boolean _caseSensitive;

      public int getCapacity() {
         return this._size;
      }

      public void setCapacity(int size) {
         this._size = size;
         if (this._size <= 0) {
            this._cache = HttpParser.NO_CACHE;
         } else {
            this._cache = null;
         }
      }

      public boolean isCaseSensitive() {
         return this._caseSensitive;
      }

      public void setCaseSensitive(boolean caseSensitive) {
         this._caseSensitive = caseSensitive;
      }

      public boolean isEnabled() {
         return this._cache != HttpParser.NO_CACHE;
      }

      public Index<HttpField> getCache() {
         return this._cache;
      }

      public HttpField getBest(ByteBuffer buffer, int i, int remaining) {
         Mutable<HttpField> cache = this._cache;
         return cache == null ? null : (HttpField)this._cache.getBest(buffer, i, remaining);
      }

      public void add(HttpField field) {
         if (this._cache == null) {
            if (this._cacheableFields == null) {
               this._cacheableFields = new ArrayList<>();
            }

            this._cacheableFields.add(field);
         } else if (!this._cache.put(field)) {
            this._cache.clear();
            this._cache.put(field);
         }
      }

      public boolean cacheable(HttpHeader header, String valueString) {
         return this.isEnabled() && header != null && valueString.length() <= this._size;
      }

      private void prepare() {
         if (this._cache == null && this._cacheableFields != null) {
            this._cache = Index.buildMutableVisibleAsciiAlphabet(this._caseSensitive, this._size);

            for (HttpField f : this._cacheableFields) {
               if (!this._cache.put(f)) {
                  break;
               }
            }

            this._cacheableFields.clear();
            this._cacheableFields = null;
         }
      }
   }

   public static enum FieldState {
      FIELD,
      IN_NAME,
      VALUE,
      IN_VALUE,
      WS_AFTER_NAME;
   }

   public interface HttpHandler {
      boolean content(ByteBuffer var1);

      boolean headerComplete();

      boolean contentComplete();

      boolean messageComplete();

      void parsedHeader(HttpField var1);

      default void parsedTrailer(HttpField field) {
      }

      void earlyEOF();

      default void badMessage(HttpException failure) {
      }
   }

   private static class IllegalCharacterException extends BadMessageException {
      private IllegalCharacterException(HttpParser.State state, HttpTokens.Token token, ByteBuffer buffer) {
         super(String.format("Illegal character %s", token));
         if (HttpParser.LOG.isDebugEnabled()) {
            HttpParser.LOG.debug(String.format("Illegal character %s in state=%s for buffer %s", token, state, BufferUtil.toDetailString(buffer)));
         }
      }
   }

   public interface RequestHandler extends HttpParser.HttpHandler {
      void startRequest(String var1, String var2, HttpVersion var3);
   }

   public interface ResponseHandler extends HttpParser.HttpHandler {
      void startResponse(HttpVersion var1, int var2, String var3);
   }

   public static enum State {
      START,
      METHOD,
      RESPONSE_VERSION,
      SPACE1,
      STATUS,
      URI,
      SPACE2,
      REQUEST_VERSION,
      REASON,
      PROXY,
      HEADER,
      CONTENT,
      EOF_CONTENT,
      CHUNKED_CONTENT,
      CHUNK_SIZE,
      CHUNK_PARAMS,
      CHUNK,
      CONTENT_END,
      TRAILER,
      END,
      CLOSE,
      CLOSED;
   }
}
