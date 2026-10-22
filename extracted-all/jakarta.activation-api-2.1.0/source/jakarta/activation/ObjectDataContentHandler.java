package jakarta.activation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

class ObjectDataContentHandler implements DataContentHandler {
   private ActivationDataFlavor[] transferFlavors = null;
   private Object obj;
   private String mimeType;
   private DataContentHandler dch = null;

   public ObjectDataContentHandler(DataContentHandler dch, Object obj, String mimeType) {
      this.obj = obj;
      this.mimeType = mimeType;
      this.dch = dch;
   }

   public DataContentHandler getDCH() {
      return this.dch;
   }

   @Override
   public synchronized ActivationDataFlavor[] getTransferDataFlavors() {
      if (this.transferFlavors == null) {
         if (this.dch != null) {
            this.transferFlavors = this.dch.getTransferDataFlavors();
         } else {
            this.transferFlavors = new ActivationDataFlavor[1];
            this.transferFlavors[0] = new ActivationDataFlavor(this.obj.getClass(), this.mimeType, this.mimeType);
         }
      }

      return this.transferFlavors;
   }

   @Override
   public Object getTransferData(ActivationDataFlavor df, DataSource ds) throws IOException {
      if (this.dch != null) {
         return this.dch.getTransferData(df, ds);
      } else if (df.equals(this.getTransferDataFlavors()[0])) {
         return this.obj;
      } else {
         throw new IOException("Unsupported DataFlavor: " + df);
      }
   }

   @Override
   public Object getContent(DataSource ds) {
      return this.obj;
   }

   @Override
   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
      if (this.dch != null) {
         this.dch.writeTo(obj, mimeType, os);
      } else if (obj instanceof byte[]) {
         os.write((byte[])obj);
      } else {
         if (!(obj instanceof String)) {
            throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.mimeType);
         }

         OutputStreamWriter osw = new OutputStreamWriter(os, Charset.defaultCharset());
         osw.write((String)obj);
         osw.flush();
      }
   }
}
