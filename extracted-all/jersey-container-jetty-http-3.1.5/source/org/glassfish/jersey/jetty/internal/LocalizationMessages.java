package org.glassfish.jersey.jetty.internal;

import java.util.Locale;
import java.util.ResourceBundle;
import org.glassfish.jersey.internal.l10n.Localizable;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory.ResourceBundleSupplier;

public final class LocalizationMessages {
   private static final String BUNDLE_NAME = "org.glassfish.jersey.jetty.internal.localization";
   private static final LocalizableMessageFactory MESSAGE_FACTORY = new LocalizableMessageFactory(
      "org.glassfish.jersey.jetty.internal.localization", new LocalizationMessages.BundleSupplier()
   );
   private static final Localizer LOCALIZER = new Localizer();

   private LocalizationMessages() {
   }

   public static Localizable localizableWRONG_SCHEME_WHEN_USING_HTTP() {
      return MESSAGE_FACTORY.getMessage("wrong.scheme.when.using.http", new Object[0]);
   }

   public static String WRONG_SCHEME_WHEN_USING_HTTP() {
      return LOCALIZER.localize(localizableWRONG_SCHEME_WHEN_USING_HTTP());
   }

   public static Localizable localizableWRONG_SCHEME_WHEN_USING_HTTPS() {
      return MESSAGE_FACTORY.getMessage("wrong.scheme.when.using.https", new Object[0]);
   }

   public static String WRONG_SCHEME_WHEN_USING_HTTPS() {
      return LOCALIZER.localize(localizableWRONG_SCHEME_WHEN_USING_HTTPS());
   }

   public static Localizable localizableURI_CANNOT_BE_NULL() {
      return MESSAGE_FACTORY.getMessage("uri.cannot.be.null", new Object[0]);
   }

   public static String URI_CANNOT_BE_NULL() {
      return LOCALIZER.localize(localizableURI_CANNOT_BE_NULL());
   }

   public static Localizable localizableUNABLE_TO_CLOSE_RESPONSE() {
      return MESSAGE_FACTORY.getMessage("unable.to.close.response", new Object[0]);
   }

   public static String UNABLE_TO_CLOSE_RESPONSE() {
      return LOCALIZER.localize(localizableUNABLE_TO_CLOSE_RESPONSE());
   }

   public static Localizable localizableERROR_WHEN_CREATING_SERVER() {
      return MESSAGE_FACTORY.getMessage("error.when.creating.server", new Object[0]);
   }

   public static String ERROR_WHEN_CREATING_SERVER() {
      return LOCALIZER.localize(localizableERROR_WHEN_CREATING_SERVER());
   }

   public static Localizable localizableNOT_SUPPORTED() {
      return MESSAGE_FACTORY.getMessage("not.supported", new Object[0]);
   }

   public static String NOT_SUPPORTED() {
      return LOCALIZER.localize(localizableNOT_SUPPORTED());
   }

   public static Localizable localizableEXCEPTION_SENDING_ERROR_RESPONSE(Object arg0, Object arg1) {
      return MESSAGE_FACTORY.getMessage("exception.sending.error.response", new Object[]{arg0, arg1});
   }

   public static String EXCEPTION_SENDING_ERROR_RESPONSE(Object arg0, Object arg1) {
      return LOCALIZER.localize(localizableEXCEPTION_SENDING_ERROR_RESPONSE(arg0, arg1));
   }

   private static class BundleSupplier implements ResourceBundleSupplier {
      private BundleSupplier() {
      }

      public ResourceBundle getResourceBundle(Locale locale) {
         return ResourceBundle.getBundle("org.glassfish.jersey.jetty.internal.localization", locale);
      }
   }
}
