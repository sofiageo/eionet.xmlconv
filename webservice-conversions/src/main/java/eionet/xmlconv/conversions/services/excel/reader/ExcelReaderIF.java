package eionet.xmlconv.conversions.services.excel.reader;

import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.services.datadict.DD_XMLInstance;

import java.io.InputStream;
import java.util.Map;

/**
 * The main class, which is calling POI HSSF methods for reading Excel file.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public interface ExcelReaderIF {

    /**
     * If the excel file is generated from Data Dictionary, then it finds the XML Shema from Excel file
     *
     * @return - XML Schema URL
     */
    String getXMLSchema();

    /**
     * Initialize the Excel Workbook from InputStream
     *
     * @param input - input Excel file
     * @throws XMLConvException In case an error occurs.
     */
    void initReader(InputStream input) throws XMLConvException;

    /**
     * Goes through the Excel worksheets and writes the data into DD_XMLInstance as xml
     *
     * @param instance - XML instance file, where the structure xml has been defined before
     * @throws XMLConvException In case an error occurs.
     */
    void readDocumentToInstance(DD_XMLInstance instance) throws XMLConvException;

    /**
     * Finds the first sheet name, that is not DO_NOT_DELETE_THIS_SHEET
     *
     * @return - Excel sheet name
     */
    String getFirstSheetName();

    /**
     * If the excel file is generated from Data Dictionary, then it finds the XML Schemas for each Excel sheet
     *
     * @return - Excel sheet name
     */
    Map<String, String> getSheetSchemas();

    /**
     * Check if sheet has data or not
     *
     * @param sheet_name
     *            - Excel sheet name
     * @return boolean - true if has data
     */
     boolean isEmptySheet(String sheet_name);

}
