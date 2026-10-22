package jakarta.ws.rs.ext;

import jakarta.ws.rs.WebApplicationException;
import java.io.IOException;

public interface WriterInterceptor {
   void aroundWriteTo(WriterInterceptorContext var1) throws IOException, WebApplicationException;
}
