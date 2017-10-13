package eionet.gdem.web.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database connection test class.
 * @author Unknown
 * @author George Sofianos
 */
public class DbTest {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(DbTest.class);

    /**
     * Test database connection
     * @param url Connection url
     * @param user User
     * @param psw Password
     * @throws Exception If an error occurs.
     */
    public void tstDbParams(String url, String user, String psw) throws DCMException, SQLException {

        Connection con = null;
        Statement stmt = null;
        ResultSet rset = null;

        try {
            // Class.forName(Properties.dbDriver);
            Class.forName(Properties.dbDriver);

            con = DriverManager.getConnection(url, user, psw);
            stmt = con.createStatement();
            String sql = "SELECT 1";
            rset = stmt.executeQuery(sql);

        } catch (Exception e) {
            LOGGER.debug("Testing database connection failed!", e);
            throw new DCMException(BusinessConstants.EXCEPTION_PARAM_DB_TEST_FAILED);
        } finally {
            // Close connection
            if (rset != null) {
                rset.close();
            }
            if (stmt != null) {
                stmt.close();
                if (!con.getAutoCommit()) {
                    con.commit();
                }
            }
            if (con != null) {
                con.close();
                con = null;
            }

        }
    }

}
