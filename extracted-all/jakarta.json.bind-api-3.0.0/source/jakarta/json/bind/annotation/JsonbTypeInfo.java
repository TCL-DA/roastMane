package jakarta.json.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface JsonbTypeInfo {
   String DEFAULT_KEY_NAME = "@type";

   String key() default "@type";

   JsonbSubtype[] value() default {};
}
