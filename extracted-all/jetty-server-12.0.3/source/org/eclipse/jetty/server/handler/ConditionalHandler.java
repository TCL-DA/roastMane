package org.eclipse.jetty.server.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.http.pathmap.PathSpecSet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IncludeExclude;
import org.eclipse.jetty.util.IncludeExcludeSet;
import org.eclipse.jetty.util.InetAddressPattern;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConditionalHandler extends Handler.Wrapper {
   private static final Logger LOG = LoggerFactory.getLogger(ConditionalHandler.class);
   private final IncludeExclude<String> _methods = new IncludeExclude();
   private final IncludeExclude<String> _pathSpecs = new IncludeExclude(PathSpecSet.class);
   private final IncludeExcludeSet<Predicate<Request>, Request> _predicates = new IncludeExcludeSet(ConditionalHandler.PredicateSet.class);
   private Predicate<Request> _handlePredicate;

   private ConditionalHandler() {
      this(false, null);
   }

   private ConditionalHandler(Handler nextHandler) {
      this(false, nextHandler);
   }

   private ConditionalHandler(boolean dynamic, Handler nextHandler) {
      super(dynamic, nextHandler);
   }

   public void clear() {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._methods.clear();
         this._pathSpecs.clear();
         this._predicates.clear();
      }
   }

   IncludeExclude<String> getMethods() {
      return this._methods;
   }

   IncludeExclude<String> getPathSpecs() {
      return this._pathSpecs;
   }

   IncludeExcludeSet<Predicate<Request>, Request> getPredicates() {
      return this._predicates;
   }

   public void includeMethod(String... methods) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._methods.include(methods);
      }
   }

   public void excludeMethod(String... methods) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._methods.exclude(methods);
      }
   }

   public void include(PathSpec... paths) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         for (PathSpec p : paths) {
            ((PathSpecSet)this._pathSpecs.getIncluded()).add(p);
         }
      }
   }

   public void exclude(PathSpec... paths) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         for (PathSpec p : paths) {
            ((PathSpecSet)this._pathSpecs.getExcluded()).add(p);
         }
      }
   }

   public void includePath(String... paths) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._pathSpecs.include(paths);
      }
   }

   public void excludePath(String... paths) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._pathSpecs.exclude(paths);
      }
   }

   public void include(InetAddressPattern... patterns) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         for (InetAddressPattern p : patterns) {
            this._predicates.include(new ConditionalHandler.InetAddressPatternPredicate(p));
         }
      }
   }

   public void includeInetAddressPattern(String... patterns) {
      for (String p : patterns) {
         this.include(InetAddressPattern.from(p));
      }
   }

   public void exclude(InetAddressPattern... patterns) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         for (InetAddressPattern p : patterns) {
            this._predicates.exclude(new ConditionalHandler.InetAddressPatternPredicate(p));
         }
      }
   }

   public void excludeInetAddressPattern(String... patterns) {
      for (String p : patterns) {
         this.exclude(InetAddressPattern.from(p));
      }
   }

   @SafeVarargs
   public final void include(Predicate<Request>... predicates) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         for (Predicate<Request> p : predicates) {
            if (p instanceof ConditionalHandler.MethodPredicate methodPredicate) {
               this.includeMethod(methodPredicate._method);
            } else if (p instanceof ConditionalHandler.PathSpecPredicate pathSpecPredicate) {
               this.include(pathSpecPredicate._pathSpec);
            } else {
               this._predicates.include(p);
            }
         }
      }
   }

   @SafeVarargs
   public final void exclude(Predicate<Request>... predicates) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         for (Predicate<Request> p : predicates) {
            if (p instanceof ConditionalHandler.MethodPredicate methodPredicate) {
               this.excludeMethod(methodPredicate._method);
            } else if (p instanceof ConditionalHandler.PathSpecPredicate pathSpecPredicate) {
               this.exclude(pathSpecPredicate._pathSpec);
            } else {
               this._predicates.exclude(p);
            }
         }
      }
   }

   private boolean testMethods(Request request) {
      return this._methods.test(request.getMethod());
   }

   private boolean testPathSpecs(Request request) {
      return this._pathSpecs.test(Request.getPathInContext(request));
   }

   private boolean testPredicates(Request request) {
      return this._predicates.test(request);
   }

   @Override
   protected void doStart() throws Exception {
      this._handlePredicate = TypeUtil.truePredicate();
      if (!this._methods.isEmpty()) {
         this._handlePredicate = this._handlePredicate.and(this::testMethods);
      }

      if (!this._pathSpecs.isEmpty()) {
         this._handlePredicate = this._handlePredicate.and(this::testPathSpecs);
      }

      if (!this._predicates.isEmpty()) {
         this._handlePredicate = this._handlePredicate.and(this::testPredicates);
      }

      super.doStart();
   }

   @Override
   public final boolean handle(Request request, Response response, Callback callback) throws Exception {
      return this._handlePredicate.test(request) ? this.onConditionsMet(request, response, callback) : this.onConditionsNotMet(request, response, callback);
   }

   protected abstract boolean onConditionsMet(Request var1, Response var2, Callback var3) throws Exception;

   protected abstract boolean onConditionsNotMet(Request var1, Response var2, Callback var3) throws Exception;

   protected boolean nextHandler(Request request, Response response, Callback callback) throws Exception {
      return super.handle(request, response, callback);
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(
         out,
         indent,
         new Object[]{
            new DumpableCollection("included methods", this._methods.getIncluded()),
            new DumpableCollection("included paths", this._pathSpecs.getIncluded()),
            new DumpableCollection("included predicates", this._predicates.getIncluded()),
            new DumpableCollection("excluded methods", this._methods.getExcluded()),
            new DumpableCollection("excluded paths", this._pathSpecs.getExcluded()),
            new DumpableCollection("excluded predicates", this._predicates.getExcluded())
         }
      );
   }

   public static Predicate<Request> from(String connectorName, String inetAddressPattern, String method, String pathSpec) {
      return from(connectorName, InetAddressPattern.from(inetAddressPattern), method, pathSpec == null ? null : PathSpec.from(pathSpec));
   }

   public static Predicate<Request> from(String connectorName, InetAddressPattern inetAddressPattern, String method, PathSpec pathSpec) {
      Predicate<Request> predicate = TypeUtil.truePredicate();
      if (connectorName != null) {
         predicate = predicate.and(new ConditionalHandler.ConnectorPredicate(connectorName));
      }

      if (inetAddressPattern != null) {
         predicate = predicate.and(new ConditionalHandler.InetAddressPatternPredicate(inetAddressPattern));
      }

      if (method != null) {
         predicate = predicate.and(new ConditionalHandler.MethodPredicate(method));
      }

      if (pathSpec != null) {
         predicate = predicate.and(new ConditionalHandler.PathSpecPredicate(pathSpec));
      }

      return predicate;
   }

   public abstract static class Abstract extends ConditionalHandler {
      protected Abstract() {
      }

      protected Abstract(Handler nextHandler) {
         super(nextHandler);
      }

      protected Abstract(boolean dynamic, Handler nextHandler) {
         super(dynamic, nextHandler);
      }
   }

   public static class ConnectorPredicate implements Predicate<Request> {
      private final String _connector;

      public ConnectorPredicate(String connector) {
         this._connector = Objects.requireNonNull(connector);
      }

      public boolean test(Request request) {
         return this._connector.equals(request.getConnectionMetaData().getConnector().getName());
      }

      @Override
      public int hashCode() {
         return this._connector.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof ConditionalHandler.ConnectorPredicate other && this._connector.equals(other._connector)) {
            return true;
         }

         return false;
      }

      @Override
      public String toString() {
         return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), this._connector);
      }
   }

   public static class DontHandle extends ConditionalHandler.ElseNext {
      public DontHandle() {
      }

      public DontHandle(Handler handler) {
         super(handler);
      }

      @Override
      protected boolean onConditionsMet(Request request, Response response, Callback callback) throws Exception {
         return false;
      }
   }

   public abstract static class ElseNext extends ConditionalHandler {
      public ElseNext() {
         this(null);
      }

      public ElseNext(Handler handler) {
         super(handler);
      }

      @Override
      protected boolean onConditionsNotMet(Request request, Response response, Callback callback) throws Exception {
         return this.nextHandler(request, response, callback);
      }
   }

   public static class InetAddressPatternPredicate implements Predicate<Request> {
      private final InetAddressPattern _pattern;

      public static InetAddress getInetAddress(SocketAddress socketAddress) {
         if (!(socketAddress instanceof InetSocketAddress inetSocketAddress)) {
            return null;
         } else if (inetSocketAddress.isUnresolved()) {
            try {
               return InetAddress.getByName(inetSocketAddress.getHostString());
            } catch (UnknownHostException var3) {
               if (ConditionalHandler.LOG.isTraceEnabled()) {
                  ConditionalHandler.LOG.trace("ignored", var3);
               }

               return null;
            }
         } else {
            return inetSocketAddress.getAddress();
         }
      }

      public InetAddressPatternPredicate(InetAddressPattern pattern) {
         this._pattern = pattern;
      }

      public boolean test(Request request) {
         return this._pattern.test(getInetAddress(request.getConnectionMetaData().getRemoteSocketAddress()));
      }

      @Override
      public int hashCode() {
         return this._pattern.hashCode();
      }

      @Override
      public boolean equals(Object other) {
         if (other instanceof ConditionalHandler.InetAddressPatternPredicate inetAddressPatternPredicate
            && this._pattern.equals(inetAddressPatternPredicate._pattern)) {
            return true;
         }

         return false;
      }

      @Override
      public String toString() {
         return "%s@%x{%s}".formatted(this.getClass().getSimpleName(), this.hashCode(), this._pattern);
      }
   }

   public static class MethodPredicate implements Predicate<Request> {
      private final String _method;

      public MethodPredicate(String method) {
         this._method = Objects.requireNonNull(method);
      }

      public boolean test(Request request) {
         return this._method.equals(request.getMethod());
      }

      @Override
      public int hashCode() {
         return this._method.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof ConditionalHandler.MethodPredicate other && this._method.equals(other._method)) {
            return true;
         }

         return false;
      }

      @Override
      public String toString() {
         return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), this._method);
      }
   }

   public static class PathSpecPredicate implements Predicate<Request> {
      private final PathSpec _pathSpec;

      public PathSpecPredicate(PathSpec pathSpec) {
         this._pathSpec = Objects.requireNonNull(pathSpec);
      }

      public boolean test(Request request) {
         return this._pathSpec.matches(Request.getPathInContext(request));
      }

      @Override
      public int hashCode() {
         return this._pathSpec.hashCode();
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof ConditionalHandler.PathSpecPredicate other && this._pathSpec.equals(other._pathSpec)) {
            return true;
         }

         return false;
      }

      @Override
      public String toString() {
         return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), this._pathSpec);
      }
   }

   public static class PredicateSet extends AbstractSet<Predicate<Request>> implements Set<Predicate<Request>>, Predicate<Request> {
      private final ArrayList<Predicate<Request>> _predicates = new ArrayList<>();

      public boolean add(Predicate<Request> predicate) {
         return this._predicates.contains(predicate) ? false : this._predicates.add(predicate);
      }

      @Override
      public boolean remove(Object o) {
         return this._predicates.remove(o);
      }

      @Override
      public Iterator<Predicate<Request>> iterator() {
         return this._predicates.iterator();
      }

      @Override
      public int size() {
         return this._predicates.size();
      }

      public boolean test(Request request) {
         if (request == null) {
            return false;
         } else {
            for (Predicate<Request> predicate : this._predicates) {
               if (predicate.test(request)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   public static class Reject extends ConditionalHandler.ElseNext {
      private final int _status;

      public Reject() {
         this(null, 403);
      }

      public Reject(int status) {
         this(null, status);
      }

      public Reject(Handler handler) {
         this(handler, 403);
      }

      public Reject(Handler handler, int status) {
         super(handler);
         if (status >= 200 && status <= 999) {
            this._status = status;
         } else {
            throw new IllegalArgumentException("bad status");
         }
      }

      @Override
      protected boolean onConditionsMet(Request request, Response response, Callback callback) throws Exception {
         Response.writeError(request, response, callback, this._status);
         return true;
      }
   }

   public static class SkipNext extends ConditionalHandler.ElseNext {
      public SkipNext() {
      }

      public SkipNext(Handler handler) {
         super(handler);
      }

      @Override
      protected boolean onConditionsMet(Request request, Response response, Callback callback) throws Exception {
         if (!(this.getHandler() instanceof Handler.Singleton nextHandler)) {
            return false;
         } else {
            Handler nextNext = nextHandler.getHandler();
            return nextNext != null && nextNext.handle(request, response, callback);
         }
      }
   }
}
