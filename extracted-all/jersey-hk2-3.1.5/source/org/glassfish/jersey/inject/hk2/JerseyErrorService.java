package org.glassfish.jersey.inject.hk2;

import jakarta.inject.Singleton;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.Errors;

public final class JerseyErrorService implements ErrorService {
   public void onFailure(final ErrorInformation error) throws MultiException {
      final String msg;
      switch (error.getErrorType()) {
         case FAILURE_TO_REIFY:
            msg = LocalizationMessages.HK_2_REIFICATION_ERROR(error.getDescriptor().getImplementation(), this.printStackTrace(error.getAssociatedException()));
            break;
         default:
            msg = LocalizationMessages.HK_2_UNKNOWN_ERROR(this.printStackTrace(error.getAssociatedException()));
      }

      try {
         Errors.warning(error.getInjectee(), msg);
      } catch (IllegalStateException var4) {
         Errors.process(new Runnable() {
            @Override
            public void run() {
               Errors.warning(this, LocalizationMessages.HK_2_FAILURE_OUTSIDE_ERROR_SCOPE());
               Errors.warning(error.getInjectee(), msg);
            }
         });
      }
   }

   private String printStackTrace(Throwable t) {
      StringWriter sw = new StringWriter();
      t.printStackTrace(new PrintWriter(sw));
      return sw.toString();
   }

   public static final class Binder extends AbstractBinder {
      protected void configure() {
         this.bind(JerseyErrorService.class).to(ErrorService.class).in(Singleton.class);
      }
   }
}
