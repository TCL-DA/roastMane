package org.eclipse.jetty.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Scanner.DiscreteListener;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.Resources;
import org.eclipse.jetty.util.security.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUserStore extends UserStore implements DiscreteListener {
   private static final Logger LOG = LoggerFactory.getLogger(PropertyUserStore.class);
   protected Resource _configResource;
   protected Scanner _scanner;
   protected int _reloadInterval = 0;
   protected boolean _firstLoad = true;
   protected List<PropertyUserStore.UserListener> _listeners;

   public Resource getConfig() {
      return this._configResource;
   }

   public void setConfig(Resource config) {
      this._configResource = config;
   }

   @Deprecated(
      forRemoval = true
   )
   public Resource getConfigResource() {
      return this.getConfig();
   }

   @Deprecated
   public boolean isHotReload() {
      return this.getReloadInterval() > 0;
   }

   @Deprecated
   public void setHotReload(boolean enable) {
      this.setReloadInterval(enable ? 1 : 0);
   }

   public void setReloadInterval(int scanSeconds) {
      if (this.isRunning()) {
         throw new IllegalStateException("Cannot set scan period while user store is running");
      } else {
         this._reloadInterval = scanSeconds;
      }
   }

   public int getReloadInterval() {
      return this._reloadInterval;
   }

   @Override
   public String toString() {
      return String.format("%s[cfg=%s]", super.toString(), this._configResource);
   }

   protected void loadUsers() throws IOException {
      Resource config = this.getConfig();
      if (config == null) {
         throw new IllegalStateException("No config path set");
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Loading {} from {}", this, config);
         }

         if (Resources.missing(config)) {
            throw new IllegalStateException("Config does not exist: " + config);
         } else {
            Properties properties = new Properties();

            try (InputStream inputStream = config.newInputStream()) {
               if (inputStream == null) {
                  throw new IllegalStateException("Config does have properties: " + config);
               }

               properties.load(inputStream);
            }

            Set<String> known = new HashSet<>();

            for (Entry<Object, Object> entry : properties.entrySet()) {
               String username = ((String)entry.getKey()).trim();
               String credentials = ((String)entry.getValue()).trim();
               String roles = null;
               int c = credentials.indexOf(44);
               if (c >= 0) {
                  roles = credentials.substring(c + 1).trim();
                  credentials = credentials.substring(0, c).trim();
               }

               if (username.length() > 0) {
                  String[] roleArray = new String[0];
                  if (roles != null && roles.length() > 0) {
                     roleArray = StringUtil.csvSplit(roles);
                  }

                  known.add(username);
                  Credential credential = Credential.getCredential(credentials);
                  this.addUser(username, credential, roleArray);
                  this.notifyUpdate(username, credential, roleArray);
               }
            }

            List<String> currentlyKnownUsers = new ArrayList<>(this._users.keySet());
            if (!this._firstLoad) {
               for (String user : currentlyKnownUsers) {
                  if (!known.contains(user)) {
                     this.removeUser(user);
                     this.notifyRemove(user);
                  }
               }
            }

            this._firstLoad = false;
            if (LOG.isDebugEnabled()) {
               LOG.debug("Loaded {} from {}", this, config);
            }
         }
      }
   }

   protected void doStart() throws Exception {
      Resource config = this.getConfig();
      if (this.getReloadInterval() > 0 && config != null) {
         this._scanner = new Scanner(null, false);
         this._scanner.addFile(config.getPath());
         this._scanner.setScanInterval(this._reloadInterval);
         this._scanner.setReportExistingFilesOnStartup(false);
         this._scanner.addListener(this);
         this.addBean(this._scanner);
      }

      this.loadUsers();
      super.doStart();
   }

   public void pathChanged(Path path) throws Exception {
      this.loadUsers();
   }

   public void pathAdded(Path path) throws Exception {
      this.loadUsers();
   }

   public void pathRemoved(Path path) throws Exception {
      this.loadUsers();
   }

   protected void doStop() throws Exception {
      super.doStop();
      this.removeBean(this._scanner);
      this._scanner = null;
   }

   private void notifyUpdate(String username, Credential credential, String[] roleArray) {
      if (this._listeners != null) {
         for (PropertyUserStore.UserListener listener : this._listeners) {
            listener.update(username, credential, roleArray);
         }
      }
   }

   private void notifyRemove(String username) {
      if (this._listeners != null) {
         for (PropertyUserStore.UserListener listener : this._listeners) {
            listener.remove(username);
         }
      }
   }

   public void registerUserListener(PropertyUserStore.UserListener listener) {
      if (this._listeners == null) {
         this._listeners = new ArrayList<>();
      }

      this._listeners.add(listener);
   }

   public interface UserListener {
      void update(String var1, Credential var2, String[] var3);

      void remove(String var1);
   }
}
