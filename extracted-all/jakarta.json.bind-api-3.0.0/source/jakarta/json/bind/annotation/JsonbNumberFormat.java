package jakarta.json.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER, ElementType.PACKAGE})
public @interface JsonbNumberFormat {
   String DEFAULT_LOCALE = "##default";

   String value() default "";

   String locale() default "##default";
}
