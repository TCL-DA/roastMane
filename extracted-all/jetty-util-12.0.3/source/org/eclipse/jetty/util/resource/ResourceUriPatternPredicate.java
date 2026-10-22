package org.eclipse.jetty.util.resource;

import java.net.URI;
import java.util.Objects;
import java.util.function.Predicate;
import org.eclipse.jetty.util.UriPatternPredicate;

public class ResourceUriPatternPredicate implements Predicate<Resource> {
   private final Predicate<URI> uriPredicate;

   public ResourceUriPatternPredicate(String regex, boolean isNullInclusive) {
      this(new UriPatternPredicate(regex, isNullInclusive));
   }

   public ResourceUriPatternPredicate(UriPatternPredicate uriPredicate) {
      this.uriPredicate = Objects.requireNonNull(uriPredicate, "UriPatternPredicate cannot be null");
   }

   public boolean test(Resource resource) {
      if (resource == null) {
         return false;
      } else {
         URI uri = resource.getURI();
         return this.uriPredicate.test(uri);
      }
   }
}
