package eionet.gdem;

import eionet.gdem.security.WebSecurityConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 *
 */
@Configuration
//@ComponentScan
//@EnableAutoConfiguration
@EnableHystrixDashboard
@EnableEurekaServer
@ImportResource({"classpath:spring-app-context.xml", "classpath:spring-datasource-context.xml",
        "classpath:spring-jpa.xml", "classpath:spring-quartz-datasource.xml", "classpath:spring-runtime.xml"})
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class).web(true);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

//    public static void main(String[] args) {
//        new SpringApplicationBuilder(Application.class).web(true).run(args);
//    }

}
