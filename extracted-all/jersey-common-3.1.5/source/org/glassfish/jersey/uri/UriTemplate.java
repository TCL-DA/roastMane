package org.glassfish.jersey.uri;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.uri.internal.UriPart;
import org.glassfish.jersey.uri.internal.UriTemplateParser;

public class UriTemplate {
   private static final String[] EMPTY_VALUES = new String[0];
   public static final Comparator<UriTemplate> COMPARATOR = new Comparator<UriTemplate>() {
      public int compare(UriTemplate o1, UriTemplate o2) {
         if (o1 == null && o2 == null) {
            return 0;
         } else if (o1 == null) {
            return 1;
         } else if (o2 == null) {
            return -1;
         } else if (o1 == UriTemplate.EMPTY && o2 == UriTemplate.EMPTY) {
            return 0;
         } else if (o1 == UriTemplate.EMPTY) {
            return 1;
         } else if (o2 == UriTemplate.EMPTY) {
            return -1;
         } else {
            int i = o2.getNumberOfExplicitCharacters() - o1.getNumberOfExplicitCharacters();
            if (i != 0) {
               return i;
            } else {
               i = o2.getNumberOfTemplateVariables() - o1.getNumberOfTemplateVariables();
               if (i != 0) {
                  return i;
               } else {
                  i = o2.getNumberOfExplicitRegexes() - o1.getNumberOfExplicitRegexes();
                  return i != 0 ? i : 0;
               }
            }
         }
      }
   };
   private static final Pattern TEMPLATE_NAMES_PATTERN = Pattern.compile("\\{([\\w\\?;][-\\w\\.,]*)\\}");
   public static final UriTemplate EMPTY = new UriTemplate();
   private final String template;
   private final String normalizedTemplate;
   private final PatternWithGroups pattern;
   private final boolean endsWithSlash;
   private final List<UriPart> templateVariables;
   private final List<UriPart> uriParts;
   private final int numOfExplicitRegexes;
   private final int numOfRegexGroups;
   private final int numOfCharacters;

   private UriTemplate() {
      this.template = this.normalizedTemplate = "";
      this.pattern = PatternWithGroups.EMPTY;
      this.endsWithSlash = false;
      this.templateVariables = Collections.emptyList();
      this.uriParts = Collections.emptyList();
      this.numOfExplicitRegexes = this.numOfCharacters = this.numOfRegexGroups = 0;
   }

   public UriTemplate(String template) throws PatternSyntaxException, IllegalArgumentException {
      this(new UriTemplateParser(template));
   }

   protected UriTemplate(UriTemplateParser templateParser) throws PatternSyntaxException, IllegalArgumentException {
      this.template = templateParser.getTemplate();
      this.normalizedTemplate = templateParser.getNormalizedTemplate();
      this.pattern = initUriPattern(templateParser);
      this.numOfExplicitRegexes = templateParser.getNumberOfExplicitRegexes();
      this.numOfRegexGroups = templateParser.getNumberOfRegexGroups();
      this.numOfCharacters = templateParser.getNumberOfLiteralCharacters();
      this.endsWithSlash = this.template.charAt(this.template.length() - 1) == '/';
      this.templateVariables = Collections.unmodifiableList(templateParser.getNames());
      this.uriParts = templateParser.getUriParts();
   }

   private static PatternWithGroups initUriPattern(UriTemplateParser templateParser) {
      return new PatternWithGroups(templateParser.getPattern(), templateParser.getGroupIndexes());
   }

   public static URI resolve(URI baseUri, String refUri) {
      return resolve(baseUri, URI.create(refUri));
   }

   public static URI resolve(URI baseUri, URI refUri) {
      Preconditions.checkNotNull(baseUri, "Input base URI parameter must not be null.");
      Preconditions.checkNotNull(refUri, "Input reference URI parameter must not be null.");
      String refString = refUri.toString();
      if (refString.isEmpty()) {
         refUri = URI.create("#");
      } else if (refString.startsWith("?")) {
         String baseString = baseUri.toString();
         int qIndex = baseString.indexOf(63);
         baseString = qIndex > -1 ? baseString.substring(0, qIndex) : baseString;
         return URI.create(baseString + refString);
      }

      URI result = baseUri.resolve(refUri);
      if (refString.isEmpty()) {
         String resolvedString = result.toString();
         result = URI.create(resolvedString.substring(0, resolvedString.indexOf(35)));
      }

      return normalize(result);
   }

