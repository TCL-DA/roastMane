package org.glassfish.jersey.media.multipart;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.media.multipart.internal.LocalizationMessages;
import org.jvnet.mimepull.MIMEPart;

public class BodyPartEntity implements Closeable {
   private static final Logger LOGGER = Logger.getLogger(BodyPartEntity.class.getName());
   private final MIMEPart mimePart;
   private volatile File file;

   public BodyPartEntity(MIMEPart mimePart) {
      this.mimePart = mimePart;
   }

   public InputStream getInputStream() {
      return this.mimePart.read();
   }

   public void cleanup() {
      this.mimePart.close();
      if (this.file != null) {
         boolean deleted = this.file.delete();
         if (!deleted && LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, LocalizationMessages.TEMP_FILE_NOT_DELETED(this.file.getAbsolutePath()));
         }
      }
   }

   @Override
   public void close() throws IOException {
      this.cleanup();
   }

   public void moveTo(File file) {
      this.mimePart.moveTo(file);
      this.file = file;
   }
}
