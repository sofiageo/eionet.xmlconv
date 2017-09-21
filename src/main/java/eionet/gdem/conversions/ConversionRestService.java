package eionet.gdem.conversions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 */
@Service
public class ConversionRestService {

    private RestTemplate restTemplate;
    public static final String qaRestServiceUrl = "http://localhost:8084/";

    @Autowired
    public ConversionRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> excel2xml(String excel) {
        return restTemplate.getForEntity(qaRestServiceUrl + "/excel2xml", String.class, excel);
    }

    public ResponseEntity<String> excel2xml(String excel, String sheet) {
        return restTemplate.getForEntity(qaRestServiceUrl + "/excel2xml", String.class, excel, sheet);
    }
}
