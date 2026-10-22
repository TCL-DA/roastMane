package jakarta.activation;

import jakarta.activation.spi.MimeTypeRegistryProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.Vector;

public class MimetypesFileTypeMap extends FileTypeMap {
   private MimeTypeRegistry[] DB;
   private static final int PROG = 0;
   private static final String defaultType = "application/octet-stream";
   private static final String confDir;

   public MimetypesFileTypeMap() {
      Vector<MimeTypeRegistry> dbv = new Vector<>(5);
      MimeTypeRegistry mf = null;
      dbv.addElement(null);
      LogSupport.log("MimetypesFileTypeMap: load HOME");

      try {
         String user_home = System.getProperty("user.home");
         if (user_home != null) {
            String path = user_home + File.separator + ".mime.types";
            mf = this.loadFile(path);
            if (mf != null) {
               dbv.addElement(mf);
            }
         }
      } catch (SecurityException var6) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Exception during MimetypesFileTypeMap class instantiation", var6);
         }
      }

      LogSupport.log("MimetypesFileTypeMap: load SYS");

      try {
         if (confDir != null) {
            mf = this.loadFile(confDir + "mime.types");
            if (mf != null) {
               dbv.addElement(mf);
            }
         }
      } catch (SecurityException var5) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Exception during MimetypesFileTypeMap class instantiation", var5);
         }
      }

      LogSupport.log("MimetypesFileTypeMap: load JAR");
      this.loadAllResources(dbv, "META-INF/mime.types");
      LogSupport.log("MimetypesFileTypeMap: load DEF");
      mf = this.loadResource("/META-INF/mimetypes.default");
      if (mf != null) {
         dbv.addElement(mf);
      }

      this.DB = new MimeTypeRegistry[dbv.size()];
      dbv.copyInto(this.DB);
   }

   private MimeTypeRegistry loadResource(String name) {
      InputStream clis = null;

      MimeTypeRegistry var4;
      try {
         clis = SecuritySupport.getResourceAsStream(this.getClass(), name);
         if (clis == null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("MimetypesFileTypeMap: not loading mime types file: " + name);
            }

            return null;
         }

         MimeTypeRegistry mf = this.getImplementation().getByInputStream(clis);
         if (LogSupport.isLoggable()) {
            LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types file: " + name);
         }

         var4 = mf;
      } catch (IOException var19) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MimetypesFileTypeMap: can't load " + name, var19);
         }

         return null;
      } catch (SecurityException var20) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MimetypesFileTypeMap: can't load " + name, var20);
         }

         return null;
      } catch (ServiceConfigurationError | NoSuchElementException var21) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider.MimeTypeRegistry: can't load " + name, var21);
         }

         return null;
      } finally {
         try {
            if (clis != null) {
               clis.close();
            }
         } catch (IOException var18) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("InputStream cannot be close for " + name, var18);
            }
         }
      }

      return var4;
   }

   private void loadAllResources(Vector<MimeTypeRegistry> v, String name) {
      boolean anyLoaded = false;

      try {
         ClassLoader cld = null;
         cld = SecuritySupport.getContextClassLoader();
         if (cld == null) {
            cld = this.getClass().getClassLoader();
         }

         URL[] urls;
         if (cld != null) {
            urls = SecuritySupport.getResources(cld, name);
         } else {
            urls = SecuritySupport.getSystemResources(name);
         }

         if (urls != null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("MimetypesFileTypeMap: getResources");
            }

            for (int i = 0; i < urls.length; i++) {
               URL url = urls[i];
               InputStream clis = null;
               if (LogSupport.isLoggable()) {
                  LogSupport.log("MimetypesFileTypeMap: URL " + url);
               }

               try {
                  clis = SecuritySupport.openStream(url);
                  if (clis != null) {
                     v.addElement(this.getImplementation().getByInputStream(clis));
                     anyLoaded = true;
                     if (LogSupport.isLoggable()) {
                        LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types from URL: " + url);
                     }
                  } else if (LogSupport.isLoggable()) {
                     LogSupport.log("MimetypesFileTypeMap: not loading mime types from URL: " + url);
                  }
               } catch (IOException var23) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MimetypesFileTypeMap: can't load " + url, var23);
                  }
               } catch (SecurityException var24) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MimetypesFileTypeMap: can't load " + url, var24);
                  }
               } catch (ServiceConfigurationError | NoSuchElementException var25) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider.MimeTypeRegistry: can't load " + url, var25);
                  }
               } finally {
                  try {
                     if (clis != null) {
                        clis.close();
                     }
                  } catch (IOException var22) {
                     if (LogSupport.isLoggable()) {
                        LogSupport.log("InputStream cannot be close for " + name, var22);
                     }
                  }
               }
            }
         }
      } catch (Exception var27) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MimetypesFileTypeMap: can't load " + name, var27);
         }
      }

      if (!anyLoaded) {
         LogSupport.log("MimetypesFileTypeMap: !anyLoaded");
         MimeTypeRegistry mf = this.loadResource("/" + name);
         if (mf != null) {
            v.addElement(mf);
         }
      }
   }

   private MimeTypeRegistry loadFile(String name) {
      MimeTypeRegistry mtf = null;

      try {
         mtf = this.getImplementation().getByFileName(name);
      } catch (IOException var4) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MimeTypeRegistry: can't load from file - " + name, var4);
         }
      } catch (ServiceConfigurationError | NoSuchElementException var5) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider.MimeTypeRegistry: can't load " + name, var5);
         }
      }

      return mtf;
   }

   public MimetypesFileTypeMap(String mimeTypeFileName) throws IOException {
      this();

      try {
         this.DB[0] = this.getImplementation().getByFileName(mimeTypeFileName);
      } catch (ServiceConfigurationError | NoSuchElementException var4) {
         String errorMessage = "Cannot find or load an implementation for MimeTypeRegistryProvider.MimeTypeRegistry: can't load " + mimeTypeFileName;
         if (LogSupport.isLoggable()) {
            LogSupport.log(errorMessage, var4);
         }

         throw new IOException(errorMessage, var4);
      }
   }

   public MimetypesFileTypeMap(InputStream is) {
      this();

      try {
         this.DB[0] = this.getImplementation().getByInputStream(is);
      } catch (IOException var3) {
      } catch (ServiceConfigurationError | NoSuchElementException var4) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider.MimeTypeRegistry: can't load InputStream", var4);
         }
      }
   }

   public synchronized void addMimeTypes(String mime_types) {
      try {
         if (this.DB[0] == null) {
            this.DB[0] = this.getImplementation().getInMemory();
         }

         this.DB[0].appendToRegistry(mime_types);
      } catch (ServiceConfigurationError | NoSuchElementException var3) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Cannot find or load an implementation for MimeTypeRegistryProvider.MimeTypeRegistry: can't add " + mime_types, var3);
         }

         throw var3;
      }
   }

   @Override
   public String getContentType(File f) {
      return this.getContentType(f.getName());
   }

   @Override
   public synchronized String getContentType(String filename) {
      int dot_pos = filename.lastIndexOf(".");
      if (dot_pos < 0) {
         return "application/octet-stream";
      } else {
         String file_ext = filename.substring(dot_pos + 1);
         if (file_ext.length() == 0) {
            return "application/octet-stream";
         } else {
            for (int i = 0; i < this.DB.length; i++) {
               if (this.DB[i] != null) {
                  String result = this.DB[i].getMIMETypeString(file_ext);
                  if (result != null) {
                     return result;
                  }
               }
            }

            return "application/octet-stream";
         }
      }
   }

   private MimeTypeRegistryProvider getImplementation() {
      return FactoryFinder.find(MimeTypeRegistryProvider.class, "com.sun.activation.registries.MimeTypeRegistryProviderImpl", false);
   }

   static {
      String dir = null;

      try {
         dir = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               String home = System.getProperty("java.home");
               String newdir = home + File.separator + "conf";
               File conf = new File(newdir);
               return conf.exists() ? newdir + File.separator : home + File.separator + "lib" + File.separator;
            }
         });
      } catch (Exception var2) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Exception during MimetypesFileTypeMap class loading", var2);
         }
      }

      confDir = dir;
   }
}
