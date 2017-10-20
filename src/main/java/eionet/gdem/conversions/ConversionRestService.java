package eionet.gdem.conversions;

import eionet.gdem.conversions.model.ConversionRequest;
import eionet.gdem.conversions.model.ConversionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 *
 */
@Service
public class ConversionRestService {

    private RestTemplate restTemplate;
    public static final String conversionRestServiceUrl = "http://xmlconv-conversions";

    @Autowired
    public ConversionRestService(@LoadBalanced RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> excel2xml(String excel) {
        return restTemplate.getForEntity(conversionRestServiceUrl + "/excel2xml", String.class, excel);
    }

    public ResponseEntity<String> excel2xml(String excel, String sheet) {
        return restTemplate.getForEntity(conversionRestServiceUrl + "/excel2xml", String.class, excel, sheet);
    }

    @Async
    public CompletableFuture<ConversionResult> convert(String url, String type) {
        ConversionRequest request = new ConversionRequest();
        request.setSourceUrl(url);
        request.setType(type);
        ConversionResult result = restTemplate.postForObject(conversionRestServiceUrl + "/convert", request, ConversionResult.class);
        return CompletableFuture.completedFuture(result);
    }
}
