package org.paulobichara.prototype.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Annotation that indicates that the given method can only be accessed by admins or the user owner
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') && isOwner(#userId))")
public @interface IsAdminOrOwner {
}
