package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonIncludeProperties {
   String[] value() default {};

   public static class Value implements JacksonAnnotationValue<JsonIncludeProperties>, Serializable {
      private static final long serialVersionUID = 1L;
      protected static final JsonIncludeProperties.Value ALL = new JsonIncludeProperties.Value(null);
      protected final Set<String> _included;

      protected Value(Set<String> included) {
         this._included = included;
      }

      public static JsonIncludeProperties.Value from(JsonIncludeProperties src) {
         return src == null ? ALL : new JsonIncludeProperties.Value(_asSet(src.value()));
      }

      public static JsonIncludeProperties.Value all() {
         return ALL;
      }

      @Override
      public Class<JsonIncludeProperties> valueFor() {
         return JsonIncludeProperties.class;
      }

      public Set<String> getIncluded() {
         return this._included;
      }

      public JsonIncludeProperties.Value withOverrides(JsonIncludeProperties.Value overrides) {
         Set<String> otherIncluded;
         if (overrides != null && (otherIncluded = overrides.getIncluded()) != null) {
            if (this._included == null) {
               return overrides;
            } else {
               HashSet<String> toInclude = new HashSet<>();

               for (String incl : otherIncluded) {
                  if (this._included.contains(incl)) {
                     toInclude.add(incl);
                  }
               }

               return new JsonIncludeProperties.Value(toInclude);
            }
         } else {
            return this;
         }
      }

      @Override
      public String toString() {
         return String.format("JsonIncludeProperties.Value(included=%s)", this._included);
      }

      @Override
      public int hashCode() {
         return this._included == null ? 0 : this._included.size();
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return o == null ? false : o.getClass() == this.getClass() && _equals(this._included, ((JsonIncludeProperties.Value)o)._included);
         }
      }

      private static boolean _equals(Set<String> a, Set<String> b) {
         return a == null ? b == null : a.equals(b);
      }

      private static Set<String> _asSet(String[] v) {
         if (v != null && v.length != 0) {
            Set<String> s = new HashSet<>(v.length);

            for (String str : v) {
               s.add(str);
            }

            return s;
         } else {
            return Collections.emptySet();
         }
      }
   }
}
