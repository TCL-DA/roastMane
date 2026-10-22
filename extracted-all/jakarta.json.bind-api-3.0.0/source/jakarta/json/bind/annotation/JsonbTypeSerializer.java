package jakarta.json.bind.annotation;

import jakarta.json.bind.serializer.JsonbSerializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface JsonbTypeSerializer {
   Class<? extends JsonbSerializer> value();
}
