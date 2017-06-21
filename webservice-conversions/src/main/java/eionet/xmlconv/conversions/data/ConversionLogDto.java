package eionet.xmlconv.conversions.data;

/**
 * Object that stores conversion log messages.
 *
 * @author Enriko Käsper
 */
public class ConversionLogDto {

    public enum ConversionLogType {
        CRITICAL, ERROR, WARNING, INFO
    };

    /**
     * Log category refers to the Excel file.
     */
    public static final String CATEGORY_WORKBOOK = "Workbook";
    /**
     * Log category refers to the Excel sheet.
     */
    public static final String CATEGORY_SHEET = "Sheet";

    /**
     * Conversion log message.
     */
    private String message;

    /**
     * Conversion log type - error, warning, info.
     */
    private ConversionLogType type;

    /**
     * Conversion log message category.
     */
    private String category;

    /**
     * Constructor creating conversion log object with properties.
     * @param type type
     * @param message message
     * @param category category
     */
    public ConversionLogDto(ConversionLogType type, String message, String category) {
        this.type = type;
        this.message = message;
        this.category = category;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the type
     */
    public ConversionLogType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(ConversionLogType type) {
        this.type = type;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ConversionLogDto [message=" + message + ", type=" + type + ", category=" + category + "]";
    }

}
