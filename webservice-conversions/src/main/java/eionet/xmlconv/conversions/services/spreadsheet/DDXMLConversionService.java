package eionet.xmlconv.conversions.services.spreadsheet;

import eionet.xmlconv.conversions.data.ConversionLogDto;
import eionet.xmlconv.conversions.data.ConversionResultDto;
import eionet.xmlconv.conversions.data.ConvertedFileDto;
import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.http.CustomURI;
import eionet.xmlconv.conversions.http.HttpFileManager;
import eionet.xmlconv.conversions.utils.CustomFileUtils;
import eionet.xmlconv.conversions.utils.UrlUtils;
import eionet.xmlconv.conversions.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

@Service
public class DDXMLConversionService {

    private boolean checkSchemaValidity = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(DDXMLConversionService.class);
    public static final String DEFAULT_FILE_NAME = "converted";

    /**
     * Converts DataDictionary MS Excel file to XML.
     *
     * @param sourceUrl - URL of the srouce Excel file
     * @return Vector result: error_code, xml_url, error_message
     * @throws XMLConvException If an error occurs
     */


    public ConversionResultDto convertDD_XML(String sourceUrl) throws XMLConvException {
        return convertDD_XML(sourceUrl, false, null);
    }

    /**
     * Converts DataDictionary MS Excel sheets to different XML files, where one xml file is dataset table.
     *
     * @param sourceUrl - URL of the source Excel file
     * @param sheetName Sheet name
     * @return Vector result: error_code, xml_url, error_message
     * @throws XMLConvException If an error occurs
     */


    public ConversionResultDto convertDD_XML_split(String sourceUrl, String sheetName) throws XMLConvException {
        return convertDD_XML(sourceUrl, true, sheetName);
    }

    /*
     * Method that calls converter to do the conversion.
     *
     * @param sourceUrl Source URL
     * @param split Split or not
     * @param sheetName Sheet name
     * @return Result transfer object
     * @throws XMLConvException If an error occurs.
     */

    private ConversionResultDto convertDD_XML(String sourceUrl, boolean split, String sheetName) throws XMLConvException {
        OutputStream resultStream = null;
        String sourceFileName = null;
        File file = null;
        ConversionResultDto resultObject = new ConversionResultDto();
        String errorMessage = null;
        HttpFileManager fileManager = new HttpFileManager();
        InputStream sourceStream = null;
        try {
            URL url = new CustomURI(sourceUrl).getURL();
            // TODO fix authentication
            sourceStream = fileManager.getFileInputStream(sourceUrl, "ticket", true);

            file = new File(CustomFileUtils.saveFileInLocalStorage(sourceStream, "tmp"));
            sourceFileName =
                Utils.isNullStr(UrlUtils.getFileNameNoExtension(sourceUrl)) ? DEFAULT_FILE_NAME : UrlUtils.getFileNameNoExtension(sourceUrl);

            // Detect the file format
            DDXMLConverter converter = DDXMLConverter.getConverter(file, resultObject, sheetName);
            boolean doConversion =
                (converter.isValidSchema() && ((split && converter.isValidSheetSchemas()) || !split))
                || !isCheckSchemaValidity();
            if (doConversion) {
                // TODO FIX this
//                resultStream = getResultOutputStream(sourceFileName);
                resultStream = null;
                converter.setHttpResponse(true);
                if (split) {
                    resultObject = converter.convertDD_XML_split(resultStream, sheetName);
                } else {
                    String tmpFileName = Utils.getUniqueTmpFileName(".xml");
                    if (resultStream == null) {
                        resultStream = new FileOutputStream(tmpFileName);
                    }
                    resultObject = converter.convertDD_XML(resultStream);
                    if (false) {
                        // if (!isHttpRequest()) {
                        // resultObject.addConvertedXml(sourceFileName + ".xml",
                        // ((ByteArrayOutputStream) resultStream).toByteArray());
                        resultObject.addConvertedFile(sourceFileName + ".xml", tmpFileName);
                    }
                }
            }
        } catch (MalformedURLException mfe) {
            errorMessage = handleConversionException("Bad URL. ", mfe);
        } catch (IOException ioe) {
            errorMessage = handleConversionException("Error opening URL. ", ioe);
        } catch (Exception e) {
            errorMessage = handleConversionException("Error converting Excel file. ", e);
        } finally {
            IOUtils.closeQuietly(sourceStream);
            IOUtils.closeQuietly(resultStream);
            fileManager.closeQuietly();
            Utils.deleteFile(file);
        }
        // Creates response Object, if error occurred
        if (errorMessage != null) {
            if (resultObject == null) {
                resultObject = new ConversionResultDto();
            }
            resultObject.setStatusCode(ConversionResultDto.STATUS_ERR_SYSTEM);
            resultObject.setStatusDescription(errorMessage);
            resultObject.addConversionLog(ConversionLogDto.ConversionLogType.CRITICAL, errorMessage, "System");
        }
        resultObject.setSourceUrl(sourceUrl);
        if ((ConversionResultDto.STATUS_ERR_SYSTEM.equals(resultObject.getStatusCode()) || ConversionResultDto.STATUS_ERR_SCHEMA_NOT_FOUND
                        .equals(resultObject.getStatusCode()))) {
            throw new XMLConvException(resultObject.getStatusDescription());
        }

        return resultObject;
    }

