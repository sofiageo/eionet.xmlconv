package eionet.xmlconv.conversions.services.converters;

import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.services.saxon.TransformationService;
import eionet.xmlconv.conversions.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Converts XML to HTML.
 *
 */
@Component(value = "htmlConverter")
public class HTMLConverter implements Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTMLConverter.class);
    private TransformationService transformationService;

    @Autowired
    public HTMLConverter(TransformationService conversionService) {
        this.transformationService = conversionService;
    }

    @Override
    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws XMLConvException {
        String htmlFile = Utils.getUniqueTmpFileName(".html");
        if (result != null) {
            transformationService.transform(source, xslt, result);
        } else {
            try {
                result = new FileOutputStream(htmlFile);
                transformationService.transform(source, xslt, result);
            } catch (IOException e) {
                throw new XMLConvException("Error creating HTML output file " + e.toString(), e);
            }
            finally{
                IOUtils.closeQuietly(result);
            }
        }
        return htmlFile;
    }

    @Override
    public String convert(Map params) {
        return null;
    }
}
