package org.apache.logging.log4j.core.util;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class BasicAuthorizationProvider implements AuthorizationProvider {
   private static final String[] PREFIXES = new String[]{"log4j2.config.", "log4j2.Configuration.", "logging.auth."};
   private static final String AUTH_USER_NAME = "username";
   private static final String AUTH_PASSWORD = "password";
   private static final String AUTH_PASSWORD_DECRYPTOR = "passwordDecryptor";
   public static final String CONFIG_USER_NAME = "log4j2.configurationUserName";
   public static final String CONFIG_PASSWORD = "log4j2.configurationPassword";
   public static final String PASSWORD_DECRYPTOR = "log4j2.passwordDecryptor";
   private static final String BASIC_AUTH_ENCODING = "log4j2.configurationAuthorizationEncoding";
   private static final String SPRING_BASIC_AUTH_ENCODING = "logging.auth.encoding";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private String authString = null;

   public BasicAuthorizationProvider(final PropertiesUtil props) {
      String userName = props.getStringProperty(PREFIXES, "username", () -> props.getStringProperty("log4j2.configurationUserName"));
      String password = props.getStringProperty(PREFIXES, "password", () -> props.getStringProperty("log4j2.configurationPassword"));
      String decryptor = props.getStringProperty(PREFIXES, "passwordDecryptor", () -> props.getStringProperty("log4j2.passwordDecryptor"));
      Charset passwordCharset = props.getCharsetProperty("log4j2.configurationAuthorizationEncoding");
      if (passwordCharset == null) {
         props.getCharsetProperty("logging.auth.encoding", StandardCharsets.UTF_8);
      }

      if (decryptor != null) {
         try {
            Object obj = LoaderUtil.newInstanceOf(decryptor);
            if (obj instanceof PasswordDecryptor) {
               password = ((PasswordDecryptor)obj).decryptPassword(password);
            }
         } catch (Exception var7) {
            LOGGER.warn("Unable to decrypt password.", var7);
         }
      }

      if (userName != null && password != null) {
         this.authString = "Basic " + Base64.getEncoder().encodeToString((userName + ":" + password).getBytes(passwordCharset));
      }
   }

   @Override
   public void addAuthorization(final URLConnection urlConnection) {
      if (this.authString != null) {
         urlConnection.setRequestProperty("Authorization", this.authString);
      }
   }
}
