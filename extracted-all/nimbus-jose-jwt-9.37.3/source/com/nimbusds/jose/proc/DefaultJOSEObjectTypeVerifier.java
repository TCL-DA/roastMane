package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEObjectType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class DefaultJOSEObjectTypeVerifier<C extends SecurityContext> implements JOSEObjectTypeVerifier<C> {
   private final Set<JOSEObjectType> allowedTypes;
   public static final DefaultJOSEObjectTypeVerifier JOSE = new DefaultJOSEObjectTypeVerifier(JOSEObjectType.JOSE, null);
   public static final DefaultJOSEObjectTypeVerifier JWT = new DefaultJOSEObjectTypeVerifier(JOSEObjectType.JWT, null);

   public DefaultJOSEObjectTypeVerifier() {
      this.allowedTypes = Collections.singleton(null);
   }

   public DefaultJOSEObjectTypeVerifier(Set<JOSEObjectType> allowedTypes) {
      if (allowedTypes != null && !allowedTypes.isEmpty()) {
         this.allowedTypes = allowedTypes;
      } else {
         throw new IllegalArgumentException("The allowed types must not be null or empty");
      }
   }

   public DefaultJOSEObjectTypeVerifier(JOSEObjectType... allowedTypes) {
      if (allowedTypes != null && allowedTypes.length != 0) {
         this.allowedTypes = new HashSet<>(Arrays.asList(allowedTypes));
      } else {
         throw new IllegalArgumentException("The allowed types must not be null or empty");
      }
   }

   public Set<JOSEObjectType> getAllowedTypes() {
      return this.allowedTypes;
   }

   @Override
   public void verify(JOSEObjectType type, C context) throws BadJOSEException {
      if (type == null && !this.allowedTypes.contains(null)) {
         throw new BadJOSEException("Required JOSE header typ (type) parameter is missing");
      } else if (!this.allowedTypes.contains(type)) {
         throw new BadJOSEException("JOSE header typ (type) " + type + " not allowed");
      }
   }
}
