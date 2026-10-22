package jakarta.json.bind.annotation;

import jakarta.json.bind.adapter.JsonbAdapter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface JsonbTypeAdapter {
   Class<? extends JsonbAdapter> value();
}
