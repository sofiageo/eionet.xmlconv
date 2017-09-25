package eionet.xmlconv.conversions.services.spreadsheet;

/*
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConversionLogDto;
import eionet.gdem.dto.ConversionLogDto.ConversionLogType;
import eionet.gdem.dto.ConversionResultDto;*/
import eionet.xmlconv.conversions.Properties;
import eionet.xmlconv.conversions.SpringApplicationContext;
import eionet.xmlconv.conversions.data.ConversionLogDto;
import eionet.xmlconv.conversions.data.ConversionLogDto.*;
import eionet.xmlconv.conversions.data.ConversionResultDto;
import eionet.xmlconv.conversions.services.MessageService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Logger writes log messages into ConversionResultDto.
 *
 * @author Enriko Käsper
 */
public class SourceReaderLogger {

    private MessageService messageService;

    public static final String CONVERSION_LOG_START_SPREADSHEET = "conversion.log.start.spreadsheet";
    public static final String CONVERSION_LOG_NO_DEFINITIONS = "conversion.log.no.definitions";
    public static final String CONVERSION_LOG_NOF_SHEETS = "conversion.log.nof.sheets";
    public static final String CONVERSION_LOG_START_SHEET = "conversion.log.start.sheet";
    public static final String CONVERSION_LOG_NO_SHEET = "conversion.log.no.sheet";
    public static final String CONVERSION_LOG_EMPTY_SHEET = "conversion.log.empty.sheet";
    public static final String CONVERSION_LOG_NOF_COLS = "conversion.log.nof.cols";
    public static final String CONVERSION_LOG_REDUNDANT_COLS = "conversion.log.redundant.cols";
    public static final String CONVERSION_LOG_MISSING_COLS = "conversion.log.missing.cols";
    public static final String CONVERSION_LOG_END_SHEET = "conversion.log.end.sheet";
    public static final String CONVERSION_LOG_NOF_RECORDS = "conversion.log.nof.records";
    public static final String CONVERSION_LOG_END_SPREADSHEET = "conversion.log.end.spreadsheet";
    public static final String CONVERSION_LOG_SHEET_SCHEMA = "conversion.log.sheet.schema";
    public static final String CONVERSION_LOG_WARNING = "conversion.log.warning";

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceReaderLogger.class);

    private long startTimestamp = 0;

    /**
     * Enum storing reader type messages.
     */
    public enum ReaderTypeEnum {
        EXCEL("MS Excel"), ODS("OpenOffice Spreadsheet");
        private String message;

        /**
         * Constructor
         * @param message Reader type
         */
        ReaderTypeEnum(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Conversion result object where to write log messages.
     */
    private ConversionResultDto conversionResult;
    /**
     * Reader type indicating whether it is Excel or OpenDocument conversion
     */
    private ReaderTypeEnum readerType;

    /**
     * Sets conversion result and reader type
     * @param conversionResult Conversion result
     * @param readerType Reader type
     */
    public SourceReaderLogger(ConversionResultDto conversionResult, ReaderTypeEnum readerType) {
        this.conversionResult = conversionResult;
        this.readerType = readerType;
        //todo fix DI
        this.messageService = (MessageService) SpringApplicationContext.getBean("messageService");
    }

    /**
     * Start reading spreadsheet.
     */
    public void logStartWorkbook() {
        startTimestamp = System.currentTimeMillis();
        conversionResult.addConversionLog(ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_START_SPREADSHEET), ConversionLogDto.CATEGORY_WORKBOOK);
    }

    /**
     * Could not find any table definitions from Data Dictionary for given MS Excel file
     */
    public void logNoDefinitionsForTables() {
        conversionResult.addConversionLog(ConversionLogType.ERROR,
                messageService.getMessage(CONVERSION_LOG_NO_DEFINITIONS, new String[] {readerType.getMessage()}),
                ConversionLogDto.CATEGORY_WORKBOOK);
    }

    /**
     * Found " + numberOfSheets + " sheets from the workbook
     *
     * @param numberOfSheets Number of workbook sheets
     * @param sheetNames Sheet names
     */
    public void logNumberOfSheets(int numberOfSheets, String sheetNames) {
        String plural = (numberOfSheets != 1) ? "s" : "";
        conversionResult.addConversionLog(
                ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_NOF_SHEETS, new String[] {Integer.toString(numberOfSheets),
                        sheetNames, plural}), ConversionLogDto.CATEGORY_WORKBOOK);
    }

    /**
     * Start reading sheet:
     *
     * @param sheetName Sheet name
     */
    public void logStartSheet(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_START_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Unable to find sheet
     *
     * @param sheetName Sheet name
     */
    public void logSheetNotFound(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                messageService.getMessage(CONVERSION_LOG_NO_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Sheet is missing or empty. Skip converting this sheet:
     *
     * @param sheetName Sheet name
     */
    public void logEmptySheet(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_EMPTY_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * "Found nofColumns columns on sheet: sheetName
     *
     * @param nofColumns Number of columns
     * @param sheetName Sheet name
     */
    public void logNumberOfColumns(int nofColumns, String sheetName) {
        String plural = (nofColumns != 1) ? "s" : "";
        conversionResult.addConversionLog(
                ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_NOF_COLS, new String[] {Integer.toString(nofColumns),
                        sheetName, plural}), ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Found redundant columns: extraColumns on sheet sheetName
     *
     * @param extraColumns Extra columns
     * @param sheetName Sheet name
     */
    public void logExtraColumns(String extraColumns, String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                messageService.getMessage(CONVERSION_LOG_REDUNDANT_COLS, new String[] {extraColumns, sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * The following columns are missing: missingColumns on sheet sheetName
     *
     * @param missingColumns Missing columns
     * @param sheetName Sheet name
     */
    public void logMissingColumns(String missingColumns, String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                messageService.getMessage(CONVERSION_LOG_MISSING_COLS, new String[] {missingColumns, sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * End reading sheet:
     *
     * @param sheetName Sheet name
     */
    public void logEndSheet(String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_END_SHEET, new String[] {sheetName}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Found numberOfRows records on sheet: sheetName
     *
     * @param numberOfRows Number of rows
     * @param sheetName Sheet name
     */
    public void logNumberOfRows(int numberOfRows, String sheetName) {
        String plural = (numberOfRows != 1) ? "s" : "";
        conversionResult.addConversionLog(
                ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_NOF_RECORDS, new String[] {Integer.toString(numberOfRows),
                        sheetName, plural}), ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * End reading spreadsheet.
     * @param fileSize File size
     */
    public void logEndWorkbook(long fileSize) {
        BigDecimal totalTime = new BigDecimal(System.currentTimeMillis() - startTimestamp).divide(new BigDecimal("1000.0"));
        String fileSizeMessage = "";
        if (fileSize > 0) {
            fileSizeMessage = FileUtils.byteCountToDisplaySize(fileSize);
        }
        String message =
                messageService.getMessage(CONVERSION_LOG_END_SPREADSHEET, new String[] {fileSizeMessage, totalTime.toPlainString()});
        conversionResult.addConversionLog(ConversionLogType.INFO, message, ConversionLogDto.CATEGORY_WORKBOOK);
        LOGGER.info(message);
    }

    /**
     * Sheet schema is: schemaUrl
     *
     * @param instanceUrl Instance URL
     * @param sheetName Table local name
     */
    public void logSheetSchema(String instanceUrl, String sheetName) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                messageService.getMessage(CONVERSION_LOG_SHEET_SCHEMA, new String[] {instanceUrl}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }
    /**
     * Log system warning:
     *
     * @param sheetName Sheet name
     * @param warnMessage Warning message
     */
    public void logSystemWarning(String sheetName, String warnMessage) {
        conversionResult.addConversionLog(ConversionLogType.WARNING,
                messageService.getMessage(CONVERSION_LOG_WARNING, new String[] {sheetName, warnMessage}),
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }

    /**
     * Log system info:
     *
     * @param sheetName Sheet name
     * @param infoMessage Information message
     */
    public void logInfo(String sheetName, String infoMessage) {
        conversionResult.addConversionLog(ConversionLogType.INFO,
                infoMessage,
                ConversionLogDto.CATEGORY_SHEET + ": " + sheetName);
    }
}
