package eionet.gdem.conversions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;


import eionet.gdem.services.db.dao.IDbSchema;
import eionet.gdem.services.db.dao.mysql.MySqlBaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import eionet.gdem.dto.ConversionDto;

/**
 *
 * DAO for finding available conversions.
 *
 * @author Enriko Käsper
 */
@Repository("convTypeDao")
public class ConvTypeMySqlDao extends MySqlBaseDao implements IConvTypeDao {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvTypeMySqlDao.class);

    private static final String qListAllConversions = "SELECT X." + IDbSchema.CNV_ID_FLD + ", X." + IDbSchema.XSL_FILE_FLD + ", X." + IDbSchema.DESCR_FLD + ", "
            + IDbSchema.RESULT_TYPE_FLD + ", S." + IDbSchema.XML_SCHEMA_FLD + ", " + IDbSchema.CONVTYPE_TABLE + "." + IDbSchema.CONTENT_TYPE_FLD + " FROM " + IDbSchema.XSL_TABLE
            + " X LEFT JOIN " + IDbSchema.XSL_SCHEMA_TABLE + " XS ON XS." + IDbSchema.STYLESHEET_ID_FLD + " = X." + IDbSchema.CNV_ID_FLD + " LEFT JOIN "
            + IDbSchema.SCHEMA_TABLE + " S ON XS." + IDbSchema.XSL_SCHEMA_ID_FLD + " = S." + IDbSchema.SCHEMA_ID_FLD + " LEFT JOIN " + IDbSchema.CONVTYPE_TABLE + " ON X."
            + IDbSchema.RESULT_TYPE_FLD + "=" + IDbSchema.CONVTYPE_TABLE + "." + IDbSchema.CONV_TYPE_FLD + " ORDER BY S." + IDbSchema.XML_SCHEMA_FLD + ", "
            + IDbSchema.RESULT_TYPE_FLD;

    private static final String qListConversionsForSchema = "SELECT X." + IDbSchema.CNV_ID_FLD + ",X." + IDbSchema.XSL_FILE_FLD + ", X." + IDbSchema.DESCR_FLD
            + "," + IDbSchema.RESULT_TYPE_FLD + ", S." + IDbSchema.XML_SCHEMA_FLD + ", " + IDbSchema.CONVTYPE_TABLE + "." + IDbSchema.CONTENT_TYPE_FLD + " FROM "
            + IDbSchema.XSL_TABLE + " X LEFT JOIN " + IDbSchema.XSL_SCHEMA_TABLE + " XS ON XS." + IDbSchema.STYLESHEET_ID_FLD + " = X." + IDbSchema.CNV_ID_FLD
            + " LEFT JOIN " + IDbSchema.SCHEMA_TABLE + " S ON XS." + IDbSchema.XSL_SCHEMA_ID_FLD + "=S." + IDbSchema.SCHEMA_ID_FLD + " LEFT JOIN "
            + IDbSchema.CONVTYPE_TABLE + " ON X." + IDbSchema.RESULT_TYPE_FLD + " = " + IDbSchema.CONVTYPE_TABLE + "." + IDbSchema.CONV_TYPE_FLD + " WHERE S."
            + IDbSchema.XML_SCHEMA_FLD + " =? " + " ORDER BY S." + IDbSchema.XML_SCHEMA_FLD + ", " + IDbSchema.RESULT_TYPE_FLD;

    private static final String qConvTypes = "SELECT " + IDbSchema.CONV_TYPE_FLD + ", " + IDbSchema.CONTENT_TYPE_FLD + ", " + IDbSchema.FILE_EXT_FLD + ", "
            + IDbSchema.CONVTYPE_DESCRIPTION_FLD + " FROM " + IDbSchema.CONVTYPE_TABLE + " ORDER BY " + IDbSchema.CONV_TYPE_FLD;

    private static final String qConvType = "SELECT " + IDbSchema.CONV_TYPE_FLD + ", " + IDbSchema.CONTENT_TYPE_FLD + ", " + IDbSchema.FILE_EXT_FLD + ", "
            + IDbSchema.CONVTYPE_DESCRIPTION_FLD + " FROM " + IDbSchema.CONVTYPE_TABLE + " WHERE " + IDbSchema.CONV_TYPE_FLD + "=?";

    @Override
    public Vector<ConversionDto> listConversions(String xmlSchema) throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean forSchema = xmlSchema != null;
        Vector<ConversionDto> v = null;
        String query = (forSchema) ? qListConversionsForSchema : qListAllConversions;

        if (isDebugMode) {
            LOGGER.debug("XMLSchema is " + xmlSchema);
            LOGGER.debug("Query is " + query);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(query);
            if (forSchema) {
                pstmt.setString(1, xmlSchema);
            }
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector<ConversionDto>(r.length);
            for (int i = 0; i < r.length; i++) {
                ConversionDto convObject = new ConversionDto();
                convObject.setConvId(r[i][0] == null ? "" : r[i][0]);
                convObject.setStylesheet(r[i][1] == null ? "" : r[i][1]);
                convObject.setDescription(r[i][2] == null ? "" : r[i][2]);
                convObject.setResultType(r[i][3] == null ? "" : r[i][3]);
                convObject.setXmlSchema(r[i][4] == null ? "" : r[i][4]);
                convObject.setContentType(r[i][5] == null ? "" : r[i][5]);
                v.add(convObject);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;
    }

    @Override
    public Vector getConvTypes() throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qConvTypes);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qConvTypes);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {
                Hashtable h = new Hashtable();
                h.put("conv_type", r[i][0]);
                h.put("content_type", r[i][1]);
                h.put("file_ext", r[i][2]);
                h.put("description", r[i][3]);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;
    }

    @Override
    public Hashtable getConvType(String convType) throws SQLException {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Hashtable h = null;

        if (isDebugMode) {
            LOGGER.debug("Conv type is " + convType);
            LOGGER.debug("Query is " + qConvType);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qConvType);
            pstmt.setString(1, convType);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);

            if (r.length == 0) {
                return null;
            }
            h = new Hashtable();
            h.put("conv_type", r[0][0]);
            h.put("content_type", r[0][1]);
            h.put("file_ext", r[0][2]);
            h.put("description", r[0][3]);
        } finally {
            closeAllResources(rs, pstmt, conn);
        }
        return h;
    }

}
