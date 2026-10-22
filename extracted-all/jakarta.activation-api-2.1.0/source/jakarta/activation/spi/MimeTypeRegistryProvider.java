package jakarta.activation.spi;

import jakarta.activation.MimeTypeRegistry;
import java.io.IOException;
import java.io.InputStream;

public interface MimeTypeRegistryProvider {
   MimeTypeRegistry getByFileName(String var1) throws IOException;

   MimeTypeRegistry getByInputStream(InputStream var1) throws IOException;

   MimeTypeRegistry getInMemory();
}
