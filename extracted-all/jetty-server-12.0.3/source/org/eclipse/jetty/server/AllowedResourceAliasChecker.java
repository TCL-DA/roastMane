package org.eclipse.jetty.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllowedResourceAliasChecker extends AbstractLifeCycle implements AliasCheck {
   private static final Logger LOG = LoggerFactory.getLogger(AllowedResourceAliasChecker.class);
   protected static final LinkOption[] FOLLOW_LINKS = new LinkOption[0];
   protected static final LinkOption[] NO_FOLLOW_LINKS = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};
   private final ContextHandler _contextHandler;
   private final Supplier<Resource> _resourceBaseSupplier;
   private final List<Path> _protected = new ArrayList<>();
   private final AllowedResourceAliasChecker.AllowedResourceAliasCheckListener _listener = new AllowedResourceAliasChecker.AllowedResourceAliasCheckListener();
   private boolean _initialized;
   protected Path _base;

   public AllowedResourceAliasChecker(ContextHandler contextHandler) {
      this(contextHandler, contextHandler::getBaseResource);
   }

   public AllowedResourceAliasChecker(ContextHandler contextHandler, Resource baseResource) {
      this(contextHandler, (Supplier<Resource>)(() -> baseResource));
   }

   public AllowedResourceAliasChecker(ContextHandler contextHandler, Supplier<Resource> resourceBaseSupplier) {
      this._contextHandler = Objects.requireNonNull(contextHandler);
      this._resourceBaseSupplier = Objects.requireNonNull(resourceBaseSupplier);
   }

   protected ContextHandler getContextHandler() {
      return this._contextHandler;
   }

   private String[] getProtectedTargets() {
      return this._contextHandler.getProtectedTargets();
   }

   private void extractBaseResourceFromContext() {
      this._base = this.getPath(this._resourceBaseSupplier.get());
      if (this._base != null) {
         try {
            if (Files.exists(this._base, NO_FOLLOW_LINKS)) {
               this._base = this._base.toRealPath(FOLLOW_LINKS);
            }

            String[] protectedTargets = this.getProtectedTargets();
            if (protectedTargets != null) {
               for (String s : protectedTargets) {
                  this._protected.add(this._base.getFileSystem().getPath(this._base.toString(), s));
               }
            }
         } catch (IOException var6) {
            LOG.warn("Base resource failure ({} is disabled): {}", new Object[]{this.getClass().getName(), this._base, var6});
            this._base = null;
         }
      }
   }

   protected void initialize() {
      this.extractBaseResourceFromContext();
      this._initialized = true;
   }

   protected void doStart() throws Exception {
      if (this._contextHandler.isStarted()) {
         this.initialize();
      } else {
         this._contextHandler.addEventListener(this._listener);
      }
   }

   protected void doStop() throws Exception {
      this._contextHandler.removeEventListener(this._listener);
      this._base = null;
      this._protected.clear();
   }

   @Override
   public boolean checkAlias(String pathInContext, Resource resource) {
      if (!this._initialized) {
         this.extractBaseResourceFromContext();
      }

      if (this._base == null) {
         return false;
      } else {
         try {
            if (!resource.exists()) {
               return false;
            } else {
               Path path = this.getPath(resource);
               return path == null ? false : this.check(pathInContext, path);
            }
         } catch (Throwable var4) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Failed to check alias", var4);
            }

            return false;
         }
      }
   }

   protected boolean check(String pathInContext, Path path) {
      return this.isAllowed(getRealPath(path));
   }

   protected boolean isAllowed(Path path) {
      if (path != null && Files.exists(path)) {
         while (path != null) {
            if (this.isSameFile(path, this._base)) {
               return true;
            }

            for (Path p : this._protected) {
               if (this.isSameFile(path, p)) {
                  return false;
               }
            }

            path = path.getParent();
         }
      }

      return false;
   }

   protected boolean isSameFile(Path path1, Path path2) {
      if (Objects.equals(path1, path2)) {
         return true;
      } else {
         try {
            if (Files.isSameFile(path1, path2)) {
               return true;
            }
         } catch (Throwable var4) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("ignored", var4);
            }
         }

         return false;
      }
   }

   private static Path getRealPath(Path path) {
      if (path != null && Files.exists(path)) {
         try {
            path = path.toRealPath(FOLLOW_LINKS);
            if (Files.exists(path)) {
               return path;
            }
         } catch (IOException var2) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("No real path for {}", path, var2);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   protected Path getPath(Resource resource) {
      try {
         return resource == null ? null : resource.getPath();
      } catch (Throwable var3) {
         LOG.trace("getPath() failed", var3);
         return null;
      }
   }

   @Override
   public String toString() {
      String[] protectedTargets = this.getProtectedTargets();
      return String.format(
         "%s@%x{base=%s,protected=%s}",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this._base,
         protectedTargets == null ? null : Arrays.asList(protectedTargets)
      );
   }

   private class AllowedResourceAliasCheckListener implements Listener {
      public void lifeCycleStarted(LifeCycle event) {
         AllowedResourceAliasChecker.this.initialize();
      }
   }
}
