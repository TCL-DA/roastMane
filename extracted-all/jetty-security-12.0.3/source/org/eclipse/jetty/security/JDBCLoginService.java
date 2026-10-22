package org.eclipse.jetty.security;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.ResourceFactory.Closeable;
import org.eclipse.jetty.util.security.Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCLoginService extends AbstractLoginService {
   private static final Logger LOG = LoggerFactory.getLogger(JDBCLoginService.class);
   protected String _config;
   protected String _jdbcDriver;
   protected String _url;
   protected String _userName;
   protected String _password;
   protected String _userTableKey;
   protected String _userTablePasswordField;
   protected String _roleTableRoleField;
   protected String _userSql;
   protected String _roleSql;
   protected Connection _con;

   public JDBCLoginService() {
   }

   public JDBCLoginService(String name) {
      this.setName(name);
   }

   public JDBCLoginService(String name, String config) {
      this.setName(name);
      this.setConfig(config);
   }

   public JDBCLoginService(String name, IdentityService identityService, String config) {
      this.setName(name);
      this.setIdentityService(identityService);
      this.setConfig(config);
   }

   protected void doStart() throws Exception {
      Properties properties = new Properties();
      Closeable resourceFactory = ResourceFactory.closeable();

      try (InputStream in = resourceFactory.newResource(this._config).newInputStream()) {
         properties.load(in);
      } catch (Throwable var12) {
         if (resourceFactory != null) {
            try {
               resourceFactory.close();
            } catch (Throwable var9) {
               var12.addSuppressed(var9);
            }
         }

         throw var12;
      }

      if (resourceFactory != null) {
         resourceFactory.close();
      }

      this._jdbcDriver = properties.getProperty("jdbcdriver");
      this._url = properties.getProperty("url");
      this._userName = properties.getProperty("username");
      this._password = properties.getProperty("password");
      this._userTableKey = properties.getProperty("usertablekey");
      this._userTablePasswordField = properties.getProperty("usertablepasswordfield");
      this._roleTableRoleField = properties.getProperty("roletablerolefield");
      String userTable = properties.getProperty("usertable");
      String userTableUserField = properties.getProperty("usertableuserfield");
      String roleTable = properties.getProperty("roletable");
      String roleTableKey = properties.getProperty("roletablekey");
      String userRoleTable = properties.getProperty("userroletable");
      String userRoleTableUserKey = properties.getProperty("userroletableuserkey");
      String userRoleTableRoleKey = properties.getProperty("userroletablerolekey");
      if (this._jdbcDriver == null
         || this._jdbcDriver.equals("")
         || this._url == null
         || this._url.equals("")
         || this._userName == null
         || this._userName.equals("")
         || this._password == null) {
         LOG.warn("UserRealm {} has not been properly configured", this.getName());
      }

      this._userSql = "select " + this._userTableKey + "," + this._userTablePasswordField + " from " + userTable + " where " + userTableUserField + " = ?";
      this._roleSql = "select r."
         + this._roleTableRoleField
         + " from "
         + roleTable
         + " r, "
         + userRoleTable
         + " u where u."
         + userRoleTableUserKey
         + " = ? and r."
         + roleTableKey
         + " = u."
         + userRoleTableRoleKey;
      Loader.loadClass(this._jdbcDriver).getDeclaredConstructor().newInstance();
      super.doStart();
   }

   public String getConfig() {
      return this._config;
   }

   public void setConfig(String config) {
      if (this.isRunning()) {
         throw new IllegalStateException("Running");
      } else {
         this._config = config;
      }
   }

   public Connection connectDatabase() throws SQLException {
      return DriverManager.getConnection(this._url, this._userName, this._password);
   }

   @Override
   public UserPrincipal loadUserInfo(String username) {
      try {
         if (null == this._con) {
            this._con = this.connectDatabase();
         }

         JDBCLoginService.JDBCUserPrincipal var6;
         try (PreparedStatement stat1 = this._con.prepareStatement(this._userSql)) {
            stat1.setObject(1, username);

            try (ResultSet rs1 = stat1.executeQuery()) {
               if (!rs1.next()) {
                  return null;
               }

               int key = rs1.getInt(this._userTableKey);
               String credentials = rs1.getString(this._userTablePasswordField);
               var6 = new JDBCLoginService.JDBCUserPrincipal(username, Credential.getCredential(credentials), key);
            }
         }

         return var6;
      } catch (SQLException var11) {
         LOG.warn("LoginService {} could not load user {}", new Object[]{this.getName(), username, var11});
         this.closeConnection();
         return null;
      }
   }

   @Override
   public List<RolePrincipal> loadRoleInfo(UserPrincipal user) {
      if (user == null) {
         return null;
      } else {
         JDBCLoginService.JDBCUserPrincipal jdbcUser = (JDBCLoginService.JDBCUserPrincipal)user;

         try {
            if (null == this._con) {
               this._con = this.connectDatabase();
            }

            List<String> roles = new ArrayList<>();

            List var6;
            try (PreparedStatement stat2 = this._con.prepareStatement(this._roleSql)) {
               stat2.setInt(1, jdbcUser.getUserKey());

               try (ResultSet rs2 = stat2.executeQuery()) {
                  while (rs2.next()) {
                     roles.add(rs2.getString(this._roleTableRoleField));
                  }

                  var6 = roles.stream().map(RolePrincipal::new).collect(Collectors.toList());
               }
            }

            return var6;
         } catch (SQLException var12) {
            LOG.warn("LoginService {} could not load roles for user {}", new Object[]{this.getName(), user.getName(), var12});
            this.closeConnection();
            return null;
         }
      }
   }

   protected void doStop() throws Exception {
      this.closeConnection();
      super.doStop();
   }

   private void closeConnection() {
      if (this._con != null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Closing db connection for JDBCLoginService");
         }

         try {
            this._con.close();
         } catch (Exception var2) {
            LOG.trace("IGNORED", var2);
         }
      }

      this._con = null;
   }

   public static class JDBCUserPrincipal extends UserPrincipal {
      final int _userKey;

      public JDBCUserPrincipal(String name, Credential credential, int key) {
         super(name, credential);
         this._userKey = key;
      }

      public int getUserKey() {
         return this._userKey;
      }
   }
}
