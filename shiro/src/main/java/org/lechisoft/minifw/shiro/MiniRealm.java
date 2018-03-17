package org.lechisoft.minifw.shiro;

import org.apache.shiro.authc.*;
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
        this.setHashedCredentialsMatcher("MD5", 1, true);
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

    /**
     * 加密
     *
     * @param hashAlgorithmName           散列算法的名字，如MD5，SHA-256等
     * @param hashIterations              散列迭代次数
     * @param storedCredentialsHexEncoded true:Hex,false:Base64
     */
    public void setHashedCredentialsMatcher(String hashAlgorithmName, int hashIterations, boolean storedCredentialsHexEncoded) {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName(hashAlgorithmName);
        credentialsMatcher.setHashIterations(hashIterations);
        credentialsMatcher.setStoredCredentialsHexEncoded(storedCredentialsHexEncoded);
        this.setCredentialsMatcher(credentialsMatcher);
    }
}
