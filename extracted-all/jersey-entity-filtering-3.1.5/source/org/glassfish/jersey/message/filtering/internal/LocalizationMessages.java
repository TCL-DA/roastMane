package org.glassfish.jersey.message.filtering.internal;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.message.filtering.internal.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.message.filtering.internal.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(Object arg0) {
      return MESSAGE_FACTORY.getMessage("entity.filtering.scope.not.annotations", new Object[]{arg0});
   }

   public static String ENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(Object arg0) {
      return LOCALIZER.localize(localizableENTITY_FILTERING_SCOPE_NOT_ANNOTATIONS(arg0));
   }

   public static Localizable localizableMERGING_FILTERING_SCOPES() {
      return MESSAGE_FACTORY.getMessage("merging.filtering.scopes", new Object[0]);
   }

   public static String MERGING_FILTERING_SCOPES() {
      return LOCALIZER.localize(localizableMERGING_FILTERING_SCOPES());
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.message.filtering.internal.localization", locale);
      }
   }
}
