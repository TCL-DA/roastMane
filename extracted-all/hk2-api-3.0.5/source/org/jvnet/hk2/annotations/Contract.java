package org.jvnet.hk2.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.glassfish.hk2.api.ContractIndicator;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ContractIndicator
public @interface Contract {
}
