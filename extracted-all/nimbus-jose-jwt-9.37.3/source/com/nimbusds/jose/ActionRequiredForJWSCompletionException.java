package com.nimbusds.jose;

public class ActionRequiredForJWSCompletionException extends JOSEException {
   private final JWSSignerOption option;
   private final CompletableJWSObjectSigning completableSigning;

   public ActionRequiredForJWSCompletionException(String message, JWSSignerOption option, CompletableJWSObjectSigning completableSigning) {
      super(message);
      if (option == null) {
         throw new IllegalArgumentException("The triggering option must not be null");
      } else {
         this.option = option;
         if (completableSigning == null) {
            throw new IllegalArgumentException("The completable signing must not be null");
         } else {
            this.completableSigning = completableSigning;
         }
      }
   }

   public JWSSignerOption getTriggeringOption() {
      return this.option;
   }

   public CompletableJWSObjectSigning getCompletableJWSObjectSigning() {
      return this.completableSigning;
   }
}
