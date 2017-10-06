package eionet.gdem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 *
 */
@Configuration
@EnableDiscoveryClient
//@SpringBootConfiguration
@SpringBootApplication
@ImportResource({"classpath:spring-app-context.xml", "classpath:spring-datasource-context.xml",
        "classpath:spring-jpa.xml", "classpath:spring-quartz-datasource.xml", "classpath:spring-runtime.xml"})
public class Application {

    //extends SpringBootServletInitializer
/*    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class).web(true);
    }*/

//    public static void main(String[] args) throws Exception {
//        SpringApplication.run(Application.class, args);
//    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class).web(true).run(args);
    }

}
