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
@Repeatable(Digits.List.class)
@Documented
@Constraint(
   validatedBy = {}
)
public @interface Digits {
   String message() default "{jakarta.validation.constraints.Digits.message}";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};

   int integer();

   int fraction();

   @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   public @interface List {
      Digits[] value();
   }
}
