package top.candyboy.access;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    public int seconds();
    public int maxCount();
    public boolean needLogin() default true;
}
