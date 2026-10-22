package jakarta.ws.rs.core;

import jakarta.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;

public interface StreamingOutput {
   void write(OutputStream var1) throws IOException, WebApplicationException;
}
