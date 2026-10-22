package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.core.Response.Status.Family;

public final class Statuses {
   public static StatusType from(int code) {
      StatusType result = Status.fromStatusCode(code);
      return (StatusType)(result != null ? result : new Statuses.StatusImpl(code, ""));
   }

   public static StatusType from(int code, String reason) {
      return new Statuses.StatusImpl(code, reason);
   }

   public static StatusType from(StatusType status, String reason) {
      return new Statuses.StatusImpl(status.getStatusCode(), reason);
   }

   private Statuses() {
      throw new AssertionError("Instantiation not allowed.");
   }

   private static final class StatusImpl implements StatusType {
      private final int code;
      private final String reason;
      private final Family family;

      private StatusImpl(int code, String reason) {
         this.code = code;
         this.reason = reason;
         this.family = Family.familyOf(code);
      }

      public int getStatusCode() {
         return this.code;
      }

      public String getReasonPhrase() {
         return this.reason;
      }

      @Override
      public String toString() {
         return this.reason;
      }

      public Family getFamily() {
         return this.family;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof StatusType)) {
            return false;
         } else {
            StatusType status = (StatusType)o;
            if (this.code != status.getStatusCode()) {
               return false;
            } else if (this.family != status.getFamily()) {
               return false;
            } else {
               return this.reason != null ? this.reason.equals(status.getReasonPhrase()) : status.getReasonPhrase() == null;
            }
         }
      }

      @Override
      public int hashCode() {
         int result = this.code;
         result = 31 * result + (this.reason != null ? this.reason.hashCode() : 0);
         return 31 * result + this.family.hashCode();
      }
   }
}
