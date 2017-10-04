package eionet.gdem.api.remoteapi;

import java.util.Vector;

/**
 * Converts the listConversions method result as XML.
 *
 * @author Enriko Käsper, TietoEnator Estonia AS
 * TODO Change to JAXB
 */

public class ListConversionsResult {

    public static final String CONVERSION_TAG = "conversion";
    public static final String CONVERT_ID_TAG = "convert_id";
    public static final String XSL_TAG = "xsl";
    public static final String CONTENT_TYPE_TAG = "content_type_out";
    public static final String RESULT_TYPE_TAG = "result_type";
    public static final String XML_SCHEMA_TAG = "xml_schema";

    private Vector conversions = null;

    /**
     * Default constructor
     */
    public ListConversionsResult() {
    }

    /**
     * Set the data
     *
     * @param list Result list
     */
    public void setResult(Vector list) {
        conversions = list;
    }

    /**
     * Write data into XML
     * @throws Exception If an error occurs
     */
    protected void writeElements() throws Exception {
/*        if (conversions == null)
            return;
        for (int i = 0; i < conversions.size(); i++) {
            Hashtable h = (Hashtable) conversions.get(i);
            Enumeration keys = h.keys();

            hd.startElement("", "", CONVERSION_TAG, null);
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                writeSimpleElement(key, (String) h.get(key));
            }
            hd.endElement("", "", CONVERSION_TAG);

        }*/
    }

}
