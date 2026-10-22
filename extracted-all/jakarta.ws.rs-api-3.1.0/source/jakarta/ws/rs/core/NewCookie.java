package jakarta.ws.rs.core;

import jakarta.ws.rs.ext.RuntimeDelegate;
import java.util.Date;
import java.util.Objects;

public class NewCookie extends Cookie {
   public static final int DEFAULT_MAX_AGE = -1;
   @Deprecated
   private static final RuntimeDelegate.HeaderDelegate<NewCookie> DELEGATE = RuntimeDelegate.getInstance().createHeaderDelegate(NewCookie.class);
   private final String comment;
   private final int maxAge;
   private final Date expiry;
   private final boolean secure;
   private final boolean httpOnly;
   private final NewCookie.SameSite sameSite;

   @Deprecated
   public NewCookie(String name, String value) {
      this(name, value, null, null, 1, null, -1, null, false, false, null);
   }

   @Deprecated
   public NewCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure) {
      this(name, value, path, domain, 1, comment, maxAge, null, secure, false, null);
   }

   @Deprecated
   public NewCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure, boolean httpOnly) {
      this(name, value, path, domain, 1, comment, maxAge, null, secure, httpOnly, null);
   }

   @Deprecated
   public NewCookie(String name, String value, String path, String domain, int version, String comment, int maxAge, boolean secure) {
      this(name, value, path, domain, version, comment, maxAge, null, secure, false, null);
   }

   @Deprecated
   public NewCookie(
      String name, String value, String path, String domain, int version, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly
   ) {
      this(name, value, path, domain, version, comment, maxAge, expiry, secure, httpOnly, null);
   }

   @Deprecated
   public NewCookie(
      String name,
      String value,
      String path,
      String domain,
      int version,
      String comment,
      int maxAge,
      Date expiry,
      boolean secure,
      boolean httpOnly,
      NewCookie.SameSite sameSite
   ) {
      super(name, value, path, domain, version);
      this.comment = comment;
      this.maxAge = maxAge;
      this.expiry = expiry;
      this.secure = secure;
      this.httpOnly = httpOnly;
      this.sameSite = sameSite;
   }

   @Deprecated
   public NewCookie(Cookie cookie) {
      this(cookie, null, -1, null, false, false, null);
   }

   @Deprecated
   public NewCookie(Cookie cookie, String comment, int maxAge, boolean secure) {
      this(cookie, comment, maxAge, null, secure, false, null);
   }

   @Deprecated
   public NewCookie(Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly) {
      this(cookie, comment, maxAge, expiry, secure, httpOnly, null);
   }

   @Deprecated
   public NewCookie(Cookie cookie, String comment, int maxAge, Date expiry, boolean secure, boolean httpOnly, NewCookie.SameSite sameSite) {
      super(
         cookie == null ? null : cookie.getName(),
         cookie == null ? null : cookie.getValue(),
         cookie == null ? null : cookie.getPath(),
         cookie == null ? null : cookie.getDomain(),
         cookie == null ? 1 : cookie.getVersion()
      );
      this.comment = comment;
      this.maxAge = maxAge;
      this.expiry = expiry;
      this.secure = secure;
      this.httpOnly = httpOnly;
      this.sameSite = sameSite;
   }

   protected NewCookie(NewCookie.AbstractNewCookieBuilder<?> builder) {
      super(builder);
      this.comment = builder.comment;
      this.maxAge = builder.maxAge;
      this.expiry = builder.expiry;
      this.secure = builder.secure;
      this.httpOnly = builder.httpOnly;
      this.sameSite = builder.sameSite;
   }

   @Deprecated
   public static NewCookie valueOf(String value) {
      return DELEGATE.fromString(value);
   }

   public String getComment() {
      return this.comment;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public Date getExpiry() {
      return this.expiry;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public NewCookie.SameSite getSameSite() {
      return this.sameSite;
   }

   public Cookie toCookie() {
      return new Cookie(this.getName(), this.getValue(), this.getPath(), this.getDomain(), this.getVersion());
   }

   @Deprecated
   @Override
   public String toString() {
      return DELEGATE.toString(this);
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.getName(),
         this.getValue(),
         this.getVersion(),
         this.getPath(),
         this.getDomain(),
         this.comment,
         this.maxAge,
         this.expiry,
         this.secure,
         this.httpOnly,
         this.sameSite
      );
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         NewCookie other = (NewCookie)obj;
         if (!Objects.equals(this.getName(), other.getName())) {
            return false;
         } else if (!Objects.equals(this.getValue(), other.getValue())) {
            return false;
         } else if (this.getVersion() != other.getVersion()) {
            return false;
         } else if (!Objects.equals(this.getPath(), other.getPath())) {
            return false;
         } else if (!Objects.equals(this.getDomain(), other.getDomain())) {
            return false;
         } else if (!Objects.equals(this.comment, other.comment)) {
            return false;
         } else if (this.maxAge != other.maxAge) {
            return false;
         } else if (!Objects.equals(this.expiry, other.expiry)) {
            return false;
         } else if (this.secure != other.secure) {
            return false;
         } else {
            return this.httpOnly != other.httpOnly ? false : this.sameSite == other.sameSite;
         }
      }
   }

   public abstract static class AbstractNewCookieBuilder<T extends NewCookie.AbstractNewCookieBuilder<T>>
      extends Cookie.AbstractCookieBuilder<NewCookie.AbstractNewCookieBuilder<T>> {
      private String comment;
      private int maxAge = -1;
      private Date expiry;
      private boolean secure;
      private boolean httpOnly;
      private NewCookie.SameSite sameSite;

      public AbstractNewCookieBuilder(String name) {
         super(name);
      }

      public AbstractNewCookieBuilder(Cookie cookie) {
         super(cookie == null ? null : cookie.getName());
         if (cookie != null) {
            this.value(cookie.getValue());
            this.path(cookie.getPath());
            this.domain(cookie.getDomain());
            this.version(cookie.getVersion());
         }
      }

      public T comment(String comment) {
         this.comment = comment;
         return this.self();
      }

      public T maxAge(int maxAge) {
         this.maxAge = maxAge;
         return this.self();
      }

      public T expiry(Date expiry) {
         this.expiry = expiry;
         return this.self();
      }

      public T secure(boolean secure) {
         this.secure = secure;
         return this.self();
      }

      public T httpOnly(boolean httpOnly) {
         this.httpOnly = httpOnly;
         return this.self();
      }

      public T sameSite(NewCookie.SameSite sameSite) {
         this.sameSite = sameSite;
         return this.self();
      }

      private T self() {
         return (T)this;
      }

      public abstract NewCookie build();
   }

   public static class Builder extends NewCookie.AbstractNewCookieBuilder<NewCookie.Builder> {
      public Builder(String name) {
         super(name);
      }

      public Builder(Cookie cookie) {
         super(cookie);
      }

      @Override
      public NewCookie build() {
         return new NewCookie(this);
      }
   }

   public static enum SameSite {
      NONE,
      LAX,
      STRICT;
   }
}
