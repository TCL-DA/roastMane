package org.eclipse.jetty.server.handler;

import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.eclipse.jetty.server.Context;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextRequest extends Request.Wrapper implements Invocable {
   private static final Logger LOG = LoggerFactory.getLogger(ContextRequest.class);
   private final ContextHandler.ScopedContext _context;

   protected ContextRequest(ContextHandler.ScopedContext context, Request request) {
      super(request);
      this._context = context;
   }

   @Override
   public void demand(Runnable demandCallback) {
      super.demand(new ContextRequest.OnContextDemand(demandCallback));
   }

   @Override
   public void addIdleTimeoutListener(Predicate<TimeoutException> onIdleTimeout) {
      super.addIdleTimeoutListener(t -> this._context.test(onIdleTimeout, t, this));
   }

   @Override
   public void addFailureListener(Consumer<Throwable> onFailure) {
      super.addFailureListener(t -> this._context.accept(onFailure, t, this));
   }

   @Override
   public Context getContext() {
      return this._context;
   }

   private class OnContextDemand implements Runnable {
      private final Runnable _demandCallback;

      public OnContextDemand(Runnable demandCallback) {
         this._demandCallback = demandCallback;
      }

      @Override
      public void run() {
         ContextRequest.this._context.run(this._demandCallback, ContextRequest.this);
      }
   }
}
