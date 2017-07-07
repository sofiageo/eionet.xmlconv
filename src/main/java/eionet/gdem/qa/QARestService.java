package eionet.gdem.qa;

import eionet.gdem.dto.ValidateDto;
import eionet.gdem.qa.model.XQScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 *
 */
@Service
public class QARestService {

    private RestTemplate restTemplate;
    public static final String qaRestServiceUrl = "http://localhost:8083/";

    @Autowired
    public QARestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> executeSaxon(XQScript script) {
        return restTemplate.getForEntity(qaRestServiceUrl + "/saxon", String.class, script);
    }

    public ResponseEntity<String> executeBaseX(XQScript script) {
        return restTemplate.getForEntity(qaRestServiceUrl + "/basex", String.class, script);
    }

    public ResponseEntity<ValidateDto[]> executeValidation(String script) {
        return restTemplate.getForEntity(qaRestServiceUrl + "/validation", ValidateDto[].class, script);
    }

    public ResponseEntity<ValidateDto[]> executeValidation(String script, String schema) {
        return restTemplate.getForEntity(qaRestServiceUrl + "/validation", ValidateDto[].class, script);
    }
}
