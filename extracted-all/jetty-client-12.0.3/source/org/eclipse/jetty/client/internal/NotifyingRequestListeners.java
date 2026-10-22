package org.eclipse.jetty.client.internal;

import java.nio.ByteBuffer;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.RequestListeners;

public class NotifyingRequestListeners extends RequestListeners {
   public void notifyQueued(Request request) {
      notifyQueued(this.getQueuedListener(), request);
   }

   public void notifyBegin(Request request) {
      notifyBegin(this.getBeginListener(), request);
   }

   public void notifyHeaders(Request request) {
      notifyHeaders(this.getHeadersListener(), request);
   }

   public void notifyCommit(Request request) {
      notifyCommit(this.getCommitListener(), request);
   }

   public void notifyContent(Request request, ByteBuffer byteBuffer) {
      notifyContent(this.getContentListener(), request, byteBuffer);
   }

   public void notifySuccess(Request request) {
      notifySuccess(this.getSuccessListener(), request);
   }

   public void notifyFailure(Request request, Throwable failure) {
      notifyFailure(this.getFailureListener(), request, failure);
   }
}
