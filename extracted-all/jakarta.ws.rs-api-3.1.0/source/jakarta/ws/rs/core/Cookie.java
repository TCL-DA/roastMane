package jakarta.ws.rs.core;

import jakarta.ws.rs.ext.RuntimeDelegate;
import java.util.Objects;

public class Cookie {
   public static final int DEFAULT_VERSION = 1;
   @Deprecated
   private static final RuntimeDelegate.HeaderDelegate<Cookie> HEADER_DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(Cookie.class);
   private final String name;
   private final String value;
   private final int version;
   private final String path;
   private final String domain;

   @Deprecated
   public Cookie(String name, String value, String path, String domain, int version) throws IllegalArgumentException {
      if (name == null) {
         throw new IllegalArgumentException("name==null");
      } else {
         this.name = name;
         this.value = value;
         this.version = version;
         this.domain = domain;
         this.path = path;
      }
   }

   @Deprecated
   public Cookie(String name, String value, String path, String domain) throws IllegalArgumentException {
      this(name, value, path, domain, 1);
   }

   @Deprecated
   public Cookie(String name, String value) throws IllegalArgumentException {
      this(name, value, null, null);
   }

   protected Cookie(Cookie.AbstractCookieBuilder<?> builder) throws IllegalArgumentException {
      if (builder.name == null) {
         throw new IllegalArgumentException("name==null");
      } else {
         this.name = builder.name;
         this.value = builder.value;
         this.version = builder.version;
         this.domain = builder.domain;
         this.path = builder.path;
      }
   }

   @Deprecated
   public static Cookie valueOf(String value) {
      return HEADER_DELEGATE.fromString(value);
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public int getVersion() {
      return this.version;
   }

   public String getDomain() {
      return this.domain;
   }

   public String getPath() {
      return this.path;
   }

   @Deprecated
   @Override
   public String toString() {
      return HEADER_DELEGATE.toString(this);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.name, this.value, this.version, this.path, this.domain);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Cookie other = (Cookie)obj;
         if (!Objects.equals(this.name, other.name)) {
            return false;
         } else if (!Objects.equals(this.value, other.value)) {
            return false;
         } else if (!Objects.equals(this.version, other.version)) {
            return false;
         } else {
            return !Objects.equals(this.path, other.path) ? false : Objects.equals(this.domain, other.domain);
         }
      }
   }

   public abstract static class AbstractCookieBuilder<T extends Cookie.AbstractCookieBuilder<T>> {
      private final String name;
      private String value;
      private int version = 1;
      private String path;
      private String domain;

      public AbstractCookieBuilder(String name) {
         this.name = name;
      }

      public T value(String value) {
         this.value = value;
         return this.self();
      }

      public T version(int version) {
         this.version = version;
         return this.self();
      }

      public T path(String path) {
         this.path = path;
         return this.self();
      }

      public T domain(String domain) {
         this.domain = domain;
         return this.self();
      }

      private T self() {
         return (T)this;
      }

      public abstract Cookie build();
   }

   public static class Builder extends Cookie.AbstractCookieBuilder<Cookie.Builder> {
      public Builder(String name) {
         super(name);
      }

      @Override
      public Cookie build() {
         return new Cookie(this);
      }
   }
}
