package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Deprecated
@Plugin(
   name = "SerializedLayout",
   category = "Core",
   elementType = "layout",
   printObject = true
)
public final class SerializedLayout extends AbstractLayout<LogEvent> {
   private static byte[] serializedHeader;

   private SerializedLayout() {
      super(null, null, null);
      LOGGER.warn(
         "SerializedLayout is deprecated due to the inherent security weakness in Java Serialization, see https://www.owasp.org/index.php/Deserialization_of_untrusted_data Consider using another layout, e.g. JsonLayout"
      );
   }

   @Override
   public byte[] toByteArray(final LogEvent event) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         ObjectOutputStream oos = new SerializedLayout.PrivateObjectOutputStream(baos);

         try {
            oos.writeObject(event);
            oos.reset();
         } catch (Throwable var7) {
            try {
               oos.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         oos.close();
      } catch (IOException var8) {
         LOGGER.error("Serialization of LogEvent failed.", var8);
      }

      return baos.toByteArray();
   }

   public LogEvent toSerializable(final LogEvent event) {
      return event;
   }

   @Deprecated
   @PluginFactory
   public static SerializedLayout createLayout() {
      return new SerializedLayout();
   }

   @Override
   public byte[] getHeader() {
      return serializedHeader;
   }

   @Override
   public String getContentType() {
      return "application/octet-stream";
   }

   static {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         new ObjectOutputStream(baos).close();
         serializedHeader = baos.toByteArray();
      } catch (Exception var2) {
         LOGGER.error("Unable to generate Object stream header", var2);
      }
   }

   private class PrivateObjectOutputStream extends ObjectOutputStream {
      public PrivateObjectOutputStream(final OutputStream os) throws IOException {
         super(os);
      }

      @Override
      protected void writeStreamHeader() {
      }
   }
}
