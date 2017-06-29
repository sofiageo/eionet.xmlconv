package eionet.xmlconv.qa.exceptions;

/**
 *
 * Temporary Exception to help with migration.
 */
public class DCMException extends RuntimeException {

    public static final String EXCEPTION_CONVERT_URL_MALFORMED = "label.conversion.url.malformed";
    public static final String EXCEPTION_CONVERT_URL_ERROR = "label.conversion.url.error";
    public static final String EXCEPTION_GENERAL = "label.exception.unknown";
    public static final String EXCEPTION_XMLPARSING_ERROR = "label.conversion.xmlparsing.error";

    private String errorCode;

    public DCMException() {
    }

    public DCMException(String s) {
        super(s);
    }
    public DCMException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public DCMException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DCMException(Throwable throwable) {
        super(throwable);
    }

    public DCMException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
