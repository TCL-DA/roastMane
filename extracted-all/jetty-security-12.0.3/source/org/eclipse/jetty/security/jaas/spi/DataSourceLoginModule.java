package org.eclipse.jetty.security.jaas.spi;

import java.sql.Connection;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.sql.DataSource;

public class DataSourceLoginModule extends AbstractDatabaseLoginModule {
   private String dbJNDIName;
   private DataSource dataSource;

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      try {
         super.initialize(subject, callbackHandler, sharedState, options);
         this.dbJNDIName = (String)options.get("dbJNDIName");
         InitialContext ic = new InitialContext();
         this.dataSource = (DataSource)ic.lookup("java:comp/env/" + this.dbJNDIName);
      } catch (NamingException var6) {
         throw new IllegalStateException(var6.toString());
      }
   }

   @Override
   public Connection getConnection() throws Exception {
      return this.dataSource.getConnection();
   }
}
