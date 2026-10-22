package org.glassfish.hk2.utilities;

import jakarta.inject.Singleton;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.MultiException;

@Singleton
public class IgnoringErrorService implements ErrorService {
   @Override
   public void onFailure(ErrorInformation errorInformation) throws MultiException {
   }
}