    /*
     * Get OutpuStram where to write the conversion result.
     *
     * @param outputFileName Output file name
     * @return OutputStream
     * @throws XMLConvException If an error occurs
     */

/*
private OutputStream getResultOutputStream(String outputFileName) throws XMLConvException {
    OutputStream resultStream = null;
    if (isHttpRequest()) {
        try {
            HttpMethodResponseWrapper httpResponse = getHttpResponse();
            httpResponse.setContentType("text/xml");
            httpResponse.setContentDisposition(outputFileName + ".xml");
            resultStream = httpResponse.getOutputStream();
        } catch (IOException e) {
            LOGGER.error("Error getting response outputstream ", e);
            throw new XMLConvException("Error getting response outputstream " + e.toString(), e);
        }
    }
    return resultStream;
}*/

    /*
     * Handle exceptions - throws Exception if the call is coming from web page, otherwise logs and returns error message.
     *
     * @param errorMessage Error message
     * @param e Exception
     * @return Error message
     * @throws XMLConvException If an error occurs
     */


    private String handleConversionException(String errorMessage, Exception e) throws XMLConvException {
        LOGGER.error(errorMessage, e);
        if (true) { //if (isHttpRequest())
            throw new XMLConvException(errorMessage + e.getMessage(), e);
        } else {
            errorMessage = errorMessage + e.getMessage();
        }
        return errorMessage;
    }

    /*
     * Converts conversion result object into Hashtable that is used in XML-RPC method result.
     *
     * @param dto Result transfer object
     * @return Hash table with result
     * @throws XMLConvException If an error occurs
     */

    public static final Hashtable<String, Object> convertExcelResult(ConversionResultDto dto) throws XMLConvException {
        Hashtable<String, Object> result = new Hashtable<String, Object>();

        result.put("resultCode", dto.getStatusCode());
        result.put("resultDescription", dto.getStatusDescription());
        result.put("conversionLog", dto.getConversionLogAsHtml());
        Vector<Hashtable<String, Object>> convertedFiles = new Vector<Hashtable<String, Object>>();

        if (dto.getConvertedFiles() != null) {
            for (ConvertedFileDto convertedFileDto : dto.getConvertedFiles()) {
                Hashtable<String, Object> convertedFile = new Hashtable<String, Object>();
                convertedFile.put("fileName", convertedFileDto.getFileName());
                convertedFile.put("content", convertedFileDto.getFileContentAsByteArray());
                convertedFiles.add(convertedFile);
            }
        }
        result.put("convertedFiles", convertedFiles);
        return result;
    }

    /*
     * @return the checkSchemaValidity
     */


    public boolean isCheckSchemaValidity() {
        return checkSchemaValidity;
    }

    /*
     * @param checkSchemaValidity the checkSchemaValidity to set
     */
    public void setCheckSchemaValidity(boolean checkSchemaValidity) {
        this.checkSchemaValidity = checkSchemaValidity;
    }

}
