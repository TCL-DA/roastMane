package org.eclipse.jetty.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jetty.http.ByteRange;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.DateParser;
import org.eclipse.jetty.http.EtagUtils;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.MultiPart;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.http.QuotedQualityCSV;
import org.eclipse.jetty.http.HttpURI.Mutable;
import org.eclipse.jetty.http.MultiPartByteRanges.ContentSource;
import org.eclipse.jetty.http.MultiPartByteRanges.InputStreamContentSource;
import org.eclipse.jetty.http.MultiPartByteRanges.Part;
import org.eclipse.jetty.http.MultiPartByteRanges.PathContentSource;
import org.eclipse.jetty.http.content.HttpContent;
import org.eclipse.jetty.http.content.PreCompressedHttpContent;
import org.eclipse.jetty.http.content.HttpContent.Factory;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Content.Sink;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceService {
   private static final Logger LOG = LoggerFactory.getLogger(ResourceService.class);
   private static final int NO_CONTENT_LENGTH = -1;
   private static final int USE_KNOWN_CONTENT_LENGTH = -2;
   private final List<CompressedContentFormat> _precompressedFormats = new ArrayList<>();
   private final Map<String, List<String>> _preferredEncodingOrderCache = new ConcurrentHashMap<>();
   private final List<String> _preferredEncodingOrder = new ArrayList<>();
   private ResourceService.WelcomeFactory _welcomeFactory;
   private ResourceService.WelcomeMode _welcomeMode = ResourceService.WelcomeMode.SERVE;
   private boolean _etags = false;
   private List<String> _gzipEquivalentFileExtensions;
   private Factory _contentFactory;
   private int _encodingCacheSize = 100;
   private boolean _dirAllowed = true;
   private boolean _acceptRanges = true;
   private HttpField _cacheControl;

   public HttpContent getContent(String path, Request request) throws IOException {
      HttpContent content = this._contentFactory.getContent(path == null ? "" : path);
      if (content != null) {
         AliasCheck aliasCheck = ContextHandler.getContextHandler(request);
         if (aliasCheck != null && !aliasCheck.checkAlias(path, content.getResource())) {
            return null;
         }

         Collection<CompressedContentFormat> compressedContentFormats = (Collection<CompressedContentFormat>)(content.getPreCompressedContentFormats() == null
            ? this._precompressedFormats
            : content.getPreCompressedContentFormats());
         if (!compressedContentFormats.isEmpty()) {
            List<String> preferredEncodingOrder = this.getPreferredEncodingOrder(request);
            if (!preferredEncodingOrder.isEmpty()) {
               for (String encoding : preferredEncodingOrder) {
                  CompressedContentFormat contentFormat = this.isEncodingAvailable(encoding, compressedContentFormats);
                  if (contentFormat != null) {
                     HttpContent preCompressedContent = this._contentFactory.getContent(path + contentFormat.getExtension());
                     if (preCompressedContent != null && (aliasCheck == null || aliasCheck.checkAlias(path, preCompressedContent.getResource()))) {
                        return new PreCompressedHttpContent(content, preCompressedContent, contentFormat);
                     }
                  }
               }
            }
         }
      }

      return content;
   }

   public Factory getHttpContentFactory() {
      return this._contentFactory;
   }

   public void setHttpContentFactory(Factory contentFactory) {
      this._contentFactory = contentFactory;
   }

   public String getCacheControl() {
      return this._cacheControl.getValue();
   }

   public List<String> getGzipEquivalentFileExtensions() {
      return this._gzipEquivalentFileExtensions;
   }

   public void doGet(Request request, Response response, Callback callback, HttpContent content) {
      String pathInContext = Request.getPathInContext(request);
      List<String> reqRanges = request.getHeaders().getValuesList(HttpHeader.RANGE.asString());
      if (!this._acceptRanges && !reqRanges.isEmpty()) {
         reqRanges = List.of();
         response.getHeaders().add(HttpHeader.ACCEPT_RANGES.asString(), "none");
      }

      boolean endsWithSlash = pathInContext.endsWith("/");
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            ".doGet(req={}, resp={}, callback={}, content={}) pathInContext={}, reqRanges={}, endsWithSlash={}",
            new Object[]{request, response, callback, content, pathInContext, reqRanges, endsWithSlash}
         );
      }

      try {
         if (content.getResource().isDirectory()) {
            this.sendWelcome(content, pathInContext, endsWithSlash, request, response, callback);
            return;
         }

         if (endsWithSlash && pathInContext.length() > 1) {
            String q = request.getHttpURI().getQuery();
            pathInContext = pathInContext.substring(0, pathInContext.length() - 1);
            if (q != null && q.length() != 0) {
               pathInContext = pathInContext + "?" + q;
            }

            this.sendRedirect(request, response, callback, URIUtil.addPaths(request.getContext().getContextPath(), pathInContext));
            return;
         }

         if (this.passConditionalHeaders(request, response, content, callback)) {
            return;
         }

         if (content.getPreCompressedContentFormats() == null || !content.getPreCompressedContentFormats().isEmpty()) {
            response.getHeaders().put(HttpHeader.VARY, HttpHeader.ACCEPT_ENCODING.asString());
         }

         HttpField contentEncoding = content.getContentEncoding();
         if (contentEncoding != null) {
            response.getHeaders().put(contentEncoding);
         } else if (this.isImplicitlyGzippedContent(pathInContext)) {
            response.getHeaders().put(HttpHeader.CONTENT_ENCODING, "gzip");
         }

         this.sendData(request, response, callback, content, reqRanges);
      } catch (Throwable var9) {
         LOG.warn("Failed to serve resource: {}", pathInContext, var9);
         if (!response.isCommitted()) {
            this.writeHttpError(request, response, callback, var9);
         } else {
            callback.failed(var9);
         }
      }
   }

   protected void writeHttpError(Request request, Response response, Callback callback, int status) {
      Response.writeError(request, response, callback, status);
   }

   protected void writeHttpError(Request request, Response response, Callback callback, Throwable cause) {
      Response.writeError(request, response, callback, cause);
   }

   protected void writeHttpError(Request request, Response response, Callback callback, int status, String msg, Throwable cause) {
      Response.writeError(request, response, callback, status, msg, cause);
   }

   protected void sendRedirect(Request request, Response response, Callback callback, String target) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendRedirect(req={}, resp={}, callback={}, target={})", new Object[]{request, response, callback, target});
      }

      Response.sendRedirect(request, response, callback, target);
   }

   private List<String> getPreferredEncodingOrder(Request request) {
      Enumeration<String> headers = request.getHeaders().getValues(HttpHeader.ACCEPT_ENCODING.asString());
      if (!headers.hasMoreElements()) {
         return Collections.emptyList();
      } else {
         String key = headers.nextElement();
         if (headers.hasMoreElements()) {
            StringBuilder sb = new StringBuilder(key.length() * 2);

            do {
               sb.append(',').append(headers.nextElement());
            } while (headers.hasMoreElements());

            key = sb.toString();
         }

         List<String> values = this._preferredEncodingOrderCache.get(key);
         if (values == null) {
            QuotedQualityCSV encodingQualityCSV = new QuotedQualityCSV(this._preferredEncodingOrder);
            encodingQualityCSV.addValue(key);
            values = encodingQualityCSV.getValues();
            if (this._preferredEncodingOrderCache.size() > this._encodingCacheSize) {
               this._preferredEncodingOrderCache.clear();
            }

            this._preferredEncodingOrderCache.put(key, values);
         }

         return values;
      }
   }

   private boolean isImplicitlyGzippedContent(String path) {
      if (path != null && this._gzipEquivalentFileExtensions != null) {
         for (String suffix : this._gzipEquivalentFileExtensions) {
            if (path.endsWith(suffix)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private CompressedContentFormat isEncodingAvailable(String encoding, Collection<CompressedContentFormat> availableFormats) {
      if (availableFormats.isEmpty()) {
         return null;
      } else {
         for (CompressedContentFormat format : availableFormats) {
            if (format.getEncoding().equals(encoding)) {
               return format;
            }
         }

         return "*".equals(encoding) ? availableFormats.iterator().next() : null;
      }
   }

   protected boolean passConditionalHeaders(Request request, Response response, HttpContent content, Callback callback) throws IOException {
      try {
         String ifm = null;
         String ifnm = null;
         String ifms = null;
         String ifums = null;

         for (HttpField field : request.getHeaders()) {
            if (field.getHeader() != null) {
               switch (field.getHeader()) {
                  case IF_MATCH:
                     ifm = field.getValue();
                     break;
                  case IF_NONE_MATCH:
                     ifnm = field.getValue();
                     break;
                  case IF_MODIFIED_SINCE:
                     ifms = field.getValue();
                     break;
                  case IF_UNMODIFIED_SINCE:
                     ifums = field.getValue();
               }
            }
         }

         if (this._etags) {
            String etag = content.getETagValue();
            if (etag != null) {
               etag = EtagUtils.rewriteWithSuffix(content.getETagValue(), "");
               if (ifm != null) {
                  String matched = this.matchesEtag(etag, ifm);
                  if (matched == null) {
                     this.writeHttpError(request, response, callback, 412);
                     return true;
                  }
               }

               if (ifnm != null) {
                  String matched = this.matchesEtag(etag, ifnm);
                  if (matched != null) {
                     response.getHeaders().put(HttpHeader.ETAG, matched);
                     this.writeHttpError(request, response, callback, 304);
                     return true;
                  }

                  return false;
               }
            }
         }

         if (ifms != null && ifnm == null) {
            String mdlm = content.getLastModifiedValue();
            if (ifms.equals(mdlm)) {
               this.writeHttpError(request, response, callback, 304);
               return true;
            }

            long ifmsl = DateParser.parseDate(ifms);
            if (ifmsl != -1L) {
               long lm = content.getResource().lastModified().toEpochMilli();
               if (lm != -1L && lm / 1000L <= ifmsl / 1000L) {
                  this.writeHttpError(request, response, callback, 304);
                  return true;
               }
            }
         }

         if (ifums != null && ifm == null) {
            long ifumsl = DateParser.parseDate(ifums);
            if (ifumsl != -1L) {
               long lm = content.getResource().lastModified().toEpochMilli();
               if (lm != -1L && lm / 1000L > ifumsl / 1000L) {
                  this.writeHttpError(request, response, callback, 412);
                  return true;
               }
            }
         }

         return false;
      } catch (IllegalArgumentException var14) {
         if (!response.isCommitted()) {
            this.writeHttpError(request, response, callback, 400, null, var14);
         }

         throw var14;
      }
   }

   private String matchesEtag(String contentETag, String requestEtag) {
      if (contentETag != null && requestEtag != null) {
         for (String tag : new QuotedCSV(true, new String[]{requestEtag})) {
            if (EtagUtils.matches(contentETag, tag)) {
               return tag;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   protected void sendWelcome(HttpContent content, String pathInContext, boolean endsWithSlash, Request request, Response response, Callback callback) throws Exception {
      if (!Objects.requireNonNull(content).getResource().isDirectory()) {
         throw new IllegalArgumentException("content must be a directory");
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug(
               "sendWelcome(content={}, pathInContext={}, endsWithSlash={}, req={}, resp={}, callback={})",
               new Object[]{content, pathInContext, endsWithSlash, request, response, callback}
            );
         }

         if (!endsWithSlash) {
            Mutable uri = HttpURI.build(request.getHttpURI());
            if (!uri.getCanonicalPath().endsWith("/")) {
               uri.path(uri.getCanonicalPath() + "/");
               response.getHeaders().put(HttpFields.CONTENT_LENGTH_0);
               this.sendRedirect(request, response, callback, uri.getPathQuery());
               return;
            }
         }

         if (!this.welcome(content, request, response, callback)) {
            if (!this.passConditionalHeaders(request, response, content, callback)) {
               this.sendDirectory(request, response, content, callback, pathInContext);
            }
         }
      }
   }

   private boolean welcome(HttpContent content, Request request, Response response, Callback callback) throws Exception {
      ResourceService.WelcomeAction welcomeAction = this.processWelcome(content, request);
      if (LOG.isDebugEnabled()) {
         LOG.debug("welcome(req={}, rsp={}, cbk={}) welcomeAction={}", new Object[]{request, response, callback, welcomeAction});
      }

      if (welcomeAction == null) {
         return false;
      } else {
         this.handleWelcomeAction(request, response, callback, welcomeAction);
         return true;
      }
   }

   protected void handleWelcomeAction(Request request, Response response, Callback callback, ResourceService.WelcomeAction welcomeAction) throws Exception {
      switch (welcomeAction.mode) {
         case REDIRECT:
            this.redirectWelcome(request, response, callback, welcomeAction.target);
            break;
         case SERVE:
            this.serveWelcome(request, response, callback, welcomeAction.target);
            break;
         case REHANDLE:
            this.rehandleWelcome(request, response, callback, welcomeAction.target);
      }
   }

   protected void redirectWelcome(Request request, Response response, Callback callback, String welcomeTarget) throws Exception {
      response.getHeaders().put(HttpFields.CONTENT_LENGTH_0);
      this.sendRedirect(request, response, callback, welcomeTarget);
   }

   protected void serveWelcome(Request request, Response response, Callback callback, String welcomeTarget) throws Exception {
      HttpContent c = this._contentFactory.getContent(welcomeTarget);
      this.sendData(request, response, callback, c, List.of());
   }

   protected void rehandleWelcome(Request request, Response response, Callback callback, String welcomeTarget) throws Exception {
      Response.writeError(request, response, callback, 500);
   }

   private ResourceService.WelcomeAction processWelcome(HttpContent content, Request request) throws IOException {
      String welcomeTarget = this.getWelcomeFactory().getWelcomeTarget(content, request);
      if (welcomeTarget == null) {
         return null;
      } else {
         String contextPath = request.getContext().getContextPath();
         ResourceService.WelcomeMode welcomeMode = this.getWelcomeMode();

         welcomeTarget = switch (welcomeMode) {
            case REDIRECT, REHANDLE -> HttpURI.build(request.getHttpURI()).path(URIUtil.addPaths(contextPath, welcomeTarget)).getPathQuery();
            case SERVE -> welcomeTarget;
         };
         if (LOG.isDebugEnabled()) {
            LOG.debug("welcome {} {}", welcomeMode, welcomeTarget);
         }

         return new ResourceService.WelcomeAction(welcomeTarget, welcomeMode);
      }
   }

   private void sendDirectory(Request request, Response response, HttpContent httpContent, Callback callback, String pathInContext) {
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "sendDirectory(req={}, resp={}, content={}, callback={}, pathInContext={})", new Object[]{request, response, httpContent, callback, pathInContext}
         );
      }

      if (!this._dirAllowed) {
         this.writeHttpError(request, response, callback, 403);
      } else {
         String base = URIUtil.addEncodedPaths(request.getHttpURI().getPath(), "/");
         String listing = ResourceListing.getAsXHTML(httpContent.getResource(), base, pathInContext.length() > 1, request.getHttpURI().getQuery());
         if (listing == null) {
            this.writeHttpError(request, response, callback, 403);
         } else {
            String characterEncoding = httpContent.getCharacterEncoding();
            Charset charset = characterEncoding == null ? StandardCharsets.UTF_8 : Charset.forName(characterEncoding);
            byte[] data = listing.getBytes(charset);
            response.getHeaders().put(HttpHeader.CONTENT_TYPE, "text/html;charset=" + charset.name());
            response.getHeaders().put(HttpHeader.CONTENT_LENGTH, (long)data.length);
            response.write(true, ByteBuffer.wrap(data), callback);
         }
      }
   }

   private void sendData(Request request, Response response, Callback callback, HttpContent content, List<String> reqRanges) throws IOException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("sendData(req={}, resp={}, callback={}) content={}, reqRanges={})", new Object[]{request, response, callback, content, reqRanges});
      }

      long contentLength = content.getContentLengthValue();
      callback = Callback.from(callback, content::release);
      if (LOG.isDebugEnabled()) {
         LOG.debug(String.format("sendData content=%s", content));
      }

      if (reqRanges.isEmpty()) {
         if (contentLength >= 0L) {
            this.putHeaders(response, content, -2L);
         } else {
            this.putHeaders(response, content, -1L);
         }

         this.writeHttpContent(request, response, callback, content);
      } else {
         List<ByteRange> ranges = ByteRange.parse(reqRanges, contentLength);
         if (ranges.isEmpty()) {
            response.getHeaders().put(HttpHeader.CONTENT_RANGE, ByteRange.toNonSatisfiableHeaderValue(contentLength));
            Response.writeError(request, response, callback, 416);
         } else if (ranges.size() == 1) {
            ByteRange range = ranges.get(0);
            this.putHeaders(response, content, range.getLength());
            response.setStatus(206);
            response.getHeaders().put(HttpHeader.CONTENT_RANGE, range.toHeaderValue(contentLength));
            Path path = content.getResource().getPath();
            Source source;
            if (path != null) {
               source = new PathContentSource(path, range);
            } else {
               source = new InputStreamContentSource(content.getResource().newInputStream(), range);
            }

            Content.copy(source, response, callback);
         } else {
            response.setStatus(206);
            String contentType = "multipart/byteranges; boundary=";
            String boundary = MultiPart.generateBoundary(null, 24);
            ContentSource byteRanges = new ContentSource(boundary);
            ranges.forEach(range -> byteRanges.addPart(new Part(content.getContentTypeValue(), content.getResource(), range, contentLength)));
            byteRanges.close();
            long partsContentLength = byteRanges.getLength();
            this.putHeaders(response, content, partsContentLength);
            response.getHeaders().put(HttpHeader.CONTENT_TYPE, contentType + boundary);
            Content.copy(byteRanges, response, callback);
         }
      }
   }

   protected void writeHttpContent(Request request, Response response, Callback callback, HttpContent content) {
      try {
         ByteBuffer buffer = content.getByteBuffer();
         if (buffer != null) {
            response.write(true, buffer, callback);
         } else {
            new ResourceService.ContentWriterIteratingCallback(content, response, callback).iterate();
         }
      } catch (Throwable var6) {
         content.release();
         callback.failed(var6);
      }
   }

   protected void putHeaders(Response response, HttpContent content, long contentLength) {
      HttpField lm = content.getLastModified();
      if (lm != null) {
         response.getHeaders().put(lm);
      }

      if (contentLength == -2L) {
         response.getHeaders().put(content.getContentLength());
      } else if (contentLength > -1L) {
         response.getHeaders().put(HttpHeader.CONTENT_LENGTH, contentLength);
      }

      HttpField ct = content.getContentType();
      if (ct != null) {
         response.getHeaders().put(ct);
      }

      HttpField ce = content.getContentEncoding();
      if (ce != null) {
         response.getHeaders().put(ce);
      }

      if (this._etags) {
         HttpField et = content.getETag();
         if (et != null) {
            response.getHeaders().put(et);
         }
      }

      if (this._acceptRanges && !response.getHeaders().contains(HttpHeader.ACCEPT_RANGES)) {
         response.getHeaders().put(new PreEncodedHttpField(HttpHeader.ACCEPT_RANGES, "bytes"));
      }

      if (this._cacheControl != null && !response.getHeaders().contains(HttpHeader.CACHE_CONTROL)) {
         response.getHeaders().put(this._cacheControl);
      }
   }

   public boolean isAcceptRanges() {
      return this._acceptRanges;
   }

   public boolean isDirAllowed() {
      return this._dirAllowed;
   }

   public boolean isEtags() {
      return this._etags;
   }

   public List<CompressedContentFormat> getPrecompressedFormats() {
      return this._precompressedFormats;
   }

   public ResourceService.WelcomeMode getWelcomeMode() {
      return this._welcomeMode;
   }

   public ResourceService.WelcomeFactory getWelcomeFactory() {
      return this._welcomeFactory;
   }

   public void setAcceptRanges(boolean acceptRanges) {
      this._acceptRanges = acceptRanges;
   }

   public void setCacheControl(String cacheControl) {
      this._cacheControl = new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, cacheControl);
   }

   public void setDirAllowed(boolean dirAllowed) {
      this._dirAllowed = dirAllowed;
   }

   public void setEtags(boolean etags) {
      this._etags = etags;
   }

   public void setGzipEquivalentFileExtensions(List<String> gzipEquivalentFileExtensions) {
      this._gzipEquivalentFileExtensions = gzipEquivalentFileExtensions;
   }

   public void setPrecompressedFormats(List<CompressedContentFormat> precompressedFormats) {
      this._precompressedFormats.clear();
      this._precompressedFormats.addAll(precompressedFormats);
      this._preferredEncodingOrder.clear();
      this._preferredEncodingOrder.addAll(this._precompressedFormats.stream().map(CompressedContentFormat::getEncoding).toList());
   }

   public void setEncodingCacheSize(int encodingCacheSize) {
      this._encodingCacheSize = encodingCacheSize;
      if (encodingCacheSize > this._preferredEncodingOrderCache.size()) {
         this._preferredEncodingOrderCache.clear();
      }
   }

   public int getEncodingCacheSize() {
      return this._encodingCacheSize;
   }

   public void setWelcomeMode(ResourceService.WelcomeMode welcomeMode) {
      this._welcomeMode = Objects.requireNonNull(welcomeMode);
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x(contentFactory=%s, dirAllowed=%b, welcomeMode=%s)",
         this.getClass().getName(),
         this.hashCode(),
         this._contentFactory,
         this._dirAllowed,
         this._welcomeMode
      );
   }

   public void setWelcomeFactory(ResourceService.WelcomeFactory welcomeFactory) {
      this._welcomeFactory = welcomeFactory;
   }

   private static class ContentWriterIteratingCallback extends IteratingCallback {
      private final ReadableByteChannel source;
      private final Sink sink;
      private final Callback callback;
      private final RetainableByteBuffer buffer;

      public ContentWriterIteratingCallback(HttpContent content, Response target, Callback callback) throws IOException {
         this.source = content.getResource().newReadableByteChannel();
         this.sink = target;
         this.callback = callback;
         ByteBufferPool bufferPool = target.getRequest().getComponents().getByteBufferPool();
         int outputBufferSize = target.getRequest().getConnectionMetaData().getHttpConfiguration().getOutputBufferSize();
         boolean useOutputDirectByteBuffers = target.getRequest().getConnectionMetaData().getHttpConfiguration().isUseOutputDirectByteBuffers();
         this.buffer = bufferPool.acquire(outputBufferSize, useOutputDirectByteBuffers);
      }

      protected Action process() throws Throwable {
         if (!this.source.isOpen()) {
            return Action.SUCCEEDED;
         } else {
            ByteBuffer byteBuffer = this.buffer.getByteBuffer();
            BufferUtil.clearToFill(byteBuffer);
            int read = this.source.read(byteBuffer);
            if (read == -1) {
               IO.close(this.source);
               this.sink.write(true, BufferUtil.EMPTY_BUFFER, this);
               return Action.SCHEDULED;
            } else {
               BufferUtil.flipToFlush(byteBuffer, 0);
               this.sink.write(false, byteBuffer, this);
               return Action.SCHEDULED;
            }
         }
      }

      protected void onCompleteSuccess() {
         this.buffer.release();
         this.callback.succeeded();
      }

      protected void onCompleteFailure(Throwable x) {
         this.buffer.release();
         this.callback.failed(x);
      }
   }

   public static record WelcomeAction(String target, ResourceService.WelcomeMode mode) {
   }

   public interface WelcomeFactory {
      String getWelcomeTarget(HttpContent var1, Request var2) throws IOException;
   }

   public static enum WelcomeMode {
      REDIRECT,
      SERVE,
      REHANDLE;
   }
}
