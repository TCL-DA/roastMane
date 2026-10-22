package org.apache.logging.log4j.util.internal;

import java.io.ObjectInputFilter;
import java.io.ObjectInputFilter.Config;
import java.io.ObjectInputFilter.FilterInfo;
import java.io.ObjectInputFilter.Status;

public class DefaultObjectInputFilter implements ObjectInputFilter {
   private final ObjectInputFilter delegate;

   public DefaultObjectInputFilter() {
      this.delegate = null;
   }

   public DefaultObjectInputFilter(final ObjectInputFilter filter) {
      this.delegate = filter;
   }

   public static DefaultObjectInputFilter newInstance(final ObjectInputFilter filter) {
      return new DefaultObjectInputFilter(filter);
   }

   @Override
   public Status checkInput(final FilterInfo filterInfo) {
      if (this.delegate != null) {
         Status status = this.delegate.checkInput(filterInfo);
         if (status != Status.UNDECIDED) {
            return status;
         }
      }

      ObjectInputFilter serialFilter = Config.getSerialFilter();
      if (serialFilter != null) {
         Status status = serialFilter.checkInput(filterInfo);
         if (status != Status.UNDECIDED) {
            return status;
         }
      }

      Class<?> serialClass = filterInfo.serialClass();
      if (serialClass != null) {
         String name = SerializationUtil.stripArray(serialClass);
         return isAllowedByDefault(name) ? Status.ALLOWED : Status.REJECTED;
      } else {
         return Status.ALLOWED;
      }
   }

   private static boolean isAllowedByDefault(final String name) {
      return isRequiredPackage(name) || SerializationUtil.REQUIRED_JAVA_CLASSES.contains(name);
   }

   private static boolean isRequiredPackage(final String name) {
      for (String packageName : SerializationUtil.REQUIRED_JAVA_PACKAGES) {
         if (name.startsWith(packageName)) {
            return true;
         }
      }

      return false;
   }
}
