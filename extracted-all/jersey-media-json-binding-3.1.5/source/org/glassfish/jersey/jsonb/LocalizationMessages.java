package org.glassfish.jersey.jsonb;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.jsonb.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.jsonb.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableERROR_JSONB_SERIALIZATION() {
      return MESSAGE_FACTORY.getMessage("error.jsonb.serialization", new Object[0]);
   }

   public static String ERROR_JSONB_SERIALIZATION() {
      return LOCALIZER.localize(localizableERROR_JSONB_SERIALIZATION());
   }

   public static Localizable localizableERROR_JSONB_EMPTYSTREAM() {
      return MESSAGE_FACTORY.getMessage("error.jsonb.emptystream", new Object[0]);
   }

   public static String ERROR_JSONB_EMPTYSTREAM() {
      return LOCALIZER.localize(localizableERROR_JSONB_EMPTYSTREAM());
   }

   public static Localizable localizableERROR_JSONB_DESERIALIZATION() {
      return MESSAGE_FACTORY.getMessage("error.jsonb.deserialization", new Object[0]);
   }

   public static String ERROR_JSONB_DESERIALIZATION() {
      return LOCALIZER.localize(localizableERROR_JSONB_DESERIALIZATION());
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.jsonb.localization", locale);
      }
   }
}
