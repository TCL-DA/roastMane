package org.glassfish.jersey.uri.internal;

import org.glassfish.jersey.uri.UriComponent;

public class UriPart {
   private final String part;

   UriPart(String part) {
      this.part = part;
   }

   public String getPart() {
      return this.part;
   }

   public String getGroup() {
      return this.part;
   }

   public boolean isTemplate() {
      return false;
   }

   public String resolve(Object value, UriComponent.Type componentType, boolean encode) {
      return this.part;
   }

   public boolean throwWhenNoTemplateArg() {
      return false;
   }

   public static String percentEncode(String toEncode, UriComponent.Type componentType, boolean encode) {
      if (encode) {
         toEncode = UriComponent.encode(toEncode, componentType);
      } else {
         toEncode = UriComponent.contextualEncode(toEncode, componentType);
      }

      return toEncode;
   }
}
