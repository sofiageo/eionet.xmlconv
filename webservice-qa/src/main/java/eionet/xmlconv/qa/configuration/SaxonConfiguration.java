package eionet.xmlconv.qa.configuration;

import eionet.xmlconv.qa.services.saxon.SaxonProcessor;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.transform.stream.StreamSource;

/**
 *
 *
 */
@Configuration
public class SaxonConfiguration {

    @Bean
    public Processor saxonProcessor() {
        try {
            return new Processor(new StreamSource(SaxonProcessor.class.getResourceAsStream("/saxon-config.xml")));
        } catch (SaxonApiException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
