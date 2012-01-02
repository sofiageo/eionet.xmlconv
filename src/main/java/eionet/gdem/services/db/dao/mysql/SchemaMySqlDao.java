package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.utils.Utils;

public class SchemaMySqlDao extends MySqlBaseDao implements ISchemaDao {

    /** */
    private static final Log LOGGER = LogFactory.getLog(SchemaMySqlDao.class);

    private static final String qSchemaID = "SELECT " + SCHEMA_ID_FLD + " FROM " + SCHEMA_TABLE + " WHERE " + XML_SCHEMA_FLD
    + "= ?";

    private static final String qInsertSchema = "INSERT INTO " + SCHEMA_TABLE + " ( " + XML_SCHEMA_FLD + ", " + SCHEMA_DESCR_FLD
    + ", " + SCHEMA_LANG_FLD + ", " + SCHEMA_VALIDATE_FLD + ", " + DTD_PUBLIC_ID_FLD + ")" + " VALUES (?,?,?,?,?)";

    private static final String qUpdateSchema = "UPDATE  " + SCHEMA_TABLE + " SET " + XML_SCHEMA_FLD + "= ?" + ", "
    + SCHEMA_DESCR_FLD + "= ?" + ", " + SCHEMA_LANG_FLD + "= ?" + ", " + SCHEMA_VALIDATE_FLD + "= ?" + ", "
    + DTD_PUBLIC_ID_FLD + "= ? " + ", " + EXPIRE_DATE_FLD + "= ? " + "" + " WHERE " + SCHEMA_ID_FLD + "= ?";

    private static final String qDeleteStyleSheets = "DELETE FROM " + XSL_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + "= ?";
    private static final String qDeleteQueries = "DELETE FROM " + QUERY_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + "= ?";
    private static final String qDeleteRootElement = "DELETE FROM " + ROOTELEM_TABLE + " WHERE " + ELEM_SCHEMA_ID_FLD + "= ?";
    private static final String qDeleteSchema = "DELETE FROM " + SCHEMA_TABLE + " WHERE " + SCHEMA_ID_FLD + "= ?";
    private static final String qDeleteSchemaFiles = "DELETE FROM " + UPL_SCHEMA_TABLE + " WHERE " + UPL_FK_SCHEMA_ID + "= ?";

    private static final String qSchemaBase = "SELECT " + SCHEMA_ID_FLD + "," + XML_SCHEMA_FLD + ", " + SCHEMA_DESCR_FLD + ", "
    + DTD_PUBLIC_ID_FLD + ", " + SCHEMA_VALIDATE_FLD + ", " + SCHEMA_LANG_FLD + ", " + EXPIRE_DATE_FLD + " FROM "
    + SCHEMA_TABLE;

    private static final String qAllSchemas = qSchemaBase + " ORDER BY " + XML_SCHEMA_FLD;
    private static final String qSchemaById = qSchemaBase + " WHERE " + SCHEMA_ID_FLD + " =  ?" + " ORDER BY " + XML_SCHEMA_FLD;
    private static final String qSchemaByName = qSchemaBase + " WHERE " + XML_SCHEMA_FLD + " =  ?" + " ORDER BY " + XML_SCHEMA_FLD;

    private static final String qSchemaStylesheets = "SELECT " + CNV_ID_FLD + ", " + XSL_FILE_FLD + ", " + DESCR_FLD + ","
    + RESULT_TYPE_FLD + "," + DEPENDS_ON + " FROM " + XSL_TABLE + " WHERE " + XSL_SCHEMA_ID_FLD + "= ?" + " ORDER BY "
    + RESULT_TYPE_FLD;

    private static final String qSchemaQueries = "SELECT " + QUERY_ID_FLD + ", " + QUERY_FILE_FLD + ", " + DESCR_FLD + ","
    + SHORT_NAME_FLD + "," + QUERY_SCRIPT_TYPE + "," + QUERY_RESULT_TYPE + "," + UPPER_LIMIT_FLD + " FROM " + QUERY_TABLE
    + " WHERE " + XSL_SCHEMA_ID_FLD + "= ?" + " ORDER BY " + SHORT_NAME_FLD;

    private static final String qSchemasWithStl = "SELECT DISTINCT S." + SCHEMA_ID_FLD + ", S." + XML_SCHEMA_FLD + ", S."
    + SCHEMA_DESCR_FLD + " FROM " + SCHEMA_TABLE + " S" + " JOIN " + XSL_TABLE + " ST ON S." + SCHEMA_ID_FLD + " = ST."
    + XSL_SCHEMA_ID_FLD + " ORDER BY S." + XML_SCHEMA_FLD;

    private static final String qUpdateSchemaValidate = "UPDATE  " + SCHEMA_TABLE + " SET " + SCHEMA_VALIDATE_FLD + "= ?"
    + " WHERE " + SCHEMA_ID_FLD + "= ? ";

    public SchemaMySqlDao() {
    }

