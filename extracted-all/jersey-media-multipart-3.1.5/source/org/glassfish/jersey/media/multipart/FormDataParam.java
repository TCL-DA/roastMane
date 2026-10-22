package org.glassfish.jersey.media.multipart;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.glassfish.jersey.server.model.ParamQualifier;

@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@ParamQualifier
public @interface FormDataParam {
   String value();
}
