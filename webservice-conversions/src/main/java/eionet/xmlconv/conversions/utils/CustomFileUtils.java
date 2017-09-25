package eionet.xmlconv.conversions.utils;

import eionet.xmlconv.qa.Properties;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 *
 * @author George Sofianos
 */
public final class CustomFileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomFileUtils.class);

    public static String saveFileInLocalStorage(InputStream inputStream, String extension) throws IOException {
        String fileName =
                Properties.TMP_DIR + File.separatorChar + "gdem_" + System.currentTimeMillis() + "-" + UUID.randomUUID() + "."
                        + extension;
        File file = new File(fileName);

        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            //LOGGER.info("File stored locally url=" + url.toString() + " at " + fileName);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
        }

        return fileName;

    }
}
