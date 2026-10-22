package jakarta.activation;

import jakarta.activation.spi.MailcapRegistryProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;

public class MailcapCommandMap extends CommandMap {
   private MailcapRegistry[] DB;
   private static final int PROG = 0;
   private static final String confDir;

   public MailcapCommandMap() {
      List<MailcapRegistry> dbv = new ArrayList<>(5);
      MailcapRegistry mf = null;
      dbv.add(null);
      LogSupport.log("MailcapCommandMap: load HOME");

      try {
         String user_home = System.getProperty("user.home");
         if (user_home != null) {
            String path = user_home + File.separator + ".mailcap";
            mf = this.loadFile(path);
            if (mf != null) {
               dbv.add(mf);
            }
         }
      } catch (SecurityException var6) {
      }

      LogSupport.log("MailcapCommandMap: load SYS");

      try {
         if (confDir != null) {
            mf = this.loadFile(confDir + "mailcap");
            if (mf != null) {
               dbv.add(mf);
            }
         }
      } catch (SecurityException var5) {
      }

      LogSupport.log("MailcapCommandMap: load JAR");
      this.loadAllResources(dbv, "META-INF/mailcap");
      LogSupport.log("MailcapCommandMap: load DEF");
      mf = this.loadResource("/META-INF/mailcap.default");
      if (mf != null) {
         dbv.add(mf);
      }

      this.DB = new MailcapRegistry[dbv.size()];
      this.DB = dbv.toArray(this.DB);
   }

