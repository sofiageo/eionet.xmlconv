package eionet.xmlconv.qa.services;

import eionet.gdem.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Type for resolving QA URIs. If XML file is referenced from XQuery as file in the root folder, then it is resolved to correct
 * location in filesystem.
 *
 * @author Enriko Käsper
 */

public class QAURIResolver implements URIResolver {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAURIResolver.class);

    /*
     * (non-Javadoc)
     *
     * @see javax.xml.transform.URIResolver#resolve(java.lang.String, java.lang.String)
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        Source resolveResult = null;
        if (!href.contains("/") && !href.contains("\\") && !href.endsWith(".xquery")) {
            String xmlFilePath = Properties.xmlfileFolder + File.separator + href;
            File file = new File(xmlFilePath);
            if (file.exists()) {
                LOGGER.debug("Streaming XML file from local folder: " + xmlFilePath);
                return new StreamSource(file);
            }
        }
        return resolveResult;
    }
}
