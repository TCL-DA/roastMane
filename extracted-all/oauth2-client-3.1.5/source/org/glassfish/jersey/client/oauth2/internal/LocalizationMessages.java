package org.glassfish.jersey.client.oauth2.internal;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.client.oauth2.internal.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.client.oauth2.internal.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableERROR_FLOW_NOT_FINISHED() {
      return MESSAGE_FACTORY.getMessage("error.flow.not.finished", new Object[0]);
   }

   public static String ERROR_FLOW_NOT_FINISHED() {
      return LOCALIZER.localize(localizableERROR_FLOW_NOT_FINISHED());
   }

   public static Localizable localizableERROR_FLOW_REQUEST_REFRESH_TOKEN(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.flow.request.refreshToken", new Object[]{arg0});
   }

   public static String ERROR_FLOW_REQUEST_REFRESH_TOKEN(Object arg0) {
      return LOCALIZER.localize(localizableERROR_FLOW_REQUEST_REFRESH_TOKEN(arg0));
   }

   public static Localizable localizableERROR_FLOW_REQUEST_ACCESS_TOKEN(Object arg0) {
      return MESSAGE_FACTORY.getMessage("error.flow.request.accessToken", new Object[]{arg0});
   }

   public static String ERROR_FLOW_REQUEST_ACCESS_TOKEN(Object arg0) {
      return LOCALIZER.localize(localizableERROR_FLOW_REQUEST_ACCESS_TOKEN(arg0));
   }

   public static Localizable localizableERROR_FLOW_WRONG_STATE() {
      return MESSAGE_FACTORY.getMessage("error.flow.wrong.state", new Object[0]);
   }

   public static String ERROR_FLOW_WRONG_STATE() {
      return LOCALIZER.localize(localizableERROR_FLOW_WRONG_STATE());
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.client.oauth2.internal.localization", locale);
      }
   }
}
