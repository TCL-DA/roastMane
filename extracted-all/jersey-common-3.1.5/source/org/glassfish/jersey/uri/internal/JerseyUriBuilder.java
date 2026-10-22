package org.glassfish.jersey.uri.internal;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriBuilderException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.guava.InetAddresses;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.uri.JerseyQueryParamStyle;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriTemplate;

public class JerseyUriBuilder extends UriBuilder {
   private String scheme;
   private String ssp;
   private String authority;
   private String userInfo;
   private String host;
   private String port;
   private final StringBuilder path;
   private MultivaluedMap<String, String> matrixParams;
   private final StringBuilder query;
   private MultivaluedMap<String, String> queryParams;
   private JerseyQueryParamStyle queryParamStyle;
   private String fragment;

   public JerseyUriBuilder() {
      this.path = new StringBuilder();
      this.query = new StringBuilder();
      this.queryParamStyle = JerseyQueryParamStyle.MULTI_PAIRS;
   }

   private JerseyUriBuilder(JerseyUriBuilder that) {
      this.scheme = that.scheme;
      this.ssp = that.ssp;
      this.authority = that.authority;
      this.userInfo = that.userInfo;
      this.host = that.host;
      this.port = that.port;
      this.path = new StringBuilder(that.path);
      this.matrixParams = that.matrixParams == null ? null : new MultivaluedStringMap(that.matrixParams);
      this.query = new StringBuilder(that.query);
      this.queryParams = that.queryParams == null ? null : new MultivaluedStringMap(that.queryParams);
      this.fragment = that.fragment;
      this.queryParamStyle = that.queryParamStyle;
   }

   public JerseyUriBuilder clone() {
      return new JerseyUriBuilder(this);
   }

