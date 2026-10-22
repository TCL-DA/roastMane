package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapFile;
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

public class MailcapCommandMap extends CommandMap {
   private MailcapFile[] DB;
   private static final int PROG = 0;
   private static final String confDir;

   public MailcapCommandMap() {
      List dbv = new ArrayList(5);
      MailcapFile mf = null;
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

      this.DB = new MailcapFile[dbv.size()];
      this.DB = dbv.toArray(this.DB);
   }

   private MailcapFile loadResource(String name) {
      InputStream clis = null;

      MailcapFile var4;
      try {
         clis = SecuritySupport.getResourceAsStream(this.getClass(), name);
         if (clis == null) {
            if (LogSupport.isLoggable()) {
               LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
            }

            return null;
         }

         MailcapFile mf = new MailcapFile(clis);
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
         }

         var4 = mf;
      } catch (IOException var17) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var17);
         }

         return null;
      } catch (SecurityException var18) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var18);
         }

         return null;
      } finally {
         try {
            if (clis != null) {
               clis.close();
            }
         } catch (IOException var16) {
         }
      }

      return var4;
   }

   private void loadAllResources(List v, String name) {
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
               InputStream clis = null;
               if (LogSupport.isLoggable()) {
                  LogSupport.log("MailcapCommandMap: URL " + url);
               }

               try {
                  clis = SecuritySupport.openStream(url);
                  if (clis != null) {
                     v.add(new MailcapFile(clis));
                     anyLoaded = true;
                     if (LogSupport.isLoggable()) {
                        LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                     }
                  } else if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + url);
                  }
               } catch (IOException var21) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: can't load " + url, var21);
                  }
               } catch (SecurityException var22) {
                  if (LogSupport.isLoggable()) {
                     LogSupport.log("MailcapCommandMap: can't load " + url, var22);
                  }
               } finally {
                  try {
                     if (clis != null) {
                        clis.close();
                     }
                  } catch (IOException var20) {
                  }
               }
            }
         }
      } catch (Exception var24) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: can't load " + name, var24);
         }
      }

      if (!anyLoaded) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: !anyLoaded");
         }

         MailcapFile mf = this.loadResource("/" + name);
         if (mf != null) {
            v.add(mf);
         }
      }
   }

   private MailcapFile loadFile(String name) {
      MailcapFile mtf = null;

      try {
         mtf = new MailcapFile(name);
      } catch (IOException var4) {
      }

      return mtf;
   }

   public MailcapCommandMap(String fileName) throws IOException {
      this();
      if (LogSupport.isLoggable()) {
         LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
      }

      if (this.DB[0] == null) {
         this.DB[0] = new MailcapFile(fileName);
      }
   }

   public MailcapCommandMap(InputStream is) {
      this();
      LogSupport.log("MailcapCommandMap: load PROG");
      if (this.DB[0] == null) {
         try {
            this.DB[0] = new MailcapFile(is);
         } catch (IOException var3) {
         }
      }
   }

   @Override
   public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
      List cmdList = new ArrayList();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase(Locale.ENGLISH);
      }

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            Map cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               this.appendPrefCmdsToList(cmdMap, cmdList);
            }
         }
      }

      for (int ix = 0; ix < this.DB.length; ix++) {
         if (this.DB[ix] != null) {
            Map cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               this.appendPrefCmdsToList(cmdMap, cmdList);
            }
         }
      }

      CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
      return cmdList.toArray(cmdInfos);
   }

   private void appendPrefCmdsToList(Map cmdHash, List cmdList) {
      for (String verb : cmdHash.keySet()) {
         if (!this.checkForVerb(cmdList, verb)) {
            List cmdList2 = (List)cmdHash.get(verb);
            String className = (String)cmdList2.get(0);
            cmdList.add(new CommandInfo(verb, className));
         }
      }
   }

   private boolean checkForVerb(List cmdList, String verb) {
      Iterator ee = cmdList.iterator();

      while (ee.hasNext()) {
         String enum_verb = ((CommandInfo)ee.next()).getCommandName();
         if (enum_verb.equals(verb)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public synchronized CommandInfo[] getAllCommands(String mimeType) {
      List cmdList = new ArrayList();
      if (mimeType != null) {
         mimeType = mimeType.toLowerCase(Locale.ENGLISH);
      }

      for (int i = 0; i < this.DB.length; i++) {
         if (this.DB[i] != null) {
            Map cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               this.appendCmdsToList(cmdMap, cmdList);
            }
         }
      }

      for (int ix = 0; ix < this.DB.length; ix++) {
         if (this.DB[ix] != null) {
            Map cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               this.appendCmdsToList(cmdMap, cmdList);
            }
         }
      }

      CommandInfo[] cmdInfos = new CommandInfo[cmdList.size()];
      return cmdList.toArray(cmdInfos);
   }

   private void appendCmdsToList(Map typeHash, List cmdList) {
      for (String verb : typeHash.keySet()) {
         for (String cmd : (List)typeHash.get(verb)) {
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
            Map cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               List v = (List)cmdMap.get(cmdName);
               if (v != null) {
                  String cmdClassName = (String)v.get(0);
                  if (cmdClassName != null) {
                     return new CommandInfo(cmdName, cmdClassName);
                  }
               }
            }
         }
      }

      for (int ix = 0; ix < this.DB.length; ix++) {
         if (this.DB[ix] != null) {
            Map cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               List v = (List)cmdMap.get(cmdName);
               if (v != null) {
                  String cmdClassName = (String)v.get(0);
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
      if (this.DB[0] == null) {
         this.DB[0] = new MailcapFile();
      }

      this.DB[0].appendToMailcap(mail_cap);
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

            Map cmdMap = this.DB[i].getMailcapList(mimeType);
            if (cmdMap != null) {
               List v = (List)cmdMap.get("content-handler");
               if (v != null) {
                  String name = (String)v.get(0);
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

            Map cmdMap = this.DB[ix].getMailcapFallbackList(mimeType);
            if (cmdMap != null) {
               List v = (List)cmdMap.get("content-handler");
               if (v != null) {
                  String name = (String)v.get(0);
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

         Class cl = null;

         try {
            cl = cld.loadClass(name);
         } catch (Exception var5) {
            cl = Class.forName(name);
         }

         if (cl != null) {
            return (DataContentHandler)cl.newInstance();
         }
      } catch (IllegalAccessException var6) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Can't load DCH " + name, var6);
         }
      } catch (ClassNotFoundException var7) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Can't load DCH " + name, var7);
         }
      } catch (InstantiationException var8) {
         if (LogSupport.isLoggable()) {
            LogSupport.log("Can't load DCH " + name, var8);
         }
      }

      return null;
   }

   @Override
   public synchronized String[] getMimeTypes() {
      List mtList = new ArrayList();

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
      List cmdList = new ArrayList();
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

   static {
      String dir = null;

      try {
         dir = AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
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
