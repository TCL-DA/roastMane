package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class JWKMatcher {
   private final Set<KeyType> types;
   private final Set<KeyUse> uses;
   private final Set<KeyOperation> ops;
   private final Set<Algorithm> algs;
   private final Set<String> ids;
   private final boolean hasUse;
   private final boolean hasID;
   private final boolean privateOnly;
   private final boolean publicOnly;
   private final int minSizeBits;
   private final int maxSizeBits;
   private final Set<Integer> sizesBits;
   private final Set<Curve> curves;
   private final Set<Base64URL> x5tS256s;
   private final boolean hasX5C;

   @Deprecated
   public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly) {
      this(types, uses, ops, algs, ids, privateOnly, publicOnly, 0, 0);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits
   ) {
      this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Curve> curves
   ) {
      this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null, curves);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves
   ) {
      this(types, uses, ops, algs, ids, false, false, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean hasUse,
      boolean hasID,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves
   ) {
      this(types, uses, ops, algs, ids, hasUse, hasID, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, null);
   }

   @Deprecated
   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean hasUse,
      boolean hasID,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves,
      Set<Base64URL> x5tS256s
   ) {
      this(types, uses, ops, algs, ids, hasUse, hasID, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, x5tS256s, false);
   }

   public JWKMatcher(
      Set<KeyType> types,
      Set<KeyUse> uses,
      Set<KeyOperation> ops,
      Set<Algorithm> algs,
      Set<String> ids,
      boolean hasUse,
      boolean hasID,
      boolean privateOnly,
      boolean publicOnly,
      int minSizeBits,
      int maxSizeBits,
      Set<Integer> sizesBits,
      Set<Curve> curves,
      Set<Base64URL> x5tS256s,
      boolean hasX5C
   ) {
      this.types = types;
      this.uses = uses;
      this.ops = ops;
      this.algs = algs;
      this.ids = ids;
      this.hasUse = hasUse;
      this.hasID = hasID;
      this.privateOnly = privateOnly;
      this.publicOnly = publicOnly;
      this.minSizeBits = minSizeBits;
      this.maxSizeBits = maxSizeBits;
      this.sizesBits = sizesBits;
      this.curves = curves;
      this.x5tS256s = x5tS256s;
      this.hasX5C = hasX5C;
   }

   public static JWKMatcher forJWEHeader(JWEHeader jweHeader) {
      return new JWKMatcher.Builder()
         .keyType(KeyType.forAlgorithm(jweHeader.getAlgorithm()))
         .keyID(jweHeader.getKeyID())
         .keyUses(KeyUse.ENCRYPTION, null)
         .algorithms(jweHeader.getAlgorithm(), null)
         .build();
   }

   public static JWKMatcher forJWSHeader(JWSHeader jwsHeader) {
      JWSAlgorithm algorithm = jwsHeader.getAlgorithm();
      if (JWSAlgorithm.Family.RSA.contains(algorithm) || JWSAlgorithm.Family.EC.contains(algorithm)) {
         return new JWKMatcher.Builder()
            .keyType(KeyType.forAlgorithm(algorithm))
            .keyID(jwsHeader.getKeyID())
            .keyUses(KeyUse.SIGNATURE, null)
            .algorithms(algorithm, null)
            .x509CertSHA256Thumbprint(jwsHeader.getX509CertSHA256Thumbprint())
            .build();
      } else if (JWSAlgorithm.Family.HMAC_SHA.contains(algorithm)) {
         return new JWKMatcher.Builder()
            .keyType(KeyType.forAlgorithm(algorithm))
            .keyID(jwsHeader.getKeyID())
            .privateOnly(true)
            .algorithms(algorithm, null)
            .build();
      } else {
         return JWSAlgorithm.Family.ED.contains(algorithm)
            ? new JWKMatcher.Builder()
               .keyType(KeyType.forAlgorithm(algorithm))
               .keyID(jwsHeader.getKeyID())
               .keyUses(KeyUse.SIGNATURE, null)
               .algorithms(algorithm, null)
               .curves(Curve.forJWSAlgorithm(algorithm))
               .build()
            : null;
      }
   }

   public Set<KeyType> getKeyTypes() {
      return this.types;
   }

   public Set<KeyUse> getKeyUses() {
      return this.uses;
   }

   public Set<KeyOperation> getKeyOperations() {
      return this.ops;
   }

   public Set<Algorithm> getAlgorithms() {
      return this.algs;
   }

   public Set<String> getKeyIDs() {
      return this.ids;
   }

   public boolean hasKeyUse() {
      return this.hasUse;
   }

   public boolean hasKeyID() {
      return this.hasID;
   }

   public boolean isPrivateOnly() {
      return this.privateOnly;
   }

   public boolean isPublicOnly() {
      return this.publicOnly;
   }

   @Deprecated
   public int getMinSize() {
      return this.getMinKeySize();
   }

   public int getMinKeySize() {
      return this.minSizeBits;
   }

   @Deprecated
   public int getMaxSize() {
      return this.getMaxKeySize();
   }

   public int getMaxKeySize() {
      return this.maxSizeBits;
   }

   public Set<Integer> getKeySizes() {
      return this.sizesBits;
   }

   public Set<Curve> getCurves() {
      return this.curves;
   }

   public Set<Base64URL> getX509CertSHA256Thumbprints() {
      return this.x5tS256s;
   }

   public boolean hasX509CertChain() {
      return this.hasX5C;
   }

   public boolean matches(JWK key) {
      if (this.hasUse && key.getKeyUse() == null) {
         return false;
      } else if (!this.hasID || key.getKeyID() != null && !key.getKeyID().trim().isEmpty()) {
         if (this.privateOnly && !key.isPrivate()) {
            return false;
         } else if (this.publicOnly && key.isPrivate()) {
            return false;
         } else if (this.types != null && !this.types.contains(key.getKeyType())) {
            return false;
         } else if (this.uses != null && !this.uses.contains(key.getKeyUse())) {
            return false;
         } else if (this.ops == null
            || this.ops.contains(null) && key.getKeyOperations() == null
            || key.getKeyOperations() != null && this.ops.containsAll(key.getKeyOperations())) {
            if (this.algs != null && !this.algs.contains(key.getAlgorithm())) {
               return false;
            } else if (this.ids != null && !this.ids.contains(key.getKeyID())) {
               return false;
            } else if (this.minSizeBits > 0 && key.size() < this.minSizeBits) {
               return false;
            } else if (this.maxSizeBits > 0 && key.size() > this.maxSizeBits) {
               return false;
            } else if (this.sizesBits != null && !this.sizesBits.contains(key.size())) {
               return false;
            } else {
               if (this.curves != null) {
                  if (!(key instanceof CurveBasedJWK)) {
                     return false;
                  }

                  CurveBasedJWK curveBasedJWK = (CurveBasedJWK)key;
                  if (!this.curves.contains(curveBasedJWK.getCurve())) {
                     return false;
                  }
               }

               if (this.x5tS256s != null) {
                  boolean matchingCertFound = false;
                  if (key.getX509CertChain() != null && !key.getX509CertChain().isEmpty()) {
                     try {
                        X509Certificate cert = X509CertUtils.parseWithException(key.getX509CertChain().get(0).decode());
                        matchingCertFound = this.x5tS256s.contains(X509CertUtils.computeSHA256Thumbprint(cert));
                     } catch (CertificateException var4) {
                     }
                  }

                  boolean matchingX5T256Found = this.x5tS256s.contains(key.getX509CertSHA256Thumbprint());
                  if (!matchingCertFound && !matchingX5T256Found) {
                     return false;
                  }
               }

               return !this.hasX5C ? true : key.getX509CertChain() != null && !key.getX509CertChain().isEmpty();
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      append(sb, "kty", this.types);
      append(sb, "use", this.uses);
      append(sb, "key_ops", this.ops);
      append(sb, "alg", this.algs);
      append(sb, "kid", this.ids);
      if (this.hasUse) {
         sb.append("has_use=true ");
      }

      if (this.hasID) {
         sb.append("has_id=true ");
      }

      if (this.privateOnly) {
         sb.append("private_only=true ");
      }

      if (this.publicOnly) {
         sb.append("public_only=true ");
      }

      if (this.minSizeBits > 0) {
         sb.append("min_size=" + this.minSizeBits + " ");
      }

      if (this.maxSizeBits > 0) {
         sb.append("max_size=" + this.maxSizeBits + " ");
      }

      append(sb, "size", this.sizesBits);
      append(sb, "crv", this.curves);
      append(sb, "x5t#S256", this.x5tS256s);
      if (this.hasX5C) {
         sb.append("has_x5c=true");
      }

      return sb.toString().trim();
   }

   private static void append(StringBuilder sb, String key, Set<?> values) {
      if (values != null) {
         sb.append(key);
         sb.append('=');
         if (values.size() == 1) {
            Object value = values.iterator().next();
            if (value == null) {
               sb.append("ANY");
            } else {
               sb.append(value.toString().trim());
            }
         } else {
            sb.append(values.toString().trim());
         }

         sb.append(' ');
      }
   }

   public static class Builder {
      private Set<KeyType> types;
      private Set<KeyUse> uses;
      private Set<KeyOperation> ops;
      private Set<Algorithm> algs;
      private Set<String> ids;
      private boolean hasUse = false;
      private boolean hasID = false;
      private boolean privateOnly = false;
      private boolean publicOnly = false;
      private int minSizeBits = 0;
      private int maxSizeBits = 0;
      private Set<Integer> sizesBits;
      private Set<Curve> curves;
      private Set<Base64URL> x5tS256s;
      private boolean hasX5C = false;

      public JWKMatcher.Builder keyType(KeyType kty) {
         if (kty == null) {
            this.types = null;
         } else {
            this.types = new HashSet<>(Collections.singletonList(kty));
         }

         return this;
      }

      public JWKMatcher.Builder keyTypes(KeyType... types) {
         this.keyTypes(new LinkedHashSet<>(Arrays.asList(types)));
         return this;
      }

      public JWKMatcher.Builder keyTypes(Set<KeyType> types) {
         this.types = types;
         return this;
      }

      public JWKMatcher.Builder keyUse(KeyUse use) {
         if (use == null) {
            this.uses = null;
         } else {
            this.uses = new HashSet<>(Collections.singletonList(use));
         }

         return this;
      }

      public JWKMatcher.Builder keyUses(KeyUse... uses) {
         this.keyUses(new LinkedHashSet<>(Arrays.asList(uses)));
         return this;
      }

      public JWKMatcher.Builder keyUses(Set<KeyUse> uses) {
         this.uses = uses;
         return this;
      }

      public JWKMatcher.Builder keyOperation(KeyOperation op) {
         if (op == null) {
            this.ops = null;
         } else {
            this.ops = new HashSet<>(Collections.singletonList(op));
         }

         return this;
      }

      public JWKMatcher.Builder keyOperations(KeyOperation... ops) {
         this.keyOperations(new LinkedHashSet<>(Arrays.asList(ops)));
         return this;
      }

      public JWKMatcher.Builder keyOperations(Set<KeyOperation> ops) {
         this.ops = ops;
         return this;
      }

      public JWKMatcher.Builder algorithm(Algorithm alg) {
         if (alg == null) {
            this.algs = null;
         } else {
            this.algs = new HashSet<>(Collections.singletonList(alg));
         }

         return this;
      }

      public JWKMatcher.Builder algorithms(Algorithm... algs) {
         this.algorithms(new LinkedHashSet<>(Arrays.asList(algs)));
         return this;
      }

      public JWKMatcher.Builder algorithms(Set<Algorithm> algs) {
         this.algs = algs;
         return this;
      }

      public JWKMatcher.Builder keyID(String id) {
         if (id == null) {
            this.ids = null;
         } else {
            this.ids = new HashSet<>(Collections.singletonList(id));
         }

         return this;
      }

      public JWKMatcher.Builder keyIDs(String... ids) {
         this.keyIDs(new LinkedHashSet<>(Arrays.asList(ids)));
         return this;
      }

      public JWKMatcher.Builder keyIDs(Set<String> ids) {
         this.ids = ids;
         return this;
      }

      public JWKMatcher.Builder hasKeyUse(boolean hasUse) {
         this.hasUse = hasUse;
         return this;
      }

      public JWKMatcher.Builder hasKeyID(boolean hasID) {
         this.hasID = hasID;
         return this;
      }

      public JWKMatcher.Builder privateOnly(boolean privateOnly) {
         this.privateOnly = privateOnly;
         return this;
      }

      public JWKMatcher.Builder publicOnly(boolean publicOnly) {
         this.publicOnly = publicOnly;
         return this;
      }

      public JWKMatcher.Builder minKeySize(int minSizeBits) {
         this.minSizeBits = minSizeBits;
         return this;
      }

      public JWKMatcher.Builder maxKeySize(int maxSizeBits) {
         this.maxSizeBits = maxSizeBits;
         return this;
      }

      public JWKMatcher.Builder keySize(int keySizeBits) {
         if (keySizeBits <= 0) {
            this.sizesBits = null;
         } else {
            this.sizesBits = Collections.singleton(keySizeBits);
         }

         return this;
      }

      public JWKMatcher.Builder keySizes(int... keySizesBits) {
         Set<Integer> sizesSet = new LinkedHashSet<>();

         for (int keySize : keySizesBits) {
            sizesSet.add(keySize);
         }

         this.keySizes(sizesSet);
         return this;
      }

      public JWKMatcher.Builder keySizes(Set<Integer> keySizesBits) {
         this.sizesBits = keySizesBits;
         return this;
      }

      public JWKMatcher.Builder curve(Curve curve) {
         if (curve == null) {
            this.curves = null;
         } else {
            this.curves = Collections.singleton(curve);
         }

         return this;
      }

      public JWKMatcher.Builder curves(Curve... curves) {
         this.curves(new LinkedHashSet<>(Arrays.asList(curves)));
         return this;
      }

      public JWKMatcher.Builder curves(Set<Curve> curves) {
         this.curves = curves;
         return this;
      }

      public JWKMatcher.Builder x509CertSHA256Thumbprint(Base64URL x5tS256) {
         if (x5tS256 == null) {
            this.x5tS256s = null;
         } else {
            this.x5tS256s = Collections.singleton(x5tS256);
         }

         return this;
      }

      public JWKMatcher.Builder x509CertSHA256Thumbprints(Base64URL... x5tS256s) {
         return this.x509CertSHA256Thumbprints(new LinkedHashSet<>(Arrays.asList(x5tS256s)));
      }

      public JWKMatcher.Builder x509CertSHA256Thumbprints(Set<Base64URL> x5tS256s) {
         this.x5tS256s = x5tS256s;
         return this;
      }

      public JWKMatcher.Builder hasX509CertChain(boolean hasX5C) {
         this.hasX5C = hasX5C;
         return this;
      }

      public JWKMatcher build() {
         return new JWKMatcher(
            this.types,
            this.uses,
            this.ops,
            this.algs,
            this.ids,
            this.hasUse,
            this.hasID,
            this.privateOnly,
            this.publicOnly,
            this.minSizeBits,
            this.maxSizeBits,
            this.sizesBits,
            this.curves,
            this.x5tS256s,
            this.hasX5C
         );
      }
   }
}
