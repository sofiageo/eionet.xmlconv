package eionet.gdem.xml.services;

/**
 * XML Exception class.
 * @author Unknown
 * @author George Sofianos
 */
public class XmlException extends Exception {
    public XmlException() {
    }

    public XmlException(String s) {
        super(s);
    }

    public XmlException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public XmlException(Throwable throwable) {
        super(throwable);
    }

    public XmlException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
