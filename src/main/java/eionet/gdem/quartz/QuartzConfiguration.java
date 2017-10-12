package eionet.gdem.quartz;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 *
 *
 */
@Configuration
public class QuartzConfiguration {

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean normalSchedulerFactoryBean(@Autowired @Qualifier("quartzDataSource") DataSource dataSource, JobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactory.setJobFactory(jobFactory);
        //schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    @Bean
    public SchedulerFactoryBean heavySchedulerFactoryBean(@Autowired @Qualifier("quartzDataSource") DataSource dataSource, JobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz-heavy.properties"));
        schedulerFactory.setJobFactory(jobFactory);
        //schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    @Bean
    public SchedulerFactoryBean localSchedulerFactoryBean(@Autowired @Qualifier("quartzDataSource") DataSource dataSource, JobFactory jobFactory) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setConfigLocation(new ClassPathResource("local-quartz.properties"));
        schedulerFactory.setJobFactory(jobFactory);
        //schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }
}
