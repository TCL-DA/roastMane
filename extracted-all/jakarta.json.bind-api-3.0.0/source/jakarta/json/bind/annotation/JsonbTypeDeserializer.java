package jakarta.json.bind.annotation;

import jakarta.json.bind.serializer.JsonbDeserializer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@JsonbAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface JsonbTypeDeserializer {
   Class<? extends JsonbDeserializer> value();
}
