package org.eclipse.yasson.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public class Messages {
   private static final String MESSAGE_BUNDLE = "yasson-messages";
   private static final String ENCODING = "UTF-8";

   private Messages() {
   }

   public static String getMessage(MessageKeys key, Object... objects) {
      return getMessage(key, Locale.getDefault(), objects);
   }

   public static String getMessage(MessageKeys key, Locale locale, Object... objects) {
      ResourceBundle messages = getResourceBundle(locale);
      MessageFormat formatter = new MessageFormat(messages.getString(key.getKey()));
      return formatter.format(objects);
   }

   private static ResourceBundle getResourceBundle(Locale locale) {
      try {
         return ResourceBundle.getBundle("yasson-messages", locale, new Messages.UTF8Control());
      } catch (UnsupportedOperationException var2) {
         return ResourceBundle.getBundle("yasson-messages", locale);
      }
   }

   static class UTF8Control extends Control {
      @Override
      public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
         String bundleName = this.toBundleName(baseName, locale);
         String resourceName = this.toResourceName(bundleName, "properties");
         ResourceBundle bundle = null;
         InputStream stream = null;
         if (reload) {
            URL url = loader.getResource(resourceName);
            if (url != null) {
               URLConnection connection = url.openConnection();
               if (connection != null) {
                  connection.setUseCaches(false);
                  stream = connection.getInputStream();
               }
            }
         } else {
            stream = loader.getResourceAsStream(resourceName);
         }

         if (stream != null) {
            try {
               bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
            } finally {
               stream.close();
            }
         }

         return bundle;
      }
   }
}
