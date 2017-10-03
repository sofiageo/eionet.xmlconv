package eionet.xmlconv.conversions;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 *
 */
@Configuration
@ImportResource({"/test-spring-app-context.xml",
        "/test-datasource-context.xml", "/test-runtime.xml"})
public class ApplicationTestContext {

}
