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

public final class UriCompliance implements ComplianceViolation.Mode {
   private static final Logger LOG = LoggerFactory.getLogger(UriCompliance.class);
   public static final Set<UriCompliance.Violation> NO_VIOLATION = Collections.unmodifiableSet(EnumSet.noneOf(UriCompliance.Violation.class));
   public static final Set<UriCompliance.Violation> AMBIGUOUS_VIOLATIONS = Collections.unmodifiableSet(
      EnumSet.of(
         UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT,
         UriCompliance.Violation.AMBIGUOUS_PATH_ENCODING,
         UriCompliance.Violation.AMBIGUOUS_PATH_PARAMETER,
         UriCompliance.Violation.AMBIGUOUS_PATH_SEGMENT,
         UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR
      )
   );
   public static final UriCompliance RFC3986 = new UriCompliance("RFC3986", EnumSet.noneOf(UriCompliance.Violation.class));
   public static final UriCompliance UNAMBIGUOUS = new UriCompliance("UNAMBIGUOUS", EnumSet.complementOf(EnumSet.copyOf(AMBIGUOUS_VIOLATIONS)));
   public static final UriCompliance DEFAULT = new UriCompliance("DEFAULT", RFC3986.getAllowed());
   public static final UriCompliance LEGACY = new UriCompliance(
      "LEGACY",
      EnumSet.of(
         UriCompliance.Violation.AMBIGUOUS_PATH_SEGMENT,
         UriCompliance.Violation.AMBIGUOUS_PATH_SEPARATOR,
         UriCompliance.Violation.AMBIGUOUS_PATH_ENCODING,
         UriCompliance.Violation.AMBIGUOUS_EMPTY_SEGMENT,
         UriCompliance.Violation.UTF16_ENCODINGS
      )
   );
   public static final UriCompliance UNSAFE = new UriCompliance("UNSAFE", EnumSet.allOf(UriCompliance.Violation.class));
   private static final AtomicInteger __custom = new AtomicInteger();
   private static final List<UriCompliance> KNOWN_MODES = List.of(DEFAULT, LEGACY, RFC3986, UNAMBIGUOUS, UNSAFE);
   private final String _name;
   private final Set<UriCompliance.Violation> _allowed;

   public static boolean isAmbiguous(Set<UriCompliance.Violation> violations) {
      if (violations.isEmpty()) {
         return false;
      } else {
         for (UriCompliance.Violation v : AMBIGUOUS_VIOLATIONS) {
            if (violations.contains(v)) {
               return true;
            }
         }

         return false;
      }
   }

   public static UriCompliance valueOf(String name) {
      for (UriCompliance compliance : KNOWN_MODES) {
         if (compliance.getName().equals(name)) {
            return compliance;
         }
      }

      LOG.warn("Unknown UriCompliance mode {}", name);
      return null;
   }

   public static UriCompliance from(Set<UriCompliance.Violation> violations) {
      return new UriCompliance("CUSTOM" + __custom.getAndIncrement(), violations);
   }

   public static UriCompliance from(String spec) {
      UriCompliance compliance = valueOf(spec);
      if (compliance == null) {
         String[] elements = spec.split("\\s*,\\s*");
         String i = elements[0];

         Set<UriCompliance.Violation> violations = (Set<UriCompliance.Violation>)(switch (i) {
            case "0" -> EnumSet.noneOf(UriCompliance.Violation.class);
            case "*" -> EnumSet.allOf(UriCompliance.Violation.class);
            default -> {
               UriCompliance mode = valueOf(elements[0]);
               yield mode == null ? EnumSet.noneOf(UriCompliance.Violation.class) : copyOf(mode.getAllowed());
            }
         });

         for (int i = 1; i < elements.length; i++) {
            String element = elements[i];
            boolean exclude = element.startsWith("-");
            if (exclude) {
               element = element.substring(1);
            }

            if (!element.equals("NON_CANONICAL_AMBIGUOUS_PATHS")) {
               UriCompliance.Violation section = UriCompliance.Violation.valueOf(element);
               if (exclude) {
                  violations.remove(section);
               } else {
                  violations.add(section);
               }
            }
         }

         compliance = new UriCompliance("CUSTOM" + __custom.getAndIncrement(), violations);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("UriCompliance from {}->{}", spec, compliance);
      }

      return compliance;
   }

