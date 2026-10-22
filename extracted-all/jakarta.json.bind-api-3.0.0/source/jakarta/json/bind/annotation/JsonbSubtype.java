package jakarta.json.bind.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface JsonbSubtype {
   String alias();

   Class<?> type();
}
