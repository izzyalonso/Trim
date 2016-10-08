package es.sandwatch.trim.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Tells trim the generic type of a class attribute mapping to an array. This is necessary because
 * of type erasure. This annotation will only be considered in objects whose type is a subclass of
 * Collection.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CollectionGenericType{
    Class<?> value();
}
