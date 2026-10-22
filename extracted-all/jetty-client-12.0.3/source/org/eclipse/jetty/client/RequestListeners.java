package org.eclipse.jetty.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import org.eclipse.jetty.util.component.Dumpable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestListeners implements Dumpable {
   private static final Logger LOG = LoggerFactory.getLogger(RequestListeners.class);
   private Request.QueuedListener queuedListener;
   private Request.BeginListener beginListener;
   private Request.HeadersListener headersListener;
   private Request.CommitListener commitListener;
   private Request.ContentListener contentListener;
   private Request.SuccessListener successListener;
   private Request.FailureListener failureListener;

   public boolean addListener(Request.Listener listener) {
      return this.addQueuedListener(listener)
         | this.addBeginListener(listener)
         | this.addHeadersListener(listener)
         | this.addCommitListener(listener)
         | this.addContentListener(listener)
         | this.addSuccessListener(listener)
         | this.addFailureListener(listener);
   }

   public boolean removeListener(Request.Listener listener) {
      return this.removeQueuedListener(listener)
         | this.removeBeginListener(listener)
         | this.removeHeadersListener(listener)
         | this.removeCommitListener(listener)
         | this.removeContentListener(listener)
         | this.removeSuccessListener(listener)
         | this.removeFailureListener(listener);
   }

   public boolean addQueuedListener(Request.QueuedListener listener) {
      if (listener == null) {
         return false;
      } else {
         Request.QueuedListener existing = this.queuedListener;
         this.queuedListener = (Request.QueuedListener)(existing == null ? listener : new RequestListeners.QueuedListenerLink(existing, listener));
         return true;
      }
   }

   public boolean removeQueuedListener(Request.QueuedListener listener) {
      if (listener == null) {
         return false;
      } else if (this.queuedListener == listener) {
         this.queuedListener = null;
         return true;
      } else {
         if (this.queuedListener instanceof RequestListeners.QueuedListenerLink link) {
            Request.QueuedListener remaining = link.remove(listener);
            if (remaining != null) {
               this.queuedListener = remaining;
               return true;
            }
         }

         return false;
      }
   }

   protected static void notifyQueued(Request.QueuedListener listener, Request request) {
      try {
         if (listener != null) {
            listener.onQueued(request);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addBeginListener(Request.BeginListener listener) {
      if (listener == null) {
         return false;
      } else {
         Request.BeginListener existing = this.beginListener;
         this.beginListener = (Request.BeginListener)(existing == null ? listener : new RequestListeners.BeginListenerLink(existing, listener));
         return true;
      }
   }

   public boolean removeBeginListener(Request.BeginListener listener) {
      if (listener == null) {
         return false;
      } else if (this.beginListener == listener) {
         this.beginListener = null;
         return true;
      } else {
         if (this.beginListener instanceof RequestListeners.BeginListenerLink link) {
            Request.BeginListener remaining = link.remove(listener);
            if (remaining != null) {
               this.beginListener = remaining;
               return true;
            }
         }

         return false;
      }
   }

   protected static void notifyBegin(Request.BeginListener listener, Request request) {
      try {
         if (listener != null) {
            listener.onBegin(request);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addHeadersListener(Request.HeadersListener listener) {
      if (listener == null) {
         return false;
      } else {
         Request.HeadersListener existing = this.headersListener;
         this.headersListener = (Request.HeadersListener)(existing == null ? listener : new RequestListeners.HeadersListenerLink(existing, listener));
         return true;
      }
   }

   public boolean removeHeadersListener(Request.HeadersListener listener) {
      if (listener == null) {
         return false;
      } else if (this.headersListener == listener) {
         this.headersListener = null;
         return true;
      } else {
         if (this.headersListener instanceof RequestListeners.HeadersListenerLink link) {
            Request.HeadersListener remaining = link.remove(listener);
            if (remaining != null) {
               this.headersListener = remaining;
               return true;
            }
         }

         return false;
      }
   }

   protected static void notifyHeaders(Request.HeadersListener listener, Request request) {
      try {
         if (listener != null) {
            listener.onHeaders(request);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addCommitListener(Request.CommitListener listener) {
      if (listener == null) {
         return false;
      } else {
         Request.CommitListener existing = this.commitListener;
         this.commitListener = (Request.CommitListener)(existing == null ? listener : new RequestListeners.CommitListenerLink(existing, listener));
         return true;
      }
   }

   public boolean removeCommitListener(Request.CommitListener listener) {
      if (listener == null) {
         return false;
      } else if (this.commitListener == listener) {
         this.commitListener = null;
         return true;
      } else {
         if (this.commitListener instanceof RequestListeners.CommitListenerLink link) {
            Request.CommitListener remaining = link.remove(listener);
            if (remaining != null) {
               this.commitListener = remaining;
               return true;
            }
         }

         return false;
      }
   }

   protected static void notifyCommit(Request.CommitListener listener, Request request) {
      try {
         if (listener != null) {
            listener.onCommit(request);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addContentListener(Request.ContentListener listener) {
      if (listener == null) {
         return false;
      } else {
         Request.ContentListener existing = this.contentListener;
         this.contentListener = (Request.ContentListener)(existing == null ? listener : new RequestListeners.ContentListenerLink(existing, listener));
         return true;
      }
   }

   public boolean removeContentListener(Request.ContentListener listener) {
      if (listener == null) {
         return false;
      } else if (this.contentListener == listener) {
         this.contentListener = null;
         return true;
      } else {
         if (this.contentListener instanceof RequestListeners.ContentListenerLink link) {
            Request.ContentListener remaining = link.remove(listener);
            if (remaining != null) {
               this.contentListener = remaining;
               return true;
            }
         }

         return false;
      }
   }

   protected static void notifyContent(Request.ContentListener listener, Request request, ByteBuffer byteBuffer) {
      try {
         if (listener != null) {
            byteBuffer.clear();
            listener.onContent(request, byteBuffer);
         }
      } catch (Throwable var4) {
         LOG.info("Exception while notifying listener {}", listener, var4);
      }
   }

   public boolean addSuccessListener(Request.SuccessListener listener) {
      if (listener == null) {
         return false;
      } else {
         Request.SuccessListener existing = this.successListener;
         this.successListener = (Request.SuccessListener)(existing == null ? listener : new RequestListeners.SuccessListenerLink(existing, listener));
         return true;
      }
   }

   public boolean removeSuccessListener(Request.SuccessListener listener) {
      if (listener == null) {
         return false;
      } else if (this.successListener == listener) {
         this.successListener = null;
         return true;
      } else {
         if (this.successListener instanceof RequestListeners.SuccessListenerLink link) {
            Request.SuccessListener remaining = link.remove(listener);
            if (remaining != null) {
               this.successListener = remaining;
               return true;
            }
         }

         return false;
      }
   }

   protected static void notifySuccess(Request.SuccessListener listener, Request request) {
      try {
         if (listener != null) {
            listener.onSuccess(request);
         }
      } catch (Throwable var3) {
         LOG.info("Exception while notifying listener {}", listener, var3);
      }
   }

   public boolean addFailureListener(Request.FailureListener listener) {
      if (listener == null) {
         return false;
      } else {
         Request.FailureListener existing = this.failureListener;
         this.failureListener = (Request.FailureListener)(existing == null ? listener : new RequestListeners.FailureListenerLink(existing, listener));
         return true;
      }
   }

   public boolean removeFailureListener(Request.FailureListener listener) {
      if (listener == null) {
         return false;
      } else if (this.failureListener == listener) {
         this.failureListener = null;
         return true;
      } else {
         if (this.failureListener instanceof RequestListeners.FailureListenerLink link) {
            Request.FailureListener remaining = link.remove(listener);
            if (remaining != null) {
               this.failureListener = remaining;
               return true;
            }
         }

         return false;
      }
   }

   protected static void notifyFailure(Request.FailureListener listener, Request request, Throwable failure) {
      try {
         if (listener != null) {
            listener.onFailure(request, failure);
         }
      } catch (Throwable var4) {
         LOG.info("Exception while notifying listener {}", listener, var4);
      }
   }

   public void clear() {
      this.queuedListener = null;
      this.beginListener = null;
      this.headersListener = null;
      this.commitListener = null;
      this.contentListener = null;
      this.successListener = null;
      this.failureListener = null;
   }

   protected Request.QueuedListener getQueuedListener() {
      return this.queuedListener;
   }

   protected Request.BeginListener getBeginListener() {
      return this.beginListener;
   }

   protected Request.HeadersListener getHeadersListener() {
      return this.headersListener;
   }

   protected Request.CommitListener getCommitListener() {
      return this.commitListener;
   }

   protected Request.ContentListener getContentListener() {
      return this.contentListener;
   }

   protected Request.SuccessListener getSuccessListener() {
      return this.successListener;
   }

   protected Request.FailureListener getFailureListener() {
      return this.failureListener;
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(
         out,
         indent,
         this,
         new Object[]{
            new RequestListeners.ListenerDump("queued", this.getQueuedListener()),
            new RequestListeners.ListenerDump("begin", this.getBeginListener()),
            new RequestListeners.ListenerDump("headers", this.getHeadersListener()),
            new RequestListeners.ListenerDump("commit", this.getCommitListener()),
            new RequestListeners.ListenerDump("content", this.getContentListener()),
            new RequestListeners.ListenerDump("success", this.getSuccessListener()),
            new RequestListeners.ListenerDump("failure", this.getFailureListener())
         }
      );
   }

   private static class BeginListenerLink
      extends RequestListeners.Link<Request.BeginListener, RequestListeners.BeginListenerLink>
      implements Request.BeginListener {
      private BeginListenerLink(Request.BeginListener prev, Request.BeginListener next) {
         super(RequestListeners.BeginListenerLink.class, RequestListeners.BeginListenerLink::new, prev, next);
      }

      @Override
      public void onBegin(Request request) {
         RequestListeners.notifyBegin(this.prev, request);
         RequestListeners.notifyBegin(this.next, request);
      }
   }

   private static class CommitListenerLink
      extends RequestListeners.Link<Request.CommitListener, RequestListeners.CommitListenerLink>
      implements Request.CommitListener {
      private CommitListenerLink(Request.CommitListener prev, Request.CommitListener next) {
         super(RequestListeners.CommitListenerLink.class, RequestListeners.CommitListenerLink::new, prev, next);
      }

      @Override
      public void onCommit(Request request) {
         RequestListeners.notifyCommit(this.prev, request);
         RequestListeners.notifyCommit(this.next, request);
      }
   }

   private static class ContentListenerLink
      extends RequestListeners.Link<Request.ContentListener, RequestListeners.ContentListenerLink>
      implements Request.ContentListener {
      private ContentListenerLink(Request.ContentListener prev, Request.ContentListener next) {
         super(RequestListeners.ContentListenerLink.class, RequestListeners.ContentListenerLink::new, prev, next);
      }

      @Override
      public void onContent(Request request, ByteBuffer content) {
         RequestListeners.notifyContent(this.prev, request, content);
         RequestListeners.notifyContent(this.next, request, content);
      }
   }

   private static class FailureListenerLink
      extends RequestListeners.Link<Request.FailureListener, RequestListeners.FailureListenerLink>
      implements Request.FailureListener {
      private FailureListenerLink(Request.FailureListener prev, Request.FailureListener next) {
         super(RequestListeners.FailureListenerLink.class, RequestListeners.FailureListenerLink::new, prev, next);
      }

      @Override
      public void onFailure(Request request, Throwable failure) {
         RequestListeners.notifyFailure(this.prev, request, failure);
         RequestListeners.notifyFailure(this.next, request, failure);
      }
   }

   private static class HeadersListenerLink
      extends RequestListeners.Link<Request.HeadersListener, RequestListeners.HeadersListenerLink>
      implements Request.HeadersListener {
      private HeadersListenerLink(Request.HeadersListener prev, Request.HeadersListener next) {
         super(RequestListeners.HeadersListenerLink.class, RequestListeners.HeadersListenerLink::new, prev, next);
      }

      @Override
      public void onHeaders(Request request) {
         RequestListeners.notifyHeaders(this.prev, request);
         RequestListeners.notifyHeaders(this.next, request);
      }
   }

   private static class Link<T, L extends RequestListeners.Link<T, L>> {
      private final Class<L> type;
      private final BiFunction<T, T, L> ctor;
      protected final T prev;
      protected final T next;

      protected Link(Class<L> type, BiFunction<T, T, L> ctor, T prev, T next) {
         this.type = type;
         this.ctor = ctor;
         this.prev = prev;
         this.next = next;
      }

      protected T remove(T listener) {
         if (this.next == listener) {
            return this.prev;
         } else if (this.type.isInstance(this.prev)) {
            T remaining = this.type.cast(this.prev).remove(listener);
            return (T)(remaining != null ? this.ctor.apply(remaining, this.next) : null);
         } else {
            return this.prev == listener ? this.next : null;
         }
      }

      @Override
      public String toString() {
         return "%s@%x(%s,%s)".formatted(this.getClass().getSimpleName(), this.hashCode(), this.prev, this.next);
      }
   }

   private static record ListenerDump(String name, Object listener) {
      @Override
      public String toString() {
         return this.name + " = " + this.listener;
      }
   }

   private static class QueuedListenerLink
      extends RequestListeners.Link<Request.QueuedListener, RequestListeners.QueuedListenerLink>
      implements Request.QueuedListener {
      private QueuedListenerLink(Request.QueuedListener prev, Request.QueuedListener next) {
         super(RequestListeners.QueuedListenerLink.class, RequestListeners.QueuedListenerLink::new, prev, next);
      }

      @Override
      public void onQueued(Request request) {
         RequestListeners.notifyQueued(this.prev, request);
         RequestListeners.notifyQueued(this.next, request);
      }
   }

   private static class SuccessListenerLink
      extends RequestListeners.Link<Request.SuccessListener, RequestListeners.SuccessListenerLink>
      implements Request.SuccessListener {
      private SuccessListenerLink(Request.SuccessListener prev, Request.SuccessListener next) {
         super(RequestListeners.SuccessListenerLink.class, RequestListeners.SuccessListenerLink::new, prev, next);
      }

      @Override
      public void onSuccess(Request request) {
         RequestListeners.notifySuccess(this.prev, request);
         RequestListeners.notifySuccess(this.next, request);
      }
   }
}
