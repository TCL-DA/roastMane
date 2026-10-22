package com.digitalpetri.enip.cip;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CipResponseException extends Exception {
   private final int generalStatus;
   private final int[] additionalStatus;

   public CipResponseException(int generalStatus, int[] additionalStatus) {
      this.generalStatus = generalStatus;
      this.additionalStatus = additionalStatus;
   }

   public int getGeneralStatus() {
      return this.generalStatus;
   }

   public int[] getAdditionalStatus() {
      return this.additionalStatus;
   }

   @Override
   public String getMessage() {
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("status=0x%02X", this.generalStatus));
      CipStatusCodes.getName(this.generalStatus).ifPresent(name -> sb.append(" [").append(name).append("] "));
      List<String> as = Arrays.stream(this.additionalStatus).mapToObj(a -> String.format("0x%04X", a)).collect(Collectors.toList());
      String additional = "[" + String.join(",", as) + "]";
      sb.append(", additional=").append(additional);
      return sb.toString();
   }

   public static Optional<CipResponseException> extract(Throwable ex) {
      if (ex instanceof CipResponseException) {
         return Optional.of((CipResponseException)ex);
      } else {
         Throwable cause = ex.getCause();
         return cause != null ? extract(cause) : Optional.empty();
      }
   }
}
