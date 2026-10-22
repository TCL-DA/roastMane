package org.glassfish.jersey.message.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicReference;

public final class Utils {
   static void throwIllegalArgumentExceptionIfNull(Object toCheck, String errorMessage) {
      if (toCheck == null) {
         throw new IllegalArgumentException(errorMessage);
      }
   }

   public static File createTempFile() throws IOException {
      final AtomicReference<IOException> exceptionReference = new AtomicReference<>();
      File file = AccessController.doPrivileged(new PrivilegedAction<File>() {
         public File run() {
            File tempFile = null;

            try {
               tempFile = Files.createTempFile("rep", "tmp").toFile();
               tempFile.deleteOnExit();
            } catch (IOException var3) {
               exceptionReference.set(var3);
            }

            return tempFile;
         }
      });
      if (exceptionReference.get() != null) {
         throw (IOException)exceptionReference.get();
      } else {
         return file;
      }
   }

   private Utils() {
      throw new AssertionError("No instances allowed.");
   }
}
