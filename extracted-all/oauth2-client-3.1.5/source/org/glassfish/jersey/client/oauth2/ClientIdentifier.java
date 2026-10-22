package org.glassfish.jersey.client.oauth2;

public class ClientIdentifier {
   private final String clientId;
   private final byte[] clientSecret;

   public ClientIdentifier(String clientId, String clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret.getBytes();
   }

   public ClientIdentifier(String clientId, byte[] clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
   }

   public String getClientId() {
      return this.clientId;
   }

   public String getClientSecret() {
      return new String(this.clientSecret);
   }

   public byte[] getClientSecretAsByteArray() {
      return this.clientSecret;
   }
}
