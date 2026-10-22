package org.glassfish.jersey.message.filtering;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.glassfish.jersey.internal.util.Tokenizer;
import org.glassfish.jersey.message.filtering.spi.ScopeResolver;

@Singleton
public class SelectableScopeResolver implements ScopeResolver {
   public static final String PREFIX = SelectableScopeResolver.class.getName() + "_";
   public static final String DEFAULT_SCOPE = PREFIX + "*";
   private static String SELECTABLE_PARAM_NAME = "select";
   @Context
   private Configuration configuration;
   @Context
   private UriInfo uriInfo;

   @PostConstruct
   private void init() {
      String paramName = (String)this.configuration.getProperty("jersey.config.entityFiltering.selectable.query");
      SELECTABLE_PARAM_NAME = paramName != null ? paramName : SELECTABLE_PARAM_NAME;
   }

   @Override
   public Set<String> resolve(Annotation[] annotations) {
      Set<String> scopes = new HashSet<>();
      List<String> fields = (List<String>)this.uriInfo.getQueryParameters().get(SELECTABLE_PARAM_NAME);
      if (fields != null && !fields.isEmpty()) {
         for (String field : fields) {
            scopes.addAll(this.getScopesForField(field));
         }
      } else {
         scopes.add(DEFAULT_SCOPE);
      }

      return scopes;
   }

   private Set<String> getScopesForField(String fieldName) {
      Set<String> scopes = new HashSet<>();
      String[] fields = Tokenizer.tokenize(fieldName, ",");

      for (String field : fields) {
         String[] subfields = Tokenizer.tokenize(field, ".");
         if (subfields.length != 0) {
            scopes.add(PREFIX + subfields[0]);
            if (subfields.length > 1) {
               scopes.add(PREFIX + field);
            }
         }
      }

      return scopes;
   }
}
