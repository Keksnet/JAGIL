package de.neo8.jagil.annotation;

import java.lang.annotation.*;

/**
 * Marks a method, package, constructor or annotation as {@link Internal}.
 * The marked objects should not be used.
 */
@Documented
@Target({ElementType.METHOD, ElementType.PACKAGE, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Internal {
}
