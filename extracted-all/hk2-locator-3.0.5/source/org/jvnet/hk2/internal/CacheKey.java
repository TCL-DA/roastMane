package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

public class CacheKey {
   private final String removalName;
   private final Type lookupType;
   private final String name;
   private final Annotation[] qualifiers;
   private final Unqualified unqualified;
   private final int hashCode;

   public CacheKey(Type lookupType, String name, Unqualified unqualified, Annotation... qualifiers) {
      this.lookupType = lookupType;
      Class<?> rawClass = ReflectionHelper.getRawClass(lookupType);
      if (rawClass != null) {
         this.removalName = rawClass.getName();
      } else {
         this.removalName = null;
      }

      this.name = name;
      if (qualifiers.length > 0) {
         this.qualifiers = qualifiers;
      } else {
         this.qualifiers = null;
      }

      this.unqualified = unqualified;
      int retVal = 0;
      if (lookupType != null) {
         retVal ^= lookupType.hashCode();
      }

      if (name != null) {
         retVal ^= name.hashCode();
      }

      for (Annotation anno : qualifiers) {
         retVal ^= anno.hashCode();
      }

      if (unqualified != null) {
         retVal = ~retVal;

         for (Class<?> clazz : unqualified.value()) {
            retVal ^= clazz.hashCode();
         }
      }

      this.hashCode = retVal;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (!(o instanceof CacheKey)) {
         return false;
      } else {
         final CacheKey other = (CacheKey)o;
         if (this.hashCode != other.hashCode) {
            return false;
         } else if (!GeneralUtilities.safeEquals(this.lookupType, other.lookupType)) {
            return false;
         } else if (!GeneralUtilities.safeEquals(this.name, other.name)) {
            return false;
         } else {
            if (this.qualifiers != null) {
               if (other.qualifiers == null) {
                  return false;
               }

               if (this.qualifiers.length != other.qualifiers.length) {
                  return false;
               }

               boolean isEqual = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                  public Boolean run() {
                     for (int lcv = 0; lcv < CacheKey.this.qualifiers.length; lcv++) {
                        if (!GeneralUtilities.safeEquals(CacheKey.this.qualifiers[lcv], other.qualifiers[lcv])) {
                           return false;
                        }
                     }

                     return true;
                  }
               });
               if (!isEqual) {
                  return false;
               }
            } else if (other.qualifiers != null) {
               return false;
            }

            if (this.unqualified != null) {
               if (other.unqualified == null) {
                  return false;
               }

               Class<?>[] myClazzes = this.unqualified.value();
               Class<?>[] otherClazzes = other.unqualified.value();
               if (myClazzes.length != otherClazzes.length) {
                  return false;
               }

               for (int lcv = 0; lcv < myClazzes.length; lcv++) {
                  if (!GeneralUtilities.safeEquals(myClazzes[lcv], otherClazzes[lcv])) {
                     return false;
                  }
               }
            } else if (other.unqualified != null) {
               return false;
            }

            return true;
         }
      }
   }

   public boolean matchesRemovalName(String name) {
      if (this.removalName == null) {
         return false;
      } else {
         return name == null ? false : this.removalName.equals(name);
      }
   }

   @Override
   public String toString() {
      return "CacheKey("
         + Pretty.type(this.lookupType)
         + ","
         + this.name
         + ","
         + (this.qualifiers == null ? 0 : this.qualifiers.length)
         + ","
         + System.identityHashCode(this)
         + ","
         + this.hashCode
         + ")";
   }
}
