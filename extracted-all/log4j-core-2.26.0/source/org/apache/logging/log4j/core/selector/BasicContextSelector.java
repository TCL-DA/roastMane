package org.apache.logging.log4j.core.selector;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.util.Lazy;

public class BasicContextSelector implements ContextSelector {
   private final Lazy<LoggerContext> defaultLoggerContext = Lazy.lazy(() -> new LoggerContext("Default"));

   @Override
   public void shutdown(final String fqcn, final ClassLoader loader, final boolean currentContext, final boolean allContexts) {
      LoggerContext ctx = this.getContext(fqcn, loader, currentContext);
      if (ctx != null && ctx.isStarted()) {
         ctx.stop(50L, TimeUnit.MILLISECONDS);
      }
   }

   @Override
   public boolean hasContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
      LoggerContext ctx = this.getContext(fqcn, loader, currentContext);
      return ctx != null && ctx.isStarted();
   }

   @Override
   public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
      LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
      return ctx != null ? ctx : (LoggerContext)this.defaultLoggerContext.get();
   }

   @Override
   public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext, final URI configLocation) {
      LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
      return ctx != null ? ctx : (LoggerContext)this.defaultLoggerContext.get();
   }

   public LoggerContext locateContext(final String name, final String configLocation) {
      return (LoggerContext)this.defaultLoggerContext.get();
   }

   @Override
   public void removeContext(final LoggerContext context) {
   }

   @Override
   public boolean isClassLoaderDependent() {
      return false;
   }

   @Override
   public List<LoggerContext> getLoggerContexts() {
      return Collections.singletonList((LoggerContext)this.defaultLoggerContext.get());
   }
}
