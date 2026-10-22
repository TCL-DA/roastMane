package jakarta.ws.rs.ext;

import jakarta.ws.rs.WebApplicationException;
import java.io.IOException;

public interface ReaderInterceptor {
   Object aroundReadFrom(ReaderInterceptorContext var1) throws IOException, WebApplicationException;
}
