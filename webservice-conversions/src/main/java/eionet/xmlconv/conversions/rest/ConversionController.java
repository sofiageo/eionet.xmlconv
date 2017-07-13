package eionet.xmlconv.conversions.rest;

import eionet.xmlconv.conversions.data.FileDto;
import eionet.xmlconv.conversions.services.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
@RestController
public class ConversionController {

    private ConversionService conversionService;

    @Autowired
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping("/convert")
    public String convert(@RequestBody FileDto xslFile, Model model) {
        Map map = new HashMap<String, String>();
        String output = "HTML";
        String result = conversionService.executeConversion(map, xslFile, output);
        return result;
    }

    @GetMapping("/convert")
    public String convertGet() {
        Map map = new HashMap<String, String>();
        String result = "This is a test case";
        return result;
    }
}
