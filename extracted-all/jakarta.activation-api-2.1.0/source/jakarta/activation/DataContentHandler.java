package jakarta.activation;

import java.io.IOException;
import java.io.OutputStream;

public interface DataContentHandler {
   ActivationDataFlavor[] getTransferDataFlavors();

   Object getTransferData(ActivationDataFlavor var1, DataSource var2) throws IOException;

   Object getContent(DataSource var1) throws IOException;

   void writeTo(Object var1, String var2, OutputStream var3) throws IOException;
}
