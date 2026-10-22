package org.glassfish.jersey.message.filtering;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configuration;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.model.ResourceMethod;

@Singleton
@Priority(4200)
@ConstrainedTo(RuntimeType.SERVER)
class ServerScopeProvider extends CommonScopeProvider {
   @Inject
   private Provider<ExtendedUriInfo> uriInfoProvider;
   private final ConcurrentMap<String, Set<String>> uriToContexts = new ConcurrentHashMap<>();

   @Inject
   public ServerScopeProvider(Configuration config, InjectionManager injectionManager) {
      super(config, injectionManager);
   }

   @Override
   public Set<String> getFilteringScopes(Annotation[] entityAnnotations, boolean defaultIfNotFound) {
      Set<String> filteringScope = super.getFilteringScopes(entityAnnotations, false);
      if (filteringScope.isEmpty()) {
         ExtendedUriInfo uriInfo = (ExtendedUriInfo)this.uriInfoProvider.get();
         String path = uriInfo.getPath();
         if (this.uriToContexts.containsKey(path)) {
            return this.uriToContexts.get(path);
         }

         for (ResourceMethod method : getMatchedMethods(uriInfo)) {
            Invocable invocable = method.getInvocable();
            this.mergeFilteringScopes(filteringScope, this.getFilteringScopes(invocable.getHandlingMethod(), invocable.getHandler().getHandlerClass()));
            if (!filteringScope.isEmpty()) {
               this.uriToContexts.putIfAbsent(path, filteringScope);
               return filteringScope;
            }
         }
      }

      return this.returnFilteringScopes(filteringScope, defaultIfNotFound);
   }

   protected Set<String> getFilteringScopes(Method resourceMethod, Class<?> resourceClass) {
      Set<String> scope = this.getFilteringScopes(resourceMethod.getAnnotations());
      if (scope.isEmpty()) {
         scope = this.getFilteringScopes(resourceClass.getAnnotations());
      }

      return scope;
   }

   private static List<ResourceMethod> getMatchedMethods(ExtendedUriInfo uriInfo) {
      List<ResourceMethod> matchedResourceLocators = uriInfo.getMatchedResourceLocators();
      List<ResourceMethod> methods = new ArrayList<>(1 + matchedResourceLocators.size());
      methods.add(uriInfo.getMatchedResourceMethod());
      methods.addAll(matchedResourceLocators);
      return methods;
   }
}
