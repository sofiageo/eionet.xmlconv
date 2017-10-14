package eionet.gdem.configuration;

import eionet.gdem.web.filters.CASLoginFilter;
import eionet.gdem.web.filters.SetCharacterEncodingFilter;
import eionet.gdem.web.spring.qasandbox.TmpUploadServlet;
import eionet.rpcserver.servlets.XmlRpcRouter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.MultipartConfigElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Configuration
@EnableWebMvc
public class ServletConfiguration {

    @Bean
    @Order(2)
    public ServletRegistrationBean thymeleafDispatcher() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setConfigLocation("/WEB-INF/servlet-thymeleaf.xml");
        dispatcherServlet.setApplicationContext(applicationContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/");
        servletRegistrationBean.setName("thymeleafDispatcher");
        servletRegistrationBean.setMultipartConfig(new MultipartConfigElement("/tmp", 40971520l, 40971520l, 0));
        return servletRegistrationBean;
    }
    @Bean
    @Order(3)
    public ServletRegistrationBean dispatcherServlet() {
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        XmlWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setConfigLocation("/WEB-INF/servlet-context.xml");
        dispatcherServlet.setApplicationContext(applicationContext);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet, "/old/*");
        servletRegistrationBean.setName("jspDispatcher");
        servletRegistrationBean.setMultipartConfig(new MultipartConfigElement("/tmp", 40971520l, 40971520l, 0));
        return servletRegistrationBean;
    }
    @Bean
    @Order(1)
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

    @Value("${edu.yale.its.tp.cas.client.filter.validateUrl}")
    private String param1;
    @Value("${edu.yale.its.tp.cas.client.filter.serverName}")
    private String param2;
    @Value("${edu.yale.its.tp.cas.client.filter.loginUrl}")
    private String param3;
    @Value("${edu.yale.its.tp.cas.client.filter.wrapRequest}")
    private String param4;

    @Bean
    public FilterRegistrationBean casLoginFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CASLoginFilter());
        Map<String, String> params = new HashMap<>();
        params.put("edu.yale.its.tp.cas.client.filter.validateUrl", param1);
        params.put("edu.yale.its.tp.cas.client.filter.serverName", param2);
        params.put("edu.yale.its.tp.cas.client.filter.loginUrl", param3);
        params.put("edu.yale.its.tp.cas.client.filter.wrapRequest", param4);
        filterRegistrationBean.setInitParameters(params);
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/old/login", "/old/login/afterLogin"));
//        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        return filterRegistrationBean;
    }
}
