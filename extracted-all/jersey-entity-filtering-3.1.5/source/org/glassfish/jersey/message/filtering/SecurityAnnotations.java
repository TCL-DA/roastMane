package org.glassfish.jersey.message.filtering;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.internal.inject.AnnotationLiteral;

public final class SecurityAnnotations {
   public static RolesAllowed rolesAllowed(String... roles) {
      List<String> list = new ArrayList<>(roles.length);

      for (String role : roles) {
         if (role != null) {
            list.add(role);
         }
      }

      return new SecurityAnnotations.RolesAllowedImpl(list.toArray(new String[list.size()]));
   }

   public static PermitAll permitAll() {
      return new SecurityAnnotations.PermitAllImpl();
   }

   public static DenyAll denyAll() {
      return new SecurityAnnotations.DenyAllImpl();
   }

   private SecurityAnnotations() {
   }

   private static final class DenyAllImpl extends AnnotationLiteral<DenyAll> implements DenyAll {
      private DenyAllImpl() {
      }
   }

   private static class PermitAllImpl extends AnnotationLiteral<PermitAll> implements PermitAll {
      private PermitAllImpl() {
      }
   }

   private static final class RolesAllowedImpl extends AnnotationLiteral<RolesAllowed> implements RolesAllowed {
      private final String[] roles;

      private RolesAllowedImpl(String[] roles) {
         this.roles = roles;
      }

      public String[] value() {
         return this.roles;
      }
   }
}
