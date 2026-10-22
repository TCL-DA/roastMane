package org.eclipse.jetty.server.handler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.MimeTypes.Type;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.server.FormFields;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.StringUtil;

public class DelayedHandler extends Handler.Wrapper {
   public DelayedHandler() {
      this(null);
   }

   public DelayedHandler(Handler handler) {
      super(handler);
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Handler next = this.getHandler();
      if (next == null) {
         return false;
      } else {
         boolean contentExpected = false;
         String contentType = null;

         label38:
         for (HttpField field : request.getHeaders()) {
            HttpHeader header = field.getHeader();
            if (header != null) {
               switch (header) {
                  case CONTENT_TYPE:
                     contentType = field.getValue();
                     break;
                  case CONTENT_LENGTH:
                     contentExpected = field.getLongValue() > 0L;
                     break;
                  case TRANSFER_ENCODING:
                     contentExpected = field.contains(HttpHeaderValue.CHUNKED.asString());
                     break;
                  case EXPECT:
                     if (field.contains(HttpHeaderValue.CONTINUE.asString())) {
                        contentExpected = false;
                        break label38;
                     }
               }
            }
         }

         Type mimeType = MimeTypes.getBaseType(contentType);
         DelayedHandler.DelayedProcess delayed = this.newDelayedProcess(contentExpected, contentType, mimeType, next, request, response, callback);
         if (delayed == null) {
            return next.handle(request, response, callback);
         } else {
            delayed.delay();
            return true;
         }
      }
   }

   protected DelayedHandler.DelayedProcess newDelayedProcess(
      boolean contentExpected, String contentType, Type mimeType, Handler handler, Request request, Response response, Callback callback
   ) {
      if (!contentExpected) {
         return null;
      } else if (!request.getConnectionMetaData().getHttpConfiguration().isDelayDispatchUntilContent()) {
         return null;
      } else if (mimeType == null) {
         return new DelayedHandler.UntilContentDelayedProcess(handler, request, response, callback);
      } else {
         return (DelayedHandler.DelayedProcess)(switch (mimeType) {
            case FORM_ENCODED -> new DelayedHandler.UntilFormDelayedProcess(handler, request, response, callback, contentType);
            default -> new DelayedHandler.UntilContentDelayedProcess(handler, request, response, callback);
         });
      }
   }

   protected abstract static class DelayedProcess {
      private final Handler _handler;
      private final Request _request;
      private final Response _response;
      private final Callback _callback;

      protected DelayedProcess(Handler handler, Request request, Response response, Callback callback) {
         this._handler = Objects.requireNonNull(handler);
         this._request = Objects.requireNonNull(request);
         this._response = Objects.requireNonNull(response);
         this._callback = Objects.requireNonNull(callback);
      }

      protected Handler getHandler() {
         return this._handler;
      }

      protected Request getRequest() {
         return this._request;
      }

      protected Response getResponse() {
         return this._response;
      }

      protected Callback getCallback() {
         return this._callback;
      }

      protected void process() {
         try {
            if (!this.getHandler().handle(this.getRequest(), this.getResponse(), this.getCallback())) {
               Response.writeError(this.getRequest(), this.getResponse(), this.getCallback(), 404);
            }
         } catch (Throwable var2) {
            Response.writeError(this.getRequest(), this.getResponse(), this.getCallback(), var2);
         }
      }

      protected abstract void delay() throws Exception;
   }

   protected static class UntilContentDelayedProcess extends DelayedHandler.DelayedProcess {
      public UntilContentDelayedProcess(Handler handler, Request request, Response response, Callback callback) {
         super(handler, request, response, callback);
      }

      @Override
      protected void delay() {
         Chunk chunk = super.getRequest().read();
         if (chunk == null) {
            this.getRequest().demand(this::onContent);
         } else {
            DelayedHandler.UntilContentDelayedProcess.RewindChunkRequest request = new DelayedHandler.UntilContentDelayedProcess.RewindChunkRequest(
               this.getRequest(), chunk
            );

            try {
               this.getHandler().handle(request, this.getResponse(), this.getCallback());
            } catch (Throwable var4) {
               Response.writeError(request, this.getResponse(), this.getCallback(), var4);
            }
         }
      }

      public void onContent() {
         this.getRequest().getContext().execute(this::process);
      }

      private static class RewindChunkRequest extends Request.Wrapper {
         private final AtomicReference<Chunk> _chunk;

         public RewindChunkRequest(Request wrapped, Chunk chunk) {
            super(wrapped);
            this._chunk = new AtomicReference<>(chunk);
         }

         @Override
         public Chunk read() {
            Chunk chunk = this._chunk.getAndSet(null);
            return chunk != null ? chunk : super.read();
         }
      }
   }

   protected static class UntilFormDelayedProcess extends DelayedHandler.DelayedProcess {
      private final Charset _charset;

      public UntilFormDelayedProcess(Handler handler, Request wrapped, Response response, Callback callback, String contentType) {
         super(handler, wrapped, response, callback);
         String cs = MimeTypes.getCharsetFromContentType(contentType);
         this._charset = StringUtil.isEmpty(cs) ? StandardCharsets.UTF_8 : Charset.forName(cs);
      }

      @Override
      protected void delay() {
         CompletableFuture<Fields> futureFormFields = FormFields.from(this.getRequest(), this._charset);
         futureFormFields.whenComplete(futureFormFields.isDone() ? this::process : this::executeProcess);
      }

      private void process(Fields fields, Throwable x) {
         if (x == null) {
            super.process();
         } else {
            Response.writeError(this.getRequest(), this.getResponse(), this.getCallback(), x);
         }
      }

      private void executeProcess(Fields fields, Throwable x) {
         if (x == null) {
            this.getRequest().getContext().execute(() -> super.process());
         } else {
            Response.writeError(this.getRequest(), this.getResponse(), this.getCallback(), x);
         }
      }
   }
}
