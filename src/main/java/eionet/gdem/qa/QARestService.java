package eionet.gdem.qa;

import eionet.gdem.dto.ValidateDto;
import eionet.gdem.qa.model.QAApiDto;
import eionet.gdem.qa.model.QAResponse;
import eionet.gdem.qa.model.Request;
import eionet.gdem.qa.model.Response;
import eionet.gdem.qa.model.XQScript;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;
    public static final String qaRestServiceUrl = "http://localhost:8083/";

    @Autowired
    public QARestService(RestTemplate restTemplate, ModelMapper modelMapper) {
        this.restTemplate = restTemplate;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<String> executeSaxon(XQScript script) {
        return restTemplate.getForEntity(qaRestServiceUrl + "/saxon", String.class, script);
    }

    public Response executeBaseX(XQScript script) {
        Request request = modelMapper.map(script, Request.class);
        ResponseEntity<QAResponse> response = restTemplate.postForEntity(qaRestServiceUrl + "/basex", request, QAResponse.class);
        QAResponse qaResponse = response.getBody();
        Response apiResponse = new Response();
        apiResponse.setResult(qaResponse.getQaResult());
        return apiResponse;
    }

    public ResponseEntity<ValidateDto[]> executeValidation(String script) {
        return restTemplate.postForEntity(qaRestServiceUrl + "/validation", script, ValidateDto[].class);
    }

    public ResponseEntity<ValidateDto[]> executeValidation(String script, String schema) {
        return restTemplate.postForEntity(qaRestServiceUrl + "/validation", script, ValidateDto[].class);
    }
}
