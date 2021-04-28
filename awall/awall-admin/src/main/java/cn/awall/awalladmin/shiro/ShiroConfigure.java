package cn.awall.awalladmin.shiro;

import cn.awall.awalladmin.shiro.relam.MRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Configuration
public class ShiroConfigure  {


    //注解授权管理
    @Bean
    DefaultAdvisorAutoProxyCreator creator(){
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
    //注解授权管理
    @Bean
    public AuthorizationAttributeSourceAdvisor advisor(DefaultWebSecurityManager manager){
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

    //加密加盐
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        return new HashedCredentialsMatcher("md5");
    }


    /***
     * 自定义realm
     * @param matcher
     * @return
     */
    @Bean
    public MRealm realm(HashedCredentialsMatcher matcher){
        MRealm myRealm = new MRealm();
        //设置加密适配器-让自定义Realm支持指定加密方式
        myRealm.setCredentialsMatcher(matcher);
        return myRealm;
    }


    //安全管理器
    @Bean
    public DefaultWebSecurityManager getManager(MRealm realm){

        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        manager.setSessionManager(getSessionManager());
        manager.setRememberMeManager(cookieManager());
        return manager;
    }

    //rememberMe
    @Bean
    public CookieRememberMeManager cookieManager(){
        CookieRememberMeManager manager = new CookieRememberMeManager();
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        cookie.setMaxAge(30*24*60*60);
        manager.setCookie(cookie);
        manager.setCookie(cookie);
        manager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
        return manager;
    }

    //设置Session
    @Bean
    public DefaultWebSessionManager getSessionManager(){
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(24*60*60*1000);
        return sessionManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(WebSecurityManager manager){
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(manager);
        factoryBean.setLoginUrl("/login");
        HashMap<String, String> map = new HashMap<>();
//        map.put("/login","anon");
//        map.put("/articles/**","anon");
//        map.put("/register","anon");
//        map.put("/sms","anon");
//        map.put("/tel","anon");
//        map.put("/getVerifyCode","anon");
//        map.put("/**","authc");
        map.put("/**","anon");
        factoryBean.setFilterChainDefinitionMap(map);
        return factoryBean;
    }
}
