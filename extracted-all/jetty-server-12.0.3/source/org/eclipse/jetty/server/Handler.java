package org.eclipse.jetty.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Destroyable;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public interface Handler extends LifeCycle, Destroyable, Request.Handler {
   @ManagedAttribute(
      value = "The Server instance associated to this Handler",
      readonly = true
   )
   Server getServer();

   void setServer(Server var1);

   @ManagedObject
   public abstract static class Abstract extends ContainerLifeCycle implements Handler {
      private static final Logger LOG = LoggerFactory.getLogger(Handler.Abstract.class);
      private final InvocationType _invocationType;
      private Server _server;

      public Abstract() {
         this(InvocationType.BLOCKING);
      }

      public Abstract(InvocationType type) {
         this._invocationType = type;
      }

      @ManagedAttribute(
         value = "The Server associated with this Handler",
         readonly = true
      )
      @Override
      public Server getServer() {
         return this._server;
      }

      @Override
      public void setServer(Server server) {
         if (this._server != server) {
            if (this.isStarted()) {
               throw new IllegalStateException(this.getState());
            } else {
               this._server = server;
            }
         }
      }

      @Override
      public InvocationType getInvocationType() {
         return this._invocationType;
      }

      protected void doStart() throws Exception {
         if (LOG.isDebugEnabled()) {
            LOG.debug("starting {}", this);
         }

         if (this._server == null) {
            LOG.warn("No Server set for {}", this);
         }

         super.doStart();
      }

      protected void doStop() throws Exception {
         if (LOG.isDebugEnabled()) {
            LOG.debug("stopping {}", this);
         }

         super.doStop();
      }

      public void destroy() {
         if (this.isRunning()) {
            throw new IllegalStateException(this.getState());
         } else {
            super.destroy();
         }
      }

      public abstract static class NonBlocking extends Handler.Abstract {
         public NonBlocking() {
            super(InvocationType.NON_BLOCKING);
         }
      }
   }

   @ManagedObject
   public abstract static class AbstractContainer extends Handler.Abstract implements Handler.Container {
      private boolean _dynamic;

      protected AbstractContainer() {
         this(true);
      }

      protected AbstractContainer(boolean dynamic) {
         this._dynamic = dynamic;
      }

      @ManagedAttribute("Whether this Handler container is dynamic")
      public boolean isDynamic() {
         return this._dynamic;
      }

      public void setDynamic(boolean dynamic) {
         if (this.isStarted()) {
            throw new IllegalStateException(this.getState());
         } else {
            this._dynamic = dynamic;
         }
      }

      @Override
      public <T extends Handler> List<T> getDescendants(Class<T> type) {
         List<T> list = new ArrayList<>();
         this.expandHandler(this, list, type);
         return list;
      }

      private <H extends Handler> void expandHandler(Handler handler, List<H> list, Class<H> type) {
         if (handler instanceof Handler.Container container) {
            for (Handler child : container.getHandlers()) {
               if (type == null || type.isInstance(child)) {
                  list.add((H)child);
               }

               this.expandHandler(child, list, type);
            }
         }
      }

      @Override
      public <T extends Handler> T getDescendant(Class<T> type) {
         return this.findHandler(this, type);
      }

      private <H extends Handler> H findHandler(Handler handler, Class<H> type) {
         if (!(handler instanceof Handler.Container container)) {
            return null;
         } else {
            for (Handler child : container.getHandlers()) {
               if (type == null || type.isInstance(child)) {
                  return (H)child;
               }

               H descendant = this.findHandler(child, type);
               if (descendant != null) {
                  return descendant;
               }
            }

            return null;
         }
      }

      @Override
      public void setServer(Server server) {
         super.setServer(server);

         for (Handler child : this.getHandlers()) {
            child.setServer(server);
         }
      }

      @Override
      public InvocationType getInvocationType() {
         if (this._dynamic) {
            return InvocationType.BLOCKING;
         } else {
            InvocationType invocationType = InvocationType.NON_BLOCKING;

            for (Handler child : this.getHandlers()) {
               invocationType = Invocable.combine(invocationType, child.getInvocationType());
            }

            return invocationType;
         }
      }

      public static <T extends Handler.Container> T findContainerOf(Handler.Container root, Class<T> type, Handler handler) {
         if (root != null && handler != null) {
            List<Handler.Container> branches = root.getDescendants(type);
            if (branches != null) {
               for (Handler.Container container : branches) {
                  List<Handler> candidates = container.getDescendants((Class<Handler>)handler.getClass());
                  if (candidates != null) {
                     for (Handler c : candidates) {
                        if (c == handler) {
                           return (T)container;
                        }
                     }
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public interface Collection extends Handler.Container {
      default void addHandler(Handler handler) {
         List<Handler> list = new ArrayList<>(this.getHandlers());
         list.add(handler);
         this.setHandlers(list);
      }

      default boolean removeHandler(Handler handler) {
         List<Handler> list = new ArrayList<>(this.getHandlers());
         boolean removed = list.remove(handler);
         if (removed) {
            this.setHandlers(list);
         }

         return removed;
      }

      default void addHandler(Supplier<Handler> supplier) {
         this.addHandler(supplier.get());
      }

      void setHandlers(List<Handler> var1);

      default void setHandlers(Handler... handlers) {
         this.setHandlers(handlers != null && handlers.length != 0 ? List.of(handlers) : List.of());
      }
   }

   @ManagedObject
   public interface Container extends Handler {
      @ManagedAttribute(
         value = "The direct children Handlers of this Container",
         readonly = true
      )
      List<Handler> getHandlers();

      default List<Handler> getDescendants() {
         return this.getDescendants(Handler.class);
      }

      default <T extends Handler> List<T> getDescendants(Class<T> type) {
         List<T> handlers = new ArrayList<>();

         for (Handler h : this.getHandlers()) {
            if (type.isInstance(h)) {
               handlers.add((T)h);
            }

            if (h instanceof Handler.Container c) {
               handlers.addAll(c.getDescendants(type));
            }
         }

         return handlers;
      }

      default <T extends Handler> T getDescendant(Class<T> type) {
         for (Handler h : this.getHandlers()) {
            if (type.isInstance(h)) {
               return (T)h;
            }

            if (h instanceof Handler.Container c) {
               T t = c.getDescendant(type);
               if (t != null) {
                  return t;
               }
            }
         }

         return null;
      }

      default <T extends Handler.Container> T getContainer(Handler handler, Class<T> type) {
         if (handler == null) {
            return null;
         } else {
            for (T container : this.getDescendants(type)) {
               if (container.getDescendants(handler.getClass()).contains(handler)) {
                  return container;
               }
            }

            return null;
         }
      }
   }

   @ManagedObject
   public static class Sequence extends Handler.AbstractContainer implements Handler.Collection {
      private volatile List<Handler> _handlers = new ArrayList<>();

      public Sequence(Handler... handlers) {
         this(handlers == null || handlers.length == 0, handlers == null ? List.of() : List.of(handlers));
      }

      public Sequence(List<Handler> handlers) {
         this(handlers == null || handlers.isEmpty(), handlers);
      }

      public Sequence(boolean dynamic, List<Handler> handlers) {
         super(dynamic);
         this.setHandlers(handlers);
      }

      @Override
      public boolean handle(Request request, Response response, Callback callback) throws Exception {
         for (Handler h : this._handlers) {
            if (h.handle(request, response, callback)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public List<Handler> getHandlers() {
         return this._handlers;
      }

      @Override
      public void setHandlers(List<Handler> handlers) {
         if (!this.isDynamic() && this.isStarted()) {
            throw new IllegalStateException(this.getState());
         } else {
            List<Handler> newHandlers = this.newHandlers(handlers);
            Server server = this.getServer();
            InvocationType serverInvocationType = server == null ? null : server.getInvocationType();
            InvocationType invocationType = InvocationType.NON_BLOCKING;

            for (Handler handler : newHandlers) {
               if (handler != null) {
                  if (handler == this || handler instanceof Handler.Container container && container.getDescendants().contains(this)) {
                     throw new IllegalStateException("setHandler loop");
                  }

                  invocationType = Invocable.combine(invocationType, handler.getInvocationType());
                  if (server != null) {
                     handler.setServer(server);
                  }
               }
            }

            if (this.isDynamic()
               && server != null
               && server.isStarted()
               && serverInvocationType != invocationType
               && serverInvocationType != InvocationType.BLOCKING) {
               throw new IllegalArgumentException("Cannot change invocation type of started server");
            } else {
               this.updateBeans(this._handlers, handlers);
               this._handlers = newHandlers;
            }
         }
      }

      @Override
      public InvocationType getInvocationType() {
         if (this.isDynamic()) {
            return InvocationType.BLOCKING;
         } else {
            InvocationType invocationType = InvocationType.NON_BLOCKING;

            for (Handler handler : this._handlers) {
               invocationType = Invocable.combine(invocationType, handler.getInvocationType());
            }

            return invocationType;
         }
      }

      protected List<Handler> newHandlers(List<Handler> handlers) {
         return handlers == null ? List.of() : List.copyOf(handlers);
      }
   }

   @ManagedObject
   public interface Singleton extends Handler.Container {
      @ManagedAttribute(
         value = "The child Handler of this Container",
         readonly = true
      )
      Handler getHandler();

      void setHandler(Handler var1);

      default void setHandler(Supplier<Handler> supplier) {
         this.setHandler(supplier.get());
      }

      @Override
      default List<Handler> getHandlers() {
         Handler next = this.getHandler();
         return next == null ? Collections.emptyList() : Collections.singletonList(next);
      }

      default void insertHandler(Handler.Singleton handler) {
         Handler.Singleton tail = handler;

         while (tail.getHandler() instanceof Handler.Wrapper) {
            tail = (Handler.Wrapper)tail.getHandler();
         }

         if (tail.getHandler() != null) {
            throw new IllegalArgumentException("bad tail of inserted wrapper chain");
         } else {
            tail.setHandler(this.getHandler());
            this.setHandler(handler);
         }
      }

      default Handler.Singleton getTail() {
         Handler.Singleton tail = this;

         while (tail.getHandler() instanceof Handler.Singleton wrapped) {
            tail = wrapped;
         }

         return tail;
      }

      static Handler updateHandler(Handler.Singleton wrapper, Handler handler) {
         Server server = wrapper.getServer();
         if (server != null && server.isStarted() && handler != null) {
            InvocationType serverInvocationType = server.getInvocationType();
            if (serverInvocationType != Invocable.combine(serverInvocationType, handler.getInvocationType()) && serverInvocationType != InvocationType.BLOCKING
               )
             {
               throw new IllegalArgumentException("Cannot change invocation type of started server");
            }
         }

         if (handler == wrapper) {
            throw new IllegalStateException("Handler loop");
         } else {
            if (handler instanceof Handler.Container container && container.getDescendants().contains(wrapper)) {
               throw new IllegalStateException("Handler loop");
            }

            if (handler != null && server != null) {
               handler.setServer(server);
            }

            if (wrapper instanceof ContainerLifeCycle container) {
               container.updateBean(wrapper.getHandler(), handler);
            }

            return handler;
         }
      }
   }

   public static class Wrapper extends Handler.AbstractContainer implements Handler.Singleton {
      private Handler _handler;

      public Wrapper() {
         this(null);
      }

      public Wrapper(boolean dynamic) {
         this(dynamic, null);
      }

      public Wrapper(Handler handler) {
         this(false, handler);
      }

      public Wrapper(boolean dynamic, Handler handler) {
         super(dynamic);
         this._handler = handler == null ? null : Handler.Singleton.updateHandler(this, handler);
      }

      @Override
      public Handler getHandler() {
         return this._handler;
      }

      @Override
      public void setHandler(Handler handler) {
         if (!this.isDynamic() && this.isStarted()) {
            throw new IllegalStateException(this.getState());
         } else {
            this._handler = Handler.Singleton.updateHandler(this, handler);
         }
      }

      @Override
      public boolean handle(Request request, Response response, Callback callback) throws Exception {
         Handler next = this.getHandler();
         return next != null && next.handle(request, response, callback);
      }

      @Override
      public InvocationType getInvocationType() {
         if (this.isDynamic()) {
            return InvocationType.BLOCKING;
         } else {
            Handler next = this.getHandler();
            return next == null ? InvocationType.NON_BLOCKING : next.getInvocationType();
         }
      }
   }
}
