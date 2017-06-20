package eionet.xmlconv.conversions.services.converters;

import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.services.odf.OpenDocumentProcessor;
import eionet.xmlconv.conversions.services.saxon.TransformationService;
import eionet.xmlconv.conversions.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class OdsConverter implements Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OdsConverter.class);
    private TransformationService transformationService;
    private OpenDocumentProcessor openDocumentProcessor;

    @Autowired
    public OdsConverter(TransformationService transformationService, OpenDocumentProcessor openDocumentProcessor) {
        this.transformationService = transformationService;
        this.openDocumentProcessor = openDocumentProcessor;
    }

    @Override
    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws XMLConvException {
        FileOutputStream xmlOut = null;
        String xmlFile = Utils.getUniqueTmpFileName(".xml");
        String odsFile = Utils.getUniqueTmpFileName(".ods");

        try {
            xmlOut = new FileOutputStream(xmlFile);
            transformationService.transform(source, xslt, xmlOut);
            if (result != null) {
                openDocumentProcessor.makeSpreadsheet(xmlFile, result);
            } else {
                openDocumentProcessor.makeSpreadsheet(xmlFile, odsFile);
            }

        } catch (FileNotFoundException e) {
            LOGGER.error("Error " + e.toString(), e);
            throw new XMLConvException("Error transforming OpenDocument Spreadhseet " + e.toString(), e);
        } finally {
            IOUtils.closeQuietly(xmlOut);
        }
        try {
            Utils.deleteFile(xmlFile);
        } catch (Exception e) {
            LOGGER.error("Couldn't delete the result file: " + xmlFile, e);
        }

        return odsFile;
    }

    @Override
    public String convert(Map params) {
        return null;
    }
}
