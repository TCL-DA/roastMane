package com.cropster.roastingintelligence.remote.impl;

import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;

public final class OAuthState {
   private static final OAuthState INSTANCE = new OAuthState();
   private OAuth2CodeGrantFlow flow;

   public static OAuthState get() {
      return INSTANCE;
   }

   private OAuthState() {
   }

   public synchronized OAuth2CodeGrantFlow getFlow() {
      return this.flow;
   }

   public synchronized void setFlow(OAuth2CodeGrantFlow flow) {
      this.flow = flow;
   }
}
