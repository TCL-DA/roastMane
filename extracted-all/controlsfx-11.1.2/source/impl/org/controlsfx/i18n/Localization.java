package impl.org.controlsfx.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localization {
   public static final String KEY_PREFIX = "@@";
   private static final String LOCALE_BUNDLE_NAME = "controlsfx";
   private static Locale locale = null;
   private static Locale resourceBundleLocale = null;
   private static ResourceBundle resourceBundle = null;

   private Localization() {
   }

   public static final Locale getLocale() {
      return locale == null ? Locale.getDefault() : locale;
   }

   public static final void setLocale(Locale newLocale) {
      locale = newLocale;
   }

   private static final synchronized ResourceBundle getLocaleBundle() {
      Locale currentLocale = getLocale();
      if (!currentLocale.equals(resourceBundleLocale)) {
         resourceBundleLocale = currentLocale;
         resourceBundle = ResourceBundle.getBundle("controlsfx", resourceBundleLocale, Localization.class.getClassLoader());
      }

      return resourceBundle;
   }

   public static final String getString(String key) {
      try {
         return getLocaleBundle().getString(key);
      } catch (MissingResourceException var2) {
         return String.format("<%s>", key);
      }
   }

   public static final String asKey(String text) {
      return "@@" + text;
   }

   public static final boolean isKey(String text) {
      return text != null && text.startsWith("@@");
   }

   public static String localize(String text) {
      return isKey(text) ? getString(text.substring("@@".length()).trim()) : text;
   }
}
