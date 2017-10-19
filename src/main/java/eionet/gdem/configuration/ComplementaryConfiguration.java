package eionet.gdem.configuration;

import eionet.gdem.SpringApplicationContext;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringValueResolver;
import org.springframework.web.client.RestTemplate;

/**
 *
 *
 */
@Configuration
@PropertySource({ "classpath:env.properties", "classpath:acl.properties", "classpath:gdem.properties", "classpath:eionetdir.properties",
        "classpath:CatalogManager.properties", "classpath:fme.properties", "classpath:messages.properties", "classpath:cache.properties",
        "classpath:http.properties"})
public class ComplementaryConfiguration {

    @LoadBalanced
    @Bean
    RestTemplate loadBalancedTemplate() {
        return new RestTemplate(getClientHttpRequestFactory());
    }

    @Primary
    @Bean
    RestTemplate defaultTemplate() {
        return new RestTemplate(getClientHttpRequestFactory());
    }


    @Bean
    @Primary
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());
    }

//    @Primary
//    @Bean
//    StringValueResolver primaryResolver() { return new EmbeddedValueResolver(SpringApplicationContext.CONTEXT.getParentBeanFactory().resolve); }
}