   public static URI normalize(String uri) {
      return normalize(URI.create(uri));
   }

   public static URI normalize(URI uri) {
      Preconditions.checkNotNull(uri, "Input reference URI parameter must not be null.");
      String path = uri.getPath();
      if (path != null && !path.isEmpty() && path.contains("/.")) {
         String[] segments = path.split("/");
         Deque<String> resolvedSegments = new ArrayDeque<>(segments.length);

         for (String segment : segments) {
            if (!segment.isEmpty() && !".".equals(segment)) {
               if ("..".equals(segment)) {
                  resolvedSegments.pollLast();
               } else {
                  resolvedSegments.offer(segment);
               }
            }
         }

         StringBuilder pathBuilder = new StringBuilder();

         for (String segmentx : resolvedSegments) {
            pathBuilder.append('/').append(UriComponent.encode(segmentx, UriComponent.Type.PATH));
         }

         String resultString = createURIWithStringValues(
            uri.getScheme(), uri.getAuthority(), null, null, null, pathBuilder.toString(), uri.getQuery(), uri.getFragment(), EMPTY_VALUES, false, false
         );
         return URI.create(resultString);
      } else {
         return uri;
      }
   }

   public static URI relativize(URI baseUri, URI refUri) {
      Preconditions.checkNotNull(baseUri, "Input base URI parameter must not be null.");
      Preconditions.checkNotNull(refUri, "Input reference URI parameter must not be null.");
      return normalize(baseUri.relativize(refUri));
   }

   public final String getTemplate() {
      return this.template;
   }

   public final PatternWithGroups getPattern() {
      return this.pattern;
   }

   public final boolean endsWithSlash() {
      return this.endsWithSlash;
   }

   public final List<String> getTemplateVariables() {
      return this.templateVariables.stream().map(UriPart::getPart).collect(Collectors.toList());
   }

   public final boolean isTemplateVariablePresent(String name) {
      for (UriPart tv : this.templateVariables) {
         if (tv.getPart().equals(name)) {
            return true;
         }
      }

      return false;
   }

   public final int getNumberOfExplicitRegexes() {
      return this.numOfExplicitRegexes;
   }

   public final int getNumberOfRegexGroups() {
      return this.numOfRegexGroups;
   }

   public final int getNumberOfExplicitCharacters() {
      return this.numOfCharacters;
   }

   public final int getNumberOfTemplateVariables() {
      return this.templateVariables.size();
   }

   public final boolean match(CharSequence uri, Map<String, String> templateVariableToValue) throws IllegalArgumentException {
      if (templateVariableToValue == null) {
         throw new IllegalArgumentException();
      } else {
         return this.pattern.match(uri, this.getTemplateVariables(), templateVariableToValue);
      }
   }

   public final boolean match(CharSequence uri, List<String> groupValues) throws IllegalArgumentException {
      if (groupValues == null) {
         throw new IllegalArgumentException();
      } else {
         return this.pattern.match(uri, groupValues);
      }
   }

   public final String createURI(final Map<String, String> values) {
      StringBuilder sb = new StringBuilder();
      this.resolveTemplate(sb, new UriTemplate.TemplateValueStrategy() {
         @Override
         public String valueFor(UriPart templateVariable, String matchedGroup) {
            String value = values.get(templateVariable.getPart());
            return value == null ? "" : templateVariable.resolve(value, null, false);
         }
      });
      return sb.toString();
   }

   public final String createURI(String... values) {
      return this.createURI(values, 0, values.length);
   }

