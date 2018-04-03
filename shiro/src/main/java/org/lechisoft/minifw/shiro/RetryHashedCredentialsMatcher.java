package org.lechisoft.minifw.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;

import java.util.concurrent.atomic.AtomicInteger;

public class RetryHashedCredentialsMatcher extends HashedCredentialsMatcher {
    private Cache<String, AtomicInteger> cache;

    private int maxRetry = 5;
    public int getMaxRetry() {
        return maxRetry;
    }
    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public RetryHashedCredentialsMatcher() {
        CacheManager cacheManager = new MemoryConstrainedCacheManager();
        this.cache = cacheManager.getCache("miniCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String userName = (String) token.getPrincipal();

        //retry count + 1
        AtomicInteger retryCount = this.cache.get(userName);
        if (null == retryCount) {
            retryCount = new AtomicInteger(0);
            this.cache.put(userName, retryCount);
        }
        if (retryCount.incrementAndGet() > this.maxRetry) {
            MiniLogger.warn(userName + " tried to login more than 5 times in period");
            throw new ExcessiveAttemptsException(userName + " tried to login more than 5 times in period");
        }
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //clear retry data
            this.cache.remove(userName);
        }
        return matches;
    }
}
