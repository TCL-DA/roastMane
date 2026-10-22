package org.eclipse.jetty.client.transport;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpFields.Mutable;

public class HttpResponse implements Response {
   private final Mutable headers = HttpFields.build();
   private final Request request;
   private HttpVersion version;
   private int status;
   private String reason;
   private Mutable trailers;

   public HttpResponse(Request request) {
      this.request = request;
   }

   @Override
   public Request getRequest() {
      return this.request;
   }

   @Override
   public HttpVersion getVersion() {
      return this.version;
   }

   public HttpResponse version(HttpVersion version) {
      this.version = version;
      return this;
   }

   @Override
   public int getStatus() {
      return this.status;
   }

   public HttpResponse status(int status) {
      this.status = status;
      return this;
   }

   @Override
   public String getReason() {
      return this.reason;
   }

   public HttpResponse reason(String reason) {
      this.reason = reason;
      return this;
   }

   @Override
   public HttpFields getHeaders() {
      return this.headers.asImmutable();
   }

   public void clearHeaders() {
      this.headers.clear();
   }

   public HttpResponse addHeader(HttpField header) {
      this.headers.add(header);
      return this;
   }

   public HttpResponse headers(Consumer<Mutable> consumer) {
      consumer.accept(this.headers);
      return this;
   }

   @Override
   public HttpFields getTrailers() {
      return this.trailers == null ? null : this.trailers.asImmutable();
   }

   public HttpResponse trailer(HttpField trailer) {
      if (this.trailers == null) {
         this.trailers = HttpFields.build();
      }

      this.trailers.add(trailer);
      return this;
   }

   @Override
   public CompletableFuture<Boolean> abort(Throwable cause) {
      return this.request.abort(cause);
   }

   @Override
   public String toString() {
      return String.format("%s[%s %d %s]@%x", HttpResponse.class.getSimpleName(), this.getVersion(), this.getStatus(), this.getReason(), this.hashCode());
   }
}
