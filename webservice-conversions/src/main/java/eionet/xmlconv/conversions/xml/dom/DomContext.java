package eionet.xmlconv.conversions.xml.dom;

import eionet.xmlconv.conversions.xml.IXmlCtx;
import eionet.xmlconv.conversions.xml.XmlUpdater;
import org.w3c.dom.Document;

/**
 * XML Context Class.
 * @author Unknown
 * @author George Sofianos
 */
public class DomContext extends XmlCommon implements IXmlCtx {
    /**
     * Default constructor
     */
    public DomContext() {
    }

    public XmlUpdater getManager() {
        return new XmlManager(this);
    }

    public DomXpath getQueryManager() {
        return new DomXpath(this);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

}
