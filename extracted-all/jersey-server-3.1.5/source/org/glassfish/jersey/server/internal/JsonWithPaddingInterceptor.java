package org.glassfish.jersey.server.internal;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.InterceptorContext;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.glassfish.jersey.message.MessageUtils;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.JSONP;

@Priority(4100)
public class JsonWithPaddingInterceptor implements WriterInterceptor {
   private static final Map<String, Set<String>> JAVASCRIPT_TYPES = new HashMap<>(2);
   @Inject
   private Provider<ContainerRequest> containerRequestProvider;

   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
      boolean isJavascript = this.isJavascript(context.getMediaType());
      JSONP jsonp = this.getJsonpAnnotation(context);
      boolean wrapIntoCallback = isJavascript && jsonp != null;
      if (wrapIntoCallback) {
         context.setMediaType(MediaType.APPLICATION_JSON_TYPE);
         context.getOutputStream().write(this.getCallbackName(jsonp).getBytes(MessageUtils.getCharset(context.getMediaType())));
         context.getOutputStream().write(40);
      }

      context.proceed();
      if (wrapIntoCallback) {
         context.getOutputStream().write(41);
      }
   }

   private boolean isJavascript(MediaType mediaType) {
      if (mediaType == null) {
         return false;
      } else {
         Set<String> subtypes = JAVASCRIPT_TYPES.get(mediaType.getType());
         return subtypes != null && subtypes.contains(mediaType.getSubtype());
      }
   }

   private String getCallbackName(JSONP jsonp) {
      String callback = jsonp.callback();
      if (!"".equals(jsonp.queryParam())) {
         ContainerRequest containerRequest = (ContainerRequest)this.containerRequestProvider.get();
         UriInfo uriInfo = containerRequest.getUriInfo();
         MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
         List<String> queryParameter = (List<String>)queryParameters.get(jsonp.queryParam());
         callback = queryParameter != null && !queryParameter.isEmpty() ? queryParameter.get(0) : callback;
      }

      return callback;
   }

   private JSONP getJsonpAnnotation(InterceptorContext context) {
      Annotation[] annotations = context.getAnnotations();
      if (annotations != null && annotations.length > 0) {
         for (Annotation annotation : annotations) {
            if (annotation instanceof JSONP) {
               return (JSONP)annotation;
            }
         }
      }

      return null;
   }

   static {
      JAVASCRIPT_TYPES.put("application", Arrays.asList("x-javascript", "ecmascript", "javascript").stream().collect(Collectors.toSet()));
      JAVASCRIPT_TYPES.put("text", Arrays.asList("javascript", "x-javascript", "ecmascript", "jscript").stream().collect(Collectors.toSet()));
   }
}