   public final String createURI(final String[] values, final int offset, final int length) {
      UriTemplate.TemplateValueStrategy ns = new UriTemplate.TemplateValueStrategy() {
         private final int lengthPlusOffset = length + offset;
         private int v = offset;
         private final Map<String, String> mapValues = new HashMap<>();

         @Override
         public String valueFor(UriPart templateVariable, String matchedGroup) {
            String tValue = this.mapValues.get(templateVariable.getPart());
            if (tValue == null && this.v < this.lengthPlusOffset) {
               tValue = values[this.v++];
               if (tValue != null) {
                  this.mapValues.put(templateVariable.getPart(), tValue);
               }
            }

            return tValue;
         }
      };
      StringBuilder sb = new StringBuilder();
      this.resolveTemplate(sb, ns);
      return sb.toString();
   }

   private void resolveTemplate(StringBuilder builder, UriTemplate.TemplateValueStrategy valueStrategy) {
      for (UriPart uriPart : this.uriParts) {
         if (uriPart.isTemplate()) {
            builder.append(valueStrategy.valueFor(uriPart, uriPart.getGroup()));
         } else {
            builder.append(uriPart.getPart());
         }
      }
   }

   @Override
   public final String toString() {
      return this.pattern.toString();
   }

   @Override
   public final int hashCode() {
      return this.pattern.hashCode();
   }

   @Override
   public final boolean equals(Object o) {
      if (o instanceof UriTemplate) {
         UriTemplate that = (UriTemplate)o;
         return this.pattern.equals(that.pattern);
      } else {
         return false;
      }
   }

   public static String createURI(
      String scheme,
      String authority,
      String userInfo,
      String host,
      String port,
      String path,
      String query,
      String fragment,
      Map<String, ?> values,
      boolean encode,
      boolean encodeSlashInPath
   ) {
      return createURI(scheme, authority, userInfo, host, port, path, query, fragment, new Object[0], encode, encodeSlashInPath, values);
   }

   public static String createURIWithStringValues(
      String scheme,
      String authority,
      String userInfo,
      String host,
      String port,
      String path,
      String query,
      String fragment,
      Map<String, ?> values,
      boolean encode,
      boolean encodeSlashInPath
   ) {
      return createURI(scheme, authority, userInfo, host, port, path, query, fragment, EMPTY_VALUES, encode, encodeSlashInPath, values);
   }

   public static String createURI(
      String scheme,
      String authority,
      String userInfo,
      String host,
      String port,
      String path,
      String query,
      String fragment,
      Object[] values,
      boolean encode,
      boolean encodeSlashInPath
   ) {
      return createURI(scheme, authority, userInfo, host, port, path, query, fragment, values, encode, encodeSlashInPath, new HashMap<>());
   }

   public static String createURIWithStringValues(
      String scheme,
      String authority,
      String userInfo,
      String host,
      String port,
      String path,
      String query,
      String fragment,
      String[] values,
      boolean encode,
      boolean encodeSlashInPath
   ) {
      Map<String, Object> mapValues = new HashMap<>();
      return createURI(scheme, authority, userInfo, host, port, path, query, fragment, values, encode, encodeSlashInPath, mapValues);
   }

