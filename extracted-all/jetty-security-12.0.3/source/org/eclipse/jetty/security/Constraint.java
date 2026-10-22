package org.eclipse.jetty.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public interface Constraint {
   Constraint ALLOWED = from("ALLOWED", Constraint.Authorization.ALLOWED);
   Constraint FORBIDDEN = from("FORBIDDEN", Constraint.Authorization.FORBIDDEN);
   Constraint ANY_USER = from("ANY_USER", Constraint.Authorization.ANY_USER);
   Constraint KNOWN_ROLE = from("KNOWN_ROLE", Constraint.Authorization.KNOWN_ROLE);
   Constraint SECURE_TRANSPORT = from("SECURE", Constraint.Transport.SECURE);
   Constraint ANY_TRANSPORT = from("ANY", Constraint.Transport.ANY);
   Constraint ALLOWED_ANY_TRANSPORT = combine("ALLOWED_ANY_TRANSPORT", ALLOWED, ANY_TRANSPORT);

   String getName();

   Constraint.Transport getTransport();

   Constraint.Authorization getAuthorization();

   Set<String> getRoles();

   static Constraint combine(Constraint leastSpecific, Constraint mostSpecific) {
      return combine(null, leastSpecific, mostSpecific);
   }

   static Constraint combine(String name, Constraint leastSpecific, Constraint mostSpecific) {
      if (leastSpecific == null) {
         return mostSpecific == null ? ALLOWED : mostSpecific;
      } else {
         return mostSpecific == null
            ? leastSpecific
            : from(
               name,
               mostSpecific.getTransport() == Constraint.Transport.INHERIT ? leastSpecific.getTransport() : mostSpecific.getTransport(),
               mostSpecific.getAuthorization() == Constraint.Authorization.INHERIT ? leastSpecific.getAuthorization() : mostSpecific.getAuthorization(),
               mostSpecific.getAuthorization() == Constraint.Authorization.INHERIT ? leastSpecific.getRoles() : mostSpecific.getRoles()
            );
      }
   }

   static Constraint from(String... roles) {
      return from(null, Constraint.Authorization.SPECIFIC_ROLE, roles);
   }

   static Constraint from(String name, Constraint.Transport transport) {
      return from(name, transport, null, null);
   }

   static Constraint from(String name, Constraint.Authorization authorization, String... roles) {
      return from(
         name,
         Constraint.Transport.INHERIT,
         authorization,
         (Set<String>)(roles != null && roles.length != 0 ? new HashSet<>(Arrays.stream(roles).toList()) : Collections.emptySet())
      );
   }

   static Constraint from(Constraint.Transport transport, Constraint.Authorization authorization, Set<String> roles) {
      return from(null, transport, authorization, roles);
   }

   static Constraint from(final String name, final Constraint.Transport transport, final Constraint.Authorization authorization, final Set<String> roles) {
      return new Constraint() {
         private final String _name = name == null ? "unnamed@%x".formatted(this.hashCode()) : name;
         private final Constraint.Transport _transport = transport == null ? Constraint.Transport.INHERIT : transport;
         private final Set<String> _roles = roles != null && !roles.isEmpty() ? Collections.unmodifiableSet(roles) : Collections.emptySet();
         private final Constraint.Authorization _authorization = authorization == null
            ? (this._roles.isEmpty() ? Constraint.Authorization.INHERIT : Constraint.Authorization.SPECIFIC_ROLE)
            : authorization;

         {
            if (!this._roles.isEmpty() && this._authorization != Constraint.Authorization.SPECIFIC_ROLE) {
               throw new IllegalStateException("Constraint with roles must be SPECIFIC_ROLE, not " + this._authorization);
            }
         }

         @Override
         public String getName() {
            return this._name;
         }

         @Override
         public Constraint.Transport getTransport() {
            return this._transport;
         }

         @Override
         public Constraint.Authorization getAuthorization() {
            return this._authorization;
         }

         @Override
         public Set<String> getRoles() {
            return this._roles;
         }

         @Override
         public String toString() {
            return "Constraint@%x{%s,%s,%s,%s}".formatted(this.hashCode(), this.getName(), this.getTransport(), this.getAuthorization(), this.getRoles());
         }
      };
   }

   public static enum Authorization {
      FORBIDDEN,
      ALLOWED,
      ANY_USER,
      KNOWN_ROLE,
      SPECIFIC_ROLE,
      INHERIT;
   }

   public static class Builder {
      private String _name;
      private Constraint.Authorization _authorization;
      private Set<String> _roles;
      private Constraint.Transport _transport;

      public Builder() {
      }

      public Builder(Constraint constraint) {
         this._transport = constraint.getTransport();
         this._authorization = constraint.getAuthorization();
         this._roles = constraint.getRoles();
      }

      public Constraint.Builder name(String name) {
         this._name = name;
         return this;
      }

      public String getName() {
         return this._name;
      }

      public Constraint.Builder transport(Constraint.Transport transport) {
         this._transport = transport;
         return this;
      }

      public Constraint.Transport getTransport() {
         return this._transport;
      }

      public Constraint.Builder authorization(Constraint.Authorization authorization) {
         this._authorization = authorization;
         return this;
      }

      public Constraint.Authorization getAuthorization() {
         return this._authorization;
      }

      public Constraint.Builder roles(String... roles) {
         if (roles != null && roles.length > 0) {
            if (this._roles == null) {
               this._roles = new HashSet<>();
            } else if (!(this._roles instanceof HashSet)) {
               this._roles = new HashSet<>(this._roles);
            }

            this._roles.addAll(Arrays.asList(roles));
         }

         return this;
      }

      public Set<String> getRoles() {
         return this._roles == null ? Collections.emptySet() : this._roles;
      }

      public Constraint build() {
         return Constraint.from(this._name, this._transport, this._authorization, this._roles);
      }
   }

   public static enum Transport {
      SECURE,
      ANY,
      INHERIT;
   }
}
