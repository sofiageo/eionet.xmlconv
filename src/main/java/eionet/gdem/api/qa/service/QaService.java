package eionet.gdem.api.qa.service;

import eionet.gdem.api.errors.QaServiceException;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface QaService {

    /**
     *
     * Given an envelopeUrl , it makes a call to the envelopeUrl/xml and parses the output XML stream 
     * in order to extract the Schemas and Files of the given envelopeUrl.
     ***/
    HashMap<String, String> extractSchemasAndFilesFromEnvelopeUrl(String envelopeUrl) throws QaServiceException;
    
    /**
     *  Calls  the method  {@link eionet.gdem.qa.XQueryService#analyzeXMLFiles(java.util.Hashtable)  }
     *  which returns a vector of vectors. Each of these vectors contains a JobID and a FileURL.
     * @return a map containing each Job Id and corresponding File URL as Key value pair.
     */
    List<QaResultsWrapper> scheduleJobs(String envelopeUrl) throws QaServiceException;
    Vector runQaScript(String sourceUrl, String scriptId) throws QaServiceException;
}
