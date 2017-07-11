package eionet.xmlconv.qa.services.external;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.http.HttpFileManager;
import eionet.xmlconv.qa.model.QAResponse;
import eionet.xmlconv.qa.model.QAScript;
import eionet.xmlconv.qa.services.external.system.SysCommandExecutor;
import eionet.xmlconv.qa.utils.CustomFileUtils;
import eionet.xmlconv.qa.utils.UrlUtils;
import eionet.xmlconv.qa.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * TODO: Check if still needed
 */
public class XGawkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XGawkService.class);

    protected String getShellCommand(String dataFile, String scriptFile, Map<String, String> params) {
        return Properties.XGAWK_COMMAND + getVariables(params) + " -f " + scriptFile + " " + dataFile;
    }

    public String execute(QAScript script) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        execute(script, out);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private void execute(QAScript script, OutputStream result) throws XMLConvException {
        String source = script.getScriptSource();
        String filename = script.getFilename();
        String sourceFileUrl = script.getSourceFileUrl();
        String xmlFileUrl = script.getXmlFileUrl();
        String tmpScriptFile = null;

        HttpFileManager fileManager = new HttpFileManager();
        try {
            // build InputSource for xsl
            if (!Utils.isNullStr(source)) {
                filename = Utils.saveStrToFile(null, source, "xgawk");
            } else if (!Utils.isNullStr(filename)) {
                // fisXsl=new FileInputStream(script.getScriptFileName());
            } else {
                throw new XMLConvException("XQuery engine could not find script source or script file name!");
            }

            InputStream sourceStream = fileManager.getFileInputStream(sourceFileUrl, null, false);
            String srcFile = CustomFileUtils.saveFileInLocalStorage(sourceStream, "xml");
            Map<String, String> params = UrlUtils.getCdrParams(xmlFileUrl);
            params.put("source_url", xmlFileUrl);

            String cmd = getShellCommand(srcFile, filename, params);
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
                // TODO FIX asap
                /*Streams.drain(new StringReader(cmdError), result);*/
                throwError = true;
            } else {
                /*Streams.drain(new StringReader(cmdOutput), result);*/
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
