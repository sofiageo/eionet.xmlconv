package eionet.xmlconv.qa.services.external;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.http.HttpFileManager;
import eionet.xmlconv.qa.services.external.system.SysCommandExecutor;
import eionet.xmlconv.qa.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Check if still needed
 * @author Enriko Käsper, Tieto Estonia XGawkQueryEngine
 *
 */
public class XGawkQueryEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(XGawkQueryEngine.class);

    protected String getShellCommand(String dataFile, String scriptFile, Map<String, String> params) {
        return Properties.XGAWK_COMMAND + getVariables(params) + " -f " + scriptFile + " " + dataFile;
    }

    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {
        String tmpScriptFile = null;
        HttpFileManager fileManager = new HttpFileManager();
        try {

            // build InputSource for xsl
            if (!Utils.isNullStr(script.getScriptSource())) {
                tmpScriptFile = Utils.saveStrToFile(null, script.getScriptSource(), "xgawk");
                script.setScriptFileName(tmpScriptFile);
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                // fisXsl=new FileInputStream(script.getScriptFileName());
            } else {
                throw new XMLConvException("XQuery engine could not find script source or script file name!");
            }

            InputStream sourceStream = fileManager.getFileInputStream(script.getSrcFileUrl(), null, false);
            String srcFile = CustomFileUtils.saveFileInLocalStorage(sourceStream, "xml");

            String originSourceUrl = script.getOrigFileUrl();
            Map<String, String> params = UrlUtils.getCdrParams(originSourceUrl);
            params.put(Constants.XQ_SOURCE_PARAM_NAME, script.getOrigFileUrl());

            String cmd = getShellCommand(srcFile, script.getScriptFileName(), params);

            LOGGER.debug("Execute command: " + cmd);

            SysCommandExecutor cmdExecutor = new SysCommandExecutor();
            int exitStatus = cmdExecutor.runCommand(cmd);
            LOGGER.debug("Exit status: " + exitStatus);

            String cmdError = cmdExecutor.getCommandError();
            LOGGER.debug("Command error: " + cmdError);

            String cmdOutput = cmdExecutor.getCommandOutput();
            // _logger.debug("Command output: " + cmdOutput);
            boolean throwError = false;

            if (Utils.isNullStr(cmdOutput) && !Utils.isNullStr(cmdError)) {
                Streams.drain(new StringReader(cmdError), result);
                throwError = true;
            } else {
                Streams.drain(new StringReader(cmdOutput), result);
            }

            // clean tmp files
            if (tmpScriptFile != null) {
                Utils.deleteFile(tmpScriptFile);
            }
            if (srcFile != null) {
                Utils.deleteFile(srcFile);
            }
            if (throwError) {
                throw new XMLConvException(cmdError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("==== Caught EXCEPTION " + e.toString());
            throw new XMLConvException(e.getMessage());
        } finally {
            fileManager.closeQuietly();
            try {
                result.close();
                result.flush();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
            }
        }
    }

    /**
     * Gets variables
     * @param params Parameters
     * @return Variables
     */
    protected String getVariables(Map<String, String> params) {

        String ret = "";
        if (!Utils.isNullHashMap(params)) {
            StringBuffer buf = new StringBuffer();
            Iterator<String> it = params.keySet().iterator();

            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                buf.append(" -v ");
                buf.append(key);
                buf.append("=\"");
                buf.append(value);
                buf.append("\"");
            }
            ret = buf.toString();
        }

        return ret;
    }
}
