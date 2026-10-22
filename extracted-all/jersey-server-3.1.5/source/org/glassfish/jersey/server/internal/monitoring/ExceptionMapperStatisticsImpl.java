package org.glassfish.jersey.server.internal.monitoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ExceptionMapperStatistics;

final class ExceptionMapperStatisticsImpl implements ExceptionMapperStatistics {
   private final Map<Class<?>, Long> exceptionMapperExecutionCount;
   private final long successfulMappings;
   private final long unsuccessfulMappings;
   private final long totalMappings;

   private ExceptionMapperStatisticsImpl(
      Map<Class<?>, Long> exceptionMapperExecutionCount, long successfulMappings, long unsuccessfulMappings, long totalMappings
   ) {
      this.exceptionMapperExecutionCount = Collections.unmodifiableMap(exceptionMapperExecutionCount);
      this.successfulMappings = successfulMappings;
      this.unsuccessfulMappings = unsuccessfulMappings;
      this.totalMappings = totalMappings;
   }

   @Override
   public Map<Class<?>, Long> getExceptionMapperExecutions() {
      return this.exceptionMapperExecutionCount;
   }

   @Override
   public long getSuccessfulMappings() {
      return this.successfulMappings;
   }

   @Override
   public long getUnsuccessfulMappings() {
      return this.unsuccessfulMappings;
   }

   @Override
   public long getTotalMappings() {
      return this.totalMappings;
   }

   @Override
   public ExceptionMapperStatistics snapshot() {
      return this;
   }

   static class Builder {
      private Map<Class<?>, Long> exceptionMapperExecutionCountMap = new HashMap<>();
      private long successfulMappings;
      private long unsuccessfulMappings;
      private long totalMappings;
      private ExceptionMapperStatisticsImpl cached;

      void addMapping(boolean success, int count) {
         this.cached = null;
         this.totalMappings++;
         if (success) {
            this.successfulMappings += (long)count;
         } else {
            this.unsuccessfulMappings += (long)count;
         }
      }

      void addExceptionMapperExecution(Class<?> mapper, int count) {
         this.cached = null;
         Long cnt = this.exceptionMapperExecutionCountMap.get(mapper);
         cnt = cnt == null ? (long)count : cnt + (long)count;
         this.exceptionMapperExecutionCountMap.put(mapper, cnt);
      }

      public ExceptionMapperStatisticsImpl build() {
         if (this.cached == null) {
            this.cached = new ExceptionMapperStatisticsImpl(
               new HashMap<>(this.exceptionMapperExecutionCountMap), this.successfulMappings, this.unsuccessfulMappings, this.totalMappings
            );
         }

         return this.cached;
      }
   }
}
