package org.eclipse.jetty.server.handler.gzip;

import java.util.ListIterator;
import java.util.Set;
import java.util.zip.Deflater;
import org.eclipse.jetty.http.EtagUtils;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.pathmap.PathSpecSet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.AsciiLowerCaseSet;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IncludeExclude;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.compression.CompressionPool;
import org.eclipse.jetty.util.compression.DeflaterPool;
import org.eclipse.jetty.util.compression.InflaterPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GzipHandler extends Handler.Wrapper implements GzipFactory {
   public static final String GZIP_HANDLER_ETAGS = "o.e.j.s.h.gzip.GzipHandler.etag";
   public static final String GZIP = "gzip";
   public static final String DEFLATE = "deflate";
   public static final int DEFAULT_MIN_GZIP_SIZE = 32;
   public static final int BREAK_EVEN_GZIP_SIZE = 23;
   private static final Logger LOG = LoggerFactory.getLogger(GzipHandler.class);
   private InflaterPool _inflaterPool;
   private DeflaterPool _deflaterPool;
   private int _minGzipSize = 32;
   private boolean _syncFlush = false;
   private int _inflateBufferSize = -1;
   private final IncludeExclude<String> _methods = new IncludeExclude();
   private final IncludeExclude<String> _inflatePaths = new IncludeExclude(PathSpecSet.class);
   private final IncludeExclude<String> _paths = new IncludeExclude(PathSpecSet.class);
   private final IncludeExclude<String> _mimeTypes = new IncludeExclude(AsciiLowerCaseSet.class);
   private HttpField _vary = GzipResponseAndCallback.VARY_ACCEPT_ENCODING;

   public GzipHandler() {
      this(null);
   }

   public GzipHandler(Handler handler) {
      super(handler);
      this._methods.include(HttpMethod.GET.asString());
      this._methods.include(HttpMethod.POST.asString());

      for (String type : MimeTypes.DEFAULTS.getMimeMap().values()) {
         if ("image/svg+xml".equals(type)) {
            this._paths.exclude("*.svgz");
         } else if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
            this._mimeTypes.exclude(type);
         }
      }

      this._mimeTypes.exclude("application/compress");
      this._mimeTypes.exclude("application/zip");
      this._mimeTypes.exclude("application/gzip");
      this._mimeTypes.exclude("application/bzip2");
      this._mimeTypes.exclude("application/brotli");
      this._mimeTypes.exclude("application/x-xz");
      this._mimeTypes.exclude("application/x-rar-compressed");
      this._mimeTypes.exclude("text/event-stream");
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} mime types {}", this, this._mimeTypes);
      }
   }

   @Override
   protected void doStart() throws Exception {
      Server server = this.getServer();
      if (this._inflaterPool == null) {
         this._inflaterPool = InflaterPool.ensurePool(server);
         this.addBean(this._inflaterPool);
      }

      if (this._deflaterPool == null) {
         this._deflaterPool = DeflaterPool.ensurePool(server);
         this.addBean(this._deflaterPool);
      }

      super.doStart();
   }

   @Override
   protected void doStop() throws Exception {
      super.doStop();
      this.removeBean(this._inflaterPool);
      this._inflaterPool = null;
      this.removeBean(this._deflaterPool);
      this._deflaterPool = null;
   }

   public HttpField getVary() {
      return this._vary;
   }

   public void setVary(HttpField vary) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         if (vary != null && !(vary instanceof PreEncodedHttpField)) {
            this._vary = new PreEncodedHttpField(vary.getHeader(), vary.getName(), vary.getValue());
         } else {
            this._vary = vary;
         }
      }
   }

   public void addExcludedMethods(String... methods) {
      for (String m : methods) {
         this._methods.exclude(m);
      }
   }

   public void addExcludedMimeTypes(String... types) {
      for (String t : types) {
         this._mimeTypes.exclude(StringUtil.csvSplit(t));
      }
   }

   public void addExcludedPaths(String... pathspecs) {
      for (String p : pathspecs) {
         this._paths.exclude(StringUtil.csvSplit(p));
      }
   }

   public void addExcludedInflationPaths(String... pathspecs) {
      for (String p : pathspecs) {
         this._inflatePaths.exclude(StringUtil.csvSplit(p));
      }
   }

   public void addIncludedMethods(String... methods) {
      for (String m : methods) {
         this._methods.include(m);
      }
   }

   public boolean isSyncFlush() {
      return this._syncFlush;
   }

   public void setSyncFlush(boolean syncFlush) {
      this._syncFlush = syncFlush;
   }

   public void addIncludedMimeTypes(String... types) {
      for (String t : types) {
         this._mimeTypes.include(StringUtil.csvSplit(t));
      }
   }

   public void addIncludedPaths(String... pathspecs) {
      for (String p : pathspecs) {
         this._paths.include(StringUtil.csvSplit(p));
      }
   }

   public void addIncludedInflationPaths(String... pathspecs) {
      for (String p : pathspecs) {
         this._inflatePaths.include(StringUtil.csvSplit(p));
      }
   }

   @Override
   public CompressionPool<Deflater>.Entry getDeflaterEntry(Request request, long contentLength) {
      if (contentLength >= 0L && contentLength < (long)this._minGzipSize) {
         LOG.debug("{} excluded minGzipSize {}", this, request);
         return null;
      } else if (!request.getHeaders().contains(HttpHeader.ACCEPT_ENCODING, "gzip")) {
         LOG.debug("{} excluded not gzip accept {}", this, request);
         return null;
      } else {
         return this._deflaterPool.acquire();
      }
   }

   public String[] getExcludedMethods() {
      Set<String> excluded = this._methods.getExcluded();
      return excluded.toArray(new String[0]);
   }

   public String[] getExcludedMimeTypes() {
      Set<String> excluded = this._mimeTypes.getExcluded();
      return excluded.toArray(new String[0]);
   }

   public String[] getExcludedPaths() {
      Set<String> excluded = this._paths.getExcluded();
      return excluded.toArray(new String[0]);
   }

   public String[] getExcludedInflationPaths() {
      Set<String> excluded = this._inflatePaths.getExcluded();
      return excluded.toArray(new String[0]);
   }

   public String[] getIncludedMethods() {
      Set<String> includes = this._methods.getIncluded();
      return includes.toArray(new String[0]);
   }

   public String[] getIncludedMimeTypes() {
      Set<String> includes = this._mimeTypes.getIncluded();
      return includes.toArray(new String[0]);
   }

   public String[] getIncludedPaths() {
      Set<String> includes = this._paths.getIncluded();
      return includes.toArray(new String[0]);
   }

   public String[] getIncludedInflationPaths() {
      Set<String> includes = this._inflatePaths.getIncluded();
      return includes.toArray(new String[0]);
   }

   public int getMinGzipSize() {
      return this._minGzipSize;
   }

   public int getInflateBufferSize() {
      return this._inflateBufferSize;
   }

   public void setInflateBufferSize(int size) {
      this._inflateBufferSize = size;
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} handle {}", this, request);
      }

      Handler next = this.getHandler();
      if (next == null) {
         return false;
      } else if (Request.as(request, GzipRequest.class) != null) {
         return next.handle(request, response, callback);
      } else {
         String pathInContext = Request.getPathInContext(request);
         boolean tryInflate = this.getInflateBufferSize() >= 0 && this.isPathInflatable(pathInContext);
         boolean tryDeflate = this._methods.test(request.getMethod())
            && this.isPathDeflatable(pathInContext)
            && this.isMimeTypeDeflatable(request.getContext().getMimeTypes(), pathInContext);
         if (!tryInflate && !tryDeflate) {
            return next.handle(request, response, callback);
         } else {
            HttpFields fields = request.getHeaders();
            boolean inflatable = false;
            boolean deflatable = false;
            boolean etagMatches = false;
            boolean seenContentEncoding = false;
            ListIterator<HttpField> i = fields.listIterator(fields.size());

            while (i.hasPrevious()) {
               HttpField field = i.previous();
               HttpHeader header = field.getHeader();
               if (header != null) {
                  switch (header) {
                     case CONTENT_ENCODING:
                        inflatable |= !seenContentEncoding && field.containsLast("gzip");
                        seenContentEncoding = true;
                        break;
                     case ACCEPT_ENCODING:
                        deflatable = field.contains("gzip");
                        break;
                     case IF_MATCH:
                     case IF_NONE_MATCH:
                        etagMatches |= field.getValue().contains(EtagUtils.ETAG_SEPARATOR);
                  }
               }
            }

            if (inflatable && tryInflate || etagMatches) {
               request = new GzipRequest(request, inflatable && tryInflate ? this.getInflateBufferSize() : -1);
            }

            if (deflatable && tryDeflate) {
               GzipResponseAndCallback gzipResponseAndCallback = new GzipResponseAndCallback(this, request, response, callback);
               response = gzipResponseAndCallback;
               callback = gzipResponseAndCallback;
            } else if (tryDeflate && this._vary != null) {
               response.getHeaders().ensureField(this._vary);
            }

            if (next.handle(request, response, callback)) {
               return true;
            } else {
               if (request instanceof GzipRequest gzipRequest) {
                  gzipRequest.destroy();
               }

               return false;
            }
         }
      }
   }

   protected boolean isMimeTypeDeflatable(MimeTypes mimeTypes, String requestURI) {
      String mimeType = mimeTypes.getMimeByExtension(requestURI);
      if (mimeType != null) {
         mimeType = HttpField.getValueParameters(mimeType, null);
         return this.isMimeTypeDeflatable(mimeType);
      } else {
         return true;
      }
   }

   @Override
   public boolean isMimeTypeDeflatable(String mimetype) {
      return this._mimeTypes.test(mimetype);
   }

   protected boolean isPathDeflatable(String requestURI) {
      return requestURI == null ? true : this._paths.test(requestURI);
   }

   protected boolean isPathInflatable(String pathInContext) {
      return pathInContext == null ? true : this._inflatePaths.test(pathInContext);
   }

   public void setExcludedMethods(String... methods) {
      this._methods.getExcluded().clear();
      this._methods.exclude(methods);
   }

   public void setExcludedMimeTypes(String... types) {
      this._mimeTypes.getExcluded().clear();
      this._mimeTypes.exclude(types);
   }

   public void setExcludedMimeTypesList(String csvTypes) {
      this.setExcludedMimeTypes(StringUtil.csvSplit(csvTypes));
   }

   public void setExcludedPaths(String... pathspecs) {
      this._paths.getExcluded().clear();
      this._paths.exclude(pathspecs);
   }

   public void setExcludedInflatePaths(String... pathspecs) {
      this._inflatePaths.getExcluded().clear();
      this._inflatePaths.exclude(pathspecs);
   }

   public void setIncludedMethods(String... methods) {
      this._methods.getIncluded().clear();
      this._methods.include(methods);
   }

   public void setIncludedMimeTypes(String... types) {
      this._mimeTypes.getIncluded().clear();
      this._mimeTypes.include(types);
   }

   public void setIncludedMimeTypesList(String csvTypes) {
      this.setIncludedMimeTypes(StringUtil.csvSplit(csvTypes));
   }

   public void setIncludedPaths(String... pathspecs) {
      this._paths.getIncluded().clear();
      this._paths.include(pathspecs);
   }

   public void setIncludedInflatePaths(String... pathspecs) {
      this._inflatePaths.getIncluded().clear();
      this._inflatePaths.include(pathspecs);
   }

   public void setMinGzipSize(int minGzipSize) {
      if (minGzipSize < 23) {
         LOG.warn("minGzipSize of {} is inefficient for short content, break even is size {}", minGzipSize, 23);
      }

      this._minGzipSize = Math.max(0, minGzipSize);
   }

   public void setIncludedMethodList(String csvMethods) {
      this.setIncludedMethods(StringUtil.csvSplit(csvMethods));
   }

   public String getIncludedMethodList() {
      return String.join(",", this.getIncludedMethods());
   }

   public void setExcludedMethodList(String csvMethods) {
      this.setExcludedMethods(StringUtil.csvSplit(csvMethods));
   }

   public String getExcludedMethodList() {
      return String.join(",", this.getExcludedMethods());
   }

   public DeflaterPool getDeflaterPool() {
      return this._deflaterPool;
   }

   public InflaterPool getInflaterPool() {
      return this._inflaterPool;
   }

   public void setDeflaterPool(DeflaterPool deflaterPool) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this.updateBean(this._deflaterPool, deflaterPool);
         this._deflaterPool = deflaterPool;
      }
   }

   public void setInflaterPool(InflaterPool inflaterPool) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this.updateBean(this._inflaterPool, inflaterPool);
         this._inflaterPool = inflaterPool;
      }
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x{%s,min=%s,inflate=%s}", this.getClass().getSimpleName(), this.hashCode(), this.getState(), this._minGzipSize, this._inflateBufferSize
      );
   }
}
