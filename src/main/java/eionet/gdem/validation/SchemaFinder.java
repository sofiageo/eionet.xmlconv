package eionet.gdem.validation;

import eionet.gdem.logging.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eionet.gdem.utils.Utils;

/**
 * Handler for parsing xml document extening SAX DefaultHandler. This class is calling different ExcelConversionhandler methods,
 * which is actuially creating Excel file
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */

public class SchemaFinder extends DefaultHandler {

    private static final String SCHEMA_REFERENCE = "schemaLocation";
    private static final String NO_NS_SCHEMA_REFERENCE = "noNamespaceSchemaLocation";
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaFinder.class);

    private String startTag = null;
    private String startTagNamespace = null;
    private String schemaLocation = null;
    private String schemaNamespace = null;
    private boolean hasNamespace = false;

    /**
     * Starts element
     * @param uri URI
     * @param localName Local name
     * @param name Name
     * @param attrs Attributes
     * @throws SAXException If an error occurs.
     */
    public void startElement(String uri, String localName, String name, Attributes attrs) throws SAXException {
        // System.out.println("element:" + uri + "||" + localName + "||" + name);

        startTag = (localName == null) ? name : localName; // we want the tag name without ns prefix, if ns processing is turned
                                                           // off, them we use name
        startTagNamespace = uri;

        // String schema_location_attr = (Utils.isNullStr(startTagNamespace))? NO_NS_SCHEMA_REFERENCE:SCHEMA_REFERENCE;

        // System.out.println("("+name);
        int length = attrs != null ? attrs.getLength() : 0;
        for (int i = 0; i < length; i++) {
            String attrName = attrs.getLocalName(i);
            if (attrName.equalsIgnoreCase(NO_NS_SCHEMA_REFERENCE)) {
                setSchemaLocation(attrs.getValue(i));
            } else if (attrName.equalsIgnoreCase(SCHEMA_REFERENCE)) {
                String sch_val = attrs.getValue(i);

                if (!Utils.isNullStr(sch_val)) {
                    // int l = sch_val.indexOf(" ");
                    // schemaLocation=sch_val.substring(l+1);
                    String schemaWithNS = attrs.getValue(i);
                    String[] splitted = schemaWithNS.split("\\s+");
                    if (splitted.length > 1) {
                        setSchemaNamespace(splitted[0]);
                        setSchemaLocation(splitted[1]);
                    } else
                        setSchemaNamespace(schemaWithNS);

                    hasNamespace = true;
                }
            }
        }
        throw new SAXException("OK");

    }

    /**
     * Logs error
     * @param e Error
     */
    public void error(SAXParseException e) {
        LOGGER.error("error on finding schema from xml");
    }

    /**
     * Logs fatal error
     * @param e Fatal error
     */
    public void fatalError(SAXParseException e) {
        LOGGER.error(Markers.FATAL, "Fatal error on finding schema from xml");
    }

    public String getStartTag() {
        return this.startTag;
    }

    public String getStartTagNamespace() {
        return this.startTagNamespace;
    }

    /**
     * Checks if schema has namespace
     * @return True if schema has namespace
     */
    public boolean hasNamespace() {
        return this.hasNamespace;
    }

    public String getSchemaLocation() {
        return this.schemaLocation;
    }

    public String getSchemaNamespace() {
        return schemaNamespace;
    }

    public void setSchemaNamespace(String schemaNamespace) {
        this.schemaNamespace = schemaNamespace;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }
}
