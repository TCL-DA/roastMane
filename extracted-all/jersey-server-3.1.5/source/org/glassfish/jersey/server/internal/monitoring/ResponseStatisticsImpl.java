package org.glassfish.jersey.server.internal.monitoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ResponseStatistics;

final class ResponseStatisticsImpl implements ResponseStatistics {
   private final Map<Integer, Long> responseCodes;
   private final Integer lastResponseCode;

   private ResponseStatisticsImpl(Integer lastResponseCode, Map<Integer, Long> responseCodes) {
      this.lastResponseCode = lastResponseCode;
      this.responseCodes = Collections.unmodifiableMap(responseCodes);
   }

   @Override
   public Integer getLastResponseCode() {
      return this.lastResponseCode;
   }

   @Override
   public Map<Integer, Long> getResponseCodes() {
      return this.responseCodes;
   }

   @Override
   public ResponseStatistics snapshot() {
      return this;
   }

   static class Builder {
      private final Map<Integer, Long> responseCodesMap = new HashMap<>();
      private Integer lastResponseCode = null;
      private ResponseStatisticsImpl cached = null;

      void addResponseCode(int responseCode) {
         this.cached = null;
         this.lastResponseCode = responseCode;
         Long currentValue = this.responseCodesMap.get(responseCode);
         if (currentValue == null) {
            currentValue = 0L;
         }

         this.responseCodesMap.put(responseCode, currentValue + 1L);
      }

      ResponseStatisticsImpl build() {
         if (this.cached == null) {
            this.cached = new ResponseStatisticsImpl(this.lastResponseCode, new HashMap<>(this.responseCodesMap));
         }

         return this.cached;
      }
   }
}
