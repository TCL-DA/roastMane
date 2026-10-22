package jakarta.ws.rs.container;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface ContainerRequestContext {
   Object getProperty(String var1);

   default boolean hasProperty(String name) {
      return this.getProperty(name) != null;
   }

   Collection<String> getPropertyNames();

   void setProperty(String var1, Object var2);

   void removeProperty(String var1);

   UriInfo getUriInfo();

   void setRequestUri(URI var1);

   void setRequestUri(URI var1, URI var2);

   Request getRequest();

   String getMethod();

   void setMethod(String var1);

   MultivaluedMap<String, String> getHeaders();

   String getHeaderString(String var1);

   Date getDate();

   Locale getLanguage();

   int getLength();

   MediaType getMediaType();

   List<MediaType> getAcceptableMediaTypes();

   List<Locale> getAcceptableLanguages();

   Map<String, Cookie> getCookies();

   boolean hasEntity();

   InputStream getEntityStream();

   void setEntityStream(InputStream var1);

   SecurityContext getSecurityContext();

   void setSecurityContext(SecurityContext var1);

   void abortWith(Response var1);
}
