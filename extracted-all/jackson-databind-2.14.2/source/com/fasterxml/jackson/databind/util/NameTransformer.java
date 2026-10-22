package com.fasterxml.jackson.databind.util;

import java.io.Serializable;

public abstract class NameTransformer {
   public static final NameTransformer NOP = new NameTransformer.NopTransformer();

   protected NameTransformer() {
   }

   public static NameTransformer simpleTransformer(final String prefix, final String suffix) {
      boolean hasPrefix = prefix != null && !prefix.isEmpty();
      boolean hasSuffix = suffix != null && !suffix.isEmpty();
      if (hasPrefix) {
         return hasSuffix ? new NameTransformer() {
            @Override
            public String transform(String name) {
               return prefix + name + suffix;
            }

            @Override
            public String reverse(String transformed) {
               if (transformed.startsWith(prefix)) {
                  String str = transformed.substring(prefix.length());
                  if (str.endsWith(suffix)) {
                     return str.substring(0, str.length() - suffix.length());
                  }
               }

               return null;
            }

            @Override
            public String toString() {
               return "[PreAndSuffixTransformer('" + prefix + "','" + suffix + "')]";
            }
         } : new NameTransformer() {
            @Override
            public String transform(String name) {
               return prefix + name;
            }

            @Override
            public String reverse(String transformed) {
               return transformed.startsWith(prefix) ? transformed.substring(prefix.length()) : null;
            }

            @Override
            public String toString() {
               return "[PrefixTransformer('" + prefix + "')]";
            }
         };
      } else {
         return hasSuffix ? new NameTransformer() {
            @Override
            public String transform(String name) {
               return name + suffix;
            }

            @Override
            public String reverse(String transformed) {
               return transformed.endsWith(suffix) ? transformed.substring(0, transformed.length() - suffix.length()) : null;
            }

            @Override
            public String toString() {
               return "[SuffixTransformer('" + suffix + "')]";
            }
         } : NOP;
      }
   }

   public static NameTransformer chainedTransformer(NameTransformer t1, NameTransformer t2) {
      return new NameTransformer.Chained(t1, t2);
   }

   public abstract String transform(String var1);

   public abstract String reverse(String var1);

   public static class Chained extends NameTransformer implements Serializable {
      private static final long serialVersionUID = 1L;
      protected final NameTransformer _t1;
      protected final NameTransformer _t2;

      public Chained(NameTransformer t1, NameTransformer t2) {
         this._t1 = t1;
         this._t2 = t2;
      }

      @Override
      public String transform(String name) {
         return this._t1.transform(this._t2.transform(name));
      }

      @Override
      public String reverse(String transformed) {
         transformed = this._t1.reverse(transformed);
         if (transformed != null) {
            transformed = this._t2.reverse(transformed);
         }

         return transformed;
      }

      @Override
      public String toString() {
         return "[ChainedTransformer(" + this._t1 + ", " + this._t2 + ")]";
      }
   }

   protected static final class NopTransformer extends NameTransformer implements Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public String transform(String name) {
         return name;
      }

      @Override
      public String reverse(String transformed) {
         return transformed;
      }
   }
}
