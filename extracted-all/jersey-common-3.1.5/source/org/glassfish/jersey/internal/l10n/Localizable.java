package org.glassfish.jersey.internal.l10n;

import java.util.Locale;
import java.util.ResourceBundle;

public interface Localizable {
   String NOT_LOCALIZABLE = "\u0000";

   String getKey();

   Object[] getArguments();

   String getResourceBundleName();

   ResourceBundle getResourceBundle(Locale var1);
}
