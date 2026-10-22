package org.eclipse.jetty.client;

import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.client.internal.HttpContentResponse;

public class CompletableResponseListener extends BufferingResponseListener {
   private final CompletableFuture<ContentResponse> completable = new CompletableFuture<>();
   private final Request request;

   public CompletableResponseListener(Request request) {
      this(request, 2097152);
   }

   public CompletableResponseListener(Request request, int maxLength) {
      super(maxLength);
      this.request = request;
      this.completable.whenComplete(this::handleExternalFailure);
   }

   public Request getRequest() {
      return this.request;
   }

   private void handleExternalFailure(ContentResponse response, Throwable failure) {
      if (failure != null) {
         this.request.abort(failure);
      }
   }

   public CompletableFuture<ContentResponse> send() {
      this.request.send(this);
      return this.completable;
   }

   public CompletableFuture<ContentResponse> send(Destination destination) {
      destination.send(this.request, this);
      return this.completable;
   }

   public CompletableFuture<ContentResponse> send(Connection connection) {
      connection.send(this.request, this);
      return this.completable;
   }

   @Override
   public void onComplete(Result result) {
      if (result.isFailed()) {
         this.completable.completeExceptionally(result.getFailure());
      } else {
         this.completable.complete(new HttpContentResponse(result.getResponse(), this.getContent(), this.getMediaType(), this.getEncoding()));
      }
   }
}
