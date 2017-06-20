package eionet.xmlconv.conversions.services.converters;

import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.services.excel.ExcelProcessor;
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

/**
 * Converts XML files to Excel.
 *
 */
public class ExcelConverter implements Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelConverter.class);
    private TransformationService transformationService;
    private ExcelProcessor ep;
    /*private ExcelProcessor ep = new ExcelProcessor();*/

    @Autowired
    public ExcelConverter(TransformationService conversionService, ExcelProcessor ep) {
        this.transformationService = conversionService;
        this.ep = ep;
    }

    @Override
    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws XMLConvException {
        String xmlFile = Utils.getUniqueTmpFileName(".xml");
        String excelFile = Utils.getUniqueTmpFileName(".xls");
        OutputStream xmlOut = null;
        try {
            xmlOut = new FileOutputStream(xmlFile);
            transformationService.transform(source, xslt, xmlOut);

            if (result != null) {
                ep.makeExcel(xmlFile, result);
            } else {
                ep.makeExcel(xmlFile, excelFile);
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Error " + e.toString(), e);
            throw new XMLConvException("Error transforming Excel " + e.toString(), e);
        }
        finally{
            IOUtils.closeQuietly(xmlOut);
        }
        try {
            Utils.deleteFile(xmlFile);
        } catch (Exception e) {
            LOGGER.error("Couldn't delete the result file: " + xmlFile, e);
        }
        return excelFile;
    }

    @Override
    public String convert(Map params) {
        return null;
    }
}
