package org.eclipse.jetty.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HttpCompliance implements ComplianceViolation.Mode {
   private static final Logger LOG = LoggerFactory.getLogger(HttpCompliance.class);
   public static final String VIOLATIONS_ATTR = "org.eclipse.jetty.http.compliance.violations";
   public static final HttpCompliance RFC7230 = new HttpCompliance("RFC7230", EnumSet.noneOf(HttpCompliance.Violation.class));
   public static final HttpCompliance RFC2616 = new HttpCompliance(
      "RFC2616", EnumSet.of(HttpCompliance.Violation.HTTP_0_9, HttpCompliance.Violation.MULTILINE_FIELD_VALUE, HttpCompliance.Violation.MISMATCHED_AUTHORITY)
   );
   public static final HttpCompliance LEGACY = new HttpCompliance("LEGACY", EnumSet.complementOf(EnumSet.of(HttpCompliance.Violation.CASE_INSENSITIVE_METHOD)));
   public static final HttpCompliance RFC2616_LEGACY = RFC2616.with(
      "RFC2616_LEGACY",
      HttpCompliance.Violation.CASE_INSENSITIVE_METHOD,
      HttpCompliance.Violation.NO_COLON_AFTER_FIELD_NAME,
      HttpCompliance.Violation.TRANSFER_ENCODING_WITH_CONTENT_LENGTH,
      HttpCompliance.Violation.MULTIPLE_CONTENT_LENGTHS
   );
   public static final HttpCompliance RFC7230_LEGACY = RFC7230.with("RFC7230_LEGACY", HttpCompliance.Violation.CASE_INSENSITIVE_METHOD);
   private static final List<HttpCompliance> KNOWN_MODES = Arrays.asList(RFC7230, RFC2616, LEGACY, RFC2616_LEGACY, RFC7230_LEGACY);
   private static final AtomicInteger __custom = new AtomicInteger();
   private final String _name;
   private final Set<HttpCompliance.Violation> _violations;

   public static HttpCompliance valueOf(String name) {
      for (HttpCompliance compliance : KNOWN_MODES) {
         if (compliance.getName().equals(name)) {
            return compliance;
         }
      }

      LOG.warn("Unknown HttpCompliance mode {}", name);
      return null;
   }

   public static HttpCompliance from(String spec) {
      HttpCompliance compliance = valueOf(spec);
      if (compliance == null) {
         String[] elements = spec.split("\\s*,\\s*");
         String i = elements[0];

         Set<HttpCompliance.Violation> sections = (Set<HttpCompliance.Violation>)(switch (i) {
            case "0" -> EnumSet.noneOf(HttpCompliance.Violation.class);
            case "*" -> EnumSet.allOf(HttpCompliance.Violation.class);
            default -> {
               HttpCompliance mode = valueOf(elements[0]);
               yield mode == null ? EnumSet.noneOf(HttpCompliance.Violation.class) : copyOf(mode.getAllowed());
            }
         });

         for (int i = 1; i < elements.length; i++) {
            String element = elements[i];
            boolean exclude = element.startsWith("-");
            if (exclude) {
               element = element.substring(1);
            }

            HttpCompliance.Violation section = HttpCompliance.Violation.valueOf(element);
            if (exclude) {
               sections.remove(section);
            } else {
               sections.add(section);
            }
         }

         compliance = new HttpCompliance("CUSTOM" + __custom.getAndIncrement(), sections);
      }

      return compliance;
   }

   private HttpCompliance(String name, Set<HttpCompliance.Violation> violations) {
      Objects.requireNonNull(violations);
      this._name = name;
      this._violations = Collections.unmodifiableSet(
         (Set<? extends HttpCompliance.Violation>)(violations.isEmpty() ? EnumSet.noneOf(HttpCompliance.Violation.class) : copyOf(violations))
      );
   }

   @Override
   public boolean allows(ComplianceViolation violation) {
      return violation instanceof HttpCompliance.Violation && this._violations.contains(violation);
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public Set<HttpCompliance.Violation> getAllowed() {
      return this._violations;
   }

   @Override
   public Set<HttpCompliance.Violation> getKnown() {
      return EnumSet.allOf(HttpCompliance.Violation.class);
   }

   public HttpCompliance with(String name, HttpCompliance.Violation... violations) {
      Set<HttpCompliance.Violation> union = (Set<HttpCompliance.Violation>)(this._violations.isEmpty()
         ? EnumSet.noneOf(HttpCompliance.Violation.class)
         : copyOf(this._violations));
      union.addAll(copyOf(violations));
      return new HttpCompliance(name, union);
   }

   public HttpCompliance without(String name, HttpCompliance.Violation... violations) {
      Set<HttpCompliance.Violation> remainder = (Set<HttpCompliance.Violation>)(this._violations.isEmpty()
         ? EnumSet.noneOf(HttpCompliance.Violation.class)
         : copyOf(this._violations));
      remainder.removeAll(copyOf(violations));
      return new HttpCompliance(name, remainder);
   }

   @Override
   public String toString() {
      return String.format("%s%s", this._name, this._violations);
   }

   private static Set<HttpCompliance.Violation> copyOf(HttpCompliance.Violation[] violations) {
      return violations != null && violations.length != 0 ? EnumSet.copyOf(Arrays.asList(violations)) : EnumSet.noneOf(HttpCompliance.Violation.class);
   }

   private static Set<HttpCompliance.Violation> copyOf(Set<HttpCompliance.Violation> violations) {
      return violations != null && !violations.isEmpty() ? EnumSet.copyOf(violations) : EnumSet.noneOf(HttpCompliance.Violation.class);
   }

   public static enum Violation implements ComplianceViolation {
      CASE_SENSITIVE_FIELD_NAME("https://tools.ietf.org/html/rfc7230#section-3.2", "Field name is case-insensitive"),
      CASE_INSENSITIVE_METHOD("https://tools.ietf.org/html/rfc7230#section-3.1.1", "Method is case-sensitive"),
      HTTP_0_9("https://tools.ietf.org/html/rfc7230#appendix-A.2", "HTTP/0.9 not supported"),
      MULTILINE_FIELD_VALUE("https://tools.ietf.org/html/rfc7230#section-3.2.4", "Line Folding not supported"),
      MULTIPLE_CONTENT_LENGTHS("https://tools.ietf.org/html/rfc7230#section-3.3.2", "Multiple Content-Lengths"),
      TRANSFER_ENCODING_WITH_CONTENT_LENGTH("https://tools.ietf.org/html/rfc7230#section-3.3.1", "Transfer-Encoding and Content-Length"),
      WHITESPACE_AFTER_FIELD_NAME("https://tools.ietf.org/html/rfc7230#section-3.2.4", "Whitespace not allowed after field name"),
      NO_COLON_AFTER_FIELD_NAME("https://tools.ietf.org/html/rfc7230#section-3.2", "Fields must have a Colon"),
      DUPLICATE_HOST_HEADERS("https://www.rfc-editor.org/rfc/rfc7230#section-5.4", "Duplicate Host Header"),
      UNSAFE_HOST_HEADER("https://www.rfc-editor.org/rfc/rfc7230#section-2.7.1", "Invalid Authority"),
      MISMATCHED_AUTHORITY("https://www.rfc-editor.org/rfc/rfc7230#section-5.4", "Mismatched Authority");

      private final String url;
      private final String description;

      private Violation(String url, String description) {
         this.url = url;
         this.description = description;
      }

      @Override
      public String getName() {
         return this.name();
      }

      @Override
      public String getURL() {
         return this.url;
      }

      @Override
      public String getDescription() {
         return this.description;
      }
   }
}
