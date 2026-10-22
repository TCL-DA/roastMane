package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ErrorService {
   void onFailure(ErrorInformation var1) throws MultiException;
}
