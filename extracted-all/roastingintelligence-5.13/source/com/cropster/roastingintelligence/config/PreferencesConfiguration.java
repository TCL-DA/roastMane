package com.cropster.roastingintelligence.config;

import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;
import org.apache.commons.lang3.LocaleUtils;

public class PreferencesConfiguration {
   public static String createSettingsPath(String instance) {
      String suffix = instance != null ? File.separator + instance : "";
      return "org" + File.separator + "cropster" + File.separator + "roastingintelligence" + suffix;
   }

   public static Preferences createPreferences(String instance) {
      if (instance != null && !instance.matches("[a-z0-9]+")) {
         throw new IllegalArgumentException("Instance name must only contain the following characters: a-z0-9");
      } else {
         if ("default".equals(instance)) {
            instance = null;
         }

         return Preferences.userRoot().node(createSettingsPath(instance));
      }
   }

   public Preferences preferences(String instance) {
      Preferences prefs = createPreferences(instance);
      String localeString = prefs.get("locale", null);
      if (localeString != null) {
         Locale.setDefault(LocaleUtils.toLocale(localeString));
      }

      return prefs;
   }
}
