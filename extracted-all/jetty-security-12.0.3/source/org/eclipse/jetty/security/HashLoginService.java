package org.eclipse.jetty.security;

import java.util.List;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashLoginService extends AbstractLoginService {
   private static final Logger LOG = LoggerFactory.getLogger(HashLoginService.class);
   private Resource _config;
   private int _reloadInterval;
   private UserStore _userStore;
   private boolean _userStoreAutoCreate = false;

   public HashLoginService() {
   }

   public HashLoginService(String name) {
      this.setName(name);
   }

   public HashLoginService(String name, Resource config) {
      this.setName(name);
      this.setConfig(config);
   }

   public Resource getConfig() {
      return this._config;
   }

   public void setConfig(Resource config) {
      this._config = config;
   }

   @Deprecated
   public boolean isHotReload() {
      return this._reloadInterval > 0;
   }

   @Deprecated
   public void setHotReload(boolean enable) {
      this.setReloadInterval(enable ? 1 : 0);
   }

   public int getReloadInterval() {
      return this._reloadInterval;
   }

   public void setReloadInterval(int reloadIntervalSeconds) {
      if (this.isRunning()) {
         throw new IllegalStateException("Cannot set while user store is running");
      } else {
         this._reloadInterval = reloadIntervalSeconds;
      }
   }

   public void setUserStore(UserStore userStore) {
      this.updateBean(this._userStore, userStore);
      this._userStore = userStore;
   }

   @Override
   protected List<RolePrincipal> loadRoleInfo(UserPrincipal user) {
      return this._userStore.getRolePrincipals(user.getName());
   }

   @Override
   protected UserPrincipal loadUserInfo(String userName) {
      return this._userStore.getUserPrincipal(userName);
   }

   protected void doStart() throws Exception {
      super.doStart();
      if (this._userStore == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("doStart: Starting new PropertyUserStore. PropertiesFile: {} refresh: {}s", this._config, this._reloadInterval);
         }

         PropertyUserStore propertyUserStore = new PropertyUserStore();
         propertyUserStore.setReloadInterval(this._reloadInterval);
         propertyUserStore.setConfig(this._config);
         this.setUserStore(propertyUserStore);
         this._userStoreAutoCreate = true;
      }
   }

   UserStore getUserStore() {
      return this._userStore;
   }

   boolean isUserStoreAutoCreate() {
      return this._userStoreAutoCreate;
   }

   protected void doStop() throws Exception {
      super.doStop();
      if (this._userStoreAutoCreate) {
         this.setUserStore(null);
         this._userStoreAutoCreate = false;
      }
   }
}
