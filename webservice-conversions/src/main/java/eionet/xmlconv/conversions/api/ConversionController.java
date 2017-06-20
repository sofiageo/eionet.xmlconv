package eionet.xmlconv.conversions.api;

import eionet.xmlconv.conversions.services.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String convert(Model model) {
        Map map = new HashMap<String, String>();
        String output = "HTML";
        String result = conversionService.executeConversion(map, output);
        return result;
    }
}
