package org.glassfish.jersey.uri;

import org.glassfish.jersey.uri.internal.UriTemplateParser;

public final class PathTemplate extends UriTemplate {
   public PathTemplate(String path) {
      super(new PathTemplate.PathTemplateParser(prefixWithSlash(path)));
   }

   private static String prefixWithSlash(String path) {
      return !path.isEmpty() && path.charAt(0) == '/' ? path : "/" + path;
   }

   private static final class PathTemplateParser extends UriTemplateParser {
      public PathTemplateParser(String path) {
         super(path);
      }

      @Override
      protected String encodeLiteralCharacters(String literalCharacters) {
         return UriComponent.contextualEncode(literalCharacters, UriComponent.Type.PATH);
      }
   }
}
