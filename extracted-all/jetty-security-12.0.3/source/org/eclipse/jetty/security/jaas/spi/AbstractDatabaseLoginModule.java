package org.eclipse.jetty.security.jaas.spi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.util.security.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDatabaseLoginModule extends AbstractLoginModule {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractDatabaseLoginModule.class);
   private String userQuery;
   private String rolesQuery;
   private String dbUserTable;
   private String dbUserTableUserField;
   private String dbUserTableCredentialField;
   private String dbUserRoleTable;
   private String dbUserRoleTableUserField;
   private String dbUserRoleTableRoleField;

   public abstract Connection getConnection() throws Exception;

   @Override
   public AbstractLoginModule.JAASUser getUser(String userName) throws Exception {
      AbstractDatabaseLoginModule.JDBCUser var14;
      try (Connection connection = this.getConnection()) {
         String dbCredential = null;

         try (PreparedStatement statement = connection.prepareStatement(this.userQuery)) {
            statement.setString(1, userName);

            try (ResultSet results = statement.executeQuery()) {
               if (results.next()) {
                  dbCredential = results.getString(1);
               }
            }
         }

         if (dbCredential == null) {
            return null;
         }

         var14 = new AbstractDatabaseLoginModule.JDBCUser(new UserPrincipal(userName, Credential.getCredential(dbCredential)));
      }

      return var14;
   }

   public List<String> getRoles(String userName) throws Exception {
      List<String> roles = new ArrayList<>();

      try (
         Connection connection = this.getConnection();
         PreparedStatement statement = connection.prepareStatement(this.rolesQuery);
      ) {
         statement.setString(1, userName);

         try (ResultSet results = statement.executeQuery()) {
            while (results.next()) {
               String roleName = results.getString(1);
               roles.add(roleName);
            }
         }
      }

      return roles;
   }

   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      super.initialize(subject, callbackHandler, sharedState, options);
      this.dbUserTable = (String)options.get("userTable");
      this.dbUserTableUserField = (String)options.get("userField");
      this.dbUserTableCredentialField = (String)options.get("credentialField");
      this.userQuery = "select " + this.dbUserTableCredentialField + " from " + this.dbUserTable + " where " + this.dbUserTableUserField + "=?";
      this.dbUserRoleTable = (String)options.get("userRoleTable");
      this.dbUserRoleTableUserField = (String)options.get("userRoleUserField");
      this.dbUserRoleTableRoleField = (String)options.get("userRoleRoleField");
      this.rolesQuery = "select " + this.dbUserRoleTableRoleField + " from " + this.dbUserRoleTable + " where " + this.dbUserRoleTableUserField + "=?";
      if (LOG.isDebugEnabled()) {
         LOG.debug("userQuery = {} rolesQuery = {}", this.userQuery, this.rolesQuery);
      }
   }

   public class JDBCUser extends AbstractLoginModule.JAASUser {
      public JDBCUser(UserPrincipal user) {
         super(user);
      }

      @Override
      public List<String> doFetchRoles() throws Exception {
         return AbstractDatabaseLoginModule.this.getRoles(this.getUserName());
      }
   }
}
