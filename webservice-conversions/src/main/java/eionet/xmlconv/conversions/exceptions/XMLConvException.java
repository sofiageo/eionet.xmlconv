package eionet.xmlconv.conversions.exceptions;

/**
 *
 * Temporary Exception to help with migration.
 */
public class XMLConvException extends RuntimeException {

    public XMLConvException() {
    }

    public XMLConvException(String s) {
        super(s);
    }

    public XMLConvException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public XMLConvException(Throwable throwable) {
        super(throwable);
    }

    public XMLConvException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
