package org.paulobichara.prototype.security;

import lombok.Getter;
import lombok.Setter;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.security.annotation.IsAdmin;
import org.paulobichara.prototype.security.annotation.IsAdminOrOwner;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * The purpose of this class is to create a custom SpEl operand. This operand is defined by the method 'isOwner' and
 * has the same name as the method. The use of this operand can be found on
 * {@link IsAdminOrOwner} and {@link IsAdmin} annotation.
 */
class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements
    MethodSecurityExpressionOperations {

    @Getter @Setter private Object filterObject;

    @Getter @Setter private Object returnObject;

    private final UserDetailsService userDetailsService;

    CustomMethodSecurityExpressionRoot(Authentication authentication, UserDetailsService userDetailsService) {
        super(authentication);
        this.userDetailsService = userDetailsService;
    }

    /**
     * Method that checks if the user id passed as parameter is the same as the one from the authenticated user.
     * @param userId id of the user owner of the resource being requested
     * @return true if it is the owner or false if it is not.
     */
    @SuppressWarnings("unused")
    public boolean isOwner(Long userId) {
        final User user;
        if (this.getPrincipal() instanceof UserPrincipal) {
            user = ((UserPrincipal)this.getPrincipal()).getUser();
        } else {
            user = ((UserPrincipal) userDetailsService.loadUserByUsername(this.getPrincipal().toString())).getUser();
        }

        return user.getId().equals(userId);
    }

    @Override
    public Object getThis() {
        return this;
    }

}