   private MailcapRegistry loadResource(String name) {
      try {
         InputStream clis = SecuritySupport.getResourceAsStream(this.getClass(), name);

         MailcapRegistry var4;
         label71: {
            try {
               if (clis != null) {
                  MailcapRegistry mf = this.getImplementation().getByInputStream(clis);
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
                  }

                  var4 = mf;
                  break label71;
               }

               if (LogSupport.isLoggable()) {
                  LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
               }
            } catch (Throwable var6) {
               if (clis != null) {
                  try {
                     clis.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (clis != null) {
               clis.close();
            }

            return null;
         }

         if (clis != null) {
            clis.close();
         }

         return var4;
      } catch (SecurityException | IOException var7) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var7);
         }
      } catch (ServiceConfigurationError | NoSuchElementException var8) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. MailcapRegistry: can't load " + name, var8);
         }
      }

      return null;
   }

   private void loadAllResources(List<MailcapRegistry> v, String name) {
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
               LogSupport.log("MailcapCommandMap: getResources");
            }

            for (int i = 0; i < urls.length; i++) {
               URL url = urls[i];
               if (LogSupport.isLoggable()) {
                  LogSupport.log("MailcapCommandMap: URL " + url);
               }

               try {
                  InputStream clis = SecuritySupport.openStream(url);

                  try {
                     if (clis != null) {
                        v.add(this.getImplementation().getByInputStream(clis));
                        anyLoaded = true;
                        if (LogSupport.isLoggable()) {
                           LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                        }
                     } else if (LogSupport.isLoggable()) {
                        LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + url);
                     }
                  } catch (Throwable var12) {
                     if (clis != null) {
                        try {
                           clis.close();
                        } catch (Throwable var11) {
                           var12.addSuppressed(var11);
                        }
                     }

                     throw var12;
                  }

                  if (clis != null) {
                     clis.close();
                  }
               } catch (SecurityException | IOException var13) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: can't load " + url, var13);
                  }
               } catch (ServiceConfigurationError | NoSuchElementException var14) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. MailcapRegistry: can't load " + name, var14);
                  }
               }
            }
         }
      } catch (Exception var15) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var15);
         }
      }

      if (!anyLoaded) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: !anyLoaded");
         }

         MailcapRegistry mf = this.loadResource("/" + name);
         if (mf != null) {
            v.add(mf);
         }
      }
   }

   private MailcapRegistry loadFile(String name) {
      MailcapRegistry mtf = null;

      try {
         mtf = this.getImplementation().getByFileName(name);
      } catch (IOException var4) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapRegistry: can't load from file - " + name, var4);
         }
      } catch (ServiceConfigurationError | NoSuchElementException var5) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. MailcapRegistry: can't load " + name, var5);
         }
      }

      return mtf;
   }

   public MailcapCommandMap(String fileName) throws IOException {
      this();
      if (this.DB[0] == null) {
         try {
            this.DB[0] = this.getImplementation().getByFileName(fileName);
         } catch (ServiceConfigurationError | NoSuchElementException var4) {
            String message = "Cannot find or load an implementation for MailcapRegistryProvider. MailcapRegistry: can't load " + fileName;
            if (LogSupport.isLoggable()) {
               LogSupport.log(message, var4);
            }

            throw new IOException(message, var4);
         }
      }

      if (this.DB[0] != null && LogSupport.isLoggable()) {
         LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
      }
   }

   public MailcapCommandMap(InputStream is) {
      this();
      if (this.DB[0] == null) {
         try {
            this.DB[0] = this.getImplementation().getByInputStream(is);
         } catch (IOException var3) {
         } catch (ServiceConfigurationError | NoSuchElementException var4) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider.MailcapRegistry: can't load InputStream", var4);
            }
         }
      }

      if (this.DB[0] != null && LogSupport.isLoggable()) {
         LogSupport.log("MailcapCommandMap: load PROG");
      }
   }

   @Override
   public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
      List<CommandInfo> cmdList = new ArrayList<>();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase(Locale.ENGLISH);
      }

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            Map<String, List<String>> cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               this.appendPrefCmdsToList(cmdMap, cmdList);
            }
         }
      }

      for (int ix = 0; ix < this.DB.length; ix++) {
         if (this.DB[ix] != null) {
            Map<String, List<String>> cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               this.appendPrefCmdsToList(cmdMap, cmdList);
            }
         }
      }

      CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
      return cmdList.toArray(cmdInfos);
   }

   private void appendPrefCmdsToList(Map<String, List<String>> cmdHash, List<CommandInfo> cmdList) {
      for (String verb : cmdHash.keySet()) {
         if (!this.checkForVerb(cmdList, verb)) {
            List<String> cmdList2 = cmdHash.get(verb);
            String className = cmdList2.get(0);
            cmdList.add(new CommandInfo(verb, className));
         }
      }
   }

   private boolean checkForVerb(List<CommandInfo> cmdList, String verb) {
      Iterator<CommandInfo> ee = cmdList.iterator();

      while (ee.hasNext()) {
         String enum_verb = ee.next().getCommandName();
         if (enum_verb.equals(verb)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public synchronized CommandInfo[] getAllCommands(String mimeType) {
      List<CommandInfo> cmdList = new ArrayList<>();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase(Locale.ENGLISH);
      }

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            Map<String, List<String>> cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               this.appendCmdsToList(cmdMap, cmdList);
            }
         }
      }

      for (int ix = 0; ix < this.DB.length; ix++) {
         if (this.DB[ix] != null) {
            Map<String, List<String>> cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               this.appendCmdsToList(cmdMap, cmdList);
            }
         }
      }

      CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
      return cmdList.toArray(cmdInfos);
   }

   private void appendCmdsToList(Map<String, List<String>> typeHash, List<CommandInfo> cmdList) {
      for (String verb : typeHash.keySet()) {
         for (String cmd : typeHash.get(verb)) {
            cmdList.add(new CommandInfo(verb, cmd));
         }
      }
   }

   @Override
   public synchronized CommandInfo getCommand(String mimeType, String cmdName) {
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase(Locale.ENGLISH);
      }

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            Map<String, List<String>> cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               List<String> v = cmdMap.get(cmdName);
               if (v != null) {
                  String cmdClassName = v.get(0);
                  if (cmdClassName != null) {
                     return new CommandInfo(cmdName, cmdClassName);
                  }
               }
            }
         }
      }

      for (int ix = 0; ix < this.DB.length; ix++) {
         if (this.DB[ix] != null) {
            Map<String, List<String>> cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               List<String> v = cmdMap.get(cmdName);
               if (v != null) {
                  String cmdClassName = v.get(0);
                  if (cmdClassName != null) {
                     return new CommandInfo(cmdName, cmdClassName);
                  }
               }
            }
         }
      }

      return null;
   }

   public synchronized void addMailcap(String mail_cap) {
      LogSupport.log("MailcapCommandMap: add to PROG");

      try {
         if (this.DB[0] == null) {
            this.DB[0] = this.getImplementation().getInMemory();
         }

         this.DB[0].appendToMailcap(mail_cap);
      } catch (ServiceConfigurationError | NoSuchElementException var3) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Cannot find or load an implementation for MailcapRegistryProvider. MailcapRegistry: can't load", var3);
         }

         throw var3;
      }
   }

   @Override
   public synchronized DataContentHandler createDataContentHandler(String mimeType) {
      if (LogSupport.isLoggable()) {
         LogSupport.log("MailcapCommandMap: createDataContentHandler for " + mimeType);
      }

      if (mimeType != null) {
         mimeType = mimeType.toLowerCase(Locale.ENGLISH);
      }

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("  search DB #" + i);
            }

            Map<String, List<String>> cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               List<String> v = cmdMap.get("content-handler");
               if (v != null) {
                  String name = v.get(0);
                  DataContentHandler dch = this.getDataContentHandler(name);
                  if (dch != null) {
                     return dch;
                  }
               }
            }
         }
      }

      for (int ix = 0; ix < this.DB.length; ix++) {
         if (this.DB[ix] != null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("  search fallback DB #" + ix);
            }

            Map<String, List<String>> cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               List<String> v = cmdMap.get("content-handler");
               if (v != null) {
                  String name = v.get(0);
                  DataContentHandler dch = this.getDataContentHandler(name);
                  if (dch != null) {
                     return dch;
                  }
               }
            }
         }
      }

      return null;
   }

   private DataContentHandler getDataContentHandler(String name) {
      if (LogSupport.isLoggable()) {
         LogSupport.log("    got content-handler");
      }

      if (LogSupport.isLoggable()) {
         LogSupport.log("      class " + name);
      }

      try {
         ClassLoader cld = null;
         cld = SecuritySupport.getContextClassLoader();
         if (cld == null) {
            cld = this.getClass().getClassLoader();
         }

         Class<?> cl = null;

         try {
            cl = cld.loadClass(name);
         } catch (Exception var5) {
            cl = Class.forName(name);
         }

         if (cl != null) {
            return (DataContentHandler)cl.getDeclaredConstructor().newInstance();
         }
      } catch (ReflectiveOperationException var6) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Can't load DCH " + name, var6);
         }
      }

      return null;
   }

   @Override
   public synchronized String[] getMimeTypes() {
      List<String> mtList = new ArrayList<>();

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            String[] ts = this.DB[i].getMimeTypes();
            if (ts != null) {
               for (int j = 0; j < ts.length; j++) {
                  if (!mtList.contains(ts[j])) {
                     mtList.add(ts[j]);
                  }
               }
            }
         }
      }

      String[] mts = new String[mtList.size()];
      return mtList.toArray(mts);
   }

   public synchronized String[] getNativeCommands(String mimeType) {
      List<String> cmdList = new ArrayList<>();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase(Locale.ENGLISH);
      }

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            String[] cmds = this.DB[i].getNativeCommands(mimeType);
            if (cmds != null) {
               for (int j = 0; j < cmds.length; j++) {
                  if (!cmdList.contains(cmds[j])) {
                     cmdList.add(cmds[j]);
                  }
               }
            }
         }
      }

      String[] cmds = new String[cmdList.size()];
      return cmdList.toArray(cmds);
   }

   private MailcapRegistryProvider getImplementation() {
      return FactoryFinder.find(MailcapRegistryProvider.class, "com.sun.activation.registries.MailcapRegistryProviderImpl", false);
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
      }

      confDir = dir;
   }
}
