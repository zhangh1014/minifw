package org.lechisoft.minifw.shiro;

import com.sun.istack.internal.NotNull;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import java.util.Arrays;

public class MiniSecurity {

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static Session getSession() {
        return MiniSecurity.getSubject().getSession();
    }

    public static String getSessionId() {
        return MiniSecurity.getSubject().getSession().getId().toString();
    }

    public static Object getSessionAttribute(Object key) {
        return MiniSecurity.getSubject().getSession().getAttribute(key);
    }

    public static String getHash(String algorithmName, Object source) {
        return MiniSecurity.getHash(algorithmName, source, "", 1);
    }

    public static String getHash(String algorithmName, Object source, Object salt) {
        return MiniSecurity.getHash(algorithmName, source, salt, 1);
    }

    public static String getHash(String algorithmName, Object source, Object salt, int hashIterations) {
        return new SimpleHash(algorithmName, source, salt, hashIterations).toString();
    }

    public static void setSessionAttribute(Object key, Object value) {
        MiniSecurity.getSubject().getSession().setAttribute(key, value);
    }

    public static boolean isAuthenticated() {
        return MiniSecurity.getSubject().isAuthenticated();
    }

    public static boolean isRemembered() {
        return MiniSecurity.getSubject().isRemembered();
    }

    public static void signIn(String userName, String password) {
        MiniSecurity.signIn(userName, password, false);
    }

    public static void signIn(String userName, String password, boolean rememberMe) {
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
        if(rememberMe){
            token.setRememberMe(true);
        }
        MiniSecurity.getSubject().login(token);
    }

    public static void signOut() {
        MiniSecurity.getSubject().logout();
    }

    public static boolean isPermitted(String permission) {
        Subject subject = MiniSecurity.getSubject();
        return subject.isPermitted(permission);
    }

    public static boolean isPermittedAll(@NotNull String... permissions) {
        Subject subject = MiniSecurity.getSubject();
        return subject.isPermittedAll(permissions);
    }

    public static boolean isPermittedAny(@NotNull String... permissions) {
        Subject subject = MiniSecurity.getSubject();
        for (String permission : permissions) {
            if (subject.isPermitted(permission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(String role) {
        Subject subject = MiniSecurity.getSubject();
        return subject.hasRole(role);
    }

    public static boolean hasAllRoles(String... roles) {
        Subject subject = MiniSecurity.getSubject();
        return subject.hasAllRoles(Arrays.asList(roles));
    }

    public static boolean hasAnyRole(@NotNull String... roles) {
        Subject subject = MiniSecurity.getSubject();
        for (String role : roles) {
            if (subject.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}