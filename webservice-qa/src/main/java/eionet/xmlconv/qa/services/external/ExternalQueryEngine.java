package eionet.xmlconv.qa.services.external;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;

import eionet.gdem.Constants;
import eionet.gdem.qa.XQScript;
import eionet.gdem.utils.Streams;
import eionet.gdem.utils.cdr.UrlUtils;
import eionet.gdem.utils.file.CustomFileUtils;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.http.HttpFileManager;
import eionet.xmlconv.qa.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Enriko Käsper, Tieto Estonia SystemQueryEngineImpl
 */

public abstract class ExternalQueryEngine {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalQueryEngine.class);

    /**
     * Gets shell command
     *
     * @param dataFile   data file
     * @param scriptFile script file
     * @param params     parameters
     * @return
     */
    protected abstract String getShellCommand(String dataFile, String scriptFile, Map<String, String> params);

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
}
