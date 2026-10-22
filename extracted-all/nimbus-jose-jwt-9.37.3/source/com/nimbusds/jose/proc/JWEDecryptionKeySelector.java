package com.nimbusds.jose.proc;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyConverter;
import com.nimbusds.jose.jwk.source.JWKSource;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWEDecryptionKeySelector<C extends SecurityContext> extends AbstractJWKSelectorWithSource<C> implements JWEKeySelector<C> {
   private final JWEAlgorithm jweAlg;
   private final EncryptionMethod jweEnc;

   public JWEDecryptionKeySelector(JWEAlgorithm jweAlg, EncryptionMethod jweEnc, JWKSource<C> jwkSource) {
      super(jwkSource);
      if (jweAlg == null) {
         throw new IllegalArgumentException("The JWE algorithm must not be null");
      } else {
         this.jweAlg = jweAlg;
         if (jweEnc == null) {
            throw new IllegalArgumentException("The JWE encryption method must not be null");
         } else {
            this.jweEnc = jweEnc;
         }
      }
   }

   public JWEAlgorithm getExpectedJWEAlgorithm() {
      return this.jweAlg;
   }

   public EncryptionMethod getExpectedJWEEncryptionMethod() {
      return this.jweEnc;
   }

   protected JWKMatcher createJWKMatcher(JWEHeader jweHeader) {
      if (!this.getExpectedJWEAlgorithm().equals(jweHeader.getAlgorithm())) {
         return null;
      } else {
         return !this.getExpectedJWEEncryptionMethod().equals(jweHeader.getEncryptionMethod()) ? null : JWKMatcher.forJWEHeader(jweHeader);
      }
   }

   @Override
   public List<Key> selectJWEKeys(JWEHeader jweHeader, C context) throws KeySourceException {
      if (this.jweAlg.equals(jweHeader.getAlgorithm()) && this.jweEnc.equals(jweHeader.getEncryptionMethod())) {
         JWKMatcher jwkMatcher = this.createJWKMatcher(jweHeader);
         List<JWK> jwkMatches = this.getJWKSource().get(new JWKSelector(jwkMatcher), context);
         List<Key> sanitizedKeyList = new LinkedList<>();

         for (Key key : KeyConverter.toJavaKeys(jwkMatches)) {
            if (key instanceof PrivateKey || key instanceof SecretKey) {
               sanitizedKeyList.add(key);
            }
         }

         return sanitizedKeyList;
      } else {
         return Collections.emptyList();
      }
   }
}
