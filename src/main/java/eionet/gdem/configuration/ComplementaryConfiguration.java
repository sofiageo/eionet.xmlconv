package eionet.gdem.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 *
 *
 */
@Configuration
public class ComplementaryConfiguration {

    @LoadBalanced
    @Bean(name = "api-gateway")
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
