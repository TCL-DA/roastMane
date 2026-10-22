package org.eclipse.jetty.http;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.thread.AutoLock;

public interface HttpCookieStore {
   boolean add(URI var1, HttpCookie var2);

   List<HttpCookie> all();

   List<HttpCookie> match(URI var1);

   boolean remove(URI var1, HttpCookie var2);

   boolean clear();

   public static class Default implements HttpCookieStore {
      private final AutoLock lock = new AutoLock();
      private final Map<String, List<HttpCookieStore.Default.StoredHttpCookie>> cookies = new HashMap<>();

      @Override
      public boolean add(URI uri, HttpCookie cookie) {
         String resolvedDomain = this.resolveDomain(uri, cookie);
         if (resolvedDomain == null) {
            return false;
         } else {
            String resolvedPath = this.resolvePath(uri, cookie);
            boolean[] added = new boolean[1];
            HttpCookieStore.Default.StoredHttpCookie storedCookie = new HttpCookieStore.Default.StoredHttpCookie(cookie, uri, resolvedDomain, resolvedPath);
            AutoLock ignored = this.lock.lock();

            try {
               String key = resolvedDomain.toLowerCase(Locale.ENGLISH);
               this.cookies.compute(key, (k, v) -> {
                  if (v != null) {
                     v.remove(storedCookie);
                  }

                  if (!cookie.isExpired()) {
                     added[0] = true;
                     if (v == null) {
                        v = new ArrayList<>();
                     }

                     v.add(storedCookie);
                     return v;
                  } else {
                     return v != null && !v.isEmpty() ? v : null;
                  }
               });
            } catch (Throwable var11) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (ignored != null) {
               ignored.close();
            }

            return added[0];
         }
      }

      private String resolveDomain(URI uri, HttpCookie cookie) {
         String uriDomain = uri.getHost();
         if (uriDomain == null) {
            return null;
         } else {
            String cookieDomain = cookie.getDomain();
            if (cookieDomain == null) {
               return uriDomain;
            } else {
               String resolvedDomain = cookieDomain;
               if (cookieDomain.startsWith(".")) {
                  resolvedDomain = cookieDomain.substring(1);
               }

               if (resolvedDomain.endsWith(".")) {
                  resolvedDomain = uriDomain;
               }

               if (!this.allowDomain(resolvedDomain)) {
                  return null;
               } else {
                  return !isSameOrSubDomain(uriDomain, resolvedDomain) ? null : resolvedDomain;
               }
            }
         }
      }

      private String resolvePath(URI uri, HttpCookie cookie) {
         String resolvedPath = cookie.getPath();
         if (resolvedPath == null || !resolvedPath.startsWith("/")) {
            String uriPath = uri.getRawPath();
            if (!StringUtil.isBlank(uriPath) && uriPath.startsWith("/")) {
               int lastSlash = uriPath.lastIndexOf(47);
               resolvedPath = uriPath.substring(0, lastSlash);
               if (resolvedPath.isEmpty()) {
                  resolvedPath = "/";
               }
            } else {
               resolvedPath = "/";
            }
         }

         return resolvedPath;
      }

      protected boolean allowDomain(String domain) {
         if (domain.endsWith(".")) {
            domain = domain.substring(0, domain.length() - 1);
         }

         if (domain.contains(".")) {
            return true;
         } else {
            return domain.equals("localhost") ? true : domain.startsWith("[") && domain.endsWith("]");
         }
      }

      @Override
      public List<HttpCookie> all() {
         AutoLock ignored = this.lock.lock();

         List var2;
         try {
            var2 = this.cookies
               .values()
               .stream()
               .flatMap(Collection::stream)
               .filter(Predicate.not(HttpCookieStore.Default.StoredHttpCookie::isExpired))
               .map(HttpCookie.class::cast)
               .toList();
         } catch (Throwable var5) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var2;
      }

      @Override
      public List<HttpCookie> match(URI uri) {
         String uriDomain = uri.getHost();
         if (uriDomain == null) {
            return List.of();
         } else {
            String path = uri.getRawPath();
            if (path == null || path.isBlank()) {
               path = "/";
            }

            boolean secure = HttpScheme.isSecure(uri.getScheme());
            List<HttpCookie> result = new ArrayList<>();
            AutoLock ignored = this.lock.lock();

            try {
               for (String domain = uriDomain.toLowerCase(Locale.ENGLISH); domain != null; domain = this.parentDomain(domain)) {
                  List<HttpCookieStore.Default.StoredHttpCookie> stored = this.cookies.get(domain);
                  Iterator<HttpCookieStore.Default.StoredHttpCookie> iterator = stored == null ? Collections.emptyIterator() : stored.iterator();

                  while (iterator.hasNext()) {
                     HttpCookieStore.Default.StoredHttpCookie cookie = iterator.next();
                     if (cookie.isExpired()) {
                        iterator.remove();
                     } else if ((!cookie.isSecure() || secure)
                        && domainMatches(uriDomain, cookie.domain, cookie.getWrapped().getDomain())
                        && pathMatches(path, cookie.path)) {
                        result.add(cookie);
                     }
                  }
               }
            } catch (Throwable var12) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (ignored != null) {
               ignored.close();
            }

            return result;
         }
      }

