package eionet.xmlconv.conversions.xml;

/*import com.wutka.dtd.DTDParser;*/
import eionet.xmlconv.conversions.exceptions.DCMException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * Document analyzer utility class.
 * @author Enriko Käsper, Tieto Estonia DocumentAnalyser
 * @author George Sofianos
 */
public final class DocumentAnalyser {

    /**
     * Private constructor
     */
    private DocumentAnalyser() {
        // do nothing
    }

    /**
     * Checks if source is XML Schema
     * @param bytes File
     * @return True if source is XML Schema
     * @throws DCMException If an error occurs.
     */
    public static boolean sourceIsXMLSchema(byte[] bytes) throws DCMException {

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            javax.xml.validation.Schema s =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(is));

            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }

    }

    /**
     * Checks if source is DTD schema.
     * @param bytes File
     * @return True if source is DTD schema.
     * @throws DCMException If an error occurs.
     * TODO: FIX THIS
     */
    public static boolean sourceIsDTD(byte[] bytes) throws DCMException {

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            /*DTDParser dtdparser = new DTDParser(new InputStreamReader(is));
            dtdparser.parse();*/
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }

    }

}
