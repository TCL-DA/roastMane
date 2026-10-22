package com.nimbusds.jose;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.StandardCharset;
import java.security.Signature;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicReference;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JWSObject extends JOSEObject {
   private static final long serialVersionUID = 1L;
   private final JWSHeader header;
   private final String signingInputString;
   private Base64URL signature;
   private final AtomicReference<JWSObject.State> state = new AtomicReference<>();

   public JWSObject(JWSHeader header, Payload payload) {
      if (header == null) {
         throw new IllegalArgumentException("The JWS header must not be null");
      } else {
         this.header = header;
         if (payload == null) {
            throw new IllegalArgumentException("The payload must not be null");
         } else {
            this.setPayload(payload);
            this.signingInputString = this.composeSigningInput();
            this.signature = null;
            this.state.set(JWSObject.State.UNSIGNED);
         }
      }
   }

   public JWSObject(Base64URL firstPart, Base64URL secondPart, Base64URL thirdPart) throws ParseException {
      this(firstPart, new Payload(secondPart), thirdPart);
   }

   public JWSObject(Base64URL firstPart, Payload payload, Base64URL thirdPart) throws ParseException {
      if (firstPart == null) {
         throw new IllegalArgumentException("The first part must not be null");
      } else {
         try {
            this.header = JWSHeader.parse(firstPart);
         } catch (ParseException var5) {
            throw new ParseException("Invalid JWS header: " + var5.getMessage(), 0);
         }

         if (payload == null) {
            throw new IllegalArgumentException("The payload (second part) must not be null");
         } else {
            this.setPayload(payload);
            this.signingInputString = this.composeSigningInput();
            if (thirdPart == null) {
               throw new IllegalArgumentException("The third part must not be null");
            } else if (thirdPart.toString().trim().isEmpty()) {
               throw new ParseException("The signature must not be empty", 0);
            } else {
               this.signature = thirdPart;
               this.state.set(JWSObject.State.SIGNED);
               if (this.getHeader().isBase64URLEncodePayload()) {
                  this.setParsedParts(new Base64URL[]{firstPart, payload.toBase64URL(), thirdPart});
               } else {
                  this.setParsedParts(new Base64URL[]{firstPart, new Base64URL(""), thirdPart});
               }
            }
         }
      }
   }

   public JWSHeader getHeader() {
      return this.header;
   }

   private String composeSigningInput() {
      return this.header.isBase64URLEncodePayload()
         ? this.getHeader().toBase64URL().toString() + '.' + this.getPayload().toBase64URL().toString()
         : this.getHeader().toBase64URL().toString() + '.' + this.getPayload().toString();
   }

   public byte[] getSigningInput() {
      return this.signingInputString.getBytes(StandardCharset.UTF_8);
   }

   public Base64URL getSignature() {
      return this.signature;
   }

   public JWSObject.State getState() {
      return this.state.get();
   }

   private void ensureUnsignedState() {
      if (this.state.get() != JWSObject.State.UNSIGNED) {
         throw new IllegalStateException("The JWS object must be in an unsigned state");
      }
   }

   private void ensureSignedOrVerifiedState() {
      if (this.state.get() != JWSObject.State.SIGNED && this.state.get() != JWSObject.State.VERIFIED) {
         throw new IllegalStateException("The JWS object must be in a signed or verified state");
      }
   }

   private void ensureJWSSignerSupport(JWSSigner signer) throws JOSEException {
      if (!signer.supportedJWSAlgorithms().contains(this.getHeader().getAlgorithm())) {
         throw new JOSEException(
            "The "
               + this.getHeader().getAlgorithm()
               + " algorithm is not allowed or supported by the JWS signer: Supported algorithms: "
               + signer.supportedJWSAlgorithms()
         );
      }
   }

   public synchronized void sign(JWSSigner signer) throws JOSEException {
      this.ensureUnsignedState();
      this.ensureJWSSignerSupport(signer);

      try {
         this.signature = signer.sign(this.getHeader(), this.getSigningInput());
      } catch (final ActionRequiredForJWSCompletionException var3) {
         throw new ActionRequiredForJWSCompletionException(var3.getMessage(), var3.getTriggeringOption(), new CompletableJWSObjectSigning() {
            @Override
            public Signature getInitializedSignature() {
               return var3.getCompletableJWSObjectSigning().getInitializedSignature();
            }

            @Override
            public Base64URL complete() throws JOSEException {
               JWSObject.this.signature = var3.getCompletableJWSObjectSigning().complete();
               JWSObject.this.state.set(JWSObject.State.SIGNED);
               return JWSObject.this.signature;
            }
         });
      } catch (JOSEException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new JOSEException(var5.getMessage(), var5);
      }

      this.state.set(JWSObject.State.SIGNED);
   }

   public synchronized boolean verify(JWSVerifier verifier) throws JOSEException {
      this.ensureSignedOrVerifiedState();

      boolean verified;
      try {
         verified = verifier.verify(this.getHeader(), this.getSigningInput(), this.getSignature());
      } catch (JOSEException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new JOSEException(var5.getMessage(), var5);
      }

      if (verified) {
         this.state.set(JWSObject.State.VERIFIED);
      }

      return verified;
   }

   @Override
   public String serialize() {
      return this.serialize(false);
   }

   public String serialize(boolean detachedPayload) {
      this.ensureSignedOrVerifiedState();
      return detachedPayload
         ? this.header.toBase64URL().toString() + '.' + '.' + this.signature.toString()
         : this.signingInputString + '.' + this.signature.toString();
   }

   public static JWSObject parse(String s) throws ParseException {
      Base64URL[] parts = JOSEObject.split(s);
      if (parts.length != 3) {
         throw new ParseException("Unexpected number of Base64URL parts, must be three", 0);
      } else {
         return new JWSObject(parts[0], parts[1], parts[2]);
      }
   }

   public static JWSObject parse(String s, Payload detachedPayload) throws ParseException {
      Base64URL[] parts = JOSEObject.split(s);
      if (parts.length != 3) {
         throw new ParseException("Unexpected number of Base64URL parts, must be three", 0);
      } else if (!parts[1].toString().isEmpty()) {
         throw new ParseException("The payload Base64URL part must be empty", 0);
      } else {
         return new JWSObject(parts[0], detachedPayload, parts[2]);
      }
   }

   public static enum State {
      UNSIGNED,
      SIGNED,
      VERIFIED;
   }
}
