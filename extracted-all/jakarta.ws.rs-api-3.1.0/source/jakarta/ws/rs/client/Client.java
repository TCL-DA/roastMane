package jakarta.ws.rs.client;

import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public interface Client extends Configurable<Client>, AutoCloseable {
   @Override
   void close();

   WebTarget target(String var1);

   WebTarget target(URI var1);

   WebTarget target(UriBuilder var1);

   WebTarget target(Link var1);

   Invocation.Builder invocation(Link var1);

   SSLContext getSslContext();

   HostnameVerifier getHostnameVerifier();
}
