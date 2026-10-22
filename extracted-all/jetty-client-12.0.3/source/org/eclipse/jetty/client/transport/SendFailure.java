package org.eclipse.jetty.client.transport;

public class SendFailure {
   public final Throwable failure;
   public final boolean retry;

   public SendFailure(Throwable failure, boolean retry) {
      this.failure = failure;
      this.retry = retry;
   }

   @Override
   public String toString() {
      return String.format("%s@%x[failure=%s,retry=%b]", this.getClass().getSimpleName(), this.hashCode(), this.failure, this.retry);
   }
}
