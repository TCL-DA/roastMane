package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DataHandlerDataSource implements DataSource {
   DataHandler dataHandler = null;

   public DataHandlerDataSource(DataHandler dh) {
      this.dataHandler = dh;
   }

   @Override
   public InputStream getInputStream() throws IOException {
      return this.dataHandler.getInputStream();
   }

   @Override
   public OutputStream getOutputStream() throws IOException {
      return this.dataHandler.getOutputStream();
   }

   @Override
   public String getContentType() {
      return this.dataHandler.getContentType();
   }

   @Override
   public String getName() {
      return this.dataHandler.getName();
   }
}
