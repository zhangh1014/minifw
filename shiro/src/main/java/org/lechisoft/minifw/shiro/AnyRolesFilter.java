package org.lechisoft.minifw.shiro;

import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class AnyRolesFilter extends AuthorizationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String[] roles = (String[]) mappedValue;

        return null == roles || roles.length == 0 || MiniSecurity.hasAnyRole(roles);

    }
}