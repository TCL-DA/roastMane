package org.eclipse.jetty.http.pathmap;

import java.util.Map.Entry;

public class MatchedResource<E> {
   private final E resource;
   private final PathSpec pathSpec;
   private final MatchedPath matchedPath;

   public MatchedResource(E resource, PathSpec pathSpec, MatchedPath matchedPath) {
      this.resource = resource;
      this.pathSpec = pathSpec;
      this.matchedPath = matchedPath;
   }

   public static <E> MatchedResource<E> of(Entry<PathSpec, E> mapping, MatchedPath matchedPath) {
      return new MatchedResource<>(mapping.getValue(), mapping.getKey(), matchedPath);
   }

   public MatchedPath getMatchedPath() {
      return this.matchedPath;
   }

   public PathSpec getPathSpec() {
      return this.pathSpec;
   }

   public E getResource() {
      return this.resource;
   }

   public String getPathMatch() {
      return this.matchedPath.getPathMatch();
   }

   public String getPathInfo() {
      return this.matchedPath.getPathInfo();
   }
}
