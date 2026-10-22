package org.glassfish.jersey.client.oauth2;

import jakarta.ws.rs.client.Client;

class AbstractAuthorizationCodeGrantBuilder<T extends OAuth2CodeGrantFlow.Builder> implements OAuth2CodeGrantFlow.Builder<T> {
   private final OAuth2CodeGrantFlow.Builder<T> delegate;

   public AbstractAuthorizationCodeGrantBuilder(OAuth2CodeGrantFlow.Builder<T> delegate) {
      this.delegate = delegate;
   }

   @Override
   public T accessTokenUri(String accessTokenUri) {
      this.delegate.accessTokenUri(accessTokenUri);
      return (T)this;
   }

   @Override
   public T authorizationUri(String authorizationUri) {
      this.delegate.authorizationUri(authorizationUri);
      return (T)this;
   }

   @Override
   public T redirectUri(String redirectUri) {
      this.delegate.redirectUri(redirectUri);
      return (T)this;
   }

   @Override
   public T clientIdentifier(ClientIdentifier consumerCredentials) {
      this.delegate.clientIdentifier(consumerCredentials);
      return (T)this;
   }

   @Override
   public T scope(String scope) {
      this.delegate.scope(scope);
      return (T)this;
   }

   @Override
   public T client(Client client) {
      this.delegate.client(client);
      return (T)this;
   }

   @Override
   public T refreshTokenUri(String refreshTokenUri) {
      this.delegate.refreshTokenUri(refreshTokenUri);
      return (T)this;
   }

   @Override
   public T property(OAuth2CodeGrantFlow.Phase phase, String key, String value) {
      this.delegate.property(phase, key, value);
      return (T)this;
   }

   @Override
   public OAuth2CodeGrantFlow build() {
      return this.delegate.build();
   }
}
