package org.eclipse.jetty.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UriPatternPredicate implements Predicate<URI> {
   private final List<Pattern> subPatterns;
   private final boolean isNullInclusive;

   public UriPatternPredicate(String regex, boolean isNullInclusive) {
      this(regex == null ? null : Pattern.compile(regex), isNullInclusive);
   }

   public UriPatternPredicate(Pattern pattern, boolean isNullInclusive) {
      this.isNullInclusive = isNullInclusive;
      String[] patterns = pattern == null ? null : pattern.pattern().split(",");
      this.subPatterns = new ArrayList<>();

      for (int i = 0; patterns != null && i < patterns.length; i++) {
         this.subPatterns.add(Pattern.compile(patterns[i]));
      }

      if (this.subPatterns.isEmpty()) {
         this.subPatterns.add(pattern);
      }
   }

   public boolean test(URI uri) {
      if (this.subPatterns.isEmpty()) {
         return this.match(null, uri);
      } else {
         for (Pattern p : this.subPatterns) {
            if (this.match(p, uri)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean match(Pattern pattern, URI uri) {
      String s = uri.toString();
      return pattern == null && this.isNullInclusive || pattern != null && pattern.matcher(s).matches();
   }
}
