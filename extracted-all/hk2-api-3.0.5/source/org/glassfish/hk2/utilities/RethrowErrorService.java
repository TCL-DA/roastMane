package org.glassfish.hk2.utilities;

import jakarta.inject.Singleton;
import java.util.Objects;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.MultiException;

@Singleton
public class RethrowErrorService implements ErrorService {
   @Override
   public void onFailure(ErrorInformation errorInformation) throws MultiException {
      Objects.requireNonNull(errorInformation, "errorInformation must not be null!");
      if (ErrorType.FAILURE_TO_REIFY.equals(errorInformation.getErrorType())) {
         MultiException me = errorInformation.getAssociatedException();
         if (me != null) {
            throw me;
         }
      }
   }
}
