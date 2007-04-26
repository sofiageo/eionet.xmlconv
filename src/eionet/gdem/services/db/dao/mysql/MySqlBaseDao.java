package eionet.gdem.services.db.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public abstract class MySqlBaseDao {

	private static DataSource ds = null;

	// ResourceBundle props;
	protected static LoggerIF logger = GDEMServices.getLogger();

	protected static boolean isDebugMode = logger.enable(LoggerIF.DEBUG);

	static {
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/GDEM_DB");
		} catch (NamingException e) {
			logger.fatal("Initialization of datasource  (jdbc/GDEM_DB) failed: ", e);
		}		
	}

	
	
	public MySqlBaseDao() {}

	
	
	/**
	 * Returns new database connection.
	 * 
	 * @throw ServiceException if no connections were available.
	 */
	public static synchronized Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	
	public static void closeAllResources(ResultSet rs, Statement pstmt, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if ((conn != null) && (!conn.isClosed())) {
				conn.close();
				conn = null;
			}
		} catch (SQLException sqle) {
		}
	}

	public static void closeConnection(Connection conn) {
		try {
			if ((conn != null) && (!conn.isClosed())) {
				conn.close();
				conn = null;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public static void commit(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public static String[][] getResults(ResultSet rset) throws SQLException {
		Vector rvec = new Vector(); // Return value as Vector
		String rval[][] = {}; // Return value

		// if (logger.enable(logger.DEBUG)) logger.debug(sql);

		// Process the result set

		try {

			ResultSetMetaData md = rset.getMetaData();

			// number of columns in the result set
			int colCnt = md.getColumnCount();

			while (rset.next()) {
				String row[] = new String[colCnt]; // Row of the result set

				// Retrieve the columns of the result set
				for (int i = 0; i < colCnt; ++i)
					row[i] = rset.getString(i + 1);

				rvec.addElement(row); // Store the row into the vector
			}
		} catch (SQLException e) {
			// logger.error("Error occurred when processing result set: " +
			// sql,e);
			logger.error(e);
			throw new SQLException("Error occurred when processing result set: " + "");
		}

		// Build return value
		if (rvec.size() > 0) {
			rval = new String[rvec.size()][];

			for (int i = 0; i < rvec.size(); ++i)
				rval[i] = (String[]) rvec.elementAt(i);
		}

		// Success
		return rval;
	}

	protected String getLastInsertID() throws SQLException {
		Connection con = null;
		String lastInsertId = null;

		con = getConnection();

		String qry = "SELECT LAST_INSERT_ID()";

		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(qry);
		rs.clearWarnings();
		if (rs.next())
			lastInsertId = rs.getString(1);
		closeAllResources(rs, stmt, con);
		return lastInsertId;
	}

}