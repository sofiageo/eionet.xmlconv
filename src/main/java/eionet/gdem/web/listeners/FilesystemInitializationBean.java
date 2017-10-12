package eionet.gdem.web.listeners;

import eionet.gdem.Properties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.File;

/**
 *
 *
 */
@Component
@DependsOn("configurationPostProcessor")
public class FilesystemInitializationBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemInitializationBean.class);
    private static int times = 0;

    @PostConstruct
    public void init() {
        cleanDirectories();
        checkFolders();
/*        times++;
        LOGGER.info("WORKS: " + times);*/
    }

    /**
     * Checks if such folders exists, if not, they are created.
     */
    private void checkFolders() {
        String[] folders
                = {Properties.xslFolder, Properties.getXslFolder(), Properties.getTmpFolder(), Properties.getXmlfileFolder(),
                Properties.schemaFolder, Properties.tmpfileDir, Properties.CACHE_TEMP_DIR};

        for (String folder : folders) {
            File f = new File(folder);
            if (!f.isDirectory()) {

                if (!f.mkdirs()) {
                    LOGGER.warn("Could not create folder: " + f.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Clears directories from left-over files
     *
     */
    private void cleanDirectories() {
        String[] directories = {Properties.tmpfileDir};
        for (String directory : directories) {
            File f = new File(directory);
            try {
                FileUtils.cleanDirectory(f);
            } catch (Exception e) {
                LOGGER.error("Could not remove directory: " + f.getAbsolutePath());
            }
        }
    }
}
