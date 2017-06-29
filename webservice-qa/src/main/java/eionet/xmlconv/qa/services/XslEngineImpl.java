package eionet.xmlconv.qa.services;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.http.HttpFileManager;
import eionet.xmlconv.qa.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * XSL engine implementation.
 * @author Enriko Käsper, Tieto Estonia XslEngineImpl
 */

public class XslEngineImpl {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(XslEngineImpl.class);


/*
    @Override
    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        FileInputStream fisXsl = null;
        String tmpXslFile = null;
        InputStream sourceStream = null;
        HttpFileManager fileManager = new HttpFileManager();
        try {
            // build InputSource for xsl
            if (!Utils.isNullStr(script.getScriptSource())) {
                tmpXslFile = Utils.saveStrToFile(null, script.getScriptSource(), "xsl");
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                fisXsl = new FileInputStream(script.getScriptFileName());
            } else {
                throw new XMLConvException("XQuery engine could not find script source or script file name!");
            }
            // Build InputSource for xml file
            sourceStream = fileManager.getFileInputStream(script.getSrcFileUrl(), null, false);
            // execute xsl transformation

            ConvertContext ctx =
                new ConvertContext(sourceStream, tmpXslFile == null ? script.getScriptFileName() : tmpXslFile,
                        result, null);
            ConvertStrategy cs = new XMLConverter();

            Map<String, String> params = UrlUtils.getCdrParams(script.getSrcFileUrl());
            params.put(Properties.XQ_SOURCE_PARAM_NAME, script.getOrigFileUrl());
            cs.setXslParams(params);
            ctx.executeConversion(cs);

            if (tmpXslFile != null) {
                Utils.deleteFile(tmpXslFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("==== Caught EXCEPTION " + e.toString());
            throw new XMLConvException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(sourceStream);
            fileManager.closeQuietly();
            IOUtils.closeQuietly(fisXsl);
        }

    }*/
}
