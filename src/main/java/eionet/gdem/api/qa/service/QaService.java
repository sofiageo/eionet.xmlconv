package eionet.gdem.api.qa.service;

import eionet.gdem.api.errors.QaServiceException;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface QaService {

    HashMap<String, String> extractSchemasAndFilesFromEnvelopeUrl(String envelope_url) throws QaServiceException;
    Vector scheduleJobs(HashMap<String, String> fileSchemasAndLinks) throws QaServiceException;
    Vector runQaScript(String sourceUrl, String scriptId) throws QaServiceException;
}
