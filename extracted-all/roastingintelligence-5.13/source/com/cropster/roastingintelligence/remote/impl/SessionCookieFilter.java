package com.cropster.roastingintelligence.remote.impl;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Provider
public final class SessionCookieFilter implements ClientRequestFilter, ClientResponseFilter {
   private final String cookieName;
   private AtomicReference<Cookie> sessionCookie = new AtomicReference<>();

   public SessionCookieFilter(String cookieName) {
      this.cookieName = cookieName;
   }

   public void clearSessionCookie() {
      this.sessionCookie = null;
   }

   public void filter(ClientRequestContext requestContext) throws IOException {
      requestContext.getHeaders().add("Accept-Encoding", "gzip");
      if (this.sessionCookie.get() != null) {
         String cookieString = String.format("%s=%s", this.cookieName, this.sessionCookie.get().getValue());
         requestContext.getHeaders().add("Cookie", cookieString);
      }
   }

   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
      if (responseContext.getCookies().containsKey(this.cookieName)) {
         NewCookie newCookie = (NewCookie)responseContext.getCookies().get(this.cookieName);
         this.sessionCookie.set(newCookie);
      }
   }
}
