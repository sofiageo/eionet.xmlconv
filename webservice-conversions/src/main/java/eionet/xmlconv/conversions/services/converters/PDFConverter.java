package eionet.xmlconv.conversions.services.converters;
import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.services.pdf.PDFConverterService;
import eionet.xmlconv.conversions.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Converts XML to PDF.
 * @author Unknown
 * @author George Sofianos
 */
public class PDFConverter implements Converter {

    private PDFConverterService pdfConverterService;

    @Autowired
    public PDFConverter(PDFConverterService pdfConverterService) {
        this.pdfConverterService = pdfConverterService;
    }

    @Override
    public String convert(InputStream source, InputStream xslt, OutputStream result, String cnvFileExt) throws XMLConvException {
        String pdfFile = Utils.getUniqueTmpFileName(".pdf");
        if (result != null) {
            pdfConverterService.pdfConvert(source, xslt, result);
        } else {
            try {
                result = new FileOutputStream(pdfFile);
                pdfConverterService.pdfConvert(source, xslt, result);
            } catch (IOException e) {
                throw new XMLConvException("Error creating PDF output file " + e.toString(), e);
            }
            finally{
                IOUtils.closeQuietly(result);
            }
        }
        return pdfFile;
    }

    @Override
    public String convert(Map params) {
        return null;
    }
}
