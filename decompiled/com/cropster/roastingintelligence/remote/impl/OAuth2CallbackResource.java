package com.cropster.roastingintelligence.remote.impl;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.cropster.roastingintelligence.Defaults;

@Path("/roastingintelligence")
@Produces({"application/json;charset=utf-8"})
public class OAuth2CallbackResource {
   public static final String CALLBACK_PATH = "/callback";
   private final URI baseUri = Defaults.getBaseUri();
   private final OAuth2Connection oAuth2Connection;

   @Inject
   public OAuth2CallbackResource(OAuth2Connection oAuth2Connection) {
      this.oAuth2Connection = oAuth2Connection;
   }

   @GET
   @Path("/callback")
   public Response finishFlow(@QueryParam("code") String code, @QueryParam("scope") String scope, @QueryParam("state") String state) {
      this.oAuth2Connection.finishFlow(code, state);
      return Response.temporaryRedirect(this.baseUri).build();
   }
}
