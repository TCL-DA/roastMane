package jakarta.ws.rs.core;

import jakarta.ws.rs.ext.RuntimeDelegate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CacheControl {
   @Deprecated
   private static final RuntimeDelegate.HeaderDelegate<CacheControl> HEADER_DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(CacheControl.class);
   private List<String> privateFields;
   private List<String> noCacheFields;
   private Map<String, String> cacheExtension;
   private boolean privateFlag;
   private boolean noCache;
   private boolean noStore;
   private boolean noTransform;
   private boolean mustRevalidate;
   private boolean proxyRevalidate;
   private int maxAge = -1;
   private int sMaxAge = -1;

   public CacheControl() {
      this.privateFlag = false;
      this.noCache = false;
      this.noStore = false;
      this.noTransform = true;
      this.mustRevalidate = false;
      this.proxyRevalidate = false;
   }

   @Deprecated
   public static CacheControl valueOf(String value) {
      return HEADER_DELEGATE.fromString(value);
   }

   public boolean isMustRevalidate() {
      return this.mustRevalidate;
   }

   public void setMustRevalidate(boolean mustRevalidate) {
      this.mustRevalidate = mustRevalidate;
   }

   public boolean isProxyRevalidate() {
      return this.proxyRevalidate;
   }

   public void setProxyRevalidate(boolean proxyRevalidate) {
      this.proxyRevalidate = proxyRevalidate;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public void setMaxAge(int maxAge) {
      this.maxAge = maxAge;
   }

   public int getSMaxAge() {
      return this.sMaxAge;
   }

   public void setSMaxAge(int smaxAge) {
      this.sMaxAge = smaxAge;
   }

   public List<String> getNoCacheFields() {
      if (this.noCacheFields == null) {
         this.noCacheFields = new ArrayList<>();
      }

      return this.noCacheFields;
   }

   public void setNoCache(boolean noCache) {
      this.noCache = noCache;
   }

   public boolean isNoCache() {
      return this.noCache;
   }

   public boolean isPrivate() {
      return this.privateFlag;
   }

   public List<String> getPrivateFields() {
      if (this.privateFields == null) {
         this.privateFields = new ArrayList<>();
      }

      return this.privateFields;
   }

   public void setPrivate(boolean flag) {
      this.privateFlag = flag;
   }

   public boolean isNoTransform() {
      return this.noTransform;
   }

   public void setNoTransform(boolean noTransform) {
      this.noTransform = noTransform;
   }

   public boolean isNoStore() {
      return this.noStore;
   }

   public void setNoStore(boolean noStore) {
      this.noStore = noStore;
   }

   public Map<String, String> getCacheExtension() {
      if (this.cacheExtension == null) {
         this.cacheExtension = new HashMap<>();
      }

      return this.cacheExtension;
   }

   @Deprecated
   @Override
   public String toString() {
      return HEADER_DELEGATE.toString(this);
   }

   @Override
   public int hashCode() {
      int hash = Objects.hash(
         this.privateFlag, this.noCache, this.noStore, this.noTransform, this.mustRevalidate, this.proxyRevalidate, this.maxAge, this.sMaxAge
      );
      hash = 41 * hash + hashCodeOf(this.privateFields);
      hash = 41 * hash + hashCodeOf(this.noCacheFields);
      return 41 * hash + hashCodeOf(this.cacheExtension);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CacheControl other = (CacheControl)obj;
         if (this.privateFlag != other.privateFlag) {
            return false;
         } else if (this.noCache != other.noCache) {
            return false;
         } else if (this.noStore != other.noStore) {
            return false;
         } else if (this.noTransform != other.noTransform) {
            return false;
         } else if (this.mustRevalidate != other.mustRevalidate) {
            return false;
         } else if (this.proxyRevalidate != other.proxyRevalidate) {
            return false;
         } else if (this.maxAge != other.maxAge) {
            return false;
         } else if (this.sMaxAge != other.sMaxAge) {
            return false;
         } else if (notEqual(this.privateFields, other.privateFields)) {
            return false;
         } else {
            return notEqual(this.noCacheFields, other.noCacheFields) ? false : !notEqual(this.cacheExtension, other.cacheExtension);
         }
      }
   }

   private static boolean notEqual(Collection<?> first, Collection<?> second) {
      if (first == second) {
         return false;
      } else if (first == null) {
         return !second.isEmpty();
      } else {
         return second == null ? !first.isEmpty() : !first.equals(second);
      }
   }

   private static boolean notEqual(Map<?, ?> first, Map<?, ?> second) {
      if (first == second) {
         return false;
      } else if (first == null) {
         return !second.isEmpty();
      } else {
         return second == null ? !first.isEmpty() : !first.equals(second);
      }
   }

   private static int hashCodeOf(Collection<?> instance) {
      return instance != null && !instance.isEmpty() ? instance.hashCode() : 0;
   }

   private static int hashCodeOf(Map<?, ?> instance) {
      return instance != null && !instance.isEmpty() ? instance.hashCode() : 0;
   }
}
