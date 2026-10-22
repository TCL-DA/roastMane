package jakarta.validation.groups;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConvertGroup.List.class)
@Documented
public @interface ConvertGroup {
   Class<?> from() default Default.class;

   Class<?> to();

   @Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
   @Retention(RetentionPolicy.RUNTIME)
   @Documented
   public @interface List {
      ConvertGroup[] value();
   }
}
