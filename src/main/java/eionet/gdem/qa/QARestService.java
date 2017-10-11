package eionet.gdem.qa;

import eionet.gdem.qa.model.QAResponse;
import eionet.gdem.qa.model.Request;
import eionet.gdem.qa.model.Response;
import eionet.gdem.qa.model.ValidationRequest;
import eionet.gdem.qa.model.ValidationResult;
import eionet.gdem.qa.model.XQScript;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 *
 *
 */
@Service
public class QARestService {

    private RestTemplate restTemplate;
    private ModelMapper modelMapper;
    public static final String qaRestServiceUrl = "http://xmlconv-qa";

    @Autowired
    public QARestService(@LoadBalanced RestTemplate restTemplate, ModelMapper modelMapper) {
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

    @Async
    public CompletableFuture<ValidationResult> executeValidation(String sourceUrl) {
        ValidationRequest req = new ValidationRequest(0l, sourceUrl, null);
        ValidationResult result = restTemplate.postForObject(qaRestServiceUrl + "/validation", req, ValidationResult.class);
        return CompletableFuture.completedFuture(result);
    }

    @Async
    public CompletableFuture<ValidationResult> executeValidation(String sourceUrl, String schema) {
        ValidationRequest req = new ValidationRequest(0l, sourceUrl, null);
        ValidationResult result = restTemplate.postForObject(qaRestServiceUrl + "/validation", req, ValidationResult.class);
        return CompletableFuture.completedFuture(result);
    }
}
