package eionet.xmlconv.qa.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 */
@Configuration
public class JacksonConfiguration {


    /**
     * From Jackson wiki http://wiki.fasterxml.com/JacksonInFiveMinutes
     * mapper can be reused, shared globally
     * @return Mapper
     */
    @Bean
    public ObjectMapper tasobjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        return objectMapper;
    }
}
