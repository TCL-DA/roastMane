package org.eclipse.jetty.security.jaas.spi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.jetty.util.Loader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCLoginModule extends AbstractDatabaseLoginModule {
   private static final Logger LOG = LoggerFactory.getLogger(JDBCLoginModule.class);
   private String dbDriver;
   private String dbUrl;
   private String dbUserName;
   private String dbPassword;

   @Override
   public Connection getConnection() throws Exception {
      if (this.dbDriver != null && this.dbUrl != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connecting using dbDriver={} dbUserName={}, dbPassword={}", new Object[]{this.dbDriver, this.dbUserName, this.dbUrl});
         }

         return DriverManager.getConnection(this.dbUrl, this.dbUserName, this.dbPassword);
      } else {
         throw new IllegalStateException("Database connection information not configured");
      }
   }

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      try {
         super.initialize(subject, callbackHandler, sharedState, options);
         this.dbDriver = (String)options.get("dbDriver");
         this.dbUrl = (String)options.get("dbUrl");
         this.dbUserName = (String)options.get("dbUserName");
         this.dbPassword = (String)options.get("dbPassword");
         if (this.dbUserName == null) {
            this.dbUserName = "";
         }

         if (this.dbPassword == null) {
            this.dbPassword = "";
         }

         if (this.dbDriver != null) {
            Loader.loadClass(this.dbDriver).getDeclaredConstructor().newInstance();
         }
      } catch (Exception var6) {
         throw new IllegalStateException(var6.toString());
      }
   }
}
