package es.sandwatch.trim.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lets you specify multiple headers to be sent with an endpoint request.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Headers{
    Header[] value();
}
