package eionet.gdem.configuration;

import eionet.gdem.web.filters.CASLoginFilter;
import eionet.gdem.web.filters.SetCharacterEncodingFilter;
import eionet.gdem.web.listeners.JobScheduler;
import eionet.gdem.web.spring.qasandbox.TmpUploadServlet;
import eionet.rpcserver.servlets.XmlRpcRouter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Configuration
public class ServletConfiguration {

    @Bean
    public ServletRegistrationBean thymeleafDispatcher() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setConfigLocation("/WEB-INF/servlet-thymeleaf.xml");
        dispatcherServlet.setApplicationContext(applicationContext);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/new/*");
        servletRegistrationBean.setName("thymeleafDispatcher");
        return servletRegistrationBean;
    }
    @Bean
    public ServletRegistrationBean jspDispatcher() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setConfigLocation("/WEB-INF/servlet-context.xml");
        dispatcherServlet.setApplicationContext(applicationContext);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/");
        servletRegistrationBean.setName("jspDispatcher");
        return servletRegistrationBean;
    }
    @Bean
    public ServletRegistrationBean restDispatcher() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setConfigLocation("/WEB-INF/servlet-restapi.xml");
        dispatcherServlet.setApplicationContext(applicationContext);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/restapi/*");
        servletRegistrationBean.setName("restDispatcher");
        return servletRegistrationBean;
    }
    @Bean
    public ServletRegistrationBean rpcRouter() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        XmlRpcRouter xmlRpcRouter = new XmlRpcRouter();
        servletRegistrationBean.setServlet(xmlRpcRouter);
        servletRegistrationBean.setUrlMappings(Arrays.asList("/RpcRouter"));
        return servletRegistrationBean;
    }
    @Bean
    public ServletRegistrationBean tmpUploadServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
        TmpUploadServlet tmpUploadServlet = new TmpUploadServlet();
        servletRegistrationBean.setServlet(tmpUploadServlet);
        servletRegistrationBean.setUrlMappings(Arrays.asList("/qasandbox/upload"));
        return servletRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean springSecurityFilterChain() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy delegatingFilterProxy = new DelegatingFilterProxy();
        filterRegistrationBean.setFilter(delegatingFilterProxy);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("*"));
        return filterRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean encodingFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        SetCharacterEncodingFilter setCharacterEncodingFilter = new SetCharacterEncodingFilter();
        filterRegistrationBean.setFilter(setCharacterEncodingFilter);
        Map<String, String> initParams = new HashMap();
        initParams.put("encoding", "UTF-8");
        filterRegistrationBean.setInitParameters(initParams);
        return filterRegistrationBean;
    }


    // TODO: Add JOB SCHEDULER and CAS FILTER.
/*    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> jobScheduler() {
        ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
        JobScheduler jobScheduler = new JobScheduler();
        servletListenerRegistrationBean.setListener(jobScheduler);
        return servletListenerRegistrationBean;
    }*/

//    @Bean
//    public FilterRegistrationBean casFilter() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        CASLoginFilter casLoginFilter = new CASLoginFilter();
//        filterRegistrationBean.setFilter(casLoginFilter);
//        filterRegistrationBean.setOrder(1);
//        filterRegistrationBean.setUrlPatterns(Arrays.asList("/login", "/login/afterlogin"));
//        return filterRegistrationBean;
//    }
}
