package jakarta.ws.rs.ext;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;

public interface WriterInterceptorContext extends InterceptorContext {
   void proceed() throws IOException, WebApplicationException;

   Object getEntity();

   void setEntity(Object var1);

   OutputStream getOutputStream();

   void setOutputStream(OutputStream var1);

   MultivaluedMap<String, Object> getHeaders();
}
