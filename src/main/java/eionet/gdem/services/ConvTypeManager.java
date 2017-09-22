package eionet.gdem.services;

import java.util.Hashtable;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ConvType;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.db.dao.IConvTypeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ConvTypeManager.
 *
 * @author Enriko Käsper, Tieto Estonia
 */
@Service
public class ConvTypeManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvTypeManager.class);
    private IConvTypeDao convTypeDao;

    @Autowired
    public ConvTypeManager(IConvTypeDao convTypeDao) {
        this.convTypeDao = convTypeDao;
    }

    /*private IConvTypeDao convTypeDao = GDEMServices.getDaoService().getConvTypeDao();*/

    /**
     * Get conversion type mappings
     *
     * @param convTypeId
     *            (HTML, XML, ...)
     * @return
     * @throws DCMException If an error occurs.
     */
    public ConvType getConvType(String convTypeId) throws DCMException {
        ConvType convType = null;
        try {

            Hashtable type = convTypeDao.getConvType(convTypeId);
            if (type == null) {
                return null;
            }
            convType = new ConvType();
            convType.setContType(type.get("content_type") == null ? null : (String) type.get("content_type"));
            convType.setConvType(type.get("conv_type") == null ? null : (String) type.get("conv_type"));
            convType.setDescription(type.get("description") == null ? null : (String) type.get("description"));
            convType.setFileExt(type.get("file_ext") == null ? null : (String) type.get("file_ext"));

        } catch (Exception e) {
            LOGGER.error("Error getting conv types", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return convType;

    }

}
