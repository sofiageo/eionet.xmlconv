package eionet.gdem.services.db.dao;

/**
 * DB Schema interface.
 * TODO: interfaces should have methods
 * @author Unknown
 */
public interface IDbSchema {

    /**
     * Table for XForms browsers.
     */
    String XFBROWSER_TABLE = "T_XFBROWSER";
    /**
     * TYPE values in FILE table.
     */
    String XFORM_FILE_TYPE = "xform";
    String CSS_FILE_TYPE = "css";
    String IMAGE_FILE_TYPE = "image";

    /**
     * PARENT_TYPE values in FILE table.
     */
    String SCHEMA_FILE_PARENT = "xml_schema";

    /**
     * Field names in XFBROWSER table.
     */
    String BROWSER_ID_FLD = "BROWSER_ID";
    String BROWSER_TYPE_FLD = "BROWSER_TYPE";
    String BROWSER_TITLE_FLD = "BROWSER_TITLE";
    String BROWSER_STYLESHEET_FLD = "STYLESHEET";
    String BROWSER_PRIORITY_FLD = "PRIORITY";



}
