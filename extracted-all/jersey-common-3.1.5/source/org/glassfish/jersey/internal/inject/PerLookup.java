package org.glassfish.jersey.internal.inject;

import jakarta.inject.Scope;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Scope
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface PerLookup {
}
