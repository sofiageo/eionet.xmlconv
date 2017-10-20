package eionet.xmlconv.conversions.services;

import eionet.xmlconv.conversions.data.FileDto;
import eionet.xmlconv.conversions.http.HttpFileManager;
import eionet.xmlconv.conversions.services.converters.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 *
 *
 */
@Service
public class FileConversionService {

    private final Converter htmlConverter;
    private final Converter pdfConverter;
    private final Converter excelConverter;
    private final Converter xmlConverter;
    private final Converter odsConverter;
    private final Converter textConverter;

    @Autowired
    public FileConversionService(@Qualifier("htmlConverter") Converter htmlConverter, @Qualifier("pdfConverter") Converter pdfConverter,
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
     * @param xslFile XSL file.
     * @param cnvTypeOut Output type of conversion.
     * @return File name of conversion result with correct extension.
     */
    public String executeConversion(String sourceUrl, FileDto xslFile, String cnvTypeOut) {
        HttpFileManager fileManager = new HttpFileManager();
        InputStream source = fileManager.getFileInputStream(sourceUrl, "", false);
        InputStream xslStream = new ByteArrayInputStream(xslFile.getFile().getBytes(StandardCharsets.UTF_8));
        OutputStream resultStream = new ByteArrayOutputStream();

        Converter converter;
        String result = null;
        if (cnvTypeOut.startsWith("HTML")) {
            result = htmlConverter.convert(source, xslStream, resultStream, cnvTypeOut);
        } else if (cnvTypeOut.equals("PDF")) {
            result = pdfConverter.convert(source, xslStream, resultStream, cnvTypeOut);
        } else if (cnvTypeOut.equals("EXCEL")) {
            result = excelConverter.convert(source, xslStream, resultStream, cnvTypeOut);
        } else if (cnvTypeOut.equals("XML")) {
            result = xmlConverter.convert(source, xslStream, resultStream, cnvTypeOut);
        } else if (cnvTypeOut.equals("ODS")) {
            result = odsConverter.convert(source, xslStream, resultStream, cnvTypeOut);
        } else {
            result = textConverter.convert(source, xslStream, resultStream, cnvTypeOut);
        }
        return result;
    }
}
