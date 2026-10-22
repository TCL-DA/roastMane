package org.eclipse.jetty.server.handler;

import java.nio.ByteBuffer;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Content.Sink;
import org.eclipse.jetty.server.ConnectionMetaData;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IncludeExclude;
import org.eclipse.jetty.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferedResponseHandler extends ConditionalHandler.Abstract {
   public static final String BUFFER_SIZE_ATTRIBUTE_NAME = BufferedResponseHandler.class.getName() + ".buffer-size";
   public static final String MAX_AGGREGATION_SIZE_ATTRIBUTE_NAME = BufferedResponseHandler.class.getName() + ".max-aggregation-size";
   private static final Logger LOG = LoggerFactory.getLogger(BufferedResponseHandler.class);
   private final IncludeExclude<String> _mimeTypes = new IncludeExclude();

   public BufferedResponseHandler() {
      this(null);
   }

   public BufferedResponseHandler(Handler handler) {
      super(handler);
      this.includeMethod(new String[]{HttpMethod.GET.asString()});

      for (String type : MimeTypes.DEFAULTS.getMimeMap().values()) {
         if (type.startsWith("image/") || type.startsWith("audio/") || type.startsWith("video/")) {
            this._mimeTypes.exclude(type);
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("{} mime types {}", this, this._mimeTypes);
      }
   }

   public void includeMimeType(String... mimeTypes) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._mimeTypes.include(mimeTypes);
      }
   }

   public void excludeMimeType(String... mimeTypes) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._mimeTypes.exclude(mimeTypes);
      }
   }

   protected boolean isMimeTypeBufferable(String mimetype) {
      return this._mimeTypes.test(mimetype);
   }

   protected boolean shouldBuffer(Response response, boolean last) {
      if (last) {
         return false;
      } else {
         int status = response.getStatus();
         if (!HttpStatus.hasNoBody(status) && !HttpStatus.isRedirection(status)) {
            String ct = response.getHeaders().get(HttpHeader.CONTENT_TYPE);
            if (ct == null) {
               return true;
            } else {
               ct = MimeTypes.getContentTypeWithoutCharset(ct);
               return this.isMimeTypeBufferable(StringUtil.asciiToLowerCase(ct));
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean onConditionsMet(Request request, Response response, Callback callback) throws Exception {
      Handler next = this.getHandler();
      if (next == null) {
         return false;
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} doHandle {} in {}", new Object[]{this, request, request.getContext()});
         }

         String mimeType = request.getContext().getMimeTypes().getMimeByExtension(request.getHttpURI().getCanonicalPath());
         if (mimeType != null) {
            mimeType = MimeTypes.getContentTypeWithoutCharset(mimeType);
            if (!this.isMimeTypeBufferable(mimeType)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("{} excluded by path suffix mime type {}", this, request);
               }

               return next.handle(request, response, callback);
            }
         }

         BufferedResponseHandler.BufferedResponse bufferedResponse = new BufferedResponseHandler.BufferedResponse(request, response, callback);
         return next.handle(request, bufferedResponse, bufferedResponse);
      }
   }

   @Override
   protected boolean onConditionsNotMet(Request request, Response response, Callback callback) throws Exception {
      return this.nextHandler(request, response, callback);
   }

   private class BufferedResponse extends Response.Wrapper implements Callback {
      private final Callback _callback;
      private Sink _bufferedContentSink;
      private boolean _firstWrite = true;
      private boolean _lastWritten;

      private BufferedResponse(Request request, Response response, Callback callback) {
         super(request, response);
         this._callback = callback;
      }

      @Override
      public void write(boolean last, ByteBuffer byteBuffer, Callback callback) {
         if (this._firstWrite) {
            this._firstWrite = false;
            if (BufferedResponseHandler.this.shouldBuffer(this, last)) {
               this._bufferedContentSink = this.createBufferedSink();
            }
         }

         this._lastWritten |= last;
         Sink destSink = (Sink)(this._bufferedContentSink != null ? this._bufferedContentSink : this.getWrapped());
         destSink.write(last, byteBuffer, callback);
      }

      private Sink createBufferedSink() {
         Request request = this.getRequest();
         ConnectionMetaData connectionMetaData = request.getConnectionMetaData();
         ByteBufferPool bufferPool = connectionMetaData.getConnector().getByteBufferPool();
         HttpConfiguration httpConfiguration = connectionMetaData.getHttpConfiguration();
         Object attribute = request.getAttribute(BufferedResponseHandler.BUFFER_SIZE_ATTRIBUTE_NAME);
         int bufferSize = attribute instanceof Integer ? (Integer)attribute : httpConfiguration.getOutputBufferSize();
         attribute = request.getAttribute(BufferedResponseHandler.MAX_AGGREGATION_SIZE_ATTRIBUTE_NAME);
         int maxAggregationSize = attribute instanceof Integer ? (Integer)attribute : httpConfiguration.getOutputAggregationSize();
         boolean direct = httpConfiguration.isUseOutputDirectByteBuffers();
         return Sink.asBuffered(this.getWrapped(), bufferPool, direct, maxAggregationSize, bufferSize);
      }

      public void succeeded() {
         if (this._bufferedContentSink != null && !this._lastWritten) {
            this._bufferedContentSink.write(true, null, this._callback);
         } else {
            this._callback.succeeded();
         }
      }

      public void failed(Throwable x) {
         if (this._bufferedContentSink != null && !this._lastWritten) {
            this._bufferedContentSink.write(true, null, Callback.NOOP);
         }

         this._callback.failed(x);
      }
   }
}
