package org.eclipse.jetty.server.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.Index.Builder;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.thread.SerializedExecutor;
import org.eclipse.jetty.util.thread.SerializedExecutor.ErrorHandlingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Context Handler Collection")
public class ContextHandlerCollection extends Handler.Sequence {
   private static final Logger LOG = LoggerFactory.getLogger(ContextHandlerCollection.class);
   private final SerializedExecutor _serializedExecutor = new SerializedExecutor();

   public ContextHandlerCollection(ContextHandler... contexts) {
      this(true, contexts);
   }

   public ContextHandlerCollection(boolean dynamic, ContextHandler... contexts) {
      super(dynamic, List.of(contexts));
   }

   @ManagedOperation(
      value = "Update the mapping of context path to context",
      impact = "ACTION"
   )
   public void mapContexts() {
      this._serializedExecutor.execute(() -> {
         List<Handler> handlers = this.getHandlers();
         if (handlers != null) {
            super.setHandlers(this.newHandlers(handlers));
         }
      });
   }

   @Override
   protected List<Handler> newHandlers(List<Handler> handlers) {
      if (handlers != null && handlers.size() != 0) {
         Map<String, ContextHandlerCollection.Branch[]> path2Branches = new HashMap<>();

         for (Handler handler : handlers) {
            ContextHandlerCollection.Branch branch = new ContextHandlerCollection.Branch(handler);

            for (String contextPath : branch.getContextPaths()) {
               ContextHandlerCollection.Branch[] branches = path2Branches.get(contextPath);
               path2Branches.put(contextPath, (ContextHandlerCollection.Branch[])ArrayUtil.addToArray(branches, branch, ContextHandlerCollection.Branch.class));
            }
         }

         for (Entry<String, ContextHandlerCollection.Branch[]> entry : path2Branches.entrySet()) {
            ContextHandlerCollection.Branch[] branches = entry.getValue();
            ContextHandlerCollection.Branch[] sorted = new ContextHandlerCollection.Branch[branches.length];
            int i = 0;

            for (ContextHandlerCollection.Branch branch : branches) {
               if (branch.hasVirtualHost()) {
                  sorted[i++] = branch;
               }
            }

            for (ContextHandlerCollection.Branch branchx : branches) {
               if (!branchx.hasVirtualHost()) {
                  sorted[i++] = branchx;
               }
            }

            entry.setValue(sorted);
         }

         ContextHandlerCollection.Mapping mapping = new ContextHandlerCollection.Mapping(handlers, path2Branches);
         if (LOG.isDebugEnabled()) {
            LOG.debug("{}", mapping._pathBranches);
         }

         return mapping;
      } else {
         return Collections.emptyList();
      }
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      List<Handler> handlers = this.getHandlers();
      if (handlers == null || handlers.isEmpty()) {
         return false;
      } else if (!(handlers instanceof ContextHandlerCollection.Mapping)) {
         return super.handle(request, response, callback);
      } else {
         ContextHandlerCollection.Mapping mapping = (ContextHandlerCollection.Mapping)this.getHandlers();
         if (handlers.size() == 1) {
            return handlers.get(0).handle(request, response, callback);
         } else {
            Index<Entry<String, ContextHandlerCollection.Branch[]>> pathBranches = mapping._pathBranches;
            if (pathBranches == null) {
               return false;
            } else {
               String path = Request.getPathInContext(request);
               if (!path.startsWith("/")) {
                  return super.handle(request, response, callback);
               } else {
                  int limit = path.length() - 1;

                  while (limit >= 0) {
                     Entry<String, ContextHandlerCollection.Branch[]> branches = (Entry<String, ContextHandlerCollection.Branch[]>)pathBranches.getBest(
                        path, 1, limit
                     );
                     if (branches == null) {
                        break;
                     }

                     int l = branches.getKey().length();
                     if (l == 1 || path.length() == l || path.charAt(l) == '/') {
                        for (ContextHandlerCollection.Branch branch : branches.getValue()) {
                           try {
                              if (branch.getHandler().handle(request, response, callback)) {
                                 return true;
                              }
                           } catch (Throwable var16) {
                              LOG.warn("Unaccepted error {}", this, var16);
                           }
                        }
                     }

                     limit = l - 2;
                  }

                  return false;
               }
            }
         }
      }
   }

   @ManagedAttribute("The paths of the contexts in this collection")
   public Set<String> getContextPaths() {
      if (this.getHandlers() instanceof ContextHandlerCollection.Mapping mapping) {
         Index<Entry<String, ContextHandlerCollection.Branch[]>> index = mapping._pathBranches;
         return index.keySet()
            .stream()
            .map(index::get)
            .map(Entry::getValue)
            .flatMap(Stream::of)
            .flatMap(b -> b.getContextPaths().stream())
            .collect(Collectors.toCollection(TreeSet::new));
      } else {
         return Set.of();
      }
   }

   public void deployHandler(final Handler handler, final Callback callback) {
      this._serializedExecutor.execute(new ErrorHandlingTask() {
         public void run() {
            ContextHandlerCollection.this.addHandler(handler);
            callback.succeeded();
         }

         public void accept(Throwable throwable) {
            callback.failed(throwable);
         }
      });
   }

   public void undeployHandler(final Handler handler, final Callback callback) {
      this._serializedExecutor.execute(new ErrorHandlingTask() {
         public void run() {
            ContextHandlerCollection.this.removeHandler(handler);
            callback.succeeded();
         }

         public void accept(Throwable throwable) {
            callback.failed(throwable);
         }
      });
   }

   private static final class Branch {
      private final Handler _handler;
      private final List<ContextHandler> _contexts;

      Branch(Handler handler) {
         this._handler = handler;
         if (handler instanceof ContextHandler) {
            this._contexts = List.of((ContextHandler)handler);
         } else if (handler instanceof Handler.Container) {
            List<ContextHandler> contexts = ((Handler.Container)handler).getDescendants(ContextHandler.class);
            this._contexts = new ArrayList<>(contexts);
         } else {
            this._contexts = List.of();
         }
      }

      Set<String> getContextPaths() {
         Set<String> set = new HashSet<>();

         for (ContextHandler context : this._contexts) {
            set.add(context.getContextPath());
         }

         return set;
      }

      boolean hasVirtualHost() {
         for (ContextHandler context : this._contexts) {
            if (context.getVirtualHosts() != null && context.getVirtualHosts().size() > 0) {
               return true;
            }
         }

         return false;
      }

      Handler getHandler() {
         return this._handler;
      }

      @Override
      public String toString() {
         return String.format("{%s,%s}", this._handler, this._contexts);
      }
   }

   private static class Mapping extends ArrayList<Handler> {
      private final Index<Entry<String, ContextHandlerCollection.Branch[]>> _pathBranches;

      private Mapping(List<Handler> handlers, Map<String, ContextHandlerCollection.Branch[]> path2Branches) {
         super(handlers);
         this._pathBranches = new Builder().caseSensitive(true).withAll(() -> {
            Map<String, Entry<String, ContextHandlerCollection.Branch[]>> result = new LinkedHashMap<>();

            for (Entry<String, ContextHandlerCollection.Branch[]> entry : path2Branches.entrySet()) {
               result.put(entry.getKey().substring(1), entry);
            }

            return result;
         }).build();
      }
   }
}