   public JerseyUriBuilder uri(URI uri) {
      if (uri == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("uri"));
      } else {
         if (uri.getRawFragment() != null) {
            this.fragment = uri.getRawFragment();
         }

         if (uri.isOpaque()) {
            this.scheme = uri.getScheme();
            this.ssp = uri.getRawSchemeSpecificPart();
            return this;
         } else {
            if (uri.getScheme() == null) {
               if (this.ssp != null && uri.getRawSchemeSpecificPart() != null) {
                  this.ssp = uri.getRawSchemeSpecificPart();
                  return this;
               }
            } else {
               this.scheme = uri.getScheme();
            }

            this.ssp = null;
            if (uri.getRawAuthority() != null) {
               if (uri.getRawUserInfo() == null && uri.getHost() == null && uri.getPort() == -1) {
                  this.authority = uri.getRawAuthority();
                  this.userInfo = null;
                  this.host = null;
                  this.port = null;
               } else {
                  this.authority = null;
                  if (uri.getRawUserInfo() != null) {
                     this.userInfo = uri.getRawUserInfo();
                  }

                  if (uri.getHost() != null) {
                     this.host = uri.getHost();
                  }

                  if (uri.getPort() != -1) {
                     this.port = String.valueOf(uri.getPort());
                  }
               }
            }

            if (uri.getRawPath() != null && !uri.getRawPath().isEmpty()) {
               this.path.setLength(0);
               this.path.append(uri.getRawPath());
            }

            if (uri.getRawQuery() != null && !uri.getRawQuery().isEmpty()) {
               this.query.setLength(0);
               this.query.append(uri.getRawQuery());
            }

            return this;
         }
      }
   }

   public JerseyUriBuilder uri(String uriTemplate) {
      if (uriTemplate == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("uriTemplate"));
      } else {
         UriParser parser = new UriParser(uriTemplate);
         parser.parse();
         String parsedScheme = parser.getScheme();
         if (parsedScheme != null) {
            this.scheme(parsedScheme);
         } else if (this.ssp != null) {
            this.ssp = null;
            parser = new UriParser(this.scheme + ":" + uriTemplate);
            parser.parse();
         }

         this.schemeSpecificPart(parser);
         String parserFragment = parser.getFragment();
         if (parserFragment != null) {
            this.fragment(parserFragment);
         }

         return this;
      }
   }

   private void schemeSpecificPart(UriParser parser) {
      if (parser.isOpaque()) {
         if (parser.getSsp() != null) {
            this.authority = this.host = this.port = null;
            this.path.setLength(0);
            this.query.setLength(0);
            this.ssp = parser.getSsp();
         }
      } else {
         this.ssp = null;
         if (parser.getAuthority() != null) {
            if (parser.getUserInfo() == null && parser.getHost() == null && parser.getPort() == null) {
               this.authority = this.encode(parser.getAuthority(), UriComponent.Type.AUTHORITY);
               this.userInfo = null;
               this.host = null;
               this.port = null;
            } else {
               this.authority = null;
               if (parser.getUserInfo() != null) {
                  this.userInfo(parser.getUserInfo());
               }

               if (parser.getHost() != null) {
                  this.host(parser.getHost());
               }

               if (parser.getPort() != null) {
                  this.port = parser.getPort();
               }
            }
         }

         if (parser.getPath() != null) {
            this.path.setLength(0);
            this.path(parser.getPath());
         }

         if (parser.getQuery() != null) {
            this.query.setLength(0);
            this.query.append(parser.getQuery());
         }
      }
   }

   public JerseyUriBuilder scheme(String scheme) {
      if (scheme != null) {
         this.scheme = scheme;
         UriComponent.validate(scheme, UriComponent.Type.SCHEME, true);
      } else {
         this.scheme = null;
      }

      return this;
   }

   public JerseyUriBuilder schemeSpecificPart(String ssp) {
      if (ssp == null) {
         throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEME_PART_NULL());
      } else {
         UriParser parser = new UriParser(this.scheme != null ? this.scheme + ":" + ssp : ssp);
         parser.parse();
         if (parser.getScheme() != null && !parser.getScheme().equals(this.scheme)) {
            throw new IllegalStateException(LocalizationMessages.URI_BUILDER_SCHEME_PART_UNEXPECTED_COMPONENT(ssp, parser.getScheme()));
         } else if (parser.getFragment() != null) {
            throw new IllegalStateException(LocalizationMessages.URI_BUILDER_URI_PART_FRAGMENT(ssp, parser.getFragment()));
         } else {
            this.schemeSpecificPart(parser);
            return this;
         }
      }
   }

   public JerseyUriBuilder userInfo(String ui) {
      this.checkSsp();
      this.userInfo = ui != null ? this.encode(ui, UriComponent.Type.USER_INFO) : null;
      return this;
   }

   public JerseyUriBuilder host(String host) {
      this.checkSsp();
      if (host != null) {
         if (host.isEmpty()) {
            throw new IllegalArgumentException(LocalizationMessages.INVALID_HOST());
         }

         if (!InetAddresses.isMappedIPv4Address(host) && !InetAddresses.isUriInetAddress(host)) {
            this.host = this.encode(host, UriComponent.Type.HOST);
         } else {
            this.host = host;
         }
      } else {
         this.host = null;
      }

      return this;
   }

   public JerseyUriBuilder port(int port) {
      this.checkSsp();
      if (port < -1) {
         throw new IllegalArgumentException(LocalizationMessages.INVALID_PORT());
      } else {
         this.port = port == -1 ? null : String.valueOf(port);
         return this;
      }
   }

   public JerseyUriBuilder replacePath(String path) {
      this.checkSsp();
      this.path.setLength(0);
      if (path != null) {
         this.appendPath(path);
      }

      return this;
   }

   public JerseyUriBuilder path(String path) {
      this.checkSsp();
      this.appendPath(path);
      return this;
   }

   public UriBuilder path(Class resource) throws IllegalArgumentException {
      this.checkSsp();
      if (resource == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("resource"));
      } else {
         Path p = Path.class.cast(resource.getAnnotation(Path.class));
         if (p == null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_CLASS_PATH_ANNOTATION_MISSING(resource));
         } else {
            this.appendPath(p);
            return this;
         }
      }
   }

   public JerseyUriBuilder path(Class resource, String methodName) {
      this.checkSsp();
      if (resource == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("resource"));
      } else if (methodName == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("methodName"));
      } else {
         Method[] methods = AccessController.doPrivileged(ReflectionHelper.getMethodsPA(resource));
         Method found = null;

         for (Method m : methods) {
            if (methodName.equals(m.getName())) {
               if (found != null && !found.isSynthetic()) {
                  if (!m.isSynthetic()) {
                     throw new IllegalArgumentException();
                  }
               } else {
                  found = m;
               }
            }
         }

         if (found == null) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_METHODNAME_NOT_SPECIFIED(methodName, resource));
         } else {
            this.appendPath(this.getPath(found));
            return this;
         }
      }
   }

   public JerseyUriBuilder path(Method method) {
      this.checkSsp();
      if (method == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("method"));
      } else {
         this.appendPath(this.getPath(method));
         return this;
      }
   }

   private Path getPath(AnnotatedElement ae) {
      Path p = ae.getAnnotation(Path.class);
      if (p == null) {
         throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_ANNOTATEDELEMENT_PATH_ANNOTATION_MISSING(ae));
      } else {
         return p;
      }
   }

   public JerseyUriBuilder segment(String... segments) throws IllegalArgumentException {
      this.checkSsp();
      if (segments == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("segments"));
      } else {
         for (String segment : segments) {
            this.appendPath(segment, true);
         }

         return this;
      }
   }

   public JerseyUriBuilder replaceMatrix(String matrix) {
      this.checkSsp();
      boolean trailingSlash = this.path.charAt(this.path.length() - 1) == '/';
      int slashIndex = trailingSlash ? this.path.lastIndexOf("/", this.path.length() - 2) : this.path.lastIndexOf("/");
      int i = this.path.indexOf(";", slashIndex);
      if (i != -1) {
         this.path.setLength(i + 1);
      } else if (matrix != null) {
         this.path.append(';');
      }

      if (matrix != null) {
         this.path.append(this.encode(matrix, UriComponent.Type.PATH));
      } else if (i != -1) {
         this.path.setLength(i);
         if (trailingSlash) {
            this.path.append("/");
         }
      }

      return this;
   }

   public JerseyUriBuilder matrixParam(String name, Object... values) {
      this.checkSsp();
      if (name == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
      } else if (values == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("value"));
      } else if (values.length == 0) {
         return this;
      } else {
         name = this.encode(name, UriComponent.Type.MATRIX_PARAM);
         if (this.matrixParams == null) {
            for (Object value : values) {
               this.path.append(';').append(name);
               if (value == null) {
                  throw new IllegalArgumentException(LocalizationMessages.MATRIX_PARAM_NULL());
               }

               String stringValue = value.toString();
               if (!stringValue.isEmpty()) {
                  this.path.append('=').append(this.encode(stringValue, UriComponent.Type.MATRIX_PARAM));
               }
            }
         } else {
            for (Object value : values) {
               if (value == null) {
                  throw new IllegalArgumentException(LocalizationMessages.MATRIX_PARAM_NULL());
               }

               this.matrixParams.add(name, this.encode(value.toString(), UriComponent.Type.MATRIX_PARAM));
            }
         }

         return this;
      }
   }

   public JerseyUriBuilder replaceMatrixParam(String name, Object... values) {
      this.checkSsp();
      if (name == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
      } else {
         if (this.matrixParams == null) {
            int i = this.path.lastIndexOf("/");
            if (i == -1) {
               i = 0;
            }

            this.matrixParams = UriComponent.decodeMatrix(this.path.substring(i), false);
            i = this.path.indexOf(";", i);
            if (i != -1) {
               this.path.setLength(i);
            }
         }

         name = this.encode(name, UriComponent.Type.MATRIX_PARAM);
         this.matrixParams.remove(name);
         if (values != null) {
            for (Object value : values) {
               if (value == null) {
                  throw new IllegalArgumentException(LocalizationMessages.MATRIX_PARAM_NULL());
               }

               this.matrixParams.add(name, this.encode(value.toString(), UriComponent.Type.MATRIX_PARAM));
            }
         }

         return this;
      }
   }

   public JerseyUriBuilder replaceQuery(String query) {
      this.checkSsp();
      this.query.setLength(0);
      if (query != null) {
         this.query.append(this.encode(query, UriComponent.Type.QUERY));
      }

      return this;
   }

   public JerseyUriBuilder queryParam(String name, Object... values) {
      this.checkSsp();
      if (name == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
      } else if (values == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("values"));
      } else if (values.length == 0) {
         return this;
      } else {
         name = this.encode(name, UriComponent.Type.QUERY_PARAM);
         List<String> stringsValues = Stream.of(values)
            .map(this::convertToString)
            .map(value -> this.encode(value, UriComponent.Type.QUERY_PARAM))
            .collect(Collectors.toList());
         switch (this.queryParamStyle) {
            case ARRAY_PAIRS:
               this.clientQueryParamArrayPairs(name, stringsValues);
               break;
            case COMMA_SEPARATED:
               this.clientQueryParamCommaSeparated(name, stringsValues);
               break;
            default:
               this.clientQueryParamMultiPairs(name, stringsValues);
         }

         return this;
      }
   }

   private String convertToString(Object value) {
      if (value == null) {
         throw new IllegalArgumentException(LocalizationMessages.QUERY_PARAM_NULL());
      } else {
         return value.toString();
      }
   }

   private void clientQueryParamMultiPairs(String name, List<String> values) {
      if (this.queryParams == null) {
         for (String value : values) {
            if (this.query.length() > 0) {
               this.query.append('&');
            }

            this.query.append(name).append('=').append(value);
         }
      } else {
         for (String value : values) {
            this.queryParams.add(name, value);
         }
      }
   }

   private void clientQueryParamCommaSeparated(String name, List<String> values) throws IllegalArgumentException {
      if (this.queryParams == null) {
         if (this.query.length() > 0) {
            this.query.append('&');
         }

         this.query.append(name).append('=').append(String.join(",", values));
      } else {
         this.queryParams.add(name, String.join(",", values));
      }
   }

   private void clientQueryParamArrayPairs(String name, List<String> values) throws IllegalArgumentException {
      if (this.queryParams == null) {
         for (String value : values) {
            if (this.query.length() > 0) {
               this.query.append('&');
            }

            this.query.append(name).append("[]").append('=').append(value);
         }
      } else {
         for (String value : values) {
            this.queryParams.add(name + "[]", value);
         }
      }
   }

   public JerseyUriBuilder setQueryParamStyle(JerseyQueryParamStyle queryParamStyle) {
      this.queryParamStyle = Objects.requireNonNull(queryParamStyle);
      return this;
   }

   public JerseyUriBuilder replaceQueryParam(String name, Object... values) {
      this.checkSsp();
      if (this.queryParams == null) {
         this.queryParams = UriComponent.decodeQuery(this.query.toString(), false, false);
         this.query.setLength(0);
      }

      name = this.encode(name, UriComponent.Type.QUERY_PARAM);
      this.queryParams.remove(name);
      if (values == null) {
         return this;
      } else {
         for (Object value : values) {
            if (value == null) {
               throw new IllegalArgumentException(LocalizationMessages.QUERY_PARAM_NULL());
            }

            this.queryParams.add(name, this.encode(value.toString(), UriComponent.Type.QUERY_PARAM));
         }

         return this;
      }
   }

   public JerseyUriBuilder resolveTemplate(String name, Object value) throws IllegalArgumentException {
      this.resolveTemplate(name, value, true, true);
      return this;
   }

   public JerseyUriBuilder resolveTemplate(String name, Object value, boolean encodeSlashInPath) {
      this.resolveTemplate(name, value, true, encodeSlashInPath);
      return this;
   }

   public JerseyUriBuilder resolveTemplateFromEncoded(String name, Object value) {
      this.resolveTemplate(name, value, false, false);
      return this;
   }

   private JerseyUriBuilder resolveTemplate(String name, Object value, boolean encode, boolean encodeSlashInPath) {
      if (name == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("name"));
      } else if (value == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("value"));
      } else {
         Map<String, Object> templateValues = new HashMap<>();
         templateValues.put(name, value);
         this.resolveTemplates(templateValues, encode, encodeSlashInPath);
         return this;
      }
   }

   public JerseyUriBuilder resolveTemplates(Map<String, Object> templateValues) throws IllegalArgumentException {
      this.resolveTemplates(templateValues, true, true);
      return this;
   }

   public JerseyUriBuilder resolveTemplates(Map<String, Object> templateValues, boolean encodeSlashInPath) throws IllegalArgumentException {
      this.resolveTemplates(templateValues, true, encodeSlashInPath);
      return this;
   }

   public JerseyUriBuilder resolveTemplatesFromEncoded(Map<String, Object> templateValues) {
      this.resolveTemplates(templateValues, false, false);
      return this;
   }

   private JerseyUriBuilder resolveTemplates(Map<String, Object> templateValues, boolean encode, boolean encodeSlashInPath) {
      if (templateValues == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("templateValues"));
      } else {
         for (Entry entry : templateValues.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
               throw new IllegalArgumentException(LocalizationMessages.TEMPLATE_PARAM_NULL());
            }
         }

         this.scheme = UriTemplate.resolveTemplateValues(UriComponent.Type.SCHEME, this.scheme, false, templateValues);
         this.userInfo = UriTemplate.resolveTemplateValues(UriComponent.Type.USER_INFO, this.userInfo, encode, templateValues);
         this.host = UriTemplate.resolveTemplateValues(UriComponent.Type.HOST, this.host, encode, templateValues);
         this.port = UriTemplate.resolveTemplateValues(UriComponent.Type.PORT, this.port, false, templateValues);
         this.authority = UriTemplate.resolveTemplateValues(UriComponent.Type.AUTHORITY, this.authority, encode, templateValues);
         UriComponent.Type pathComponent = encodeSlashInPath ? UriComponent.Type.PATH_SEGMENT : UriComponent.Type.PATH;
         String newPath = UriTemplate.resolveTemplateValues(pathComponent, this.path.toString(), encode, templateValues);
         this.path.setLength(0);
         this.path.append(newPath);
         String newQuery = UriTemplate.resolveTemplateValues(UriComponent.Type.QUERY_PARAM, this.query.toString(), encode, templateValues);
         this.query.setLength(0);
         this.query.append(newQuery);
         this.fragment = UriTemplate.resolveTemplateValues(UriComponent.Type.FRAGMENT, this.fragment, encode, templateValues);
         return this;
      }
   }

   public JerseyUriBuilder fragment(String fragment) {
      this.fragment = fragment != null ? this.encode(fragment, UriComponent.Type.FRAGMENT) : null;
      return this;
   }

   private void checkSsp() {
      if (this.ssp != null) {
         throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEMA_PART_OPAQUE());
      }
   }

   private void appendPath(Path path) {
      if (path == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("path"));
      } else {
         this.appendPath(path.value());
      }
   }

   private void appendPath(String path) {
      this.appendPath(path, false);
   }

   private void appendPath(String segments, boolean isSegment) {
      if (segments == null) {
         throw new IllegalArgumentException(LocalizationMessages.PARAM_NULL("segments"));
      } else if (!segments.isEmpty()) {
         this.encodeMatrix();
         segments = this.encode(segments, isSegment ? UriComponent.Type.PATH_SEGMENT : UriComponent.Type.PATH);
         boolean pathEndsInSlash = this.path.length() > 0 && this.path.charAt(this.path.length() - 1) == '/';
         boolean segmentStartsWithSlash = segments.charAt(0) == '/';
         if (this.path.length() > 0 && !pathEndsInSlash && !segmentStartsWithSlash) {
            this.path.append('/');
         } else if (pathEndsInSlash && segmentStartsWithSlash) {
            segments = segments.substring(1);
            if (segments.isEmpty()) {
               return;
            }
         }

         this.path.append(segments);
      }
   }

   private void encodeMatrix() {
      if (this.matrixParams != null && !this.matrixParams.isEmpty()) {
         for (Entry<String, List<String>> e : this.matrixParams.entrySet()) {
            String name = e.getKey();

            for (String value : e.getValue()) {
               this.path.append(';').append(name);
               if (!value.isEmpty()) {
                  this.path.append('=').append(value);
               }
            }
         }

         this.matrixParams = null;
      }
   }

   private void encodeQuery() {
      if (this.queryParams != null && !this.queryParams.isEmpty()) {
         for (Entry<String, List<String>> e : this.queryParams.entrySet()) {
            String name = e.getKey();

            for (String value : e.getValue()) {
               if (this.query.length() > 0) {
                  this.query.append('&');
               }

               this.query.append(name).append('=').append(value);
            }
         }

         this.queryParams = null;
      }
   }

   private String encode(String s, UriComponent.Type type) {
      return UriComponent.contextualEncode(s, type, true);
   }

   public URI buildFromMap(Map<String, ?> values) {
      return this._buildFromMap(true, true, values);
   }

   public URI buildFromMap(Map<String, ?> values, boolean encodeSlashInPath) {
      return this._buildFromMap(true, encodeSlashInPath, values);
   }

   public URI buildFromEncodedMap(Map<String, ?> values) throws IllegalArgumentException, UriBuilderException {
      return this._buildFromMap(false, false, values);
   }

   private URI _buildFromMap(boolean encode, boolean encodeSlashInPath, Map<String, ?> values) {
      if (this.ssp != null) {
         throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEMA_PART_OPAQUE());
      } else {
         this.encodeMatrix();
         this.encodeQuery();
         String uri = UriTemplate.createURI(
            this.scheme,
            this.authority,
            this.userInfo,
            this.host,
            this.port,
            this.path.toString(),
            this.query.toString(),
            this.fragment,
            values,
            encode,
            encodeSlashInPath
         );
         return this.createURI(uri);
      }
   }

   public URI build(Object... values) {
      return this._build(true, true, values);
   }

   public URI build(Object[] values, boolean encodeSlashInPath) {
      return this._build(true, encodeSlashInPath, values);
   }

   public URI buildFromEncoded(Object... values) {
      return this._build(false, false, values);
   }

   public String toTemplate() {
      this.encodeMatrix();
      this.encodeQuery();
      StringBuilder sb = new StringBuilder();
      if (this.scheme != null) {
         sb.append(this.scheme).append(':');
      }

      if (this.ssp != null) {
         sb.append(this.ssp);
      } else {
         boolean hasAuthority = false;
         if (this.userInfo != null || this.host != null || this.port != null) {
            hasAuthority = true;
            sb.append("//");
            if (this.userInfo != null && !this.userInfo.isEmpty()) {
               sb.append(this.userInfo).append('@');
            }

            if (this.host != null) {
               sb.append(this.host);
            }

            if (this.port != null) {
               sb.append(':').append(this.port);
            }
         } else if (this.authority != null) {
            hasAuthority = true;
            sb.append("//").append(this.authority);
         }

         if (this.path.length() > 0) {
            if (hasAuthority && this.path.charAt(0) != '/') {
               sb.append("/");
            }

            sb.append((CharSequence)this.path);
         } else if (hasAuthority && (this.query.length() > 0 || this.fragment != null && !this.fragment.isEmpty())) {
            sb.append("/");
         }

         if (this.query.length() > 0) {
            sb.append('?').append((CharSequence)this.query);
         }
      }

      if (this.fragment != null && !this.fragment.isEmpty()) {
         sb.append('#').append(this.fragment);
      }

      return sb.toString();
   }

   private URI _build(boolean encode, boolean encodeSlashInPath, Object... values) {
      if (this.ssp != null) {
         if (values != null && values.length != 0) {
            throw new IllegalArgumentException(LocalizationMessages.URI_BUILDER_SCHEMA_PART_OPAQUE());
         } else {
            return this.createURI(this.create());
         }
      } else {
         this.encodeMatrix();
         this.encodeQuery();
         if (this.queryParamStyle == JerseyQueryParamStyle.COMMA_SEPARATED) {
            this.groupQueryParams();
         }

         String uri = UriTemplate.createURI(
            this.scheme,
            this.authority,
            this.userInfo,
            this.host,
            this.port,
            this.path.toString(),
            this.query.toString(),
            this.fragment,
            values,
            encode,
            encodeSlashInPath
         );
         return this.createURI(uri);
      }
   }

   private void groupQueryParams() {
      MultivaluedMap<String, String> queryParams = UriComponent.decodeQuery(this.query.toString(), false, false);
      this.query.setLength(0);
      queryParams.forEach(this::clientQueryParamCommaSeparated);
   }

   private String create() {
      return UriComponent.encodeTemplateNames(this.toTemplate());
   }

   private URI createURI(String uri) {
      try {
         return new URI(uri);
      } catch (URISyntaxException var3) {
         throw new UriBuilderException(var3);
      }
   }

   public String toString() {
      return this.toTemplate();
   }

   public boolean isAbsolute() {
      return this.scheme != null;
   }
}
