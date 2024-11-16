package de.neo8.jagil.annotation;

import java.lang.annotation.*;

/**
 * Marks a method or constructor as unstable.
 * Objects with this annotation should be used with caution.
 */
@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Internal(forVisibilityChange = false)
public @interface UnstableFeature {
}
