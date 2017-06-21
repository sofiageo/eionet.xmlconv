package eionet.xmlconv.conversions.services.converters;

import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.services.saxon.TransformationService;
import eionet.xmlconv.conversions.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Converts XML to XML.
 * @author Unknown
 * @author George Sofianos
 */
@Component(value = "xmlConverter")
public class XMLConverter implements Converter {

    private TransformationService transformationService;

    @Autowired
    public XMLConverter(TransformationService conversionService) {
        this.transformationService = conversionService;
    }

    @Override
    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws XMLConvException {
        String xmlFile =  Utils.getUniqueTmpFileName("." + cnvFileExt);
        if (result != null) {
            transformationService.transform(source, xslt, result);
        } else {
            try {
                result = new FileOutputStream(xmlFile);
                transformationService.transform(source, xslt, result);
            } catch (IOException e) {
                throw new XMLConvException("Error creating XML output file " + e.toString(), e);
            }
            finally{
                IOUtils.closeQuietly(result);
            }
        }
        return xmlFile;
    }

    @Override
    public String convert(Map params) {
        return null;
    }
}
