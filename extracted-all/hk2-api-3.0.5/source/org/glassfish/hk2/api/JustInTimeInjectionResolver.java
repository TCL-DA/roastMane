package org.glassfish.hk2.api;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface JustInTimeInjectionResolver {
   boolean justInTimeResolution(Injectee var1);
}