    @Override
    public String addSchema(String xmlSchema, String description) throws SQLException {
        return addSchema(xmlSchema, description, null, false, null);
    }

    @Override
    public String addSchema(String xmlSchema, String description, String schemaLang, boolean doValidate, String public_id)
    throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        description = (description == null ? "" : description);
        schemaLang = (schemaLang == null ? "" : schemaLang);
        String strValidate = doValidate ? "1" : "0";

        if (isDebugMode) {
            LOGGER.debug("Query is " + qInsertSchema);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qInsertSchema);
            pstmt.setString(1, xmlSchema);
            pstmt.setString(2, description);
            pstmt.setString(3, schemaLang);
            pstmt.setString(4, strValidate);
            pstmt.setString(5, public_id);
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
        return getSchemaID(xmlSchema);
    }

    @Override
    public void updateSchema(String schema_id, String xmlSchema, String description, String schemaLang, boolean doValidate,
            String public_id, Date expireDate) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        description = (description == null ? "" : description);
        schemaLang = (schemaLang == null ? "" : schemaLang);
        public_id = (public_id == null ? "" : public_id);
        String strValidate = doValidate ? "1" : "0";

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUpdateSchema);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUpdateSchema);
            pstmt.setString(1, xmlSchema);
            pstmt.setString(2, description);
            pstmt.setString(3, schemaLang);
            pstmt.setString(4, strValidate);
            pstmt.setString(5, public_id);
            if (expireDate == null) {
                pstmt.setNull(6, Types.DATE);
            } else {
                pstmt.setDate(6, new java.sql.Date(expireDate.getTime()));
            }
            pstmt.setInt(7, Integer.parseInt(schema_id));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public void updateSchemaValidate(String schema_id, boolean validate) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String strValidate = (validate) ? "1" : "0";

        if (isDebugMode) {
            LOGGER.debug("Query is " + qUpdateSchemaValidate);
        }
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qUpdateSchemaValidate);
            pstmt.setString(1, strValidate);
            pstmt.setInt(2, Integer.parseInt(schema_id));
            pstmt.executeUpdate();
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public void removeSchema(String schemaId, boolean del_stylesheets, boolean del_queries, boolean del_upl_schemas,
            boolean del_self) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            int schemaIdInt = Integer.parseInt(schemaId);
            // delete all stylesheets at first
            if (del_stylesheets) {
                pstmt = conn.prepareStatement(qDeleteStyleSheets);
                pstmt.setInt(1, schemaIdInt);
                if (isDebugMode) {
                    LOGGER.debug("Query is " + qDeleteStyleSheets);
                }
                pstmt.executeUpdate();
                pstmt.close();
            }

            if (del_queries) {
                pstmt = conn.prepareStatement(qDeleteQueries);
                pstmt.setInt(1, schemaIdInt);
                if (isDebugMode) {
                    LOGGER.debug("Query is " + qDeleteQueries);
                }
                pstmt.executeUpdate();
                pstmt.close();
            }

            if (del_upl_schemas) {
                // delete all files from T_UPL_SCHEMA table
                pstmt = conn.prepareStatement(qDeleteSchemaFiles);
                pstmt.setInt(1, schemaIdInt);
                if (isDebugMode) {
                    LOGGER.debug("Query is " + qDeleteSchemaFiles);
                }
                pstmt.executeUpdate();
                pstmt.close();
            }

            if (del_self) {
                // delete all root element mappings at first
                pstmt = conn.prepareStatement(qDeleteRootElement);
                pstmt.setInt(1, schemaIdInt);
                if (isDebugMode) {
                    LOGGER.debug("Query is " + qDeleteRootElement);
                }
                pstmt.executeUpdate();
                pstmt.close();

                // Delete row from T_SCHEMA
                pstmt = conn.prepareStatement(qDeleteSchema);
                pstmt.setInt(1, schemaIdInt);
                if (isDebugMode) {
                    LOGGER.debug("Query is " + qDeleteSchema);
                }
                pstmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException sqle) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException(sqle.getMessage(), sqle.getSQLState());
        } finally {
            closeAllResources(null, pstmt, conn);
        }
    }

    @Override
    public Vector getSchemas(String schemaId) throws SQLException {
        return getSchemas(schemaId, true);
    }

    private static final int ALL_SCHEMAS = 1;
    private static final int SCHEMA_BY_ID = 2;
    private static final int SCHEMA_BY_NAME = 3;

    @Override
    public Vector getSchemas(String schemaId, boolean stylesheets) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;
        int queryType = ALL_SCHEMAS;

        if (schemaId != null) {
            if (Utils.isNum(schemaId)) {
                queryType = SCHEMA_BY_ID;
            } else {
                queryType = SCHEMA_BY_NAME;
            }
        }
        try {
            conn = getConnection();

            switch (queryType) {
                case ALL_SCHEMAS:
                    pstmt = conn.prepareStatement(qAllSchemas);
                    break;
                case SCHEMA_BY_ID:
                    pstmt = conn.prepareStatement(qSchemaById);
                    pstmt.setInt(1, Integer.parseInt(schemaId));
                    break;
                case SCHEMA_BY_NAME:
                    pstmt = conn.prepareStatement(qSchemaByName);
                    pstmt.setString(1, schemaId);
                    break;
                default:
                    break;
            }

            if (isDebugMode) {
                LOGGER.debug("Query is "
                        + ((queryType == ALL_SCHEMAS) ? qAllSchemas : (queryType == SCHEMA_BY_ID) ? qSchemaById : qSchemaByName));
            }
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);

            for (int i = 0; i < r.length; i++) {
                HashMap h = new HashMap();
                h.put("schema_id", r[i][0]);
                h.put("xml_schema", r[i][1]);
                h.put("description", r[i][2]);
                h.put("dtd_public_id", r[i][3]);
                h.put("validate", r[i][4]);
                h.put("schema_lang", r[i][5]);
                h.put("expire_date", r[i][6]);
                if (stylesheets) {
                    Vector v_xls = getSchemaStylesheets(r[i][0], conn);
                    h.put("stylesheets", v_xls);
                    Vector v_queries = getSchemaQueries(r[i][0], conn);
                    h.put("queries", v_queries);
                }
                v.add(h);
            }
        } finally {
            closeAllResources(null, pstmt, conn);
        }
        return v;
    }

    @Override
    public HashMap getSchema(String schema_id) throws SQLException {
        return getSchema(schema_id, false);
    }

    @Override
    public HashMap getSchema(String schema_id, boolean stylesheets) throws SQLException {

        Vector schemas = getSchemas(schema_id, stylesheets);

        if (schemas == null) {
            return null;
        }
        if (schemas.size() == 0) {
            return null;
        }

        return (HashMap) schemas.get(0);

    }

    @Override
    public String getSchemaID(String schema) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[][] r = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qSchemaID);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qSchemaID);
            pstmt.setString(1, schema);
            rs = pstmt.executeQuery();

            r = getResults(rs);
            if (r.length == 0) {
                return null;
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return r[0][0];

    }

    @Override
    public Vector getSchemaStylesheets(String schemaId) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return getSchemaStylesheets(schemaId, conn);
        } finally {
            closeAllResources(null, null, conn);
        }

    }

    private Vector getSchemaStylesheets(String schemaId, Connection conn) throws SQLException {
        int id = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (schemaId == null) {
            throw new SQLException("Schema ID not defined");
        }
        try {
            id = Integer.parseInt(schemaId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + schemaId);
        }

        if (isDebugMode) {
            LOGGER.debug("Query is " + qSchemaStylesheets);
        }

        try {
            pstmt = conn.prepareStatement(qSchemaStylesheets);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);

            for (int i = 0; i < r.length; i++) {
                HashMap h = new HashMap();
                h.put("convert_id", r[i][0]);
                h.put("xsl", r[i][1]);
                h.put("description", r[i][2]);
                h.put("content_type_out", r[i][3]);
                h.put("depends_on", r[i][4]);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, null);
        }

        return v;

    }

    @Override
    public Vector getSchemaQueries(String schemaId) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return getSchemaQueries(schemaId, conn);
        } finally {
            closeAllResources(null, null, conn);
        }

    }

    private Vector getSchemaQueries(String schemaId, Connection conn) throws SQLException {
        int id = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (schemaId == null) {
            throw new SQLException("Schema ID not defined");
        }
        try {
            id = Integer.parseInt(schemaId);
        } catch (NumberFormatException n) {
            throw new SQLException("not numeric ID " + schemaId);
        }

        if (isDebugMode) {
            LOGGER.debug("Query is " + qSchemaQueries);
        }

        try {
            pstmt = conn.prepareStatement(qSchemaQueries);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);

            for (int i = 0; i < r.length; i++) {
                HashMap h = new HashMap();
                h.put("query_id", r[i][0]);
                h.put("query", r[i][1]);
                h.put("description", r[i][2]);
                h.put("short_name", r[i][3]);
                h.put("script_type", r[i][4]);
                h.put("result_type", r[i][5]);
                h.put("upper_limit", r[i][6]);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, null);
        }

        return v;

    }

    @Override
    public Vector getSchemasWithStl() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector v = null;

        if (isDebugMode) {
            LOGGER.debug("Query is " + qSchemasWithStl);
        }

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(qSchemasWithStl);
            rs = pstmt.executeQuery();
            String[][] r = getResults(rs);
            v = new Vector(r.length);
            for (int i = 0; i < r.length; i++) {
                HashMap h = new HashMap();
                h.put("schema_id", r[i][0]);
                h.put("xml_schema", r[i][1]);
                h.put("description", r[i][2]);
                v.add(h);
            }
        } finally {
            closeAllResources(rs, pstmt, conn);
        }

        return v;

    }

}