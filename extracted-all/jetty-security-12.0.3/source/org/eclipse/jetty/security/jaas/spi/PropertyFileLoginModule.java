package org.eclipse.jetty.security.jaas.spi;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.jetty.security.PropertyUserStore;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.security.jaas.JAASLoginService;
import org.eclipse.jetty.security.jaas.PropertyUserStoreManager;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyFileLoginModule extends AbstractLoginModule {
   public static final String DEFAULT_FILENAME = "realm.properties";
   private static final Logger LOG = LoggerFactory.getLogger(PropertyFileLoginModule.class);
   private PropertyUserStore _store;

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      super.initialize(subject, callbackHandler, sharedState, options);
      this.setupPropertyUserStore(options);
   }

   private void setupPropertyUserStore(Map<String, ?> options) {
      String filename = (String)options.get("file");
      filename = filename == null ? "realm.properties" : filename;
      PropertyUserStoreManager mgr = (PropertyUserStoreManager)JAASLoginService.INSTANCE.get().getBean(PropertyUserStoreManager.class);
      if (mgr == null) {
         throw new IllegalStateException("No PropertyUserStoreManager");
      } else {
         this._store = mgr.getPropertyUserStore(filename);
         if (this._store == null) {
            int reloadInterval = 0;
            String tmp = (String)options.get("reloadInterval");
            if (tmp != null) {
               try {
                  reloadInterval = Integer.parseInt(tmp);
               } catch (NumberFormatException var11) {
                  LOG.warn("'reloadInterval' is not an integer");
               }
            } else {
               tmp = (String)options.get("hotReload");
               if (tmp != null) {
                  LOG.warn("Use 'reloadInterval' boolean property instead of 'hotReload'");
                  reloadInterval = Boolean.parseBoolean(tmp) ? 1 : 0;
               }
            }

            PropertyUserStore newStore = new PropertyUserStore();
            ResourceFactory resourceFactory = ResourceFactory.of(newStore);
            Resource config = resourceFactory.newResource(filename);
            newStore.setConfig(config);
            newStore.setReloadInterval(reloadInterval);
            this._store = mgr.addPropertyUserStore(filename, newStore);

            try {
               this._store.start();
            } catch (Exception var10) {
               LOG.warn("Exception starting propertyUserStore {} ", config, var10);
            }
         }
      }
   }

   @Override
   public AbstractLoginModule.JAASUser getUser(String userName) throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Checking PropertyUserStore {} for {}", this._store.getConfig(), userName);
      }

      UserPrincipal up = this._store.getUserPrincipal(userName);
      if (up == null) {
         return null;
      } else {
         List<RolePrincipal> rps = this._store.getRolePrincipals(userName);
         final List<String> roles = rps == null ? Collections.emptyList() : rps.stream().map(RolePrincipal::getName).collect(Collectors.toList());
         return new AbstractLoginModule.JAASUser(up) {
            @Override
            public List<String> doFetchRoles() {
               return roles;
            }
         };
      }
   }
}
