package eionet.gdem.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 *
 *
 */
@Component("jobScheduler")
public class SchedulerFactory extends SchedulerFactoryBean {

    private final DataSource quartzDataSource;

    @Autowired
    public SchedulerFactory(@Qualifier("quartzDataSource") DataSource quartzDataSource) {
        this.quartzDataSource = quartzDataSource;
    }
}
