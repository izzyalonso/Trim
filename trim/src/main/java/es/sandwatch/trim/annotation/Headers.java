package es.sandwatch.trim.annotation;

/**
 * Lets you specify multiple headers to be sent with an endpoint request.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public @interface Headers{
    Header[] value();
}
