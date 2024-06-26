package ichigo.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
    //ElementType.TYPE,
    //ElementType.FIELD,
    //ElementType.CONSTRUCTOR,
    ElementType.METHOD
})
public @interface RequestMapping {
    String value() default "";
}
