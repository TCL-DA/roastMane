package jakarta.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DecimalMin.List.class)
@Documented
@Constraint(
   validatedBy = {}
)
public @interface DecimalMin {
   String message() default "{jakarta.validation.constraints.DecimalMin.message}";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};

   String value();

   boolean inclusive() default true;

   @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   public @interface List {
      DecimalMin[] value();
   }
}
