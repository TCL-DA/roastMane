package org.glassfish.jersey.message.filtering;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Configuration;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.message.filtering.internal.LocalizationMessages;
import org.glassfish.jersey.message.filtering.spi.FilteringHelper;
import org.glassfish.jersey.message.filtering.spi.ScopeProvider;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;
import org.glassfish.jersey.model.internal.RankedComparator;

@Singleton
class CommonScopeProvider implements ScopeProvider {
   private static final Logger LOGGER = Logger.getLogger(CommonScopeProvider.class.getName());
   private final List<ScopeResolver> resolvers;
   private final Configuration config;

   @Inject
   public CommonScopeProvider(Configuration config, InjectionManager injectionManager) {
      this.config = config;
      Spliterator<ScopeResolver> resolverSpliterator = Providers.getAllProviders(injectionManager, ScopeResolver.class, new RankedComparator()).spliterator();
      this.resolvers = StreamSupport.stream(resolverSpliterator, false).collect(Collectors.toList());
   }

   @Override
   public Set<String> getFilteringScopes(Annotation[] entityAnnotations, boolean defaultIfNotFound) {
      Set<String> filteringScopes = new HashSet<>();
      filteringScopes.addAll(this.getFilteringScopes(entityAnnotations));
      if (filteringScopes.isEmpty()) {
         filteringScopes.addAll(this.getFilteringScopes(this.config));
      }

      return this.returnFilteringScopes(filteringScopes, defaultIfNotFound);
   }

   protected Set<String> returnFilteringScopes(Set<String> filteringScopes, boolean returnDefaultFallback) {
      return returnDefaultFallback && filteringScopes.isEmpty() ? FilteringHelper.getDefaultFilteringScope() : filteringScopes;
   }

   protected Set<String> getFilteringScopes(Annotation[] annotations) {
      Set<String> filteringScopes = new HashSet<>();

      for (ScopeResolver provider : this.resolvers) {
         this.mergeFilteringScopes(filteringScopes, provider.resolve(annotations));
      }

      return filteringScopes;
   }

   private Set<String> getFilteringScopes(Configuration config) {
      Object property = config.getProperty("jersey.config.entityFiltering.scope");
      Set<String> filteringScopes = Collections.emptySet();
      if (property != null) {
         if (property instanceof Annotation) {
            filteringScopes = this.getFilteringScopes(new Annotation[]{(Annotation)property});
         } else if (property instanceof Annotation[]) {
            filteringScopes = this.getFilteringScopes((Annotation[])property);
         } else {
            LOGGER.log(Level.CONFIG, LocalizationMessages.ENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(property));
         }
      }

      return filteringScopes;
   }

   protected void mergeFilteringScopes(Set<String> filteringScopes, Set<String> resolvedScopes) {
      if (!filteringScopes.isEmpty() && !resolvedScopes.isEmpty()) {
         LOGGER.log(Level.FINE, LocalizationMessages.MERGING_FILTERING_SCOPES());
      }

      filteringScopes.addAll(resolvedScopes);
   }
}
