package eionet.xmlconv.qa.services.basex;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.basex.core.Context;
import org.basex.core.MainOptions;
import org.basex.core.cmd.Set;
import org.basex.io.out.ArrayOutput;
import org.basex.io.serial.SerializerOptions;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;

import static java.util.Objects.isNull;

/**
 * @author George Sofianos
 *
 */
public class BaseXLocalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXLocalService.class);

    protected void runQuery(QAScript script, OutputStream result) throws XMLConvException {
        String scriptFileName = script.getFileName();
        String source_url = script.getSourceUrl();
        String outputType = script.getOutputType();

        Context context = new Context();
        QueryProcessor proc = null;
        try {
            new Set(MainOptions.INTPARSE, true).execute(context);
            new Set(MainOptions.QUERYPATH, Properties.queriesFolder).execute(context);

            String scriptSource = null;
            if (!Utils.isNullStr(scriptFileName)) {
                try (Reader queryReader = new FileReader(scriptFileName)) {
                    scriptSource = new String(IOUtils.toByteArray(queryReader, "UTF-8"));
                } catch (IOException e) {
                    LOGGER.error("Error while reading XQuery file: " + e);
                    throw new XMLConvException("Error while reading XQuery file: " + scriptFileName + " : " + ExceptionUtils.getStackTrace(e), e);
                }
            }
            // TODO find out if can be a singleton
            proc = new QueryProcessor( scriptSource, context);
            proc.bind("source_url", source_url, "xs:string");

            // same serialization options with saxon
            /*SerializerOptions opts = new SerializerOptions();
            opts.set(SerializerOptions.INDENT, "no");*/
/*
            depends on script

            opts.set(SerializerOptions.ENCODING, DEFAULT_ENCODING);
            if (getOutputType().equals(HTML_CONTENT_TYPE)) {
                opts.set(SerializerOptions.METHOD, HTML_CONTENT_TYPE);
            } else {
                opts.set(SerializerOptions.METHOD, getOutputType());
            }

            if (getOutputType().equals(XML_CONTENT_TYPE)) {
                opts.set(SerializerOptions.OMIT_XML_DECLARATION, "no");
            } else {
                opts.set(SerializerOptions.OMIT_XML_DECLARATION, "yes");
            }*/

            Value res = proc.value();

            ArrayOutput A = res.serialize();
            result.write(A.toArray());
        } catch ( QueryException | IOException e) {
            LOGGER.error("Error executing BaseX xquery script : " + e.getMessage());
            throw new XMLConvException(e.getMessage());
        } finally {
            if (!isNull(proc))  {
                proc.close();
            }
            context.close();
        }
    }
}
