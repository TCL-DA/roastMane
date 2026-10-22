package org.glassfish.jersey.server.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.util.Tokenizer;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.LocalizationMessages;

@PreMatching
@Priority(3050)
public final class HttpMethodOverrideFilter implements ContainerRequestFilter {
   final int config;

   public static void enableFor(ResourceConfig rc, HttpMethodOverrideFilter.Source... sources) {
      rc.registerClasses(HttpMethodOverrideFilter.class);
      rc.property("jersey.config.server.httpMethodOverride", sources);
   }

   public HttpMethodOverrideFilter(@Context Configuration rc) {
      this(parseConfig(rc.getProperty("jersey.config.server.httpMethodOverride")));
   }

   public HttpMethodOverrideFilter(HttpMethodOverrideFilter.Source... sources) {
      int c = 0;

      for (HttpMethodOverrideFilter.Source cf : sources) {
         if (cf != null) {
            c |= cf.getFlag();
         }
      }

      if (c == 0) {
         c = 3;
      }

      this.config = c;
   }

   private static HttpMethodOverrideFilter.Source[] parseConfig(Object config) {
      if (config == null) {
         return new HttpMethodOverrideFilter.Source[0];
      } else if (config instanceof HttpMethodOverrideFilter.Source[]) {
         return (HttpMethodOverrideFilter.Source[])config;
      } else if (config instanceof HttpMethodOverrideFilter.Source) {
         return new HttpMethodOverrideFilter.Source[]{(HttpMethodOverrideFilter.Source)config};
      } else {
         String[] stringValues;
         if (config instanceof String) {
            stringValues = Tokenizer.tokenize((String)config, " ,;\n");
         } else {
            if (!(config instanceof String[])) {
               return new HttpMethodOverrideFilter.Source[0];
            }

            stringValues = Tokenizer.tokenize((String[])config, " ,;\n");
         }

         HttpMethodOverrideFilter.Source[] result = new HttpMethodOverrideFilter.Source[stringValues.length];

         for (int i = 0; i < stringValues.length; i++) {
            try {
               result[i] = HttpMethodOverrideFilter.Source.valueOf(stringValues[i]);
            } catch (IllegalArgumentException var5) {
               Logger.getLogger(HttpMethodOverrideFilter.class.getName())
                  .log(Level.WARNING, LocalizationMessages.INVALID_CONFIG_PROPERTY_VALUE("jersey.config.server.httpMethodOverride", stringValues[i]));
            }
         }

         return result;
      }
   }

   private String getParamValue(HttpMethodOverrideFilter.Source source, MultivaluedMap<String, String> paramsMap, String paramName) {
      String value = source.isPresentIn(this.config) ? (String)paramsMap.getFirst(paramName) : null;
      if (value == null) {
         return null;
      } else {
         value = value.trim();
         return value.length() == 0 ? null : value.toUpperCase(Locale.ROOT);
      }
   }

   public void filter(ContainerRequestContext request) {
      if (request.getMethod().equalsIgnoreCase("POST")) {
         String header = this.getParamValue(HttpMethodOverrideFilter.Source.HEADER, request.getHeaders(), "X-HTTP-Method-Override");
         String query = this.getParamValue(HttpMethodOverrideFilter.Source.QUERY, request.getUriInfo().getQueryParameters(), "_method");
         String override;
         if (header == null) {
            override = query;
         } else {
            override = header;
            if (query != null && !query.equals(header)) {
               throw new BadRequestException();
            }
         }

         if (override != null) {
            request.setMethod(override);
            if (override.equals("GET")
               && request.getMediaType() != null
               && MediaType.APPLICATION_FORM_URLENCODED_TYPE.getType().equals(request.getMediaType().getType())) {
               UriBuilder ub = request.getUriInfo().getRequestUriBuilder();
               Form f = ((ContainerRequest)request).readEntity(Form.class);

               for (Entry<String, List<String>> param : f.asMap().entrySet()) {
                  ub.queryParam(param.getKey(), param.getValue().toArray());
               }

               request.setRequestUri(request.getUriInfo().getBaseUri(), ub.build(new Object[0]));
            }
         }
      }
   }

   public static enum Source {
      HEADER(1),
      QUERY(2);

      private final int flag;

      private Source(int flag) {
         this.flag = flag;
      }

      public int getFlag() {
         return this.flag;
      }

      public boolean isPresentIn(int config) {
         return (config & this.flag) == this.flag;
      }
   }
}
