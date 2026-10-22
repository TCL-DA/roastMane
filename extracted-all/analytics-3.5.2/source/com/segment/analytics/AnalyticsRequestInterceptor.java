package com.segment.analytics;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Interceptor.Chain;

class AnalyticsRequestInterceptor implements Interceptor {
   private static final String USER_AGENT_HEADER = "User-Agent";
   @Nonnull
   private final String userAgent;

   AnalyticsRequestInterceptor(@Nonnull String userAgent) {
      this.userAgent = userAgent;
   }

   public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      Request newRequest = request.newBuilder().addHeader("User-Agent", this.userAgent).build();
      return chain.proceed(newRequest);
   }
}
