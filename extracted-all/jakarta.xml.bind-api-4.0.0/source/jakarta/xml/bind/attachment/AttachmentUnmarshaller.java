package jakarta.xml.bind.attachment;

import jakarta.activation.DataHandler;

public abstract class AttachmentUnmarshaller {
   protected AttachmentUnmarshaller() {
   }

   public abstract DataHandler getAttachmentAsDataHandler(String var1);

   public abstract byte[] getAttachmentAsByteArray(String var1);

   public boolean isXOPPackage() {
      return false;
   }
}
