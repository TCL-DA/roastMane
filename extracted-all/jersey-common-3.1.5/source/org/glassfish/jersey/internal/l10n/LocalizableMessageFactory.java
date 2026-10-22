package org.glassfish.jersey.internal.l10n;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizableMessageFactory {
   private final String _bundlename;
   private final LocalizableMessageFactory.ResourceBundleSupplier _rbSupplier;

   @Deprecated
   public LocalizableMessageFactory(String bundlename) {
      this._bundlename = bundlename;
      this._rbSupplier = null;
   }

   public LocalizableMessageFactory(String bundlename, LocalizableMessageFactory.ResourceBundleSupplier rbSupplier) {
      this._bundlename = bundlename;
      this._rbSupplier = rbSupplier;
   }

   public Localizable getMessage(String key, Object... args) {
      return new LocalizableMessage(this._bundlename, this._rbSupplier, key, args);
   }

   public interface ResourceBundleSupplier {
      ResourceBundle getResourceBundle(Locale var1);
   }
}
