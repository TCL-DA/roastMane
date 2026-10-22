package com.nimbusds.jose;

import com.nimbusds.jose.util.JSONStringUtils;
import java.io.Serializable;
import net.jcip.annotations.Immutable;

@Immutable
public class Algorithm implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final Algorithm NONE = new Algorithm("none", Requirement.REQUIRED);
   private final String name;
   private final Requirement requirement;

   public Algorithm(String name, Requirement req) {
      if (name == null) {
         throw new IllegalArgumentException("The algorithm name must not be null");
      } else {
         this.name = name;
         this.requirement = req;
      }
   }

   public Algorithm(String name) {
      this(name, null);
   }

   public final String getName() {
      return this.name;
   }

   public final Requirement getRequirement() {
      return this.requirement;
   }

   @Override
   public final int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      return object instanceof Algorithm && this.toString().equals(object.toString());
   }

   @Override
   public final String toString() {
      return this.name;
   }

   public final String toJSONString() {
      return JSONStringUtils.toJSONString(this.name);
   }

   public static Algorithm parse(String s) {
      return s == null ? null : new Algorithm(s);
   }
}