      private static boolean domainMatches(String uriDomain, String domain, String cookieDomain) {
         return cookieDomain != null && !cookieDomain.endsWith(".") ? isSameOrSubDomain(uriDomain, cookieDomain) : uriDomain.equalsIgnoreCase(domain);
      }

      private static boolean isSameOrSubDomain(String subDomain, String domain) {
         int subDomainLength = subDomain.length();
         int domainLength = domain.length();
         if (!subDomain.regionMatches(true, subDomainLength - domainLength, domain, 0, domainLength)) {
            return false;
         } else {
            int beforeMatch = subDomainLength - domainLength - 1;
            return beforeMatch < 0 ? true : subDomain.charAt(beforeMatch) == '.';
         }
      }

      private static boolean pathMatches(String uriPath, String cookiePath) {
         if (cookiePath == null) {
            return true;
         } else if (uriPath.equals(cookiePath)) {
            return true;
         } else {
            return !uriPath.startsWith(cookiePath) ? false : cookiePath.endsWith("/") || uriPath.charAt(cookiePath.length()) == '/';
         }
      }

      @Override
      public boolean remove(URI uri, HttpCookie cookie) {
         String uriDomain = uri.getHost();
         if (uriDomain == null) {
            return false;
         } else {
            String resolvedPath = this.resolvePath(uri, cookie);
            boolean[] removed = new boolean[1];
            AutoLock ignored = this.lock.lock();

            try {
               for (String domain = uriDomain.toLowerCase(Locale.ENGLISH); domain != null; domain = this.parentDomain(domain)) {
                  this.cookies
                     .compute(
                        domain,
                        (k, v) -> {
                           if (v == null) {
                              return null;
                           } else {
                              Iterator<HttpCookieStore.Default.StoredHttpCookie> iterator = v.iterator();

                              while (iterator.hasNext()) {
                                 HttpCookieStore.Default.StoredHttpCookie storedCookie = iterator.next();
                                 if (uriDomain.equalsIgnoreCase(storedCookie.uri.getHost())
                                    && storedCookie.path.equals(resolvedPath)
                                    && storedCookie.getWrapped().getName().equals(cookie.getName())) {
                                    iterator.remove();
                                    removed[0] = true;
                                 }
                              }

                              return v.isEmpty() ? null : v;
                           }
                        }
                     );
               }
            } catch (Throwable var10) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (ignored != null) {
               ignored.close();
            }

            return removed[0];
         }
      }

      private String parentDomain(String domain) {
         int dot = domain.indexOf(46);
         if (dot < 0) {
            return null;
         } else {
            domain = domain.substring(dot + 1);
            return domain.indexOf(46) < 0 ? null : domain;
         }
      }

      @Override
      public boolean clear() {
         AutoLock ignored = this.lock.lock();

         boolean var6;
         label43: {
            try {
               if (this.cookies.isEmpty()) {
                  var6 = false;
                  break label43;
               }

               this.cookies.clear();
               var6 = true;
            } catch (Throwable var5) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (ignored != null) {
               ignored.close();
            }

            return var6;
         }

         if (ignored != null) {
            ignored.close();
         }

         return var6;
      }

      private static class StoredHttpCookie extends HttpCookie.Wrapper {
         private final long creationNanoTime = NanoTime.now();
         private final URI uri;
         private final String domain;
         private final String path;

         private StoredHttpCookie(HttpCookie wrapped, URI uri, String domain, String path) {
            super(wrapped);
            this.uri = Objects.requireNonNull(uri);
            this.domain = Objects.requireNonNull(domain);
            this.path = Objects.requireNonNull(path);
         }

         @Override
         public boolean isExpired() {
            long maxAge = this.getMaxAge();
            if (maxAge >= 0L && NanoTime.secondsSince(this.creationNanoTime) > maxAge) {
               return true;
            } else {
               Instant expires = this.getExpires();
               return expires != null && Instant.now().isAfter(expires);
            }
         }

         @Override
         public int hashCode() {
            return Objects.hash(this.getWrapped().getName(), this.domain.toLowerCase(Locale.ENGLISH), this.path);
         }

         @Override
         public boolean equals(Object obj) {
            if (this == obj) {
               return true;
            } else {
               return !(obj instanceof HttpCookieStore.Default.StoredHttpCookie that)
                  ? false
                  : this.getName().equals(that.getName()) && this.domain.equalsIgnoreCase(that.domain) && this.path.equals(that.path);
            }
         }
      }
   }

   public static class Empty implements HttpCookieStore {
      @Override
      public boolean add(URI uri, HttpCookie cookie) {
         return false;
      }

      @Override
      public List<HttpCookie> all() {
         return List.of();
      }

      @Override
      public List<HttpCookie> match(URI uri) {
         return List.of();
      }

      @Override
      public boolean remove(URI uri, HttpCookie cookie) {
         return false;
      }

      @Override
      public boolean clear() {
         return false;
      }
   }
}