   public UriCompliance(String name, Set<UriCompliance.Violation> violations) {
      Objects.requireNonNull(violations);
      this._name = name;
      this._allowed = violations.isEmpty() ? NO_VIOLATION : Collections.unmodifiableSet(copyOf(violations));
   }

   @Override
   public boolean allows(ComplianceViolation violation) {
      return violation instanceof UriCompliance.Violation && this._allowed.contains(violation);
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public Set<UriCompliance.Violation> getAllowed() {
      return this._allowed;
   }

   @Override
   public Set<UriCompliance.Violation> getKnown() {
      return EnumSet.allOf(UriCompliance.Violation.class);
   }

   public UriCompliance with(String name, UriCompliance.Violation... violations) {
      Set<UriCompliance.Violation> union = (Set<UriCompliance.Violation>)(this._allowed.isEmpty()
         ? EnumSet.noneOf(UriCompliance.Violation.class)
         : copyOf(this._allowed));
      union.addAll(copyOf(violations));
      return new UriCompliance(name, union);
   }

   public UriCompliance without(String name, UriCompliance.Violation... violations) {
      Set<UriCompliance.Violation> remainder = (Set<UriCompliance.Violation>)(this._allowed.isEmpty()
         ? EnumSet.noneOf(UriCompliance.Violation.class)
         : copyOf(this._allowed));
      remainder.removeAll(copyOf(violations));
      return new UriCompliance(name, remainder);
   }

   @Override
   public String toString() {
      return String.format("%s%s", this._name, this._allowed);
   }

   private static Set<UriCompliance.Violation> copyOf(UriCompliance.Violation[] violations) {
      return violations != null && violations.length != 0 ? EnumSet.copyOf(Arrays.asList(violations)) : EnumSet.noneOf(UriCompliance.Violation.class);
   }

   private static Set<UriCompliance.Violation> copyOf(Set<UriCompliance.Violation> violations) {
      return violations != null && !violations.isEmpty() ? EnumSet.copyOf(violations) : EnumSet.noneOf(UriCompliance.Violation.class);
   }

   public static String checkUriCompliance(UriCompliance compliance, HttpURI uri) {
      for (UriCompliance.Violation violation : UriCompliance.Violation.values()) {
         if (uri.hasViolation(violation) && (compliance == null || !compliance.allows(violation))) {
            return violation.getDescription();
         }
      }

      return null;
   }

   public static enum Violation implements ComplianceViolation {
      AMBIGUOUS_PATH_SEGMENT("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path segment"),
      AMBIGUOUS_EMPTY_SEGMENT("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI empty segment"),
      AMBIGUOUS_PATH_SEPARATOR("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path separator"),
      AMBIGUOUS_PATH_PARAMETER("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path parameter"),
      AMBIGUOUS_PATH_ENCODING("https://tools.ietf.org/html/rfc3986#section-3.3", "Ambiguous URI path encoding"),
      UTF16_ENCODINGS("https://www.w3.org/International/iri-edit/draft-duerst-iri.html#anchor29", "UTF-16 encoding"),
      BAD_UTF8_ENCODING("https://datatracker.ietf.org/doc/html/rfc5987#section-3.2.1", "Bad UTF-8 encoding");

      private final String _url;
      private final String _description;

      private Violation(String url, String description) {
         this._url = url;
         this._description = description;
      }

      @Override
      public String getName() {
         return this.name();
      }

      @Override
      public String getURL() {
         return this._url;
      }

      @Override
      public String getDescription() {
         return this._description;
      }
   }
}
