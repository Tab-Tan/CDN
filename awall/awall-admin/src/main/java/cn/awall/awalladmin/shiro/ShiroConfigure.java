package cn.awall.awalladmin.shiro;

import cn.awall.awalladmin.shiro.relam.MRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
//        manager.setSessionManager(getSessionManager());
//        manager.setRememberMeManager(cookieManager());
        return manager;
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
//        map.put("/**","authc");S
        map.put("/**","anon");
        factoryBean.setFilterChainDefinitionMap(map);
        return factoryBean;
    }
}
