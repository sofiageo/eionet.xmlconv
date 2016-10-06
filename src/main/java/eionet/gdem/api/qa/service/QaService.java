package eionet.gdem.api.qa.service;

import eionet.gdem.api.errors.QaServiceException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface QaService {

    HashMap<String, String> extractSchemasAndFilesFromEnvelopeUrl(String envelope_url) throws QaServiceException;
    
    /**
     *  creates a table out of a Map of File Schemas and Links and feeds it to : {@link eionet.gdem.qa.XQueryService#analyzeXMLFiles(java.util.Hashtable)  }
     *  which returns a vector of vectors. Each of these vectors contain a JobID and a FileURL.
     * 
     * @return a map containing each Job Id and corresponding File URL as Key value pair.
     */
    LinkedHashMap<String,String> scheduleJobs(HashMap<String, String> fileSchemasAndLinks) throws QaServiceException;
    Vector runQaScript(String sourceUrl, String scriptId) throws QaServiceException;
}
