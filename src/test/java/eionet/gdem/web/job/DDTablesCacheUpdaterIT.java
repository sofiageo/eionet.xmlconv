package eionet.gdem.web.job;

import eionet.gdem.cache.CacheManagerUtil;
import eionet.gdem.test.ApplicationTestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContextEvent;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author George Sofianos
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationTestContext.class })
public class DDTablesCacheUpdaterIT {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private ServletContextEvent event = mock(ServletContextEvent.class);

    @Test
    public void testUpdateCache() throws SchedulerException {
        int emptyCacheSize = CacheManagerUtil.getDDTables().size();
        //Example at: http://www.citerus.se/mocking-to-the-rescue/
        JobExecutionContext mockContext = mock(JobExecutionContext.class);
        JobDetail detail = mock(JobDetail.class);
        JobDataMap map = new JobDataMap();
        map.put("indexCount", 145);
        when(detail.getJobDataMap()).thenReturn(map);
        when(mockContext.getJobDetail()).thenReturn(detail);

        DDTablesCacheUpdater updater = new DDTablesCacheUpdater();
        updater.execute(mockContext);
        assertTrue("Expected updated cache: ", CacheManagerUtil.getDDTables().size() > emptyCacheSize);
    }

}