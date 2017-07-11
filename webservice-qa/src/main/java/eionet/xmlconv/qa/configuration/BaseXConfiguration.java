package eionet.xmlconv.qa.configuration;

import org.basex.core.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class BaseXConfiguration {


    /**
     * Copying from BaseX mailing list:
     * It’s recommendable indeed to only create one instance of the Context class. Context instances are lightweight, but operations like
     * transactions are centrally controlled by this class. QueryProcessor are usually created anew for each query evaluation.
     * @return
     */
    @Bean
    public Context basexContext() {
        Context context = new Context();
        return context;
    }
}
