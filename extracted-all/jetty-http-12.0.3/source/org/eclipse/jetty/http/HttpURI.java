package org.eclipse.jetty.http;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.Index.Builder;

public interface HttpURI {
   static HttpURI.Mutable build() {
      return new HttpURI.Mutable();
   }

   static HttpURI.Mutable build(HttpURI uri) {
      return new HttpURI.Mutable(uri);
   }

   static HttpURI.Mutable build(HttpURI uri, String pathQuery) {
      return new HttpURI.Mutable(uri, pathQuery);
   }

   static HttpURI.Mutable build(HttpURI uri, String path, String param, String query) {
      return new HttpURI.Mutable(uri, path, param, query);
   }

   static HttpURI.Mutable build(URI uri) {
      return new HttpURI.Mutable(uri);
   }

   static HttpURI.Mutable build(String uri) {
      return new HttpURI.Mutable(uri);
   }

   static HttpURI.Mutable build(String method, String uri) {
      if (HttpMethod.CONNECT.is(method)) {
         HostPort hostPort = new HostPort(uri);
         return new HttpURI.Mutable(null, hostPort.getHost(), hostPort.getPort(), null);
      } else {
         return uri.startsWith("/") ? build().pathQuery(uri) : build(uri);
      }
   }

   static HttpURI.Immutable from(URI uri) {
      return new HttpURI.Mutable(uri).asImmutable();
   }

   static HttpURI.Immutable from(String uri) {
      return new HttpURI.Mutable(uri).asImmutable();
   }

   static HttpURI.Immutable from(String method, String uri) {
      if (HttpMethod.CONNECT.is(method)) {
         return build().uri(method, uri).asImmutable();
      } else {
         return uri.startsWith("/") ? build().pathQuery(uri).asImmutable() : from(uri);
      }
   }

   static HttpURI.Immutable from(String scheme, HostPort hostPort, String pathQuery) {
      return new HttpURI.Mutable(scheme, hostPort.getHost(), hostPort.getPort(), pathQuery).asImmutable();
   }

   static HttpURI.Immutable from(String scheme, String host, int port, String pathQuery) {
      return new HttpURI.Mutable(scheme, host, port, pathQuery).asImmutable();
   }

   HttpURI.Immutable asImmutable();

   String asString();

   String getAuthority();

   String getDecodedPath();

   String getCanonicalPath();

   String getFragment();

   String getHost();

   String getParam();

   String getPath();

   String getPathQuery();

   int getPort();

   String getQuery();

   String getScheme();

   String getUser();

   boolean hasAuthority();

   boolean isAbsolute();

   boolean isAmbiguous();

   boolean hasViolations();

   boolean hasViolation(UriCompliance.Violation var1);

   Collection<UriCompliance.Violation> getViolations();