   private static String createURI(
      String scheme,
      String authority,
      String userInfo,
      String host,
      String port,
      String path,
      String query,
      String fragment,
      Object[] values,
      boolean encode,
      boolean encodeSlashInPath,
      Map<String, ?> mapValues
   ) {
      StringBuilder sb = new StringBuilder();
      int offset = 0;
      if (scheme != null) {
         offset = createUriComponent(UriComponent.Type.SCHEME, scheme, values, offset, false, mapValues, sb);
         sb.append(':');
      }

      boolean hasAuthority = false;
      if (notEmpty(userInfo) || notEmpty(host) || notEmpty(port)) {
         hasAuthority = true;
         sb.append("//");
         if (notEmpty(userInfo)) {
            offset = createUriComponent(UriComponent.Type.USER_INFO, userInfo, values, offset, encode, mapValues, sb);
            sb.append('@');
         }

         if (notEmpty(host)) {
            offset = createUriComponent(UriComponent.Type.HOST, host, values, offset, encode, mapValues, sb);
         }

         if (notEmpty(port)) {
            sb.append(':');
            offset = createUriComponent(UriComponent.Type.PORT, port, values, offset, false, mapValues, sb);
         }
      } else if (notEmpty(authority)) {
         hasAuthority = true;
         sb.append("//");
         offset = createUriComponent(UriComponent.Type.AUTHORITY, authority, values, offset, encode, mapValues, sb);
      }

      if (notEmpty(path) || notEmpty(query) || notEmpty(fragment)) {
         if (hasAuthority && (path == null || path.isEmpty() || path.charAt(0) != '/')) {
            sb.append('/');
         }

         if (notEmpty(path)) {
            UriComponent.Type t = encodeSlashInPath ? UriComponent.Type.PATH_SEGMENT : UriComponent.Type.PATH;
            offset = createUriComponent(t, path, values, offset, encode, mapValues, sb);
         }

         if (notEmpty(query)) {
            int sbLength = sb.length();
            offset = createUriComponent(UriComponent.Type.QUERY_PARAM, query, values, offset, encode, mapValues, sb);
            if (sb.length() > sbLength) {
               char firstQuery = sb.charAt(sbLength);
               if (firstQuery != '?' && (query.trim().charAt(0) != '{' || firstQuery != '&')) {
                  sb.insert(sbLength, '?');
               }
            }
         }

         if (notEmpty(fragment)) {
            sb.append('#');
            createUriComponent(UriComponent.Type.FRAGMENT, fragment, values, offset, encode, mapValues, sb);
         }
      }

      return sb.toString();
   }

   private static boolean notEmpty(String string) {
      return string != null && !string.isEmpty();
   }

   private static int createUriComponent(
      final UriComponent.Type componentType,
      String template,
      final Object[] values,
      final int valueOffset,
      final boolean encode,
      final Map<String, ?> _mapValues,
      StringBuilder b
   ) {
      if (template.indexOf(123) == -1) {
         b.append(template);
         return valueOffset;
      } else {
         UriTemplateParser templateParser = new UriTemplateParser(template);

         class ValuesFromArrayStrategy implements UriTemplate.TemplateValueStrategy {
            private int offset = valueOffset;

            @Override
            public String valueFor(UriPart templateVariable, String matchedGroup) {
               Object value = _mapValues.get(templateVariable.getPart());
               if (value == null && this.offset < values.length) {
                  value = values[this.offset++];
                  ((Map<String, Object>)_mapValues).put(templateVariable.getPart(), value);
               }

               if (value == null && templateVariable.throwWhenNoTemplateArg()) {
                  throw new IllegalArgumentException(String.format("The template variable '%s' has no value", templateVariable.getPart()));
               } else {
                  return templateVariable.resolve(value, componentType, encode);
               }
            }
         }

         ValuesFromArrayStrategy cs = new ValuesFromArrayStrategy();
         new UriTemplate(templateParser).resolveTemplate(b, cs);
         return cs.offset;
      }
   }

   public static String resolveTemplateValues(final UriComponent.Type type, String template, final boolean encode, final Map<String, ?> _mapValues) {
      if (template != null && !template.isEmpty() && template.indexOf(123) != -1) {
         StringBuilder sb = new StringBuilder();
         new UriTemplate(new UriTemplateParser(template)).resolveTemplate(sb, new UriTemplate.TemplateValueStrategy() {
            @Override
            public String valueFor(UriPart templateVariable, String matchedGroup) {
               Object value = _mapValues.get(templateVariable.getPart());
               if (value != null) {
                  return templateVariable.resolve(value.toString(), type, encode);
               } else if (_mapValues.containsKey(templateVariable.getPart())) {
                  throw new IllegalArgumentException(String.format("The value associated of the template value map for key '%s' is 'null'.", templateVariable));
               } else {
                  return matchedGroup;
               }
            }
         });
         return sb.toString();
      } else {
         return template;
      }
   }

   private interface TemplateValueStrategy {
      String valueFor(UriPart var1, String var2);
   }
}
