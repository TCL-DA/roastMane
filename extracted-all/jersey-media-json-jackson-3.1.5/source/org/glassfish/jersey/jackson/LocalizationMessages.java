package org.glassfish.jersey.jackson;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.jackson.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.jackson.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableERROR_JACKSON_STREAMREADCONSTRAINTS(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.jackson.streamreadconstraints", new Object[]{arg0});
   }

   public static String ERROR_JACKSON_STREAMREADCONSTRAINTS(Object arg0) {
      return LOCALIZER.localize(localizableERROR_JACKSON_STREAMREADCONSTRAINTS(arg0));
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.jackson.localization", locale);
      }
   }
}
