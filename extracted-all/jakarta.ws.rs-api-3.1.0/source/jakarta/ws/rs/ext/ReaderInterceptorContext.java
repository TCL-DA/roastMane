package jakarta.ws.rs.ext;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;

public interface ReaderInterceptorContext extends InterceptorContext {
   Object proceed() throws IOException, WebApplicationException;

   InputStream getInputStream();

   void setInputStream(InputStream var1);

   MultivaluedMap<String, String> getHeaders();
}
