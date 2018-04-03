package org.lechisoft.minifw.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

public class MiniRealm extends AuthorizingRealm {

    RealmData data;

    public MiniRealm(RealmData data) {
        this.data = data;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = (String) token.getPrincipal();

        User user = this.data.getUser(userName);
        if (null == user) {
            throw new UnknownAccountException();
        }

        return new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(),
                ByteSource.Util.bytes(user.getSalt()), this.getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String userName = (String) principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.addRoles(this.data.getRoles(userName));
        authorizationInfo.addStringPermissions(this.data.getPermissions(userName));

        return authorizationInfo;
    }
}
