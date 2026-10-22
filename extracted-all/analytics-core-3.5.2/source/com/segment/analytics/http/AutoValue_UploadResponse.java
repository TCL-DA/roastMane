package com.segment.analytics.http;

final class AutoValue_UploadResponse extends UploadResponse {
   private final boolean success;

   AutoValue_UploadResponse(boolean success) {
      this.success = success;
   }

   @Override
   public boolean success() {
      return this.success;
   }

   @Override
   public String toString() {
      return "UploadResponse{success=" + this.success + "}";
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o instanceof UploadResponse) {
         UploadResponse that = (UploadResponse)o;
         return this.success == that.success();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int h$ = 1;
      h$ *= 1000003;
      return h$ ^ (this.success ? 1231 : 1237);
   }
}
