package eionet.xmlconv.conversions.services;

import eionet.xmlconv.conversions.services.excel.ExcelProcessor;
import eionet.xmlconv.conversions.services.saxon.XsltService;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
// TODO: Check if necessary
public class Converter {

    private XsltService xsltService;

    public Converter(XsltService xsltService) {
        this.xsltService = xsltService;
    }

    public String excelConversion(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) {
        String xmlFile = Utils.getUniqueTmpFileName(".xml");
        String excelFile = Utils.getUniqueTmpFileName(".xls");
        OutputStream xmlOut = null;
        try {
            xmlOut = new FileOutputStream(xmlFile);
            xsltService.transform(source, xslt, xmlOut);
            ExcelProcessor ep = new ExcelProcessor();
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
}
