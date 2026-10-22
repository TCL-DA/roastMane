package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Collection;
import java.util.Collections;
import org.apache.logging.log4j.util.internal.SerializationUtil;

public class FilteredObjectInputStream extends ObjectInputStream {
   private final Collection<String> allowedExtraClasses;

   public FilteredObjectInputStream() throws IOException, SecurityException {
      this.allowedExtraClasses = Collections.emptySet();
   }

   public FilteredObjectInputStream(final InputStream inputStream) throws IOException {
      super(inputStream);
      this.allowedExtraClasses = Collections.emptySet();
   }

   public FilteredObjectInputStream(final Collection<String> allowedExtraClasses) throws IOException, SecurityException {
      this.allowedExtraClasses = allowedExtraClasses;
   }

   public FilteredObjectInputStream(final InputStream inputStream, final Collection<String> allowedExtraClasses) throws IOException {
      super(inputStream);
      this.allowedExtraClasses = allowedExtraClasses;
   }

   public Collection<String> getAllowedClasses() {
      return this.allowedExtraClasses;
   }

   @Override
   protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      String name = SerializationUtil.stripArray(desc.getName());
      if (!isAllowedByDefault(name) && !this.allowedExtraClasses.contains(name)) {
         throw new InvalidObjectException("Class is not allowed for deserialization: " + name);
      } else {
         return super.resolveClass(desc);
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
