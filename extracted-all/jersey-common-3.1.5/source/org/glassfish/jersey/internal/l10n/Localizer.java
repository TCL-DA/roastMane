package org.glassfish.jersey.internal.l10n;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.glassfish.hk2.osgiresourcelocator.ResourceFinder;
import org.glassfish.jersey.internal.OsgiRegistry;
import org.glassfish.jersey.internal.util.ReflectionHelper;

public class Localizer {
   private final Locale _locale;
   private final HashMap<String, ResourceBundle> _resourceBundles;

   public Localizer() {
      this(Locale.getDefault());
   }

   public Localizer(Locale l) {
      this._locale = l;
      this._resourceBundles = new HashMap<>();
   }

   public Locale getLocale() {
      return this._locale;
   }

   public String localize(Localizable l) {
      String key = l.getKey();
      if ("\u0000".equals(key)) {
         return (String)l.getArguments()[0];
      } else {
         String bundlename = l.getResourceBundleName();

         try {
            ResourceBundle bundle = this._resourceBundles.get(bundlename);
            if (bundle == null) {
               try {
                  bundle = ResourceBundle.getBundle(bundlename, this._locale);
               } catch (MissingResourceException var18) {
                  int i = bundlename.lastIndexOf(46);
                  if (i != -1) {
                     String alternateBundleName = bundlename.substring(i + 1);

                     try {
                        bundle = ResourceBundle.getBundle(alternateBundleName, this._locale);
                     } catch (MissingResourceException var17) {
                        try {
                           bundle = ResourceBundle.getBundle(bundlename, this._locale, Thread.currentThread().getContextClassLoader());
                        } catch (MissingResourceException var16) {
                           OsgiRegistry osgiRegistry = ReflectionHelper.getOsgiRegistryInstance();
                           if (osgiRegistry != null) {
                              bundle = osgiRegistry.getResourceBundle(bundlename);
                           } else {
                              String path = bundlename.replace('.', '/') + ".properties";
                              URL bundleUrl = ResourceFinder.findEntry(path);
                              if (bundleUrl != null) {
                                 try {
                                    bundle = new PropertyResourceBundle(bundleUrl.openStream());
                                 } catch (IOException var15) {
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (bundle == null) {
                  return this.getDefaultMessage(l);
               }

               this._resourceBundles.put(bundlename, bundle);
            }

            if (key == null) {
               key = "undefined";
            }

            String msg;
            try {
               msg = bundle.getString(key);
            } catch (MissingResourceException var14) {
               msg = bundle.getString("undefined");
            }

            Object[] args = l.getArguments();

            for (int i = 0; i < args.length; i++) {
               if (args[i] instanceof Localizable) {
                  args[i] = this.localize((Localizable)args[i]);
               }
            }

            return MessageFormat.format(msg, args);
         } catch (MissingResourceException var19) {
            return this.getDefaultMessage(l);
         }
      }
   }

   private String getDefaultMessage(Localizable l) {
      String key = l.getKey();
      Object[] args = l.getArguments();
      StringBuilder sb = new StringBuilder();
      sb.append("[failed to localize] ");
      sb.append(key);
      if (args != null) {
         sb.append('(');

         for (int i = 0; i < args.length; i++) {
            if (i != 0) {
               sb.append(", ");
            }

            sb.append(String.valueOf(args[i]));
         }

         sb.append(')');
      }

      return sb.toString();
   }
}