   default boolean hasAmbiguousSegment() {
      return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_SEGMENT);
   }

   default boolean hasAmbiguousEmptySegment() {
      return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT);
   }

   default boolean hasAmbiguousSeparator() {
      return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR);
   }

   default boolean hasAmbiguousParameter() {
      return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_PARAMETER);
   }

   default boolean hasAmbiguousEncoding() {
      return this.hasViolation(UriCompliance.Violation.AMBIGUOUS_PATH_ENCODING);
   }

   default boolean hasUtf16Encoding() {
      return this.hasViolation(UriCompliance.Violation.UTF16_ENCODINGS);
   }

   default URI toURI() {
      try {
         String query = this.getQuery();
         return new URI(this.getScheme(), null, this.getHost(), this.getPort(), this.getPath(), query == null ? null : UrlEncoded.decodeString(query), null);
      } catch (URISyntaxException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static class Immutable implements HttpURI, Serializable {
      private static final long serialVersionUID = 2245620284548399386L;
      private final String _scheme;
      private final String _user;
      private final String _host;
      private final int _port;
      private final String _path;
      private final String _param;
      private final String _query;
      private final String _fragment;
      private String _uri;
      private String _canonicalPath;
      private Set<UriCompliance.Violation> _violations;

      private Immutable(HttpURI.Mutable builder) {
         this._scheme = builder._scheme;
         this._user = builder._user;
         this._host = builder._host;
         this._port = builder._port;
         this._path = builder._path;
         this._param = builder._param;
         this._query = builder._query;
         this._fragment = builder._fragment;
         this._uri = builder._uri;
         this._canonicalPath = builder._canonicalPath;
         if (builder._violations != null) {
            this._violations = Collections.unmodifiableSet(EnumSet.copyOf(builder._violations));
         }
      }

      private Immutable(String uri) {
         this._scheme = null;
         this._user = null;
         this._host = null;
         this._port = -1;
         this._path = uri;
         this._param = null;
         this._query = null;
         this._fragment = null;
         this._uri = uri;
         this._canonicalPath = null;
      }

      @Override
      public HttpURI.Immutable asImmutable() {
         return this;
      }

      @Override
      public String asString() {
         if (this._uri == null) {
            StringBuilder out = new StringBuilder();
            if (this._scheme != null) {
               out.append(this._scheme).append(':');
            }

            if (this._host != null) {
               out.append("//");
               if (this._user != null) {
                  out.append(this._user).append('@');
               }

               out.append(this._host);
            }

            if (this._port > 0) {
               out.append(':').append(this._port);
            }

            if (this._path != null) {
               out.append(this._path);
            }

            if (this._query != null) {
               out.append('?').append(this._query);
            }

            if (this._fragment != null) {
               out.append('#').append(this._fragment);
            }

            if (out.length() > 0) {
               this._uri = out.toString();
            } else {
               this._uri = "";
            }
         }

         return this._uri;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof HttpURI) ? false : this.asString().equals(((HttpURI)o).asString());
         }
      }

      @Override
      public String getAuthority() {
         return this._port > 0 ? this._host + ":" + this._port : this._host;
      }

      @Override
      public String getDecodedPath() {
         return URIUtil.decodePath(this.getCanonicalPath());
      }

      @Override
      public String getCanonicalPath() {
         if (this._canonicalPath == null && this._path != null) {
            this._canonicalPath = URIUtil.canonicalPath(this._path);
         }

         return this._canonicalPath;
      }

      @Override
      public String getFragment() {
         return this._fragment;
      }

      @Override
      public String getHost() {
         return this._host != null && this._host.isEmpty() ? null : this._host;
      }

      @Override
      public String getParam() {
         return this._param;
      }

      @Override
      public String getPath() {
         return this._path;
      }

      @Override
      public String getPathQuery() {
         return this._query == null ? this._path : this._path + "?" + this._query;
      }

      @Override
      public int getPort() {
         return this._port;
      }

      @Override
      public String getQuery() {
         return this._query;
      }

      @Override
      public String getScheme() {
         return this._scheme;
      }

      @Override
      public String getUser() {
         return this._user;
      }

      @Override
      public boolean hasAuthority() {
         return this._host != null;
      }

      @Override
      public int hashCode() {
         return this.asString().hashCode();
      }

      @Override
      public boolean isAbsolute() {
         return !StringUtil.isEmpty(this._scheme);
      }

      @Override
      public boolean isAmbiguous() {
         return this._violations != null && UriCompliance.isAmbiguous(this._violations);
      }

      @Override
      public boolean hasViolations() {
         return this._violations != null && !this._violations.isEmpty();
      }

      @Override
      public boolean hasViolation(UriCompliance.Violation violation) {
         return this._violations != null && this._violations.contains(violation);
      }

      @Override
      public Collection<UriCompliance.Violation> getViolations() {
         return this._violations == null ? Collections.emptySet() : this._violations;
      }

      @Override
      public String toString() {
         return this.asString();
      }

      @Override
      public URI toURI() {
         try {
            return new URI(
               this._scheme, null, this._host, this._port, this._path, this._query == null ? null : UrlEncoded.decodeString(this._query), this._fragment
            );
         } catch (URISyntaxException var2) {
            throw new RuntimeException(var2);
         }
      }
   }

   public static class Mutable implements HttpURI {
      private static final Index<Boolean> __ambiguousSegments = new Builder()
         .caseSensitive(false)
         .with(".", Boolean.FALSE)
         .with("%2e", Boolean.TRUE)
         .with("%u002e", Boolean.TRUE)
         .with("..", Boolean.FALSE)
         .with(".%2e", Boolean.TRUE)
         .with(".%u002e", Boolean.TRUE)
         .with("%2e.", Boolean.TRUE)
         .with("%2e%2e", Boolean.TRUE)
         .with("%2e%u002e", Boolean.TRUE)
         .with("%u002e.", Boolean.TRUE)
         .with("%u002e%2e", Boolean.TRUE)
         .with("%u002e%u002e", Boolean.TRUE)
         .build();
      private String _scheme;
      private String _user;
      private String _host;
      private int _port;
      private String _path;
      private String _param;
      private String _query;
      private String _fragment;
      private String _uri;
      private String _canonicalPath;
      private Set<UriCompliance.Violation> _violations;
      private boolean _emptySegment;

      private Mutable() {
      }

      private Mutable(HttpURI uri) {
         this.uri(uri);
      }

      private Mutable(HttpURI baseURI, String pathQuery) {
         this._uri = null;
         this._scheme = baseURI.getScheme();
         this._user = baseURI.getUser();
         this._host = baseURI.getHost();
         this._port = baseURI.getPort();
         if (pathQuery != null) {
            this.parse(HttpURI.Mutable.State.PATH, pathQuery);
         }
      }

      private Mutable(HttpURI baseURI, String path, String param, String query) {
         this._uri = null;
         this._scheme = baseURI.getScheme();
         this._user = baseURI.getUser();
         this._host = baseURI.getHost();
         this._port = baseURI.getPort();
         if (path != null) {
            this.parse(HttpURI.Mutable.State.PATH, path);
         }

         if (param != null) {
            this._param = param;
         }

         if (query != null) {
            this._query = query;
         }
      }

      private Mutable(String uri) {
         this._port = -1;
         this.parse(HttpURI.Mutable.State.START, uri);
      }

      private Mutable(URI uri) {
         this._uri = null;
         this._scheme = uri.getScheme();
         this._host = uri.getHost();
         if (this._host == null && uri.getRawSchemeSpecificPart().startsWith("//")) {
            this._host = "";
         }

         this._port = uri.getPort();
         this._user = uri.getUserInfo();
         String path = uri.getRawPath();
         if (path != null) {
            this.parse(HttpURI.Mutable.State.PATH, path);
         }

         this._query = uri.getRawQuery();
         this._fragment = uri.getRawFragment();
      }

      private Mutable(String scheme, String host, int port, String pathQuery) {
         if (port == HttpScheme.getDefaultPort(scheme)) {
            port = 0;
         }

         this._uri = null;
         this._scheme = scheme;
         this._host = host;
         this._port = port;
         if (pathQuery != null) {
            this.parse(HttpURI.Mutable.State.PATH, pathQuery);
         }
      }

      @Override
      public HttpURI.Immutable asImmutable() {
         return new HttpURI.Immutable(this);
      }

      @Override
      public String asString() {
         return this.asImmutable().toString();
      }

      public HttpURI.Mutable authority(String host, int port) {
         if (host != null && !this.isPathValidForAuthority(this._path)) {
            throw new IllegalArgumentException("Relative path with authority");
         } else {
            this._user = null;
            this._host = host;
            this._port = port;
            this._uri = null;
            return this;
         }
      }

      public HttpURI.Mutable authority(String hostPort) {
         if (hostPort != null && !this.isPathValidForAuthority(this._path)) {
            throw new IllegalArgumentException("Relative path with authority");
         } else {
            HostPort hp = new HostPort(hostPort);
            this._user = null;
            this._host = hp.getHost();
            this._port = hp.getPort();
            this._uri = null;
            return this;
         }
      }

      private boolean isPathValidForAuthority(String path) {
         if (path == null) {
            return true;
         } else {
            return !path.isEmpty() && !"*".equals(path) ? path.startsWith("/") : true;
         }
      }

      public HttpURI.Mutable clear() {
         this._scheme = null;
         this._user = null;
         this._host = null;
         this._port = -1;
         this._path = null;
         this._param = null;
         this._query = null;
         this._fragment = null;
         this._uri = null;
         this._canonicalPath = null;
         this._emptySegment = false;
         if (this._violations != null) {
            this._violations.clear();
         }

         return this;
      }

      public HttpURI.Mutable decodedPath(String path) {
         this._uri = null;
         this._path = URIUtil.encodePath(path);
         this._canonicalPath = URIUtil.canonicalPath(this._path);
         return this;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof HttpURI) ? false : this.asString().equals(((HttpURI)o).asString());
         }
      }

      public HttpURI.Mutable fragment(String fragment) {
         this._fragment = fragment;
         return this;
      }

      @Override
      public String getAuthority() {
         return this._port > 0 ? this._host + ":" + this._port : this._host;
      }

      @Override
      public String getDecodedPath() {
         return URIUtil.decodePath(this.getCanonicalPath());
      }

      @Override
      public String getCanonicalPath() {
         if (this._canonicalPath == null && this._path != null) {
            this._canonicalPath = URIUtil.canonicalPath(this._path);
         }

         return this._canonicalPath;
      }

      @Override
      public String getFragment() {
         return this._fragment;
      }

      @Override
      public String getHost() {
         return this._host;
      }

      @Override
      public String getParam() {
         return this._param;
      }

      @Override
      public String getPath() {
         return this._path;
      }

      @Override
      public String getPathQuery() {
         return this._query == null ? this._path : this._path + "?" + this._query;
      }

      @Override
      public int getPort() {
         return this._port;
      }

      @Override
      public String getQuery() {
         return this._query;
      }

      @Override
      public String getScheme() {
         return this._scheme;
      }

      @Override
      public String getUser() {
         return this._user;
      }

      @Override
      public boolean hasAuthority() {
         return this._host != null;
      }

      @Override
      public int hashCode() {
         return this.asString().hashCode();
      }

      public HttpURI.Mutable host(String host) {
         if (host != null && !this.isPathValidForAuthority(this._path)) {
            throw new IllegalArgumentException("Relative path with authority");
         } else {
            this._host = host;
            this._uri = null;
            return this;
         }
      }

      @Override
      public boolean isAbsolute() {
         return StringUtil.isNotBlank(this._scheme);
      }

      @Override
      public boolean isAmbiguous() {
         return this._violations != null && UriCompliance.isAmbiguous(this._violations);
      }

      @Override
      public boolean hasViolations() {
         return this._violations != null && !this._violations.isEmpty();
      }

      @Override
      public boolean hasViolation(UriCompliance.Violation violation) {
         return this._violations != null && this._violations.contains(violation);
      }

      @Override
      public Collection<UriCompliance.Violation> getViolations() {
         return (Collection<UriCompliance.Violation>)(this._violations == null ? Collections.emptySet() : Collections.unmodifiableCollection(this._violations));
      }

      public HttpURI.Mutable normalize() {
         HttpScheme scheme = this._scheme == null ? null : (HttpScheme)HttpScheme.CACHE.get(this._scheme);
         if (scheme != null && this._port == scheme.getDefaultPort()) {
            this._port = 0;
            this._uri = null;
         }

         return this;
      }

      public HttpURI.Mutable param(String param) {
         this._param = param;
         if (this._path != null && this._param != null) {
            int lastSlash = this._path.lastIndexOf(47);
            if (lastSlash >= 0) {
               int trailingParam = this._path.indexOf(59, lastSlash + 1);
               if (trailingParam >= 0) {
                  this._path = this._path.substring(0, trailingParam);
               }
            }

            this._path = this._path + ";" + this._param;
         }

         this._uri = null;
         return this;
      }

      public HttpURI.Mutable path(String path) {
         if (this.hasAuthority() && !this.isPathValidForAuthority(path)) {
            throw new IllegalArgumentException("Relative path with authority");
         } else if (!URIUtil.isPathValid(path)) {
            throw new IllegalArgumentException("Path not correctly encoded: " + path);
         } else {
            this._uri = null;
            this._path = path;
            this._canonicalPath = null;
            if (this._param != null) {
               if (path.indexOf(59) >= 0) {
                  this._param = null;
               } else {
                  this._path = this._path + ";" + this._param;
               }
            }

            return this;
         }
      }

      public HttpURI.Mutable pathQuery(String pathQuery) {
         if (this.hasAuthority() && !this.isPathValidForAuthority(pathQuery)) {
            throw new IllegalArgumentException("Relative path with authority");
         } else {
            this._uri = null;
            this._path = null;
            this._canonicalPath = null;
            this._param = null;
            this._query = null;
            if (pathQuery != null) {
               this.parse(HttpURI.Mutable.State.PATH, pathQuery);
            }

            return this;
         }
      }

      public HttpURI.Mutable port(int port) {
         this._port = port;
         this._uri = null;
         return this;
      }

      public HttpURI.Mutable query(String query) {
         this._query = query;
         this._uri = null;
         return this;
      }

      public HttpURI.Mutable scheme(HttpScheme scheme) {
         return this.scheme(scheme.asString());
      }

      public HttpURI.Mutable scheme(String scheme) {
         this._scheme = scheme;
         this._uri = null;
         return this;
      }

      @Override
      public String toString() {
         return this.asString();
      }

      @Override
      public URI toURI() {
         try {
            return new URI(this._scheme, null, this._host, this._port, this._path, this._query == null ? null : UrlEncoded.decodeString(this._query), null);
         } catch (URISyntaxException var2) {
            throw new RuntimeException(var2);
         }
      }

      public HttpURI.Mutable uri(HttpURI uri) {
         this._scheme = uri.getScheme();
         this._user = uri.getUser();
         this._host = uri.getHost();
         this._port = uri.getPort();
         this._path = uri.getPath();
         this._param = uri.getParam();
         this._query = uri.getQuery();
         this._uri = null;
         this._canonicalPath = uri.getCanonicalPath();
         Collection<UriCompliance.Violation> violations = uri.getViolations();
         if (!violations.isEmpty()) {
            this._violations = EnumSet.copyOf(violations);
         }

         return this;
      }

      public HttpURI.Mutable uri(String uri) {
         this.clear();
         this._uri = uri;
         this.parse(HttpURI.Mutable.State.START, uri);
         return this;
      }

      public HttpURI.Mutable uri(String method, String uri) {
         if (HttpMethod.CONNECT.is(method)) {
            this.clear();
            this.parse(HttpURI.Mutable.State.HOST, uri);
         } else if (uri.startsWith("/")) {
            this.clear();
            this.pathQuery(uri);
         } else {
            this.uri(uri);
         }

         return this;
      }

      public HttpURI.Mutable uri(String uri, int offset, int length) {
         this.clear();
         int end = offset + length;
         this._uri = uri.substring(offset, end);
         this.parse(HttpURI.Mutable.State.START, uri);
         return this;
      }

      public HttpURI.Mutable user(String user) {
         this._user = user;
         this._uri = null;
         return this;
      }

      private void parse(HttpURI.Mutable.State state, String uri) {
         int mark = 0;
         int pathMark = 0;
         int segment = 0;
         boolean encodedPath = false;
         boolean encodedUtf16 = false;
         int encodedCharacters = 0;
         int encodedValue = 0;
         boolean dot = false;
         int end = uri.length();
         this._emptySegment = false;

         for (int i = 0; i < end; i++) {
            char c = uri.charAt(i);
            switch (state) {
               case START:
                  switch (c) {
                     case '#':
                        this.checkSegment(uri, segment, i, false);
                        this._path = "";
                        mark = i + 1;
                        state = HttpURI.Mutable.State.FRAGMENT;
                        continue;
                     case '%':
                        encodedPath = true;
                        encodedCharacters = 2;
                        encodedValue = 0;
                        segment = i;
                        pathMark = i;
                        mark = i;
                        state = HttpURI.Mutable.State.PATH;
                        continue;
                     case '*':
                        this._path = "*";
                        state = HttpURI.Mutable.State.ASTERISK;
                        continue;
                     case '.':
                        dot = true;
                        segment = i;
                        pathMark = i;
                        state = HttpURI.Mutable.State.PATH;
                        continue;
                     case '/':
                        mark = i;
                        state = HttpURI.Mutable.State.HOST_OR_PATH;
                        continue;
                     case ';':
                        this.checkSegment(uri, segment, i, true);
                        mark = i + 1;
                        state = HttpURI.Mutable.State.PARAM;
                        continue;
                     case '?':
                        this.checkSegment(uri, segment, i, false);
                        this._path = "";
                        mark = i + 1;
                        state = HttpURI.Mutable.State.QUERY;
                        continue;
                     default:
                        mark = i;
                        if (this._scheme == null) {
                           state = HttpURI.Mutable.State.SCHEME_OR_PATH;
                        } else {
                           segment = i;
                           pathMark = i;
                           state = HttpURI.Mutable.State.PATH;
                        }
                        continue;
                  }
               case HOST_OR_PATH:
                  switch (c) {
                     case '#':
                     case '%':
                     case '.':
                     case ';':
                     case '?':
                     case '@':
                        i--;
                        pathMark = mark;
                        segment = mark + 1;
                        state = HttpURI.Mutable.State.PATH;
                        continue;
                     case '/':
                        this._host = "";
                        mark = i + 1;
                        state = HttpURI.Mutable.State.HOST;
                        continue;
                     default:
                        pathMark = mark;
                        segment = mark + 1;
                        state = HttpURI.Mutable.State.PATH;
                        continue;
                  }
               case SCHEME_OR_PATH:
                  switch (c) {
                     case '#':
                        this._path = uri.substring(mark, i);
                        state = HttpURI.Mutable.State.FRAGMENT;
                        continue;
                     case '%':
                        encodedPath = true;
                        encodedCharacters = 2;
                        encodedValue = 0;
                        state = HttpURI.Mutable.State.PATH;
                        continue;
                     case '/':
                        segment = i + 1;
                        state = HttpURI.Mutable.State.PATH;
                        continue;
                     case ':':
                        this._scheme = uri.substring(mark, i);
                        state = HttpURI.Mutable.State.START;
                        continue;
                     case ';':
                        mark = i + 1;
                        state = HttpURI.Mutable.State.PARAM;
                        continue;
                     case '?':
                        this._path = uri.substring(mark, i);
                        mark = i + 1;
                        state = HttpURI.Mutable.State.QUERY;
                     default:
                        continue;
                  }
               case HOST:
                  switch (c) {
                     case '/':
                        this._host = uri.substring(mark, i);
                        mark = i;
                        pathMark = i;
                        segment = i + 1;
                        state = HttpURI.Mutable.State.PATH;
                        continue;
                     case ':':
                        if (i > mark) {
                           this._host = uri.substring(mark, i);
                        }

                        mark = i + 1;
                        state = HttpURI.Mutable.State.PORT;
                        continue;
                     case '@':
                        if (this._user != null) {
                           throw new IllegalArgumentException("Bad authority");
                        }

                        this._user = uri.substring(mark, i);
                        mark = i + 1;
                        continue;
                     case '[':
                        state = HttpURI.Mutable.State.IPV6;
                     default:
                        continue;
                  }
               case IPV6:
                  switch (c) {
                     case '/':
                        throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
                     case ']':
                        c = uri.charAt(++i);
                        this._host = uri.substring(mark, i);
                        if (c == ':') {
                           mark = i + 1;
                           state = HttpURI.Mutable.State.PORT;
                        } else {
                           mark = i;
                           pathMark = i;
                           state = HttpURI.Mutable.State.PATH;
                        }
                     default:
                        continue;
                  }
               case PORT:
                  if (c == '@') {
                     if (this._user != null) {
                        throw new IllegalArgumentException("Bad authority");
                     }

                     this._user = this._host + ":" + uri.substring(mark, i);
                     mark = i + 1;
                     state = HttpURI.Mutable.State.HOST;
                  } else if (c == '/') {
                     this._port = TypeUtil.parseInt(uri, mark, i - mark, 10);
                     mark = i;
                     pathMark = i;
                     segment = i + 1;
                     state = HttpURI.Mutable.State.PATH;
                  }
                  break;
               case PATH:
                  if (encodedCharacters > 0) {
                     if (encodedCharacters == 2 && c == 'u' && !encodedUtf16) {
                        this.addViolation(UriCompliance.Violation.UTF16_ENCODINGS);
                        encodedUtf16 = true;
                        encodedCharacters = 4;
                     } else {
                        encodedValue = (encodedValue << 4) + TypeUtil.convertHexDigit(c);
                        if (--encodedCharacters == 0) {
                           switch (encodedValue) {
                              case 0:
                                 throw new IllegalArgumentException("Illegal character in path");
                              case 37:
                                 this.addViolation(UriCompliance.Violation.AMBIGUOUS_PATH_ENCODING);
                                 break;
                              case 47:
                                 this.addViolation(UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR);
                           }
                        }
                     }
                  } else {
                     switch (c) {
                        case '#':
                           this.checkSegment(uri, segment, i, false);
                           this._path = uri.substring(pathMark, i);
                           mark = i + 1;
                           state = HttpURI.Mutable.State.FRAGMENT;
                           break;
                        case '%':
                           encodedPath = true;
                           encodedUtf16 = false;
                           encodedCharacters = 2;
                           encodedValue = 0;
                           break;
                        case '.':
                           dot |= segment == i;
                           break;
                        case '/':
                           if (i != 0) {
                              this.checkSegment(uri, segment, i, false);
                           }

                           segment = i + 1;
                           break;
                        case ';':
                           this.checkSegment(uri, segment, i, true);
                           mark = i + 1;
                           state = HttpURI.Mutable.State.PARAM;
                           break;
                        case '?':
                           this.checkSegment(uri, segment, i, false);
                           this._path = uri.substring(pathMark, i);
                           mark = i + 1;
                           state = HttpURI.Mutable.State.QUERY;
                     }
                  }
                  break;
               case PARAM:
                  switch (c) {
                     case '#':
                        this._path = uri.substring(pathMark, i);
                        this._param = uri.substring(mark, i);
                        mark = i + 1;
                        state = HttpURI.Mutable.State.FRAGMENT;
                        continue;
                     case '/':
                        encodedPath = true;
                        segment = i + 1;
                        state = HttpURI.Mutable.State.PATH;
                     case ';':
                     default:
                        continue;
                     case '?':
                        this._path = uri.substring(pathMark, i);
                        this._param = uri.substring(mark, i);
                        mark = i + 1;
                        state = HttpURI.Mutable.State.QUERY;
                        continue;
                  }
               case QUERY:
                  if (c == '#') {
                     this._query = uri.substring(mark, i);
                     mark = i + 1;
                     state = HttpURI.Mutable.State.FRAGMENT;
                  }
                  break;
               case FRAGMENT:
                  this._fragment = uri.substring(mark, end);
                  i = end;
                  break;
               case ASTERISK:
                  throw new IllegalArgumentException("Bad character '*'");
               default:
                  throw new IllegalStateException(state.toString());
            }
         }

         switch (state) {
            case START:
               this._path = "";
               this.checkSegment(uri, segment, end, false);
               break;
            case HOST_OR_PATH:
            case SCHEME_OR_PATH:
               this._path = uri.substring(mark, end);
               break;
            case HOST:
               if (end > mark) {
                  this._host = uri.substring(mark, end);
               }
               break;
            case IPV6:
               throw new IllegalArgumentException("No closing ']' for ipv6 in " + uri);
            case PORT:
               this._port = TypeUtil.parseInt(uri, mark, end - mark, 10);
               break;
            case PATH:
               this.checkSegment(uri, segment, end, false);
               this._path = uri.substring(pathMark, end);
               break;
            case PARAM:
               this._path = uri.substring(pathMark, end);
               this._param = uri.substring(mark, end);
               break;
            case QUERY:
               this._query = uri.substring(mark, end);
               break;
            case FRAGMENT:
               this._fragment = uri.substring(mark, end);
            case ASTERISK:
               break;
            default:
               throw new IllegalStateException(state.toString());
         }

         if (!encodedPath && !dot) {
            if (this._param == null) {
               this._canonicalPath = this._path;
            } else {
               this._canonicalPath = this._path.substring(0, this._path.length() - this._param.length() - 1);
            }
         } else if (this._path != null) {
            this._canonicalPath = URIUtil.canonicalPath(this._path, this::onBadUtf8);
            if (this._canonicalPath == null) {
               throw new IllegalArgumentException("Bad URI");
            }
         }
      }

      private RuntimeException onBadUtf8() {
         this.addViolation(UriCompliance.Violation.BAD_UTF8_ENCODING);
         return null;
      }

      private void checkSegment(String uri, int segment, int end, boolean param) {
         if (this._emptySegment) {
            this.addViolation(UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT);
         }

         if (end == segment) {
            if (end >= uri.length() || "#?".indexOf(uri.charAt(end)) >= 0) {
               return;
            }

            if (segment == 0) {
               this.addViolation(UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT);
               return;
            }

            if (!this._emptySegment) {
               this._emptySegment = true;
               return;
            }
         }

         Boolean ambiguous = (Boolean)__ambiguousSegments.get(uri, segment, end - segment);
         if (ambiguous != null) {
            if (Boolean.TRUE.equals(ambiguous)) {
               this.addViolation(UriCompliance.Violation.AMBIGUOUS_PATH_SEGMENT);
            }

            if (param) {
               this.addViolation(UriCompliance.Violation.AMBIGUOUS_PATH_PARAMETER);
            }
         }
      }

      private void addViolation(UriCompliance.Violation violation) {
         if (this._violations == null) {
            this._violations = EnumSet.of(violation);
         } else {
            this._violations.add(violation);
         }
      }

      private static enum State {
         START,
         HOST_OR_PATH,
         SCHEME_OR_PATH,
         HOST,
         IPV6,
         PORT,
         PATH,
         PARAM,
         QUERY,
         FRAGMENT,
         ASTERISK;
      }
   }
}
