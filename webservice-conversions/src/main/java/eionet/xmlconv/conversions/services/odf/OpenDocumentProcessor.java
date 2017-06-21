package eionet.xmlconv.conversions.services.odf;

import eionet.xmlconv.conversions.exceptions.XMLConvException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Processes OpenDocument files.
 * @author Unknown
 * @author George Sofianos
 */
@Component
public class OpenDocumentProcessor {

    /**
     * This class is creating handlers for creating OpenDocument file from xml called from ConversionService
     */
    public OpenDocumentProcessor() {
    }

    /**
     * Creates ODS Spreadsheet
     * @param sIn Input String
     * @param sOut Output String
     * @throws XMLConvException If an error occurs.
     */
    public void makeSpreadsheet(String sIn, String sOut) throws XMLConvException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(sOut);
            makeSpreadsheet(sIn, out);
        } catch (FileNotFoundException e) {
            throw new XMLConvException("ErrorConversionHandler - couldn't save the OpenDocumentSpreadheet file: " + e.toString(), e);
        } finally{
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Creates ODS Spreadsheet
     * @param sIn Input String
     * @param sOut Output String
     * @throws XMLConvException If an error occurs.
     */
    public void makeSpreadsheet(String sIn, OutputStream sOut) throws XMLConvException {

        if (sIn == null) {
            return;
        }
        if (sOut == null) {
            return;
        }

        try {
            OpenDocument od = new OpenDocument();
            od.setContentFile(sIn);
            od.createOdsFile(sOut);
        } catch (Exception e) {
            throw new XMLConvException("Error generating OpenDocument Spreadsheet file: " + e.toString(), e);
        }

        return;
    }
}
