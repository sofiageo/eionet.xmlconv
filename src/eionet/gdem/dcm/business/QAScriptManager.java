/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.dcm.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import org.apache.struts.upload.FormFile;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.QAScript;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.ISchemaDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;

/**
 * @author Enriko Käsper, Tieto Estonia QAScriptManager
 */

public class QAScriptManager {

	private static LoggerIF _logger = GDEMServices.getLogger();
	private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();
	private ISchemaDao schemaDao = GDEMServices.getDaoService().getSchemaDao();

	public QAScript getQAScript(String queryId) throws DCMException {
		QAScript qaScript = new QAScript();

		try {
			if (!queryId.equals("")) {
				HashMap scriptData = queryDao.getQueryInfo(queryId);

				if (scriptData == null)
					scriptData = new HashMap<String, String>();

				qaScript.setScriptId((String) scriptData.get("query_id"));
				qaScript.setSchemaId((String) scriptData.get("schema_id"));
				qaScript.setSchema((String) scriptData.get("xml_schema"));
				qaScript.setDescription((String) scriptData.get("description"));
				qaScript.setShortName((String) scriptData.get("short_name"));
				qaScript.setResultType((String) scriptData.get("content_type"));
				qaScript.setScriptType((String) scriptData.get("script_type"));
				qaScript.setFileName((String) scriptData.get("query"));

				String queryFolder = Properties.queriesFolder;

				if (!Utils.isNullStr(qaScript.getFileName())) {
					qaScript.setFilePath(Names.QUERY_FOLDER + qaScript.getFileName());
					if (!queryFolder.endsWith(File.separator))
						queryFolder = queryFolder + File.separator;
					String queryContent = null;
					try {
						queryContent = Utils.readStrFromFile(queryFolder + qaScript.getFileName());
					} catch (IOException e) {
						queryContent = Constants.FILEREAD_EXCEPTION + queryFolder + qaScript.getFileName() + "\n "
								+ e.toString();
					}
					qaScript.setScriptContent(queryContent);
					String checksum = null;
					try {
						checksum = Utils.getChecksumFromFile(queryFolder + qaScript.getFileName());
					} catch (IOException e) {
						checksum = "";
					}
					qaScript.setChecksum(checksum);
					try {
						File f = new File(queryFolder + qaScript.getFileName());
						if (f != null && f.exists())
							qaScript.setModified(Utils.getDateTime(new Date(f.lastModified())));
					} catch (Exception e) {
					}

				}
			}

		} catch (Exception e) {
			_logger.error("Error getting QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
		return qaScript;

	}

	public void update(String user, String scriptId, String shortName, String schemaId, String resultType,
			String descr, String scriptType, String curFileName, FormFile file) throws DCMException {
		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "u")) {
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error updating QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		if (Utils.isNullStr(scriptId) || Utils.isNullStr(schemaId)) {
			_logger.error("Cannot update QA script. Script ID or schema ID is empty.");
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			String fileName = file.getFileName().trim();
			// upload file
			if (!Utils.isNullStr(fileName)) {
				if (Utils.isNullStr(curFileName)) {
					// check if file exists
					if (fileExists(fileName)) {
						throw new DCMException(BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS);
					}
				}
				// create backup of existing file
				BackupManager bum = new BackupManager();
				bum.backupFile(Properties.queriesFolder, curFileName, scriptId, user);

				storeQAScriptFile(file, curFileName);
			}
			queryDao.updateQuery(scriptId, schemaId, shortName, descr, curFileName, resultType, scriptType);
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error updating QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}

	public void update(String user, String scriptId, String shortName, String schemaId, String resultType,
			String descr, String scriptType, String curFileName, String content, boolean updateContent)
			throws DCMException {
		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "u")) {
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error updating QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		if (Utils.isNullStr(scriptId) || Utils.isNullStr(schemaId)) {
			_logger.error("Cannot update QA script. Script ID or schema ID is empty.");
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			if (!Utils.isNullStr(curFileName) && !Utils.isNullStr(content)
					&& content.indexOf(Constants.FILEREAD_EXCEPTION) == -1 && updateContent) {

				// create backup of existing file
				BackupManager bum = new BackupManager();
				bum.backupFile(Properties.queriesFolder, curFileName, scriptId, user);

				Utils.saveStrToFile(Properties.queriesFolder + File.separator + curFileName, content, null);
			}
			queryDao.updateQuery(scriptId, schemaId, shortName, descr, curFileName, resultType, scriptType);
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error updating QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}

	public boolean fileExists(String fileName) throws SQLException {

		if (queryDao.checkQueryFile(fileName)) {
			// file name exists in database
			return true;
		}

		File file = new File(Properties.queriesFolder, fileName);

		if (file == null) {
			return false;
		}

		return file.exists();

	}

	public void storeQAScriptFile(FormFile file, String fileName) throws FileNotFoundException, IOException {

		InputStream in = file.getInputStream();
		String filepath = new String(Properties.queriesFolder + "/" + fileName);
		OutputStream w = new FileOutputStream(filepath);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
			w.write(buffer, 0, bytesRead);
		}
		w.close();
		in.close();
		file.destroy();

	}

	public void delete(String user, String scriptId) throws DCMException {
		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "d")) {
				_logger.debug("You don't have permissions to delete QA script!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_DELETE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error deleting QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		if (Utils.isNullStr(scriptId)) {
			_logger.error("Cannot delete QA script. Script ID is empty.");
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			HashMap hash = queryDao.getQueryInfo(scriptId);
			String fileName = (String) hash.get("query");

			String queriesFolder = Properties.queriesFolder;
			if (!queriesFolder.endsWith(File.separator))
				queriesFolder = queriesFolder + File.separator;
			Utils.deleteFile(queriesFolder + fileName);

			queryDao.removeQuery(scriptId);

		} catch (Exception e) {
			_logger.error("Error deleting QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}
	}

	public void add(String user, String scriptId, String shortName, String schemaId, String schema, String resultType,
			String description, String scriptType, FormFile scriptFile) throws DCMException {
		try {
			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_QUERIES_PATH, "i")) {
				_logger.debug("You don't have permissions to insert QA script!");
				throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_DELETE);
			}
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			_logger.error("Error deleting QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

		try {
			String fileName = scriptFile.getFileName().trim();
			// upload file
			if (!Utils.isNullStr(fileName)) {
				// check if file exists
				if (fileExists(fileName)) {
					throw new DCMException(BusinessConstants.EXCEPTION_QASCRIPT_FILE_EXISTS);
				}
			}
			if (Utils.isNullStr(schemaId) || "0".equals(schemaId)) {
				schemaId = schemaDao.getSchemaID(schema);
				if (Utils.isNullStr(schemaId) || "0".equals(schemaId)) {
					schemaId = schemaDao.addSchema(schema, null);
				}
			}

			queryDao.addQuery(schemaId, shortName, fileName, description, resultType, scriptType);
			storeQAScriptFile(scriptFile, fileName);
		} catch (DCMException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			_logger.error("Error updating QA script", e);
			throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
		}

	}
}
