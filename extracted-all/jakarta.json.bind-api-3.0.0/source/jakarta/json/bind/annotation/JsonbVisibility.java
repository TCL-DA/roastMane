package jakarta.json.bind.annotation;

import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.PACKAGE})
public @interface JsonbVisibility {
   Class<? extends PropertyVisibilityStrategy> value();
}
