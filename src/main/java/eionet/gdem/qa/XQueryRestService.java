package eionet.gdem.qa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * TODO maybe rename to QARestService
 */
@Service
public class XQueryRestService {

    private RestTemplate restTemplate;
    public static final String XQueryServerUrl = "http://localhost:8083/";

    @Autowired
    public XQueryRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> executeSaxon(String script) {
        return restTemplate.getForEntity(XQueryServerUrl + "/saxon", String.class, script);
    }

    public ResponseEntity<String> executeBaseX(String script) {
        return restTemplate.getForEntity(XQueryServerUrl + "/basex", String.class, script);
    }

    public ResponseEntity<String> executeValidation(String script) {
        return restTemplate.getForEntity(XQueryServerUrl + "/validation", String.class, script);
    }
}
