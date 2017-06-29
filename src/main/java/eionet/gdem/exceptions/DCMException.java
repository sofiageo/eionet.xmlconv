package eionet.gdem.exceptions;

/**
 * Generic Exception.
 * @author Unknown
 */
public class DCMException extends RuntimeException {

    private String errorCode;

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Constructor
     * @param errorCode Error code
     * @param message Exception message
     */
    public DCMException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor
     * @param errorCode Error code
     */
    public DCMException(String errorCode) {
        this.errorCode = errorCode;
    }

}
