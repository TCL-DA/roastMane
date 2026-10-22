package org.glassfish.jersey.media.multipart;

import jakarta.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public class MultiPartProperties {
   public static final int DEFAULT_BUFFER_THRESHOLD = 4096;
   public static final String MULTI_PART_CONFIG_RESOURCE = "jersey-multipart-config.properties";
   public static final String BUFFER_THRESHOLD = "jersey.config.multipart.bufferThreshold";
   public static final int BUFFER_THRESHOLD_MEMORY_ONLY = -1;
   public static final String TEMP_DIRECTORY = "jersey.config.multipart.tempDir";
   private int bufferThreshold = 4096;
   private String tempDir = null;

   public MultiPartProperties() {
      this.configure();
   }

   public int getBufferThreshold() {
      return this.bufferThreshold;
   }

   public String getTempDir() {
      return this.tempDir;
   }

   public MultiPartProperties bufferThreshold(int threshold) {
      this.bufferThreshold = threshold < -1 ? -1 : threshold;
      return this;
   }

   public MultiPartProperties tempDir(String path) {
      this.tempDir = path;
      return this;
   }

   private void configure() {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      if (loader == null) {
         loader = this.getClass().getClassLoader();
      }

      InputStream stream = null;

      try {
         stream = loader.getResourceAsStream("jersey-multipart-config.properties");
         if (stream != null) {
            Properties props = new Properties();
            props.load(stream);
            if (props.containsKey("jersey.config.multipart.bufferThreshold")) {
               this.bufferThreshold = Integer.parseInt(props.getProperty("jersey.config.multipart.bufferThreshold"));
            }

            if (props.containsKey("jersey.config.multipart.tempDir")) {
               this.tempDir = props.getProperty("jersey.config.multipart.tempDir");
            }

            return;
         }
      } catch (IOException var12) {
         throw new IllegalArgumentException(var12);
      } finally {
         if (stream != null) {
            try {
               stream.close();
            } catch (IOException var11) {
            }
         }
      }
   }

   public ContextResolver<MultiPartProperties> resolver() {
      return new ContextResolver<MultiPartProperties>() {
         public MultiPartProperties getContext(Class<?> type) {
            return MultiPartProperties.this;
         }
      };
   }
}
