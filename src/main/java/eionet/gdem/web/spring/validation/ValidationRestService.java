package eionet.gdem.web.spring.validation;

import eionet.gdem.dto.ValidateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 *
 *
 */
@Service
public class ValidationRestService {

    private RestTemplate restTemplate;
    private static final String validationServerUrl = "http://localhost:8083/validate";

    @Autowired
    public ValidationRestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<ValidateDto[]> validate(String xmlFileUrl, String xmlSchemaUrl) {
        return restTemplate.getForEntity(validationServerUrl, ValidateDto[].class, "");
    }


}
