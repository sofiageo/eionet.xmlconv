package eionet.xmlconv.conversions.services;

import eionet.xmlconv.conversions.services.converters.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 *
 */
@Service
public class ConversionService {

    private Converter htmlConverter;
    private Converter pdfConverter;
    private Converter excelConverter;
    private Converter xmlConverter;
    private Converter odsConverter;
    private Converter textConverter;

    @Autowired
    public ConversionService(@Qualifier("htmlConverter") Converter htmlConverter, @Qualifier("pdfConverter") Converter pdfConverter,
                            @Qualifier("excelConverter") Converter excelConverter, @Qualifier("xmlConverter") Converter xmlConverter,
                            @Qualifier("odsConverter") Converter odsConverter, @Qualifier("textConverter") Converter textConverter) {
        this.htmlConverter = htmlConverter;
        this.pdfConverter = pdfConverter;
        this.excelConverter = excelConverter;
        this.xmlConverter = xmlConverter;
        this.odsConverter = odsConverter;
        this.textConverter = textConverter;
    }

    /**
     * Choose the correct converter for given content type and execute the conversion.
     * @param params Map of parameters passed to converter.
     * @param cnvTypeOut Output type of conversion.
     * @return File name of conversion result with correct extension.
     */
    public String executeConversion(Map<String, String> params, String cnvTypeOut) {
        InputStream source;
        InputStream xslStream = new BufferedInputStream(new FileInputStream(xslName));
        OutputStream resultStream;
        String cnvFileExt;

        Converter converter;
        String result = null;
        if (cnvTypeOut.startsWith("HTML")) {
            result = htmlConverter.convert(params);
        } else if (cnvTypeOut.equals("PDF")) {
            result = pdfConverter.convert(params);
        } else if (cnvTypeOut.equals("EXCEL")) {
            result = excelConverter.convert(params);
        } else if (cnvTypeOut.equals("XML")) {
            result = xmlConverter.convert(params);
        } else if (cnvTypeOut.equals("ODS")) {
            result = odsConverter.convert(params);
        } else {
            result = textConverter.convert(params);
        }
        return result;
    }
}
