package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.status.StatusLogger;

public class PropertyFilePropertySource extends PropertiesPropertySource {
   private static final Logger LOGGER = StatusLogger.getLogger();

   public PropertyFilePropertySource(final String fileName) {
      this(fileName, true);
   }

   public PropertyFilePropertySource(final String fileName, final boolean useTccl) {
      super(loadPropertiesFile(fileName, useTccl));
   }

   @SuppressFBWarnings(
      value = {"URLCONNECTION_SSRF_FD"},
      justification = "This property source should only be used with hardcoded file names."
   )
   private static Properties loadPropertiesFile(final String fileName, final boolean useTccl) {
      Properties props = new Properties();

      for (URL url : LoaderUtil.findResources(fileName, useTccl)) {
         try {
            InputStream in = url.openStream();

            try {
               props.load(in);
            } catch (Throwable var9) {
               if (in != null) {
                  try {
                     in.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (in != null) {
               in.close();
            }
         } catch (IOException var10) {
            LOGGER.error("Unable to read URL `{}`", url, var10);
         }
      }

      return props;
   }
}
