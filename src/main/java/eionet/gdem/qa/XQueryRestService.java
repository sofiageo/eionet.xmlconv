package eionet.gdem.qa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 *
 */
@Service
public class XQueryRestService {

    private RestTemplate restTemplate;
    public static final String XQueryServerUrl = "http://localhost:8083/xquery";

    @Autowired
    public XQueryRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> execute(String script) {
        return restTemplate.getForEntity(XQueryServerUrl, String.class, script);
    }

}
