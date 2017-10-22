package eionet.xmlconv.conversions.rest;

import eionet.xmlconv.conversions.data.ConversionResultDto;
import eionet.xmlconv.conversions.data.FileDto;
import eionet.xmlconv.conversions.model.ConversionRequest;
import eionet.xmlconv.conversions.model.ConversionResponse;
import eionet.xmlconv.conversions.services.FileConversionService;
import eionet.xmlconv.conversions.services.spreadsheet.DDXMLConversionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
@RestController
public class ConversionController {

    private FileConversionService conversionService;
    private DDXMLConversionService ddxmlConversionService;
    private ModelMapper modelMapper;

    @Autowired
    public ConversionController(FileConversionService conversionService, DDXMLConversionService ddxmlConversionService, ModelMapper modelMapper) {
        this.conversionService = conversionService;
        this.ddxmlConversionService = ddxmlConversionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convert(@RequestBody ConversionRequest req) throws IOException {
        ConversionResponse response = new ConversionResponse();
        String sourceUrl = req.getSourceUrl();
        String type = req.getType();
        String xslFileName = req.getXslFileName();
        String result = conversionService.executeConversion(sourceUrl, xslFileName, type);
        response.setResult(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @GetMapping("/convert")
//    public String convertGet() {
//        Map map = new HashMap<String, String>();
//        String result = "This is a test case";
//        return result;
//    }

    @PostMapping("/excel2xml")
    public ResponseEntity<ConversionResponse> excel2xml(@RequestBody ConversionRequest request) {
        Map map = new HashMap<String, String>();
        String sourceUrl = request.getSourceUrl();
        ConversionResultDto result = ddxmlConversionService.convertDD_XML(sourceUrl);
        ConversionResponse response = new ConversionResponse();
        response.setResult(result.getConversionLogAsHtml());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
