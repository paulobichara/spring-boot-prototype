package org.paulobichara.prototype.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.annotation.Secured;

/**
 * Annotation that indicates that the given method can only be accessed by admins.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Secured("ROLE_ADMIN")
public @interface IsAdmin {
}
