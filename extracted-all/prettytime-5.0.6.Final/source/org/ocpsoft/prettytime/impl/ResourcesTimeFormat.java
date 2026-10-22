package org.ocpsoft.prettytime.impl;

import java.util.Locale;
import java.util.ResourceBundle;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.TimeFormat;
import org.ocpsoft.prettytime.format.SimpleTimeFormat;

public class ResourcesTimeFormat extends SimpleTimeFormat {
   private final ResourcesTimeUnit unit;
   private TimeFormat override;
   private String overrideResourceBundle;

   public ResourcesTimeFormat(ResourcesTimeUnit unit) {
      this.unit = unit;
   }

   public ResourcesTimeFormat(ResourcesTimeUnit unit, String overrideResourceBundle) {
      this.unit = unit;
      this.overrideResourceBundle = overrideResourceBundle;
   }

   public ResourcesTimeFormat setLocale(Locale locale) {
      ResourceBundle bundle = null;
      if (this.overrideResourceBundle != null) {
         try {
            bundle = ResourceBundle.getBundle(this.overrideResourceBundle, locale);
         } catch (Exception var8) {
         }
      }

      if (bundle == null) {
         bundle = ResourceBundle.getBundle(this.unit.getResourceBundleName(), locale);
      }

      if (bundle instanceof TimeFormatProvider) {
         TimeFormat format = ((TimeFormatProvider)bundle).getFormatFor(this.unit);
         if (format != null) {
            this.override = format;
         }
      } else {
         this.override = null;
      }

      if (this.override == null) {
         this.setPattern(bundle.getString(this.unit.getResourceKeyPrefix() + "Pattern"));
         this.setFuturePrefix(bundle.getString(this.unit.getResourceKeyPrefix() + "FuturePrefix"));
         this.setFutureSuffix(bundle.getString(this.unit.getResourceKeyPrefix() + "FutureSuffix"));
         this.setPastPrefix(bundle.getString(this.unit.getResourceKeyPrefix() + "PastPrefix"));
         this.setPastSuffix(bundle.getString(this.unit.getResourceKeyPrefix() + "PastSuffix"));
         this.setSingularName(bundle.getString(this.unit.getResourceKeyPrefix() + "SingularName"));
         this.setPluralName(bundle.getString(this.unit.getResourceKeyPrefix() + "PluralName"));

         try {
            this.setFuturePluralName(bundle.getString(this.unit.getResourceKeyPrefix() + "FuturePluralName"));
         } catch (Exception var7) {
         }

         try {
            this.setFutureSingularName(bundle.getString(this.unit.getResourceKeyPrefix() + "FutureSingularName"));
         } catch (Exception var6) {
         }

         try {
            this.setPastPluralName(bundle.getString(this.unit.getResourceKeyPrefix() + "PastPluralName"));
         } catch (Exception var5) {
         }

         try {
            this.setPastSingularName(bundle.getString(this.unit.getResourceKeyPrefix() + "PastSingularName"));
         } catch (Exception var4) {
         }
      }

      return this;
   }

   @Override
   public String decorate(Duration duration, String time) {
      return this.override == null ? super.decorate(duration, time) : this.override.decorate(duration, time);
   }

   @Override
   public String decorateUnrounded(Duration duration, String time) {
      return this.override == null ? super.decorateUnrounded(duration, time) : this.override.decorateUnrounded(duration, time);
   }

   @Override
   public String format(Duration duration) {
      return this.override == null ? super.format(duration) : this.override.format(duration);
   }

   @Override
   public String formatUnrounded(Duration duration) {
      return this.override == null ? super.formatUnrounded(duration) : this.override.formatUnrounded(duration);
   }
}
