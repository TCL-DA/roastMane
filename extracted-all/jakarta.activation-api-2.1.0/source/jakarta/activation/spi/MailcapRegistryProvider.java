package jakarta.activation.spi;

import jakarta.activation.MailcapRegistry;
import java.io.IOException;
import java.io.InputStream;

public interface MailcapRegistryProvider {
   MailcapRegistry getByFileName(String var1) throws IOException;

   MailcapRegistry getByInputStream(InputStream var1) throws IOException;

   MailcapRegistry getInMemory();
}
