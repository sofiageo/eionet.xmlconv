package eionet.gdem.dcm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import eionet.gdem.Properties;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.utils.xml.XPathQuery;
import eionet.gdem.utils.xml.dom.DomContext;
import eionet.gdem.utils.xml.IXmlCtx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversion class.
 */
public class Conversion {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(Conversion.class);
    public static String CONVERSION_ELEMENT = "conversion";
    /** List of generated conversions. */
    private static List<ConversionDto> conversions = new ArrayList<ConversionDto>();

    /**
     * Load generated conversion types from dcm/conversions.xml.
     */
    static {
        try {
            IXmlCtx ctx = new DomContext();
            ctx.checkFromFile(Properties.convFile);
            XPathQuery xQuery = ctx.getQueryManager();
            List<String> identifiers = xQuery.getElementIdentifiers(CONVERSION_ELEMENT);
            for (int i = 0; i < identifiers.size(); i++) {
                String id = identifiers.get(i);
                ConversionDto resObject = new ConversionDto();
                resObject.setConvId(id);
                resObject.setDescription(xQuery.getElementValue(id, "description"));
                resObject.setResultType(xQuery.getElementValue(id, "result_type"));
                resObject.setStylesheet(xQuery.getElementValue(id, "stylesheet"));
                resObject.setIgnoreGeneratedIfManualExists(xQuery.getElementValue(id, "ignore_if_manual") != null
                        && "true".equals(xQuery.getElementValue(id, "ignore_if_manual")));
                Hashtable convType = GDEMServices.getDaoService().getConvTypeDao().getConvType(resObject.getResultType());

                resObject.setContentType((String) convType.get("content_type"));
                conversions.add(resObject);
            }
        } catch (Exception ex) {
            LOGGER.error("Error reading conversions.xml file ", ex);
        }

    }

    /**
     * Get the list of generated conversion types.
     *
     * @return
     */
    public static List<ConversionDto> getConversions() {
        return conversions;
    }

    /**
     * Get generated conversion type by ID.
     *
     * @param convId
     *            Conversion ID stored in xml conf.
     * @return ConversionDto object.
     */
    public static ConversionDto getConversionById(String convId) {
        ConversionDto conversion = null;
        for (int i = 0; i < conversions.size(); i++) {
            if ((conversions.get(i)).getConvId().compareTo(convId) == 0) {
                conversion = conversions.get(i);
                break;
            }
        }
        return conversion;
    }

}
